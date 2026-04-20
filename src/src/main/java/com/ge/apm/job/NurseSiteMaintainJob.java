package com.ge.apm.job;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateTime;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import javaslang.Tuple2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by 503035627
 */
@Component
@EnableScheduling
@Deprecated
public class NurseSiteMaintainJob {
    public static final Logger LOGGER = LoggerFactory.getLogger(NurseSiteMaintainJob.class);


    @Autowired
    BatchJobRepository batchJobRepository;

    @Autowired
    NurseSiteMaintainRepository nurseSiteMaintainRepository;

    @Autowired
    UserOrgInfoRepository userOrgInfoRepository;

    @Autowired
    NurseSiteMaintainItemRepository nurseSiteMaintainItemRepository;

    @Autowired
    NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;


    @Autowired
    NurseSiteMaintainDataRepository nurseSiteMaintainDataRepository;

    @Autowired
    NurseSiteMaintainConfigItemRepository nurseSiteMaintainConfigItemRepository;


    @Autowired
    NurseSiteMaintainService nurseSiteMaintainService;

    @Autowired
    NurseSiteMaintainCheckItemRepository nurseSiteMaintainCheckItemRepository;

//    @Scheduled(cron = "5/20 * * * * *")
    public void execute() {
        try {
            checkJob();
            doJob();
        } catch (Exception e) {
            // TODO: handle exception
            LOGGER.error(e.getMessage());
            if (firstJob != null) {
                firstJob.setLog(e.getMessage().substring(0, Math.min(240, e.getMessage().length())));
                firstJob.setProcessEndTime(new Date());
                firstJob.setStatus(BatchJob.STATUS_FAILED);
                batchJobRepository.save(firstJob);
            }
        }
    }

    private BatchJob firstJob;

    private void doJob() throws Exception {
        LOGGER.info("NurseSiteMaintainJob start to work ...");
        while (true) {
            List<BatchJob> listJob = batchJobRepository.findNewBatchJobWithName(BatchJob.NAME_NURSESITE);
            if (listJob != null && listJob.size() > 0) {
                for (int i = 0; i < listJob.size(); ++i) {
                    firstJob = listJob.get(i);
                    tDoJob();
                }
            } else {
                break;
            }
        }
        LOGGER.info("NurseSiteMaintainJob end ...");
    }

    private void tDoJob() throws Exception {
        firstJob.setProcessStartTime(new Date());
        firstJob.setStatus(BatchJob.STATUS_START);
        batchJobRepository.save(firstJob);
        String action = firstJob.getSubName();
        String content = firstJob.getContent();
        if (StringUtils.isNotEmpty(content) && StringUtils.isNotEmpty(action)) {
            // content:nurseSiteMaintainConfigId,startBatchDate,endBatchDate,currentUser
            String[] params = content.split(",");
            if ("create".equals(action)) {
                doJobCreate(params);
            } else if ("update".equals(action)) {
                doJobUpdate(params);
            } else if ("deleteAssetInfo".equals(action)) {
                doJobDeleteAssetInfo(params);
            } else if ("complete".equals(action)) {
                doJobComplete(params, firstJob.getCreateTime());
            } else if ("delete".equals(action)) {
                doJobDelete(params);
            } else if ("dailyUpdate".equals(action)) {
                doJobDailyUpdate();
            } else if ("deleteCheckConfig".equals(action)) {
                doJobDeleteConfig(params);
            }
        }
        firstJob.setProcessEndTime(new Date());
        firstJob.setStatus(BatchJob.STATUS_SUCCESS);
        batchJobRepository.save(firstJob);
    }

    private void checkJob() {
        LOGGER.info("NurseSiteMaintainJob start check ...");
        batchJobRepository.updateJobStatus(1, 0);
        LOGGER.info("NurseSiteMaintainJob end check ...");
    }

    public void doJobDailyUpdate() {
        Date currentDate = DateUtil.now();
        DateTime startTime = DateUtil.beginOfDay(currentDate);
        DateTime endTime = DateUtil.offsetDay(startTime, 1);
        Date startDate = new Date(startTime.getTime());
        Date endDate = new Date(endTime.getTime());

        //0：未检查,1:已检查，2:通过没commnet没有delay，3有问题，延期了,4进行中,5,通过有备注没有延期，6，通过有备注延期，7，通过，没有备注，延期了，8，有问题，没有延期
        List<Integer> listStatus = ListUtil.newArrayList(2, 3, 4, 5);
        for (int configType = 1; configType <= 3; configType++) {
            nurseSiteMaintainDataRepository.updateDailyByStatus(startDate, endDate, listStatus, configType);
            nurseSiteMaintainItemRepository.updateDailyByStatus(startDate, endDate, 2, configType, "停机");
            nurseSiteMaintainItemRepository.updateDailyByStatus(startDate, endDate, 3, configType, "限制功能使用");
            nurseSiteMaintainItemRepository.updateDailyByStatus(startDate, endDate, 4, configType, "待报废");
            nurseSiteMaintainItemRepository.updateDailyByStatus(startDate, endDate, 5, configType, "停用");
            nurseSiteMaintainItemRepository.updateDailyByValid(startDate, endDate, false, configType, "报废");
            nurseSiteMaintainItemRepository.updateDailyByDelete(startDate, endDate, true, configType, "已删除");
            nurseSiteMaintainItemRepository.updateDailyByStatus(startDate, endDate, listStatus, configType, currentDate, 8,true);
            nurseSiteMaintainRepository.updateDailyByStatus(startDate, endDate, listStatus, configType, currentDate, 8, 8);
        }
        nurseSiteMaintainRepository.updaIsFinish();
    }

    public void doJobDeleteAssetInfo(String[] params) {
        String assetUid = params[4];
        Set<String> configIds = new HashSet<String>();
        List<NurseSiteMaintainConfigItem> listItem = nurseSiteMaintainConfigItemRepository.findByAssetUid(assetUid);
        if (listItem != null && listItem.size() > 0) {
            for (int i = 0; i < listItem.size(); ++i) {
                NurseSiteMaintainConfigItem tempItem = listItem.get(i);
                configIds.add(tempItem.getParentId());
            }
            nurseSiteMaintainConfigItemRepository.deleteByAssetUid(assetUid);
        }
        if (configIds.size() > 0) {
            Iterator<String> it = configIds.iterator();
            while (it.hasNext()) {
                String id = it.next();
                if (StringUtils.isNotEmpty(id)) {
                    listItem = nurseSiteMaintainConfigItemRepository.findByParentId(id);
                    if (listItem != null && listItem.size() > 0) {
                        deleteUnCheckedMaintainData(id, assetUid, false);
                    } else {
                        deleteUnCheckedMaintainData(id, assetUid, true);
                        NurseSiteMaintainConfig tempConfig = nurseSiteMaintainConfigRepository.findById(id);
                        if (tempConfig != null) {
//                            nurseSiteMaintainConfigRepository.delete(tempConfig);
                            //改为逻辑删除
                            tempConfig.setDeleted(true);
                            nurseSiteMaintainConfigRepository.save(tempConfig);
                        }
                    }
                }
            }
        }
    }

    private void doJobUpdate(String[] params) {
        String configId = params[0];
        String userUid = params[3];
        NurseSiteMaintainConfig nurseSiteMaintainConfig = nurseSiteMaintainConfigRepository.findById(configId);
        if (!Strings.isEmpty(configId) && !Strings.isEmpty(userUid)) {
            updateUnCheckedMaintainData(userUid, nurseSiteMaintainConfig);
        }
    }

    private void doJobCreate(String[] params) throws Exception {
        String configId = params[0];
        String strStart = params[1];
        String strEnd = params[2];
        String userUid = params[3];
        NurseSiteMaintainConfig nurseSiteMaintainConfig = nurseSiteMaintainConfigRepository.findById(configId);

        LocalDate startBatchDate = LocalDate.parse(strStart, DateTimeFormatter.BASIC_ISO_DATE);
        LocalDate endBatchDate = LocalDate.parse(strEnd, DateTimeFormatter.BASIC_ISO_DATE);

        Set<String> listAssetUid = nurseSiteMaintainConfigItemRepository.findByParentId(configId).stream()
                .map(NurseSiteMaintainConfigItem::getAssetUid).collect(Collectors.toSet());
        Set<String> orgUidList = findAccessClinicalUidByUserUid(userUid);

        if (null != nurseSiteMaintainConfig && startBatchDate != null && endBatchDate != null && !Strings.isEmpty(userUid)
                && listAssetUid != null && !CollectionUtils.isEmpty(orgUidList) && orgUidList.contains(nurseSiteMaintainConfig.getClinicalDeptUID())) {
            batchSaveMaintainData(startBatchDate, endBatchDate, userUid, nurseSiteMaintainConfig);
        }
    }

    private void doJobComplete(String[] params, Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String strStart = params[1];
        String strEnd = params[2];
        String userUid = params[3];
        Date startBatchDate = sdf.parse(strStart);
        Date endBatchDate = sdf.parse(strEnd);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        Calendar ca2 = Calendar.getInstance();
        ca2.set(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        Date delayDate = ca2.getTime();
        String signature = params[4];
        String clinical = params[5];
        Set<String> orgUidList = findAccessClinicalUidByUserUid(userUid);
        List<NurseSiteMaintain> listMaintain = null;
        if (StringUtils.isEmpty(clinical)) {
            listMaintain = nurseSiteMaintainRepository.findDefaultData(orgUidList, startBatchDate, endBatchDate);
        } else {
            listMaintain = nurseSiteMaintainRepository.findDataByClinicalOrgUid(clinical, startBatchDate, endBatchDate);
        }
        if (listMaintain != null && listMaintain.size() > 0) {
            for (int i = 0; i < listMaintain.size(); ++i) {
                nurseSiteMaintainService.tBatchCompleteMaintain(listMaintain.get(i), signature, userUid, date, delayDate);
            }
        }
    }

    private void doJobDelete(String[] params) {
        String maintainId = params[4];
        nurseSiteMaintainService.nativeDeleteMaintain(maintainId);
    }

    private void batchSaveMaintainData(LocalDate start, LocalDate end, String userUid, NurseSiteMaintainConfig nurseSiteMaintainConfig) {

        Integer type = nurseSiteMaintainConfig.getType();
        String typeValue = nurseSiteMaintainConfig.getTypeValue();

        Set<Tuple2<LocalDate, Integer>> scheduleDateSet = new HashSet<>();
        if (StringUtils.isNotEmpty(typeValue)) {
            Set<Integer> arr = Arrays.stream(typeValue.split(",")).map(w -> Integer.parseInt(w)).collect(Collectors.toSet());

            Calendar calendar = Calendar.getInstance();

            for (LocalDate date = start; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
                switch (type) {
                    case 1:
                        Integer intTypeValue = Integer.parseInt(typeValue);
                        for (int i = 0; i < intTypeValue; ++i) {
                            scheduleDateSet.add(new Tuple2<>(date, i + 1));
                        }
                        break;
                    case 2:
                        Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
                        Date utilDate = Date.from(instant);
                        calendar.setTime(utilDate);
                        if (arr.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
                            scheduleDateSet.add(new Tuple2<>(date, 0));
                        }
                        break;
                    case 3:
                        if (arr.contains(date.getDayOfMonth())) {
                            scheduleDateSet.add(new Tuple2<>(date, 0));
                        }
                        break;
                    default:
                }
            }
        }
        if(!CollectionUtils.isEmpty(scheduleDateSet)) {
            List<NurseSiteMaintain>  existMaintain = nurseSiteMaintainRepository
                    .findUncheckedMaintain(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    , Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()), nurseSiteMaintainConfig.getId());
            List<String> removeMaintainIdList = new ArrayList<>();
            existMaintain.stream().forEach(m -> {
                if (m.getStatus() == 0) {
                    removeMaintainIdList.add(m.getId());
                } else {
                    scheduleDateSet.remove(new Tuple2<>(m.getScheduleDate(), m.getSequence()));
                }
            });
            deleteMaintains(removeMaintainIdList);
            createAndSaveNurseSiteMaintains(scheduleDateSet, userUid, nurseSiteMaintainConfig);
        }
    }


    // where and
    public Set<String> findAccessClinicalUidByUserUid(String userUid) {
        List<String> accessOrgUidByUserUid = userOrgInfoRepository.findAccessUidByUserUid(userUid);
        return new HashSet<>(accessOrgUidByUserUid);
    }


    public void updateUnCheckedMaintainData(String userUid, NurseSiteMaintainConfig nurseSiteMaintainConfig) {
        // ‘覆盖历史未完成检查’，先删除，然后新建未完成的检查，如果是‘修改将来未完成检查’，只需要删除更新配置后的未完成检查
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if(nurseSiteMaintainConfig.getApplyDirectly()){
            try {
                String format = sdf.format(new Date());
                Date parse = sdf.parse(format);
                List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findUncheckedByConfig(nurseSiteMaintainConfig.getId());
                listMaintain=listMaintain.stream().filter(item-> item.getScheduleDate() != null && parse.before(item.getScheduleDate())).collect(Collectors.toList());
                Set<Tuple2<LocalDate, Integer>> scheduleDateSet = listMaintain.stream()
                        .map(i-> new Tuple2<>(Instant.ofEpochMilli(i.getScheduleDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                                i.getSequence())).collect(Collectors.toSet());
                deleteMaintains(listMaintain.stream().map(NurseSiteMaintain::getId).collect(Collectors.toList()));
                createAndSaveNurseSiteMaintains(scheduleDateSet, userUid,nurseSiteMaintainConfig );
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("修改历史未完成检查异常:", e);
            }

        }else if(nurseSiteMaintainConfig.getApplyFeatureData()){
            try {
                String date = sdf.format(nurseSiteMaintainConfig.getApplyFeatureTime());
                Date applyFeatureTime = sdf.parse(date);
                List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findUncheckedByConfigAfterToday(nurseSiteMaintainConfig.getId(), applyFeatureTime);
                Set<Tuple2<LocalDate, Integer>> scheduleDateSet = listMaintain.stream()
                        .map(i-> new Tuple2<>(Instant.ofEpochMilli(i.getScheduleDate().getTime()).atZone(ZoneId.systemDefault()).toLocalDate(),
                                i.getSequence())).collect(Collectors.toSet());

                deleteMaintains(listMaintain.stream().map(NurseSiteMaintain::getId).collect(Collectors.toList()));
                createAndSaveNurseSiteMaintains(scheduleDateSet, userUid,nurseSiteMaintainConfig);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("修改将来未完成检查异常:", e);
            }
        }

    }

    public void deleteUnCheckedMaintainData(String configId, String assetUid, boolean all) {
        List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findUncheckedByConfig(configId);
        if (!CollectionUtils.isEmpty(listMaintain)) {
            nurseSiteMaintainService.tDeleteUnCheckedMaintainData(listMaintain, assetUid, all);
        }
    }

    private void deleteMaintains( List<String> removeMaintainIdList){
        if (!CollectionUtils.isEmpty(removeMaintainIdList)) {
            List<String> maintainItemIdList = nurseSiteMaintainRepository.findItemIdByMaintainList(removeMaintainIdList);
            if (!CollectionUtils.isEmpty(maintainItemIdList)) {
                nurseSiteMaintainDataRepository.deleteMaintainDataByParentId(new HashSet<>(maintainItemIdList));
                nurseSiteMaintainItemRepository.deleteMaintainItemByParentId(new HashSet<>(removeMaintainIdList));
            }
            nurseSiteMaintainRepository.deleteMaintain(new HashSet<>(removeMaintainIdList));
        }
    }


    private void createAndSaveNurseSiteMaintains(Set<Tuple2<LocalDate, Integer>> scheduleDateSet, String userUid, NurseSiteMaintainConfig nurseSiteMaintainConfig) {

        Set<String> listAssetUid = nurseSiteMaintainConfigItemRepository.findByParentId(nurseSiteMaintainConfig.getId()).stream()
                .map(NurseSiteMaintainConfigItem::getAssetUid).collect(Collectors.toSet());
        String strCheckDetail = nurseSiteMaintainConfig.getCheckItemsDetail();
        if (StringUtils.isEmpty(strCheckDetail) || CollectionUtils.isEmpty(listAssetUid)) {
            return;
        }
        JSONArray jsonArray = JSONArray.parseArray(strCheckDetail);

        List<NurseSiteMaintain> maintains = scheduleDateSet.stream().map(i-> createMaintain(Date.from(i._1().atStartOfDay(ZoneId.systemDefault()).toInstant()),i._2(),
                userUid,nurseSiteMaintainConfig, createNurseSiteMaintainItem(listAssetUid, jsonArray))
        ).collect(Collectors.toList());
        nurseSiteMaintainRepository.save(maintains);
    }

    private List<NurseSiteMaintainItem> createNurseSiteMaintainItem(Set<String> assetUidSet , JSONArray jsonArray){
        List<NurseSiteMaintainItem> listNewMaintainItem = new ArrayList<>();
        listNewMaintainItem.addAll(assetUidSet.stream().map(aid -> {
            NurseSiteMaintainItem item = new NurseSiteMaintainItem();
            item.setAssetUid(aid);
            item.setListData(jsonArray.stream()
                    .map(d -> (JSONObject) d)
                    .map(d -> {
                        NurseSiteMaintainData p = new NurseSiteMaintainData();
                        p.setCheckItem(d.getString("name"));
                        p.setIndex(d.getLong("key"));
                        p.setItem(item);
                        return p;
                    }).collect(Collectors.toList()));
            return item;
        }).collect(Collectors.toList()));
        return listNewMaintainItem;
    }

    private NurseSiteMaintain createMaintain(Date date, Integer sequence, String userUid, NurseSiteMaintainConfig nurseSiteMaintainConfig,List<NurseSiteMaintainItem> listNewMaintainItem) {
        if (nurseSiteMaintainConfig.getType() != null && nurseSiteMaintainConfig.getType() != 1) {
            sequence = 0;
        }
        NurseSiteMaintain tempNurseSiteMaintain = new NurseSiteMaintain();
        Date currentDate = new Date();
        tempNurseSiteMaintain.setTenantUID(nurseSiteMaintainConfig.getTenantUID());
        tempNurseSiteMaintain.setInstitutionUID(nurseSiteMaintainConfig.getInstitutionUID());
        tempNurseSiteMaintain.setHospitalUID(nurseSiteMaintainConfig.getHospitalUID());
        tempNurseSiteMaintain.setSiteUID(nurseSiteMaintainConfig.getSiteUID());
        tempNurseSiteMaintain.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
        tempNurseSiteMaintain.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
        tempNurseSiteMaintain.setCheckDate(null);
        tempNurseSiteMaintain.setCreateByUid(userUid);
        tempNurseSiteMaintain.setCreateDate(currentDate);
        tempNurseSiteMaintain.setModifyByUid(userUid);
        tempNurseSiteMaintain.setModifyDate(currentDate);
        tempNurseSiteMaintain.setScheduleDate(date);
        tempNurseSiteMaintain.setSequence(sequence);
        tempNurseSiteMaintain.setStatus(0);
        tempNurseSiteMaintain.setName(nurseSiteMaintainConfig.getName());
        tempNurseSiteMaintain.setListNurseSiteMaintainItem(listNewMaintainItem);
        tempNurseSiteMaintain.setConfigId(nurseSiteMaintainConfig.getId());
        tempNurseSiteMaintain.getListNurseSiteMaintainItem().stream().forEach(tempItem -> {
            tempItem.setNurseSiteMaintain(tempNurseSiteMaintain);
            tempItem.setCheckDate(null);
            tempItem.setCreateByUid(userUid);
            tempItem.setCreateDate(currentDate);
            tempItem.setModifyByUid(userUid);
            tempItem.setModifyDate(currentDate);
            tempItem.setScheduleDate(date);
            tempItem.setConfigId(nurseSiteMaintainConfig.getId());
            tempItem.setSequence(tempNurseSiteMaintain.getSequence());
            tempItem.setName(nurseSiteMaintainConfig.getName());
            tempItem.setClinicalDeptName(nurseSiteMaintainConfig.getClinicalDeptName());
            tempItem.setClinicalDeptUID(nurseSiteMaintainConfig.getClinicalDeptUID());
            tempItem.setStatus(0);
        });
        return tempNurseSiteMaintain;
    }

    @Transactional(rollbackFor = Exception.class)
    public void doJobDeleteConfig(String[] params){
        List<NurseSiteMaintainConfig> list = nurseSiteMaintainConfigRepository.findByDeletedStatus();

        if(CollectionUtils.isEmpty(list)){
            return;
        }
        String userUid = params[3];

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (NurseSiteMaintainConfig config : list) {
            Date date = null;
            try {
                date = sdf.parse(sdf.format(new Date()));
            } catch (ParseException e) {
                LOGGER.error("删除检查项配置，日期格式化异常", e);
                return;
            }
            // 删除将来未完成工单
            List<NurseSiteMaintain> listMaintain = nurseSiteMaintainRepository.findUncheckedByConfigAfterToday(config.getId(), date);
            deleteMaintains(listMaintain.stream().map(NurseSiteMaintain::getId).collect(Collectors.toList()));
            config.setDeleteResult(true);
            config.setModifyByUid(userUid);
            config.setModifyDate(new Date());
            nurseSiteMaintainConfigRepository.save(config);
        }
    }

}

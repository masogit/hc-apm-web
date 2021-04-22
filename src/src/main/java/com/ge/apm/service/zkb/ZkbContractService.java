package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/4/16
 * @description :
 */
@Service
public class ZkbContractService {

    public static final String SHEET_ZKB_CONTRACT = "合同";
    public static final String SHEET_ZKB_CONTRACT_COURSE = "相关课程";
    public static final String SHEET_ZKB_CONTRACT_ASSET = "相关设备";


    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbCourseRepository courseRepository;

    @Autowired
    private ZkbContractCourseRepository contractCourseRepository;

    @Autowired
    private ZkbContractRepository contractRepository;

    @Autowired
    private ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    private ZkbContractLogService zkbContractLogService;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    public List<TenantInfo> getSourceTenantInfoList(){

        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("type", SearchFilter.Operator.EQ, 3));
        return tenantInfoDao.findBySearchFilter(searchFilters);
    }

    public TenantInfo getTenantInfo(String tenantUID){
        return tenantInfoDao.findByuid(tenantUID);
    }

    public List<Map<String, Object>> createZkbContractData(List<ZkbContract> zkbContractList) {
        List<Map<String, Object>> zkbContractDataList = new ArrayList<>();

        for (ZkbContract zkbContract : zkbContractList) {
            Map<String, Object> zkbContractDataMap = new HashMap<>();
            zkbContract.setAgentName(getTenantInfo(zkbContract.getTenantUID()).getName());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_region"), zkbContract.getRegion());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_agentName"), zkbContract.getAgentName());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_contractNo"), zkbContract.getContractNo());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_contractType"), zkbContract.getContractType());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_startDate"), DateFormatUtils.format(zkbContract.getStartDate(),"yyyy-MM-dd"));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_endDate"), DateFormatUtils.format(zkbContract.getEndDate(),"yyyy-MM-dd"));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_contractState"), zkbContract.getContractState());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_ibNumber"), zkbContract.getIbNumber());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota"), isEmptyInteger(zkbContract.getSpareQuota()));
            zkbContractDataMap.put("探头任意剩余名额", isEmptyInteger(zkbContract.getProbeQuota()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes"), isEmptyInteger(zkbContract.getGeArtificialSupportTimes()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail"), isEmptyInteger(zkbContract.getSpareArtificail()));

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota_total"), isEmptyInteger(zkbContract.getSpareQuotaTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota_apply"), isEmptyInteger(zkbContract.getSpareQuotaApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota_use"), isEmptyInteger(zkbContract.getSpareQuotaUse()));

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_probeQuota_total"), isEmptyInteger(zkbContract.getProbeQuotaTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_probeQuota_apply"), isEmptyInteger(zkbContract.getProbeQuotaApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_probeQuota_use"), isEmptyInteger(zkbContract.getProbeQuotaUse()));
            zkbContractDataMap.put("探头普通剩余名额", isEmptyInteger(zkbContract.getProbeQuotaNormal()));
            zkbContractDataMap.put("探头特殊剩余名额", isEmptyInteger(zkbContract.getProbeQuotaSpecial()));
            zkbContractDataMap.put("探头高值剩余名额", isEmptyInteger(zkbContract.getProbeQuotaHigh()));

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes_total"), isEmptyInteger(zkbContract.getGeArtificialSupportTimesTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes_apply"), isEmptyInteger(zkbContract.getGeArtificialSupportTimesApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes_use"), isEmptyInteger(zkbContract.getGeArtificialSupportTimesUse()));

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail_total"), isEmptyInteger(zkbContract.getSpareArtificailTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail_apply"), isEmptyInteger(zkbContract.getSpareArtificailApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail_use"), isEmptyInteger(zkbContract.getSpareArtificailUse()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_remark"), zkbContract.getRemark());

            zkbContract.setContractState(calcContractState(zkbContract.getStartDate(),zkbContract.getEndDate()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_contractState"),zkbContract.getContractState());

            zkbContractDataList.add(zkbContractDataMap);
            //处理合同课程数据
            this.putZkbContractCourseData(zkbContract, zkbContractDataMap);

            //处理合同设备信息
            this.putZkbContractAssetData(zkbContract,zkbContractDataMap);
        }

        return zkbContractDataList;
    }

    public Integer isEmptyInteger(Integer value){
        if(value == null){
            return 0;
        }else{
            return value;
        }
    }

    public Double isEmptyDouble(Double value){
        if(value == null){
            return new Double(0);
        }else{
            return value;
        }
    }

    public String calcContractState(Date startDate,Date endDate){
        Integer startFlag = whenEQ(new Date(),startDate);
        Integer endFlag = whenEQ(new Date(),endDate);
        if(startFlag < 0){
            return "未生效";
        }else if(startFlag >= 0 && endFlag <=0){
            return "有效";
        }else{
            return "已过期";
        }
    }

    /**
     * 比较两日期是否大小
     * @return 比较两日期大小。如果date>=otherDate则返回true,否则false
     */
    public static Integer whenEQ(Date d, Date o) {
        Integer flag = 0;
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
            Date date = f.parse(f.format(d));
            Date otherDate = f.parse(f.format(o));
            if (date != null && otherDate != null) {
                long time = date.getTime() - otherDate.getTime();
                if (time < 0) {
                    flag = -1;
                }else if(time == 0){
                    flag =0;
                }else{
                    flag = 1;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void putZkbContractCourseData(ZkbContract zkbContract, Map<String, Object> zkbContractDataMap) {
        List<ZkbContractCourse> zkbContractCourseList = zkbContract.getZkbContractCourses();

        List<Map<String, Object>> zkbContractCourseDataList = new ArrayList<>();
        for (ZkbContractCourse zkbContractCourse : zkbContractCourseList) {
            Map<String, Object> zkbContractCourseDataMap = new HashMap<>();
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_region"), zkbContract.getRegion());
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_agentName"), zkbContract.getAgentName());
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_contractNo"), zkbContract.getContractNo());
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_contractType"), zkbContract.getContractType());
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_startDate"), DateFormatUtils.format(zkbContract.getStartDate(),"yyyy-MM-dd"));
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_endDate"), DateFormatUtils.format(zkbContract.getEndDate(),"yyyy-MM-dd"));
            zkbContractCourseDataMap.put(WebUtil.getMessage("zkbContract_contractState"), zkbContract.getContractState());
            zkbContractCourseDataMap.put("课程ID", zkbContractCourse.getZkbCourse().getCid());
            zkbContractCourseDataMap.put("课程名称", zkbContractCourse.getZkbCourse().getCname());
            zkbContractCourseDataMap.put("modality", zkbContractCourse.getZkbCourse().getModality());
            zkbContractCourseDataMap.put("培训合同号", zkbContractCourse.getCourseContractNo());
            zkbContractCourseDataMap.put("课程剩余名额", isEmptyDouble(zkbContractCourse.getMainCourseCount()));
            zkbContractCourseDataMap.put("课程总额", isEmptyDouble(zkbContractCourse.getMainCourseCountTotal()));
            zkbContractCourseDataMap.put("课程申请名额", isEmptyDouble(zkbContractCourse.getMainCourseCountApply()));
            zkbContractCourseDataMap.put("课程已使用名额", isEmptyDouble(zkbContractCourse.getMainCourseCountUse()));

            zkbContractCourseDataList.add(zkbContractCourseDataMap);
        }

        zkbContractDataMap.put("zkbContractCourseDataList", zkbContractCourseDataList);
    }

    public void putZkbContractAssetData(ZkbContract zkbContract, Map<String, Object> zkbContractDataMap){
        List<ZkbContractAsset> assets = zkbContractAssetRepository.findByContractId(zkbContract.getId());
        List<Map<String, Object>> zkbContractAssetDataList = new ArrayList<>();
        for (ZkbContractAsset zkbContractAsset : assets) {
            Map<String, Object> zkbContractAssetDataMap = new HashMap<>();
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_region"), zkbContract.getRegion());
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_agentName"), zkbContract.getAgentName());
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_contractNo"), zkbContract.getContractNo());
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_contractType"), zkbContract.getContractType());
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_startDate"), DateFormatUtils.format(zkbContract.getStartDate(),"yyyy-MM-dd"));
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_endDate"), DateFormatUtils.format(zkbContract.getEndDate(),"yyyy-MM-dd"));
            zkbContractAssetDataMap.put(WebUtil.getMessage("zkbContract_contractState"), zkbContract.getContractState());
            zkbContractAssetDataMap.put("设备二维码", zkbContractAsset.getAssetInfo().getQrCode());
            zkbContractAssetDataMap.put("Sys ID", zkbContractAsset.getAssetInfo().getSystemId());
            zkbContractAssetDataMap.put("科室", zkbContractAsset.getAssetInfo().getClinicalDeptName());
            zkbContractAssetDataMap.put("设备名称", zkbContractAsset.getAssetInfo().getName());
            String rfValue = zkbContractAsset.getAssetInfo().getRepairForm() ==null ? "" : String.valueOf(zkbContractAsset.getAssetInfo().getRepairForm());
            zkbContractAssetDataMap.put("维修类型", WebUtil.getFieldValueMessage("repairForm",rfValue));
            if(zkbContractAsset.getAssetInfo() != null){
                zkbContractAssetDataMap.put("品牌", zkbContractAsset.getAssetInfo().getMakerShortName());
            }
            if(StringUtils.isNotEmpty(zkbContractAsset.getAssetType())){
                zkbContractAssetDataMap.put("高低端机", WebUtil.getFieldValueMessage("zkbContractAssetType",zkbContractAsset.getAssetType()));
            }
            zkbContractAssetDataMap.put("备注", zkbContractAsset.getAssetInfo().getComments());

            zkbContractAssetDataList.add(zkbContractAssetDataMap);
        }

        zkbContractDataMap.put("zkbContractAssetDataList", zkbContractAssetDataList);
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbContractData, List<Map<String, Object>> zkbContractCourseData,List<Map<String, Object>> zkbContractAssetData) {
        Integer keyRow_zkbContract = 1;
        Integer dataRow_zkbContract = 2;

        for (Map<String, Object> item : zkbContractData) {
            doc.writeRowData(SHEET_ZKB_CONTRACT, keyRow_zkbContract, dataRow_zkbContract++, item);
        }

        Integer keyRow_zkbContractCourse = 1;
        Integer dataRow_zkbContractCourse = 2;
        for (Map<String, Object> item : zkbContractCourseData) {
            doc.writeRowData(SHEET_ZKB_CONTRACT_COURSE, keyRow_zkbContractCourse, dataRow_zkbContractCourse++, item);
        }

        Integer keyRow_zkbContractAsset = 1;
        Integer dataRow_zkbContractAsset = 2;
        for (Map<String, Object> item : zkbContractAssetData) {
            doc.writeRowData(SHEET_ZKB_CONTRACT_ASSET, keyRow_zkbContractAsset, dataRow_zkbContractAsset++, item);
        }
    }


    public List<Map<String, Object>> createZkbContractCourseData(List<Map<String, Object>> zkbContractDataList) {
        List<Map<String, Object>> zkbContractCourseDataList = new ArrayList<>();
        for (Map<String, Object> zkbContractDataMap : zkbContractDataList) {
            if(zkbContractDataMap.containsKey("zkbContractCourseDataList")){
                zkbContractCourseDataList.addAll((List<Map<String, Object>>)zkbContractDataMap.get("zkbContractCourseDataList"));
            }
        }

        return zkbContractCourseDataList;
    }

    public List<Map<String, Object>> createZkbContractAssetData(List<Map<String, Object>> zkbContractDataList) {
        List<Map<String, Object>> zkbContractAssetDataList = new ArrayList<>();
        for (Map<String, Object> zkbContractDataMap : zkbContractDataList) {
            if(zkbContractDataMap.containsKey("zkbContractAssetDataList")){
                zkbContractAssetDataList.addAll((List<Map<String, Object>>)zkbContractDataMap.get("zkbContractAssetDataList"));
            }
        }

        return zkbContractAssetDataList;
    }

    public List<ZkbCourse> selectAllCourse(){
        List<ZkbCourse> zkbCourses = courseRepository.find();
        return zkbCourses;
    }

    /**
     * 判断合同号是否存在，同一个租户只能存在一个合同号
     * 区分新增与编辑的情况
     */
    public HashMap<String,Object> checkContract(ZkbContract zkbContract,String actionName){
        HashMap<String,Object> resut = new HashMap<>();
        boolean flag = true;
        String errorLog = "";
        List<ZkbContract> contractList = contractRepository.findByTenantUIDAndContractNo(zkbContract.getTenantUID(),zkbContract.getContractNo());
        if(contractList.size() ==0){
            flag = true;
        }else if (contractList.size() == 1 && "Edit".equals(actionName)){
            ZkbContract exist = contractList.get(0);
            if(!exist.getId().equals(zkbContract.getId())){
                flag = false;
                errorLog += WebUtil.getMessage("ErrorContractNoIsExist")+",";
            }
        }else{
            flag = false;
            errorLog += WebUtil.getMessage("ErrorContractNoIsExist")+",";
        }

        if(zkbContract.getStartDate().compareTo(zkbContract.getEndDate()) > 0){
            flag = false;
            errorLog += "开始时间不能大于结束时间,";
        }

        int spareQuotaTotal = zkbContract.getSpareQuotaTotal() == null ? 0 : zkbContract.getSpareQuotaTotal();
        int spareQuota = zkbContract.getSpareQuota() == null ? 0 : zkbContract.getSpareQuota();
        int spareQuotaApply = zkbContract.getSpareQuotaApply() == null ? 0 : zkbContract.getSpareQuotaApply();
        int spareQuotaUse = zkbContract.getSpareQuotaUse() == null ? 0 : zkbContract.getSpareQuotaUse();
        if(spareQuotaTotal != (spareQuota+spareQuotaApply+spareQuotaUse)){
            flag = false;
            errorLog += "备件总额不等于备件剩余名额、备件申请名额、备件已使用名额之和,";
        }

        int probeQuotaTotal = zkbContract.getProbeQuotaTotal() == null ? 0 : zkbContract.getProbeQuotaTotal();
        int probeQuota = zkbContract.getProbeQuota() == null ? 0 : zkbContract.getProbeQuota();
        int probeQuotaNormal = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
        int probeQuotaSpecial = zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial();
        int probeQuotaApply = zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply();
        int probeQuotaUse = zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse();
        if(probeQuotaTotal != (probeQuota + probeQuotaApply + probeQuotaUse + probeQuotaNormal + probeQuotaSpecial)){
            flag = false;
            errorLog += "探头总额不等于剩余名额、申请名额、已使用名额之和,";
        }

        int geArtificialSupportTimesTotal = zkbContract.getGeArtificialSupportTimesTotal() == null ? 0 : zkbContract.getGeArtificialSupportTimesTotal();
        int geArtificialSupportTimes = zkbContract.getGeArtificialSupportTimes() == null ? 0 : zkbContract.getGeArtificialSupportTimes();
        int geArtificialSupportTimesApply = zkbContract.getGeArtificialSupportTimesApply() == null ? 0 : zkbContract.getGeArtificialSupportTimesApply();
        int geArtificialSupportTimesUse = zkbContract.getGeArtificialSupportTimesUse() == null ? 0 : zkbContract.getGeArtificialSupportTimesUse();
        if(geArtificialSupportTimesTotal != (geArtificialSupportTimes + geArtificialSupportTimesApply + geArtificialSupportTimesUse)){
            flag = false;
            errorLog += "人工支持总次数不等于人工支持剩余次数、人工支持申请次数、人工支持已使用次数之和,";
        }

        int spareArtificialTotal = zkbContract.getSpareArtificailTotal() == null ? 0 : zkbContract.getSpareArtificailTotal();
        int spareArtificial = zkbContract.getSpareArtificail() == null ? 0 : zkbContract.getSpareArtificail();
        int spareArtificialApply = zkbContract.getSpareArtificailApply() == null ? 0 : zkbContract.getSpareArtificailApply();
        int spareArtificialUse = zkbContract.getSpareArtificailUse() == null ? 0 : zkbContract.getSpareArtificailUse();
        if(spareArtificialTotal != (spareArtificial + spareArtificialApply + spareArtificialUse)){
            flag = false;
            errorLog += "备件/人工/保养总额不等于备件/人工/保养剩余名额、备件/人工/保养申请名额、备件/人工/保养已使用名额之和,";
        }

        if(zkbContract.getZkbContractCourses() != null && zkbContract.getZkbContractCourses().size() > 0){
            for(ZkbContractCourse c : zkbContract.getZkbContractCourses()){
                double courseCountTotal = c.getMainCourseCountTotal() == null ? 0 : c.getMainCourseCountTotal();
                double courseCount = c.getMainCourseCount() == null ? 0 : c.getMainCourseCount();
                double courseCountApply = c.getMainCourseCountApply() == null ? 0 : c.getMainCourseCountApply();
                double courseCountUse = c.getMainCourseCountUse() == null ? 0 : c.getMainCourseCountUse();
                if(courseCountTotal != (courseCount + courseCountApply + courseCountUse)){
                    flag = false;
                    errorLog += "课程总额不等于课程剩余名额、课程申请名额、课程已使用名额之和,";
                }
            }
        }
        if (errorLog.length()>0){
            errorLog = errorLog.substring(0,errorLog.length()-1);
        }
        resut.put("flag",flag);
        resut.put("errorLog",errorLog);
        return resut;
    }

    /**
     * 保存合同
     */
    public ZkbContract saveContract(ZkbContract zkbContract,String actionName,String reason,UserAccount currentUser){
        zkbContract.setLastModifiedDate(new Date());
        zkbContract.setLastModifiedBy(currentUser.getLoginName());
        zkbContract = contractRepository.save(zkbContract);
        if("Edit".equals(actionName)){
            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"contractUpdate","","",reason,currentUser);
        }
        return zkbContract;
    }

    public void deleteContract(ZkbContract zkbContract,String reason,UserAccount currentUser){
        String courseData = "";
        String assetData = "";
        if(zkbContract.getZkbContractCourses() != null && zkbContract.getZkbContractCourses().size() > 0){
            for(ZkbContractCourse c : zkbContract.getZkbContractCourses()){
                courseData += c.getZkbCourse().getCid()+",";
            }
            if(courseData.length() >0){
                courseData = courseData.substring(0,courseData.length()-1);
            }
        }
        List<ZkbContractAsset> zkbContractAssets = zkbContractAssetRepository.findByContractId(zkbContract.getId());
        if(zkbContractAssets != null && zkbContractAssets.size() > 0){
            for(ZkbContractAsset c : zkbContractAssets){
                assetData += c.getAssetInfo().getQrCode()+",";
            }
            if(assetData.length() >0){
                assetData = assetData.substring(0,assetData.length()-1);
            }
        }
        contractRepository.delete(zkbContract);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"contractDelete",courseData,assetData,reason,currentUser);

    }


    /**
     * 保存合同的课程，并且将合同课程加入列表中
     */
    @Transactional
    public List<ZkbContractCourse> saveContractCourse(ZkbContractCourse zkbContractCourse,List<ZkbContractCourse> contractCourseList,ZkbContract zkbContract,String actionDetailName,String reason,UserAccount currentUser){
        contractCourseRepository.save(zkbContractCourse);
        List<ZkbContractCourse> tempZkbContractCourseList = new ArrayList<>();
        contractCourseList.forEach((item) -> {
            if(!item.getId().equals(zkbContractCourse.getId())){
                tempZkbContractCourseList.add(item);
            }
        });
        tempZkbContractCourseList.add(zkbContractCourse);

        if("Edit".equals(actionDetailName)){
            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"courseUpdate",zkbContractCourse.getZkbCourse().getCid(),"",reason,currentUser);
        }
        return tempZkbContractCourseList;
    }

    /**
     * 删除合同课程，并把列表中课程删除
     */
    @Transactional
    public List<ZkbContractCourse> delContractCourse(ZkbContractCourse zkbContractCourse,List<ZkbContractCourse> contractCourseList,ZkbContract zkbContract,UserAccount currentUser,String reason){
        contractCourseRepository.delete(zkbContractCourse);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"courseDelete",zkbContractCourse.getZkbCourse().getCid(),"",reason,currentUser);

        List<ZkbContractCourse> tempZkbContractCourseList = new ArrayList<>();
        contractCourseList.forEach((item) -> {
            if(!item.getId().equals(zkbContractCourse.getId())){
                tempZkbContractCourseList.add(item);
            }
        });
        contractCourseList.remove(zkbContractCourse);
        return contractCourseList;
    }

    /**
     * 根据合同号查询合同下的设备
     */
    public List<ZkbContractAsset> queryAssetByContractId(String contractId){
        List<ZkbContractAsset> list = zkbContractAssetRepository.findByContractId(contractId);
        return list;
    }

    /**
     * 根据选择的设备，保存合同设备数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveAsset(List<AssetInfo> targetAssets,ZkbContract zkbContract,UserAccount currentUser,List<ZkbContractAsset> zkbContractAssetList){
        for(AssetInfo a : targetAssets){
            boolean flag = false;
            if(zkbContractAssetList != null && zkbContractAssetList.size() > 0){
                for(ZkbContractAsset asset : zkbContractAssetList){
                    if(a.getUid().equals(asset.getAssetId())){
                        flag=true;
                        break;
                    }
                }
            }
            if(flag){
                continue;
            }
            ZkbContractAsset zkbContractAsset = new ZkbContractAsset();
            zkbContractAsset.setContractId(zkbContract.getId());
            zkbContractAsset.setAssetId(a.getUid());
            zkbContractAssetRepository.save(zkbContractAsset);

            zkbContract.setLastModifiedDate(new Date());
            zkbContract.setLastModifiedBy(currentUser.getLoginName());
            contractRepository.save(zkbContract);

            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetAdd","",a.getQrCode(),"",currentUser);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(ZkbContract zkbContract,ZkbContractAsset zkbContractAsset,String reason,UserAccount currentUser){
        zkbContractAssetRepository.save(zkbContractAsset);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetEdit","",zkbContractAsset.getAssetInfo().getQrCode(),reason,currentUser);

        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbContractAsset.getAssetId());
        assetInfo.setRepairForm(zkbContractAsset.getAssetInfo().getRepairForm());
        assetInfoRepository.save(assetInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(ZkbContractAsset zkbContractAsset,ZkbContract zkbContract,UserAccount currentUser,String reason) {
        zkbContractAssetRepository.delete(zkbContractAsset);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetDelete","",zkbContractAsset.getAssetInfo().getQrCode(),reason,currentUser);
    }

}

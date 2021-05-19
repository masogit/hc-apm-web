package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.time.*;
import java.util.*;

/**
 * Created by 212605082 on 2018/8/20.
 */
@Service
public class ZkbContractImportService {
    private static final Logger logger = LoggerFactory.getLogger(ZkbContractImportService.class);

    public static final String EXCEL_CONTRACT_TEMPLATE_VERSION = "v1.0";

    public static final String SHEET_SERVICE_CONTRACT = "合同";
    public static final String SHEET_SERVICE_CONTRACT_COURSE = "相关课程";
    public static final String SHEET_SERVICE_CONTRACT_Asset = "相关设备";
    public static final int EXCEL_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACT_DATA_ROW = 2;

    public static final int EXCEL_EXPORT_CONTRACT_HEADER_ROW = 1;
    public static final int EXCEL_EXPORT_CONTRACT_DATA_ROW = 2;

    @Autowired
    private ZkbContractRepository zkbContractRepository;

    @Autowired
    private ZkbContractCourseRepository zkbContractCourseRepository;

    @Autowired
    private ZkbContractAssetRepository zkbContractAssetRepository;

    @Autowired
    private ZkbCourseRepository courseRepository;

    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Autowired
    private TenantInfoRepository tenantInfoDao;
    @Autowired
    private ZkbContractLogService zkbContractLogService;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public ZkbContract queryContractByNo(String contractNo){
        ZkbContract contracts = zkbContractRepository.findFirstByContractNo(contractNo);
        return contracts;
    }

    public AssetInfo queryAssetInfoByQrCode(String qrCode){
        AssetInfo assetInfo = assetInfoRepository.findFirstByQrCode(qrCode);
        return assetInfo;
    }

    public ZkbContractAsset queryContractAssetByContractIdAndAssetId(String contractId,String assetId){
        ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findFirstByContractIdAndAssetId(contractId,assetId);
        return zkbContractAsset;
    }

    public ZkbContractCourse queryContractCourseByContractIdAndCourseId(String contractId,String courseId){
        ZkbContractCourse zkbContractCourse = zkbContractCourseRepository.findFirstByContractIdAndCourseId(contractId,courseId);
        return zkbContractCourse;
    }

    //region 合同主表
    public List<Map<String, Object>> getContractMapFromTemplate(ExcelDocument doc) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();

        //for (Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, dataRow); rowMap.size() > 0 ; rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, keyRow, ++dataRow)) {
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT, EXCEL_CONTRACT_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            ZkbContractVO contractInfoVO = new ZkbContractVO();


            contractInfoVO.setLineNum(i);

            ZkbContractRow contractRow = new ZkbContractRow(contractInfoVO, rowMap);
            contractRow.setFieldStringValue("合同号", "setContractNo");
            contractRow.setFieldStringValue("区域","setRegion");
            contractRow.setFieldStringValue("代理商","setAgentName");
            contractRow.setFieldStringValue("合同类型", "setContractType");
            contractRow.setFieldDateValue("合同开始日期", "setStartDate");
            contractRow.setFieldDateValue("合同结束日期", "setEndDate");
            contractRow.setFieldNumValue("IB数量", "setIbNumber");
            contractRow.setFieldNumValue("备件剩余名额", "setSpareQuota");
            contractRow.setFieldNumValue("备件总额", "setSpareQuotaTotal");
            contractRow.setFieldNumValue("备件已使用名额", "setSpareQuotaUse");
            contractRow.setFieldNumValue("人工支持总次数", "setGeArtificialSupportTimesTotal");
            contractRow.setFieldNumValue("人工支持剩余次数", "setGeArtificialSupportTimes");
            contractRow.setFieldNumValue("人工支持已使用次数", "setGeArtificialSupportTimesUse");
            contractRow.setFieldNumValue("探头任意总额", "setProbeQuotaTotal");
            contractRow.setFieldNumValue("探头任意剩余名额", "setProbeQuota");
            contractRow.setFieldNumValue("探头任意已使用名额", "setProbeQuotaUse");


            contractRow.setFieldNumValue("探头普通总额", "setProbeQuotaNormalTotal");
            contractRow.setFieldNumValue("探头普通剩余名额", "setProbeQuotaNormal");
            contractRow.setFieldNumValue("探头普通已使用名额", "setProbeQuotaNormalUse");

            contractRow.setFieldNumValue("探头特殊总额", "setProbeQuotaSpecialTotal");
            contractRow.setFieldNumValue("探头特殊剩余名额", "setProbeQuotaSpecial");
            contractRow.setFieldNumValue("探头特殊已使用名额", "setProbeQuotaSpecialUse");

            contractRow.setFieldNumValue("探头高值总额", "setProbeQuotaHighTotal");
            contractRow.setFieldNumValue("探头高值剩余名额", "setProbeQuotaHigh");
            contractRow.setFieldNumValue("探头高值已使用名额", "setProbeQuotaHighUse");

            contractRow.setFieldNumValue("备件+人工+保养总额", "setSpareArtificailTotal");
            contractRow.setFieldNumValue("备件+人工+保养剩余名额", "setSpareArtificail");
            contractRow.setFieldNumValue("备件+人工+保养已使用名额", "setSpareArtificailUse");
            contractRow.setFieldStringValue("备注", "setRemark");

            if(contractInfoVO.isNull()){
                break;
            }

            map.put("contractInfoVO", contractInfoVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(contractInfoVO.getId()) ? ImportStatus.New.name() : ImportStatus.Exist.name());
            map.put("hashCode", contractInfoVO.hashCode());

            result.add(map);
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }


    public void importData(List<Map<String, Object>> importContractMapList,UserAccount currentUser) {

        int row = 0;
        try {
            Map<String, String> tenantInfoMap =getTenantInfoMap();
            for (Map<String, Object> item : importContractMapList) {
                ++row;
                if(checkData(item, importContractMapList,tenantInfoMap)) {
                    ZkbContractVO contractInfoVO = (ZkbContractVO)item.get("contractInfoVO");
                    contractInfoVO.setTenantUID(tenantInfoMap.get(contractInfoVO.getAgentName()));
                    importOneContract(contractInfoVO,currentUser);
                }

            }

        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }

    public Map<String,String> getTenantInfoMap(){
        List<TenantInfo> tenantInfos = tenantInfoDao.find();
        Map<String, String> tenantInfoMap = new HashMap<>();
        for(TenantInfo t : tenantInfos){
            tenantInfoMap.put(t.getName(),t.getUid());
        }
        return tenantInfoMap;
    }

    @Transactional(rollbackFor = Exception.class)
    protected void importOneContract(ZkbContractVO contractInfoVO,UserAccount currentUser) {
        ZkbContract newZkbContract = new ZkbContract();
        BeanUtils.copyProperties(contractInfoVO, newZkbContract);
        newZkbContract.setCreatedBy(currentUser.getLoginName());
        newZkbContract.setCreatedDate(new Date());

        newZkbContract.setSpareQuotaApply(newZkbContract.getSpareQuotaTotal() - newZkbContract.getSpareQuotaUse() - newZkbContract.getSpareQuota());
        newZkbContract.setProbeQuotaApply(newZkbContract.getProbeQuotaTotal() - newZkbContract.getProbeQuotaUse() - newZkbContract.getProbeQuota());
        newZkbContract.setGeArtificialSupportTimesApply(newZkbContract.getGeArtificialSupportTimesTotal() - newZkbContract.getGeArtificialSupportTimesUse() - newZkbContract.getGeArtificialSupportTimes());
        newZkbContract.setSpareArtificailApply(newZkbContract.getSpareArtificailTotal() - newZkbContract.getSpareArtificailUse() - newZkbContract.getSpareArtificail());
        zkbContractRepository.save(newZkbContract);
    }

    public boolean checkData(Map<String, Object> item, List<Map<String, Object>> importContractMapList,Map<String, String> tenantInfoMap) {

        boolean isValidateData = true;
        ZkbContractVO contractInfoVO = (ZkbContractVO)item.get("contractInfoVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ZkbContractRow.clearErrMessage(item);
        if(contractInfoVO.getContractNo() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorContractNoIsEmpty"));
            isValidateData = false;
        }else{
            ZkbContract zkbContract = queryContractByNo(contractInfoVO.getContractNo());
            if(zkbContract != null){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorContractNoIsExist"));
                isValidateData = false;
            }
        }

        if(contractInfoVO.getRegion() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorRegionIsEmpty"));
            isValidateData = false;
        }else{
            if(!EnumUtils.isValidEnum(ZkbContract.ContractRegion.class,contractInfoVO.getRegion())){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbRegionIsNotRange"));
                isValidateData = false;
            }
        }

        if(contractInfoVO.getContractType() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorContractTypeIsEmpty"));
            isValidateData = false;
        }else{
            if(!EnumUtils.isValidEnum(ZkbContract.ContractType.class,contractInfoVO.getContractType())){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbContractTypeIsNotRange"));
                isValidateData = false;
            }
        }

        if(contractInfoVO.getStartDate() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorStartTimeIsEmpty"));
            isValidateData = false;
        }

        if(contractInfoVO.getEndDate() == null ){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbContractEndTimeEmpty"));
            isValidateData = false;
        }

        if(contractInfoVO.getAgentName() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbContractTenantUIDEmpty"));
            isValidateData = false;
        }else{
            if(!tenantInfoMap.containsKey(contractInfoVO.getAgentName())){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbContractTenantUIDExist"));
                isValidateData = false;
            }
        }

        int spareQuotaTotal = contractInfoVO.getSpareQuotaTotal() == null ? 0 : contractInfoVO.getSpareQuotaTotal();
        int spareQuota = contractInfoVO.getSpareQuota() == null ? 0 : contractInfoVO.getSpareQuota();
        int spareQuotaUse = contractInfoVO.getSpareQuotaUse() == null ? 0 : contractInfoVO.getSpareQuotaUse();
        if(spareQuotaTotal < (spareQuota + spareQuotaUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "备件总额小于备件剩余名额、备件已使用名额之和");
            isValidateData = false;
        }

        int probeQuotaTotal = contractInfoVO.getProbeQuotaTotal() == null ? 0 : contractInfoVO.getProbeQuotaTotal();
        int probeQuota = contractInfoVO.getProbeQuota() == null ? 0 : contractInfoVO.getProbeQuota();
        int probeQuotaUse = contractInfoVO.getProbeQuotaUse() == null ? 0 : contractInfoVO.getProbeQuotaUse();
        if(probeQuotaTotal < (probeQuota  + probeQuotaUse )){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "探头任意总额小于任意剩余名额、任意已使用名额之和");
            isValidateData = false;
        }

        int probeQuotaNormalTotal = contractInfoVO.getProbeQuotaNormalTotal() == null ? 0 : contractInfoVO.getProbeQuotaNormalTotal();
        int probeQuotaNormal = contractInfoVO.getProbeQuotaNormal() == null ? 0 : contractInfoVO.getProbeQuotaNormal();
        int probeQuotaNormalUse = contractInfoVO.getProbeQuotaNormalUse() == null ? 0 : contractInfoVO.getProbeQuotaNormalUse();
        if(probeQuotaNormalTotal < (probeQuotaNormal  + probeQuotaNormalUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "探头普通总额小于普通剩余名额、普通已使用名额之和");
            isValidateData = false;
        }

        int probeQuotaSpecialTotal = contractInfoVO.getProbeQuotaSpecialTotal() == null ? 0 : contractInfoVO.getProbeQuotaSpecialTotal();
        int probeQuotaSpecial = contractInfoVO.getProbeQuotaSpecial() == null ? 0 : contractInfoVO.getProbeQuotaSpecial();
        int probeQuotaSpecialUse = contractInfoVO.getProbeQuotaSpecialUse() == null ? 0 : contractInfoVO.getProbeQuotaSpecialUse();
        if(probeQuotaSpecialTotal < (probeQuotaSpecial + probeQuotaSpecialUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "探头特殊总额小于特殊剩余名额、特殊已使用名额之和");
            isValidateData = false;
        }

        int probeQuotaHighTotal = contractInfoVO.getProbeQuotaHighTotal() == null ? 0 : contractInfoVO.getProbeQuotaHighTotal();
        int probeQuotaHigh = contractInfoVO.getProbeQuotaHigh() == null ? 0 : contractInfoVO.getProbeQuotaHigh();
        int probeQuotaHighUse = contractInfoVO.getProbeQuotaHighUse() == null ? 0 : contractInfoVO.getProbeQuotaHighUse();
        if(probeQuotaHighTotal < (probeQuotaHigh  + probeQuotaHighUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "探头高值总额小于高值剩余名额、高值已使用名额之和");
            isValidateData = false;
        }

        int geArtificialSupportTimesTotal = contractInfoVO.getGeArtificialSupportTimesTotal() == null ? 0 : contractInfoVO.getGeArtificialSupportTimesTotal();
        int geArtificialSupportTimes = contractInfoVO.getGeArtificialSupportTimes() == null ? 0 : contractInfoVO.getGeArtificialSupportTimes();
        int geArtificialSupportTimesUse = contractInfoVO.getGeArtificialSupportTimesUse() == null ? 0 : contractInfoVO.getGeArtificialSupportTimesUse();
        if(geArtificialSupportTimesTotal < (geArtificialSupportTimes + geArtificialSupportTimesUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "人工支持总次数小于人工支持剩余次数、人工支持已使用次数之和");
            isValidateData = false;
        }

        int spareArtificailTotal = contractInfoVO.getSpareArtificailTotal() == null ? 0 : contractInfoVO.getSpareArtificailTotal();
        int spareArtificail = contractInfoVO.getSpareArtificail() == null ? 0 : contractInfoVO.getSpareArtificail();
        int spareArtificailUse = contractInfoVO.getSpareArtificailUse() == null ? 0 : contractInfoVO.getSpareArtificailUse();
        if(spareArtificailTotal < (spareArtificail + spareArtificailUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "备件/人工/保养总额小于备件/人工/保养剩余名额、备件/人工/保养已使用名额之和");
            isValidateData = false;
        }

        for (Map<String, Object> tempContractMap: importContractMapList) {
            ZkbContractVO tempZkbContractVO = (ZkbContractVO)tempContractMap.get("contractInfoVO");
            Integer tempHashCode = Integer.valueOf(tempContractMap.get("hashCode").toString());
            if(hashCode.equals(tempHashCode) && contractInfoVO.getLineNum() != tempZkbContractVO.getLineNum()){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorDuplicatedData"));
                isValidateData = false;
                break;
            }
        }

        return isValidateData;
    }
    //endregion

    //region 合同课程
    /**
     * @author : 503189362
     * @description : 从excel获取合同课程的信息
     * 获取的数据按照Map("合同号","List<单条合同课程信息>")格式返回
     * @date : 2020/5/11 16:39
     */
    public List<Map<String, Object>> getContractCourseMapFromTemplate(ExcelDocument doc) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT_COURSE).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT_COURSE, EXCEL_CONTRACT_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbContractCourseVO contractCourseVO = new ZkbContractCourseVO();

            contractCourseVO.setLineNum(i);
            ZkbContractCourseRow contractCourseRow = new ZkbContractCourseRow(contractCourseVO, rowMap);
            contractCourseRow.setFieldStringValue("合同号", "setContractNo");
            contractCourseRow.setFieldStringValue("培训合同号", "setCourseContractNo");
            contractCourseRow.setFieldStringValue("课程ID","setCid");
            contractCourseRow.setFieldStringValue("课程名称","setCname");
            contractCourseRow.setFieldDoubleValue("课程总额", "setMainCourseCountTotal");
            contractCourseRow.setFieldDoubleValue("课程剩余名额", "setMainCourseCount");
            contractCourseRow.setFieldDoubleValue("课程已使用名额", "setMainCourseCountUse");

            map.put("contractCourseVO", contractCourseVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(contractCourseVO.getId()) ? ImportStatus.New.name() : ImportStatus.Exist.name());
            map.put("hashCode", contractCourseVO.hashCode());
            result.add(map);
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractCourseMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    /**
     * @author : 503189362
     * @description : 主要获取合同课程的List
     *
     * @date : 2020/5/11 16:37
     */
    @Transactional
    public List<ZkbContractCourse> importCourseData(List<Map<String, Object>>impMap){
        List<ZkbContractCourse> list = new ArrayList<>();

        //获取课程信息
        Map<String, ZkbCourse> courseListMap = new HashMap<>();
        List<ZkbCourse> zkbCourses = courseRepository.find();
        for(ZkbCourse course : zkbCourses){
            courseListMap.put(course.getCid()+"|"+course.getCname(),course);
        }

        for(int i = 0; i < impMap.size();i++){
            Map<String, Object> objectMap = impMap.get(i);
            ZkbContractCourseVO contractCourseVO =  (ZkbContractCourseVO)objectMap.get("contractCourseVO");

            if(contractCourseVO != null){
                if(checkCourseData(objectMap,courseListMap,impMap)){
                    importOneContractCourse(contractCourseVO);
                }
            }
        }
        return list;
    }

    public boolean checkCourseData(Map<String, Object> item, Map<String, ZkbCourse> courseListMap, List<Map<String, Object>> importContractCourseMapList){
        boolean isValidateData = true;
        ZkbContractCourseVO contractCourseVO =  (ZkbContractCourseVO)item.get("contractCourseVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());
        ZkbContractCourseRow.clearErrMessage(item);
        if(contractCourseVO != null){
            if(StringUtils.isEmpty(contractCourseVO.getCid())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseIdIsEmpty"));
            }
            if(StringUtils.isEmpty(contractCourseVO.getCname())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseNameIsEmpty"));
            }
            if(StringUtils.isEmpty(contractCourseVO.getContractNo())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorContractNoIsEmpty"));
            }else{
                ZkbContract zkbContract = queryContractByNo(contractCourseVO.getContractNo());
                if(zkbContract == null){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorContractIsNotExist"));
                }else{
                    contractCourseVO.setContractId(zkbContract.getId());
                }
            }

            ZkbCourse zkbCourse = courseListMap.get(contractCourseVO.getCid()+"|"+contractCourseVO.getCname());
            if(zkbCourse == null){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseIsNotExist"));
            }else{
                contractCourseVO.setZkbCourse(zkbCourse);
                contractCourseVO.setCourseId(zkbCourse.getId());
            }

            if(!StringUtils.isEmpty(contractCourseVO.getContractId()) && !StringUtils.isEmpty(contractCourseVO.getCourseId())){
                ZkbContractCourse zkbContractCourse = queryContractCourseByContractIdAndCourseId(contractCourseVO.getContractId(),contractCourseVO.getCourseId());
                if(zkbContractCourse != null){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorContractCourseIsExist"));
                }
            }

            double courseCountTotal = contractCourseVO.getMainCourseCountTotal() == null ? 0 : contractCourseVO.getMainCourseCountTotal();
            double courseCount = contractCourseVO.getMainCourseCount() == null ? 0 : contractCourseVO.getMainCourseCount();
            double courseCountUse = contractCourseVO.getMainCourseCountUse() == null ? 0 : contractCourseVO.getMainCourseCountUse();
            if(courseCountTotal < (courseCount + courseCountUse)){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCourseRow.addErrMessage(item, "课程总额小于课程剩余名额、课程已使用名额之和");
            }

            for (Map<String, Object> tempContractMap: importContractCourseMapList) {
                ZkbContractCourseVO tempZkbContractCourseVO = (ZkbContractCourseVO)tempContractMap.get("contractCourseVO");
                Integer tempHashCode = Integer.valueOf(tempContractMap.get("hashCode").toString());
                if(hashCode.equals(tempHashCode) && contractCourseVO.getLineNum() != tempZkbContractCourseVO.getLineNum()){
                    item.put("status", ImportStatus.Failure);
                    ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorDuplicatedData"));
                    isValidateData = false;
                    break;
                }
            }
        }

        return isValidateData;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importOneContractCourse(ZkbContractCourseVO contractCourseVO){
        ZkbContractCourse zkbContractCourse = new ZkbContractCourse();
        zkbContractCourse.setCourseContractNo(contractCourseVO.getCourseContractNo());
        zkbContractCourse.setMainCourseCount(contractCourseVO.getMainCourseCount());
        zkbContractCourse.setCourseId(contractCourseVO.getZkbCourse().getId());
        zkbContractCourse.setContractId(contractCourseVO.getContractId());
        zkbContractCourse.setMainCourseCountTotal(contractCourseVO.getMainCourseCountTotal());
        zkbContractCourse.setMainCourseCountUse(contractCourseVO.getMainCourseCountUse());
        zkbContractCourse.setMainCourseCountApply(contractCourseVO.getMainCourseCountTotal() - contractCourseVO.getMainCourseCountUse() - contractCourseVO.getMainCourseCount());
        zkbContractCourseRepository.save(zkbContractCourse);
    }
    //endregion

    //region 合同设备
    public List<Map<String, Object>> getContractAssetMapFromTemplate(ExcelDocument doc) throws Exception{
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT_Asset).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT_Asset, EXCEL_CONTRACT_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbContractAssetVO zkbContractAssetVO = new ZkbContractAssetVO();
            zkbContractAssetVO.setLineNum(i);

            ZkbContractAssetRow zkbContractAssetRow = new ZkbContractAssetRow(zkbContractAssetVO, rowMap);
            zkbContractAssetRow.setFieldStringValue("合同号", "setContractNo");
            zkbContractAssetRow.setFieldStringValue("设备二维码","setQrCode");
            zkbContractAssetRow.setFieldStringValue("是否删除","setFlag");
            zkbContractAssetRow.setFieldStringValue("高低端机","setAssetType");
            zkbContractAssetRow.setFieldStringValue("Sub Region","setSubRegion");
            zkbContractAssetRow.setFieldStringValue("Modality","setModality");
            zkbContractAssetRow.setFieldStringValue("责任工程师","setResponsiblEngineer");
            zkbContractAssetRow.setFieldStringValue("KA","setKa");
            zkbContractAssetRow.setFieldStringValue("维修类型","setRepairForm");

            map.put("zkbContractAssetVO", zkbContractAssetVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(zkbContractAssetVO.getId()) ? ImportStatus.New.name() : ImportStatus.Exist.name());
            map.put("hashCode", zkbContractAssetVO.hashCode());
            result.add(map);
        }
        return result;
    }

    public List<ZkbContractCourse> importAssetData(List<Map<String, Object>>impAssetMap,UserAccount currentUser){
        for (Map<String, Object> item : impAssetMap) {
            ZkbContractAssetVO zkbContractAssetVO = (ZkbContractAssetVO)item.get("zkbContractAssetVO");
            if(zkbContractAssetVO != null && checkAssetData(item,impAssetMap)) {
                importOneContractAsset(zkbContractAssetVO,currentUser);
            } 
        }
        return null;
    }

    public boolean checkAssetData(Map<String, Object> item, List<Map<String, Object>> importContractCourseMapList){
        boolean isValidateData = true;
        ZkbContractAssetVO zkbContractAssetVO = (ZkbContractAssetVO)item.get("zkbContractAssetVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ZkbContractAssetRow.clearErrMessage(item);
        if(zkbContractAssetVO.getContractNo() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorContractNumberIsEmpty"));
            isValidateData = false;
        }else {
            ZkbContract zkbContract =  queryContractByNo(zkbContractAssetVO.getContractNo());
            if(zkbContract == null){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorContractIsNotExist"));
            }else{
                zkbContractAssetVO.setContractId(zkbContract.getId());
            }
        }

        if(zkbContractAssetVO.getQrCode() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbQrCodeIsEmpty"));
            isValidateData = false;
        }else{
            AssetInfo assetInfo = queryAssetInfoByQrCode(zkbContractAssetVO.getQrCode());
            if(assetInfo ==null){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbQrCodeIsNotExist"));
            }else{
                zkbContractAssetVO.setAssetId(assetInfo.getUid());
            }
        }

        if(!StringUtils.isEmpty(zkbContractAssetVO.getContractNo()) && !StringUtils.isEmpty(zkbContractAssetVO.getQrCode())){
            ZkbContractAsset zkbContractAsset = queryContractAssetByContractIdAndAssetId(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getAssetId());
            if("Y".equals(zkbContractAssetVO.getFlag())){
                if(zkbContractAsset ==null){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbContractAssetIsNotExist"));
                }else{
                    zkbContractAssetVO.setZkbContractAssetId(zkbContractAsset.getId());
                }
            }else{
                if(zkbContractAsset != null){
                    //zkbContractAssetVO.setFlag("N");
                    //isValidateData = false;
                    //item.put("status", ImportStatus.Failure);
                    //ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbContractAssetIsExist"));
                }
            }
        }

        for (Map<String, Object> tempContractAssetMap: importContractCourseMapList) {
            ZkbContractAssetVO tempZkbContractAssetVO = (ZkbContractAssetVO)tempContractAssetMap.get("zkbContractAssetVO");
            Integer tempHashCode = Integer.valueOf(tempContractAssetMap.get("hashCode").toString());
            if(hashCode.equals(tempHashCode) && zkbContractAssetVO.getLineNum() != tempZkbContractAssetVO.getLineNum()){
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorDuplicatedData"));
                isValidateData = false;
                break;
            }
        }

        return isValidateData;
    }

    @Transactional(rollbackFor = Exception.class)
    public void  importOneContractAsset(ZkbContractAssetVO zkbContractAssetVO,UserAccount currentUser){
        if(StringUtils.isEmpty(zkbContractAssetVO.getFlag())) {
            ZkbContractAsset zkbContractAsset = queryContractAssetByContractIdAndAssetId(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getAssetId());
            if(zkbContractAsset != null){
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getAssetType())){
                    zkbContractAsset.setAssetType(getHighLowAssetFromCh(zkbContractAssetVO.getAssetType()));
                }
                zkbContractAssetRepository.save(zkbContractAsset);
                zkbContractLogService.addLog(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getContractNo(),"assetEdit","",zkbContractAssetVO.getQrCode(),"导入设备编辑",currentUser);

                if(StringUtils.isNotEmpty(zkbContractAssetVO.getRepairForm())){
                    Integer repairForm = getRepairFormFromCh(zkbContractAssetVO.getRepairForm());
                    AssetInfo assetInfo = assetInfoRepository.findByUid(zkbContractAsset.getAssetId());
                    if(assetInfo != null && repairForm != null){
                        if(!repairForm.equals(assetInfo.getRepairForm())){
                            assetInfo.setRepairForm(repairForm);
                            assetInfoRepository.save(assetInfo);
                        }
                    }
                }
            }else{
                ZkbContractAsset newZkbContractAsset = new ZkbContractAsset();
                newZkbContractAsset.setContractId(zkbContractAssetVO.getContractId());
                newZkbContractAsset.setAssetId(zkbContractAssetVO.getAssetId());
                newZkbContractAsset.setHighLowMachine(zkbContractAssetVO.getHighLowMachine());
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getAssetType())){
                    newZkbContractAsset.setAssetType(getHighLowAssetFromCh(zkbContractAssetVO.getAssetType()));
                }
                newZkbContractAsset.setSubRegion(zkbContractAssetVO.getSubRegion());
                newZkbContractAsset.setModality(zkbContractAssetVO.getModality());
                newZkbContractAsset.setResponsiblEngineer(zkbContractAssetVO.getResponsiblEngineer());
                newZkbContractAsset.setKa(zkbContractAssetVO.getKa());
                zkbContractAssetRepository.save(newZkbContractAsset);
                zkbContractLogService.addLog(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getContractNo(),"assetAdd","",zkbContractAssetVO.getQrCode(),"导入新增",currentUser);
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getRepairForm())){
                    Integer repairForm = getRepairFormFromCh(zkbContractAssetVO.getRepairForm());
                    AssetInfo assetInfo = assetInfoRepository.findByUid(zkbContractAsset.getAssetId());
                    if(assetInfo != null && repairForm != null){
                        if(!repairForm.equals(assetInfo.getRepairForm())){
                            assetInfo.setRepairForm(repairForm);
                            assetInfoRepository.save(assetInfo);
                        }
                    }
                }
            }
        }else if("Y".equals(zkbContractAssetVO.getFlag())){
            zkbContractAssetRepository.delete(zkbContractAssetVO.getZkbContractAssetId());
            zkbContractLogService.addLog(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getContractNo(),"assetDelete","",zkbContractAssetVO.getQrCode(),"导入删除",currentUser);
        }
    }
    public String getHighLowAssetFromCh(String va){
        switch (va){
            case "超声高端机":
                return "highMachine";
            case "超声低端机":
                return "lowMachine";
            case "N/A":
                return "empty";
            default:
                return "";
        }
    }

    public Integer getRepairFormFromCh(String va){
        switch (va){
            case "内修":
                return 1;
            case "外修":
                return 2;
            case "内外修可选":
                return 3;
            default:
                return null;
        }
    }
    //endregion



    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importContractMapList) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: importContractMapList) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (ImportStatus.CheckFail.name().equals(importStatus) || ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }

    public List<Map<String, Object>> filterCourseErrorRecords(List<Map<String, Object>> impCourseMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: impCourseMap) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (ImportStatus.CheckFail.name().equals(importStatus) || ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }

        }
        return result;
    }



    public Boolean exportFailData(ExcelDocument doc, List<Map<String, Object>> errorContractList) {

        int keyRowExport = EXCEL_EXPORT_CONTRACT_HEADER_ROW;
        int dataRowExport = EXCEL_EXPORT_CONTRACT_DATA_ROW;
        Boolean hasFailData = false;

        //doc.clearRowsData(SHEET_SERVICE_CONTRACT, dataRowExport, keyRowExport + errorContractList.size());
        for (Map<String, Object> item : errorContractList) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_SERVICE_CONTRACT, keyRowExport, dataRowExport++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }

        return hasFailData;
    }


    public boolean checkDocVersion(ExcelDocument doc){
        return true;
    }

}

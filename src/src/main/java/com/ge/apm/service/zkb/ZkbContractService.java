package com.ge.apm.service.zkb;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;

import java.util.*;

/**
 * @author : 503189362
 * @date : 2020/4/16
 * @description :
 */
@Service
public class ZkbContractService {


    private static final String SHEET_ZKB_CONTRACT = "Contract";
    private static final String SHEET_ZKB_CONTRACT_COURSE = "Contract_Course";
    private static final String SHEET_ZKB_CONTRACT_ASSET = "Contract_Asset";


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
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota"), zkbContract.getSpareQuota());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_probeQuota"), zkbContract.getProbeQuota());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes"), zkbContract.getGeArtificialSupportTimes());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail"), zkbContract.getSpareArtificail());
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

    public String calcContractState(Date startDate,Date endDate){
        if(whenEQ(startDate,new Date())){
            return "未生效";
        }else if(!whenEQ(new Date(),endDate)){
            return "有效";
        }else{
            return "已过期";
        }
    }

    /**
     * 比较两日期是否大小
     * @param date      日期
     * @param otherDate 另一个日期
     * @return 比较两日期大小。如果date>=otherDate则返回true,否则false
     */
    public static Boolean whenEQ(Date date, Date otherDate) {
        Boolean flag = true;
        if (date != null && otherDate != null) {
            long time = date.getTime() - otherDate.getTime();
            if (time < 0) {
                flag = false;
            }
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
            zkbContractCourseDataMap.put("课程额度", zkbContractCourse.getCourseCount());

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
            zkbContractAssetDataMap.put("维修类型", WebUtil.getMessage("assetGroup"+"-"+zkbContractAsset.getAssetInfo().getAssetGroup()));
            if(zkbContractAsset.getAssetInfo() != null && zkbContractAsset.getAssetInfo().getBrand() != null){
                zkbContractAssetDataMap.put("品牌", zkbContractAsset.getAssetInfo().getBrand().getName());
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
    public boolean checkContractNo(ZkbContract zkbContract,String actionName){
        List<ZkbContract> contractList = contractRepository.findByTenantUIDAndContractNo(zkbContract.getTenantUID(),zkbContract.getContractNo());
        if(contractList.size() ==0){
            return true;
        }else if (contractList.size() == 1 && "Edit".equals(actionName)){
            ZkbContract exist = contractList.get(0);
            if(exist.getId().equals(zkbContract.getId())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * 保存合同
     */
    public ZkbContract saveContract(ZkbContract zkbContract,String actionName,String reason,UserAccount currentUser){
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

            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetAdd","",a.getQrCode(),"",currentUser);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(ZkbContractAsset zkbContractAsset,ZkbContract zkbContract,UserAccount currentUser,String reason) {
        zkbContractAssetRepository.delete(zkbContractAsset);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetDelete","",zkbContractAsset.getAssetInfo().getQrCode(),reason,currentUser);
    }

}

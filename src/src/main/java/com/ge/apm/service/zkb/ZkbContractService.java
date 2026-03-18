package com.ge.apm.service.zkb;

import com.ge.healthcare.cn.common.utils.date.DateUnit;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
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
    public static final String SHEET_ZKB_CONTRACT_CAT = "相关高值探头";


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

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;

    @Autowired
    private ZkbPartOrderCatRepository zkbPartOrderCatRepository;

    @Autowired
    private ZkbPartOrderRepository zkbPartOrderRepository;

    @Autowired
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    @Autowired
    private ZkbContractQuotaLogService zkbContractQuotaLogService;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

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
            if(StringUtils.isNotEmpty(zkbContract.getContractAmount())){
                zkbContractDataMap.put("合同金额", WebUtil.getFieldValueMessage("zkbcontractAmount", zkbContract.getContractAmount()));
            }
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_startDate"), DateFormatUtils.format(zkbContract.getStartDate(),"yyyy-MM-dd"));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_endDate"), DateFormatUtils.format(zkbContract.getEndDate(),"yyyy-MM-dd"));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_contractState"), zkbContract.getContractState());
            if(StringUtils.isNotEmpty(zkbContract.getQuotaDeductType())){
                zkbContractDataMap.put("名额扣除类型", WebUtil.getFieldValueMessage("zkbContractQuotaDeductType", zkbContract.getQuotaDeductType()));
            }
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_ibNumber"), zkbContract.getIbNumber());
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota"), isEmptyInteger(zkbContract.getSpareQuota()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota_total"), isEmptyInteger(zkbContract.getSpareQuotaTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota_apply"), isEmptyInteger(zkbContract.getSpareQuotaApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareQuota_use"), isEmptyInteger(zkbContract.getSpareQuotaUse()));

            zkbContractDataMap.put("探头任意总额", isEmptyInteger(zkbContract.getProbeQuotaTotal()));
            zkbContractDataMap.put("探头任意剩余名额", isEmptyInteger(zkbContract.getProbeQuota()));
            zkbContractDataMap.put("探头任意申请名额", isEmptyInteger(zkbContract.getProbeQuotaApply()));
            zkbContractDataMap.put("探头任意已使用名额", isEmptyInteger(zkbContract.getProbeQuotaUse()));

            zkbContractDataMap.put("探头普通总额", isEmptyInteger(zkbContract.getProbeQuotaNormalTotal()));
            zkbContractDataMap.put("探头普通剩余名额", isEmptyInteger(zkbContract.getProbeQuotaNormal()));
            zkbContractDataMap.put("探头普通申请名额", isEmptyInteger(zkbContract.getProbeQuotaNormalApply()));
            zkbContractDataMap.put("探头普通已使用名额", isEmptyInteger(zkbContract.getProbeQuotaNormalUse()));

            zkbContractDataMap.put("探头特殊总额", isEmptyInteger(zkbContract.getProbeQuotaSpecialTotal()));
            zkbContractDataMap.put("探头特殊剩余名额", isEmptyInteger(zkbContract.getProbeQuotaSpecial()));
            zkbContractDataMap.put("探头特殊申请名额", isEmptyInteger(zkbContract.getProbeQuotaSpecialApply()));
            zkbContractDataMap.put("探头特殊已使用名额", isEmptyInteger(zkbContract.getProbeQuotaSpecialUse()));

            zkbContractDataMap.put("保养总额", isEmptyInteger(zkbContract.getCcsbMaintenanceQuotaTotal()));
            zkbContractDataMap.put("保养剩余名额", isEmptyInteger(zkbContract.getCcsbMaintenanceQuota()));
            zkbContractDataMap.put("保养申请名额", isEmptyInteger(zkbContract.getCcsbMaintenanceQuotaApply()));
            zkbContractDataMap.put("保养已使用名额", isEmptyInteger(zkbContract.getCcsbMaintenanceQuotaUse()));

            zkbContractDataMap.put("备件高值总额", isEmptyInteger(zkbContract.getPartHighQuotaTotal()));
            zkbContractDataMap.put("备件高值剩余名额", isEmptyInteger(zkbContract.getPartHighQuota()));
            zkbContractDataMap.put("备件高值申请中名额", isEmptyInteger(zkbContract.getPartHighQuotaApply()));
            zkbContractDataMap.put("备件高值已使用名额", isEmptyInteger(zkbContract.getPartHighQuotaUse()));

            zkbContractDataMap.put("耗材总额", isEmptyInteger(zkbContract.getConsumableQuotaTotal()));
            zkbContractDataMap.put("耗材剩余名额", isEmptyInteger(zkbContract.getConsumableQuota()));
            zkbContractDataMap.put("耗材申请中名额", isEmptyInteger(zkbContract.getConsumableQuotaApply()));
            zkbContractDataMap.put("耗材已使用名额", isEmptyInteger(zkbContract.getConsumableQuotaUse()));
            zkbContractDataMap.put("耗材增幅", isEmptyInteger(zkbContract.getConsumeGrowth()));

            /*zkbContractDataMap.put("探头高值总额", isEmptyInteger(zkbContract.getProbeQuotaHighTotal()));
            zkbContractDataMap.put("探头高值剩余名额", isEmptyInteger(zkbContract.getProbeQuotaHigh()));
            zkbContractDataMap.put("探头高值申请名额", isEmptyInteger(zkbContract.getProbeQuotaHighApply()));
            zkbContractDataMap.put("探头高值已使用名额", isEmptyInteger(zkbContract.getProbeQuotaHighUse()));*/

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes"), isEmptyInteger(zkbContract.getGeArtificialSupportTimes()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail"), isEmptyInteger(zkbContract.getSpareArtificail()));

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes_total"), isEmptyInteger(zkbContract.getGeArtificialSupportTimesTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes_apply"), isEmptyInteger(zkbContract.getGeArtificialSupportTimesApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_geArtificialSupportTimes_use"), isEmptyInteger(zkbContract.getGeArtificialSupportTimesUse()));

            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail_total"), isEmptyInteger(zkbContract.getSpareArtificailTotal()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail_apply"), isEmptyInteger(zkbContract.getSpareArtificailApply()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_spareArtificail_use"), isEmptyInteger(zkbContract.getSpareArtificailUse()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_remark"), zkbContract.getRemark());

            zkbContract.setContractState(calcContractState(zkbContract.getStartDate(),zkbContract.getEndDate()));
            zkbContractDataMap.put(WebUtil.getMessage("zkbContract_contractState"),zkbContract.getContractState());
            if(StringUtils.isNotEmpty(zkbContract.getFiveNineNineFlag())){
                if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag())){
                    zkbContractDataMap.put("是否ZKB 599方案","是");
                }else{
                    zkbContractDataMap.put("是否ZKB 599方案","否");
                }
            }
            if(StringUtils.isNotEmpty(zkbContract.getFirstYearFlag())){
                if(ZkbConstantUtil.T.equals(zkbContract.getFirstYearFlag())){
                    zkbContractDataMap.put("599合同年份","599合同第一年");
                }else if(ZkbConstantUtil.F.equals(zkbContract.getFirstYearFlag())){
                    zkbContractDataMap.put("599合同年份","599合同第2和3年");
                }
            }
            zkbContractDataMap.put("公司编码", zkbContract.getCompanyCode());
            if(StringUtils.isNotEmpty(zkbContract.getCanApplyToolbox())){
                if(ZkbConstantUtil.NORMAL_Y.equals(zkbContract.getCanApplyToolbox())){
                    zkbContractDataMap.put("是否可以申请工具箱","是");
                }else if(ZkbConstantUtil.NORMAL_N.equals(zkbContract.getCanApplyToolbox())){
                    zkbContractDataMap.put("是否可以申请工具箱","否");
                }
            }
            zkbContractDataMap.put("关联合同号", zkbContract.getLinkContractNo());
            if(StringUtils.isNotEmpty(zkbContract.getAssetQuotaType())){
                if(ZkbConstantUtil.T.equals(zkbContract.getAssetQuotaType())){
                    zkbContractDataMap.put("2022年起探头高低端名额分类", "是");
                }else  if(ZkbConstantUtil.F.equals(zkbContract.getAssetQuotaType())){
                    zkbContractDataMap.put("2022年起探头高低端名额分类", "否");
                }
            }

            zkbContractDataList.add(zkbContractDataMap);
            //处理合同课程数据
            this.putZkbContractCourseData(zkbContract, zkbContractDataMap);

            //处理合同设备信息
            this.putZkbContractAssetData(zkbContract,zkbContractDataMap);
            //
            this.putZkbContractCatData(zkbContract,zkbContractDataMap);
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
            zkbContractAssetDataMap.put("设备分类", WebUtil.getFieldValueMessage("assetGroup", zkbContractAsset.getAssetInfo().getAssetGroup()!=null?zkbContractAsset.getAssetInfo().getAssetGroup().toString():""));
            String rfValue = zkbContractAsset.getAssetInfo().getRepairForm() ==null ? "" : String.valueOf(zkbContractAsset.getAssetInfo().getRepairForm());
            zkbContractAssetDataMap.put("维修类型", WebUtil.getFieldValueMessage("repairForm",rfValue));
            if(zkbContractAsset.getAssetInfo() != null){
                zkbContractAssetDataMap.put("品牌", zkbContractAsset.getAssetInfo().getMakerShortName());
            }
            if(StringUtils.isNotEmpty(zkbContractAsset.getAssetType())){
                zkbContractAssetDataMap.put("高低端机", WebUtil.getFieldValueMessage("zkbContractAssetType",zkbContractAsset.getAssetType()));
            }
            if(StringUtils.isNotEmpty(zkbContractAsset.getTrainReasonFlag())){
                zkbContractAssetDataMap.put("是否代理商自身原因未完成培训", WebUtil.getFieldValueMessage("zkbPoAssetTrainReasonFlag",zkbContractAsset.getTrainReasonFlag()));
            }
            if(StringUtils.isNotEmpty(zkbContractAsset.getSupportMaintainFlag())){
                zkbContractAssetDataMap.put("是否由GE FE提供年度1次现场保养", WebUtil.getFieldValueMessage("zkbPoAssetTrainReasonFlag",zkbContractAsset.getSupportMaintainFlag()));
            }
            zkbContractAssetDataMap.put("备注", zkbContractAsset.getAssetInfo().getComments());
            zkbContractAssetDataMap.put("zone", zkbContractAsset.getZone());
            zkbContractAssetDataMap.put("ID", zkbContractAsset.getId());
            zkbContractAssetDataList.add(zkbContractAssetDataMap);
        }

        zkbContractDataMap.put("zkbContractAssetDataList", zkbContractAssetDataList);
    }

    public void putZkbContractCatData(ZkbContract zkbContract, Map<String, Object> zkbContractDataMap) {
        List<ZkbContractCat> zkbContractCourseList = zkbContractCatRepository.findByContractId(zkbContract.getId());

        List<Map<String, Object>> zkbContractCatDataList = new ArrayList<>();
        for (ZkbContractCat c : zkbContractCourseList) {
            Map<String, Object> zkbContractCatDataMap = new HashMap<>(20);
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_region"), zkbContract.getRegion());
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_agentName"), zkbContract.getAgentName());
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_contractNo"), zkbContract.getContractNo());
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_contractType"), zkbContract.getContractType());
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_startDate"), DateFormatUtils.format(zkbContract.getStartDate(),"yyyy-MM-dd"));
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_endDate"), DateFormatUtils.format(zkbContract.getEndDate(),"yyyy-MM-dd"));
            zkbContractCatDataMap.put(WebUtil.getMessage("zkbContract_contractState"), zkbContract.getContractState());
            zkbContractCatDataMap.put("SN编号", c.getSnNo());
            if(c.getEnabled()){
                zkbContractCatDataMap.put("状态", "可用");
            }else{
                zkbContractCatDataMap.put("状态", "不可用");
            }
            zkbContractCatDataList.add(zkbContractCatDataMap);
        }

        zkbContractDataMap.put("zkbContractCatDataList", zkbContractCatDataList);
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbContractData) {
        List<Map<String, Object>> zkbContractCourseData = createZkbContractCourseData(zkbContractData);
        List<Map<String, Object>> zkbContractAssetData = createZkbContractAssetData(zkbContractData);
        List<Map<String, Object>> zkbContractCatData = createZkbContractCatData(zkbContractData);

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

        Integer keyRow_zkbContractCat = 1;
        Integer dataRow_zkbContractCat = 2;
        for (Map<String, Object> item : zkbContractCatData) {
            doc.writeRowData(SHEET_ZKB_CONTRACT_CAT, keyRow_zkbContractCat, dataRow_zkbContractCat++, item);
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

    public List<Map<String, Object>> createZkbContractCatData(List<Map<String, Object>> zkbContractDataList) {
        List<Map<String, Object>> zkbContractAssetDataList = new ArrayList<>();
        for (Map<String, Object> zkbContractDataMap : zkbContractDataList) {
            if(zkbContractDataMap.containsKey("zkbContractCatDataList")){
                zkbContractAssetDataList.addAll((List<Map<String, Object>>)zkbContractDataMap.get("zkbContractCatDataList"));
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

        boolean newContractFlag = false;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())){
            newContractFlag = false;
        }else{
            newContractFlag = true;
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

        int probeQuotaApply = zkbContract.getProbeQuotaApply() == null ? 0 : zkbContract.getProbeQuotaApply();
        int probeQuotaUse = zkbContract.getProbeQuotaUse() == null ? 0 : zkbContract.getProbeQuotaUse();
        if(probeQuotaTotal != (probeQuota + probeQuotaApply + probeQuotaUse)){
            flag = false;
            if(!newContractFlag){
                errorLog += "探头总额不等于剩余名额、申请名额、已使用名额之和,";
            }else{
                errorLog += "探头任意总额不等于任意剩余名额、任意申请名额、任意已使用名额之和,";
            }
        }

        if(newContractFlag){
            int probeQuotaNormal = zkbContract.getProbeQuotaNormal() == null ? 0 : zkbContract.getProbeQuotaNormal();
            int probeQuotaNormalTotal = zkbContract.getProbeQuotaNormalTotal() == null ? 0 : zkbContract.getProbeQuotaNormalTotal();
            int probeQuotaNormalApply = zkbContract.getProbeQuotaNormalApply() == null ? 0 : zkbContract.getProbeQuotaNormalApply();
            int probeQuotaNormalUse = zkbContract.getProbeQuotaNormalUse() == null ? 0 : zkbContract.getProbeQuotaNormalUse();
            if(probeQuotaNormalTotal != (probeQuotaNormal + probeQuotaNormalApply + probeQuotaNormalUse)){
                flag = false;
                errorLog += "探头普通总额不等于普通剩余名额、普通申请名额、普通已使用名额之和,";
            }

            int probeQuotaSpecial = zkbContract.getProbeQuotaSpecial() == null ? 0 : zkbContract.getProbeQuotaSpecial();
            int probeQuotaSpecialTotal = zkbContract.getProbeQuotaSpecialTotal() == null ? 0 : zkbContract.getProbeQuotaSpecialTotal();
            int probeQuotaSpecialApply = zkbContract.getProbeQuotaSpecialApply() == null ? 0 : zkbContract.getProbeQuotaSpecialApply();
            int probeQuotaSpecialUse = zkbContract.getProbeQuotaSpecialUse() == null ? 0 : zkbContract.getProbeQuotaSpecialUse();
            if(probeQuotaSpecialTotal != (probeQuotaSpecial + probeQuotaSpecialApply + probeQuotaSpecialUse)){
                flag = false;
                errorLog += "探头特殊总额不等于特殊剩余名额、特殊申请名额、特殊已使用名额之和,";
            }

            int probeQuotaHigh = zkbContract.getProbeQuotaHigh() == null ? 0 : zkbContract.getProbeQuotaHigh();
            int probeQuotaHighTotal = zkbContract.getProbeQuotaHighTotal() == null ? 0 : zkbContract.getProbeQuotaHighTotal();
            int probeQuotaHighApply = zkbContract.getProbeQuotaHighApply() == null ? 0 : zkbContract.getProbeQuotaHighApply();
            int probeQuotaHighUse = zkbContract.getProbeQuotaHighUse() == null ? 0 : zkbContract.getProbeQuotaHighUse();
            if(probeQuotaHighTotal != (probeQuotaHigh + probeQuotaHighApply + probeQuotaHighUse)){
                flag = false;
                errorLog += "探头高值总额不等于高值剩余名额、高值申请名额、高值已使用名额之和,";
            }
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

        if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
            int ccsbMaintenanceQuotaTotal = zkbContract.getCcsbMaintenanceQuotaTotal() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaTotal();
            int ccsbMaintenanceQuota = zkbContract.getCcsbMaintenanceQuota() == null ? 0 : zkbContract.getCcsbMaintenanceQuota();
            int ccsbMaintenanceQuotaApply = zkbContract.getCcsbMaintenanceQuotaApply() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaApply();
            int ccsbMaintenanceQuotaUse = zkbContract.getCcsbMaintenanceQuotaUse() == null ? 0 : zkbContract.getCcsbMaintenanceQuotaUse();
            if(ccsbMaintenanceQuotaTotal != (ccsbMaintenanceQuota + ccsbMaintenanceQuotaApply + ccsbMaintenanceQuotaUse)){
                flag = false;
                errorLog += "保养总额不等保养剩余名额、保养申请名额、保养已使用名额之和,";
            }
        }

        if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
            int partHighQuotaTotal = zkbContract.getPartHighQuotaTotal() == null ? 0 : zkbContract.getPartHighQuotaTotal();
            int partHighQuota = zkbContract.getPartHighQuota() == null ? 0 : zkbContract.getPartHighQuota();
            int partHighQuotaApply = zkbContract.getPartHighQuotaApply() == null ? 0 : zkbContract.getPartHighQuotaApply();
            int partHighQuotaUse = zkbContract.getPartHighQuotaUse() == null ? 0 : zkbContract.getPartHighQuotaUse();
            if(partHighQuotaTotal != (partHighQuota + partHighQuotaApply + partHighQuotaUse)){
                flag = false;
                errorLog += "备件高值总额不等于备件高值剩余名额、备件高值申请名额、备件高值已使用名额之和,";
            }
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

        if(ZkbContract.ContractType.Biomed.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.CCS.toString().equals(zkbContract.getContractType())){
            if(ZkbContract.QuotaDeductTypeEnum.Null.toString().equals(zkbContract.getQuotaDeductType())){
                flag = false;
                errorLog += "合同扣除类型不能为N/A,";
            }
        }
        if(ZkbContract.YOrNEnum.Y.toString().equals(zkbContract.getFiveNineNineFlag())){
            /*try {
                int n = CopyByInvok.getBetweenYear(zkbContract.getContractStartDate(),zkbContract.getContractEndDate());
                if(n <= 2){
                    flag = false;
                    errorLog += "ZKB 599方案的合同有效期不能小于3年,";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }*/

        }

        if (errorLog.length()>0){
            errorLog = errorLog.substring(0,errorLog.length()-1);
        }
        resut.put("flag",flag);
        resut.put("errorLog",errorLog);
        return resut;
    }

    public String getQuotaString(ZkbContract zkbContract){
        StringBuilder sb = new StringBuilder();
        sb.append(StringAppendQuota("备件名额",nullToZero(zkbContract.getSpareQuotaTotal()),nullToZero(zkbContract.getSpareQuotaApply()),nullToZero(zkbContract.getSpareQuota()),nullToZero(zkbContract.getSpareQuotaUse())));
        sb.append(StringAppendQuota("联合名额",nullToZero(zkbContract.getSpareArtificailTotal()),nullToZero(zkbContract.getSpareArtificailApply()),nullToZero(zkbContract.getSpareArtificail()),nullToZero(zkbContract.getSpareArtificailUse())));
        sb.append(StringAppendQuota("人工名额",nullToZero(zkbContract.getGeArtificialSupportTimesTotal()),nullToZero(zkbContract.getGeArtificialSupportTimesApply()),nullToZero(zkbContract.getGeArtificialSupportTimes()),nullToZero(zkbContract.getGeArtificialSupportTimesUse())));
        sb.append(StringAppendQuota("耗材名额",nullToZero(zkbContract.getConsumableQuotaTotal()),nullToZero(zkbContract.getConsumableQuotaApply()),nullToZero(zkbContract.getConsumableQuota()),nullToZero(zkbContract.getConsumableQuotaUse())));
        if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
            sb.append(StringAppendQuota("探头任意名额",nullToZero(zkbContract.getProbeQuotaTotal()),nullToZero(zkbContract.getProbeQuotaApply()),nullToZero(zkbContract.getProbeQuota()),nullToZero(zkbContract.getProbeQuotaUse())));
            sb.append(StringAppendQuota("探头普通名额",nullToZero(zkbContract.getProbeQuotaNormalTotal()),nullToZero(zkbContract.getProbeQuotaNormalApply()),nullToZero(zkbContract.getProbeQuotaNormal()),nullToZero(zkbContract.getProbeQuotaNormalUse())));
            sb.append(StringAppendQuota("探头特殊名额",nullToZero(zkbContract.getProbeQuotaSpecialTotal()),nullToZero(zkbContract.getProbeQuotaSpecialApply()),nullToZero(zkbContract.getProbeQuotaSpecial()),nullToZero(zkbContract.getProbeQuotaSpecialUse())));
        }else{
            sb.append(StringAppendQuota("探头名额",nullToZero(zkbContract.getProbeQuotaTotal()),nullToZero(zkbContract.getProbeQuotaApply()),nullToZero(zkbContract.getProbeQuota()),nullToZero(zkbContract.getProbeQuotaUse())));
        }
        if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
            sb.append(StringAppendQuota("CCSB保养名额",nullToZero(zkbContract.getCcsbMaintenanceQuotaTotal()),nullToZero(zkbContract.getCcsbMaintenanceQuotaApply()),nullToZero(zkbContract.getCcsbMaintenanceQuota()),nullToZero(zkbContract.getCcsbMaintenanceQuotaUse())));
        }
        if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
            sb.append(StringAppendQuota("备件高值名额",nullToZero(zkbContract.getPartHighQuotaTotal()),nullToZero(zkbContract.getPartHighQuotaApply()),nullToZero(zkbContract.getPartHighQuota()),nullToZero(zkbContract.getPartHighQuotaUse())));
        }
        return sb.toString();
    }

    /**
     * 保存合同
     */
    @Transactional(rollbackFor = Exception.class)
    public ZkbContract saveContract(ZkbContract zkbContract,String actionName,String reason,UserAccount currentUser){
        zkbContract.setLastModifiedDate(new Date());
        zkbContract.setLastModifiedBy(currentUser.getLoginName());
        if("Edit".equals(actionName)){
            ZkbContract oldContract = contractRepository.findById(zkbContract.getId());
            quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),currentUser);
            quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),currentUser);
            quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),currentUser);
            quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Consumable.toString(),currentUser);
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
                quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString(),currentUser);
                quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),currentUser);
                quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString(),currentUser);
            }else{
                quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Probe.toString(),currentUser);
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),currentUser);
            }
            if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),currentUser);
            }
            if(zkbContract.getConsumeGrowthDate() == null){
                zkbContract.setConsumeGrowthDate(zkbContract.getStartDate());
            }
            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"contractUpdate",null,null,reason,currentUser,null,getQuotaString(zkbContract));
            zkbContract = contractRepository.save(zkbContract);
        }else {
            zkbContract.setConsumeGrowthDate(zkbContract.getStartDate());
            zkbContract = contractRepository.save(zkbContract);
            zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Consumable.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
                zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
                zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
                zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }else{
                zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Probe.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }
            if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())){
                zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }

        }
        syncAsset(zkbContract);
        return zkbContract;
    }

    public String StringAppendQuota(String quotaName, int total, int apply, int current, int used){

        String result = "【%s : %s, %s, %s, %s】";
        return String.format(result, quotaName,total,apply,current,used);
        //return result;
    }

    /*
     * 将合同结束日期同步到设备的维保截止日期
     * 先比较两者是否一致，如果一致，则不同步
     */
    public void syncAsset(ZkbContract zkbContract){
        List<ZkbContractAsset> zkbContractAssets = zkbContractAssetRepository.findByContractId(zkbContract.getId());
        for(ZkbContractAsset zkbContractAsset : zkbContractAssets){
            AssetInfo a = zkbContractAsset.getAssetInfo();
            if(a.getWarrantyDate() != null){
                long betweenDay = DateUtil.between(a.getWarrantyDate(), zkbContract.getEndDate(), DateUnit.DAY);
                if(betweenDay != 0){
                    a.setWarrantyDate(zkbContract.getEndDate());
                    assetInfoRepository.save(a);
                }
            }else {
                a.setWarrantyDate(zkbContract.getEndDate());
                assetInfoRepository.save(a);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void quotaChange(ZkbContract zkbContract,ZkbContract oldContract,String orderType,UserAccount currentUser){
        boolean f = false;
        if(ZkbContractQuotaLog.OrderTypeEnum.Spare.toString().equals(orderType)){
            if(nullToZero(oldContract.getSpareQuota()) - nullToZero(zkbContract.getSpareQuota()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getSpareQuotaTotal()) - nullToZero(zkbContract.getSpareQuotaTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getSpareQuotaApply()) - nullToZero(zkbContract.getSpareQuotaApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getSpareQuotaUse()) - nullToZero(zkbContract.getSpareQuotaUse()) != 0){
                f = true;
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString().equals(orderType)){
            if(nullToZero(oldContract.getProbeQuota()) - nullToZero(zkbContract.getProbeQuota()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaTotal()) - nullToZero(zkbContract.getProbeQuotaTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaApply()) - nullToZero(zkbContract.getProbeQuotaApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaUse()) - nullToZero(zkbContract.getProbeQuotaUse()) != 0){
                f = true;
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString().equals(orderType)){
            if(nullToZero(oldContract.getProbeQuotaNormal()) - nullToZero(zkbContract.getProbeQuotaNormal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaNormalTotal()) - nullToZero(zkbContract.getProbeQuotaNormalTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaNormalApply()) - nullToZero(zkbContract.getProbeQuotaNormalApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaNormalUse()) - nullToZero(zkbContract.getProbeQuotaNormalUse()) != 0){
                f = true;
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString().equals(orderType)){
            if(nullToZero(oldContract.getProbeQuotaSpecial()) - nullToZero(zkbContract.getProbeQuotaSpecial()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaSpecialTotal()) - nullToZero(zkbContract.getProbeQuotaSpecialTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaSpecialApply()) - nullToZero(zkbContract.getProbeQuotaSpecialApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaSpecialUse()) - nullToZero(zkbContract.getProbeQuotaSpecialUse()) != 0){
                f = true;
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString().equals(orderType)){
            if(nullToZero(oldContract.getSpareArtificail()) - nullToZero(zkbContract.getSpareArtificail()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getSpareArtificailTotal()) - nullToZero(zkbContract.getSpareArtificailTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getSpareArtificailApply()) - nullToZero(zkbContract.getSpareArtificailApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getSpareArtificailUse()) - nullToZero(zkbContract.getSpareArtificailUse()) != 0){
                f = true;
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString().equals(orderType)){
            if(nullToZero(oldContract.getGeArtificialSupportTimes()) - nullToZero(zkbContract.getGeArtificialSupportTimes()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getGeArtificialSupportTimesTotal()) - nullToZero(zkbContract.getGeArtificialSupportTimesTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getGeArtificialSupportTimesApply()) - nullToZero(zkbContract.getGeArtificialSupportTimesApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getGeArtificialSupportTimesUse()) - nullToZero(zkbContract.getGeArtificialSupportTimesUse()) != 0){
                f = true;
            }
        }else if(ZkbContractQuotaLog.OrderTypeEnum.Probe.toString().equals(orderType)){
            if(nullToZero(oldContract.getProbeQuota()) - nullToZero(zkbContract.getProbeQuota()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaTotal()) - nullToZero(zkbContract.getProbeQuotaTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaApply()) - nullToZero(zkbContract.getProbeQuotaApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getProbeQuotaUse()) - nullToZero(zkbContract.getProbeQuotaUse()) != 0){
                f = true;
            }
        } else if(ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString().equals(orderType)){
            if(nullToZero(oldContract.getCcsbMaintenanceQuota()) - nullToZero(zkbContract.getCcsbMaintenanceQuota()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getCcsbMaintenanceQuotaTotal()) - nullToZero(zkbContract.getCcsbMaintenanceQuotaTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getCcsbMaintenanceQuotaApply()) - nullToZero(zkbContract.getCcsbMaintenanceQuotaApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getCcsbMaintenanceQuotaUse()) - nullToZero(zkbContract.getCcsbMaintenanceQuotaUse()) != 0){
                f = true;
            }
        } else if(ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString().equals(orderType)){
            if(nullToZero(oldContract.getPartHighQuota()) - nullToZero(zkbContract.getPartHighQuota()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getPartHighQuotaTotal()) - nullToZero(zkbContract.getPartHighQuotaTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getPartHighQuotaApply()) - nullToZero(zkbContract.getPartHighQuotaApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getPartHighQuotaUse()) - nullToZero(zkbContract.getPartHighQuotaUse()) != 0){
                f = true;
            }
        } else if(ZkbContractQuotaLog.OrderTypeEnum.Consumable.toString().equals(orderType)){
            if(nullToZero(oldContract.getConsumableQuota()) - nullToZero(zkbContract.getConsumableQuota()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getConsumableQuotaTotal()) - nullToZero(zkbContract.getConsumableQuotaTotal()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getConsumableQuotaApply()) - nullToZero(zkbContract.getConsumableQuotaApply()) != 0){
                f = true;
            }
            if(nullToZero(oldContract.getConsumableQuotaUse()) - nullToZero(zkbContract.getConsumableQuotaUse()) != 0){
                f = true;
            }
        }
        if(f){
            zkbContractQuotaLogService.saveData(zkbContract,null,null,"contractUpdate",orderType,ZkbContract.class.getSimpleName(),currentUser,null);
        }
    }

    public Integer nullToZero(Integer va){
        return va == null ? 0 : va;
    }

    public boolean checkForDelete(ZkbContract zkbContract){
        Integer zkbFeQty = zkbPartOrderRepository.fetchQty(zkbContract.getId());
        Integer geFeQty = zkbFePartOrderRepository.fetchQty(zkbContract.getId());
        if(zkbFeQty != null && zkbFeQty > 0 ){
            return false;
        }
        if(geFeQty != null && geFeQty > 0 ){
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(ZkbContract zkbContract,String reason,UserAccount currentUser){
        String courseData = "";
        String assetData = "";
        String catData = "";
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

        List<ZkbContractCat> zkbContractCats = zkbContractCatRepository.findByContractId(zkbContract.getId());
        if(zkbContractCats != null && zkbContractCats.size() > 0){
            for(ZkbContractCat c : zkbContractCats){
                catData += c.getSnNo()+",";
            }
            if(catData.length() >0){
                catData = catData.substring(0,catData.length()-1);
            }
        }
        contractRepository.delete(zkbContract);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"contractDelete",courseData,assetData,reason,currentUser,catData,getQuotaString(zkbContract));

    }


    /**
     * 保存合同的课程，并且将合同课程加入列表中
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ZkbContractCourse> saveContractCourse(ZkbContractCourse zkbContractCourse,List<ZkbContractCourse> contractCourseList,ZkbContract zkbContract,String actionDetailName,String reason,UserAccount currentUser){
        contractCourseList.forEach((item) -> {
            if(item.getId().equals(zkbContractCourse.getId())){
                item=zkbContractCourse;
            }
        });
        if(StringUtils.isBlank(zkbContractCourse.getId()))
        {
            contractCourseList.add(zkbContractCourse);
        }
        contractCourseRepository.save(zkbContractCourse);
        if("Edit".equals(actionDetailName)){
            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"courseUpdate",zkbContractCourse.getZkbCourse().getCid(),"",reason,currentUser,null,getQuotaString(zkbContract));
        }
        return contractCourseList;
    }

    /**
     * 删除合同课程，并把列表中课程删除
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ZkbContractCourse> delContractCourse(ZkbContractCourse zkbContractCourse,List<ZkbContractCourse> contractCourseList,ZkbContract zkbContract,UserAccount currentUser,String reason){
        contractCourseRepository.delete(zkbContractCourse);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"courseDelete",zkbContractCourse.getZkbCourse().getCid(),"",reason,currentUser,null,getQuotaString(zkbContract));

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

    public List<ZkbContractCourse> queryCourseByContractId(String contractId){
        List<ZkbContractCourse> list = contractCourseRepository.findByContractId(contractId);
        return list;
    }
    public List<ZkbContractCat> queryCatByContractId(String contractId){
        List<ZkbContractCat> list = zkbContractCatRepository.findByContractId(contractId);
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
            zkbContractAsset.setCreatedDate(new Date());
            zkbContractAsset.setCreatedBy(currentUser.getName());
            zkbContractAsset.setLastModifiedDate(new Date());
            zkbContractAsset.setLastModifiedBy(currentUser.getName());
            zkbContractAssetRepository.save(zkbContractAsset);

            zkbContract.setLastModifiedDate(new Date());
            zkbContract.setLastModifiedBy(currentUser.getLoginName());
            contractRepository.save(zkbContract);

            if(a.getWarrantyDate() != null){
                long betweenDay = DateUtil.between(a.getWarrantyDate(), zkbContract.getEndDate(), DateUnit.DAY);
                if(betweenDay != 0){
                    a.setWarrantyDate(zkbContract.getEndDate());
                    assetInfoRepository.save(a);
                }
            }else{
                a.setWarrantyDate(zkbContract.getEndDate());
                assetInfoRepository.save(a);
            }

            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetAdd","",a.getQrCode(),"",currentUser,null,getQuotaString(zkbContract));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAsset(ZkbContract zkbContract,ZkbContractAsset zkbContractAsset,String reason,UserAccount currentUser){
        zkbContractAsset.setLastModifiedBy(currentUser.getName());
        zkbContractAsset.setLastModifiedDate(new Date());
        zkbContractAssetRepository.save(zkbContractAsset);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetEdit","",zkbContractAsset.getAssetInfo().getQrCode(),reason,currentUser,null,getQuotaString(zkbContract));

        AssetInfo assetInfo = assetInfoRepository.findByUid(zkbContractAsset.getAssetId());
        assetInfo.setRepairForm(zkbContractAsset.getAssetInfo().getRepairForm());
        if(assetInfo.getWarrantyDate() != null){
            long betweenDay = DateUtil.between(assetInfo.getWarrantyDate(), zkbContract.getEndDate(), DateUnit.DAY);
            if(betweenDay != 0){
                assetInfo.setWarrantyDate(zkbContract.getEndDate());
            }
        }else{
            assetInfo.setWarrantyDate(zkbContract.getEndDate());
        }
        assetInfoRepository.save(assetInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAsset(ZkbContractAsset zkbContractAsset,ZkbContract zkbContract,UserAccount currentUser,String reason) {
        zkbContractAssetRepository.delete(zkbContractAsset);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"assetDelete","",zkbContractAsset.getAssetInfo().getQrCode(),reason,currentUser,null,getQuotaString(zkbContract));
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveCat(ZkbContractCat zkbContractCat,String actionCatName,String reason,UserAccount currentUser,ZkbContract zkbContract){
        if("Create".equals(actionCatName)){
            zkbContractCat.setCreatedDate(new Date());
            zkbContractCat.setCreatedBy(currentUser.getName());
        }else{
            zkbContractCat.setLastModifiedBy(currentUser.getName());
            zkbContractCat.setLastModifiedDate(new Date());
        }

        zkbContractCatRepository.save(zkbContractCat);

        if("Edit".equals(actionCatName)){
            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"catUpdate","","",reason,currentUser,zkbContractCat.getSnNo(),getQuotaString(zkbContract));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCat(ZkbContractCat zkbContractCat,ZkbContract zkbContract,UserAccount currentUser,String reason){
        zkbContractCatRepository.delete(zkbContractCat);
        zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"catDelete","","",reason,currentUser,zkbContractCat.getSnNo(),getQuotaString(zkbContract));
    }


    public void resetZeroScore(ZkbContract zkbContract, UserAccount userAccount){
        zkbIntegralService.resetZeroScore(zkbContract.getTenantUID(), userAccount, ZkbIntegralRecord.RecordType.ContractEdit.name(), "合同编辑积分清零");
    }
}

package com.ge.apm.service.zkb;

import com.ge.healthcare.cn.common.utils.date.DateUnit;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import javax.annotation.Resource;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

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
    public static final String SHEET_SERVICE_CONTRACT_Cat = "相关高值探头";
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

    @Autowired
    private ZkbContractCatRepository zkbContractCatRepository;

    @Autowired
    private ZkbContractService zkbContractService;

    @Autowired
    private ZkbContractQuotaLogService zkbContractQuotaLogService;

    @Autowired
    private ZkbIntegralService zkbIntegralService;

    @Resource(name = "zkbZoneRepository")
    private ZkbZoneRepository zkbZoneRepository;

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
            contractRow.setFieldStringValue("合同金额", "setContractAmount");
            contractRow.setFieldDateValue("合同开始日期", "setStartDate");
            contractRow.setFieldDateValue("合同结束日期", "setEndDate");
            contractRow.setFieldStringValue("名额扣除类型", "setQuotaDeductType");
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

            contractRow.setFieldNumValue("保养总额", "setCcsbMaintenanceQuotaTotal");
            contractRow.setFieldNumValue("保养剩余名额", "setCcsbMaintenanceQuota");
            contractRow.setFieldNumValue("保养已使用名额", "setCcsbMaintenanceQuotaUse");

            contractRow.setFieldNumValue("备件高值总额", "setPartHighQuotaTotal");
            contractRow.setFieldNumValue("备件高值剩余名额", "setPartHighQuota");
            contractRow.setFieldNumValue("备件高值已使用名额", "setPartHighQuotaUse");

            contractRow.setFieldNumValue("耗材总额", "setConsumableQuotaTotal");
            contractRow.setFieldNumValue("耗材剩余名额", "setConsumableQuota");
            contractRow.setFieldNumValue("耗材已使用名额", "setConsumableQuotaUse");
            contractRow.setFieldNumValue("耗材增幅", "setConsumeGrowth");
            /*contractRow.setFieldNumValue("探头高值总额", "setProbeQuotaHighTotal");
            contractRow.setFieldNumValue("探头高值剩余名额", "setProbeQuotaHigh");
            contractRow.setFieldNumValue("探头高值已使用名额", "setProbeQuotaHighUse");*/

            contractRow.setFieldNumValue("备件+人工+保养总额", "setSpareArtificailTotal");
            contractRow.setFieldNumValue("备件+人工+保养剩余名额", "setSpareArtificail");
            contractRow.setFieldNumValue("备件+人工+保养已使用名额", "setSpareArtificailUse");
            contractRow.setFieldStringValue("是否ZKB 599方案", "setFiveNineNineFlag");
            contractRow.setFieldStringValue("备注", "setRemark");
            contractRow.setFieldStringValue("公司编码", "setCompanyCode");
            contractRow.setFieldStringValue("599合同年份", "setFirstYearFlag");
            contractRow.setFieldStringValue("关联合同号", "setLinkContractNo");
            contractRow.setFieldStringValue("2022年起探头高低端名额分类", "setAssetQuotaType");
            contractRow.setFieldStringValue("积分是否清零", "setResetZeroScoreFlag");
            contractRow.setFieldStringValue("是否可以申请工具箱", "setCanApplyToolbox");

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
        try {
            for (Map<String, Object> item : importContractMapList) {
                if(checkData(item, importContractMapList)) {
                    ZkbContractVO contractInfoVO = (ZkbContractVO)item.get("contractInfoVO");
                    importOneContract(contractInfoVO,currentUser);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    protected void importOneContract(ZkbContractVO contractInfoVO,UserAccount currentUser) {
        ZkbContract zkbContract = queryContractByNo(contractInfoVO.getContractNo());
        if(zkbContract != null){
            if(StringUtils.isNotEmpty(contractInfoVO.getQuotaDeductType())){
                if("旧逻辑扣减".equals(contractInfoVO.getQuotaDeductType())){
                    zkbContract.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.Old.toString());
                }else if("新逻辑扣减".equals(contractInfoVO.getQuotaDeductType())){
                    zkbContract.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.New.toString());
                }else if("N/A".equals(contractInfoVO.getQuotaDeductType())){
                    zkbContract.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.Null.toString());
                }
            }
            if(StringUtils.isNotEmpty(contractInfoVO.getContractAmount())){
                if("100W/年以下".equals(contractInfoVO.getContractAmount())){
                    zkbContract.setContractAmount(ZkbContract.ContractAmount.oneMillionLess.toString());
                }else if("100W/年以上".equals(contractInfoVO.getContractAmount())){
                    zkbContract.setContractAmount(ZkbContract.ContractAmount.oneMillionMore.toString());
                }
            }
            zkbContract.setSpareQuotaTotal(contractInfoVO.getSpareQuotaTotal());
            zkbContract.setSpareQuota(contractInfoVO.getSpareQuota());
            zkbContract.setSpareQuotaUse(contractInfoVO.getSpareQuotaUse());

            zkbContract.setProbeQuotaTotal(contractInfoVO.getProbeQuotaTotal());
            zkbContract.setProbeQuota(contractInfoVO.getProbeQuota());
            zkbContract.setProbeQuotaUse(contractInfoVO.getProbeQuotaUse());

            zkbContract.setProbeQuotaNormalTotal(contractInfoVO.getProbeQuotaNormalTotal() == null ? 0 : contractInfoVO.getProbeQuotaNormalTotal());
            zkbContract.setProbeQuotaNormal(contractInfoVO.getProbeQuotaNormal() == null ? 0 : contractInfoVO.getProbeQuotaNormal());
            zkbContract.setProbeQuotaNormalUse(contractInfoVO.getProbeQuotaNormalUse() == null ? 0 : contractInfoVO.getProbeQuotaNormalUse());

            zkbContract.setProbeQuotaSpecialTotal(contractInfoVO.getProbeQuotaSpecialTotal() == null ? 0 : contractInfoVO.getProbeQuotaSpecialTotal());
            zkbContract.setProbeQuotaSpecial(contractInfoVO.getProbeQuotaSpecial() == null ? 0 : contractInfoVO.getProbeQuotaSpecial());
            zkbContract.setProbeQuotaSpecialUse(contractInfoVO.getProbeQuotaSpecialUse() == null ? 0 : contractInfoVO.getProbeQuotaSpecialUse());

            zkbContract.setSpareArtificailTotal(contractInfoVO.getSpareArtificailTotal());
            zkbContract.setSpareArtificail(contractInfoVO.getSpareArtificail());
            zkbContract.setSpareArtificailUse(contractInfoVO.getSpareArtificailUse());

            zkbContract.setGeArtificialSupportTimesTotal(contractInfoVO.getGeArtificialSupportTimesTotal());
            zkbContract.setGeArtificialSupportTimes(contractInfoVO.getGeArtificialSupportTimes());
            zkbContract.setGeArtificialSupportTimesUse(contractInfoVO.getGeArtificialSupportTimesUse());

            zkbContract.setCcsbMaintenanceQuotaTotal(contractInfoVO.getCcsbMaintenanceQuotaTotal());
            zkbContract.setCcsbMaintenanceQuota(contractInfoVO.getCcsbMaintenanceQuota());
            zkbContract.setCcsbMaintenanceQuotaUse(contractInfoVO.getCcsbMaintenanceQuotaUse());

            zkbContract.setPartHighQuotaTotal(contractInfoVO.getPartHighQuotaTotal());
            zkbContract.setPartHighQuota(contractInfoVO.getPartHighQuota());
            zkbContract.setPartHighQuotaUse(contractInfoVO.getPartHighQuotaUse());

            zkbContract.setConsumableQuota(contractInfoVO.getConsumableQuota());
            zkbContract.setConsumableQuotaTotal(contractInfoVO.getConsumableQuotaTotal());
            zkbContract.setConsumableQuotaUse(contractInfoVO.getConsumableQuotaUse());
            zkbContract.setConsumeGrowth(contractInfoVO.getConsumeGrowth());

            zkbContract.setSpareQuotaApply(zkbContract.getSpareQuotaTotal() - zkbContract.getSpareQuotaUse() - zkbContract.getSpareQuota());
            zkbContract.setProbeQuotaApply(zkbContract.getProbeQuotaTotal() - zkbContract.getProbeQuotaUse() - zkbContract.getProbeQuota());
            zkbContract.setProbeQuotaNormalApply(zkbContract.getProbeQuotaNormalTotal() - zkbContract.getProbeQuotaNormalUse() - zkbContract.getProbeQuotaNormal());
            zkbContract.setProbeQuotaSpecialApply(zkbContract.getProbeQuotaSpecialTotal() - zkbContract.getProbeQuotaSpecialUse() - zkbContract.getProbeQuotaSpecial());
            zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesTotal() - zkbContract.getGeArtificialSupportTimesUse() - zkbContract.getGeArtificialSupportTimes());
            zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailTotal() - zkbContract.getSpareArtificailUse() - zkbContract.getSpareArtificail());
            zkbContract.setPartHighQuotaApply(zkbContract.getPartHighQuotaTotal() - zkbContract.getPartHighQuota() - zkbContract.getPartHighQuotaUse());
            zkbContract.setCcsbMaintenanceQuotaApply(zkbContract.getCcsbMaintenanceQuotaTotal() - zkbContract.getCcsbMaintenanceQuota() - zkbContract.getCcsbMaintenanceQuotaUse());
            zkbContract.setConsumableQuotaApply(zkbContract.getConsumableQuotaTotal() - zkbContract.getConsumableQuota() - zkbContract.getConsumableQuotaUse());

            ZkbContract oldContract = zkbContractRepository.findById(zkbContract.getId());
            zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Spare.toString(),currentUser);
            zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.toString(),currentUser);
            zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Artificial.toString(),currentUser);
            zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Consumable.toString(),currentUser);
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())){
                zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString(),currentUser);
                zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),currentUser);
                zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString(),currentUser);
            }else{
                zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.Probe.toString(),currentUser);
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())){
                zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),currentUser);
            }
            if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(zkbContract.getContractType())){
                zkbContractService.quotaChange(zkbContract,oldContract,ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),currentUser);
            }
            zkbContractLogService.addLog(zkbContract.getId(),zkbContract.getContractNo(),"contractUpdate",null,null, "excel导入更新",currentUser,null,zkbContractService.getQuotaString(zkbContract));

            if(StringUtils.isNotEmpty(contractInfoVO.getFiveNineNineFlag())){
                if("是".equals(contractInfoVO.getFiveNineNineFlag())){
                    zkbContract.setFiveNineNineFlag(ZkbContract.YOrNEnum.Y.toString());
                    if("599合同第一年".equals(contractInfoVO.getFirstYearFlag())){
                        zkbContract.setFirstYearFlag(ZkbConstantUtil.T);
                    }else if("599合同第2和3年".equals(contractInfoVO.getFirstYearFlag())){
                        zkbContract.setFirstYearFlag(ZkbConstantUtil.F);
                    }else{
                        zkbContract.setFirstYearFlag("");
                    }
                }else{
                    zkbContract.setFiveNineNineFlag(ZkbContract.YOrNEnum.N.toString());
                }
                zkbContract.setLinkContractNo(contractInfoVO.getLinkContractNo());
                zkbContract.setCompanyCode(contractInfoVO.getCompanyCode());
            }
            if("是".equals(contractInfoVO.getAssetQuotaType())){
                zkbContract.setAssetQuotaType(ZkbConstantUtil.T);
            }else if("否".equals(contractInfoVO.getAssetQuotaType())){
                zkbContract.setAssetQuotaType(ZkbConstantUtil.F);
            }
            zkbContract.setLastModifiedBy(currentUser.getLoginName());
            zkbContract.setLastModifiedDate(new Date());
            if(StrUtil.isNotEmpty(contractInfoVO.getCanApplyToolbox())){
                //zkbContract.setCanApplyToolbox(contractInfoVO.getCanApplyToolbox());
                if("是".equals(contractInfoVO.getCanApplyToolbox())){
                    zkbContract.setCanApplyToolbox(ZkbConstantUtil.NORMAL_Y);
                }else if("否".equals(contractInfoVO.getCanApplyToolbox())){
                    zkbContract.setCanApplyToolbox(ZkbConstantUtil.NORMAL_N);
                }
            }
            zkbContractRepository.save(zkbContract);
        }else{
            ZkbContract newZkbContract = new ZkbContract();
            BeanUtils.copyProperties(contractInfoVO, newZkbContract);
            newZkbContract.setCreatedBy(currentUser.getLoginName());
            newZkbContract.setCreatedDate(new Date());
            newZkbContract.setLastModifiedDate(new Date());
            newZkbContract.setLastModifiedBy(currentUser.getLoginName());
            if(StringUtils.isNotEmpty(newZkbContract.getQuotaDeductType())){
                if("旧逻辑扣减".equals(newZkbContract.getQuotaDeductType())){
                    newZkbContract.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.Old.toString());
                }else if("新逻辑扣减".equals(newZkbContract.getQuotaDeductType())){
                    newZkbContract.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.New.toString());
                }else if("N/A".equals(newZkbContract.getQuotaDeductType())){
                    newZkbContract.setQuotaDeductType(ZkbContract.QuotaDeductTypeEnum.Null.toString());
                }
            }
            if(StringUtils.isNotEmpty(newZkbContract.getContractAmount())){
                if("100W/年以下".equals(newZkbContract.getContractAmount())){
                    newZkbContract.setContractAmount(ZkbContract.ContractAmount.oneMillionLess.toString());
                }else if("100W/年以上".equals(contractInfoVO.getContractAmount())){
                    newZkbContract.setContractAmount(ZkbContract.ContractAmount.oneMillionMore.toString());
                }
            }
            if(StringUtils.isNotEmpty(contractInfoVO.getFiveNineNineFlag())){
                if("是".equals(contractInfoVO.getFiveNineNineFlag())){
                    newZkbContract.setFiveNineNineFlag(ZkbContract.YOrNEnum.Y.toString());
                    if("599合同第一年".equals(contractInfoVO.getFirstYearFlag())){
                        newZkbContract.setFirstYearFlag(ZkbConstantUtil.T);
                    }else if("599合同第2和3年".equals(contractInfoVO.getFirstYearFlag())){
                        newZkbContract.setFirstYearFlag(ZkbConstantUtil.F);
                    }else{
                        newZkbContract.setFirstYearFlag("");
                    }
                }else{
                    newZkbContract.setFiveNineNineFlag(ZkbContract.YOrNEnum.N.toString());
                   /*newZkbContract.setStartDate(newZkbContract.getContractStartDate());
                    newZkbContract.setEndDate(newZkbContract.getContractEndDate());*/
                }
            }
            if("是".equals(contractInfoVO.getAssetQuotaType())){
                newZkbContract.setAssetQuotaType(ZkbConstantUtil.T);
            }else if("否".equals(contractInfoVO.getAssetQuotaType())){
                newZkbContract.setAssetQuotaType(ZkbConstantUtil.F);
            }
            if(StrUtil.isEmpty(contractInfoVO.getCanApplyToolbox())){
                newZkbContract.setCanApplyToolbox(ZkbConstantUtil.NORMAL_Y);
            }else{
                if("是".equals(contractInfoVO.getCanApplyToolbox())){
                    newZkbContract.setCanApplyToolbox(ZkbConstantUtil.NORMAL_Y);
                }else if("否".equals(contractInfoVO.getCanApplyToolbox())){
                    newZkbContract.setCanApplyToolbox(ZkbConstantUtil.NORMAL_N);
                }
            }
            newZkbContract.setSpareQuotaApply(newZkbContract.getSpareQuotaTotal() - newZkbContract.getSpareQuotaUse() - newZkbContract.getSpareQuota());
            newZkbContract.setProbeQuotaApply(newZkbContract.getProbeQuotaTotal() - newZkbContract.getProbeQuotaUse() - newZkbContract.getProbeQuota());
            newZkbContract.setGeArtificialSupportTimesApply(newZkbContract.getGeArtificialSupportTimesTotal() - newZkbContract.getGeArtificialSupportTimesUse() - newZkbContract.getGeArtificialSupportTimes());
            newZkbContract.setSpareArtificailApply(newZkbContract.getSpareArtificailTotal() - newZkbContract.getSpareArtificailUse() - newZkbContract.getSpareArtificail());
            newZkbContract.setPartHighQuotaApply(newZkbContract.getPartHighQuotaTotal() - newZkbContract.getPartHighQuota() - newZkbContract.getPartHighQuotaUse());
            newZkbContract.setCcsbMaintenanceQuotaApply(newZkbContract.getCcsbMaintenanceQuotaTotal() - newZkbContract.getCcsbMaintenanceQuota() - newZkbContract.getCcsbMaintenanceQuotaUse());
            newZkbContract.setConsumableQuotaApply(newZkbContract.getConsumableQuotaTotal() - newZkbContract.getConsumableQuota() - newZkbContract.getConsumableQuotaUse());
            newZkbContract.setConsumeGrowthDate(newZkbContract.getStartDate());
            zkbContractRepository.save(newZkbContract);
            zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Spare.name(),ZkbContract.class.getSimpleName(),currentUser,null);
            zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.SpareArtificial.name(),ZkbContract.class.getSimpleName(),currentUser,null);
            zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Artificial.name(),ZkbContract.class.getSimpleName(),currentUser,null);
            zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Consumable.name(),ZkbContract.class.getSimpleName(),currentUser,null);
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(newZkbContract.getQuotaDeductType())){
                zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.ProbeAny.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
                zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.ProbeNormal.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
                zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.ProbeSpecial.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }else{
                zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.Probe.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }
            if(ZkbContract.ContractType.CCSB.toString().equals(newZkbContract.getContractType())){
                zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.CssbMaintenance.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }
            if(ZkbContract.ContractType.DI.toString().equals(zkbContract.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(zkbContract.getContractType())) {
                zkbContractQuotaLogService.saveData(newZkbContract,null,null,"contractAdd",ZkbContractQuotaLog.OrderTypeEnum.PartsHigh.toString(),ZkbContract.class.getSimpleName(),currentUser,null);
            }
        }
        if(StringUtils.isNotEmpty(contractInfoVO.getResetZeroScoreFlag()) && "Y".equals(contractInfoVO.getResetZeroScoreFlag())){
            zkbIntegralService.resetZeroScore(contractInfoVO.getTenantUID(), currentUser, ZkbIntegralRecord.RecordType.ContractImport.name(), "合同导入积分清零");
        }

    }

    public boolean checkData(Map<String, Object> item, List<Map<String, Object>> importContractMapList) {
        boolean isValidateData = true;
        ZkbContractVO contractInfoVO = (ZkbContractVO)item.get("contractInfoVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ZkbContractRow.clearErrMessage(item);
        if(contractInfoVO.getContractNo() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorContractNoIsEmpty"));
            isValidateData = false;
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
        }
        //合同金额
        if(contractInfoVO.getContractAmount() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, WebUtil.getMessage("错误：合同金额为空"));
            isValidateData = false;
        }
        if(StringUtils.isEmpty(contractInfoVO.getFiveNineNineFlag())){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "是否ZKB 599方案不能为空！");
            isValidateData = false;
        }else {
            if(ZkbConstantUtil.T.equals(contractInfoVO.getFiveNineNineFlag())){
                if(StringUtils.isEmpty(contractInfoVO.getFirstYearFlag())){
                    item.put("status", ImportStatus.Failure);
                    ZkbContractRow.addErrMessage(item, "599合同年份不能为空！");
                    isValidateData = false;
                }
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
            List<TenantInfo> tenantInfoList = tenantInfoDao.findByNameAndType(contractInfoVO.getAgentName(),3);
            if(tenantInfoList == null || tenantInfoList.size() == 0){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("ErrorZkbContractTenantUIDExist"));
                isValidateData = false;
            }else if(tenantInfoList.size() > 1) {
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, WebUtil.getMessage("该代理商名称存在多条数据！"));
                isValidateData = false;
            }else{
                TenantInfo tenantInfo = tenantInfoList.get(0);
                contractInfoVO.setTenantUID(tenantInfo.getUid());
            }
        }
        if(StringUtils.isEmpty(contractInfoVO.getQuotaDeductType())){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item, "探头名额扣除类型不能为空！");
            isValidateData = false;
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

        int consumableQuotaTotal = contractInfoVO.getConsumableQuotaTotal() == null ? 0 : contractInfoVO.getConsumableQuotaTotal();
        int consumableQuota = contractInfoVO.getConsumableQuota() == null ? 0 : contractInfoVO.getConsumableQuota();
        int consumableQuotaUse = contractInfoVO.getConsumableQuotaUse() == null ? 0 : contractInfoVO.getConsumableQuotaUse();
        if(consumableQuotaTotal < (consumableQuota + consumableQuotaUse)){
            item.put("status", ImportStatus.Failure);
            ZkbContractRow.addErrMessage(item,  "耗材总额小于耗材剩余名额、耗材已使用名额之和");
            isValidateData = false;
        }

        if(ZkbContract.ContractType.Biomed.toString().equals(contractInfoVO.getContractType()) || ZkbContract.ContractType.CCS.toString().equals(contractInfoVO.getContractType())) {
            if (ZkbContract.QuotaDeductTypeEnum.Null.toString().equals(contractInfoVO.getQuotaDeductType())) {
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item, "合同扣除类型不能为N/A");
                isValidateData = false;
            }
        }

        if(ZkbContract.ContractType.CCSB.toString().equals(contractInfoVO.getContractType())){
            int ccsbMaintenanceQuotaTotal = contractInfoVO.getCcsbMaintenanceQuotaTotal() == null ? 0 : contractInfoVO.getCcsbMaintenanceQuotaTotal();
            int ccsbMaintenanceQuota = contractInfoVO.getCcsbMaintenanceQuota() == null ? 0 : contractInfoVO.getCcsbMaintenanceQuota();
            int ccsbMaintenanceQuotaUse = contractInfoVO.getCcsbMaintenanceQuotaUse() == null ? 0 : contractInfoVO.getCcsbMaintenanceQuotaUse();
            if(ccsbMaintenanceQuotaTotal < (ccsbMaintenanceQuota  + ccsbMaintenanceQuotaUse)){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item,  "保养总额小于保养剩余名额、保养已使用名额之和");
                isValidateData = false;
            }
        }

        if(ZkbContract.ContractType.DI.toString().equals(contractInfoVO.getContractType()) || ZkbContract.ContractType.ULS_OEC.toString().equals(contractInfoVO.getContractType()) || ZkbContract.ContractType.PCB.toString().equals(contractInfoVO.getContractType())){
            int partHighQuotaTotal = contractInfoVO.getPartHighQuotaTotal() == null ? 0 : contractInfoVO.getPartHighQuotaTotal();
            int partHighQuota = contractInfoVO.getPartHighQuota() == null ? 0 : contractInfoVO.getPartHighQuota();
            int partHighQuotaUse = contractInfoVO.getPartHighQuotaUse() == null ? 0 : contractInfoVO.getPartHighQuotaUse();
            if(partHighQuotaTotal < (partHighQuota + partHighQuotaUse)){
                item.put("status", ImportStatus.Failure);
                ZkbContractRow.addErrMessage(item,  "备件高值总额小于备件高值剩余名额、备件高值已使用名额之和");
                isValidateData = false;
            }
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
            courseListMap.put(course.getCid(),course);
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
        boolean checkCourseId = true;
        if(contractCourseVO != null){
            if(StringUtils.isEmpty(contractCourseVO.getCid())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                checkCourseId=false;
                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseIdIsEmpty"));
            }

            if(StringUtils.isEmpty(contractCourseVO.getCourseContractNo())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseContractNoEmpty"));
            }

//            if(StringUtils.isEmpty(contractCourseVO.getCname())){
//                isValidateData = false;
//                item.put("status", ImportStatus.Failure);
//                ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseNameIsEmpty"));
//            }
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
            if(checkCourseId)
            {
                ZkbCourse zkbCourse = courseListMap.get(contractCourseVO.getCid());
                if(zkbCourse == null){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractCourseRow.addErrMessage(item, WebUtil.getMessage("ErrorCourseIsNotExist"));
                }else{
                    contractCourseVO.setZkbCourse(zkbCourse);
                    contractCourseVO.setCourseId(zkbCourse.getId());
                }
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
        zkbContractCourse.setCreatedDate(new Date());
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
            zkbContractAssetRow.setFieldStringValue("是否代理商自身原因未完成培训","setTrainReasonFlag");
            zkbContractAssetRow.setFieldStringValue("是否由GE FE提供年度1次现场保养","setSupportMaintainFlag");
            zkbContractAssetRow.setFieldStringValue("zone","setZone");
            zkbContractAssetRow.setFieldStringValue("ID","setId");
            map.put("zkbContractAssetVO", zkbContractAssetVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(zkbContractAssetVO.getId()) ? ImportStatus.New.name() : ImportStatus.Exist.name());
            map.put("hashCode", zkbContractAssetVO.hashCode());
            result.add(map);
        }
        return result;
    }

    public void importAssetData(List<Map<String, Object>>impAssetMap,UserAccount currentUser){
        List<ZkbZone> zkbZoneList = zkbZoneRepository.find();
        List<String> zoneStr = zkbZoneList.stream().map(ZkbZone::getZone).collect(Collectors.toList());
        for (Map<String, Object> item : impAssetMap) {
            ZkbContractAssetVO zkbContractAssetVO = (ZkbContractAssetVO)item.get("zkbContractAssetVO");
            if(zkbContractAssetVO != null && checkAssetData(item,impAssetMap, zoneStr)) {
                importOneContractAsset(zkbContractAssetVO,currentUser);
            } 
        }
    }

    public boolean checkAssetData(Map<String, Object> item, List<Map<String, Object>> importContractCourseMapList, List<String> zoneStr){
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

        if("Y".equals(zkbContractAssetVO.getFlag())){
            if(StringUtils.isEmpty(zkbContractAssetVO.getId())){
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, "删除设备时请输入ID");
                isValidateData = false;
            }else {
                ZkbContractAsset zkbContractAsset = zkbContractAssetRepository.findById(zkbContractAssetVO.getId());
                if(zkbContractAsset ==null){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbContractAssetIsNotExist"));
                }else{
                    zkbContractAssetVO.setZkbContractAssetId(zkbContractAsset.getId());
                }
            }
        }else{
            if(zkbContractAssetVO.getQrCode() == null){
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbQrCodeIsEmpty"));
                isValidateData = false;
            }else {
                AssetInfo assetInfo = queryAssetInfoByQrCode(zkbContractAssetVO.getQrCode());
                if (assetInfo == null) {
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, WebUtil.getMessage("ErrorImpZkbQrCodeIsNotExist"));
                } else {
                    zkbContractAssetVO.setAssetId(assetInfo.getUid());
                    if (!"Y".equals(zkbContractAssetVO.getFlag())) {
                        boolean flag = true;
                        if (assetInfo.getIsDeleted() != null && assetInfo.getIsDeleted()) {
                            flag = false;
                        }
                        if (!assetInfo.getIsValid()) {
                            flag = false;
                        }
                        if (!flag) {
                            isValidateData = false;
                            item.put("status", ImportStatus.Failure);
                            ZkbContractAssetRow.addErrMessage(item, "设备状态异常，请检查！");
                        }
                    }
                }
            }
            if(StringUtils.isEmpty(zkbContractAssetVO.getSupportMaintainFlag())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, "是否由GE FE提供年度1次现场保养不能为空！");
            }else{
                if(StringUtils.isEmpty(getTrainReasonFlagFromCh(zkbContractAssetVO.getSupportMaintainFlag()))){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, "是否由GE FE提供年度1次现场保养只能选择是或否！");
                }else{
                    zkbContractAssetVO.setSupportMaintainFlag(getTrainReasonFlagFromCh(zkbContractAssetVO.getSupportMaintainFlag()));
                }
            }

            if(StringUtils.isEmpty(zkbContractAssetVO.getTrainReasonFlag())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, "是否代理商自身原因未完成培训不能为空！");
            }else{
                if(StringUtils.isEmpty(getTrainReasonFlagFromCh(zkbContractAssetVO.getTrainReasonFlag()))){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, "是否代理商自身原因未完成培训取值范围有误！");
                }
            }

            if(StringUtils.isEmpty(zkbContractAssetVO.getAssetType())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, "高低端机不能为空！");
            }else{
                if(StringUtils.isEmpty(getHighLowAssetFromCh(zkbContractAssetVO.getAssetType()))){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, "高低端机取值范围有误！");
                }
            }

            if(StringUtils.isEmpty(zkbContractAssetVO.getZone())){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractAssetRow.addErrMessage(item, "zone不能为空！");
            }else{
                if(!zoneStr.contains(zkbContractAssetVO.getZone())){
                    isValidateData = false;
                    item.put("status", ImportStatus.Failure);
                    ZkbContractAssetRow.addErrMessage(item, "zone取值范围有误！");
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
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getTrainReasonFlag())){
                    zkbContractAsset.setTrainReasonFlag(getTrainReasonFlagFromCh(zkbContractAssetVO.getTrainReasonFlag()));
                }else {
                    zkbContractAsset.setTrainReasonFlag("empty");
                }
                zkbContractAsset.setSupportMaintainFlag(zkbContractAssetVO.getSupportMaintainFlag());
                zkbContractAsset.setLastModifiedBy(currentUser.getName());
                zkbContractAsset.setLastModifiedDate(new Date());
                zkbContractAsset.setZone(zkbContractAssetVO.getZone());
                zkbContractAssetRepository.save(zkbContractAsset);
                zkbContractLogService.addLog(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getContractNo(),"assetEdit","",zkbContractAssetVO.getQrCode(),"导入设备编辑",currentUser,null, null);

                AssetInfo assetInfo = assetInfoRepository.findByUid(zkbContractAsset.getAssetId());
                boolean modifyFlag= false;
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getRepairForm())){
                    Integer repairForm = getRepairFormFromCh(zkbContractAssetVO.getRepairForm());
                    if(assetInfo != null && repairForm != null){
                        if(!repairForm.equals(assetInfo.getRepairForm())){
                            assetInfo.setRepairForm(repairForm);
                            modifyFlag = true;
                        }
                    }
                }
                ZkbContract zkbContract = zkbContractRepository.findById(zkbContractAsset.getContractId());
                if(assetInfo.getWarrantyDate() != null){
                    long betweenDay = DateUtil.between(assetInfo.getWarrantyDate(), zkbContract.getEndDate(), DateUnit.DAY);
                    if(betweenDay != 0){
                        assetInfo.setWarrantyDate(zkbContract.getEndDate());
                        modifyFlag = true;
                    }
                }else {
                    assetInfo.setWarrantyDate(zkbContract.getEndDate());
                    modifyFlag = true;
                }

                if(modifyFlag){
                    assetInfoRepository.save(assetInfo);
                }
            }else{
                ZkbContractAsset newZkbContractAsset = new ZkbContractAsset();
                newZkbContractAsset.setContractId(zkbContractAssetVO.getContractId());
                newZkbContractAsset.setAssetId(zkbContractAssetVO.getAssetId());
                newZkbContractAsset.setHighLowMachine(zkbContractAssetVO.getHighLowMachine());
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getAssetType())){
                    newZkbContractAsset.setAssetType(getHighLowAssetFromCh(zkbContractAssetVO.getAssetType()));
                }
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getTrainReasonFlag())){
                    newZkbContractAsset.setTrainReasonFlag(getTrainReasonFlagFromCh(zkbContractAssetVO.getTrainReasonFlag()));
                }else {
                    newZkbContractAsset.setTrainReasonFlag("empty");
                }
                if(StringUtils.isNotBlank(zkbContractAssetVO.getSupportMaintainFlag())){
                    newZkbContractAsset.setSupportMaintainFlag(zkbContractAssetVO.getSupportMaintainFlag());
                }
                newZkbContractAsset.setSubRegion(zkbContractAssetVO.getSubRegion());
                newZkbContractAsset.setModality(zkbContractAssetVO.getModality());
                newZkbContractAsset.setResponsiblEngineer(zkbContractAssetVO.getResponsiblEngineer());
                newZkbContractAsset.setKa(zkbContractAssetVO.getKa());
                newZkbContractAsset.setCreatedBy(currentUser.getName());
                newZkbContractAsset.setCreatedDate(new Date());
                newZkbContractAsset.setLastModifiedBy(currentUser.getName());
                newZkbContractAsset.setLastModifiedDate(new Date());
                newZkbContractAsset.setZone(zkbContractAssetVO.getZone());
                zkbContractAssetRepository.save(newZkbContractAsset);
                zkbContractLogService.addLog(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getContractNo(),"assetAdd","",zkbContractAssetVO.getQrCode(),"导入新增",currentUser,null, null);

                AssetInfo assetInfo = assetInfoRepository.findByUid(newZkbContractAsset.getAssetId());
                boolean modifyFlag= false;
                if(StringUtils.isNotEmpty(zkbContractAssetVO.getRepairForm())){
                    Integer repairForm = getRepairFormFromCh(zkbContractAssetVO.getRepairForm());
                    if(assetInfo != null && repairForm != null){
                        if(!repairForm.equals(assetInfo.getRepairForm())){
                            assetInfo.setRepairForm(repairForm);
                            modifyFlag = true;
                        }
                    }
                }
                ZkbContract zkbContract = zkbContractRepository.findById(newZkbContractAsset.getContractId());
                if(assetInfo.getWarrantyDate() != null){
                    long betweenDay = DateUtil.between(assetInfo.getWarrantyDate(), zkbContract.getEndDate(), DateUnit.DAY);
                    if(betweenDay != 0){
                        assetInfo.setWarrantyDate(zkbContract.getEndDate());
                        modifyFlag = true;
                    }
                }else {
                    assetInfo.setWarrantyDate(zkbContract.getEndDate());
                    modifyFlag = true;
                }

                if(modifyFlag){
                    assetInfoRepository.save(assetInfo);
                }
            }
        }else if("Y".equals(zkbContractAssetVO.getFlag())){
            zkbContractAssetRepository.delete(zkbContractAssetVO.getZkbContractAssetId());
            zkbContractLogService.addLog(zkbContractAssetVO.getContractId(),zkbContractAssetVO.getContractNo(),"assetDelete","",zkbContractAssetVO.getQrCode(),"导入删除",currentUser,null, null);
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
            case "depot":
                return "depot";
            default:
                return "";
        }
    }

    public String getTrainReasonFlagFromCh(String va){
        switch (va){
            case "是":
                return "t";
            case "否":
                return "f";
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

    //region 合同高值探头
    public List<Map<String, Object>> getContractCatMapFromTemplate(ExcelDocument doc) throws Exception{
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_CONTRACT_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_SERVICE_CONTRACT_Cat).getLastRowNum() + 1 ; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_SERVICE_CONTRACT_Cat, EXCEL_CONTRACT_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbContractCatVO zkbContractCatVO = new ZkbContractCatVO();
            zkbContractCatVO.setLineNum(i);

            ZkbContractCatRow zkbContractCatRow = new ZkbContractCatRow(zkbContractCatVO, rowMap);
            zkbContractCatRow.setFieldStringValue("合同号", "setContractNo");
            zkbContractCatRow.setFieldStringValue("SN编号","setSnNo");
            zkbContractCatRow.setFieldStringValue("状态","setEnabledName");

            map.put("zkbContractCatVO", zkbContractCatVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(zkbContractCatVO.getId()) ? ImportStatus.New.name() : ImportStatus.Exist.name());
            map.put("hashCode", zkbContractCatVO.hashCode());
            result.add(map);
        }
        return result;
    }
    public void importCatData(List<Map<String, Object>>impCatMap,UserAccount currentUser){
        for (Map<String, Object> item : impCatMap) {
            ZkbContractCatVO zkbContractCatVO = (ZkbContractCatVO)item.get("zkbContractCatVO");
            if(zkbContractCatVO != null && checkCatData(item,impCatMap)) {
                importOneContractCat(zkbContractCatVO,currentUser);
            }
        }
    }

    public boolean checkCatData(Map<String, Object> item, List<Map<String, Object>> importContractCatMapList){
        boolean isValidateData = true;
        ZkbContractCatVO zkbContractCatVO = (ZkbContractCatVO)item.get("zkbContractCatVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ZkbContractCatRow.clearErrMessage(item);
        if(zkbContractCatVO.getContractNo() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractCatRow.addErrMessage(item, WebUtil.getMessage("ErrorContractNumberIsEmpty"));
            isValidateData = false;
        }else {
            ZkbContract zkbContract =  queryContractByNo(zkbContractCatVO.getContractNo());
            if(zkbContract == null){
                isValidateData = false;
                item.put("status", ImportStatus.Failure);
                ZkbContractCatRow.addErrMessage(item, WebUtil.getMessage("ErrorContractIsNotExist"));
            }else{
                zkbContractCatVO.setContractId(zkbContract.getId());
            }
        }

        if(StringUtils.isEmpty(zkbContractCatVO.getSnNo())){
            item.put("status", ImportStatus.Failure);
            ZkbContractCatRow.addErrMessage(item, "SN编号不能为空");
            isValidateData = false;
        }else{
           /* if(StringUtils.isNotEmpty(zkbContractCatVO.getContractId())){
                ZkbContractCat zkbContractCat = zkbContractCatRepository.findFirstByContractIdAndSnNo(zkbContractCatVO.getContractId(),zkbContractCatVO.getSnNo());
                if(zkbContractCat != null){
                    item.put("status", ImportStatus.Failure);
                    ZkbContractCatRow.addErrMessage(item, "SN编号不能重复");
                    isValidateData = false;
                }
            }*/
        }

        if(zkbContractCatVO.getEnabledName() == null){
            item.put("status", ImportStatus.Failure);
            ZkbContractCatRow.addErrMessage(item, "状态不能为空");
            isValidateData = false;
        }

       /* for (Map<String, Object> tempContractCatMap: importContractCatMapList) {
            ZkbContractCatVO tempZkbContractCatVO = (ZkbContractCatVO)tempContractCatMap.get("zkbContractCatVO");
            Integer tempHashCode = Integer.valueOf(tempContractCatMap.get("hashCode").toString());
            if(hashCode.equals(tempHashCode) && zkbContractCatVO.getLineNum() != tempZkbContractCatVO.getLineNum()){
                item.put("status", ImportStatus.Failure);
                ZkbContractCatRow.addErrMessage(item, WebUtil.getMessage("ErrorDuplicatedData"));
                isValidateData = false;
                break;
            }
        }*/
        return isValidateData;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importOneContractCat(ZkbContractCatVO zkbContractCatVO,UserAccount currentUser){
        ZkbContractCat zkbContractCat = new ZkbContractCat();
        zkbContractCat.setContractId(zkbContractCatVO.getContractId());
        zkbContractCat.setSnNo(zkbContractCatVO.getSnNo());
        if("可用".equals(zkbContractCatVO.getEnabledName())){
            zkbContractCat.setEnabled(true);
        }else if("不可用".equals(zkbContractCatVO.getEnabledName())){
            zkbContractCat.setEnabled(false);
        }
        zkbContractCat.setCreatedBy(currentUser.getName());
        zkbContractCat.setCreatedDate(new Date());
        zkbContractCatRepository.save(zkbContractCat);
        zkbContractLogService.addLog(zkbContractCatVO.getContractId(),zkbContractCatVO.getContractNo(),"catAdd","",zkbContractCatVO.getSnNo(),"导入新增",currentUser,null, null);
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

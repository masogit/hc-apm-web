package com.ge.apm.service.agreementTemplate;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.AdjustmentAgreementDataRepository;
import com.ge.apm.domain.AdjustmentAgreementData;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 调剂协议模板服务
 * 负责协议数据的持久化、模板渲染和文件生成
 */
@Service
public class AgreementTemplateService {

    @Autowired
    private AdjustmentAgreementDataRepository agreementDataRepository;

    @Autowired
    private SystemTemplateService systemTemplateService;

    /** 合同条款字段键（与模板占位符一一对应） */
    private static final String[] CONTRACT_FIELD_KEYS = {
            "transferAmount", "transferAmountWritten", "payYear", "payMonth", "payDay", "penaltyRate",
            "bankName", "bankBranch", "bankAccount", "rentAmount", "rentAmountWritten",
            "paymentDay", "rentPenaltyRate", "rentBankName", "rentBankBranch", "rentBankAccount",
            "rentStartYear", "rentStartMonth", "rentStartDay", "rentEndYear", "rentEndMonth", "rentEndDay",
            "deliverYear", "deliverMonth", "deliverDay", "deliverAddress",
            "acceptDays", "failNotifyDays", "fixDays",
            "breachAmount", "totalCopies", "partyACopies", "partyBCopies", "partyCCopies", "archiveCopies",
            "signPartyAName", "signPartyARep", "signAYear", "signAMonth", "signADay",
            "signPartyBName", "signPartyBRep", "signBYear", "signBMonth", "signBDay",
            "signPartyCName", "signPartyCRep", "signCYear", "signCMonth", "signCDay"
    };

    /** 合同复选框键（与模板占位符一一对应） */
    private static final String[] CONTRACT_CHECK_KEYS = {
            "isTransfer", "isLease",
            "payMonthly", "payQuarterly", "payYearly", "payLumpSum",
            "transportPartyA", "transportPartyB", "transportShared",
            "inspectPartyA", "inspectPartyB", "inspectNegotiate",
            "disputePartyA", "disputePartyB", "disputePartyC"
    };

    /** 终止协议字段键（与模板占位符一一对应） */
    private static final String[] TERMINATION_FIELD_KEYS = {
            "terminationReason",
            "origSignYear", "origSignMonth", "origSignDay",
            "returnYear", "returnMonth", "returnDay",
            "settlementDetail1", "settlementDetail2",
            "termOtherReason",
            "totalCopies", "partyACopies", "partyBCopies", "partyCCopies",
            "signPartyARep", "signAYear", "signAMonth", "signADay",
            "signPartyBRep", "signBYear", "signBMonth", "signBDay",
            "signPartyCRep", "signCYear", "signCMonth", "signCDay"
    };

    /** 终止协议复选框键（与模板占位符一一对应） */
    private static final String[] TERMINATION_CHECK_KEYS = {
            "origTransferCheck", "origLeaseCheck",
            "termCompleted", "termNegotiated", "termScrapped", "termOtherCheck",
            "disputePartyA", "disputePartyB", "disputePartyC"
    };

    // ==================== 数据持久化 ====================

    /**
     * 按医院UID和协议类型查询已有协议数据
     */
    public AdjustmentAgreementData findAgreementData(String hospitalUid, String agreementType) {
        return agreementDataRepository.findByHospitalUidAndAgreementType(hospitalUid, agreementType);
    }

    /**
     * 保存协议数据（新建或覆盖更新）
     *
     * @param existingData          已有记录（为null时新建）
     * @param hospitalUid           医院UID
     * @param agreementType         协议类型
     * @param lendingHospitalUid    借出医院UID
     * @param borrowingHospitalUid  借入医院UID
     * @param templateDataJson      JSON格式的表单数据
     * @param userId                操作用户UID
     * @return 保存后的实体
     */
    public AdjustmentAgreementData saveAgreementData(AdjustmentAgreementData existingData,
                                                     String hospitalUid,
                                                     String agreementType,
                                                     String lendingHospitalUid,
                                                     String borrowingHospitalUid,
                                                     String templateDataJson,
                                                     String userId) {
        if (existingData == null) {
            existingData = new AdjustmentAgreementData();
            existingData.setHospitalUid(hospitalUid);
            existingData.setAgreementType(agreementType);
            existingData.setCreatedBy(userId);
            existingData.setCreatedDate(new Date());
        }
        existingData.setTemplateData(templateDataJson);
        existingData.setLendingHospitalUid(lendingHospitalUid);
        existingData.setBorrowingHospitalUid(borrowingHospitalUid);
        existingData.setUpdatedBy(userId);
        existingData.setUpdatedDate(new Date());
        return agreementDataRepository.save(existingData);
    }

    // ==================== JSON 序列化/反序列化 ====================

    /**
     * 将表单数据序列化为JSON字符串
     */
    public String buildFormDataJson(String partyAName, String partyACreditCode,
                                    String partyARepresentative, String partyAPhone,
                                    String partyBName, String partyBCreditCode,
                                    String partyBRepresentative, String partyBPhone,
                                    String partyCName, String partyCCreditCode,
                                    String partyCRepresentative, String partyCPhone,
                                    Map<String, String> contractData,
                                    Map<String, Boolean> contractChecks) {
        JSONObject data = new JSONObject();
        data.put("partyAName", partyAName);
        data.put("partyACreditCode", partyACreditCode);
        data.put("partyARepresentative", partyARepresentative);
        data.put("partyAPhone", partyAPhone);
        data.put("partyBName", partyBName);
        data.put("partyBCreditCode", partyBCreditCode);
        data.put("partyBRepresentative", partyBRepresentative);
        data.put("partyBPhone", partyBPhone);
        data.put("partyCName", partyCName);
        data.put("partyCCreditCode", partyCCreditCode);
        data.put("partyCRepresentative", partyCRepresentative);
        data.put("partyCPhone", partyCPhone);
        data.put("contractData", new JSONObject(new HashMap<>(contractData)));
        data.put("contractChecks", new JSONObject(new HashMap<>(contractChecks)));
        return data.toJSONString();
    }

    /**
     * 从JSON字符串解析甲方名称
     */
    public static String parsePartyAName(String json) {
        return parseStringField(json, "partyAName");
    }

    /**
     * 从JSON字符串解析指定字段
     */
    public static String parseStringField(String json, String fieldName) {
        if (StringUtils.isEmpty(json)) return "";
        try {
            JSONObject data = JSONObject.parseObject(json);
            String val = data.getString(fieldName);
            return val != null ? val : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 从JSON字符串解析合同条款Map
     */
    public static Map<String, String> parseContractData(String json) {
        Map<String, String> result = new HashMap<>();
        if (StringUtils.isEmpty(json)) return result;
        try {
            JSONObject data = JSONObject.parseObject(json);
            JSONObject contractJson = data.getJSONObject("contractData");
            if (contractJson != null) {
                for (String key : contractJson.keySet()) {
                    String val = contractJson.getString(key);
                    result.put(key, val != null ? val : "");
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    /**
     * 从JSON字符串解析合同复选框Map
     */
    public static Map<String, Boolean> parseContractChecks(String json) {
        Map<String, Boolean> result = new HashMap<>();
        if (StringUtils.isEmpty(json)) return result;
        try {
            JSONObject data = JSONObject.parseObject(json);
            JSONObject checksJson = data.getJSONObject("contractChecks");
            if (checksJson != null) {
                for (String key : checksJson.keySet()) {
                    Boolean val = checksJson.getBoolean(key);
                    result.put(key, val != null ? val : false);
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    // ==================== 模板渲染 ====================

    /**
     * 构建poi-tl渲染数据Map
     *
     * @param partyAName          甲方名称
     * @param partyACreditCode    甲方信用代码
     * @param partyARepresentative 甲方代表人
     * @param partyAPhone         甲方电话
     * @param partyBName          乙方名称
     * @param partyBCreditCode    乙方信用代码
     * @param partyBRepresentative 乙方代表人
     * @param partyBPhone         乙方电话
     * @param partyCName          丙方名称
     * @param partyCCreditCode    丙方信用代码
     * @param partyCRepresentative 丙方代表人
     * @param partyCPhone         丙方电话
     * @param contractData        合同条款字段Map
     * @param contractChecks      合同复选框Map
     * @return poi-tl渲染数据
     */
    public Map<String, Object> buildRenderData(String partyAName, String partyACreditCode,
                                                String partyARepresentative, String partyAPhone,
                                                String partyBName, String partyBCreditCode,
                                                String partyBRepresentative, String partyBPhone,
                                                String partyCName, String partyCCreditCode,
                                                String partyCRepresentative, String partyCPhone,
                                                Map<String, String> contractData,
                                                Map<String, Boolean> contractChecks,
                                                String agreementType) {
        Map<String, Object> data = new HashMap<>();
        // 甲方信息
        data.put("partyAName", safe(partyAName));
        data.put("partyACreditCode", safe(partyACreditCode));
        data.put("partyARepresentative", safe(partyARepresentative));
        data.put("partyAPhone", safe(partyAPhone));
        // 乙方信息
        data.put("partyBName", safe(partyBName));
        data.put("partyBCreditCode", safe(partyBCreditCode));
        data.put("partyBRepresentative", safe(partyBRepresentative));
        data.put("partyBPhone", safe(partyBPhone));
        // 丙方信息
        data.put("partyCName", safe(partyCName));
        data.put("partyCCreditCode", safe(partyCCreditCode));
        data.put("partyCRepresentative", safe(partyCRepresentative));
        data.put("partyCPhone", safe(partyCPhone));
        // 根据协议类型选择对应的字段键和复选框键
        String[] fieldKeys = getFieldKeys(agreementType);
        String[] checkKeys = getCheckKeys(agreementType);
        // 合同条款字段
        for (String key : fieldKeys) {
            String val = contractData.get(key);
            data.put(key, StringUtils.isNotEmpty(val) ? val : "");
        }
        // 复选框（转为Unicode字符）
        for (String key : checkKeys) {
            Boolean val = contractChecks.get(key);
            data.put(key, val != null && val ? "\u2611" : "\u2610"); // ☑ or ☐
        }
        return data;
    }

    private static final Logger logger = LoggerFactory.getLogger(AgreementTemplateService.class);

    /** 本地模板classpath路径（作为远程模板的fallback） */
    private static final String LOCAL_TEMPLATE_PATH = "/portal/hospitalAdjustment/template/agreement_template.docx";

    /** 终止协议本地模板classpath路径 */
    private static final String LOCAL_TERMINATION_TEMPLATE_PATH = "/portal/hospitalAdjustment/template/termination_agreement_template.docx";

    /**
     * 获取协议Word模板字节数组
     * 优先从本地classpath加载模板，若不存在则fallback到远程模板服务
     *
     * @param user 当前用户
     * @return 模板字节数组，未找到返回null
     */
    public byte[] getTemplateBytes(UserAccount user) throws IOException {
        return getTemplateBytes(user, AdjustmentAgreementData.TYPE_ADJUSTMENT);
    }

    /**
     * 获取协议Word模板字节数组（按协议类型区分）
     *
     * @param user            当前用户
     * @param agreementType   协议类型
     * @return 模板字节数组，未找到返回null
     */
    public byte[] getTemplateBytes(UserAccount user, String agreementType) throws IOException {
        // 根据协议类型选择本地模板路径
        String localPath = AdjustmentAgreementData.TYPE_TERMINATION.equals(agreementType)
                ? LOCAL_TERMINATION_TEMPLATE_PATH : LOCAL_TEMPLATE_PATH;
        // 1. 优先从本地classpath加载
        byte[] localBytes = loadLocalTemplate(localPath);
        if (localBytes != null) {
            logger.info("使用本地模板: {}", localPath);
            return localBytes;
        }
        // 2. Fallback: 从远程模板服务获取
        logger.warn("本地模板未找到，尝试从远程模板服务获取");
        byte[] templateBytes = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.HOSPITAL_ADJUSTMENT_AGREEMENT, user);
        if (templateBytes != null) {
            return templateBytes;
        }
        logger.error("本地模板和远程模板均未找到");
        return null;
    }

    /**
     * 从本地classpath加载模板文件
     */
    private byte[] loadLocalTemplate() throws IOException {
        return loadLocalTemplate(LOCAL_TEMPLATE_PATH);
    }

    /**
     * 从本地classpath加载指定路径的模板文件
     */
    private byte[] loadLocalTemplate(String path) throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            logger.error("无法获取FacesContext，无法加载本地模板");
            return null;
        }
        InputStream is = facesContext.getExternalContext().getResourceAsStream(path);
        if (is == null) {
            logger.error("本地模板文件不存在: {}", path);
            return null;
        }
        try {
            return IOUtils.toByteArray(is);
        } finally {
            is.close();
        }
    }

    /**
     * 渲染协议Word文件
     *
     * @param renderData poi-tl渲染数据
     * @param user       当前用户
     * @return 渲染后的Word文件字节数组，模板未找到返回null
     */
    public byte[] renderAgreement(Map<String, Object> renderData, UserAccount user) throws IOException {
        return renderAgreement(renderData, user, AdjustmentAgreementData.TYPE_ADJUSTMENT);
    }

    /**
     * 渲染协议Word文件（按协议类型区分模板）
     */
    public byte[] renderAgreement(Map<String, Object> renderData, UserAccount user, String agreementType) throws IOException {
        byte[] templateBytes = getTemplateBytes(user, agreementType);
        if (templateBytes == null) {
            return null;
        }
        return WordExportServiceUtil.generate(renderData, templateBytes, new HashMap<>());
    }

    // ==================== 工具方法 ====================

    private String safe(String value) {
        return StringUtils.isNotEmpty(value) ? value : "";
    }

    /**
     * 获取合同条款字段键数组
     */
    public static String[] getContractFieldKeys() {
        return CONTRACT_FIELD_KEYS.clone();
    }

    /**
     * 获取合同复选框键数组
     */
    public static String[] getContractCheckKeys() {
        return CONTRACT_CHECK_KEYS.clone();
    }

    /**
     * 获取终止协议字段键数组
     */
    public static String[] getTerminationFieldKeys() {
        return TERMINATION_FIELD_KEYS.clone();
    }

    /**
     * 获取终止协议复选框键数组
     */
    public static String[] getTerminationCheckKeys() {
        return TERMINATION_CHECK_KEYS.clone();
    }

    /**
     * 根据协议类型获取字段键数组
     */
    public static String[] getFieldKeys(String agreementType) {
        if (AdjustmentAgreementData.TYPE_TERMINATION.equals(agreementType)) {
            return TERMINATION_FIELD_KEYS.clone();
        }
        return CONTRACT_FIELD_KEYS.clone();
    }

    /**
     * 根据协议类型获取复选框键数组
     */
    public static String[] getCheckKeys(String agreementType) {
        if (AdjustmentAgreementData.TYPE_TERMINATION.equals(agreementType)) {
            return TERMINATION_CHECK_KEYS.clone();
        }
        return CONTRACT_CHECK_KEYS.clone();
    }
}

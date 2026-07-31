package com.ge.apm.view.hospitalAdjustment;

import com.ge.apm.domain.AdjustmentAgreementData;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.agreementTemplate.AgreementTemplateService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import webapp.framework.web.WebUtil;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.*;

/**
 * 调剂协议编辑页面 Controller
 * 支持协议表单数据的编辑、保存（JSON）和下载（poi-tl渲染Word）
 */
@ManagedBean
@ViewScoped
public class AgreementEditController implements Serializable {

    private static final long serialVersionUID = 1L;

    private AgreementTemplateService agreementTemplateService;
    private UserContextService userContextService;
    private UserAccount currentUserAccount;

    /** 当前工单UID */
    @Getter @Setter
    private String workOrderUid;

    /** 协议类型 */
    @Getter @Setter
    private String agreementType;

    /** 数据库中已有的记录（用于编辑） */
    private AdjustmentAgreementData currentData;

    // ========== 表单字段（甲方 - 调出单位） ==========
    @Getter @Setter private String partyAName = "";
    @Getter @Setter private String partyACreditCode = "";
    @Getter @Setter private String partyARepresentative = "";
    @Getter @Setter private String partyAPhone = "";

    // ========== 表单字段（乙方 - 调入单位） ==========
    @Getter @Setter private String partyBName = "";
    @Getter @Setter private String partyBCreditCode = "";
    @Getter @Setter private String partyBRepresentative = "";
    @Getter @Setter private String partyBPhone = "";

    // ========== 表单字段（丙方 - 监管单位，固定值） ==========
    @Getter private String partyCName = "常州市武进区药品医用耗材管理中心";
    @Getter private String partyCCreditCode = "12320483MB0201929K";
    @Getter @Setter private String partyCRepresentative = "";
    @Getter @Setter private String partyCPhone = "";

    // ========== 借出/借入医院UID ==========
    @Getter @Setter private String lendingHospitalUid;
    @Getter @Setter private String borrowingHospitalUid;

    // ========== 合同条款数据（所有条款空白处的字符串值） ==========
    private Map<String, String> contractData = new HashMap<>();

    public Map<String, String> getContractData() {
        return contractData;
    }

    public void setContractData(Map<String, String> contractData) {
        this.contractData = contractData;
    }

    public String getContractField(String key) {
        String value = contractData.get(key);
        return value != null ? value : "";
    }

    public String contractField(String key) {
        return getContractField(key);
    }

    public void setContractField(String key, String value) {
        contractData.put(key, value != null ? value : "");
    }

    // ========== 合同复选框数据（Boolean类型） ==========
    private Map<String, Boolean> contractChecks = new HashMap<>();

    public Map<String, Boolean> getContractChecks() {
        return contractChecks;
    }

    public void setContractChecks(Map<String, Boolean> contractChecks) {
        this.contractChecks = contractChecks;
    }

    public Boolean getContractCheck(String key) {
        Boolean value = contractChecks.get(key);
        return value != null ? value : false;
    }

    public Boolean contractCheck(String key) {
        return getContractCheck(key);
    }

    /** 复选框单选组定义（每组只能勾选一个） */
    private static final String[][] ADJUSTMENT_CHECK_GROUPS = {
            {"isTransfer", "isLease"},
            {"payMonthly", "payQuarterly", "payYearly", "payLumpSum"},
            {"transportPartyA", "transportPartyB", "transportShared"},
            {"inspectPartyA", "inspectPartyB", "inspectNegotiate"},
            {"disputePartyA", "disputePartyB", "disputePartyC"}
    };
    private static final String[][] TERMINATION_CHECK_GROUPS = {
            {"origTransferCheck", "origLeaseCheck"},
            {"termCompleted", "termNegotiated", "termScrapped", "termOtherCheck"},
            {"disputePartyA", "disputePartyB", "disputePartyC"}
    };

    /** 上一次复选框状态快照，用于判断用户实际点击了哪个 */
    private Map<String, Boolean> previousContractChecks = new HashMap<>();

    public void setContractCheck(String key, Boolean value) {
        contractChecks.put(key, value != null ? value : false);
    }

    /**
     * 复选框Ajax事件监听：在Map被JSF更新后执行同组互斥（单选行为）
     * 通过对比上次状态找出用户实际点击的选项，保留它并取消同组其他选项
     */
    public void onCheckChange() {
        String[][] groups = AdjustmentAgreementData.TYPE_TERMINATION.equals(this.agreementType)
                ? TERMINATION_CHECK_GROUPS : ADJUSTMENT_CHECK_GROUPS;
        for (String[] group : groups) {
            // 找出本组中新变为true的选项（用户刚点击的）
            String newlyChecked = null;
            for (String gk : group) {
                Boolean current = contractChecks.get(gk);
                Boolean previous = previousContractChecks.get(gk);
                if (Boolean.TRUE.equals(current) && !Boolean.TRUE.equals(previous)) {
                    newlyChecked = gk;
                    break;
                }
            }
            if (newlyChecked != null) {
                // 保留用户点击的，取消同组其他
                for (String other : group) {
                    if (!other.equals(newlyChecked)) {
                        contractChecks.put(other, false);
                    }
                }
            }
        }
        // 更新快照
        previousContractChecks = new HashMap<>(contractChecks);
    }

    public AgreementEditController() {
        agreementTemplateService = WebUtil.getBean(AgreementTemplateService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        currentUserAccount = UserContextService.getCurrentUserAccount();
        initContractMaps();
        init();

    }

    private void initContractMaps() {
        // 根据协议类型初始化字段键
        String type = this.agreementType != null ? this.agreementType : AdjustmentAgreementData.TYPE_ADJUSTMENT;
        for (String key : AgreementTemplateService.getFieldKeys(type)) {
            contractData.put(key, "");
        }
        for (String key : AgreementTemplateService.getCheckKeys(type)) {
            contractChecks.put(key, false);
        }
    }

    /**
     * 页面初始化：根据医院UID+协议类型加载已有数据
     */
    private void init() {
        // 优先从明文URL参数读取agreementType（后备方案）
        String plainType = WebUtil.getRequestParameter("agreementType");

        // 从加密URL参数获取工单UID和协议类型
        String encodeStr = WebUtil.getRequestParameter("str");
        if (encodeStr != null) {
            this.workOrderUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "workOrderUid");
            this.agreementType = (String) UrlEncryptController.getValueFromMap(encodeStr, "agreementType");
            this.lendingHospitalUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "lendingHospitalUid");
            this.borrowingHospitalUid = (String) UrlEncryptController.getValueFromMap(encodeStr, "borrowingHospitalUid");
        }

        // 如果加密参数未解析出agreementType，使用明文参数作为后备
        if (StringUtils.isEmpty(this.agreementType) && StringUtils.isNotEmpty(plainType)) {
            this.agreementType = plainType;
            System.out.println("[AgreementEditController] agreementType 从明文参数获取: " + plainType);
        }

        if (StringUtils.isEmpty(this.agreementType)) {
            this.agreementType = AdjustmentAgreementData.TYPE_ADJUSTMENT;
        }

        System.out.println("[AgreementEditController] 最终 agreementType=" + this.agreementType
                + ", str参数=" + (encodeStr != null ? "有" : "无")
                + ", 明文参数=" + (plainType != null ? plainType : "无"));

        // 按 hospital_uid + agreement_type 查询已有数据（模板复用）
        String hospitalUid = currentUserAccount.getHospitalUID();
        currentData = agreementTemplateService.findAgreementData(hospitalUid, this.agreementType);
        System.out.println("[AgreementEditController] 查询结果: " + (currentData != null ? "有数据" : "无数据"));

        if (currentData != null && StringUtils.isNotEmpty(currentData.getTemplateData())) {
            // 反序列化JSON到表单字段
            loadFormData(currentData.getTemplateData());
            this.lendingHospitalUid = currentData.getLendingHospitalUid();
            this.borrowingHospitalUid = currentData.getBorrowingHospitalUid();
        }
    }

    /**
     * 从JSON字符串加载表单数据
     */
    private void loadFormData(String json) {
        this.partyAName = AgreementTemplateService.parseStringField(json, "partyAName");
        this.partyACreditCode = AgreementTemplateService.parseStringField(json, "partyACreditCode");
        this.partyARepresentative = AgreementTemplateService.parseStringField(json, "partyARepresentative");
        this.partyAPhone = AgreementTemplateService.parseStringField(json, "partyAPhone");
        this.partyBName = AgreementTemplateService.parseStringField(json, "partyBName");
        this.partyBCreditCode = AgreementTemplateService.parseStringField(json, "partyBCreditCode");
        this.partyBRepresentative = AgreementTemplateService.parseStringField(json, "partyBRepresentative");
        this.partyBPhone = AgreementTemplateService.parseStringField(json, "partyBPhone");
        this.partyCRepresentative = AgreementTemplateService.parseStringField(json, "partyCRepresentative");
        this.partyCPhone = AgreementTemplateService.parseStringField(json, "partyCPhone");
        // 加载合同条款数据
        Map<String, String> loadedContractData = AgreementTemplateService.parseContractData(json);
        for (Map.Entry<String, String> entry : loadedContractData.entrySet()) {
            contractData.put(entry.getKey(), entry.getValue());
        }
        // 加载复选框数据
        Map<String, Boolean> loadedChecks = AgreementTemplateService.parseContractChecks(json);
        for (Map.Entry<String, Boolean> entry : loadedChecks.entrySet()) {
            contractChecks.put(entry.getKey(), entry.getValue());
        }
        // 同步快照，防止首次点击时误判
        previousContractChecks = new HashMap<>(contractChecks);
    }

    /**
     * 保存协议数据（覆盖更新）
     */
    public void saveAgreementData() {
        try {
            String hospitalUid = currentUserAccount.getHospitalUID();
            String json = agreementTemplateService.buildFormDataJson(
                    partyAName, partyACreditCode, partyARepresentative, partyAPhone,
                    partyBName, partyBCreditCode, partyBRepresentative, partyBPhone,
                    partyCName, partyCCreditCode, partyCRepresentative, partyCPhone,
                    contractData, contractChecks);

            currentData = agreementTemplateService.saveAgreementData(
                    currentData, hospitalUid, this.agreementType,
                    this.lendingHospitalUid, this.borrowingHospitalUid,
                    json, currentUserAccount.getUid());

            WebUtil.addSuccessMessage("协议数据保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("协议数据保存失败: " + e.getMessage());
        }
    }

    /**
     * 下载协议文件：使用poi-tl将JSON数据渲染到Word模板
     */
    public void downloadAgreement() {
        try {
            // 构建poi-tl渲染数据
            Map<String, Object> renderData = agreementTemplateService.buildRenderData(
                    partyAName, partyACreditCode, partyARepresentative, partyAPhone,
                    partyBName, partyBCreditCode, partyBRepresentative, partyBPhone,
                    partyCName, partyCCreditCode, partyCRepresentative, partyCPhone,
                    contractData, contractChecks, this.agreementType);

            // 渲染Word文件（传入协议类型以使用对应模板）
            byte[] resultBytes = agreementTemplateService.renderAgreement(renderData, currentUserAccount, this.agreementType);
            if (resultBytes == null) {
                WebUtil.addErrorMessage("未找到协议模板文件");
                return;
            }

            // 输出下载
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.reset();
            response.setContentType("application/octet-stream");
            // 根据协议类型设置下载文件名
            String fileName = AdjustmentAgreementData.TYPE_TERMINATION.equals(this.agreementType)
                    ? "调剂终止协议.docx" : "调剂缔结协议.docx";
            response.setHeader("Content-Disposition", "attachment;filename=" +
                    java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.getOutputStream().write(resultBytes);
            response.getOutputStream().flush();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("下载协议文件失败: " + e.getMessage());
        }
    }
}

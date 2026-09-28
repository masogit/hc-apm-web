package com.ge.apm.view.hospitalAdjustment;

import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.hospitalAdjustment.AdjustmentTransferService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.view.secondment.DepartmentSecondmentProcess;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.util.CollectionUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ManagedBean
@ViewScoped
public class AdjustmentTransferApplicationDetailController extends GenericCRUDUUIDController<AdjustmentTransferApplication> implements Serializable, ServerEventInterface {
    private AdjustmentTransferApplicationRepository dao;
    private IdleEquipmentStockRepository stockRepository;

    private UserAccount currentUserAccount;


    private AssetInfoRepository assetInfoRepository;
    private OrgInfoRepository orgInfoRepository;


    private AdjustmentTransferService adjustmentTransferService;
    private AssetInfo selectedAsset;

    private BlobFileService blobService;
    private UserAccountRepository userDao;

    @Getter
    @Setter
    private OrgInfo filterDeptOrg;


    @Getter
    @Setter
    private List<V2_BlobObject> attachements;
    private AdjustmentApplicationDetailController adjustmentApplicationDetailController;

    private UserContextService userContextService;
    @Getter
    @Setter
    private List<AdjustmentTransferNodeProcess> process;
    @Getter
    @Setter
    private List<String> attachments=new ArrayList<>();
    private StreamedContent exportFile;

    private ConfigItemService configItemService;
    private SystemTemplateService systemTemplateService;
    private final String hospitalAdjustmentReturnTimeKey="hospitalAdjustmentReturnTime";

    @Getter
    @Setter
    private String returnTypeName;

    @Getter
    @Setter
    private List<V2_BlobObject> returnImages;
    @Override
    protected GenericRepositoryUUID<AdjustmentTransferApplication> getDAO() {
        return dao;
    }


    @Override
    protected void init() {
        blobService = WebUtil.getBean(BlobFileService.class);
        dao = WebUtil.getBean(AdjustmentTransferApplicationRepository.class);
        stockRepository = WebUtil.getBean(IdleEquipmentStockRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        masterDataService = WebUtil.getBean(MasterDataService.class);
        adjustmentTransferService = WebUtil.getBean(AdjustmentTransferService.class);
        adjustmentApplicationDetailController = WebUtil.getBean(AdjustmentApplicationDetailController.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        configItemService = WebUtil.getBean(ConfigItemService.class);
        systemTemplateService = WebUtil.getBean(SystemTemplateService.class);

        currentUserAccount = UserContextService.getCurrentUserAccount();
        String encodeStr = WebUtil.getRequestParameter("str");
        String actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "actionName");

        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedid");
        if(selectedId==null){
            return;
        }
        setSelected(dao.findByUid(selectedId));
        filterDeptOrg = orgInfoRepository.findByUid(selected.getBorrowDeptUid());
        attachements = blobService.findBlobObjectsByBoUid(selected.getUid());
        if(selected.getAttachments()!=null){
            attachments= JSONObject.parseArray(selected.getAttachments(),String.class);

        }
        processNode();
        // 待审批状态下，处理日期默认带出当前时间
        if(Objects.equals(selected.getStatus(), 1) && selected.getPendingDeliveryDate() == null){
            selected.setPendingDeliveryDate(new Date());
        }
        signatureId=selected.getHandlerSignature();
    }

    private void processNode() {
        process=new ArrayList<>();
        Set<String> condition = new HashSet<>();

        // 收集所有候选池UID
        condition.addAll(Arrays.asList(selected.getApplicationPendingApprovalUid().split(",")));
        if(selected.getPendingDeliveryUid()!=null){
            condition.addAll(Arrays.asList(selected.getPendingDeliveryUid().split(",")));
        }
        if(selected.getReturnPendingUid()!=null){
            condition.addAll(Arrays.asList(selected.getReturnPendingUid().split(",")));
        }
        condition.add(selected.getReturnUid());
        Set<String> collect1 = condition.stream().filter(i -> i != null).collect(Collectors.toSet());
        List<UserAccount> userAccounts = userDao.findByUidIn(collect1);
        Map<String, UserAccount> collect = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, item -> item));
        UserAccount system = new UserAccount();
        system.setName("System");
        collect.put("System",system);

        Integer status = selected.getStatus();

        // 判断是否有退回步骤
        boolean hasReturnStep = status >= 7 || (status == 6 && selected.getReturnUid() != null);

        // 步骤1: 待借用审批
        Date step1End = null;
        if(status >= 4){
            step1End = selected.getPendingDeliveryDate();
        } else if(status == 2 || status == 3){
            step1End = selected.getUpdateTime();
        }
        addNode("待借用审批", selected.getApplicationPendingApprovalUid(), selected.getCreateTime(), step1End, collect);

        if(status == 2 || status == 3){
            return;
        }

        // 步骤2: 待交付 (status >= 4)
        if(status >= 4){
            Date step2End = null;
            if(status >= 6){
                // 到货确认完成即结束，与后续退回流程无关
                step2End = selected.getDeliveryDate();
            }
            addNode("待交付", selected.getPendingDeliveryUid(), selected.getPendingDeliveryDate(), step2End, collect);
        }

        // 步骤3: 待退回审批
        if(hasReturnStep){
            Date step3End = null;
            if(status == 10 || status == 8){
                step3End = selected.getReturnApprovalDate();
            } else if(status == 6 && selected.getReturnApprovalResult() != null && selected.getReturnApprovalResult() == 2){
                step3End = selected.getReturnApprovalDate();
            }
            addNode("待退回审批", selected.getReturnPendingUid(), selected.getReturnDate(), step3End, collect);
        }

        // 步骤4: 待退回 (status == 10 或 8)
        if(status == 10 || status == 8){
            Date step4End = (status == 8) ? selected.getUpdateTime() : null;
            addNode("待退回", selected.getReturnUid(), selected.getReturnApprovalDate(), step4End, collect);
        }
    }

    private void addNode(String nodeName, String operatorUids, Date startTime, Date endTime, Map<String, UserAccount> userAccountMap){
        StringBuilder name = new StringBuilder();
        if (operatorUids != null) {
            for (String s : operatorUids.split(",")) {
                UserAccount userAccount = userAccountMap.get(s);
                if(userAccount != null){
                    name.append(userAccount.getName()).append(",");
                }
            }
            if (name.length() > 0) {
                name.setLength(name.length() - 1);
            }
        }
        AdjustmentTransferNodeProcess processNode=new AdjustmentTransferNodeProcess();
        processNode.setNodeName(nodeName);
        processNode.setOperator(name.toString());
        processNode.setOperatorTime(startTime);
        processNode.setEndTime(endTime);
        process.add(processNode);
    }



    public void initIdleEquipmentApplication() {
        IdleEquipmentStock idleEquipmentStock = selected.getIdleEquipmentStock();

        adjustmentApplicationDetailController.initDteail(idleEquipmentStock.getApplicationUid());
        adjustmentApplicationDetailController.setFieldsetName("调出单位信息");
    }



    public String statusName(Integer status) {
        if (status == null) return "";
        switch (status){
            case 1:
                return "待审批";
            case 2:
                return "审批未通过";
            case 3:
                return "已取消";
            case 4:
                return "待交付";
            case 6:
                return "已完成";
            case 7:
                return "待退回审批";
            case 10:
                return "退回中";
            case 8:
                return "已退回";
            default:
                return "";
        }
    }





    public String getUserNameByUid(String orgUid) {
        UserAccount user = userDao.getByUid(orgUid);
        return user == null ? orgUid : user.getName();
    }

    /**
     * 获取当前时间，用于到货确认页面默认显示
     */
    public Date getCurrentTime() {
        return new Date();
    }





    public List<V2_BlobObject> getAttachements() {
        if (com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(attachements)) return Lists.newArrayList();
        Set<String> validTypes = FieldValueMessageController.getFieldValueList("HospitalAdjustmentBorrowAttachmentType")
                .stream().map(I18nMessage::getMsgKey).collect(Collectors.toSet());
        List<V2_BlobObject> filtered = attachements.stream()
                .filter(att -> validTypes.contains(att.getBoSubType()))
                .collect(Collectors.toList());
        for (V2_BlobObject att : filtered) {
            att.setBoSubTypeValue(WebUtil.getFieldValueMessage("HospitalAdjustmentBorrowAttachmentType", att.getBoSubType()));
        }

        return filtered;
    }
    private MasterDataService masterDataService;

    // 签名相关
    @Getter
    @Setter
    private String signatureContent;
    @Getter
    private StreamedContent signatureContentStream;
    @Getter
    private String signatureId;

    @Getter
    private boolean newSignature = true;
    public void getSignature() {


        List<Map<String, String>> signatureList = masterDataService.getUserSignature();

        if (!CollectionUtils.isEmpty(signatureList)) {
            String lastSignatureId = signatureList.get(signatureList.size() - 1).get("signatureUid");
            this.signatureContentStream = blobService.getBlobObjectByStorageId(lastSignatureId);
            this.signatureId = lastSignatureId;
            this.newSignature = false;
        }else{
            this.newSignature = true;
        }
    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
    }

    public void saveSignatureAndSubmitApprove() throws Exception {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if(org.apache.commons.lang.StringUtils.isEmpty(this.signatureId) && (this.newSignature && org.apache.commons.lang.StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }

        if(this.newSignature && StringUtils.isNotEmpty(signatureContent)){
            try {
                String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] decode = decoder.decodeBuffer(base64);

                V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                if (null != blobObject) {
                    this.signatureId = blobObject.getObjectStorageId();
                    masterDataService.addUserSignature(this.signatureId);
                }else{
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                WebUtil.addErrorMessage("签名保存失败！");
                response.setStatus(206);
                return;
            }
        }

    }

    public UserAccount getCurrentUserAccount() {
        return currentUserAccount;
    }

    public void setCurrentUserAccount(UserAccount currentUserAccount) {
        this.currentUserAccount = currentUserAccount;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }


    public void applicationApproval() {
        if(this.signatureId==null){
            WebUtil.addWarnMessage("签名为空");
            return;
        }

        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(!Objects.equals(byUid.getStatus(), 1)){
            WebUtil.addWarnMessage("申请已经审批过");
            return;
        }
        selected.setStatus(4);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        List<String> uids = adjustmentTransferService.getAdjustmentTransferApproval(selected,true,false);

        String join = String.join(",", uids);
        selected.setPendingDeliveryUid(join);
        selected.setPendingDeliveryDate(new Date());

        selected.setApplicationActualApprovalUid(currentUserAccount.getUid());

        selected.setHandlerSignature(signatureId);
        dao.save(selected);
        adjustmentTransferService.approvalMsg(selected);
        processNode();
        WebUtil.addSuccessMessage("审批完成");

    }

    public void refuse() {
        if(this.signatureId==null){
            WebUtil.addWarnMessage("签名为空");
            return;
        }

        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(!Objects.equals(byUid.getStatus(), 1)){
            WebUtil.addWarnMessage("申请已经审批过");
            return;
        }
        selected.setHandlerSignature(signatureId);
        selected.setPendingDeliveryDate(new Date());

        adjustmentTransferService.refuse(selected,currentUserAccount);
        processNode();
        WebUtil.addSuccessMessage("审批结束");
    }
    public void cancel(){
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(!Objects.equals(byUid.getStatus(),1) && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        try {
            adjustmentTransferService.cancel(selected,currentUserAccount);
            processNode();
            WebUtil.addSuccessMessage("取消成功");

        }catch (Exception e){
            logger.error("调剂借用取消失败 error: ",e);
            WebUtil.addSuccessMessage("取消失败");
        }

    }



    public void   confirmReceipt() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(!Objects.equals(byUid.getStatus(),4)  && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        // 校验调剂缔结协议文件是否已上传
        List<V2_BlobObject> agreementFiles = getAgreementFiles();
        if (agreementFiles == null || agreementFiles.isEmpty()) {
            WebUtil.addWarnMessage("请先上传调剂缔结协议文件");
            return;
        }

        try {
            // 保存到到货确认字段（使用已有的deliveryUid/deliveryDate）
            selected.setDeliveryUid(currentUserAccount.getUid());
            selected.setDeliveryDate(new Date());
            adjustmentTransferService.confirmReceipt(selected,currentUserAccount);
            processNode();
            WebUtil.addSuccessMessage("交付成功");
        }catch (Exception e){
            logger.error("交付异常 error: ",e);
            WebUtil.addErrorMessage("交付失败");
        }
    }

    // ================= 到货确认 - 协议文件相关 =================

    @Getter
    private StreamedContent agreementTemplateFile;

    /**
     * 获取调剂缔结协议文件列表（多文件）
     */
    public List<V2_BlobObject> getAgreementFiles() {
        if (selected == null || selected.getUid() == null) return Lists.newArrayList();
        return blobService.fetchBlobObject(selected.getUid(),
                V2_BlobObject.BO_TYPE_HospitalAdjustmentContract,
                V2_BlobObject.BO_SUB_TYPE_ADJUSTMENT_AGREEMENT);
    }

    public void downloadAgreementTemplate() {
        try {
            java.io.InputStream inputStream = systemTemplateService.getTemplateInputStream(SystemTemplateEnum.HOSPITAL_ADJUSTMENT_AGREEMENT, currentUserAccount);
            if (inputStream != null) {
                agreementTemplateFile = new DefaultStreamedContent(inputStream, "application/octet-stream", "调剂缔结协议模板.docx");
            } else {
                WebUtil.addWarnMessage("未找到模板文件");
            }
        } catch (Exception e) {
            logger.error("下载协议模板文件失败 error: ", e);
            WebUtil.addErrorMessage("下载协议模板文件失败");
        }
    }

    public void handleAgreementFileUpload(FileUploadEvent event) {
        try {
            V2_BlobObject uploadedFileObject = blobService.uploadFile(event.getFile());
            if (uploadedFileObject != null) {
                uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_HospitalAdjustmentContract);
                uploadedFileObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_ADJUSTMENT_AGREEMENT);
                uploadedFileObject.setBoUuid(selected.getUid());
                blobService.saveBlobObject(uploadedFileObject);
                WebUtil.addSuccessMessage("协议文件上传成功");
            } else {
                WebUtil.addErrorMessage("协议文件上传失败");
            }
        } catch (Exception e) {
            logger.error("协议文件上传失败 error: ", e);
            WebUtil.addErrorMessage("协议文件上传失败");
        }
    }

    public void deleteAgreementFile(V2_BlobObject blobObject) {
        try {
            if (blobObject != null) {
                blobService.deleteBlobFile(blobObject);
                WebUtil.addSuccessMessage("协议文件删除成功");
            }
        } catch (Exception e) {
            logger.error("协议文件删除失败 error: ", e);
            WebUtil.addErrorMessage("协议文件删除失败");
        }
    }

    public void   equipmentReturn() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(!Objects.equals(byUid.getStatus(), 6)  || !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }


        if(StringUtils.isEmpty(selected.getReturnType())){
            WebUtil.addWarnMessage("请选择退回类型");
            return;
        }

        if(StringUtils.isEmpty(selected.getReturnReason())){
            WebUtil.addWarnMessage("请填写退回原因");
            return;
        }

        selected.setStatus(7);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setReturnDate(new Date());
        selected.setReturnUid(currentUserAccount.getUid());
        List<String> returnApprovalUserIds = adjustmentTransferService.getAdjustmentTransferApproval(selected, false, true);
        String join = String.join(",", returnApprovalUserIds);
        selected.setReturnPendingUid(join);
        dao.save(selected);

        adjustmentTransferService.returnNotifyMsg(selected);
        processNode();
        WebUtil.addSuccessMessage("退回申请已提交");
    }


    public void   beforeReturn() {
        selected.setReturnDate(new Date());
        selected.setReturnUid(currentUserAccount.getUid());
        selected.setReturnType(null);
        selected.setReturnReason(null);
        returnTypeName = null;
        returnImages = getReturnImages();
    }

    public List<V2_BlobObject> getReturnImages() {
        if (selected == null || selected.getUid() == null) return Lists.newArrayList();
        return blobService.fetchBlobObject(selected.getUid(),
                V2_BlobObject.BO_TYPE_HospitalAdjustmentContract,
                V2_BlobObject.BO_SUB_TYPE_ADJUSTMENT_RETURN_IMAGE);
    }

    public void handleReturnImageUpload(FileUploadEvent event) {
        try {
            List<V2_BlobObject> existingImages = getReturnImages();
            if (existingImages != null && existingImages.size() >= 5) {
                WebUtil.addWarnMessage("图片数量最多上传5份");
                return;
            }
            V2_BlobObject uploadedFileObject = blobService.uploadFile(event.getFile());
            if (uploadedFileObject != null) {
                uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_HospitalAdjustmentContract);
                uploadedFileObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_ADJUSTMENT_RETURN_IMAGE);
                uploadedFileObject.setBoUuid(selected.getUid());
                blobService.saveBlobObject(uploadedFileObject);
                returnImages = getReturnImages();
                WebUtil.addSuccessMessage("图片上传成功");
            } else {
                WebUtil.addErrorMessage("图片上传失败");
            }
        } catch (Exception e) {
            logger.error("退回图片上传失败 error: ", e);
            WebUtil.addErrorMessage("图片上传失败");
        }
    }

    public void deleteReturnImage(V2_BlobObject blobObject) {
        try {
            if (blobObject != null) {
                blobService.deleteBlobFile(blobObject);
                returnImages = getReturnImages();
                WebUtil.addSuccessMessage("图片删除成功");
            }
        } catch (Exception e) {
            logger.error("退回图片删除失败 error: ", e);
            WebUtil.addErrorMessage("图片删除失败");
        }
    }

    public String getReturnTypeLabel(String returnType) {
        if (returnType == null) return "";
        switch (returnType) {
            case "scrapReturn": return "报废退回（退回原单位有实际物流动作）";
            case "agreementReturn": return "协议退回（借出到期退回，退回原单位有实际物流动作）";
            case "noUsageReturn": return "无使用需求退回，借入方不需要使用，设备退回设备调剂库可由其他医院继续申请调用（线上退回，无实际物流动作）";
            default: return "";
        }
    }

    public void   returnPass() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());

        if(!Objects.equals(byUid.getStatus(), 7)  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name())  ){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        try {
            adjustmentTransferService.returnPass(selected,currentUserAccount);
            processNode();
        }catch (Exception e){
            logger.error("调剂退回审核异常 error: ",e);
            WebUtil.addErrorMessage("调剂退回审核异常 ");

        }
    }

    public void   returnRefuse() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());

        if(!Objects.equals(byUid.getStatus(), 7)  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_WORKBENCH.name())  ){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }
        try {
            adjustmentTransferService.returnRefuse(selected,currentUserAccount);
            processNode();
        }catch (Exception e){
            logger.error("调剂退回审核拒绝异常 error: ",e);
            WebUtil.addErrorMessage("调剂退回审核拒绝异常 ");

        }
    }

    /**
     * 退回确认：借入方上传调剂中止协议后完成退回，状态10→8
     */
    public void returnConfirm() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(!Objects.equals(byUid.getStatus(), 10) || !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        // 校验调剂中止协议文件是否已上传
        List<V2_BlobObject> terminationFiles = getTerminationAgreementFiles();
        if (terminationFiles == null || terminationFiles.isEmpty()) {
            WebUtil.addWarnMessage("请先上传调剂中止协议文件");
            return;
        }

        try {
            adjustmentTransferService.returnConfirm(selected, currentUserAccount);
            processNode();
            WebUtil.addSuccessMessage("退回确认成功");
        } catch (Exception e) {
            logger.error("退回确认异常 error: ", e);
            WebUtil.addErrorMessage("退回确认失败");
        }
    }

    // ================= 退回确认 - 调剂中止协议文件相关 =================

    /**
     * 获取调剂中止协议文件列表
     */
    public List<V2_BlobObject> getTerminationAgreementFiles() {
        if (selected == null || selected.getUid() == null) return Lists.newArrayList();
        return blobService.fetchBlobObject(selected.getUid(),
                V2_BlobObject.BO_TYPE_HospitalAdjustmentContract,
                V2_BlobObject.BO_SUB_TYPE_ADJUSTMENT_TERMINATION_AGREEMENT);
    }

    public void handleTerminationAgreementFileUpload(FileUploadEvent event) {
        try {
            V2_BlobObject uploadedFileObject = blobService.uploadFile(event.getFile());
            if (uploadedFileObject != null) {
                uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_HospitalAdjustmentContract);
                uploadedFileObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_ADJUSTMENT_TERMINATION_AGREEMENT);
                uploadedFileObject.setBoUuid(selected.getUid());
                blobService.saveBlobObject(uploadedFileObject);
                WebUtil.addSuccessMessage("调剂中止协议文件上传成功");
            } else {
                WebUtil.addErrorMessage("文件上传失败");
            }
        } catch (Exception e) {
            logger.error("调剂中止协议文件上传失败 error: ", e);
            WebUtil.addErrorMessage("文件上传失败");
        }
    }

    public void deleteTerminationAgreementFile(V2_BlobObject blobObject) {
        try {
            if (blobObject != null) {
                blobService.deleteBlobFile(blobObject);
                WebUtil.addSuccessMessage("文件删除成功");
            }
        } catch (Exception e) {
            logger.error("调剂中止协议文件删除失败 error: ", e);
            WebUtil.addErrorMessage("文件删除失败");
        }
    }

    public boolean returnValid() {
        // 已完成状态的工单均可发起退回
        return Objects.equals(selected.getStatus(), 6);
    }


    public boolean isBeyondConfiguredReturnTime(Date targetDate) {
        LocalDateTime targetDateTime = targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!currentDateTime.isAfter(targetDateTime)) {
            return false;
        }

        long daysDiff = ChronoUnit.DAYS.between(targetDateTime, currentDateTime);

        String configItemByKeyToString = configItemService.getConfigItemByKeyToString(hospitalAdjustmentReturnTimeKey, currentUserAccount.getInstitutionUID(), currentUserAccount.getTenantUID());

        int hospitalAdjustmentReturnTime = Integer.parseInt(configItemByKeyToString);

        return daysDiff >= hospitalAdjustmentReturnTime;
    }

    public void download()   {
        try {
            byte[] download = adjustmentTransferService.download(selected);
            exportFile= new DefaultStreamedContent(new ByteArrayInputStream(download), "application/msword", "调剂申请表.doc");

        }catch (Exception e){
            WebUtil.addErrorMessage("调剂申请表下载失败");
            logger.error("调剂申请表下载失败 error: ",e);
        }

    }


    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    /**
     * 打开编辑调剂协议页面
     */
    public void openEditAgreement() {
        String operation = "AgreementEdit.xhtml?workOrderUid=" + selected.getUid()
                + "&agreementType=ADJUSTMENT"
                + "&lendingHospitalUid=" + (selected.getStockHospitalUid() != null ? selected.getStockHospitalUid() : "")
                + "&borrowingHospitalUid=" + (selected.getBorrowHospitalUid() != null ? selected.getBorrowHospitalUid() : "");
        String encryptedPart = UrlEncryptController.encodeUrlParam(operation);
        // 额外拼接明文 agreementType 参数作为后备
        String url = encryptedPart + "&agreementType=ADJUSTMENT";
        org.primefaces.context.RequestContext.getCurrentInstance().addCallbackParam("url", url);
    }

    /**
     * 打开编辑终止协议页面
     */
    public void openEditTerminationAgreement() {
        String operation = "TerminationAgreementEdit.xhtml?workOrderUid=" + selected.getUid()
                + "&agreementType=TERMINATION"
                + "&lendingHospitalUid=" + (selected.getStockHospitalUid() != null ? selected.getStockHospitalUid() : "")
                + "&borrowingHospitalUid=" + (selected.getBorrowHospitalUid() != null ? selected.getBorrowHospitalUid() : "");
        String encryptedPart = UrlEncryptController.encodeUrlParam(operation);
        // 额外拼接明文 agreementType 参数作为后备
        String url = encryptedPart + "&agreementType=TERMINATION";
        org.primefaces.context.RequestContext.getCurrentInstance().addCallbackParam("url", url);
    }
}

package com.ge.apm.view.hospitalAdjustment;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.hospitalAdjustment.AdjustmentTransferService;
import com.ge.apm.service.remote.MasterDataService;
import com.ge.apm.view.secondment.DepartmentSecondmentProcess;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
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
import java.time.LocalDate;
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

        processNode();
        selected.setApplicationPendingApprovalDate(new Date());
        signatureId=selected.getHandlerSignature();
    }

    private void processNode() {
        process=new ArrayList<>();
        Set<String> condition = new HashSet<>();
        condition.add(selected.getApplicationPendingApprovalUid());
        condition.add(selected.getPendingDeliveryUid());
        condition.add(selected.getReturnUid());
        Set<String> collect1 = condition.stream().filter(i -> i != null).collect(Collectors.toSet());
        List<UserAccount> userAccounts = userDao.findByUidIn(collect1);
        Map<String, UserAccount> collect = userAccounts.stream().collect(Collectors.toMap(UserAccount::getUid, item -> item));
        addNode("待审批",collect.get(selected.getApplicationPendingApprovalUid()),selected.getApplicationPendingApprovalDate());
        addNode("待交付",collect.get(selected.getPendingDeliveryUid()),selected.getPendingDeliveryDate());
        addNode("设备退回",collect.get(selected.getReturnUid()),selected.getReturnDate());
    }


    private void addNode(String nodeName,UserAccount operator, Date operatorTime){
        String name = "";
        if(operator != null){
            name = operator.getName();
        };
        AdjustmentTransferNodeProcess processNode=new AdjustmentTransferNodeProcess();
        processNode.setNodeName(nodeName);
        processNode.setOperator(name);
        processNode.setOperatorTime(operatorTime);
        process.add(processNode);
    }



    public void initIdleEquipmentApplication() {
        IdleEquipmentStock idleEquipmentStock = selected.getIdleEquipmentStock();

        adjustmentApplicationDetailController.initDteail(idleEquipmentStock.getApplicationUid());

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
            case 5:
                return "已交付";
            case 6:
                return "已完成";
            case 7:
                return "退回待审批";
            case 8:
                return "已退回";

            case 9:
                return "退回审批不通过";
            default:
                return "";
        }
    }





    public String getUserNameByUid(String orgUid) {
        UserAccount user = userDao.getByUid(orgUid);
        return user == null ? orgUid : user.getName();
    }





    public List<V2_BlobObject> getAttachements() {
        if (com.baomidou.mybatisplus.toolkit.CollectionUtils.isEmpty(attachements)) return Lists.newArrayList();
        for (V2_BlobObject att : attachements) {
            att.setBoSubTypeValue(WebUtil.getFieldValueMessage("HospitalAdjustmentBorrowAttachmentType", att.getBoSubType()));
        }

        return attachements;
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
        if(byUid.getStatus()!=1){
            WebUtil.addWarnMessage("申请已经审批过");
            return;
        }
        selected.setStatus(4);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setApplicationPendingApprovalUid(currentUserAccount.getUid());
        selected.setHandlerSignature(signatureId);
        dao.save(selected);
        adjustmentTransferService.approvalMsg(selected);
        WebUtil.addSuccessMessage("审批完成");

    }

    public void refuse() {
        if(this.signatureId==null){
            WebUtil.addWarnMessage("签名为空");
            return;
        }

        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(byUid.getStatus()!=1){
            WebUtil.addWarnMessage("申请已经审批过");
            return;
        }
        selected.setStatus(2);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setApplicationPendingApprovalUid(currentUserAccount.getUid());
        selected.setHandlerSignature(signatureId);
        dao.save(selected);
    }
    public void cancel(){
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(byUid.getStatus()!=1 && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        try {
            adjustmentTransferService.cancel(selected,currentUserAccount);
            WebUtil.addSuccessMessage("取消成功");

        }catch (Exception e){
            logger.error("调剂借用取消失败 error: ",e);
            WebUtil.addSuccessMessage("取消失败");
        }

    }



    public void   confirmReceipt() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(byUid.getStatus()!=4  && !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        try {
            adjustmentTransferService.confirmReceipt(selected,currentUserAccount);
            WebUtil.addSuccessMessage("交付成功");
        }catch (Exception e){
            logger.error("交付异常 error: ",e);
            WebUtil.addErrorMessage("交付失败");
        }
    }

    public void   equipmentReturn() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());
        if(byUid.getStatus()!=5  || !selected.getApplicationUid().equals(currentUserAccount.getUid())){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        if(!returnValid()){
            WebUtil.addWarnMessage("已超过退回时间");
            return;
        }

        selected.setStatus(7);
        selected.setUpdateTime(new Date());
        selected.setUpdatedBy(currentUserAccount.getUid());
        selected.setReturnDate(new Date());
        selected.setReturnUid(currentUserAccount.getUid());

        dao.save(selected);
    }


    public void   beforeReturn() {
        selected.setReturnDate(new Date());
        selected.setReturnUid(currentUserAccount.getUid());

    }

    public void   returnPass() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());

        if(byUid.getStatus()!=7  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())  ){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }

        try {
            adjustmentTransferService.returnPass(selected,currentUserAccount);

        }catch (Exception e){
            logger.error("调剂退回审核异常 error: ",e);
            WebUtil.addErrorMessage("调剂退回审核异常 ");

        }
    }

    public void   returnRefuse() {
        AdjustmentTransferApplication byUid = dao.findByUid(selected.getUid());

        if(byUid.getStatus()!=7  ||  !userContextService.accessContainsFunctionMenu(ApmFunction.HOSPITAL_ADJUSTMENT_IDLE_EQUIPMENT_ADJUSTMENT_ACCEPTANCE.name())  ){
            WebUtil.addWarnMessage("无权操作,请检查订单状态");
            return;
        }
        try {
            adjustmentTransferService.returnRefuse(selected,currentUserAccount);

        }catch (Exception e){
            logger.error("调剂退回审核拒绝异常 error: ",e);
            WebUtil.addErrorMessage("调剂退回审核拒绝异常 ");

        }
    }

    public boolean returnValid() {
        if(selected.getStatus()!=5){
            return false;
        }
        Date pendingDeliveryDate = selected.getPendingDeliveryDate();
        if(pendingDeliveryDate==null){
            return false;
        }
        return isWithin7Days(pendingDeliveryDate);
    }


    public static boolean isWithin7Days(Date targetDate) {
        LocalDate targetLocalDate = targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentLocalDate = LocalDate.now();
        long daysDiff = Math.abs(ChronoUnit.DAYS.between(targetLocalDate, currentLocalDate));
        return daysDiff <= 7;
    }
}

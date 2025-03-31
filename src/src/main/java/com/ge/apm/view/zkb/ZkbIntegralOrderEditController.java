package com.ge.apm.view.zkb;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbIntegralOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbIntegralOrderApplyService;
import com.ge.apm.service.zkb.ZkbIntegralOrderAuditService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
public class ZkbIntegralOrderEditController extends GenericCRUDUUIDController<ZkbIntegralOrder> {

    private ZkbIntegralOrderRepository zkbIntegralOrderRepository;
    private AssetInfoRepository assetInfoRepository;
    private TenantInfoRepository tenantInfoRepository;
    private ZkbIntegralOrderApplyService zkbIntegralOrderApplyService;
    private ZkbIntegralOrderAuditService zkbIntegralOrderAuditService;
    private BlobFileService blobService;
    private UserAccount currentUser;

    @Getter@Setter
    private String actionName;
    @Getter @Setter
    private ZkbIntegralOrderStatusVo zkbIntegralOrderStatusVo;
    @Getter @Setter
    private V2_WorkOrder queryWorkFlow;
    @Getter @Setter
    private HashMap<String, Object> selectedWorkFlow;
    @Getter @Setter
    private List<HashMap<String, Object>> v2_listHashMap = new ArrayList<>();
    @Getter @Setter
    private List<ZkbContract> contractList = new ArrayList<>();
    @Getter @Setter
    private AssetInfo assetInfo;
    @Getter @Setter
    private ZkbContract selectedContract;
    String encodeStr;
    private String selectionTenantInfo;

    @Getter @Setter
    List<ZkbPartOrderCat> zkbPartOrderCatList = new ArrayList<>();

    @Getter @Setter
    List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();

    private String queryStatus;

    @Override
    protected void init() {
        super.init();
        zkbIntegralOrderRepository = WebUtil.getBean(ZkbIntegralOrderRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        zkbIntegralOrderApplyService = WebUtil.getBean(ZkbIntegralOrderApplyService.class);
        zkbIntegralOrderAuditService = WebUtil.getBean(ZkbIntegralOrderAuditService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        zkbIntegralOrderStatusVo = new ZkbIntegralOrderStatusVo();
        encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        String selectedId = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectedId");
        selectionTenantInfo = (String) UrlEncryptController.getValueFromMap(encodeStr, "selectionTenantInfo");
        queryStatus = UrlEncryptController.getValueFromMap(encodeStr, "queryStatus") == null ? "" : (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        queryWorkFlow = new V2_WorkOrder();
        queryWorkFlow.setAssetInfo(new AssetInfo());
        queryWorkFlow.setServiceRequest(new V2_ServiceRequest());
        if("Create".equals(actionName)){
            this.selected = new ZkbIntegralOrder();
            selected.setQty(1);//固定为1
            selected.setRequestDate(new Date());
            selected.setRequestUser(currentUser.getName());
            selected.setRequestPhone(currentUser.getTelephone());
            TenantInfo tenantInfo = tenantInfoRepository.findByuid(currentUser.getTenantUID());
            selected.setTenantName(tenantInfo.getName());
        }else if("Edit".equals(actionName)){
            selectionTenantInfo = currentUser.getTenantUID();
            selected = zkbIntegralOrderAuditService.queryInitData(selectedId, contractList, v2_listHashMap);
            selectedContract = zkbIntegralOrderAuditService.queryZkbContract(selected.getContractId());
            if(selected == null || StrUtil.isEmpty(selected.getId())){
                WebUtil.addErrorMessage("数据不存在！");
                return;
            }
            if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
                if(ZkbContract.ContractType.CCS.name().equals(selectedContract.getContractType()) || ZkbContract.ContractType.Biomed.name().equals(selectedContract.getContractType())){
                    v2_blobObjectList = zkbIntegralOrderAuditService.queryBlobObjectByIntegralId(selectedId);
                }
                zkbPartOrderCatList = zkbIntegralOrderApplyService.queryPartOrderCatByContract(currentUser);
                contractList = zkbIntegralOrderApplyService.queryContractByTenantUid(currentUser);
            }else{
                selectedWorkFlow = v2_listHashMap.get(0);
            }
            if(!currentUser.getTenantUID().equals(selected.getTenantUID())){
                zkbIntegralOrderStatusVo.setCanEdit(false);
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
            }
        }
        zkbIntegralOrderStatusVo.setCurrentScore(zkbIntegralOrderApplyService.queryScore(currentUser));

    }

    public void queryWorkFlowList(){
        String errMsg = "";
        if(StringUtils.isEmpty(selected.getOrderType())){
            errMsg += "请输入订单类型!\n";
        }else if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.toString().equals(selected.getOrderType())){
            if(StringUtils.isEmpty(selected.getArtificialType())){
                errMsg += "请输入机型!";
            }
        }
        if(StrUtil.isNotEmpty(errMsg)){
            WebUtil.addErrorMessage(errMsg,"");
            return;
        }
        if(queryWorkFlow == null){
            WebUtil.addErrorMessage("查询有误，请输入条件","");
            return;
        }
        if((queryWorkFlow.getAssetInfo() == null || StringUtils.isEmpty(queryWorkFlow.getAssetInfo().getSystemId())) &&
                (queryWorkFlow.getAssetInfo() == null || StringUtils.isEmpty(queryWorkFlow.getAssetInfo().getDepartNum())) &&
                (queryWorkFlow.getServiceRequest() == null || StringUtils.isEmpty(queryWorkFlow.getServiceRequest().getSrNum()))) {
            WebUtil.addErrorMessage("查询有误，请输入条件","");
            return;
        }
        v2_listHashMap = zkbIntegralOrderApplyService.getQueryWorkFlowList(queryWorkFlow,selectionTenantInfo);
        contractList = new ArrayList<>();
    }

    public void workFlowSelected(SelectEvent event){

        if(v2_listHashMap ==null || v2_listHashMap.size() == 0 || v2_listHashMap.get(0) ==null || event.getObject() == null ){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "未查询出合同数据！", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }
        selectedWorkFlow = (HashMap<String, Object>) event.getObject();
        assetInfo = assetInfoRepository.findByUid(String.valueOf(selectedWorkFlow.get("assetId")));
        selected.setProductNumber(assetInfo.getFunctionType());
        //验证设备状态
        if (StringUtils.isNotBlank(zkbIntegralOrderApplyService.checkAsset(assetInfo))) {
            WebUtil.addErrorMessage(zkbIntegralOrderApplyService.checkAsset(assetInfo),"");
            return;
        }
        contractList = zkbIntegralOrderApplyService.queryContractByAsset(selectionTenantInfo,String.valueOf(selectedWorkFlow.get("assetId")),(Date)selectedWorkFlow.get("requestTime"));
        String resultString  = zkbIntegralOrderApplyService.workFlowCheck(selected, contractList, assetInfo, currentUser, zkbIntegralOrderStatusVo);
        if(StrUtil.isNotEmpty(resultString)){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resultString, "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            contractList = new ArrayList<>();
            return;
        }
        selectedContract = contractList.get(0);
        zkbIntegralOrderApplyService.workFlowSelectedHandle(selected, selectedWorkFlow, selected.getOrderType(), assetInfo, selectedContract, currentUser);

    }

    public void orderTypeChange(){
        //重新校验前先把状态重置
        zkbIntegralOrderStatusVo.setScoreCanApply(true);
        Integer deductScore = 0;
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            deductScore = 900;
        }else {
            deductScore = 200;
        }
        if(zkbIntegralOrderStatusVo.getCurrentScore() < deductScore){
            zkbIntegralOrderStatusVo.setScoreCanApply(false);
            WebUtil.addErrorMessage("当前申请需要扣除" +deductScore+ "，积分不够！");
        }
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
           /* List<ZkbIntegralOrder> zkbIntegralOrders = zkbIntegralOrderApplyService.queryIntegralOrderByCreatedBy(currentUser);
            if(CollectionUtils.isNotEmpty(zkbIntegralOrders) && zkbIntegralOrders.size() >=1){
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
                WebUtil.addErrorMessage("超过可申请数量，申请失败！");
                return;
            }*/

            List<ZkbIntegralOrder> zkbIntegralOrders = zkbIntegralOrderApplyService.queryIntegralOrderByTenantThreeYear(currentUser);
            if(zkbIntegralOrders.size() >= 2){
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
                WebUtil.addErrorMessage("超过可申请数量，申请失败！");
                return;
            }
            if(CollectionUtils.isEmpty(zkbPartOrderCatList)){
                zkbPartOrderCatList = zkbIntegralOrderApplyService.queryPartOrderCatByContract(currentUser);
            }
           /* String rs = zkbIntegralOrderApplyService.checkScoreFlagValid(selectedContract, selected.getOrderType(), currentUser);
            if(StrUtil.isNotEmpty(rs)){
                WebUtil.addErrorMessage("积分申请不符合条件:"+rs);
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
            }*/
            selectedContract = null;
            contractList = zkbIntegralOrderApplyService.queryContractByTenantUid(currentUser);
        }else{
            selectedContract = null;
            contractList = new ArrayList<>();
        }
    }

    public void contractChange(ValueChangeEvent event){
        selectedContract = contractList.stream().filter(t -> t.getId().equals(event.getNewValue())).findAny().get();
        if(selectedContract != null){
            selected.setContractNo(selectedContract.getContractNo());
        }
    }


    public String showBodyForm(){
        if(StrUtil.isEmpty(selected.getOrderType())){
            return "0";
        }else if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            return "1";
        }else {
            if(CollectionUtils.isNotEmpty(contractList) && contractList.size() == 1){
                return "2";
            }else{
                return "0";
            }
        }
    }

    public void preSave(){
        if(!zkbIntegralOrderApplyService.checkScore(currentUser, selected)){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        if(!zkbIntegralOrderApplyService.checkSave(selected, v2_blobObjectList, selectedContract, selectedWorkFlow)){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        if(!zkbIntegralOrderApplyService.checkLogic(selected, currentUser,selectedContract)){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        zkbIntegralOrderApplyService.preSave(selected, zkbIntegralOrderStatusVo);
    }

    public void tempSave(){
        if(StrUtil.isEmpty(selected.getId())){
            if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
                selected.setWorkOrderId(selectedWorkFlow.get("id").toString());
                selected.setAssetId(selectedWorkFlow.get("assetId").toString());
                selected.setSrNum(selectedWorkFlow.get("srNum").toString());
                selected.setContractId(selectedContract.getId());
                selected.setContractNo(selectedContract.getContractNo());
            }
            selected.setTenantUID(currentUser.getTenantUID());
        }
        zkbIntegralOrderApplyService.tempSave(selected, currentUser, selectedContract, v2_blobObjectList);
        WebUtil.addSuccessMessage("保存成功！");
    }

    public String saveData(){
        if(StrUtil.isEmpty(selected.getId())){
            if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
                selected.setWorkOrderId(selectedWorkFlow.get("id").toString());
                selected.setAssetId(selectedWorkFlow.get("assetId").toString());
                selected.setSrNum(selectedWorkFlow.get("srNum").toString());
                selected.setContractId(selectedContract.getId());
                selected.setContractNo(selectedContract.getContractNo());
            }
            selected.setTenantUID(currentUser.getTenantUID());
        }

        zkbIntegralOrderApplyService.create(selected, currentUser, selectedContract, v2_blobObjectList);
        WebUtil.addSuccessMessage("提交成功！");
        return "List?faces-redirect=true&queryStatus="+queryStatus;

    }

    public boolean isShowRegionalManager(){
        boolean isShowRegionalManager = true;
        if(!"ToolBox".equals(selected.getOrderType())){
            if("2".equals(selected.getArtificialType())){
                isShowRegionalManager = false;
            }
        }
        return isShowRegionalManager;
    }

    public String backList(){
        return "List?faces-redirect=true&queryStatus="+queryStatus ;
    }

    public void handleFileUpload(FileUploadEvent event){
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            //blobObject.setBoId(selected.getId());
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_INTEGRAL_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_INTEGRAL_ORDER);
            v2_blobObject.setBoUuid(selected.getId());
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void removeFile(V2_BlobObject v2BlobObject){
        v2_blobObjectList.remove(v2BlobObject);
        blobService.deleteBlobFile(v2BlobObject);
    }

    @Override
    protected GenericRepositoryUUID<ZkbIntegralOrder> getDAO() {
        return zkbIntegralOrderRepository;
    }
}

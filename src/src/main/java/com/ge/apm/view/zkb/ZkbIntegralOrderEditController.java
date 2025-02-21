package com.ge.apm.view.zkb;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbIntegralOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.zkb.ZkbIntegralOrderApplyService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
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

    private String queryStatus;

    @Override
    protected void init() {
        super.init();
        zkbIntegralOrderRepository = WebUtil.getBean(ZkbIntegralOrderRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        zkbIntegralOrderApplyService = WebUtil.getBean(ZkbIntegralOrderApplyService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        zkbIntegralOrderStatusVo = new ZkbIntegralOrderStatusVo();
        encodeStr = WebUtil.getRequestParameter("str");
        actionName = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
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
            List<ZkbIntegralOrder> zkbIntegralOrders = zkbIntegralOrderApplyService.queryIntegralOrderByCreatedBy(currentUser);
            if(CollectionUtils.isNotEmpty(zkbIntegralOrders) && zkbIntegralOrders.size() >=1){
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
                WebUtil.addErrorMessage("超过可申请数量，申请失败！");
                return;
            }

            zkbIntegralOrders = zkbIntegralOrderApplyService.queryIntegralOrderByTenantThreeYear(currentUser);
            if(zkbIntegralOrders.size() >= 2){
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
                WebUtil.addErrorMessage("超过可申请数量，申请失败！");
                return;
            }
            if(CollectionUtils.isEmpty(zkbPartOrderCatList)){
                zkbPartOrderCatList = zkbIntegralOrderApplyService.queryPartOrderCatByContract(currentUser);
            }
            if(!zkbIntegralOrderApplyService.checkScoreFlagValid(selectedContract, selected.getOrderType(), currentUser)){
                WebUtil.addErrorMessage("积分申请不符合条件，请检查！");
                zkbIntegralOrderStatusVo.setScoreCanApply(false);
            }

            contractList = zkbIntegralOrderApplyService.queryContractByTenantUid(currentUser);
        }
    }

    public void contractChange(ValueChangeEvent event){
        ZkbContract zkbContract = contractList.stream().filter(t -> t.getId().equals(event.getNewValue())).findAny().get();
        if(zkbContract != null){
            selected.setContractNo(zkbContract.getContractNo());
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
        boolean checkFlag = true;
        Integer score = zkbIntegralOrderApplyService.queryScore(currentUser);
        Integer deductScore = 0;
        if(ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            deductScore = 900;
        }else {
            deductScore = 200;
        }
        if(score < deductScore){
            WebUtil.addErrorMessage("当前申请需要扣除" +deductScore+ "，积分不够！", "");
            checkFlag = false;
        }

        if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            if(StrUtil.isEmpty(selected.getArtificialType())){
                WebUtil.addErrorMessage("机型不能为空！");
                checkFlag = false;
            }
        }

        if(!checkFlag){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        zkbIntegralOrderApplyService.preSave(selected, zkbIntegralOrderStatusVo);
    }


    public String saveData(){
        if(!ZkbIntegralOrder.OrderTypeEnum.ToolBox.name().equals(selected.getOrderType())){
            selected.setWorkOrderId(selectedWorkFlow.get("id").toString());
            selected.setAssetId(selectedWorkFlow.get("assetId").toString());
            selected.setSrNum(selectedWorkFlow.get("srNum").toString());
            selected.setContractId(selectedContract.getId());
            selected.setContractNo(selectedContract.getContractNo());
        }

        selected.setTenantUID(currentUser.getTenantUID());
        zkbIntegralOrderApplyService.create(selected, currentUser, selectedContract);
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


    @Override
    protected GenericRepositoryUUID<ZkbIntegralOrder> getDAO() {
        return zkbIntegralOrderRepository;
    }
}

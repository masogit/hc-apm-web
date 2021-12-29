package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbContractAssetService;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderDetailController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    /**
     * 智康宝liucheng service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * 智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 合同信息
     */
    private ZkbContractRepository zkbContractRepository;

    /**
     * 合同列表
     */
    private List<ZkbContract> contractList;

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;

    /**
     * 当前用户称
     */
    private String userName;

    /**
     * 当前用户UID
     */
    private String userUid;

    /**
     * 当前用户tenantUid
     */
    private String tenantUID = "";

    /**
     * 当前用户登录名
     */
    private String loginName = "";

    /**
     * 默认合同错误
     */
    private Boolean contractError = false;

    /**
     * 主键
     */
    private String id;

    /**
     * 查询状态
     */
    private String queryStatus;

    /**
     * 当前用户在config中的配置权限
     */
    private Map<String, String> configTypeMap = new HashMap<>();

    private Map<String, String> i18Map;

    private ZkbContractAssetService zkbContractAssetService;

    /**
     *  提示信息confirm message
     */
    private String confirmMessage;
    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName = currentUser.getLoginName();
        this.userName = currentUser.getName();
        this.userUid = currentUser.getUid();
        this.tenantUID = currentUser.getTenantUID();
        String encodeStr = WebUtil.getRequestParameter("str");
        String action = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        this.id = (String) UrlEncryptController.getValueFromMap(encodeStr, "id");
        this.queryStatus = (String) UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        try {
            if (ZkbFePartOrderController.CREATE_ACTION.equals(action)) {
                this.prepareCreate();
            } else if (ZkbFePartOrderController.EDIT_ACTION.equals(action)) {
                this.prepareEdit();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        configTypeMap = this.zkbFePartOrderService.getMapConfigByLoginName(this.loginName);
        this.i18Map = this.zkbFePartOrderService.getI18MessageMap(ZkbFePartOrderService.status);
        zkbContractAssetService = WebUtil.getBean(ZkbContractAssetService.class);
    }

    @Override
    protected GenericRepositoryUUID<ZkbFePartOrder> getDAO() {
        return this.zkbFePartOrderRepository;
    }

    public List<ZkbContract> getContractList() {
        return contractList;
    }

    public void setContractList(List<ZkbContract> contractList) {
        this.contractList = contractList;
    }

    /**
     * 添加新数据前，初始化
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.zkbFePartOrderService.initNewOrder(this.selected);
        this.selected.setReportUser(this.userName);
        this.selected.setOrderType(ZkbFePartOrder.OrderType.ManMade.toString());
        this.selected.setCurrentStep(ZkbFePartOrder.workFlowSteps.Requested.toString());
        this.zkbFePartOrderService.initTestStatus(this.loginName, this.selected);
    }

    /**
     * 编辑数据前,初始化
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected = this.zkbFePartOrderRepository.findById(this.id);
        this.contractList = this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        this.confirmMessage =this.genConfirmMessage();
    }

    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * KB - ZKB FE 申请与GE FE审批需要弹窗显示剩余名额
     */
    public String genConfirmMessage(){
        String confirmMessage = new String();
        ZkbContract zct = this.contractList.get(0);
        ZkbFePartOrder zfpo = this.selected;
        if("ManMade".equals(this.selected.getOrderType())){//人工  新逻辑扣减     //  旧逻辑扣联合名额
            if("false".equals(this.selected.getSubtractTotalStatus())){
                confirmMessage += "本次申请不扣除名额！";
            }else{
                if ("DI".equals(zct.getContractType())
                        || "PCB".equals(zct.getContractType())
                        || "PCB MVS".equals(zct.getContractType())) {
                    confirmMessage += "本次扣除备件+人工+保养名额：1"+"；";
                    String spareArtificail = zct.getSpareArtificail()!= null?zct.getSpareArtificail().toString():"0";
                    confirmMessage += "备件+人工+保养剩余名额："+spareArtificail;
                } else if ("CCS".equals(zct.getContractType())
                        || "Biomed".equals(zct.getContractType())) {
                    confirmMessage += "本次扣除人工支持名额：1"+"；";
                    String geArtificialSupportTimes = zct.getGeArtificialSupportTimes()!= null?zct.getGeArtificialSupportTimes().toString():"0";
                    confirmMessage += "人工支持剩余次数："+geArtificialSupportTimes;
                }
            }
        }
        return confirmMessage.toString();
    }

    /**
     * 根据systemId填充设备合同信息
     */
    public void fullInfoBySystemId(String systemId) {
        if (StringUtils.isBlank(systemId)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdError"));
            return;
        }
        this.zkbFePartOrderService.fullInfoBySystemId(this.selected, systemId);
        this.contractList = this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        this.contractError = false;
        //未匹配到合同
        if (CollectionUtils.isEmpty(this.contractList)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_notFindContractError"));
            this.contractError = true;
            return;
        }
        Boolean fiveNineNine = zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            contractError = true;
        }
    }

    public String backList() {
        return this.zkbFePartOrderService.backList(this.queryStatus);
    }

    public String saveAndToListPage() {
        if (StringUtils.isBlank(this.selected.getContractId())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractCannotBeNull"));
            return "";
        }
        //检查合同号和systemId是否一致
        Boolean consist = this.zkbFePartOrderService.checkSystemIdAndContract(this.selected);
        if (!consist) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSystemIdError"));
            return "";
        }
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            return "";
        }
        if ("true".equals(this.selected.getSubtractTotalStatus())) {
            if ("DI".equals(zkbContract.getContractType())
                    || "PCB".equals(zkbContract.getContractType())
                    || "PCB MVS".equals(zkbContract.getContractType())) {
                if (zkbContract.getSpareArtificail() == null || zkbContract.getSpareArtificail() <= 0) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
            } else if ("CCS".equals(zkbContract.getContractType())
                    || "Biomed".equals(zkbContract.getContractType())) {
                if (zkbContract.getGeArtificialSupportTimes() == null || zkbContract.getGeArtificialSupportTimes() <= 0) {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
            }
            this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract, 1, this.selected, this.currentUser, "人工申请");
        }
        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(), new Date(), null, GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        return url;
    }


    /**
     * 供应商同意
     *
     * @return
     */
    public String supplierAccept(String comment) {
        Date date = new Date();
        this.selected.setComment(comment);
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        //GE FE合同到期后400下单前增加Commercial角色进行审批
        ZkbContract zkbContract = zkbContractRepository.findById(this.selected.getContractId());
        String workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingApm.toString();
        String format = "yyyy-MM-dd";
        String dateStr = DateUtil.getFormatStrByDate(this.selected.getReportDate(), format);
        Date reportDate = DateUtil.getDateByFormatStr(dateStr, format);
        if (reportDate.compareTo(zkbContract.getStartDate()) < 0 || reportDate.compareTo(zkbContract.getEndDate()) > 0){
            workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString();
        }
        String result = this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
                workFlowStep, date, null, GatewayURL.FE_PART_ORDER_SEND_EMAIL);
        //注意：流程为WaitingCommercial审批时需要发微信、短信、邮件通知
        if(workFlowStep.equals(ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString())){
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        }
        return result;
    }

    public boolean checkContractDate() {
        return this.zkbFePartOrderService.checkContractDate(this.contractList);
    }

    /**
     * 供应商拒绝同意
     *
     * @return
     */
    public String supplierReject() {
        Date date = new Date();
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract, 3, this.selected, this.currentUser, this.i18Map.get("TenantReject"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), date, date, GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * Commercial同意
     *
     * @return
     */
    public String commercialAccept(String comment) {
        Date date = new Date();
        this.selected.setCommercialComment(comment);
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.CommercialAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingApm.toString(), date, null, GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * Commercial拒绝
     *
     * @return
     */
    public String commercialReject() {
        Date date = new Date();
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract, 3, this.selected, this.currentUser, this.i18Map.get("CommercialReject"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.CommercialReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), date, date, GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * 400团队同意
     *
     * @return
     */
    public String fullApmInfo() {

        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract, 2, this.selected, this.currentUser, this.i18Map.get("FourHundredsAccept"));
        Date date = new Date();
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected, this.userUid, this.userName, this.tenantUID, this.queryStatus, ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(), date, date, GatewayURL.FE_PART_ORDER_SEND_WECHART);
    }

    /**
     * 判断当前用户在当前节点是否有编辑权限
     *
     * @return
     */
    public Boolean hasEditRight(String fieldsetType) {
        return this.zkbFePartOrderService.hasEditRight(fieldsetType, this.selected, this.configTypeMap, ZkbFePartOrder.manmadeStepAndRoles);
    }

    /**
     * 判断是否隐藏还是显示
     *
     * @param fieldsetType
     * @return
     */
    public Boolean checkRendered(String fieldsetType) {
        return this.zkbFePartOrderService.checkRendered(fieldsetType, this.selected, this.configTypeMap, ZkbFePartOrder.manmadeStepAndRoles);
    }

    public Boolean checkRendered() {
        if (StringUtils.isNotBlank(this.selected.getGeEngineerName()) && this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString())) {
            return true;
        }
        return false;
    }

    public Boolean getContractError() {
        return contractError;
    }

    public void setContractError(Boolean contractError) {
        this.contractError = contractError;
    }

    public String getConfirmMessage() {
        return confirmMessage;
    }

    public void setConfirmMessage(String confirmMessage) {
        this.confirmMessage = confirmMessage;
    }
}

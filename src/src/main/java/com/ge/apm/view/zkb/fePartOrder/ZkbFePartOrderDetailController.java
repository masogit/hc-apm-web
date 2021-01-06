package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.service.zkb.ZkbFePartOrderStepService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 503202755 2020/9/23
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderDetailController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    /**
     * 智康宝人工申请 service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     *智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 合同信息
     */
    private ZkbContractRepository zkbContractRepository;

    /**
     * 智康宝人工申请STEP service
     */
    private ZkbFePartOrderStepService zkbFePartOrderStepService;

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
    private String tenantUID="";

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

    /**
     * 获取审批角色配置信息
     */
    private ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository;

    /**
     * 备件申请中的人工申请
     */
    private List<ZkbFePartOrder> sparePartsZkbFePartOrder = new LinkedList<>();

//    /**
//     * 备件申请列表选中项
//     */
//    private ZkbFePartOrderAssetInfo selectZkbFePartOrderAssetInfo;

    /**
     * action动作名称
     */
    private String actionItemName;

    private static final String dateFormat = "yyyyMMddHHmmss";

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderStepService = WebUtil.getBean(ZkbFePartOrderStepService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
        this.userName=currentUser.getName();
        this.userUid=currentUser.getUid();
        this.tenantUID=currentUser.getTenantUID();
        String encodeStr = WebUtil.getRequestParameter("str");
        String action = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        this.id=(String) UrlEncryptController.getValueFromMap(encodeStr, "id");
        this.queryStatus=(String)UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        try {
            if (ZkbFePartOrderController.CREATE_ACTION.equals(action)) {
                this.prepareCreate();
            }else if(ZkbFePartOrderController.EDIT_ACTION.equals(action))
            {
                this.prepareEdit();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        configTypeMap=this.getMapConfigByLoginName();
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
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.zkbFePartOrderService.initNewOrder(this.selected);
        this.selected.setReportUser(this.userName);
        this.selected.setOrderType(ZkbFePartOrder.OrderType.ManMade.toString());
    }

    /**
     * 编辑数据前,初始化
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected=this.zkbFePartOrderRepository.findById(this.id);
        this.loadZkbContractInfo();
    }

    /**
     * 根据systemId填充设备合同信息
     */
    public void fullInfoBySystemId(String systemId)
    {
        if(StringUtils.isBlank(systemId))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdError"));
            return;
        }
        this.zkbFePartOrderService.fullInfoBySystemId(this.selected,systemId);
        this.loadZkbContractInfo();
        this.contractError=false;
        //未匹配到合同
        if(CollectionUtils.isEmpty(this.contractList))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_notFindContractError"));
            this.contractError=true;
            return;
        }
        ZkbContract zkbContract = this.contractList.get(0);
        Date startDate = zkbContract.getStartDate();
        Date endDate = zkbContract.getEndDate();
        Date date = new Date();
        //合同过期
        if(!(date.compareTo(startDate)>=0&&this.adjustDate(date).compareTo(endDate)<=0))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractOverdueError"));
            this.contractError=true;
        }
    }

    private Date adjustDate(Date date)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String lastDay = format.format(date.getTime());
        lastDay = lastDay+"000000";
        SimpleDateFormat formatter = new SimpleDateFormat(ZkbFePartOrderDetailController.dateFormat);
        Date date1 = null;
        try {
            date1 = formatter.parse(lastDay);
        } catch (ParseException e) {
            logger.info(e.getMessage());
        }
        return date1;
    }

    /**
     * 加载合同信息
     */
    public void loadZkbContractInfo()
    {
        List<ZkbContract> list = new ArrayList<>();
        if(StringUtils.isBlank(this.selected.getContractId())) {
            this.setContractList(null);
            return;
        }
        ZkbContract item = this.zkbContractRepository.findById(this.selected.getContractId());
        list.add(item);
        this.setContractList(list);
    }

    public String backList(){
        return "List.xhtml?faces-redirect=true&queryStatus="+queryStatus;
    }

    public String saveAndToListPage()
    {
        if(StringUtils.isBlank(this.selected.getContractId()))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractCannotBeNull"));
            return "";
        }
        //检查合同号和systemId是否一致
        Boolean consist =  this.zkbFePartOrderService.checkSystemIdAndContract(this.selected);
        if(!consist){
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSystemIdError"));
            return "";
        }
        if("true".equals(this.selected.getSubtractTotalStatus()))
        {
            ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
            if("DI".equals(zkbContract.getContractType())
                    ||"PCB".equals(zkbContract.getContractType()))
            {
                if(zkbContract.getSpareArtificail()==null||zkbContract.getSpareArtificail()<=0)
                {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
            }else if("CCS".equals(zkbContract.getContractType())
                    ||"Biomed".equals(zkbContract.getContractType()))
            {
                if(zkbContract.getGeArtificialSupportTimes()==null||zkbContract.getGeArtificialSupportTimes()<=0)
                {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
            }
            this.moveRemianTimeOfContract(zkbContract,1);
        }
        String url = this.wolkFlowToComplete(ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(),new Date(),null,GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        return url;
    }

    /**
     * @param zkbContract 合同信息
     * @param type 1.把剩余名额移入申请名额 2.把申请名额移入已使用名额 3.把申请名额移入剩余名额
     */
    private void moveRemianTimeOfContract(ZkbContract zkbContract,Integer type)
    {

        if(!"true".equals(this.selected.getSubtractTotalStatus())) return;
        if("DI".equals(zkbContract.getContractType())
                ||"PCB".equals(zkbContract.getContractType()))
        {
            switch(type){
                case 1 :
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()-1);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()+1);
                    break;
                case 2 :
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()-1);
                    zkbContract.setSpareArtificailUse(zkbContract.getSpareArtificailUse()+1);
                    break;
                case 3:
                    zkbContract.setSpareArtificail(zkbContract.getSpareArtificail()+1);
                    zkbContract.setSpareArtificailApply(zkbContract.getSpareArtificailApply()-1);
                    break;
            }
        }else if("CCS".equals(zkbContract.getContractType())
                ||"Biomed".equals(zkbContract.getContractType()))
        {
            switch(type){
                case 1 :
                    zkbContract.setGeArtificialSupportTimes(zkbContract.getGeArtificialSupportTimes()-1);
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply()+1);
                    break;
                case 2 :
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply()-1);
                    zkbContract.setGeArtificialSupportTimesUse(zkbContract.getGeArtificialSupportTimesUse()+1);
                    break;
                case 3:
                    zkbContract.setGeArtificialSupportTimes(zkbContract.getGeArtificialSupportTimes()+1);
                    zkbContract.setGeArtificialSupportTimesApply(zkbContract.getGeArtificialSupportTimesApply()-1);
                    break;
            }
        }
        this.zkbContractRepository.save(zkbContract);
    }

    /**
     * 供应商同意
     * @return
     */
    public String supplierAccept(String comment)
    {
        Date date = new Date();
        this.selected.setComment(comment);
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        return this.wolkFlowToComplete(ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingApm.toString(),date,null,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * 供应商拒绝同意
     * @return
     */
    public String supplierReject()
    {
        Date date = new Date();
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.moveRemianTimeOfContract(zkbContract,3);
        return this.wolkFlowToComplete(ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * 400团队同意
     * @return
     */
    public String fullApmInfo()
    {

        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.moveRemianTimeOfContract(zkbContract,2);
        Date date = new Date();
        return this.wolkFlowToComplete(ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_WECHART);
    }

    /**
     *
     * 判断当前用户在当前节点是否有编辑权限
     * @return
     */
    public Boolean hasEditRight()
    {
        if(StringUtils.isBlank(this.selected.getId()))
        {
            return true;
        }
        if(ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString().equals(this.selected.getCurrentStep())
                &&this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.Supplier.toString()))
        {
            return true;
        }else if(ZkbFePartOrder.workFlowSteps.WaitingApm.toString().equals(this.selected.getCurrentStep())
                &&this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString()))
        {
            return true;
        }
        return false;
    }
    /**
     * 根据登录账户名获取当前账户名流程配置信息
     * @return
     */
    private Map<String, String> getMapConfigByLoginName()
    {
        Map<String, String> configTypeMap = new HashMap<>();
        List<ZkbFePartOrderUserConfig> list = this.zkbFePartOrderUserConfigRepository.findByLoginName(this.loginName);
        if(CollectionUtils.isEmpty(list))
        {
            return configTypeMap;
        }
        for(ZkbFePartOrderUserConfig item:list)
        {
            if(item==null) continue;
            if(configTypeMap.containsKey(item.getConfigType())) continue;
            configTypeMap.put(item.getConfigType(),item.getConfigType());
        }
        return configTypeMap;
    }

    /**
     * @param status 流程状态
     * @param currentStep 当前流程节点
     * @param startTime 开始时间
     * @param endTime 结束事假
     * @return
     */
    public String wolkFlowToComplete(String status,String currentStep,Date startTime,Date endTime,GatewayURL gatewayURL)
    {
        this.selected.setStatus(status);
        this.selected.setCurrentStep(currentStep);
        this.zkbFePartOrderRepository.save(this.selected);
        this.zkbFePartOrderStepService.updateLatestStepEndTime(this.selected.getId(),startTime);
        this.zkbFePartOrderStepService.addNewItem(
                this.selected.getId(),
                currentStep,
                this.userUid,this.userName,
                startTime,
                endTime,
                status,
                this.tenantUID);
        this.zkbFePartOrderService.sendMessage(this.selected.getId(),gatewayURL);
        return this.backList();
    }

    /**
     * 判断当前流程是否在APM回填步骤
     * @return
     */
    public Boolean workFlowInThirdStep()
    {
        if(StringUtils.isBlank(this.selected.getCurrentStep()))
            return false;
        if(ZkbFePartOrder.workFlowSteps.WaitingApm.toString().equals(this.selected.getCurrentStep()))
        {
            return true;

        }
        return false;
    }

    /**
     * 判断当前流程是否在完成步骤
     * @return
     */
    public Boolean workFlowInFourthStep()
    {
        if(StringUtils.isBlank(this.selected.getCurrentStep()))
            return false;
        if(ZkbFePartOrder.workFlowSteps.Complete.toString().equals(this.selected.getCurrentStep()))
        {
            return true;

        }
        return false;
    }

    /**
     * 判断当前用户在当前流程是否可见AMP tap
     * @return
     */
    public Boolean hasApmInfoRight()
    {
        if(!configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.FourHundredsTeam.toString()))
        {
            return false;
        }
        if(this.workFlowInThirdStep())
        {
            return true;
        }
        if(this.workFlowInFourthStep()&&ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString().equals(this.selected.getStatus()))
        {
            return true;
        }
        return false;
    }

    /**
     * 判断当前流程是否在供应商步骤
     * @return
     */
    public Boolean workFlowInSupplierStep()
    {
        if(StringUtils.isBlank(this.selected.getCurrentStep()))
            return false;
        if(ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString().equals(this.selected.getCurrentStep()))
        {
            return true;

        }
        return false;
    }

    /**
     * 判断当前流程是否在第一步
     * @return
     */
    public Boolean workFlowInFistStep()
    {
        if(StringUtils.isBlank(this.selected.getCurrentStep()))
            return true;
        return false;
    }

    public Boolean getContractError() {
        return contractError;
    }

    public void setContractError(Boolean contractError) {
        this.contractError = contractError;
    }

//    public void getSparePartsZkbFePartOrder()
//    {
//
//        List<SearchFilter> searchFilters = new ArrayList<>();
//        if(StringUtils.isNotBlank(this.selected.getSystemId()))
//        {
//            searchFilters.add(new SearchFilter("systemId", SearchFilter.Operator.LIKE,this.selected.getSystemId()));
//        }
//        if(StringUtils.isNotBlank(this.selected.getFatherPartOrderNo()))
//        {
//            searchFilters.add(new SearchFilter("partOrderNo", SearchFilter.Operator.LIKE,this.selected.getFatherPartOrderNo()));
//        }
//        this.sparePartsZkbFePartOrder = this.zkbFePartOrderRepository.findBySearchFilter(searchFilters);
//    }
//
//    /**
//     * 申请列表预添加
//     */
//    public void prepareItemCreate(){
//        this.actionItemName = "Create";
//        this.selectZkbFePartOrderAssetInfo = new ZkbFePartOrderAssetInfo();
//    }
//
//    /**
//     * 申请列表预编辑
//     */
//    public void prepareItemEdit(){
//        this.actionItemName = "Edit";
//    }
//
//    /**
//     * 临时保存
//     */
//    public void saveZkbFePartOrderAssetInfoTemp()
//    {
//        if("Create".equals(this.actionItemName)){
//            if(this.selected.getZkbFePartOrderAssetInfoList() ==null){
//                this.selected.setZkbFePartOrderAssetInfoList(new ArrayList<>());
//            }
//            int i = this.selected.getZkbFePartOrderAssetInfoList().size();
//            this.selectZkbFePartOrderAssetInfo.setIndex(i);
//            this.selected.getZkbFePartOrderAssetInfoList().add(this.selectZkbFePartOrderAssetInfo);
//        }else{
//            this.selected.getZkbFePartOrderAssetInfoList().add(this.selectZkbFePartOrderAssetInfo.getIndex(),this.selectZkbFePartOrderAssetInfo);
//        }
//    }
}

package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbFePartOrderUserConfig;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 503202755 2020/9/23
 *
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderDetailController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    /**
     * 智康宝liucheng service
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


    private static final String dateFormat = "yyyyMMddHHmmss";

    private Map<String,String> i18Map;

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
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
        configTypeMap=this.zkbFePartOrderService.getMapConfigByLoginName(this.loginName);
        this.i18Map = this.zkbFePartOrderService.getI18MessageMap(ZkbFePartOrderService.zkbFePartOrder_status);
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
        this.selected.setCurrentStep(ZkbFePartOrder.workFlowSteps.Requested.toString());
        this.zkbFePartOrderService.initTestStatus(this.loginName,this.selected);
    }

    /**
     * 编辑数据前,初始化
     */
    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected=this.zkbFePartOrderRepository.findById(this.id);
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
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
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        this.contractError=false;
        //未匹配到合同
        if(CollectionUtils.isEmpty(this.contractList))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_notFindContractError"));
            this.contractError=true;
            return;
        }
    }

    public String backList(){
        return this.zkbFePartOrderService.backList(this.queryStatus);
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
                    ||"PCB".equals(zkbContract.getContractType())
                    ||"PCB MVS".equals(zkbContract.getContractType()))
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
            this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract,1,this.selected,this.currentUser,"人工申请");
        }
        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(),new Date(),null,GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        return url;
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
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
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
        this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract,3,this.selected,this.currentUser,this.i18Map.get("WaitingAssignment"));
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * 400团队同意
     * @return
     */
    public String fullApmInfo()
    {

        ZkbContract zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        this.zkbFePartOrderService.moveRemianTimeOfContract(zkbContract,2,this.selected,this.currentUser,this.i18Map.get("WaitingApm"));
        Date date = new Date();
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_WECHART);
    }

    /**
     *
     * 判断当前用户在当前节点是否有编辑权限
     * @return
     */
    public Boolean hasEditRight(String fieldsetType)
    {
        return this.zkbFePartOrderService.hasEditRight(fieldsetType,this.selected,this.configTypeMap,ZkbFePartOrder.manmadeStepAndRoles);
    }

    /**
     * 判断是否隐藏还是显示
     * @param fieldsetType
     * @return
     */
    public Boolean checkRendered(String fieldsetType)
    {
        return this.zkbFePartOrderService.checkRendered(fieldsetType,this.selected,this.configTypeMap,ZkbFePartOrder.manmadeStepAndRoles);
    }

    public Boolean checkRendered()
    {
        if(StringUtils.isNotBlank(this.selected.getGeEngineerName())&&this.configTypeMap.containsKey(ZkbFePartOrderUserConfig.ConfigTypeEnum.GEAdmin.toString()))
        {
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

}

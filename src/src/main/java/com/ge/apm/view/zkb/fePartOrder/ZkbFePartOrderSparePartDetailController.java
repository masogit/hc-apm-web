package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbContractAssetService;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;


/**
 * GEFE维修申请controller
 * author:503202755
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderSparePartDetailController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    /**
     * 智康宝人工申请 service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * 获取资产信息
     */
    private AssetInfoRepository assetInfoRepository;

    /**
     * 维修申请 申请设备列表
     */
    private ZkbFePartOrderAssetInfoRepository zkbFePartOrderAssetInfoRepository;

    /**
     * 维修申请归还repository
     */
    private ZkbFePartOrderReturnRepository zkbFePartOrderReturnRepository;

    /**
     *智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 合同信息
     */
    private ZkbContractRepository zkbContractRepository;

    /**
     * 备件申请中的人工申请列表
     */
    private List<ZkbFePartOrder> sparePartsZkbFePartOrderList;

    /**
     * 备件申请当前数据
     */
    private ZkbFePartOrder selectZkbFePartOrder;

    /**
     * 备件申请列表单个保存
     */
    private ZkbFePartOrderAssetInfo selectZkbFePartOrderAssetInfo = null;

    /**
     * 备件归还选中
     */
    private ZkbFePartOrderReturn selectZkbFePartOrderReturn = null;

    /**
     * action动作名称
     */
    private String actionItemName;

    /**
     * 主键
     */
    private String id;

    /**
     * 查询状态
     */
    private String queryStatus;

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
     * 电弧号码
     */
    private String telephone;

    /**
     * 当前用户在config中的配置权限
     */
    private Map<String, String> configTypeMap = new HashMap<>();

    @Override
    protected GenericRepositoryUUID<ZkbFePartOrder> getDAO() {
        return this.zkbFePartOrderRepository;
    }

    /**
     * 标题
     */
    private String title="GEFE维修申请";

    /**
     * 坏还
     */
    public static String badReturnValue ="badReturn";

    /**
     * 最大调整数量
     */
    public Integer maxAssetQty = new Integer(0);

    private Map<String,String> i18Map;

    private ZkbContractAssetService zkbContractAssetService;

    public static final String MESSAGE_TYPE = "messageOne";

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        zkbFePartOrderAssetInfoRepository = WebUtil.getBean(ZkbFePartOrderAssetInfoRepository.class);
        zkbFePartOrderReturnRepository=WebUtil.getBean(ZkbFePartOrderReturnRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        this.userName=currentUser.getName();
        this.userUid=currentUser.getUid();
        this.tenantUID=currentUser.getTenantUID();
        this.telephone=currentUser.getTelephone();
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
        this.i18Map = this.zkbFePartOrderService.getI18MessageMap(ZkbFePartOrderService.status);
        zkbContractAssetService = WebUtil.getBean(ZkbContractAssetService.class);
    }


    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        this.zkbFePartOrderService.initNewSparePartsOrder(this.selected);
        this.selected.setUrgencyStatus(null);
        this.selected.setReportUser(this.userName);
        this.selected.setApplyPersonPhone(this.telephone);
        this.selected.setOrderType(ZkbFePartOrder.OrderType.SpareParts.toString());
        this.selected.setCurrentStep(ZkbFePartOrder.workFlowSteps.Requested.toString());
        this.zkbFePartOrderService.initTestStatus(this.loginName,this.selected);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected=this.zkbFePartOrderRepository.findById(this.id);
        this.sparePartsZkbFePartOrderList = this.zkbFePartOrderService.getSparePartsZkbFePartOrder(null,null,this.selected.getFatherPartOrderNo(),null,null);
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = this.zkbFePartOrderAssetInfoRepository.findByFePartOrderId(this.id);
        if(CollectionUtils.isNotEmpty(zkbFePartOrderAssetInfoList))
        {
            int i = 0;
            for(ZkbFePartOrderAssetInfo item:zkbFePartOrderAssetInfoList)
            {
                item.setIndex(i);
                i++;
            }
        }
        this.selected.setZkbFePartOrderAssetInfoList(zkbFePartOrderAssetInfoList);
        List<ZkbFePartOrderReturn> zkbFePartOrderReturnList = this.zkbFePartOrderReturnRepository.findByPartOrderId(this.selected.getId());
        if(CollectionUtils.isNotEmpty(zkbFePartOrderReturnList))
        {
            int i = 0;
            for(ZkbFePartOrderReturn item:zkbFePartOrderReturnList)
            {
                item.setIndex(i);
                i++;
            }
        }
        this.selected.setZkbFePartOrderReturnList(zkbFePartOrderReturnList);
        this.orderAnnotationChange();
    }

    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * KB - ZKB FE 申请与GE FE审批需要弹窗显示剩余名额
     */
    public String genConfirmMessage(String typeArge){
        Integer beforeAdjustQty = this.zkbFePartOrderAssetInfoRepository.fetchQty(this.selected.getId());
        List<ZkbFePartOrderAssetInfo> afterAdjustAssetInfoList = this.selected.getZkbFePartOrderAssetInfoList();
        Integer adjustApplyCount = new Integer(0);
        adjustApplyCount = afterAdjustAssetInfoList.stream().mapToInt(ZkbFePartOrderAssetInfo ::getAssetQty).sum();
        String confirmMessage = new String();
        String thisDeductionMessage = new String();
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if("SpareParts".equals(this.selected.getOrderType())){//备件
            if(ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType())){//高端机不扣名额
                confirmMessage += "本次申请不扣除名额！";
                thisDeductionMessage = "本次申请不扣除名额！";

            }else {
                int applyCount = new Integer(0);
                int curApplyCount = this.getApplyCount();
                if("DI".equals(zkbContract.getContractType())
                        ||"PCB".equals(zkbContract.getContractType())
                        ||"PCB MVS".equals(zkbContract.getContractType())) {
                    Integer spareArtificail = zkbContract.getSpareArtificail()!= null?zkbContract.getSpareArtificail():new Integer(0);
                    if(adjustApplyCount<beforeAdjustQty){
                        applyCount = beforeAdjustQty-adjustApplyCount;
                        spareArtificail = spareArtificail + applyCount;
                    }
                    confirmMessage += "本次扣除"+curApplyCount+"个备件+人工+保养名额；";
                    confirmMessage += "备件+人工+保养剩余名额为"+spareArtificail+"。";
                    thisDeductionMessage = "本次扣除"+curApplyCount+"个备件+人工+保养名额。";
                }else if("CCS".equals(zkbContract.getContractType())
                        ||"Biomed".equals(zkbContract.getContractType())) {
                    Integer spareQuota = zkbContract.getSpareQuota()!= null?zkbContract.getSpareQuota():new Integer(0);
                    if(adjustApplyCount<beforeAdjustQty){
                        applyCount = beforeAdjustQty-adjustApplyCount;
                        spareQuota =spareQuota + applyCount;
                    }
                    confirmMessage += "本次扣除"+curApplyCount+"个备件名额；";
                    confirmMessage += "备件剩余名额为"+spareQuota+"。";
                    thisDeductionMessage = "本次扣除"+curApplyCount+"个备件名额。";
                }
            }
        }
        return ZkbFePartOrderSparePartDetailController.MESSAGE_TYPE.equals(typeArge)?confirmMessage:thisDeductionMessage;
    }


    /**
     * 根据systemId 和 报修单号查询
     * @param systemId systemid
     * @param sr 报修单号
     */
    public void getSparePartsZkbFePartOrder(String systemId,String sr)
    {

        if (StringUtils.isNotBlank(zkbFePartOrderService.checkAsset(systemId))) {
            WebUtil.addErrorMessage(zkbFePartOrderService.checkAsset(systemId),"");
            return;
        }
        if(StringUtils.isBlank(systemId)&&StringUtils.isBlank(sr))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdAndFatherPartOrderNoError"));
            return;
        }
        this.sparePartsZkbFePartOrderList=this.zkbFePartOrderService.getSparePartsZkbFePartOrder(systemId,sr,null,null,null);
    }

    /**
     * 选择单据
     */
    public void selectedWorkFlow()
    {
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selectZkbFePartOrder.getContractId());
        this.selected = this.zkbFePartOrderService.selectedWorkFlow(this.selected,this.selectZkbFePartOrder);
        ZkbContract zkbContract =  contractList.get(0);
        AssetInfo assetInfo = assetInfoRepository.getBySystemId(this.selected.getSystemId());
        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(zkbContract,selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),assetInfo.getUid());
        if(!fiveNineNine){
            this.selected.setFatherPartOrderNo(null);
        }
    }

    /**
     * 临时保存
     */
    public void saveZkbFePartOrderAssetInfoTemp()
    {
        if(StringUtils.isNotBlank(this.selectZkbFePartOrderAssetInfo.getAssetNo()))
        {
            this.selectZkbFePartOrderAssetInfo.setAssetNo(this.selectZkbFePartOrderAssetInfo.getAssetNo().trim());
        }
        if("Create".equals(this.actionItemName)){
            if(CollectionUtils.isEmpty(this.selected.getZkbFePartOrderAssetInfoList())){
                this.selected.setZkbFePartOrderAssetInfoList(new ArrayList<>());
            }
            int i = this.selected.getZkbFePartOrderAssetInfoList().size();
            this.selectZkbFePartOrderAssetInfo.setIndex(i);
            this.selected.getZkbFePartOrderAssetInfoList().add(this.selectZkbFePartOrderAssetInfo);
        }else{
            this.selected.getZkbFePartOrderAssetInfoList().set(this.selectZkbFePartOrderAssetInfo.getIndex(),this.selectZkbFePartOrderAssetInfo);
        }
        this.selectZkbFePartOrderAssetInfo = null;
    }
    /**
     * 临时保存
     */
    public void saveReturnZkbFePartOrderAssetInfoTemp()
    {
        if("Create".equals(this.actionItemName)){
            if(CollectionUtils.isEmpty(this.selected.getZkbFePartOrderReturnList())){
                this.selected.setZkbFePartOrderReturnList(new ArrayList<>());
            }
            int i = this.selected.getZkbFePartOrderReturnList().size();
            this.selectZkbFePartOrderReturn.setIndex(i);
            this.selected.getZkbFePartOrderReturnList().add(this.selectZkbFePartOrderReturn);
        }else{
            this.selected.getZkbFePartOrderReturnList().set(this.selectZkbFePartOrderReturn.getIndex(),this.selectZkbFePartOrderReturn);
        }
        this.selectZkbFePartOrderReturn = null;
    }
    public boolean isItemDisabled(){
        if(this.selectZkbFePartOrderAssetInfo == null){
            return true;
        }else{
            return false;
        }
    }

    public boolean isReturnItemDisabled()
    {

        if(this.selectZkbFePartOrderReturn!=null&&StringUtils.isBlank(this.selectZkbFePartOrderReturn.getId()))
        {
            return false;
        }
        return true;
    }

    /**
     * 申请列表预添加
     */
    public void prepareItemCreate(){
        this.actionItemName = "Create";
        this.selectZkbFePartOrderAssetInfo = new ZkbFePartOrderAssetInfo();
    }

    /**
     * 申请列表预编辑
     */
    public void prepareItemEdit(){
        this.actionItemName = "Edit";
        if(StringUtils.isNotBlank(this.selectZkbFePartOrderAssetInfo.getId()))
        this.maxAssetQty =this.zkbFePartOrderAssetInfoRepository.findById(this.selectZkbFePartOrderAssetInfo.getId()).getAssetQty();
    }

    public void deleteItem(){
        this.selected.getZkbFePartOrderAssetInfoList().remove(this.selectZkbFePartOrderAssetInfo);
        this.selectZkbFePartOrderAssetInfo=null;
    }

    /**
     * 申请列表预添加
     */
    public void prepareReturnItemCreate(){
        this.actionItemName = "Create";
        this.selectZkbFePartOrderReturn = new ZkbFePartOrderReturn();
        this.selectZkbFePartOrderReturn.setReturnDate(new Date());
    }

    /**
     * 申请列表预编辑
     */
    public void prepareReturnItemEdit(){
        this.actionItemName = "Edit";
    }

    public void deleteReturnItem(){
        this.selected.getZkbFePartOrderReturnList().remove(this.selectZkbFePartOrderReturn);
        this.selectZkbFePartOrderReturn = null;
    }

    public String saveAndToListPage()
    {
        if(CollectionUtils.isEmpty(this.selected.getZkbFePartOrderAssetInfoList()))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_sparePartListError"));
            return "";
        }
        int applyCount = this.getApplyCount();
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        AssetInfo assetInfo = assetInfoRepository.getBySystemId(this.selected.getSystemId());
        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(zkbContract,selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),assetInfo.getUid());
        if(!fiveNineNine){
            return "";
        }
        Integer reaminApplyCount = null;
        if("DI".equals(zkbContract.getContractType())
                ||"PCB".equals(zkbContract.getContractType())
                ||"PCB MVS".equals(zkbContract.getContractType()))
        {
            reaminApplyCount=zkbContract.getSpareArtificail();
        }else if("CCS".equals(zkbContract.getContractType())
            ||"Biomed".equals(zkbContract.getContractType()))
        {
            reaminApplyCount=zkbContract.getSpareQuota();
        }
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))
        {
            if(reaminApplyCount==null||applyCount>reaminApplyCount)
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                return "";
            }
        }

        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(),new Date(),null, GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = this.selected.getZkbFePartOrderAssetInfoList();
        for(ZkbFePartOrderAssetInfo item:zkbFePartOrderAssetInfoList )
        {
            item.setFePartOrderId(this.selected.getId());
            this.zkbFePartOrderAssetInfoRepository.save(item);
        }
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))
        {
            this.zkbFePartOrderService.moveSpareQuota(1,zkbContract,applyCount,this.currentUser,this.selected,"备件申请");
        }else{
            updateContractAssetApplyNum(ZkbFePartOrder.workFlowStatus.Requested.toString(),zkbContract);
        }
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        return url;
    }

    /**
     * 获取申请数量
     * @return
     */
    private int getApplyCount()
    {
        int applyCount = 0;
        for(ZkbFePartOrderAssetInfo item:this.selected.getZkbFePartOrderAssetInfoList())
        {
            applyCount=applyCount+item.getAssetQty();
        }
        return applyCount;
    }
    public boolean checkContractDate()
    {
        return this.zkbFePartOrderService.checkContractDate(this.contractList);
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
        Integer beforeAdjustQty = this.zkbFePartOrderAssetInfoRepository.fetchQty(this.selected.getId());
        beforeAdjustQty=beforeAdjustQty==null?0:beforeAdjustQty;
        List<ZkbFePartOrderAssetInfo> afterAdjustAssetInfoList = this.selected.getZkbFePartOrderAssetInfoList();
        Integer qty = this.qty(afterAdjustAssetInfoList);
        if(qty<=0)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("申请数量合计不能为0"));
            return "";
        }
        Integer adjustApplyCount = new Integer(0);
        List<ZkbFePartOrderAssetInfo> beforeAdjustAssetList = this.zkbFePartOrderAssetInfoRepository.findByFePartOrderId(this.selected.getId());
        Map<String,ZkbFePartOrderAssetInfo> beforeAdjustAssetMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(beforeAdjustAssetList))
        {
            for(ZkbFePartOrderAssetInfo item:beforeAdjustAssetList)
            {
                beforeAdjustAssetMap.put(item.getId(),item);
            }
        }
        if(CollectionUtils.isNotEmpty(afterAdjustAssetInfoList))
        {
            for(ZkbFePartOrderAssetInfo item:afterAdjustAssetInfoList)
            {
                adjustApplyCount=adjustApplyCount+item.getAssetQty();
                ZkbFePartOrderAssetInfo beforeAdjustAssetInfoItem = beforeAdjustAssetMap.get(item.getId());
                if(beforeAdjustAssetInfoItem!=null)
                {
                    item.setBeforeAdjustAssetQty(beforeAdjustAssetInfoItem.getAssetQty());
                    this.zkbFePartOrderAssetInfoRepository.save(item);
                }
            }
        }
        if(adjustApplyCount<beforeAdjustQty&&!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))//高端机不扣名额
        {
            ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
            this.zkbFePartOrderService.moveSpareQuota(4,zkbContract,beforeAdjustQty-adjustApplyCount,this.currentUser,this.selected,this.i18Map.get("TenantAccept"));
        }
        //GE FE合同到期后400下单前增加Commercial角色进行审批
        ZkbContract zkbContract = zkbContractRepository.findById(this.selected.getContractId());
        String workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingApm.toString();
        String format = "yyyy-MM-dd";
        String dateStr = DateUtil.getFormatStrByDate(this.selected.getReportDate(), format);
        Date reportDate = DateUtil.getDateByFormatStr(dateStr, format);
        if (reportDate.compareTo(zkbContract.getStartDate()) < 0 || reportDate.compareTo(zkbContract.getEndDate()) > 0){
            workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString();
        }
        String result = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
                workFlowStep,date,null,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
        //注意：流程为WaitingCommercial审批时需要发微信、短信、邮件通知
        if(workFlowStep.equals(ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString())){
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        }
        return result;
    }
    private Integer qty(List<ZkbFePartOrderAssetInfo> afterAdjustAssetInfoList)
    {
        Integer qty = new Integer(0);
        for(ZkbFePartOrderAssetInfo item:afterAdjustAssetInfoList)
        {
            qty=qty+item.getAssetQty();
        }
        return qty;
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
        int applyCount = this.getApplyCount();
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))
        {
            this.zkbFePartOrderService.moveSpareQuota(4,zkbContract,applyCount,this.currentUser,this.selected,this.i18Map.get("TenantReject"));
        }else{
            updateContractAssetApplyNum(ZkbFePartOrder.workFlowStatus.TenantReject.toString(),zkbContract);
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * Commercial同意
     * @return
     */
    public String commercialAccept(String comment)
    {
        Date date = new Date();
        this.selected.setCommercialComment(comment);
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.CommercialAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingApm.toString(),date,null,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * Commercial拒绝
     * @return
     */
    public String commercialReject()
    {
        Date date = new Date();
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        int applyCount = this.getApplyCount();
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))
        {
            this.zkbFePartOrderService.moveSpareQuota(4,zkbContract,applyCount,this.currentUser,this.selected,this.i18Map.get("CommercialReject"));
        }else{
            updateContractAssetApplyNum(ZkbFePartOrder.workFlowStatus.CommercialReject.toString(),zkbContract);
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.CommercialReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * 400团队同意
     * @return
     */
    public String fullApmInfo()
    {
        Date date = new Date();
        List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfo = this.selected.getZkbFePartOrderAssetInfoList();
        for(ZkbFePartOrderAssetInfo item:zkbFePartOrderAssetInfo)
        {
            if(item.getAssetQty()==0)
                continue;
            if(StringUtils.isBlank(item.getOrderNo())||item.getPrice()==null)
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_assetFieldError"));
                return "";
            }
        }
        for(ZkbFePartOrderAssetInfo item:zkbFePartOrderAssetInfo)
        {
            this.zkbFePartOrderAssetInfoRepository.save(item);
        }
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))
        {
            ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
            int applyCount = this.getApplyCount();
            this.zkbFePartOrderService.moveSpareQuota(2,zkbContract,applyCount,this.currentUser,this.selected,this.i18Map.get("FourHundredsAccept"));
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString(),date,null,GatewayURL.FE_PART_ORDER_SEND_WECHART);
    }


    /**
     * 400团队同意
     * @return
     */
    public String geAdminReturn()
    {
        List<ZkbFePartOrderReturn> zkbFePartOrderReturnList = this.selected.getZkbFePartOrderReturnList();
        if(CollectionUtils.isEmpty(zkbFePartOrderReturnList)||!this.checkHasNew(zkbFePartOrderReturnList))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_returnListCannotBeNull"));
            return "";
        }
        List<ZkbFePartOrderAssetInfo> assetInfoList = this.selected.getZkbReturnFePartOrderAssetInfoList();
        Map<String,Integer> assetInfoMap = this.zkbFePartOrderService.getAssetInfoMap(assetInfoList);
        Map<String,Integer> zkbFePartOrderReturnMap = this.zkbFePartOrderService.getZkbFePartOrderReturnMap(zkbFePartOrderReturnList,this.selected);
        boolean returnComplete = true;
        for(String key:assetInfoMap.keySet())
        {
            if(!zkbFePartOrderReturnMap.containsKey(key))
            {
                returnComplete=false;
                continue;
            }
            if(zkbFePartOrderReturnMap.get(key)>assetInfoMap.get(key))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_returnTooMaxError"));
                return "";
            }
            if(zkbFePartOrderReturnMap.get(key)<assetInfoMap.get(key))
            {
                returnComplete=false;
            }

        }
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        int returnCount = this.getReturnCount(zkbFePartOrderReturnList);
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType())&&returnCount>0)
        {
            String statusType = "GEAdminAcceptPart";
            if(returnComplete)
                statusType = "GEAdminAccept";
            this.zkbFePartOrderService.moveSpareQuota(3,zkbContract,returnCount,this.currentUser,this.selected,this.i18Map.get(statusType));
        }
        for(ZkbFePartOrderReturn item:zkbFePartOrderReturnList)
        {
            item.setPartOrderId(this.selected.getId());
            this.zkbFePartOrderReturnRepository.save(item);
        }
        String workFlowStatus ="";
        String workFlowSteps ="";
        Date startDate = null;
        Date endDate = null;
        GatewayURL gatewayURL =null;
        if(returnComplete)
        {
            workFlowStatus =ZkbFePartOrder.workFlowStatus.GEAdminAccept.toString();
            workFlowSteps =ZkbFePartOrder.workFlowSteps.Complete.toString();
            startDate = new Date();
            endDate = new Date();
            gatewayURL=GatewayURL.FE_PART_ORDER_SEND_WECHART;
        }else if(returnCount>0)
        {
            workFlowStatus =ZkbFePartOrder.workFlowStatus.GEAdminAcceptPart.toString();
            workFlowSteps =ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString();
            startDate = new Date();
            endDate = null;
            gatewayURL=GatewayURL.FE_PART_ORDER_SEND_WECHART;
        }else
        {
            workFlowStatus =ZkbFePartOrder.workFlowStatus.GEAdminAcceptPart.toString();
            workFlowSteps =ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString();
            startDate = new Date();
            endDate = null;
            gatewayURL=null;
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(
                this.selected,
                this.userUid,
                this.userName,
                this.tenantUID,
                this.queryStatus,
                workFlowStatus,
                workFlowSteps,startDate,endDate,gatewayURL);
    }

    /**
     * 检查是否添加了新数据
     * @param zkbFePartOrderReturnList
     * @return
     */
    private Boolean checkHasNew(List<ZkbFePartOrderReturn> zkbFePartOrderReturnList)
    {
        ListIterator<ZkbFePartOrderReturn> listIt = zkbFePartOrderReturnList.listIterator();
        while (listIt.hasNext()) {
            ZkbFePartOrderReturn item = listIt.next();
            if(StringUtils.isBlank(item.getId()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取归还数量
     * @param zkbFePartOrderReturnList 归还列表
     * @return
     */
    public int getReturnCount(List<ZkbFePartOrderReturn> zkbFePartOrderReturnList)
    {

        int qty = 0;
        for(ZkbFePartOrderReturn item:zkbFePartOrderReturnList)
        {
            if(badReturnValue.equals(item.getReturnType())||StringUtils.isNotBlank(item.getId()))
                continue;
            qty=qty+item.getQty();
        }
        return qty;
    }


    public String backList(){
        return this.zkbFePartOrderService.backList(this.queryStatus);
    }

    /**
     *
     * 判断当前用户在当前节点是否有编辑权限
     * @return
     */
    public Boolean hasEditRight(String fieldsetType)
    {
        return this.zkbFePartOrderService.hasEditRight(fieldsetType,this.selected,this.configTypeMap,ZkbFePartOrder.stepAndRoles);
    }

    /**
     * 判断当前流程是否在第一步
     * @return
     */
    public Boolean workFlowInFistStep()
    {
        return this.zkbFePartOrderService.workFlowInFistStep(this.selected.getCurrentStep());
    }

    /**
     * 判断是否隐藏还是显示
     * @param fieldsetType
     * @return
     */
    public Boolean checkRendered(String fieldsetType)
    {
       return this.zkbFePartOrderService.checkRendered(fieldsetType,this.selected,this.configTypeMap,ZkbFePartOrder.stepAndRoles);
    }

    /**
     * 判断是否选中人工申请流程
     * @return
     */
    public boolean checkFatherPartNo()
    {
        if(StringUtils.isNotBlank(this.selected.getFatherPartOrderNo()))
        {
            return true;
        }
        return false;
    }

    private boolean showProblemOrderNumber = false;
    public void orderAnnotationChange(){
        if(ZkbFePartOrderService.normalOrderValue.equals(selected.getOrderAnnotation())){
            showProblemOrderNumber = true;
        }else{
            showProblemOrderNumber = false;
        }
    }

    /**
     * 根据备件号获取OrderNo
     * @return
     */
    public List<ZkbFePartOrderAssetInfo> getSubOrderNoList() {
        if(this.selectZkbFePartOrderReturn==null||StringUtils.isBlank(this.selectZkbFePartOrderReturn.getAssetNo()))
        {
            return null;
        }
        return this.zkbFePartOrderAssetInfoRepository.findByFePartOrderIdAndAssetNo(this.selected.getId(),this.selectZkbFePartOrderReturn.getAssetNo());
    }

    public void updateContractAssetApplyNum(String status,ZkbContract zkbContract){
        if(zkbContract == null){
            zkbContract = this.zkbContractRepository.findById(this.selected.getContractId());;
        }
        if("Y".equals(zkbContract.getFiveNineNineFlag()) && 1 == zkbContract.getYearCount()) {
            AssetInfo assetInfo = assetInfoRepository.getBySystemId(this.selected.getSystemId());
            if(assetInfo != null){
                if(status.equals(ZkbFePartOrder.workFlowSteps.Requested.toString())){
                    zkbContractAssetService.addContractAssetApplyNum(zkbContract.getId(),assetInfo.getUid());
                }else{
                    zkbContractAssetService.subtractContractAssetApplyNum(zkbContract.getId(),assetInfo.getUid());
                }
            }
        }
    }

    public List<ZkbFePartOrder> getSparePartsZkbFePartOrderList() {
        return sparePartsZkbFePartOrderList;
    }

    public void setSparePartsZkbFePartOrderList(List<ZkbFePartOrder> sparePartsZkbFePartOrderList) {
        this.sparePartsZkbFePartOrderList = sparePartsZkbFePartOrderList;
    }

    public ZkbFePartOrderAssetInfo getSelectZkbFePartOrderAssetInfo() {
        return selectZkbFePartOrderAssetInfo;
    }

    public void setSelectZkbFePartOrderAssetInfo(ZkbFePartOrderAssetInfo selectZkbFePartOrderAssetInfo) {
        this.selectZkbFePartOrderAssetInfo = selectZkbFePartOrderAssetInfo;
    }

    public ZkbFePartOrder getSelectZkbFePartOrder() {
        return selectZkbFePartOrder;
    }

    public void setSelectZkbFePartOrder(ZkbFePartOrder selectZkbFePartOrder) {
        this.selectZkbFePartOrder = selectZkbFePartOrder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ZkbContract> getContractList() {
        return contractList;
    }

    public void setContractList(List<ZkbContract> contractList) {
        this.contractList = contractList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public AssetInfoRepository getAssetInfoRepository() {
        return assetInfoRepository;
    }

    public void setAssetInfoRepository(AssetInfoRepository assetInfoRepository) {
        this.assetInfoRepository = assetInfoRepository;
    }

    public ZkbFePartOrderReturn getSelectZkbFePartOrderReturn() {
        return selectZkbFePartOrderReturn;
    }

    public void setSelectZkbFePartOrderReturn(ZkbFePartOrderReturn selectZkbFePartOrderReturn) {
        this.selectZkbFePartOrderReturn = selectZkbFePartOrderReturn;
    }

    public Map<String, String> getConfigTypeMap() {
        return configTypeMap;
    }

    public void setConfigTypeMap(Map<String, String> configTypeMap) {
        this.configTypeMap = configTypeMap;
    }

    public boolean isShowProblemOrderNumber() {
        return showProblemOrderNumber;
    }

    public void setShowProblemOrderNumber(boolean showProblemOrderNumber) {
        this.showProblemOrderNumber = showProblemOrderNumber;
    }

    public Integer getMaxAssetQty() {
        return maxAssetQty;
    }

    public void setMaxAssetQty(Integer maxAssetQty) {
        this.maxAssetQty = maxAssetQty;
    }

    public boolean hasOrderModifyRight() {
        return zkbFePartOrderService.hasOrderModifyRight(selected.getOrderType(),selected.getStatus(),configTypeMap);
    }

    public boolean hasOrderReturnModifyRight() {
        return zkbFePartOrderService.hasOrderReturnModifyRight(selected.getOrderType(),selected.getStatus(),configTypeMap);
    }

    public boolean hasAdminRight() {
        return this.zkbFePartOrderService.hasAdminRight(configTypeMap);
    }

    public void saveOrderModify(){
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        boolean flag = true;
        String errorLog = "";
        for(ZkbFePartOrderAssetInfo zkbFePartOrderAssetInfo : selected.getZkbFePartOrderAssetInfoList()){
            if(StringUtils.isEmpty(zkbFePartOrderAssetInfo.getAssetNo())){
                flag = false;
                errorLog += "备件号不能为空,";
            }
            if(StringUtils.isEmpty(zkbFePartOrderAssetInfo.getAssetDescription())){
                flag = false;
                errorLog += "配件描述不能为空,";
            }
            if(StringUtils.isEmpty(zkbFePartOrderAssetInfo.getPrice())){
                flag = false;
                errorLog += "价格不能为空,";
            }
            if(StringUtils.isEmpty(zkbFePartOrderAssetInfo.getOrderNo())){
                flag = false;
                errorLog += "Order#不能为空,";
            }
            ZkbFePartOrderAssetInfo oldItem =zkbFePartOrderAssetInfoRepository.findById(zkbFePartOrderAssetInfo.getId());
            if(oldItem != null){
                if(!oldItem.getAssetNo().equals(zkbFePartOrderAssetInfo.getAssetNo()) || !oldItem.getAssetDescription().equals(zkbFePartOrderAssetInfo.getAssetDescription())){
                    if(StringUtils.isEmpty(zkbFePartOrderAssetInfo.getSparePart())){
                        flag = false;
                        errorLog += "备件号或配件名称修改后备注不能为空,";
                    }
                }
            }
        }
        if(!flag){
            errorLog = errorLog.substring(0,errorLog.length()-1);
            WebUtil.addErrorMessage(errorLog,"");
            return;
        }
        zkbFePartOrderService.saveOrderModify(selected,currentUser);
        this.selected=this.zkbFePartOrderRepository.findById(selected.getId());
        List<ZkbFePartOrderAssetInfo> zkbFePartOrderAssetInfoList = this.zkbFePartOrderAssetInfoRepository.findByFePartOrderId(selected.getId());
        selected.setZkbFePartOrderAssetInfoList(zkbFePartOrderAssetInfoList);
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
    }

    public String saveReturnModify(){
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        String urlPage = "";
        List<ZkbFePartOrderReturn> zkbFePartOrderReturnList = this.selected.getZkbFePartOrderReturnList();
        if(CollectionUtils.isEmpty(zkbFePartOrderReturnList)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_returnListCannotBeNull"));
            return urlPage;
        }
        List<ZkbFePartOrderAssetInfo> assetInfoList = this.selected.getZkbReturnFePartOrderAssetInfoList();
        Map<String,Integer> assetInfoMap = this.zkbFePartOrderService.getAssetInfoMap(assetInfoList);
        Map<String,Integer> zkbFePartOrderReturnMap = this.zkbFePartOrderService.getZkbFePartOrderReturnMap(zkbFePartOrderReturnList,this.selected);
        for(String key:assetInfoMap.keySet()) {
            if(!zkbFePartOrderReturnMap.containsKey(key)) {
                continue;
            }
            if(zkbFePartOrderReturnMap.get(key) != assetInfoMap.get(key)) {
                WebUtil.addErrorMessage(WebUtil.getMessage("归还数量必须等于申请数量"));
                return urlPage;
            }
        }

        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType())) {
            ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
            List<ZkbFePartOrderReturn> oldZkbFePartOrderReturnList = this.zkbFePartOrderReturnRepository.findByPartOrderId(selected.getId());
            int oldReturnCount = getModifyReturnCount(oldZkbFePartOrderReturnList);
            int returnCount = getModifyReturnCount(zkbFePartOrderReturnList);
            if(returnCount > oldReturnCount){
                this.zkbFePartOrderService.moveSpareQuota(3,zkbContract,returnCount-oldReturnCount,this.currentUser,this.selected,"GE FE备件已归还信息修改【返还名额】");
            }else if(returnCount < oldReturnCount){//当前归还数量小于修改前原数量=好还变坏还,则需要补扣名额。
                this.zkbFePartOrderService.moveSpareQuota(5,zkbContract,oldReturnCount-returnCount,this.currentUser,this.selected,"GE FE备件已归还信息修改【补扣名额】");
            }
        }
        zkbFePartOrderService.saveReturnModify(selected,currentUser);
        this.selected=this.zkbFePartOrderRepository.findById(selected.getId());
        selected.setZkbFePartOrderReturnList(zkbFePartOrderReturnRepository.findByPartOrderId(selected.getId()));
//        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
        urlPage =UrlEncryptController.encodeUrlParam("Edit?action=" + ZkbFePartOrderController.EDIT_ACTION + "&id=" + this.selected.getId() + "&queryStatus=myWork");
        return urlPage;
    }

    public String cancelOrderModify(){
       return UrlEncryptController.encodeUrlParam("Edit?action=" + ZkbFePartOrderController.EDIT_ACTION + "&id=" + this.selected.getId() + "&queryStatus=myWork");
    }

    /**
     * 获取归还数量
     * @param zkbFePartOrderReturnList 归还列表
     * @return
     */
    public int getModifyReturnCount(List<ZkbFePartOrderReturn> zkbFePartOrderReturnList) {
        int qty = 0;
        for(ZkbFePartOrderReturn item:zkbFePartOrderReturnList)
        {
            if(!badReturnValue.equals(item.getReturnType())){
                qty += item.getQty();
            }
        }
        return qty;
    }

}

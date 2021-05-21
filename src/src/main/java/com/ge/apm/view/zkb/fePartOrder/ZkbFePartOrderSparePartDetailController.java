package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
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
     * 根据systemId 和 报修单号查询
     * @param systemId systemid
     * @param sr 报修单号
     */
    public void getSparePartsZkbFePartOrder(String systemId,String sr)
    {
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
    }

    /**
     * 临时保存
     */
    public void saveZkbFePartOrderAssetInfoTemp()
    {
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
        Integer reaminApplyCount = null;
        if("DI".equals(zkbContract.getContractType())
                ||"PCB".equals(zkbContract.getContractType()))
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
            this.zkbFePartOrderService.moveSpareQuota(1,zkbContract,applyCount);
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
        if(adjustApplyCount<beforeAdjustQty)
        {
            ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
            this.zkbFePartOrderService.moveSpareQuota(4,zkbContract,beforeAdjustQty-adjustApplyCount);
        }
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
        int applyCount = this.getApplyCount();
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(!ZkbFePartOrderService.highArtificialTypeValue.equals(this.selected.getArtificialType()))
        {
            this.zkbFePartOrderService.moveSpareQuota(4,zkbContract,applyCount);
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
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
            this.zkbFePartOrderService.moveSpareQuota(2,zkbContract,applyCount);
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
        List<ZkbFePartOrderAssetInfo> assetInfoList = this.selected.getZkbFePartOrderAssetInfoList();
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
            this.zkbFePartOrderService.moveSpareQuota(3,zkbContract,returnCount);
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
}

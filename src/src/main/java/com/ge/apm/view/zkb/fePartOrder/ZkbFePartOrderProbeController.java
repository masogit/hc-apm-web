package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbContract;
import com.ge.apm.domain.ZkbFePartOrder;
import com.ge.apm.domain.ZkbPartOrderCat;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * GEFE配件申请controller
 * author:503202755
 */
@ManagedBean
@ViewScoped
public class ZkbFePartOrderProbeController extends GenericCRUDUUIDController<ZkbFePartOrder> {

    /**
     * 智康宝人工申请 service service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * GE维修申请service
     */
    private ZkbPartOrderService zkbPartOrderService;

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

//    /**
//     * 故障图片上传列表暂时移除掉
//     */
//    private List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();

    /**
     * 备件申请当前数据
     */
    private ZkbFePartOrder selectZkbFePartOrder;

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
     * 探头型号列表
     */
    private List<ZkbPartOrderCat> zkbPartOrderCatList ;

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

    /**
     * 获取附件处理service
     */
    private BlobFileService blobService;

    @Override
    protected GenericRepositoryUUID<ZkbFePartOrder> getDAO() {
        return this.zkbFePartOrderRepository;
    }

    /**
     * 价值类型提示
     */
    private String valueTypeTips = "";


    /**
     * 合同日期开始时间
     */
    private static final String contractStartDate = "2021-03-01 00:00:00";


    /**
     * 合同日期开始时间格式化
     */
    private static final String contractStartDateFormat = "yyyy-MM-dd HH:mm:ss";

    private String remindLetterFlag = "0";

    /**
     * 是否显示扣除按钮
     */
    private Boolean showZkbPoProbeQuotaType = false;

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
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
        this.selected.setOrderType(ZkbFePartOrder.OrderType.Probe.toString());
        this.selected.setCurrentStep(ZkbFePartOrder.workFlowSteps.Requested.toString());
        this.selected.setCatNum(1);
        this.selected.setOrderAnnotation(ZkbFePartOrderService.normalOrderValue);
        this.orderAnnotationChange();
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected=this.zkbFePartOrderRepository.findById(this.id);
        this.sparePartsZkbFePartOrderList = this.zkbFePartOrderService.getSparePartsZkbFePartOrder(null,null,this.selected.getFatherPartOrderNo(),null,ZkbFePartOrder.OrderType.Probe.toString());
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        Date contractStartDate = this.getDateFormat(ZkbFePartOrderProbeController.contractStartDateFormat,ZkbFePartOrderProbeController.contractStartDate);
        ZkbContract item = this.contractList.get(0);
        if(item.getStartDate()!=null&&item.getStartDate().compareTo(contractStartDate)>=0)
        {
            this.showZkbPoProbeQuotaType=true;
        }
       // this.initBlobObjectList(this.selected.getId());
        this.orderAnnotationChange();
    }

    /**
     * 查询
     * @param systemId system id
     * @param sr sr
     */
    public void getSparePartsZkbFePartOrder(String systemId,String sr)
    {
        if(StringUtils.isBlank(systemId)&&StringUtils.isBlank(sr))
        {
             WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdAndFatherPartOrderNoError"));
            return;
        }
        this.sparePartsZkbFePartOrderList=this.zkbFePartOrderService.getSparePartsZkbFePartOrder(systemId,sr,null,ZkbFePartOrderService.assetGroup,ZkbFePartOrder.OrderType.Probe.toString());
    }

    /**
     * 选择单据
     */
    public void selectedWorkFlow()
    {
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selectZkbFePartOrder.getContractId());
        this.selected = this.zkbFePartOrderService.selectedWorkFlow(this.selected,this.selectZkbFePartOrder);
        this.selected.setProductNumber(this.selectZkbFePartOrder.getFunctionType());
        this.showZkbPoProbeQuotaType=false;
        this.selected.setZkbPoProbeQuotaType(null);
        if(CollectionUtils.isNotEmpty(this.contractList))
        {
            ZkbContract item = this.contractList.get(0);
            initZknPartCat(item.getStartDate());
            Date contractStartDate = this.getDateFormat(ZkbFePartOrderProbeController.contractStartDateFormat,ZkbFePartOrderProbeController.contractStartDate);
            if(item.getStartDate()!=null&&item.getStartDate().compareTo(contractStartDate)>=0)
            {
                this.showZkbPoProbeQuotaType=true;
            }
        }
        zkbPartOrderCatSelect();

    }
    public boolean hasLetterViewRight()
    {
        return this.zkbFePartOrderService.hasLetterViewRight(configTypeMap);
    }

    public boolean hasAdminRight()
    {
        return this.zkbFePartOrderService.hasAdminRight(configTypeMap);
    }

    public boolean hasSupplierRight()
    {
        return this.zkbFePartOrderService.hasSupplierRight(configTypeMap);
    }

//    /**
//     * 上传附件
//     * @param event
//     */
//    public void handleFileUpload(FileUploadEvent event){
//        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
//        if (null != v2_blobObject) {
//            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_FE_PART_ORDER);
//            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_FE_PART_ORDER);
//            v2_blobObjectList.add(v2_blobObject);
//            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
//        } else {
//            WebUtil.addErrorMessage("fail to upload file");
//        }
//    }

//    /**
//     * 初始化附件列表
//     * @param id 流程主键
//     */
//    public void initBlobObjectList(String id)
//    {
//        this.v2_blobObjectList=this.blobService.findBlobObjectsByBoUid(id);
//    }

//    /**
//     * 移除附件
//     * @param v2_blobObject
//     */
//    public void removeFile(V2_BlobObject v2_blobObject) {
//        v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());
//
//    }

    /**
     * 崔涵成功
     */
    public void remindLetter(){
        zkbFePartOrderService.sendWeChatRemindLetter(selected);
        WebUtil.addSuccessMessage("催函成功！");
        remindLetterFlag = "2";
    }

    /**
     * 提交
     * @return
     */
    public String saveAndToListPage()
    {
        calcRemainQty();
//        if(CollectionUtils.isEmpty(v2_blobObjectList))
//        {
//            WebUtil.addErrorMessage("附件不能为空！");
//            return "";
//        }
        if(this.selected.getRemainQty()<0)
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
            return "";
        }
        this.selected.setCatId(this.selected.getZkbPartOrderCat().getId());
        if(this.selected.getQty()>0)
        {
            this.zkbFePartOrderService.moveProbe(1,this.contractList.get(0),this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType());
        }
        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(),new Date(),null, GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        //this.zkbFePartOrderService.saveUploadFile(this.v2_blobObjectList,this.selected.getId());
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
        this.selected.setTenantAssignDate(date);
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
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0)
        {
            this.zkbFePartOrderService.moveProbe(4,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType());
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
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0){
            this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType());
            if("t".equals(this.selected.getNewCatFlag()))
            {
                zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
                Date contractStartDate = this.getDateFormat(ZkbFePartOrderProbeController.contractStartDateFormat,ZkbFePartOrderProbeController.contractStartDate);
                if(zkbContract.getStartDate()!=null&&zkbContract.getStartDate().compareTo(contractStartDate)==-1)
                {
                    this.zkbFePartOrderService.moveProbe(3,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType());
                    this.selected.setQty(this.getSelected().getQty()*2);
                }
            }
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
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(!ZkbFePartOrderSparePartDetailController.badReturnValue.equals(this.selected.getReturnType()))
        {
            this.zkbFePartOrderService.moveProbe(5,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType());
        }
        Date date = new Date();
        return this.zkbFePartOrderService.wolkFlowToComplete(
                this.selected,
                this.userUid,
                this.userName,
                this.tenantUID,
                this.queryStatus,
                ZkbFePartOrder.workFlowStatus.GEAdminAccept.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_WECHART);
    }

    public List<ZkbPartOrderCat> autoCompleteCat(String query){
        List<ZkbPartOrderCat> res = zkbPartOrderCatList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getCatModel().contains(query)).collect(Collectors.toList());
        return res;
    }

    public void initZknPartCat(Date contractDate){
        zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(contractDate);
    }

    public void zkbPartOrderCatSelect(){
        if(this.selected.getZkbPartOrderCat() != null){
            if(ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                this.selected.setQty(1);
                valueTypeTips = "";
            }else if(ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                this.selected.setQty(0);
                valueTypeTips = "您的选择为高值探头，请确认合同中是否有此权益！";
            }else if(ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())){
                this.selected.setQty(2);
//                if(CollectionUtils.isNotEmpty(this.contractList))
//                {
//                    ZkbContract zkbContract = this.contractList.get(0);
//                    Date contractStartDate = this.getDateFormat(ZkbFePartOrderProbeController.contractStartDateFormat,ZkbFePartOrderProbeController.contractStartDate);
//                    if(zkbContract.getStartDate()!=null&&zkbContract.getStartDate().compareTo(contractStartDate)>=0)
//                    {
//                        this.selected.setQty(1);
//                    }
//                }
                valueTypeTips = "";
            }
            if(this.showZkbPoProbeQuotaType)
            {
                this.selected.setQty(1);
            }
        }
        //calcRemainQty();
    }

    private Date getDateFormat(String format,String formatDate)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void calcRemainQty(){
        if(CollectionUtils.isEmpty(contractList))
        {
            return;
        }
        ZkbContract zkbContract = contractList.get(0);
        Integer qty = new Integer(0);
        if(showZkbPoProbeQuotaType)
        {
            if(ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                qty=zkbContract.getProbeQuota();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                qty=zkbContract.getProbeQuotaSpecial();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                qty=zkbContract.getProbeQuotaNormal();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.High.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                qty=zkbContract.getProbeQuotaHigh();
            }
        }else
            {
                qty=zkbContract.getProbeQuota();
            }
        Integer contractCount = qty == null ? 0 : qty;
        this.selected.setRemainQty(contractCount - this.selected.getQty());
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
        return this.zkbFePartOrderService.hasEditRight(fieldsetType,this.selected,this.configTypeMap,ZkbFePartOrder.probeStepAndRoles);
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
       return this.zkbFePartOrderService.checkRendered(fieldsetType,this.selected,this.configTypeMap,ZkbFePartOrder.probeStepAndRoles);
    }

    public void hasUploadLetter(){
        this.selected.setHasUploadLetter(ZkbFePartOrderController.hasUploadLetterValue);
        this.zkbFePartOrderRepository.save(selected);
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

    public List<ZkbFePartOrder> getSparePartsZkbFePartOrderList() {
        return sparePartsZkbFePartOrderList;
    }

    public void setSparePartsZkbFePartOrderList(List<ZkbFePartOrder> sparePartsZkbFePartOrderList) {
        this.sparePartsZkbFePartOrderList = sparePartsZkbFePartOrderList;
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

    public boolean isShowProblemOrderNumber() {
        return showProblemOrderNumber;
    }

    public void setShowProblemOrderNumber(boolean showProblemOrderNumber) {
        this.showProblemOrderNumber = showProblemOrderNumber;
    }

//    public List<V2_BlobObject> getV2_blobObjectList() {
//        return v2_blobObjectList;
//    }
//
//    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
//        this.v2_blobObjectList = v2_blobObjectList;
//    }

    public String getValueTypeTips() {
        return valueTypeTips;
    }

    public void setValueTypeTips(String valueTypeTips) {
        this.valueTypeTips = valueTypeTips;
    }

    public String getRemindLetterFlag() {
        return remindLetterFlag;
    }

    public void setRemindLetterFlag(String remindLetterFlag) {
        this.remindLetterFlag = remindLetterFlag;
    }

    public Map<String, String> getConfigTypeMap() {
        return configTypeMap;
    }

    public void setConfigTypeMap(Map<String, String> configTypeMap) {
        this.configTypeMap = configTypeMap;
    }

    public Boolean getShowZkbPoProbeQuotaType() {
        return showZkbPoProbeQuotaType;
    }

    public void setShowZkbPoProbeQuotaType(Boolean showZkbPoProbeQuotaType) {
        this.showZkbPoProbeQuotaType = showZkbPoProbeQuotaType;
    }
}

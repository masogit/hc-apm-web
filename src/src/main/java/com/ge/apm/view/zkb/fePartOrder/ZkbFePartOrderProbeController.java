package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbContractCatRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
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


    private String remindLetterFlag = "0";

    /**
     * 是否显示扣除按钮
     */
    private Boolean showZkbPoProbeQuotaType = false;

    /**
     * 是否显示高值探头按钮
     */
    private Boolean showZkbhighCatFlag = false;


    /**
     * 高值
     */
    private final static String[] valueHighValueType = new String[]{ZkbPartOrderCat.ValueType.HighValue.toString()};

    /**
     * 普通
     */
    private final static String[] valueNormalValueType = new String[]{ZkbPartOrderCat.ValueType.NormalValue.toString()};

    /**
     * 特殊
     */
    private final static String[] valueSpecialValueValueType = new String[]{ZkbPartOrderCat.ValueType.SpecialValue.toString()};

    /**
     * 特殊和普通
     */
    private final static String[] valueNormalAndSpecialValueType = new String[]{ZkbPartOrderCat.ValueType.NormalValue.toString(),ZkbPartOrderCat.ValueType.SpecialValue.toString()};

    private List<HashMap<String, String>> highCatList = new ArrayList<>();

    /**
     * 价值类型提示
     */
    private String noHighCatTips = "";

    private ZkbContractCatRepository zkbContractCatRepository;

    private static final String four_in_one = "fourInOne";

    /**
     * PDF上传列表
     */
    private List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        zkbContractCatRepository = WebUtil.getBean(ZkbContractCatRepository.class);
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
        this.zkbFePartOrderService.initTestStatus(this.loginName,this.selected);
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected=this.zkbFePartOrderRepository.findById(this.id);
        this.sparePartsZkbFePartOrderList = this.zkbFePartOrderService.getSparePartsZkbFePartOrder(null,null,this.selected.getFatherPartOrderNo(),null,ZkbFePartOrder.OrderType.Probe.toString());
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
//        Date contractStartDate = this.zkbFePartOrderService.getDateFormat(ZkbFePartOrderProbeController.contractStartDateFormat,ZkbFePartOrderProbeController.contractStartDate);
        ZkbContract item = this.contractList.get(0);
        if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(item.getQuotaDeductType()))
        {
            this.showZkbPoProbeQuotaType=true;
        }else
            {
                this.showZkbhighCatFlag=true;
            }
        this.orderAnnotationChange();
        initHighList(item.getId(),true);
        this.initBlobObjectList(this.selected.getId());
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
        this.showZkbhighCatFlag=false;
        this.selected.setZkbPoProbeQuotaType(null);
        if(CollectionUtils.isNotEmpty(this.contractList))
        {
            ZkbContract item = this.contractList.get(0);
            initZknPartCat();
            if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(item.getQuotaDeductType()))
            {
                this.showZkbPoProbeQuotaType=true;
            }else
                {
                    this.showZkbhighCatFlag=true;
                }
        }
        zkbPartOrderCatSelect();
        initHighList(this.selectZkbFePartOrder.getContractId(),false);
//        if(CollectionUtils.isEmpty(highCatList))
//        {
//            noHighCatTips="无可用高值探头，请确认合同信息或联系客服";
//        }else
//            {
//                noHighCatTips="";
//            }

    }

    public void initHighList(String contractId,boolean isAll){
        highCatList = zkbPartOrderService.queryHighCatByContract(contractId,isAll);
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
        if(this.checkShowSN()&&!four_in_one.equals(this.selected.getContractCatId()))
        {
            this.selected.setQty(0);
            ZkbContractCat zkbContractCat = this.zkbContractCatRepository.findById(this.selected.getContractCatId());
            if(!zkbContractCat.getEnabled())
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("选中的SN编号探头不可用"));
                return "";
            }
        }else
            {
                if(this.checkShowSN()&&four_in_one.equals(this.selected.getContractCatId()))
                {
                    this.selected.setQty(4);
                    this.selected.setZkbPoProbeQuotaType(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString());
                }
                calcRemainQty();
                if(this.selected.getRemainQty()<0)
                {
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
                if(!this.checkShowSN())
                {
                    this.selected.setContractCatId(null);
                    this.selected.setCatId(this.selected.getZkbPartOrderCat().getId());
                }
            }
        if(!this.showZkbhighCatFlag)
        {
            this.selected.setHighCatFlag(null);
        }
        if(this.selected.getQty()>0)
        {
            this.zkbFePartOrderService.moveProbe(1,this.contractList.get(0),this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true);
        }
        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(),new Date(),null, GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        if(this.checkShowSN())
        {
          this.zkbFePartOrderService.updateZkbContractCat(currentUser,false,this.selected.getContractCatId());
        }
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        return url;
    }

    public Boolean checkShowSN()
    {
        if(this.showZkbPoProbeQuotaType&&"High".equals(this.selected.getZkbPoProbeQuotaType()))
        {
            return true;
        }
        if(this.showZkbhighCatFlag&&"t".equals(this.selected.getHighCatFlag()))
        {
            return true;
        }
            return false;
    }


    /**
     * 供应商同意
     * @return
     */
    public String supplierAccept()
    {
        Date date = new Date();
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(date);
        if(CollectionUtils.isEmpty(this.contractList))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("合同信息错误"));
            return "";
        }
        ZkbContract zkbContract = this.contractList.get(0);
        if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())&& ZkbFePartOrderService.testStatusTrueValue.equals(this.selected.getNewCatFlag()))
        {
            if(CollectionUtils.isEmpty(this.v2_blobObjectList))
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("请上传院方合同证明"));
                return "";
            }
        }
        if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zkbContract.getQuotaDeductType())&&ZkbFePartOrderService.testStatusTrueValue.equals(this.selected.getNewCatFlag()))
        {
            this.calcRemainQty();
            if(this.selected.getRemainQty()<0)
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                return "";
            }
            if(this.selected.getQty()!=null&&this.selected.getQty()>0)
            {
                this.zkbFePartOrderService.moveProbe(1,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true);
                this.selected.setQty(this.selected.getQty()*2);
                this.selected.setDoubleQty(true);
            }
        }
        if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())&&ZkbFePartOrderService.testStatusTrueValue.equals(this.selected.getNewCatFlag()))
        {
            this.zkbFePartOrderService.saveUploadFile(this.v2_blobObjectList,this.selected.getId());
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingApm.toString(),date,null,GatewayURL.FE_PART_ORDER_SEND_EMAIL);
    }

    /**
     * 供应商拒绝同意
     * @return
     */
    public String supplierReject(String comment)
    {
        if(StringUtils.isBlank(comment))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("代理商审批备注能为空"));
            return "";
        }
        Date date = new Date();
        this.selected.setComment(comment);
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0)
        {
            this.zkbFePartOrderService.moveProbe(4,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true);
        }
        if(StringUtils.isNotBlank(this.selected.getContractCatId()))
        {
            this.zkbFePartOrderService.updateZkbContractCat(currentUser,true,this.selected.getContractCatId());
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
            if("t".equals(this.selected.getNewCatFlag()))
            {
                if(this.selected.getDoubleQty()!=null&&this.selected.getDoubleQty())
                {
                    this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true);
                }else
                    {
                        Boolean firstSave = true;
                        Boolean secondSave = false;
                        zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
                        if(!ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType()))
                        {
                            secondSave=true;
                            firstSave=false;
                        }
                        if(secondSave)
                        {
                            calcRemainQty();
                            if(this.selected.getRemainQty()<0)
                            {
                                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                                return "";
                            }
                        }
                        this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,firstSave);
                        if(secondSave)
                        {
                            this.zkbFePartOrderService.moveProbe(3,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,secondSave);
                            this.selected.setQty(this.getSelected().getQty()*2);
                            this.selected.setDoubleQty(true);
                        }
                    }
            }else
                {
                    if(this.selected.getDoubleQty()!=null&&this.selected.getDoubleQty())
                    {
                        this.zkbFePartOrderService.moveProbe(4,zkbContract,this.selected.getQty()/2,showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,false);
                        this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty()/2,showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true);
                        this.selected.setQty(this.selected.getQty()/2);
                        this.selected.setDoubleQty(false);
                    }else
                    {
                        this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true);
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
        return this.zkbFePartOrderService.geAdminReturn(this.selected,showZkbPoProbeQuotaType,this.userUid,this.userName,this.tenantUID,this.queryStatus,this.currentUser);
    }

    public List<ZkbPartOrderCat> autoCompleteCat(String query){
        List<ZkbPartOrderCat> res = zkbPartOrderCatList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getCatModel().contains(query)).collect(Collectors.toList());
        return res;
    }

    public void initZknPartCat(){
        ZkbContract item = this.contractList.get(0);
        if(showZkbPoProbeQuotaType&&StringUtils.isNotBlank(this.selected.getZkbPoProbeQuotaType()))
        {
            List<String> valueTypeList = null;
            if(ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                valueTypeList= Arrays.asList(valueNormalAndSpecialValueType);
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                valueTypeList= Arrays.asList(valueNormalValueType);
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                valueTypeList= Arrays.asList(valueSpecialValueValueType);
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.High.toString().equals(this.selected.getZkbPoProbeQuotaType()))
            {
                valueTypeList= Arrays.asList(valueHighValueType);
            }
            zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(item.getStartDate(),valueTypeList);
        }else
        {
            zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(item.getStartDate());
        }
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
                valueTypeTips = "";
            }
            if(this.showZkbPoProbeQuotaType)
            {
                this.selected.setQty(1);
            }
        }
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
        if(this.checkShowSN()&&four_in_one.equals(this.selected.getContractCatId()))
        {
            qty=zkbContract.getProbeQuotaNormal();
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

    /**
     * 移除附件
     * @param v2_blobObject
     */
    public void removeFile(V2_BlobObject v2_blobObject) {
        v2_blobObjectList = v2_blobObjectList.stream().filter(item -> !item.getId().equals(v2_blobObject.getId())).collect(Collectors.toList());

    }

    /**
     * 初始化附件列表
     * @param id 流程主键
     */
    public void initBlobObjectList(String id)
    {
        this.v2_blobObjectList=this.blobService.findBlobObjectsByBoUid(id);
    }

    /**
     * 上传附件
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event){
        if(CollectionUtils.isNotEmpty(v2_blobObjectList))
        {
            WebUtil.addErrorMessage("只能上传一份院方合同证明");
            return;
        }
        V2_BlobObject v2_blobObject = blobService.uploadFile(event);
        if (null != v2_blobObject) {
            v2_blobObject.setBoType(V2_BlobObject.BO_TYPE_ZKB_FE_PART_ORDER);
            v2_blobObject.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_FE_PART_ORDER);
            v2_blobObjectList.add(v2_blobObject);
            WebUtil.addSuccessMessage(v2_blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
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

    public Boolean getShowZkbhighCatFlag() {
        return showZkbhighCatFlag;
    }

    public void setShowZkbhighCatFlag(Boolean showZkbhighCatFlag) {
        this.showZkbhighCatFlag = showZkbhighCatFlag;
    }

    public List<HashMap<String, String>> getHighCatList() {
        return highCatList;
    }

    public void setHighCatList(List<HashMap<String, String>> highCatList) {
        this.highCatList = highCatList;
    }

    public String getNoHighCatTips() {
        return noHighCatTips;
    }

    public void setNoHighCatTips(String noHighCatTips) {
        this.noHighCatTips = noHighCatTips;
    }

    public List<V2_BlobObject> getV2_blobObjectList() {
        return v2_blobObjectList;
    }

    public void setV2_blobObjectList(List<V2_BlobObject> v2_blobObjectList) {
        this.v2_blobObjectList = v2_blobObjectList;
    }
}

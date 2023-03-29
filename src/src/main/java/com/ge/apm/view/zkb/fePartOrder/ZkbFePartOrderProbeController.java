package com.ge.apm.view.zkb.fePartOrder;

import com.ge.apm.dao.ZkbContractCatQuotaHistoryRepository;
import com.ge.apm.dao.ZkbContractCatRepository;
import com.ge.apm.dao.ZkbContractRepository;
import com.ge.apm.dao.ZkbFePartOrderRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.GatewayURL;
import com.ge.apm.service.zkb.ZkbContractAssetService;
import com.ge.apm.service.zkb.ZkbContractCatQuotaHistoryService;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.service.zkb.ZkbPartOrderService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.util.DateUtil;
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

    private static final Logger logger = LoggerFactory.getLogger(ZkbFePartOrderProbeController.class);

    /**
     * 默认合同错误
     */
    private Boolean contractError = false;
    /**
     * 智康宝人工申请 service service
     */
    private ZkbFePartOrderService zkbFePartOrderService;

    /**
     * GE维修申请service
     */
    private ZkbPartOrderService zkbPartOrderService;

    /**
     * 新逻辑扣除service
     */
    private ZkbContractCatQuotaHistoryService zkbContractCatQuotaHistoryService;



    /**
     *智康宝人工申请 reporsitory
     */
    private ZkbFePartOrderRepository zkbFePartOrderRepository;

    /**
     * 探头扣除历史表
     */
    private ZkbContractCatQuotaHistoryRepository zkbContractCatQuotaHistoryRepository;

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

    public static final String four_in_one = "fourToOne";

    public static final String FOUR_IN_ONE = "FourInOne";

    public static final String MESSAGE_TYPE = "messageOne";

    public static final String NOT_REPAIRED = "notRepaired";

    /**
     * PDF上传列表
     */
    private List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();

    private Map<String,String> i18Map;

    private ZkbContractAssetService zkbContractAssetService;

    private String encodeStr;

    private String notGeAssetBox;

    @Override
    protected void init() {
        super.init();
        zkbFePartOrderService = WebUtil.getBean(ZkbFePartOrderService.class);
        zkbContractCatQuotaHistoryService = WebUtil.getBean(ZkbContractCatQuotaHistoryService.class);
        zkbPartOrderService = WebUtil.getBean(ZkbPartOrderService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        zkbFePartOrderRepository = WebUtil.getBean(ZkbFePartOrderRepository.class);
        zkbContractRepository = WebUtil.getBean(ZkbContractRepository.class);
        zkbContractCatRepository = WebUtil.getBean(ZkbContractCatRepository.class);
        zkbContractCatQuotaHistoryRepository = WebUtil.getBean(ZkbContractCatQuotaHistoryRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        this.userName=currentUser.getName();
        this.userUid=currentUser.getUid();
        this.tenantUID=currentUser.getTenantUID();
        this.telephone=currentUser.getTelephone();
        encodeStr = WebUtil.getRequestParameter("str");
        String action = (String) UrlEncryptController.getValueFromMap(encodeStr, "action");
        this.id=(String) UrlEncryptController.getValueFromMap(encodeStr, "id");
        this.queryStatus=(String)UrlEncryptController.getValueFromMap(encodeStr, "queryStatus");
        this.notGeAssetBox=(String)UrlEncryptController.getValueFromMap(encodeStr, "notGeAssetBox");
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
        this.selected.setOrderType(ZkbFePartOrder.OrderType.Probe.toString());
        this.selected.setCurrentStep(ZkbFePartOrder.workFlowSteps.Requested.toString());
        this.selected.setCatNum(1);
        this.selected.setOrderAnnotation(ZkbFePartOrderService.normalOrderValue);
        this.orderAnnotationChange();
        this.zkbFePartOrderService.initTestStatus(this.loginName,this.selected);
    }

    public boolean checkContractDate()
    {
        return this.zkbFePartOrderService.checkContractDate(this.contractList);
    }

    public Boolean fourHundredStatus() {
        Boolean fourHundredStatus = true;
        if(ZkbFePartOrder.workFlowStatus.FourHundredsReject.toString().equals(this.selected.getStatus())){
            fourHundredStatus = false;
        }
        return fourHundredStatus;
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selected=this.zkbFePartOrderRepository.findById(this.id);
        if(StringUtils.isBlank(this.selected.getSecondNewCatFlag()) )
        {
            this.selected.setSecondNewCatFlag(this.selected.getNewCatFlag());
        }
        this.sparePartsZkbFePartOrderList = this.zkbFePartOrderService.getSparePartsZkbFePartOrder(null,null,this.selected.getFatherPartOrderNo(),null,ZkbFePartOrder.OrderType.Probe.toString());
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
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
        List<AssetInfo> assetInfoList = this.zkbFePartOrderService.initAssetInfo(this.selected);
        AssetInfo assetInfo = (assetInfoList!=null&&assetInfoList.size()>0)?assetInfoList.get(0):new AssetInfo();
        this.selected.setManufacture(assetInfo.getManufacture());
    }



    /**
     * @autor by lyj 503277717
     * @date 2021/12/21
     * KB - ZKB FE 申请与GE FE审批需要弹窗显示剩余名额
     */
    public String genConfirmMessage(String type){
        String confirmMessage = new String();
        String thisDeductionMessage = new String();
        ZkbContract zct = this.contractList.get(0);
        int qyt ="t".equals(this.selected.getNotGeAsset())?this.selected.getQty():this.handerQty(zct);
        Integer probeQuota = zct.getProbeQuota()!=null?zct.getProbeQuota():new Integer(0);
        Integer probeQuotaNormal = zct.getProbeQuotaNormal()!=null?zct.getProbeQuotaNormal():new Integer(0);
        Integer probeQuotaSpecial = zct.getProbeQuotaSpecial()!=null?zct.getProbeQuotaSpecial():new Integer(0);
        if("t".equals(this.selected.getNotGeAsset())){//非ge设备
            if("Special".equals(this.selected.getZkbPoProbeQuotaType())){
                confirmMessage += "本次扣除"+qyt+"个探头特殊名额；";
                thisDeductionMessage = "本次扣除"+qyt+"个探头特殊名额；";
                confirmMessage += "探头特殊剩余名额为"+probeQuotaSpecial+"。";
            }else if("Normal".equals(this.selected.getZkbPoProbeQuotaType())){
                confirmMessage += "本次扣除"+qyt+"个探头普通名额；";
                thisDeductionMessage = "本次扣除"+qyt+"个探头普通名额；";
                confirmMessage += "探头普通剩余名额为"+probeQuotaNormal+"。";
            }else{
                confirmMessage += "本次扣除"+qyt+"个探头任意名额；";
                thisDeductionMessage = "本次扣除"+qyt+"个探头任意名额；";
                confirmMessage += "探头任意剩余名额为"+probeQuota+"。";
            }
        }else{
            if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zct.getQuotaDeductType())){//旧
                int multiplyFlag = 1;
                if(ZkbFePartOrderService.testStatusTrueValue.equals(this.selected.getNewCatFlag())){
                    multiplyFlag=2;
                    probeQuota = probeQuota - qyt;
                }
                if("t".equals(this.selected.getHighCatFlag())){//是否高值   是高值  FourInOne 扣名额  其他不扣名额
                    if(ZkbFePartOrderProbeController.FOUR_IN_ONE.equals(this.selected.getContractCatSnNo())){
                        confirmMessage += "本次扣除"+qyt*multiplyFlag+"个探头名额；";
                        thisDeductionMessage = "本次扣除"+qyt*multiplyFlag+"个探头名额；";
                        confirmMessage += "探头剩余名额为"+probeQuota+"。";
                    }else{
                        confirmMessage += "本次申请不扣除名额！";
                        thisDeductionMessage = "本次申请不扣除名额！";
                    }
                }else{
                    confirmMessage += "本次扣除"+qyt*multiplyFlag+"个探头名额；";
                    thisDeductionMessage = "本次扣除"+qyt*multiplyFlag+"个探头名额；";
                    confirmMessage += "探头剩余名额为"+probeQuota+"。";
                }
            }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zct.getQuotaDeductType())){
                if("High".equals(this.selected.getZkbPoProbeQuotaType())){//高值  FourInOne 扣普通  其他不扣
                    if("FourInOne".equals(this.selected.getContractCatSnNo())){
                        confirmMessage += "本次扣除"+qyt+"个探头普通名额；";
                        thisDeductionMessage = "本次扣除"+qyt+"个探头普通名额；";
                        confirmMessage += "探头普通剩余名额为"+probeQuotaNormal+"。";
                    }else{
                        confirmMessage += "本次申请不扣除名额！";
                        thisDeductionMessage = "本次申请不扣除名额！";
                    }
                }else if("Any".equals(this.selected.getZkbPoProbeQuotaType())){
                    confirmMessage += "本次扣除"+qyt+"个探头任意名额；";
                    thisDeductionMessage = "本次扣除"+qyt+"个探头任意名额；";
                    confirmMessage += "探头任意剩余名额为"+probeQuota+"。";
                }else if("Special".equals(this.selected.getZkbPoProbeQuotaType())){
                    confirmMessage += "本次扣除"+qyt+"个探头特殊名额；";
                    thisDeductionMessage = "本次扣除"+qyt+"个探头特殊名额；";
                    confirmMessage += "探头特殊剩余名额为"+probeQuotaSpecial+"。";
                }else if("Normal".equals(this.selected.getZkbPoProbeQuotaType())){
                    confirmMessage += "本次扣除"+qyt+"个探头普通名额；";
                    thisDeductionMessage = "本次扣除"+qyt+"个探头普通名额；";
                    confirmMessage += "探头普通剩余名额为"+probeQuotaNormal+"。";
                }
            }
        }
        return ZkbFePartOrderProbeController.MESSAGE_TYPE.equals(type)?confirmMessage:thisDeductionMessage;
    }


    public Integer handerQty(ZkbContract zct){
        int qyt = 0 ;
        if(ZkbContract.QuotaDeductTypeEnum.Old.toString().equals(zct.getQuotaDeductType())) {//旧
            if (ZkbFePartOrderService.testStatusTrueValue.equals(this.selected.getHighCatFlag())) {
                if (ZkbFePartOrderProbeController.FOUR_IN_ONE.equalsIgnoreCase(this.selected.getContractCatSnNo())) {
                    qyt = 4;
                }
            } else {
                if (ZkbPartOrderCat.ValueType.NormalValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())) {
                    qyt = 1;
                } else if (ZkbPartOrderCat.ValueType.SpecialValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType())) {
                    qyt = 2;
                }
            }
        }else if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zct.getQuotaDeductType())) {
            qyt = 1;
            if ("High".equals(this.selected.getZkbPoProbeQuotaType())) {
                if (ZkbFePartOrderProbeController.FOUR_IN_ONE.equalsIgnoreCase(this.selected.getContractCatSnNo())) {
                    qyt = 4;
                } else {
                    qyt = 0;
                }
            }
        }
        return qyt;
    }




    /**
     * 查询
     * @param systemId system id
     * @param sr sr
     */
   /* public void getSparePartsZkbFePartOrder(String systemId,String sr){
        if(StringUtils.isBlank(systemId)&&StringUtils.isBlank(sr)){
             WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdAndFatherPartOrderNoError"));
            return;
        }
        //验证设备状态
        if (StringUtils.isNotBlank(zkbFePartOrderService.checkAsset(systemId))) {
            WebUtil.addErrorMessage(zkbFePartOrderService.checkAsset(systemId),"");
            this.sparePartsZkbFePartOrderList = null;
            return;
        }
        this.sparePartsZkbFePartOrderList=this.zkbFePartOrderService.getSparePartsZkbFePartOrder(systemId,sr,null,ZkbFePartOrderService.assetGroup,ZkbFePartOrder.OrderType.Probe.toString());
    }*/
    public void getSparePartsZkbFePartOrder(String notGeAsset,String systemId,String sr){
        if (StringUtils.isBlank(notGeAsset)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("错误：请选择是否GE设备或者非GE设备"));
            return;
        }
        if(StringUtils.isBlank(systemId)&&StringUtils.isBlank(sr)){
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_systemIdAndFatherPartOrderNoError"));
            return;
        }
        this.sparePartsZkbFePartOrderList=this.zkbFePartOrderService.getSparePartsZkbFePartOrder(systemId,sr,null,ZkbFePartOrderService.assetGroup,ZkbFePartOrder.OrderType.Probe.toString());
    }


    /**
     * 选择单据
     */
    public void selectedWorkFlow() {
        this.contractError = false;
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfoWithCheck(this.selectZkbFePartOrder.getContractId(),this.selectZkbFePartOrder.getSystemId());
        //未匹配到合同
        if (CollectionUtils.isEmpty(this.contractList)) {
            WebUtil.addErrorMessage("未查询出合同数据！","");
            this.contractError = true;
            return;
        }
        ZkbContract zkbContract =  this.contractList.stream().findFirst().get();
        if (ZkbContract.ContractType.CCSB.toString().equals(zkbContract.getContractType())&&ZkbFePartOrder.OrderType.Probe.toString().equals(this.selected.getOrderType())) {
            WebUtil.addErrorMessage("合同类型为CCSB的设备只允许进行备件申请！","");
            this.contractError = true;
            return;
        }

        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            this.selected.setFatherPartOrderNo(null);
            return ;
        }
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
        initHighList(this.selectZkbFePartOrder.getContractId(),false);
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
    public String saveAndToListPage(String notGeAsset) {
        if("t".equals(notGeAsset)){
            this.selected.setNotGeAsset("t");
        }
        if("t".equals(notGeAsset)){
            if(!zkbFePartOrderService.checkContracts(this.selected.getTenantUid())){
                WebUtil.addErrorMessage(WebUtil.getMessage("该设备所在代理商没有biomed 和PCB MVS类型合同，不能进行非GE设备申请！"));
                return "";
            }
            if(!zkbFePartOrderService.assetTypeHighMachineValue.equals(this.selected.getArtificialType())){
                WebUtil.addErrorMessage(WebUtil.getMessage("非GE设备只能申请高端机！"));
                return "";
            }
        }
        this.contractList=this.zkbFePartOrderService.loadZkbContractInfo(this.selected.getContractId());
        if (CollectionUtils.isEmpty(this.sparePartsZkbFePartOrderList)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("工单不能为空！"));
            return "";
        }
        Boolean fiveNineNine = this.zkbContractAssetService.checkFiveNineNineContract(contractList.get(0),selected.getReportDate(),selected.getArtificialType(),selected.getOrderType(),null);
        if(!fiveNineNine){
            return "";
        }
        if(this.selected.getZkbPartOrderCat()!=null&&ZkbPartOrderCat.ValueType.HighValue.toString().equals(this.selected.getZkbPartOrderCat().getValueType()))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("不能选择高值探头"));
            return "";
        }
        if(this.checkShowSN() && !FOUR_IN_ONE.equals(this.selected.getContractCatSnNo()))
        {
            if ("t".equals(this.selected.getNotGeAsset())){
                this.zkbFePartOrderService.calDeductQtyNotGE(this.selected);
            }else {
                this.selected.setQty(0);
            }
            ZkbContractCat zkbContractCat = this.zkbContractCatRepository.findById(this.selected.getContractCatId());
            if(!zkbContractCat.getEnabled())
            {
                WebUtil.addErrorMessage(WebUtil.getMessage("选中的SN编号探头不可用"));
                return "";
            }
        }else
            {
                if ("t".equals(this.selected.getNotGeAsset())){
                    this.zkbFePartOrderService.calDeductQtyNotGE(this.selected);
                }else {
                    this.zkbFePartOrderService.calDeductQty(this.selected,this.contractList.get(0));
                }
                calcRemainQty();
                ZkbContract zkbContract = this.contractList.get(0);
                if(this.selected.getRemainQty()<0){
                    WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                    return "";
                }
                if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                    calcRemainQty2022();
                    if(this.selected.getRemainQty()<0){
                        String assetTypeName = this.zkbFePartOrderService.getAssetTypeName(zkbContract,this.selected);
                        WebUtil.addErrorMessage("错误：该合同中的"+assetTypeName+"探头名额不足",null);
                        return "";
                    }

                }
                if(!this.checkShowSN())
                {
                    this.selected.setContractCatId(null);
                    this.selected.setCatId(this.selected.getZkbPartOrderCat().getId());
                }
            }
        logger.info("2  ZKB - 2022：zkbContract" + this.contractList.get(0).getProbeQuota()+"ProbeQuotaSpecial:"+this.contractList.get(0).getProbeQuotaSpecial()+"ProbeQuotaNormal:"+this.contractList.get(0).getProbeQuotaNormal());
        if(!this.showZkbhighCatFlag)
        {
            this.selected.setHighCatFlag(null);
        }
        if(this.selected.getQty()>0) {
            this.zkbFePartOrderService.moveProbe(1,this.contractList.get(0),this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,"探头申请");
        }
        String url = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.Requested.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingAssignment.toString(),new Date(),null, GatewayURL.FE_PART_ORDER_SEND_MESSAGE,this.notGeAssetBox);
        if(this.selected.getQty()>0){
            //TODO liyonjian ZKB - 2022年新探头逻辑
            ZkbContract zkbContract = this.contractList.get(0);
            if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                zkbContractCatQuotaHistoryService.insertZkbFeContractCatQuotaHistory(this.contractList.get(0),this.selected,this.currentUser);
            }
        }
        if(this.checkShowSN())
        {
          this.zkbFePartOrderService.updateZkbContractCat(currentUser,false,this.selected.getContractCatId());
        }
        this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
        logger.info("6  ZKB - 2022：zkbContract" + this.contractList.get(0).getProbeQuota()+"ProbeQuotaSpecial:"+this.contractList.get(0).getProbeQuotaSpecial()+"ProbeQuotaNormal:"+this.contractList.get(0).getProbeQuotaNormal());
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
            if(this.selected.getQty()!=null&&this.selected.getQty()>0&&this.selected.getRemainQty()<0){
                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                return "";
            }
            if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                this.calcRemainQty2022();
                if(this.selected.getQty()!=null&&this.selected.getQty()>0&&this.selected.getRemainQty()<0){
                    String assetTypeName = this.zkbFePartOrderService.getAssetTypeName(zkbContract,this.selected);
                    WebUtil.addErrorMessage("错误：该合同中的"+assetTypeName+"探头名额不足",null);
                    return "";
                }

            }
            if(this.selected.getQty()!=null&&this.selected.getQty()>0)
            {
                this.zkbFePartOrderService.moveProbe(1,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("TenantAccept"));
                this.selected.setQty(this.selected.getQty()*2);
                this.selected.setDoubleQty(true);
                //TODO liyonjian ZKB - 2022年新探头逻辑
                if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                    zkbContractCatQuotaHistoryService.updateZkbFeContractCatQuotaHistoryStatusOrQty(zkbContract,this.selected,this.currentUser,null,this.selected.getQty());
                }
            }
        }
        if(ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())&&ZkbFePartOrderService.testStatusTrueValue.equals(this.selected.getNewCatFlag()))
        {
            this.zkbFePartOrderService.saveUploadFile(this.v2_blobObjectList,this.selected.getId());
        }
        String workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingApm.toString();
        String format = "yyyy-MM-dd";
        String dateStr = DateUtil.getFormatStrByDate(this.selected.getReportDate(), format);
        Date reportDate = DateUtil.getDateByFormatStr(dateStr, format);
        //GE FE合同到期后400下单前增加Commercial角色进行审批  且不是599合同
        if (((reportDate.compareTo(zkbContract.getStartDate()) < 0 || reportDate.compareTo(zkbContract.getEndDate()) > 0) && !"Y".equals(zkbContract.getFiveNineNineFlag()))&&!"t".equals(this.selected.getNotGeAsset())){
            workFlowStep = ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString();
        }
        String result = this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantAccept.toString(),
                workFlowStep,date,null,GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
        //注意：流程为WaitingCommercial审批时需要发微信、短信、邮件通知
        if(workFlowStep.equals(ZkbFePartOrder.workFlowSteps.WaitingCommercial.toString())&&!"Y".equals(zkbContract.getFiveNineNineFlag())){
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_WECHART);
            this.zkbFePartOrderService.sendMessage(this.selected.getId(), GatewayURL.FE_PART_ORDER_SEND_MESSAGE);
        }
        return result;
    }

    /**
     * 供应商拒绝
     * @return
     */
    public String supplierReject(String comment) {
        if(StringUtils.isBlank(comment)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("代理商审批备注不能为空"));
            return "";
        }
        this.selected.setComment(comment);
        this.selected.setTenantAssignUser(this.userName);
        this.selected.setTenantAssignDate(new Date());
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0) {
            this.zkbFePartOrderService.moveProbe(4,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("TenantReject"));
            //TODO liyonjian ZKB - 2022年新探头逻辑
            if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                zkbContractCatQuotaHistoryService.updateZkbFeContractCatQuotaHistoryStatusOrQty(zkbContract,this.selected,this.currentUser,"N",null);
            }
        }
        if (StringUtils.isNotBlank(selected.getContractCatId())) {
            this.zkbFePartOrderService.updateZkbContractCat(currentUser,true,this.selected.getContractCatId());
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.TenantReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),new Date(),new Date(),GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
    }

    /**
     * commercial同意
     * @return
     */
    public String commercialAccept()
    {
        Date date = new Date();
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.CommercialAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingApm.toString(),date,null,GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
    }

    /**
     * commercial拒绝同意
     * @return
     */
    public String commercialReject(String comment)
    {
        if(StringUtils.isBlank(comment))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("Commercial审批备注不能为空"));
            return "";
        }
        Date date = new Date();
        this.selected.setCommercialComment(comment);
        this.selected.setCommercialAssignUser(this.userName);
        this.selected.setCommercialAssignDate(date);
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0){
            this.zkbFePartOrderService.moveProbe(4,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("TenantReject"));
            //TODO liyonjian ZKB - 2022年新探头逻辑  //此处需要改为update
            if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                zkbContractCatQuotaHistoryService.updateZkbFeContractCatQuotaHistoryStatusOrQty(zkbContract,this.selected,this.currentUser,"N",null);
            }
        }
        if (StringUtils.isNotBlank(selected.getContractCatId())) {
            this.zkbFePartOrderService.updateZkbContractCat(currentUser,true,this.selected.getContractCatId());
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.CommercialReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),date,date,GatewayURL.FE_PART_ORDER_SEND_EMAIL,this.notGeAssetBox);
    }

    /**
     * 400团队同意
     * @return
     */
    public String fullApmInfo() {
        //无法下载   把校验移到  后台
        if(StringUtils.isBlank(this.selected.getOrderNo())){
            WebUtil.addErrorMessage(WebUtil.getMessage("Order# 输入不可为空！"));
            return "";
        }
        if(StringUtils.isBlank(this.selected.getPrice()) && !"t".equals(this.selected.getNotGeAsset())){
            WebUtil.addErrorMessage(WebUtil.getMessage("价格 输入不可为空！"));
            return "";
        }
        if(StringUtils.isBlank(this.selected.getSecondNewCatFlag()) && !"t".equals(this.selected.getNotGeAsset())){
            WebUtil.addErrorMessage(WebUtil.getMessage("是否全新探头 输入不可为空！"));
            return "";
        }


        Date date = new Date();
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0){
            if("t".equals(this.selected.getSecondNewCatFlag()))
            {
                if(this.selected.getDoubleQty()!=null&&this.selected.getDoubleQty())
                {
                    this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("FourHundredsAccept"));
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
                        if(secondSave){
                            calcRemainQty();
                            if(this.selected.getRemainQty()<0){
                                WebUtil.addErrorMessage(WebUtil.getMessage("zkbFePartOrder_contractSupportTimesCannotBeZero"));
                                return "";
                            }
                            if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                                this.calcRemainQty2022();
                                if(this.selected.getRemainQty()<0){
                                    String assetTypeName = this.zkbFePartOrderService.getAssetTypeName(zkbContract,this.selected);
                                    WebUtil.addErrorMessage("错误：该合同中的"+assetTypeName+"探头名额不足",null);
                                    return "";
                                }

                            }
                        }
                        this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,firstSave,this.i18Map.get("FourHundredsAccept"));
                        if(secondSave){
                            this.zkbFePartOrderService.moveProbe(3,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,secondSave,this.i18Map.get("FourHundredsAccept"));
                            this.selected.setQty(this.getSelected().getQty()*2);
                            this.selected.setDoubleQty(true);
                        }
                    }
            }else
                {
                    if(this.selected.getDoubleQty()!=null&&this.selected.getDoubleQty())
                    {
                        this.zkbFePartOrderService.moveProbe(4,zkbContract,this.selected.getQty()/2,showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("FourHundredsAccept"));
                        this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty()/2,showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("FourHundredsAccept"));
                        this.selected.setQty(this.selected.getQty()/2);
                        this.selected.setDoubleQty(false);
                    }else
                    {
                        this.zkbFePartOrderService.moveProbe(2,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("FourHundredsAccept"));
                    }
                }
                    //TODO liyonjian ZKB - 2022年新探头逻辑  //此处需要改为update
                if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                    zkbContractCatQuotaHistoryService.updateZkbFeContractCatQuotaHistoryStatusOrQty(zkbContract,this.selected,this.currentUser,null,this.selected.getQty());
                }
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.FourHundredsAccept.toString(),
                ZkbFePartOrder.workFlowSteps.WaitingGEAdmin.toString(),date,null,GatewayURL.FE_PART_ORDER_SEND_WECHART,this.notGeAssetBox);
    }

    /**
     * 400团队拒绝
     * @return
     */
    public String fullApmReject(){
        this.selected.setFullApmInfoUser(this.userName);
        this.selected.setFullApmInfoDate(new Date());
        ZkbContract zkbContract =  this.zkbContractRepository.findById(this.selected.getContractId());
        if(this.selected.getQty()>0){
            this.zkbFePartOrderService.moveProbe(6,zkbContract,this.selected.getQty(),showZkbPoProbeQuotaType,this.selected.getZkbPoProbeQuotaType(),this.currentUser,this.selected,true,this.i18Map.get("FourHundredsReject"));
            //TODO liyonjian ZKB - 2022年新探头逻辑  //此处需要改为update
            if(ZkbContract.AssetQuotaTypeEnum.t.toString().equals(zkbContract.getAssetQuotaType())){
                zkbContractCatQuotaHistoryService.updateZkbFeContractCatQuotaHistoryStatusOrQty(zkbContract,this.selected,this.currentUser,"N",null);
            }
        }
        if (StringUtils.isNotBlank(selected.getContractCatId())) {
            this.zkbFePartOrderService.updateZkbContractCat(currentUser,true,this.selected.getContractCatId());
        }
        return this.zkbFePartOrderService.wolkFlowToComplete(this.selected,this.userUid,this.userName,this.tenantUID,this.queryStatus,ZkbFePartOrder.workFlowStatus.FourHundredsReject.toString(),
                ZkbFePartOrder.workFlowSteps.Complete.toString(),new Date(),null,GatewayURL.FE_PART_ORDER_SEND_WECHART,this.notGeAssetBox);
    }
    /**
     * admin归还
     * @return
     */
    public String geAdminReturn(){

        //无法下载   把校验移到  后台
        if(StringUtils.isBlank(this.selected.getReturnType())){
            WebUtil.addErrorMessage(WebUtil.getMessage("归还类型 输入不可为空！"));
            return "";
        }
        if(StringUtils.isBlank(this.selected.getWaybillNo())){
            WebUtil.addErrorMessage(WebUtil.getMessage("运单号 输入不可为空！"));
            return "";
        }
        if(this.selected.getReturnDate()==null){
            WebUtil.addErrorMessage(WebUtil.getMessage("归还日期 输入不可为空！"));
            return "";
        }
        if(StringUtils.isBlank(this.selected.getNewCatSn())&&!"t".equals(this.selected.getNotGeAsset())){
            WebUtil.addErrorMessage(WebUtil.getMessage("新探头SN编号 输入不可为空！"));
            return "";
        }
        if("t".equals(this.selected.getNotGeAsset())&&NOT_REPAIRED.equals(this.selected.getReturnType())&&StringUtils.isBlank(this.selected.getGeAdminNgeComment())){
            WebUtil.addErrorMessage(WebUtil.getMessage("归还类型为未维修完成时 原因输入不可为空！"));
            return "";
        }
        if("DOA".equals(this.selected.getReturnType())){
            if(StringUtils.isBlank(this.selected.getPartSerialNumber())){
                WebUtil.addErrorMessage(WebUtil.getMessage("Part Serial Number 输入不可为空！"));
                return "";
            }
            if(StringUtils.isBlank(this.selected.getPartQualityIssue())){
                WebUtil.addErrorMessage(WebUtil.getMessage("故障描述 输入不可为空！"));
                return "";
            }
        }
        return this.zkbFePartOrderService.geAdminReturn(this.selected,showZkbPoProbeQuotaType,this.userUid,this.userName,this.tenantUID,this.queryStatus,this.currentUser,this.i18Map,this.notGeAssetBox);
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
            zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(valueTypeList);
        }else
        {
            zkbPartOrderCatList = zkbPartOrderService.queryPartOrderCat(item.getStartDate());
        }
    }

    public void calcRemainQty(){
        if(CollectionUtils.isEmpty(contractList))
        {
            return;
        }
        ZkbContract zkbContract = contractList.get(0);
        Integer qty = new Integer(0);
        boolean logistic = ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())?true:false;
        if(logistic){
            if(ZkbFePartOrder.zkbPoProbeQuotaType.Any.toString().equals(this.selected.getZkbPoProbeQuotaType())){
                qty=zkbContract.getProbeQuota();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Special.toString().equals(this.selected.getZkbPoProbeQuotaType())){
                qty=zkbContract.getProbeQuotaSpecial();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.Normal.toString().equals(this.selected.getZkbPoProbeQuotaType())){
                qty=zkbContract.getProbeQuotaNormal();
            }else if(ZkbFePartOrder.zkbPoProbeQuotaType.High.toString().equals(this.selected.getZkbPoProbeQuotaType())){
                qty=zkbContract.getProbeQuotaNormal();
            }
        }else{
            qty=zkbContract.getProbeQuota();
        }
        Integer contractCount = qty == null ? 0 : qty;
        this.selected.setRemainQty(contractCount - this.selected.getQty());

    }

    public void calcRemainQty2022(){
        if(CollectionUtils.isEmpty(contractList)){
            return;
        }
        ZkbContract zkbContract = contractList.get(0);
        Integer qty = new Integer(0);
        boolean logistic = ZkbContract.QuotaDeductTypeEnum.New.toString().equals(zkbContract.getQuotaDeductType())?true:false;
        //TODO liyonjian ZKB - 2022年新探头逻辑
        qty = zkbContractCatQuotaHistoryService.handleQty(zkbContract,this.selected);
        Integer contractCount = qty == null ? 0 : qty;
        this.selected.setRemainQty(contractCount - this.selected.getQty());
    }

    public String backList(){
        return this.zkbFePartOrderService.backList(this.queryStatus,this.notGeAssetBox);
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
        if(StringUtils.isNotBlank(this.selected.getFatherPartOrderNo())&&!this.contractError) {
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

    public boolean hasOrderModifyRight() {
        return zkbFePartOrderService.hasOrderModifyRight(selected.getOrderType(),selected.getStatus(),configTypeMap);
    }

    public boolean hasOrderReturnModifyRight() {
        return zkbFePartOrderService.hasOrderReturnModifyRight(selected.getOrderType(),selected.getStatus(),configTypeMap);
    }

    public void saveOrderModify(){
        zkbFePartOrderService.saveOrderModify(selected,currentUser);
        this.selected=this.zkbFePartOrderRepository.findById(selected.getId());
    }

    public void saveReturnModify(){
        ZkbFePartOrder oldZkbPartOrder = zkbFePartOrderRepository.findById(selected.getId());
        ZkbContract zkbContract = this.zkbContractRepository.findById(selected.getContractId());
        if(!oldZkbPartOrder.getReturnType().equals(selected.getReturnType())){
            Integer type = 0;
            String step = null;
            if("badReturn".equals(selected.getReturnType())){
                type = 3;
                step = "GE FE探头已归还信息修改【补扣名额】";
            }else if("badReturn".equals(oldZkbPartOrder.getReturnType())){
                type = 5;
                step = "GE FE探头已归还信息修改【返还名额】";
            }
            if (type > 0 && selected.getQty() != null && selected.getQty() > 0) {
                zkbFePartOrderService.moveProbe(type, zkbContract, selected.getQty(), showZkbPoProbeQuotaType, selected.getZkbPoProbeQuotaType(), currentUser, selected, true, step);
            }
            if (StringUtils.isNotBlank(selected.getContractCatId())) {
                zkbFePartOrderService.updateZkbContractCat(currentUser, true, selected.getContractCatId());
            }
        }

        zkbFePartOrderService.saveReturnModify(selected,currentUser);
        this.selected=this.zkbFePartOrderRepository.findById(selected.getId());
    }

    public void setContractCatSnNo(){
        if(selected.getContractCatId() != null && highCatList != null){
            for (Map<String, String> contractCat : highCatList){
                if(contractCat != null && contractCat.get("msgKey").equals(selected.getContractCatId())){
                    selected.setContractCatSnNo(contractCat.get("value"));
                    break;
                }
            }
        }
    }

    public Boolean getContractError() {
        return contractError;
    }

    public void setContractError(Boolean contractError) {
        this.contractError = contractError;
    }

    public String getNotGeAssetBox() {
        return notGeAssetBox;
    }

    public void setNotGeAssetBox(String notGeAssetBox) {
        this.notGeAssetBox = notGeAssetBox;
    }
}

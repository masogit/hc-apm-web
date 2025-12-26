package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.ge.apm.dao.*;
import com.ge.apm.dao.mapper.AssetInfoMapper;
import com.ge.apm.domain.*;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.domain.pure.CustomConfigObject;
import com.ge.apm.domain.pure.SimplePartsItemValue;
import com.ge.apm.domain.simpleparts.SimplePartsItem;
import com.ge.apm.service.approval.MultiLevelApprovalService;
import com.ge.apm.service.asset.AssetInfoMgrService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.asset.CustomConfigService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigService;
import com.ge.apm.service.constant.PmConstant;
import com.ge.apm.service.constant.SimplePartsConstant;
import com.ge.apm.service.constant.WorkOrderConstant;
import com.ge.apm.service.orginfo.OrgInfoTreeService;
import com.ge.apm.service.pm.PmFileExportTemplate;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.simpleparts.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.ApmConstant;
import com.ge.apm.service.utils.FileScanService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.service.wo.ServiceRequestApiService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.service.wo.WorkflowService;
import com.ge.apm.view.SupplierController;
import com.ge.apm.view.sysutil.FileDownloadService;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.ExportFields;
import com.ge.apm.web.PmOrderActionForm;
import com.ge.healthcare.cn.common.cons.RepairSourceEnum;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateField;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.*;
import org.primefaces.event.data.FilterEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;
import sun.misc.BASE64Decoder;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.TimeUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * v
 *
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class V2PmOrderController extends GenericCRUDUUIDController<V2_PmOrder> implements ServerEventInterface {
    protected static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(V2PmOrderController.class);
    V2_WorkOrderDetailRepository v2_workOrderDetailRepository;

    private String fileTypeName;

    @Setter @Getter
    private List<PmOrderRule> ruleList;
    @Getter @Setter
    private PmOrderRule pmOrderRule;

    @Getter @Setter
    private FileScanService fileScanService;
    @Setter @Getter
    private Boolean isBatchOp=true;
//    @Setter @Getter
//    private Boolean isCreatable=true;
    private SupplierController supplierController;
    @Setter @Getter
    private Supplier newSupplier;
    public CustomConfigService customConfigService;
    //选定科室过滤
    @Setter @Getter
    private String filterClinicalDeptOrg;

    @Setter @Getter
    private Boolean periodShow;
    @Setter @Getter
    private Object  ruleNameObj;
    @Setter @Getter
    private Object  periodObj;
    private TableDefinitionService tableService;

    private List<String> pmFieldsExport= new ArrayList<>();
    private String filterAssetDeptOrg;
    @Setter @Getter
    List<UnstructuredDataCollection> reportList;
    //搜索字串
    private String oneSearchStr;
    //选定的院区
    private OrgInfo selectSite;
    private List<OrgInfo> clinicalDeptList;
    private List<OrgInfo> clinicalDeptListMVS;
    private OrgInfoTreeService orgInfoTreeService;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSet;
    @Setter @Getter
    private Set<String> clinicalDeptOrgUidSetMVS;
    @Setter @Getter
    List<PmOrderRuleCheckList> pmOrderRuleCheckList;
    private ServiceRequestApiService serviceRequestApiService;
    private PmOrderRuleRepository pmOrderRuleRepository;
    private RelRuleAssetRepository relRuleAssetRepository;
    private AssetTypeGroupRepository assetTypeGroupRepository;
    private String token;
    private List<UnstructuredDataCollection> unDataCollection;
    private UnstructuredDataCollection unCollection;
    private UnstructuredDataCollectionRepository reportDao;
    //private List<PmOrderRuleCheckList> pmCheckItemList;// 每条检查项记录
    private List<UnstructuredData> pmCheckItemList;// 每条检查项记录
    private CustomConfigObject currentCustomConfig;
    private ExternalTempUserRepository externalTempUserDao;
    private ConfigItemValueRepository configItemValueRepository;
    List<String> ruleStrList;
    @Setter @Getter
    private Boolean nochange = false;
    private static final String SIGNATURE_HOLDER = "审核人签名：";
    private static final String EMPTY_SIGNATURE_HOLDER = "";
    @Getter @Setter
    Boolean switchTmp= false;
    @Getter @Setter
    private String fileterOrgName;
    @Getter @Setter
    private String fileterOrgNameMVS;
    @Getter @Setter
    private String filterRule;
    @Getter @Setter
    private Integer casePriority;
    private  String cancelInt;
    private String supplierName;
    private AutoCompleteService autoCompleteService;
    private AssetInfoMgrService aimService;
    private List<Supplier> supplierList = new ArrayList<Supplier>();
    private V2_PmOrderRepository dao;
    private AssetInfoMapper assetInfoMapper;
    @Getter @Setter
    private Double totalManHours;
    @Getter @Setter
    private String dptName;
    private UserAccountRepository userDao;
    private V2_PmOrderService pmOrderService;
    private V2_PmOrder selectedPmOrder;
    private String assetName;
    private String assetUid;
    private Integer ruleLevel;
    private Date startTime;
    private Date endTime;
    private Date planTime;
    private Date applyTime;
    private Date planTimeForBatch;
    private String currentPersonIdBatch;
    private Boolean terminate;
    private UaaService uaaService;
    private UserContextService userContextService;
    private BlobFileService blobService;
    private List<V2_PmOrder> selectedPmOrderList;
    //private V2_WorkOrderCoworkerRepository coWorkerDao;
    //status：2-待派工/3-已派工/4-已关单/5-已取消
    private Integer queryIndex;
    private AssetInfo selectedAsset;
    private Integer currentStatus = 1;
    private Boolean needConfirmTime = false;
    private String cancelReason;
    private String stepFileter;
    private String repairType;
    private Integer repairTypeInt;
    //for details form
    private List<V2_WorkOrder_Detail> approvedDetails;
    private List<V2_WorkOrder_Detail> accessoryList;
    private List<V2_WorkOrderCoworker> coWorkerList;
    private V2_WorkOrder_Detail itemDetail;
    private V2_WorkOrderCoworker coWorkerDetail;
    private Date esTime;
    private String comments;
    private Date confirmedUpDate;
    private Date confirmedDownTime;
    private String checkInDate;
    private Integer rating;
    private String failReason;
    private String equipmentTaker;
    private Date takeTime;
    private String approvaler;
    private List<V2_BlobObject> picList;
    private List<AssetTypeGroup> assetTypeGroupList;

    @Getter @Setter
    private List<V2_BlobObject> picBeforeList;
    @Getter @Setter
    private List<V2_BlobObject> picAfterList;

    private List<V2_BlobObject> picListForRP;
    private List<V2_BlobObject> audioList;
    private V2_WorkOrderService woService;
    private V2_ServiceRequest newServiceRequest;
    private BlobObjectRepository blobDao;
    private String pmOrderId;
    private String filterPlanTime;
    private Map<String, Boolean> selectedMap;
    private TableDefinitionRepository tableDao;
    private UnstructuredDataRepository unstructuredDataDao;
    private PmOrderRuleCheckListRepository pmckLstDao;
    HashMap<String, UnstructuredData> hmCheck;
    private Boolean isSelectedCurrentPage = false;
    private List<V2_PmOrder> currentPagePmOrders;
    private Integer pmType;
    private String pmRuleUid;
    private I18nMessageRepository i18nMessageRepository;
    private String head;
    // cache for maintenance report
    private StreamedContent maintenanceReport;
    private UserAccount loginUser;
    private TenantInfoRepository tenantInfoRepository;
    Boolean isInsulate;
    private String reportUid; //selected table base uid
    private Map<String, Object> selectedObjectMap = new HashMap();
    private PmReportService pmReportService;
    private MultiLevelApprovalService multiLevelApprovalService;
    private String queryAssetUid;
    private String orderId;
    private ConfigService configService;
    private SimplePartsConfigService partsConfigService;
    //tab Title
    @Getter
    private String tabTitle;
    private String plannedCompletionTimeFormat = null;
    private String actualCompletionTimeFormat = null;
    @Setter@Getter
    private boolean hasConfigMultiLevelApproval = false;
    private String filterPmRuleLevel;
    //备注
    @Getter @Setter
    private String remarks;
    // 签名相关
    private String signatureContent;
    private StreamedContent signatureContentStream;
    private String signatureId;
    private boolean newSignature = true;

    @Getter @Setter
    private String siteUidFilter;
    @Getter @Setter
    private String siteUidFilterMVS;
    @Getter @Setter
    private List<OrgInfo> orgListMVS;
    @Getter @Setter
    List<V2_PmOrder> bySearchFilterMVS = null;
    @Getter @Setter
    List<V2_PmOrder> bySearchFilterInternal = null;
    @Getter @Setter
    private List<OrgInfo> orgList;

    @Getter @Setter
    private boolean repaFlag = false;

    @Getter @Setter
    private TreeNode[] selectedAccessOrgNodes;

    private WorkflowService workflowService;

    private ConfigCacheService configCacheService;

    private FileDownloadService fileDownloadService;

    @Getter@Setter
    private V2_PmOrderManHoursDetail selectedPmOrderManHoursDetail;



    public String getActualCompletionTimeFormat() {
        return actualCompletionTimeFormat;
    }

    public void setActualCompletionTimeFormat(String actualCompletionTimeFormat) {
        this.actualCompletionTimeFormat = actualCompletionTimeFormat;
    }

    public String getPlannedCompletionTimeFormat() {
        return plannedCompletionTimeFormat;
    }

    public void setPlannedCompletionTimeFormat(String plannedCompletionTimeFormat) {
        this.plannedCompletionTimeFormat = plannedCompletionTimeFormat;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
    @Getter @Setter
    private StreamedContent excelFile;
    @Getter
    @Setter
    private SimplePartsItem partsItem;
    @Getter
    @Setter
    private Integer currentQuantity;
    @Getter
    @Setter
    private int statusFlag;
    private SimplePartsRequestService partsRequestService;
    private SimplePartsInventoryService partsInventoryService;
    private SimplePartsItemService partsItemService;
    private BlobObjectRepository blobObjectRepository;
    private SimplePartsRequestWorkOrderService partsRequestWorkOrderService;
    @Getter @Setter
    private ExternalTempUser externalTempUser;
    @Getter @Setter
    private List<Map<String,String>> maintenancePersonInformation;
    @Getter @Setter
    public String customerName;
    @Getter @Setter
    public String customerTel;
    @Getter @Setter
    public String siteName;
    @Getter @Setter
    private String requestAssetUid;
    @Getter @Setter
    boolean showLocationNameFlag;
    public boolean downloadButton(){
        if(queryIndex==10){
            return false;
        }
        if(pmIdList.size()>0){
            return false;
        }else{
            return true;
        }
    }
    public String getSiteOrgName(String siteUID) {
        OrgInfo org = configCacheService.findOrgByUid(siteUID);
        if (org != null) {
            return org.getName();
        }
        return null;
    }

    public boolean exportButtonShow(){
        if (queryIndex==10 || queryIndex  == 20){
            return false;
        }else{
            return !isChecked();

        }
    }
   public void exportExcel() throws IOException{
       if(queryIndex == 10){
           long start = System.currentTimeMillis();
           List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());

           List<Object> list =  dao.getAllPmOrder(orgUidList);
           long search = System.currentTimeMillis();
           logger.info("cost time :" + (search - start)/1000);

           String path = new DateTime().toString("yyyyMMddHHmm") + File.separator + loginUser.getLoginName();
           String filePath = fileScanService.getUploadFileFullPath(path);
           File dest = new File(filePath + File.separator+"pm.xlsx");
          FileUtils.forceMkdir(new File(filePath));
//            String sourcePath = this.getClass().getClassLoader().getResource("template/PmOrder_export.xlsx").getPath();
//            File source = new File(sourcePath);
//           FileUtils.copyFile(source,dest);
           InputStream  inputStream=fileDownloadService.getTemplateFileInputStream(SystemTemplateEnum.PM_ORDER_EXPORT.getCode(), SystemTemplateEnum.PM_ORDER_EXPORT.getFileType());
           FileUtils.copyToFile(inputStream,dest);
           genPmExcel(filePath + File.separator+"pm.xlsx",list);
           excelFile = new DefaultStreamedContent(new FileInputStream(filePath+File.separator+"pm.xlsx"), "application/.ms-excel", "PmOrder_Export.xlsx");
           long end = System.currentTimeMillis();
           logger.info("cost time :"+ (end - search)/1000);
           return;
       }
       PmFileExportTemplate template = new PmFileExportTemplate();
        List<V2_PmOrder> pmOrderList = getPmListByselectedId();
       if(pmOrderList ==null && queryIndex!=10){
           WebUtil.addErrorMessage(WebUtil.getMessage("SelectExportFiles"));
           RequestContext.getCurrentInstance().addCallbackParam("", true);
           return;
       }
        template.exportPm(pmOrderList,pmFieldsExport,queryIndex);
       excelFile = new DefaultStreamedContent(template.getInputStream(), "application/.ms-excel", "PmOrder_Export.xlsx");
   }


    public void exportExcel1() throws IOException{
        PmFileExportTemplate template = new PmFileExportTemplate();
        List<V2_PmOrder> pmOrderList=  getExportList();
        if(pmOrderList ==null && queryIndex!=10){
            WebUtil.addErrorMessage(WebUtil.getMessage("SelectExportFiles"));
            RequestContext.getCurrentInstance().addCallbackParam("", true);
            return;
        }
        template.writePm(pmOrderList,queryIndex);
        excelFile = new DefaultStreamedContent(template.getInputStream(), "application/.ms-excel", "PmOrder_Export.xlsx");
    }

    private List<V2_PmOrder> getExportList() {
        List<V2_PmOrder> pmList=null;
        pick(null);
        if(pmIdList.size()!=0 && queryIndex!=10 ) {
            pmList = dao.getBatchPmOrderList(pmIdList);
        }else{
//            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
//            pmList = dao.getPmOrderListAll(orgUidList);


            Sort.Order orders = new Sort.Order(Sort.Direction.DESC, "planTime");
            Sort sort = new Sort(orders);
            PageRequest    pageRequest = new PageRequest(0,30000,sort);
            Page<V2_PmOrder> v2PmOrders = loadData(pageRequest);
            pmList= v2PmOrders.getContent();
        }
        return pmList;
    }

    public String checkValue(Object obj){
        if(obj != null){
            return obj.toString();
        }else {
            return "";
        }
   }

   public void genPmExcel(String path,List<Object> list){
       SXSSFWorkbook wb = new SXSSFWorkbook(100);
       Sheet sheet = wb.createSheet();
       Row headRow = sheet.createRow(0);

       Cell cell0 = headRow.createCell(0);
       cell0.setCellValue(WebUtil.getMessage("hospitalId"));
       Cell cell1 = headRow.createCell(1);
       cell1.setCellValue(WebUtil.getMessage("assetName"));
       Cell cell2 = headRow.createCell(2);
       cell2.setCellValue(WebUtil.getMessage("serialNum"));
       Cell cell3 = headRow.createCell(3);
       cell3.setCellValue(WebUtil.getMessage("departNum"));
       Cell cell4 = headRow.createCell(4);
       cell4.setCellValue(WebUtil.getMessage("functionType"));
       Cell cell5 = headRow.createCell(5);
       cell5.setCellValue(WebUtil.getMessage("MeasurementGrade2"));
       Cell cell6 = headRow.createCell(6);
       cell6.setCellValue(WebUtil.getMessage("MaintenanceType"));
       Cell cell7 = headRow.createCell(7);
       cell7.setCellValue(WebUtil.getMessage("clinicalDeptId"));
       Cell cell8 = headRow.createCell(8);
       cell8.setCellValue(WebUtil.getMessage("PmOrderRule"));
       Cell cell9 = headRow.createCell(9);
       cell9.setCellValue(WebUtil.getMessage("MaintenancePeriod"));
       Cell cell10 = headRow.createCell(10);
       cell10.setCellValue(WebUtil.getMessage("creatorUID"));
       Cell cell11 = headRow.createCell(11);
       cell11.setCellValue(WebUtil.getMessage("caseOwnerId"));
       Cell cell12 = headRow.createCell(12);
       cell12.setCellValue(WebUtil.getMessage("MaintenanceNumber"));
       Cell cell13 = headRow.createCell(13);
       cell13.setCellValue(WebUtil.getMessage("currentStep"));
       Cell cell14 = headRow.createCell(14);
       cell14.setCellValue(WebUtil.getMessage("intExtType"));
       Cell cell15 = headRow.createCell(15);
       cell15.setCellValue(WebUtil.getMessage("PlannedCompletionTime"));

       for(int i=0;i<list.size();i++){
           Row dataRow = sheet.createRow(i+1);
           for (int j = 0; j <16 ; j++) {
               Cell dataCell = dataRow.createCell(j);
               dataCell.setCellValue(checkValue(((Object[])list.get(i))[j]));
           }
       }
       FileOutputStream fileOutputStream = null;
       try {
            fileOutputStream = new FileOutputStream(path);
           wb.write(fileOutputStream);
           fileOutputStream.flush();
           fileOutputStream.close();
       } catch (Exception e) {
           e.printStackTrace();
       }finally {
           if(fileOutputStream != null){
               try {
                   fileOutputStream.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }

    List<String> pmIdList = new ArrayList<String>();
   private  List<V2_PmOrder> getPmListByselectedId(){
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);

        });
       if(queryIndex == 10 || queryIndex == 20){
           if(pmIdList.size()==0) {//如果全部没选 就是在"全部中"点击了导出
               selectedMap.forEach((k, v) -> {
                   pmIdList.add(k);
               });

           }
       }

        if(pmIdList.size()==0) {
            return null;
        }
        List<V2_PmOrder> pmList = dao.getBatchPmOrderList(pmIdList);
        return pmList;
    }
    public void pick(String itemId){
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);

        });
    }
    @Override
    public void onFilter(FilterEvent event){
        super.onFilter(event);
        Map<String, Object> filters = event.getFilters();
         ruleNameObj = filters.get("ruleName");
         periodObj = filters.get("period");
        removeFilterOnField("ruleName");
        removeFilterOnField("period");
        selectedMap.clear();
    }

    public void updatePMRuleFilterChange(){
       if(ruleNameObj != null || periodObj != null) {
           Set<String> pmRuleUids = getPMRuleUids(ruleNameObj, periodObj);
           if (CollectionUtils.isEmpty(pmRuleUids)) {
               pmRuleUids.add("-1");
           }
           filterArrayList.add(new SearchFilter("pmRuleUid", SearchFilter.Operator.IN, pmRuleUids));
       }
    }

    private Set<String> getPMRuleUids(Object ruleNameObj,Object periodObj){
       String ruleName = "";
       Integer period = null;
       if(ruleNameObj != null){
            ruleName = String.valueOf(ruleNameObj);
        }
        if(periodObj != null){
            period = Integer.parseInt(String.valueOf(periodObj));
        }
        List<SearchFilter> pmRuleFilter = new ArrayList<>();
        if(StringUtils.isNotEmpty(ruleName)) {
            pmRuleFilter.add(new SearchFilter("name", SearchFilter.Operator.LIKE, ruleName));
        }
        if(period != null) {
            pmRuleFilter.add(new SearchFilter("period", SearchFilter.Operator.EQ, period));
        }
        List<PmOrderRule> pmOrderRules = pmOrderRuleRepository.findBySearchFilter(pmRuleFilter);
        return pmOrderRules.stream().map(PmOrderRule::getUid).collect(Collectors.toSet());
    }



    public PmOrderRule getRulePm(V2_PmOrder v2_pmOrder){
       //根据设备查找规则
        return Optional.ofNullable(pmOrderService.acqPmOrderRule(v2_pmOrder)).orElse(new PmOrderRule());
//        PmOrderRule orderRule = pmOrderRuleRepository.findByUid(v2_pmOrder.getPmRuleUid());
//        if(orderRule == null){
//            return new PmOrderRule();
//        }else {
//            return orderRule;
//        }
//            return new PmOrderRule();
//        }
//        List<PmOrderRule> pmRuleByPmNum = pmOrderRuleRepository.findPmRuleByPmNum(v2_pmOrder.getPmNum(),v2_pmOrder.getTenantUID());
//        if(CollectionUtils.isEmpty(pmRuleByPmNum)){
//            return new PmOrderRule();
//        }
//        else{
//            PmOrderRule pmOrderRule = pmRuleByPmNum.get(0);
//            return pmOrderRule;
//        }
    }
    public void preBatchCancel(){
        cancelReason=null;
         List<V2_PmOrder> pmList =getPmListByselectedId();
        if (pmList != null) {
            //周期性工单的pmtype是1
            Optional<V2_PmOrder> any = pmList.stream().filter(item -> item.getPmType() == 1).findAny();
            if(any.isPresent()){
                cancelInt="2";
                periodShow=true;
            }else{
                cancelInt="1";
                periodShow=false;
            }
        }
    }

    public boolean batchCancelFlag(){
        List<V2_PmOrder> pmList =getPmListByselectedId();
        if (pmList == null) {
            return false;
        }
        Optional<V2_PmOrder> any = pmList.stream().filter(t -> t.getPmType() == 4).findAny();
        if (any.isPresent()){
            return true;
        }
        return false;
    }

    public boolean batchCancelShow(){
         if(queryIndex==0 || queryIndex==-1){
             if(!userContextService.hasRole("AssetHead"))
             {
                 return true;
             }
             else{
                 return false;
             }
         }else{
             return false;
         }
    }
    public void prepareEditAccDetailonPrice(String itemId) {
        accessoryList = woService.getWorkOrderDetails(selected.getId());//?如果一条woId仅仅对应一条workOrderDetail 这条代码就不需要
        if (!accessoryList.isEmpty())
            if (accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().isPresent()){
                itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
            }
        supplierList = aimService.getSuplierListBySiteUid(selected.getTenantUID());
        supplierName = itemDetail.getSupplier();
        nochange = true;
    }
    public void prepareCreateSupplier() {
        newSupplier = new Supplier();
    }

    public double getClosePrice(Double apartsQuantity,Double partsPrice){
        if(apartsQuantity != null && partsPrice != null){
            return apartsQuantity * partsPrice;
        }
        return 0;
    }

    public void saveItem(){
        if(partsItem==null && itemDetail.getParts()==null) {
            WebUtil.addErrorMessage("备件名称不能为空");
            return;
        }
        if (!accessoryList.contains(itemDetail)) {
            //合同价格"默认等于"预估单价
            if(itemDetail.getClosedPrice()==null) {
                itemDetail.setClosedPrice(itemDetail.getPartsPrice());
            }
            itemDetail.setArrivedGood(false);
            itemDetail.setArrivedGoodReturn(false);
            accessoryList.add(itemDetail);
        }
       //switchTmp is false 是修改
        if(switchTmp) {
            if (itemDetail.getId() != null) {
                V2_WorkOrder_Detail byId = v2_workOrderDetailRepository.findById(itemDetail.getId());
                //如果是新的备件 或者 备件是刚刚"新增保存"完成的，那么这个备件都视为新的备件
                if (byId == null || !byId.getParts().equalsIgnoreCase(itemDetail.getParts())) {
                    itemDetail.setId(null);
                }
            }
        }

        serviceRequestApiService.addAccess(token,selectedPmOrder.getAssetInfo().getUid(),itemDetail);
        accessoryList = getWorkOrderDetail(selectedPmOrder.getId());
    }
    public void onSelectSiteTreeNode2(NodeSelectEvent event) {
        // Compared with the above method "onSelectSiteTreeNode",
        // this method does not clear existing filters
        if (null != event.getTreeNode() && null != event.getTreeNode().getData()) {
            selectSite = (OrgInfo) event.getTreeNode().getData();
        } else {
            selectSite = null;
        }

        //如果是mvs role,选择了所有租户选项，则清空所选机构
        if (userContextService.getIsMVSRole() && selectSite != null && selectSite.getId() == -1) {
            selectSite = null;
        }

        if(selectSite==null) this.removeFilterOnField("siteUID");

        loadClinicalDeptListForFilter();
    }
    public void saveAndadd(int oper){
        //照旧原来的添加一个备件
        if(oper ==1){ //来自保存新增，从新建一个item
            itemDetail.setId(null);
        }
        //saveItem();

        if (!accessoryList.contains(itemDetail)) {
            //合同价格"默认等于"预估单价
            if(itemDetail.getClosedPrice()==null) {
                itemDetail.setClosedPrice(itemDetail.getPartsPrice());
            }
            itemDetail.setArrivedGood(false);
            itemDetail.setArrivedGoodReturn(false);
            accessoryList.add(itemDetail);

        }
        itemDetail.setId(null);
        //调用微服务来存附件
        serviceRequestApiService.addAccess(token,selectedPmOrder.getAssetInfo().getUid(),itemDetail);

        V2_WorkOrder_Detail vwd= new V2_WorkOrder_Detail();
        try {
            BeanUtils.copyProperties(vwd,itemDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
        vwd.setId(itemDetail.getId());
        // clear 备件名称 备件数量  预估价格
        vwd.setParts(null);
        vwd.setApartsQuantity(null);
        vwd.setPartsPrice(null);
        vwd.setClosedPrice(null);
        itemDetail= vwd;

        accessoryList = getWorkOrderDetail(selectedPmOrder.getId());

    }
    public void prepareCancel(String itemId) {
        cancelReason = null;
        selected = dao.findById(itemId);
        int type = selected.getPmType();
        if (type == 1) {
            cancelInt = "2";
        }
        if (type == 2 || type == 3 || selected.isFinished()) {
            cancelInt = "1";
        }
    }
    public void changeStatus() {
        partsItem = null;
        currentQuantity = 0;
        itemDetail.setParts(null);
        itemDetail.setPartsPrice(null);
        itemDetail.setClosedPrice(null);
        if (itemDetail != null) {
            String partsType = itemDetail.getPartsType();
            if (WorkOrderConstant.PARTS_TYPE_RECEIVING.equals(partsType)) {
                statusFlag = WorkOrderConstant.STATUS_FLAG_RECEIVING;
                return;
            }
            if (WorkOrderConstant.PARTS_TYPE_PURCHASE.equals(partsType)) {
                statusFlag = WorkOrderConstant.STATUS_FLAG_PURCHASE;
                return;
            }
        }
        statusFlag = WorkOrderConstant.STATUS_FLAG_OTHER;
    }
    public List<SimplePartsItem> autosparePartName() {
        List<SimplePartsItem> all = partsInventoryService.findAll();
        return all;
    }
    public List<SimplePartsItem> initSparePartsMaster() {
        List<SimplePartsItem> partsItemList = partsItemService.findAll();
        return partsItemList;
    }

    public List<SimplePartsItem> autosparePartNameForPm(String name) {
        List<SimplePartsItem> all = partsInventoryService.findAllByName(name);
        return all;
    }
    public List<SimplePartsItem> initSparePartsMasterForPm(String name) {
        List<SimplePartsItem> partsItemList = partsItemService.findAllByName(name);
        return partsItemList;
    }

    public void onSparePartsMasterValueChange(SelectEvent event) {
        SimplePartsItem partsItem = (SimplePartsItem) event.getObject();
        if (partsItem == null) return;
        itemDetail.setParts(partsItem.getName());
        itemDetail.setPartsItemId(partsItem.getId());
        if(userContextService.isCountyArea()){
            itemDetail.setPartsPrice(null);
            if(partsItem.getUnitPrice() != null){
                itemDetail.setPartsPrice(partsItem.getUnitPrice().doubleValue());
            }
            itemDetail.setPartsDescription(partsItem.getPartsDescription());
        }
    }
    public void onSparePartsValueChange(SelectEvent event) {
        Object object = event.getObject();

        if (null != object) {
            partsItem = (SimplePartsItem) object;
            itemDetail.setParts(partsItem.getName());
            SimplePartsItemValue partsItemValue = partsRequestService.getInventoryNumAndAvgPriceByPartsItem(partsItem.getId(),null).get();
            itemDetail.setPartsPrice(partsItemValue.getEstimatedUnitPrice().doubleValue());
            itemDetail.setClosedPrice(partsItemValue.getEstimatedUnitPrice().doubleValue());
            itemDetail.setCurrentQuantity(partsItemValue.getInStockNum());
            itemDetail.setPartsItemId(partsItem.getId());
            if(userContextService.isCountyArea()){
                itemDetail.setPartsPrice(null);
                if(partsItem.getUnitPrice() != null){
                    itemDetail.setPartsPrice(partsItem.getUnitPrice().doubleValue());
                }
                itemDetail.setPartsDescription(partsItem.getPartsDescription());
            }
        }
    }
    public void cancelPMorder() {
        pmOrderService.invokePmOerCancel(cancelReason, selected, cancelInt);
    }


    public void cancelBatchPMorder() {
        pmOrderService.invokeBatchPmOrderCancel(cancelReason, selectedMap, cancelInt);
    }

    public boolean getPmRule(){
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null && workflowConfig.getPmRuleLevelFour();
    }
    public boolean getPmRuleList(){
        autoSetPmRuleUid(assetUid,ruleLevel);
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        WorkflowConfig workflowConfig = configService.findConfigBySite(userAccount.getSiteUID());
        return workflowConfig != null
                && workflowConfig.getPmFourthRuleLevelForAsset()
                && StringUtils.isNotEmpty(assetUid)
                && ruleLevel != null
                && ruleLevel == 4
//                && pmType != null
                && getPmOrderRulesByAsset() != null
                ;
    }

    public String getRuleLevelName(Integer ruleLevel){
        switch (ruleLevel){
            case 1:
                return "巡检";

            case 2:
                return "2";

            case 3:
                return "3";

            case 4:
                return "质量控制";
            default:
                return "";
        }
    }


    public boolean allowChanged() {
        //is closed
        if (selected == null) {
            return true;
        }
        if (selected.isFinished()) {//关单后
            if (userContextService.hasAnyRole("AssetHead")) {
                return true;
            } else {
                return false;
            }
        } else {
            if (userContextService.hasAnyRole("AssetHead") || userContextService.hasAnyRole("AssetStaff")) {
                return true;
            }
        }
        return true;
    }

    public boolean allowChangedForDisable() {
        if (selected.isFinished()) {//关单后
            if (userContextService.hasAnyRole("AssetHead")) {
                return false;
            } else {
                return true;
            }
        } else {
            if (userContextService.hasAnyRole("AssetHead") || userContextService.hasAnyRole("AssetStaff")) {
                return false;
            } else {
                return true;
            }
        }

    }

    public void partsPriceUpdate() {
       if(itemDetail.getPartsPrice() == null || itemDetail.getApartsQuantity() == null){
           itemDetail.setClosedPrice(0.0);
       }else{
           itemDetail.setClosedPrice(itemDetail.getPartsPrice()*itemDetail.getApartsQuantity());
       }
    }

    public void partsQuantity1Update() {
        if(itemDetail.getApartsQuantity() == null || itemDetail.getPartsPrice() == null){
            itemDetail.setClosedPrice(0.0);
        }else{
            itemDetail.setClosedPrice(itemDetail.getPartsPrice()*itemDetail.getApartsQuantity());
        }
    }

    public void closePrcieUpdate() {
        itemDetail.setClosedPrice(itemDetail.getClosedPrice());
    }

    public void orderSet() {
        if (itemDetail == null) return;
        if (itemDetail.getClosed() == true)
            itemDetail.setClosedTime(new Date());
        else {
            itemDetail.setClosedTime(null);
        }
    }
    public void arrivalTimeSet() {
        if (itemDetail == null) return;
        if (itemDetail.getArrived() == true)
            itemDetail.setArrivedTime(new Date());
        else {
            itemDetail.setArrivedTime(null);
        }
    }

    public void handleToggle(ToggleEvent event) {
        //折叠保养清单后向前台传递的消息
       /* FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "折叠", "Visibility:" + event.getVisibility());
        FacesContext.getCurrentInstance().addMessage(null, msg);*/
    }

    public boolean sameType(){
        ArrayList<String> orderNumList = new ArrayList<String>();
        List<Map.Entry<String, Boolean>> collect =
                sameOpHashmap.entrySet().stream().filter(item -> item.getValue()).collect(Collectors.toList());
        collect.stream().forEach(item->{
            orderNumList.add(item.getKey());
        });
        boolean result=false;
         if(orderNumList.size()>0) {
             result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getApprovalStatus().equalsIgnoreCase("1"));
             if(result){
                 WebUtil.addErrorMessage(WebUtil.getMessage("CantOperateWorkorderUnderApproval"));
                 RequestContext.getCurrentInstance().addCallbackParam("batchForbidForApproval", true);
                 return result;
             }

             result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getRuleLevel() == 3);
             if(result){
                 WebUtil.addErrorMessage(WebUtil.getMessage("CanTLevel3WorkOrdersInBatches"));
                 RequestContext.getCurrentInstance().addCallbackParam("batchForbidForMaintanLevel", true);
                 return result;
             }
             result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getRuleLevel() == 4);
             if(result){
                 WebUtil.addErrorMessage(WebUtil.getMessage("CanTLevel4WorkOrdersInBatches"));
                 RequestContext.getCurrentInstance().addCallbackParam("batchForbidForMaintanLevel", true);
                 return result;
             }
         }
         return result;
    }

    public void forbidTransfer(){
        ArrayList<String> orderNumList = new ArrayList<String>();
        List<Map.Entry<String, Boolean>> collect =
                sameOpHashmap.entrySet().stream().filter(item -> item.getValue()).collect(Collectors.toList());
        collect.stream().forEach(item->{
            orderNumList.add(item.getKey());
        });
        boolean result=false;
        if(orderNumList.size()>0) {
            result = dao.getBatchPmOrderList(orderNumList).stream().anyMatch(item -> item.getApprovalStatus().equalsIgnoreCase("1"));
            if(result){
                WebUtil.addErrorMessage(WebUtil.getMessage("CantRessignApprovalBatch"));
                RequestContext.getCurrentInstance().addCallbackParam("batchForbidForTransfer", true);

            }
        }
    }
    public void addReporter(String boUid) {//boUid  is pmOrderId
        UnstructuredDataCollection newReport = new UnstructuredDataCollection();
        newReport.setBoUuid(boUid);
        TableDefinition tbd = tableDao.findByBaseId(reportUid);//table_def:baseId
        if(tbd==null) return;
        newReport.setSchemaId(tbd.getId());
        PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(selectedPmOrder);
        if(pmOrderRule==null){
            pmOrderService.saveReport(newReport);
            return;
        }
        if (tbd.getBaseId().equalsIgnoreCase(pmOrderRule.getPerformanceReport()))
            newReport.setBoType("pm-performance-report");
        if (tbd.getBaseId().equalsIgnoreCase(pmOrderRule.getSafetyReport()))
            newReport.setBoType("pm-safety-report");
        pmOrderService.saveReport(newReport);
    }

    public List<ServiceRequestMsg> getServiceRequestMessageList(String pmOrderId) {
        return selected == null ? null : pmOrderService.getServiceRequestMessageList(pmOrderId);
    }

    public List<TableDefinition> getReportList(String siteUID) {
        OrgInfo site = configCacheService.findOrgByUid(siteUID);
        List<String> orgUids = Arrays.asList(site.getSiteUID(), site.getHospitalUID(), site.getInstitutionUID(), site.getTenantUID());
        return tableDao.getBySiteUIDIn(orgUids);
    }


    //todo
    public boolean hasRepairReport() {
        List<TableDefinition> tdList;
        List<TableDefinition> tdListTotal = new ArrayList<TableDefinition>();
        reportList = pmOrderService.getReportList(selectedPmOrder.getId());
        if (reportList != null) {
            for (int i = 0; i < reportList.size(); i++) {
                tdList = tableDao.getByBaseId(tableDao.findById(reportList.get(i).getSchemaId()).getBaseId());
                tdListTotal.addAll(tdList);
            }
        }
        return tdListTotal.stream().anyMatch(item -> item.getId().equalsIgnoreCase(reportUid));//reportUId: tableDefinition-id
    }

    /**
     * Check whether the target is selected.
     *
     * @return true if the mantenance report item has been selected, false otherwise.
     */
    public boolean isTargetSelected() {
        return !selectedMap.isEmpty() && selectedMap.values().contains(true);
    }

    /**
     * Generate maintenance report.
     */
    public void doExportMaintenanceReport() {
        maintenanceReport = null;
        try {
            maintenanceReport = pmReportService.generate(selectedMap, isInsulate, loginUser);
        } catch (BusinessException e) {
            logger.error("downLoad pm_word BusinessException ",e);
            WebUtil.addErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("downLoad pm_word Exception",e);
            Logger.getLogger(V2PmOrderController.class.getName()).log(Level.WARNING, "Error happens while generate maintenance report", e);
            WebUtil.addErrorMessage(WebUtil.getMessage("FailedToGenerateTheMaintenanceReport"));
        }
    }

    /**
     * Getter method for maintenance report.
     *
     * @return {@link StreamedContent} object that contains maintenance report.
     */
    public StreamedContent getMaintenanceReport() {
        return maintenanceReport;
    }

    /**
     * Setter for maintenance report.
     *
     * @param maintenanceReport {@link StreamedContent} object.
     */
    public void setMaintenanceReport(StreamedContent maintenanceReport) {
        this.maintenanceReport = maintenanceReport;
    }

    //是否全是批量完成
    public boolean isBatchComplete() {
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true)
                    pmList.add(item);
            });
        });
        //pmList存放的是所选中pm工单
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> item.getSimplify().equals(true));
        return mark;
    }

    public boolean isBatchComplete_backup() {
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true)
                    pmList.add(item);
            });
        });
        //pmList存放的是所选中pm工单
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> item.getSimplify().equals(true));
        return mark;
    }
    public boolean isBatchRob() {
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true)
                    pmList.add(item);
            });
        });
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> !item.getSimplify().equals(true));
        return mark;
    }


    public boolean isBatchTakePm() {
        // System.out.println(result);
        boolean mark = true;
        List<V2_PmOrder> pmOrderList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId().equalsIgnoreCase(k) && v == true){
                    pmOrderList.add(item);
                }
            });
        });
        //x3存放的是所选中pm工单
        if (pmOrderList.size() > 0)
            mark = pmOrderList.stream().anyMatch(item -> item.getSimplify().equals(true));
        return mark;//==false?true:false;
    }

    public Map<String,Boolean> sameOpHashmap = new HashMap<String,Boolean>();
    public boolean isChecked() {
        Map<String, Boolean> map = this.selectedMap;
        sameOpHashmap = map;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCheckedForBatchComplate() {

        if(null == pmIdList || pmIdList.size() == 0){
            return false;
        }
        WorkflowConfig wfc = configService.findConfigBySite(loginUser.getSiteUID());
        boolean autoAck = null == wfc || null == wfc.getAutoAckMaintenance() ? true : wfc.getAutoAckMaintenance();
        boolean match = dao.getBatchPmOrderList(this.pmIdList).stream().anyMatch(item -> autoAck != item.getAutoAckMaintenance());
        if(match){
            return false;
        }

        Map<String, Boolean> map = this.selectedMap;
        sameOpHashmap = map;
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if (entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    public String getTableDefName(String templateId) {
        TableDefinition tb = tableDao.findById(templateId);
        if (tb != null) {
            return tb.getName();
        } else {
            return String.format(WebUtil.getMessage("SchemaIdUnknowReport"), templateId);
        }
    }

    UnstructuredData untest;

    public void hmCheckSet(String key) throws Exception {
        //临时用hmCheck1  因为hmCheck的value 是String ?
        untest = (UnstructuredData) hmCheck.get(key);
    }

    private String qaStatus = "1";

    public void onQaStatusChange() {
    }

    public Boolean canCreateSr() {
        if (selectedAsset != null && selectedAsset.getIbStatus() != null && selectedAsset.getIbStatus() == 1) {
            if (selectedAsset.getRepairForm() != null && selectedAsset.getRepairForm() == 2) {
                if (null != selectedAsset.getGeDeviceType() && (selectedAsset.getGeDeviceType() == 22 || selectedAsset.getGeDeviceType() == 3)) {
                    return selectedAsset.getWarrantyDate() == null || selectedAsset.getWarrantyDate().before(new Date());
                }
                return false;
            }
        }
        return true;
    }
    //此方法暂时没用到
/*    public void saveCheckItemDataBatch(List<String> pmOrderNumList){
        List<V2_PmOrder> batchPmOrderList = dao.getBatchPmOrderList(pmOrderNumList);
        //循环处理每一条工单，将其保养规则对应的保养检查项目存到undatastructCollection和undatastruct中

        for (int i = 0; i < batchPmOrderList.size(); i++) {
            HashMap<String, UnstructuredData> hmCheckBatch = new HashMap<String,UnstructuredData>();
            V2_PmOrder pmOrder=batchPmOrderList.get(i);
            pmCheckItemList = acPmOrderUleCheckList(pmOrder);
            if (pmCheckItemList != null){
                pmCheckItemList.stream().filter(item-> !hmCheckBatch.containsKey(item.getUid()))
                        .forEach(item -> {
                            UnstructuredData unData = new UnstructuredData();
                            unData.setValue("pass");
                            unData.setKey(item.getUid());
                            hmCheckBatch.put(item.getUid(), unData);
                        });

            }
            Iterator<Map.Entry<String, UnstructuredData>> it = hmCheckBatch.entrySet().iterator();
            unDataCollection = reportDao.findCKByBoUuid(pmOrder.getId(),"pm-checklist-report");
            UnstructuredDataCollection unColl = new UnstructuredDataCollection();
            ArrayList<UnstructuredData> unD = new ArrayList<UnstructuredData>();
            if(unDataCollection.isEmpty()){
                unColl.setBoUuid(pmOrder.getId());
                unColl.setBoType("pm-checklist-report");
                PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(pmOrder);
                if (pmOrderRule != null) {
                    unColl.setSchemaId(pmOrderRule.getPerformanceReport());
                }
                while (it.hasNext()) {
                    Map.Entry<String, UnstructuredData> entry = it.next();
                    untest = entry.getValue();
                    if (untest.getCollectionId() == null) {
                        unD.add(untest);
                    }
                }
                unColl.setData(unD);
                pmOrderService.saveReport(unColl);
            }
        }
    }*/
    public void saveCheckItemData() {
        UnstructuredDataCollection unColl = new UnstructuredDataCollection();
        ArrayList<UnstructuredData> unD = new ArrayList<>();
        Iterator<Map.Entry<String, UnstructuredData>> it = hmCheck.entrySet().iterator();
        unDataCollection = reportDao.findCKByBoUuid(selectedPmOrder.getId(), "pm-checklist-report");
        try {
            if (unDataCollection.isEmpty()) {//检查项没有对应的unData即通过和不通过设置，保存新设置
                unColl.setBoUuid(selectedPmOrder.getId());
                unColl.setBoType("pm-checklist-report");
                PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(selectedPmOrder);
                if (pmOrderRule != null) {
                    unColl.setSchemaId(pmOrderRule.getUid());
                }
                //将检查项的数据保存到UnstructuredDataCollection中.
                while (it.hasNext()) {
                    Map.Entry<String, UnstructuredData> entry = it.next();
                    untest = entry.getValue();
                    if (untest.getCollectionId() == null) {
                        unD.add(untest);
                    }
                }
                unColl.setData(unD);
                pmOrderService.saveReport(unColl);//insert into unstructured_data_collection
            } else { //更新已经有的检查项
                //清空原来的unstructuredDataList
                unDataCollection.get(0).setData(new ArrayList<>());
                while (it.hasNext()) {
                    Map.Entry<String, UnstructuredData> entry = it.next();
                    untest = entry.getValue();
                    unDataCollection.get(0).getData().add(untest);
                }
                pmOrderService.saveReport(unDataCollection);
            }
            //  WebUtil.addSuccessMessage("保存检查项操作成功");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<UnstructuredDataCollection> findByBoUuidNoPMCheckList(String boUid) {
        return reportDao.findByBoUuidNoPMCheckList(boUid);
    }


    public void prepareAproval(String woid) {
        prepareAct(woid);
        selectedMap = new HashMap<>();
        approvedDetails = getWorkOrderDetail(selectedPmOrder.getId());
        approvedDetails.stream().forEach(item -> {
            if (item.getApproved() == null) {
                item.setApproved(false);
            }
            selectedMap.put(item.getId(), item.getApproved());
        });
    }


    public void onApprovedDetailsSelected(String id) {
        if ("All".equals(id)) {
            getNeedApprovalDetails().stream().filter(item -> !item.getApproved()).forEach(item -> selectedMap.put(item.getId(), selectedMap.get("All")));
        } else {
            selectedMap.put("All", !selectedMap.entrySet().stream().anyMatch(item -> !item.getKey().equals("All") && !item.getValue()));
        }
    }


    public void removeAttachmentByIdForRP(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picListForRP.removeAll(picListForRP.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void accessoryArrival(String itemId) {
        accessoryList.stream().filter(item -> itemId.equals(item.getId())).forEach(item -> item.setArrived(true));
    }

    public void removeAttachmentById(String blobId) {
        blobService.deleteBlobFileById(blobId);
        picList.removeAll(picList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
    }

    public void removeAttachmentByIdandType(String blobId, String type) {
        blobService.deleteBlobFileById(blobId);
        if (type.equals("after")) {
            picAfterList.removeAll(picAfterList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
        } else if (type.equals("before")) {
            picBeforeList.removeAll(picBeforeList.stream().filter(item -> item.getId().equals(blobId)).collect(Collectors.toList()));
        }
    }

    public boolean disableUploadButton(String type) {
        if (type.equals("after")) {
            return picAfterList.size() > 4;
        } else if (type.equals("before")) {
            return picBeforeList.size() > 4;
        }
        return false;
    }

    public List<UserAccount> findRepairPerson() {
        if (this.selectedPmOrder != null) {
            return woService.getWorkerList(this.selectedPmOrder.getAssetId());
        } else {
            return Collections.emptyList();
        }
    }

    public List<UserAccount> findRepairPersonBatch() {
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        currentPagePmOrders.stream().forEach(item -> {
            if (selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true) {
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        if (pmListTmp.size() > 0) { //如果是批量操作
            return pmOrderService.getWorkerList(pmListTmp.get(0).getAssetId());
        }
        if (this.selectedPmOrder != null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public void reassignWorkOrder() {
        if (isChecked() == false) {//非批量
            pmOrderService.reassignWorkOrder(selectedPmOrder, selectedPmOrder.getCurrentPersonId(), comments);
        } else {//批量派工
            pmOrderService.reassignWorkOrderBatch(currentPagePmOrders, selectedMap, currentPersonIdBatch, comments);
        }
        cancel();
    }



    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picList.add(blobObject);
            String keyByValue = V2_BlobObject.AttachmentType.getKeyByValue(fileTypeName);
//            blobObject.setBoType(V2_BlobObject.BO_TYPE_PmRequest);
            blobObject.setBoType(keyByValue);
            String fileType=blobObject.getObjectName().split("\\.")[blobObject.getObjectName().split("\\.").length-1];
            String name=fileTypeName+"_"+blobObject.getObjectName();
            blobObject.setObjectName(name);
            blobObject.setBoUuid(selectedPmOrder.getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadAfter(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picAfterList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PmOrder);
            blobObject.setBoUuid(selectedPmOrder.getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadBefore(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picBeforeList.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PrePmOrder);
            blobObject.setBoUuid(selectedPmOrder.getId());
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public void handleFileUploadForRP(FileUploadEvent event) {
        V2_BlobObject blobObject = blobService.uploadFile(event);
        if (null != blobObject) {
            picListForRP.add(blobObject);
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PmRequest);
            blobService.saveBlobObject(blobObject);
            WebUtil.addSuccessMessage(blobObject.getObjectName() + WebUtil.getMessage("SuccessUploaded"));
        } else {
            WebUtil.addErrorMessage("fail to upload file");
        }
    }

    public OrgInfo getOrgInfoByUid(String orgUid) {
        return configCacheService.findOrgByUid(orgUid);
    }

    public void savePM(){
        this.savePM(new PmOrderActionForm());
    }

    public void savePM(PmOrderActionForm formData) {
        if (selectedAsset.getStatus() == 5) {
            WebUtil.addErrorMessage(WebUtil.getMessage("DeactivatedCantRepair"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage(WebUtil.getMessage("CantRepairToBeTerminated"));
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("CantMaintenNoAssetDepartment"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            if (null != planTime) {
                formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
            }
            formData.setRuleLevel(ruleLevel);
            formData.setAttachments(picList);
            formData.setPmType(pmType);
            formData.setCustomerName(customerName);
            formData.setCustomerTel(customerName);
            formData.setSiteName(siteName);
            formData.setPmRuleUid(pmRuleUid);
            Map<String, Object> res = pmOrderService.createPMrequestApi(selectedAsset.getUid(), formData);
            if (res != null && res.get("bizStatusCode") != null) {
                if (res.get("bizStatusCode").equals("INVALID_PARAM")) {
                    WebUtil.addErrorMessage(res.get("message").toString());
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("invalidParam", true);
                } else {
                    // TODO: add check list
                }
            }
        }
    }

    public List<PmOrderSteps> aquirePmOrderStep() {
        return Optional.ofNullable(selected).map(V2_PmOrder::getId).map(pmOrderService::getPmOrderStep).orElse(ListUtil.empty());
    }

    public void createPMRequest() {
        cancel();
        if(StringUtils.isNotBlank(requestAssetUid)){
            Object  eventObject= pmOrderService.getAssetInfoByUid(requestAssetUid);
            this.onServerEvent("onAssetSelected",eventObject);
            requestAssetUid = null;
        }
    }

    public void onStatusChange() {
        if (null != selectedAsset) {//new sr
            needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
        } else {//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }
    }

    public boolean isFillRequired(HashMap<String,String> hashMap){
        if(!StringUtils.isEmpty(hashMap.get("pmReportNeedFill"))){
            WebUtil.addErrorMessage(
                hashMap.get("pmReportNeedFill") + " " + WebUtil.getMessage("NoFilled"));
            RequestContext.getCurrentInstance().addCallbackParam("reports", true);
            return true;
        }
        if(!StringUtils.isEmpty(hashMap.get("saftyReportNeedFill"))){
            WebUtil.addErrorMessage(
                hashMap.get("saftyReportNeedFill") + " " + WebUtil.getMessage("NoFilled"));
            RequestContext.getCurrentInstance().addCallbackParam("reports", true);
            return true;
        }
        return false;
    }
    public boolean requiredCheck() {


        if (selectedPmOrder.getManHours() == null && endTime == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ManhoursRequired"));
            RequestContext.getCurrentInstance().addCallbackParam("needManhours", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("EndTimeRequired"));
            RequestContext.getCurrentInstance().addCallbackParam("needendtime", true);
            return false;
        } else if (endTime == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("EndTimeRequired"));
            RequestContext.getCurrentInstance().addCallbackParam("needendtime", true);
            return false;
        } else if (selectedPmOrder.getManHours() == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ManhoursRequired"));
            RequestContext.getCurrentInstance().addCallbackParam("needManhours", true);
            return false;
        }
        if (selectedPmOrder.getPlanTime() == null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("PlannedCompletionTimeRequired"));
            RequestContext.getCurrentInstance().addCallbackParam("needplanTime", true);
            return false;
        }
        return true;
    }

    public void createServiceRequest(V2_PmOrder selectedPmOrder, int from) {
        //如果from 是1 表示是从保养对话框来的报修命令，就要校验一些必填项
        if (from == 1) {
            if (!requiredCheck()) {
                return;
            }
        }
        HashMap<String, String> hm = pmOrderService.fillInform(selectedPmOrder);
        //如果返回false的话就不用跳出提醒填写报告提示
        if(isFillRequired(hm)){
            return;
        }
        //去保修前把单子关了,把备件信息和协作人都传给后台
        if (accessoryList != null) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
            accessoryList.forEach(item -> item.setCreatedDate(null));
        }
        if (coWorkerList != null) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
            coWorkerList.forEach(item -> item.setCreatedDate(null));
        }
        selectedPmOrder.setQualityChecking(qaStatus);
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList, currentPersonIdBatch,picBeforeList,picAfterList, pmHoursList);
        //保存保养清单中的数据
        saveCheckItemData();
        selected = null;
        newServiceRequest = new V2_ServiceRequest();
       // String requestReason=null;

        String strPriority = JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].priority.defaultValue[0]").toString();
        String strAssetStatus =JSONPath.eval(currentCustomConfig,"$.mobilePage.pageWorkOrder.group[type='orderFields'].assetStatus.defaultValue[0]").toString();
        Object requestReason = JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='descriptions'].requestReason.defaultValue[0]");
        if(requestReason==null){
            newServiceRequest.setRequestReason("设备故障");
        }else{
            newServiceRequest.setRequestReason(requestReason.toString());
        }
        currentStatus= Integer.valueOf(strAssetStatus);
        newServiceRequest.setCasePriority(Integer.valueOf(strPriority));//紧急程度默认是设置为一般
        newServiceRequest.setStatus(currentStatus);

        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestorPhone(UserContextService.getCurrentUserAccount().getTelephone());
        newServiceRequest.setRequestTime(new Date());

        String repairSource = RepairSourceEnum.Maintenance.name();
        if(selectedPmOrder.getRuleLevel()!=null && selectedPmOrder.getRuleLevel()==4){
            repairSource=RepairSourceEnum.QualityControl.name();
        }
        newServiceRequest.setSrRepairSource(repairSource);
        if (selectedPmOrder != null) {
            selectedAsset = selectedPmOrder.getAssetInfo();
            currentStatus=3;
            final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
            dptName = configCacheService.findOrgByUid(orgInfoUID).getName();
            casePriority =3;//一般
        }
        Object repairReportSendPlaceFlag =JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.group[type='orderFields'].repairReportSendPlace.enable[0]");
        if(repairReportSendPlaceFlag != null){
            repaFlag= Boolean.valueOf(repairReportSendPlaceFlag.toString());
        }
        Object repairReportSendPlace=JSONPath.eval(currentCustomConfig, "mobilePage.pageWorkOrder.group[type='orderFields'].repairReportSendPlace.defaultValue[0]");
        if(repairReportSendPlace != null){
            newServiceRequest.setRepairReportSendPlace(repairReportSendPlace.toString());
        }
    }

    public void saveServiceRequest() {

        if(currentStatus!=2 && currentStatus!=3){
            WebUtil.addErrorMessage(WebUtil.getMessage("MustSelectCurrentStatus"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("status", true);
        }
        if(newServiceRequest.getRequestReason()==null){
            newServiceRequest.setRequestReason(WebUtil.getMessage("EquipmentFault"));
        }
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage(WebUtil.getMessage("CantRepairToBeTerminated"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage(WebUtil.getMessage("NoDepartmentCantRepair"));
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            //1表示主动报修
            newServiceRequest.setSrRepairType("1");
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(dptName);

            if(casePriority ==null) {
                newServiceRequest.setCasePriority(1);
            }else{
                newServiceRequest.setCasePriority(casePriority);
            }
            if(StringUtils.isBlank(newServiceRequest.getSrRepairSource())){
                newServiceRequest.setSrRepairSource(RepairSourceEnum.Maintenance.name());
            }
            woService.createServiceRequest(newServiceRequest, selectedAsset, picListForRP,null);
            //报修完后直接关单
            //pmOrderService.closePM(selectedPmOrder,picList);
            cancel();
        }
    }

    public void cancelsr() {
        selected = null;
        newServiceRequest = null;
        esTime = null;
        comments = null;
        failReason = null;
        rating = null;
        confirmedDownTime = null;
        equipmentTaker = null;
        takeTime = null;
        picList = null;
        picAfterList = null;
        picBeforeList = null;
        fileTypeName = null;
    }
    public void onSupplierChange() {
        Supplier supplier = (Supplier) selectedObjectMap.get("supplier");
        if (supplier == null)
            itemDetail.setSupplier(supplierName);
        else
            itemDetail.setSupplier(supplier.getName());
    }
    private PmOrderRuleCheckListRepository pmOrderRuleCheckListRepository;


    protected void init() {
        fileDownloadService = WebUtil.getBean(FileDownloadService.class);
        configItemValueRepository = WebUtil.getBean(ConfigItemValueRepository.class);
        fileScanService = WebUtil.getBean(FileScanService.class);
        tableService = WebUtil.getBean(TableDefinitionService.class);
        supplierController = WebUtil.getBean(SupplierController.class);
        customConfigService=WebUtil.getBean(CustomConfigService.class);
        currentCustomConfig = customConfigService.getCurrentCustomConfig(UserContextService.getCurrentUserAccount().getSiteUID());
        token = UserContextService.getAccessToken();
        partsConfigService = WebUtil.getBean(SimplePartsConfigService.class);
        pmOrderRuleCheckListRepository = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        serviceRequestApiService = WebUtil.getBean(ServiceRequestApiService.class);
        aimService = WebUtil.getBean(AssetInfoMgrService.class);
        tableDao = WebUtil.getBean(TableDefinitionRepository.class);
        unstructuredDataDao = WebUtil.getBean(UnstructuredDataRepository.class);
        pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        relRuleAssetRepository = WebUtil.getBean(RelRuleAssetRepository.class);
        assetInfoMapper = WebUtil.getBean(AssetInfoMapper.class);
        pmckLstDao = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        reportDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        pmOrderService = WebUtil.getBean(V2_PmOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        autoCompleteService = WebUtil.getBean(AutoCompleteService.class);
        orgInfoTreeService = WebUtil.getBean(OrgInfoTreeService.class);
        configService = WebUtil.getBean(ConfigService.class);
        v2_workOrderDetailRepository = WebUtil.getBean(V2_WorkOrderDetailRepository.class);
        partsRequestService = WebUtil.getBean(SimplePartsRequestService.class);
        partsInventoryService = WebUtil.getBean(SimplePartsInventoryService.class);
        partsItemService = WebUtil.getBean(SimplePartsItemService.class);
        partsRequestWorkOrderService = WebUtil.getBean(SimplePartsRequestWorkOrderService.class);
        UserContextService userContextService = WebUtil.getBean(UserContextService.class);
        this.selectedMap = new HashMap();
        pmReportService = WebUtil.getBean(PmReportService.class);
        multiLevelApprovalService= WebUtil.getBean(MultiLevelApprovalService.class);
        blobObjectRepository = WebUtil.getBean(BlobObjectRepository.class);
        workflowService = WebUtil.getBean(WorkflowService.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        assetTypeGroupRepository = WebUtil.getBean(AssetTypeGroupRepository.class);
        externalTempUserDao = WebUtil.getBean(ExternalTempUserRepository.class);
        loginUser = UserContextService.getCurrentUserAccount();
        // 用户可见资产分类
        assetTypeGroupList = loginUser.getAssetTypeGroupList();
        orgList = new ArrayList<>();
        orgListMVS = new ArrayList<>();
        //如果是院长等管理层登录进来queryIndex默认是8即当前就显示待审核
        String query = WebUtil.getRequestParameter("query");
        String enCodeParameters = WebUtil.getRequestParameter("str");
        if (!Strings.isNullOrEmpty(enCodeParameters)) {
            requestAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "assetUID");
            queryAssetUid = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "queryAssetUID");
            orderId = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "orderId");
            if (null != UrlEncryptController.getValueFromMap(enCodeParameters, "query")) {
                query = (String) UrlEncryptController.getValueFromMap(enCodeParameters, "query");
            }
        }
        if(!userContextService.hasAnyRole("AssetHead,AssetStaff")){
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = 8;
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        }else {
            if (Strings.isNullOrEmpty(query)) {
                if(userContextService.hasCurrentTenantName("")){
                    queryIndex = 10;
                }else{
                    queryIndex = -1;
                }
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        }
        for (ExportFields exs: ExportFields.values()
        ) {
            if(userContextService.isLocale()){
                pmFieldsExport.add(exs.getChinese());
            }else{
                pmFieldsExport.add(exs.getEnglish());
            }
        }


        this.filterBySite = false;
        this.filterByHospital = false;
        hmCheck = new HashMap<String, UnstructuredData>();
        isInsulate = tenantInfoRepository.findByuid(loginUser.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
        WorkflowConfig wfc = configService.findConfigBySite(loginUser.getSiteUID());
        if(wfc !=null)
            isBatchOp=wfc.getPmIsSupportBatchFinish();
        loadClinicalDeptList();
        loadRuleList();
        String configItemValue = configItemValueRepository.findByKeyAndSiteForUseParent(loginUser.getSiteUID(),loginUser.getHospitalUID(), loginUser.getInstitutionUID(), loginUser.getTenantUID(), "showLocationNameFlag");
        showLocationNameFlag =  StrUtil.isNotEmpty(configItemValue) && "true".equals(configItemValue);
    }



    public void loadRuleList(){
        String tenantUID = userContextService.getLoginUser().getTenantUID();
        ruleList = pmOrderRuleRepository.findPmRuleByTenant(tenantUID);
    }
    public void saveNewSupplier() {
        this.newSupplier.setSiteId(loginUser.getSiteId());
        this.newSupplier.setHospitalId(loginUser.getHospitalId());
        this.newSupplier.setTenantUID(loginUser.getTenantUID());
        this.newSupplier.setInstitutionUID(loginUser.getInstitutionUID());
        this.newSupplier.setHospitalUID(loginUser.getHospitalUID());
        this.newSupplier.setSiteUID(loginUser.getSiteUID());
        this.newSupplier.setCreatedDate(new Date());
        this.newSupplier.setCreatedBy(loginUser.getLoginName());

        supplierController.setSelected(newSupplier);
        supplierController.saveSubmit();
        supplierList.add(newSupplier);
        if (org.apache.commons.lang.StringUtils.isNotBlank(newSupplier.getUid()) && !supplierList.contains(newSupplier)) {
            supplierList.add(newSupplier);
        }
    }
    public void submitAppvalPmOrder() {
        if(StringUtils.isEmpty(comments)){
            comments = WebUtil.getMessage("QuotationApproval");
        }
        //先保存检查项
        saveCheckItemData();
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        accessoryList.forEach(item -> {
            item.setCreatedDate(null);
            if (null != item.getApproved() && !item.getApproved()) {
                item.setApproved(null);
            }
        });
        pmOrderService.submitWorkOrder(selectedPmOrder, accessoryList, approvaler, comments);
        cancel();
    }

    public void dispatchWorkOrder() {
        if (isChecked() == false) {
            Date planTime = selectedPmOrder.getPlanTime();
            pmOrderService.dispatchWorkOrder(selectedPmOrder, planTime);
            cancel();
        } else {//批量派工
            pmOrderService.dispatchWorkOrderBatch(currentPagePmOrders, selectedMap, currentPersonIdBatch, planTimeForBatch);
            cancel();
        }
    }

    public List<UserAccount> getWorkerList() {
        if (this.selectedPmOrder != null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public List<UserAccount> getWorkerListBatch() {
        List<V2_PmOrder> pmListTmp = new ArrayList<V2_PmOrder>();
        currentPagePmOrders.stream().forEach(item -> {
            if (selectedMap.containsKey(item.getId()) && selectedMap.get(item.getId()) == true) {
                //pmListTmp保存了所要提交的批量pmOrder
                pmListTmp.add(item);
            }
        });
        if (pmListTmp.size() > 0) { //如果是批量操作
            return pmOrderService.getWorkerList(pmListTmp.get(0).getAssetId());
        }
        if (this.selectedPmOrder != null)
            return pmOrderService.getWorkerList(selectedPmOrder.getAssetId());
        else
            return new ArrayList<UserAccount>();
    }

    public List<UserAccount> getApproveList(String name) {
        if (this.selectedPmOrder != null)
            return pmOrderService.getApproveList(selectedPmOrder.getAssetUid(),name);
        else
            return new ArrayList<UserAccount>();
    }

    private List<Integer> tagGroupAssets;

    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }
        //type =1 是接单 ,type =2 是抢单
    public void acceptWorkOrder(int type) {
        if (isChecked() == false) //如果是false说明不是批量的操作
        {
            Date planTime = selectedPmOrder.getPlanTime();
            pmOrderService.acceptWorkOrder(selectedPmOrder, planTime);
        } else { //批量操作
            //先判定 批量的里面会不会包含 待接单Tab中 二级简化中的"完成"保养单
            List<String> assetIdList = new ArrayList<String>();
            selectedMap.forEach((k, v) -> {
                if (v == true)
                    assetIdList.add(k);
            });
            List<V2_PmOrder> pmList = dao.checkSimplify(assetIdList);
            if (pmList.size() > 0) {
                WebUtil.addErrorMessage(
                    WebUtil.getMessage("CantContainWorkordersToBeAcceptedOrCompeted"));
                return;
            }
            pmOrderService.acceptWorkOrderBatch(currentPagePmOrders, selectedMap, planTimeForBatch,type);
        }
        cancel();
    }

    private List<Integer> tagNoGroupAssets;

    private List<Integer> getNoTagGroupAssets() {
        if (tagNoGroupAssets == null) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            if (orgUidList == null || orgUidList.isEmpty()) {
                orgUidList = Arrays.asList("");
            }
            tagNoGroupAssets = dao.getAssetsByNoTagGroup(orgUidList);
        }
        return tagNoGroupAssets;
    }

    //获取当前登录人可看到的设备, 这个人需要在标签组里面
    private List<Integer> getTagGroupAssets() {
        if (tagGroupAssets == null) {
            tagGroupAssets = dao.getAssetsByTagGroup(UserContextService.getCurrentUserAccount().getId());
        }
        return tagGroupAssets;
    }

    public void filterOutPm(List<SearchFilter> searchFilterList){
        searchFilterList.add(new SearchFilter("assetInfo.isDeleted", SearchFilter.Operator.NE,true));
//        searchFilterList.add(new SearchFilter("assetInfo.status", SearchFilter.Operator.NE,5));
//        searchFilterList.add(new SearchFilter("assetInfo.status", SearchFilter.Operator.NE,4));
//        searchFilterList.add(new SearchFilter("assetInfo.isValid", SearchFilter.Operator.EQ,true));
        searchFilterList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));
    }

    public void deleteReporter(String id) {
        if (null != id) {
            tableService.deleteReport(id);
        }
    }
    /*DISPATER(2,"待派工", "Assign"),ACCEPT(3,"待接单", "Accept"),
			CONFIRM(5,"待验收", "Ack"),CLOSE(6,"待关单", "Close"),EVALUATE(7,"待评价", "Rate"),APPROVAL(8, "待审核", "Approval"), MAINTAIN(0, "保养中", "Repair");*/
    Page<V2_PmOrder> result;

    List<SearchFilter> filterArrayList = new ArrayList<>();
    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        if (loginUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(assetTypeGroupList)) {
            return new PageImpl<>(ListUtil.empty(), pageRequest, 0);
        }
        this.setSearchFilter();

        if(queryIndex == 20){
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("currentStepName", SearchFilter.Operator.EQ,
                WebUtil.getMessage("Canceled")));
        }
        if(queryIndex==100){
            //from asset detail page
            filterOutPm(filterArrayList);
            if (!Strings.isNullOrEmpty(queryAssetUid)) {
                filterArrayList.add(new SearchFilter("assetInfo.uid", SearchFilter.Operator.EQ, queryAssetUid, "1*"));
            }
            if (!Strings.isNullOrEmpty(orderId)) {
                filterArrayList.add(new SearchFilter("id", SearchFilter.Operator.EQ, orderId, "1*"));
            }
        }
        if(queryIndex==10){
            pmIdList = new ArrayList<>();
            filterOutPm(filterArrayList);
            Sort sort= returnSort(pageRequest);

            pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);
            if("MVS保养".equals(tabTitle)){
                List<SearchFilter> filters = buildMvsSearchFilter();
                logger.info("loadData.MvsSearchFilter" +JSONObject.toJSONString(filters));
                result  = dao.findBySearchFilter(filters, pageRequest);
                bySearchFilterMVS = result.getContent();
                //loadMVSClinicalDeptList();
            }else{
                result = dao.findBySearchFilter(this.filterArrayList, pageRequest);
                bySearchFilterInternal = result.getContent();
            }
//            Page<V2_PmOrder> result =dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPagePmOrders = result.getContent();
            result.getContent().forEach(item->pmIdList.add(item.getId()));
            return result;
        }
        if(queryIndex==-2){
            pmIdList = new ArrayList<>();
            filterArrayList.add(new SearchFilter("simplify", SearchFilter.Operator.EQ, true));
        }
        if (queryIndex == -1) {//待办
            pmIdList = new ArrayList<>();
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, userContextService.getLoginUser().getUid(),"1*2+"));
            List<Sort.Order> orders = new ArrayList<Sort.Order>();
            orders.add(new Sort.Order(Sort.Direction.DESC,"planTime"));
            orders.add(new   Sort.Order(Sort.Direction.DESC,"pmNum"));
            Sort.Order orderFor = pageRequest.getSort().getOrderFor("planTime");
            //默认是计划完成时间按降序排,如果选择升续操作 就会加入下面的column
            if(orderFor!=null) {
                if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                    orders = new ArrayList<Sort.Order>();
                    orders.add(new Sort.Order(Sort.Direction.ASC, "planTime"));
                    orders.add(new Sort.Order(Sort.Direction.ASC, "pmNum"));
                }
            }else{
                //如果选择除计划完成时间的其它列 进行排序
                orders =  acqListSortOrder(pageRequest);
            }
            Sort sort = new Sort(orders);
            pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);
            if("MVS保养".equals(tabTitle)){
                List<SearchFilter> filters = buildMvsSearchFilter();
                result  = dao.findBySearchFilter(filters, pageRequest);
                bySearchFilterMVS = result.getContent();
               // loadMVSClinicalDeptList();
            }else{
                result = dao.findBySearchFilter(this.filterArrayList, pageRequest);
                bySearchFilterInternal = result.getContent();
            }
//            Page<V2_PmOrder> result = dao.findBySearchFilter(this.searchFilters, pageRequest);
           // result.getContent().forEach(item->pmIdList.add(item.getId()));
            currentPagePmOrders = result.getContent();
            return result;
        }
        if (queryIndex == 2) { //待派工  Assign
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign"));
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            filterArrayList.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
            filterArrayList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, sitelist, "1*3+"));

        } else if (queryIndex == 3) {//待接单
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept"));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            //someone could only access siteA,but assigned with asset on SiteB, therefore "OR" is added saying that one also could see the asset that is assigned to him.
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 31) {//待抢单 dispatch 2表示人工派工或抢单模式 3表示抢单模式 4表示人工派工
            isBatchOp=true;
            //抢单tab中 ,已经派的单但是未被接单人接的单子也要显示出来 (已经派的单状态是Accept，所以也要加进来)
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("dispatchMode", SearchFilter.Operator.NE, 4));
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign", "1*3+"));
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept", "1*3+7*"));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+7*"));
            filterArrayList.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            /*searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+4*"));*/
            //用户必须对其可视范围内的设备都可见,包括打过标签的设备(如果该设备的标签和组绑定了，并且当前用户没有在该组内，那是看不到的)和没有打过标签的设备
            //打过标签的设备(该标签都和组绑定过)
            List<Integer> tagGroupAssets = getTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                filterArrayList.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*4+"));
            } else {
                filterArrayList.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*4+"));
            }
            //没有打过标签的设备
            tagGroupAssets = getNoTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                filterArrayList.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*4+owner*"));
            } else {
                filterArrayList.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*4+owner*"));
            }
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            //维修责任人数量小于等于1的条件
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner1*"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner1*"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner2*"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner2*"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner3*"));
            filterArrayList.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner3*"));

            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
        } else if (queryIndex == 6) { //待关单
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Close"));
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 8) { //待审核
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
            //1表示该pm工单已被提交等待审核
            filterArrayList.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 0) { //已计划
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            filterArrayList.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            filterArrayList.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 11) { //待验收
            filterOutPm(filterArrayList);
            filterArrayList.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Acceptance"));
            filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));

        } else if (queryIndex == 7) { //已关单

            if (!userContextService.hasRole("AssetHead") && !userContextService.isPmDispatcher()) {
                filterArrayList.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
                filterArrayList.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            }
            filterArrayList.add(new SearchFilter("currentStepName", SearchFilter.Operator.EQ, "已关单"));
            filterArrayList.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, true));
        }
        this.selected = null;
        Sort sort = returnSort(pageRequest);
        pageRequest = new PageRequest(pageRequest.getPageNumber(),pageRequest.getPageSize(),sort);
        //查询Mvs保养
        if("MVS保养".equals(tabTitle)){
            List<SearchFilter> filters = buildMvsSearchFilter();
            result = dao.findBySearchFilter(filters, pageRequest);
            bySearchFilterMVS = result.getContent();
           // loadMVSClinicalDeptList();
        }else{
            result = dao.findBySearchFilter(this.filterArrayList, pageRequest);
            bySearchFilterInternal = result.getContent();
        }
        currentPagePmOrders = result.getContent();
        this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        if(queryIndex==20 || queryIndex== 8){
            pmIdList= new ArrayList<>();

        }
        if(selectedMap.size() > 0){
            selectedMap = new HashMap();
        }
        return result;
    }

    public String getFilterPmRuleLevel() {
        return filterPmRuleLevel;
    }

    public void setFilterPmRuleLevel(String filterPmRuleLevel) {
        this.filterPmRuleLevel = filterPmRuleLevel;
    }

    public List<SearchFilter> buildMvsSearchFilter(){

        List<SearchFilter> filters = new ArrayList<>();
        if (!Strings.isNullOrEmpty(orderId)) {
            filters.add(new SearchFilter("id", SearchFilter.Operator.EQ, orderId, "1*"));
        }else{
            TenantInfoRepository tenantDao=WebUtil.getBean(TenantInfoRepository.class);
            Optional<TenantInfo> tenantInfo = tenantDao.findFirstByName(ApmConstant.MVS_TENANT_NAME);
            String tenantUid = tenantInfo.get().getUid();
            filters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, tenantUid));
            List<String> assetUids = pmOrderService.getMvsAssectUidsStr(token);
            logger.info("pmorder.buildMvsSearchFilter" +JSONObject.toJSONString(assetUids));
            if (CollectionUtils.isEmpty(assetUids)) {
                assetUids.add("1++1");
            }
            filters.add(new SearchFilter("assetUid", SearchFilter.Operator.IN, assetUids));

          /*  List<String> orderIds = getVisableMvsAssetSystemId();
            if (CollectionUtils.isEmpty(orderIds)) {
                orderIds = new ArrayList<>();
                orderIds.add("0");
            }*/

            //filters.add(new SearchFilter("id", SearchFilter.Operator.IN, orderIds));
            //filters.add(new SearchFilter("mvsIbMapping.ibId", SearchFilter.Operator.IsNotNull, null));
            for (SearchFilter searchFilter : filterArrayList) {
                if (("mvsIbMapping.assetInfo.clinicalDeptUID,mvsIbMapping.assetInfo.siteUID,currentStepId,isFinished,dispatchMode,approvalStatus,currentStepName" +
                        ",mvsIbMapping.assetInfo.name,mvsIbMapping.assetInfo.serialNum,mvsIbMapping.assetInfo.departNum,mvsIbMapping.assetInfo.functionType,pmType,ruleLevel,pmNum,planTime").contains(searchFilter.fieldName)) {
                    filters.add(searchFilter);
                }
            }
        }
        return filters;
    }

    List<String> getVisableMvsAssetSystemId(){
        /*获取一脉阳光租户的mvs工单id*/
        TenantInfoRepository tenantDao=WebUtil.getBean(TenantInfoRepository.class);
        Optional<TenantInfo> tenantInfo = tenantDao.findFirstByName(ApmConstant.MVS_TENANT_NAME);
        String tenantUid = tenantInfo.get().getUid();

        /*根据登录进来的租户获取到该租户绑定的设备的uid*/
        String token = UserContextService.getAccessToken();
//        String to = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ5bXlnQWRtaW4iLCJhdXRoIjoieW15Z0FkbWluIiwidWlkIjoiYmI5MDQ5NDYxMWIxNDllYzk0MWMzZTUyMTUwZDQ0YjQiLCJ1c2VySWQiOjE4Mjg4LCJleHAiOjE2MzgzNDYzOTB9.S6s0ow5Ab9RUO5Efo9XG-lAQmQxbxrf6aDsuUsnApStJ3iCdE7bjJHLs6hKJAQK-QYY4i9w8VmTlps7WZk0-iA";
        List<String> orgUidList = pmOrderService.getMvsAssectUidsStr(token);
        if (CollectionUtils.isEmpty(orgUidList)) {
            orgUidList.add("1++1");
        }
        return assetInfoMapper.getMvsPmOrders(orgUidList,tenantUid);
    }

    //tab切换
    public void onTabChange(TabChangeEvent event) {
        FacesMessage msg = new FacesMessage("activeTab", event.getTab().getTitle());
        tabTitle = msg.getDetail();
        /*if("MVS工单".equals(tabTitle)){
        }*/
//        queryIndex = -1;
        selected = null;
    }
    public void updateQueryIndex(int query){
        queryIndex = query;
        selected = null;
    }

    protected Sort returnSort(PageRequest pageRequest){
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC,"planTime"));
        orders.add(new Sort.Order(Sort.Direction.DESC,"pmNum"));
        Sort.Order orderFor = pageRequest.getSort().getOrderFor("planTime");
        //默认是计划完成时间按降序排,如果选择升续操作 就会加入下面的column
        if(orderFor!=null) {
            if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.ASC, "planTime"));
                orders.add(new Sort.Order(Sort.Direction.ASC, "pmNum"));
            }
        }else{
            //如果选择除计划完成时间的其它列 进行排序
            orders =  acqListSortOrder(pageRequest);
        }
        Sort sort = new Sort(orders);
        return sort;

    }

    private  List<Sort.Order> acqListSortOrder(PageRequest pageRequest){
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        Sort sort1 = pageRequest.getSort();
        String filterName = sort1.toString().substring(0,sort1.toString().indexOf(":"));
        Sort.Order orderFor =  pageRequest.getSort().getOrderFor(filterName);
        if(orderFor!=null) {
            if (orderFor.getDirection().equals(Sort.Direction.ASC)) {
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.ASC, filterName));
            }else{
                orders = new ArrayList<Sort.Order>();
                orders.add(new Sort.Order(Sort.Direction.DESC, filterName));
            }
        }
     return orders;
    }

    public StreamedContent getExcelFile() {
        return excelFile;
    }

    public void onSelectPmOrder() {
        accessoryList = getWorkOrderDetail(selected.getId());
        coWorkerList = getWorkOrderCoworker(selected.getId());
        // 获取picList
        picList = blobDao.findByBoUuid(selected.getId());
        reportList = pmOrderService.getReportList(selected.getId());

        loadHmCheck(selected);
        // 获取temp user信息
        initExternalTempUser();
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return pmOrderService.getWorkOrderCoworker(woId);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetailON() {

        return Optional.ofNullable(selected).map(V2_PmOrder::getId).map(pmOrderService::getWorkOrderDetails).orElse(ListUtil.empty());
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworkerON(String woId) {
        return Optional.ofNullable(selected).map(V2_PmOrder::getId).map(pmOrderService::getWorkOrderCoworker).orElse(ListUtil.empty());
    }


    public void closePM(V2_PmOrder selectedPmOrder) {
        if (isChecked() == false) {
            pmOrderService.closePM(selectedPmOrder, picList);
        } else {
            picList.stream().forEach(item -> {
                item.setId(null);
            });
            currentPagePmOrders.stream().forEach(item -> {
                item.setQualityChecking("1");
            });
            pmOrderService.closePMBatch(currentPagePmOrders, selectedMap, picList);
        }
        cancel();
    }

    //审批
    public boolean approveNotes() {
        //如果发现有一个是true的  就返回false，否则 全是false 返回true
        return !selectedMap.entrySet().stream().anyMatch(item -> item.getValue());
    }

    public void getSignature() {
        this.signatureContent = null;
        this.signatureContentStream = null;

        List<Map<String, String>> signatureList = woService.getUserSignature();

        if (!org.springframework.util.CollectionUtils.isEmpty(signatureList)) {
            String lastSignatureId = signatureList.get(signatureList.size() - 1).get("signatureUid");
            this.signatureContentStream = blobService.getBlobObjectByStorageId(lastSignatureId);
            this.signatureId = lastSignatureId;
            this.newSignature = false;
        }else{
            this.newSignature = true;
        }
    }

    public void resetSignature() {
        this.signatureId = null;
        this.signatureContent = null;
        this.signatureContentStream = null;
        this.newSignature = true;
    }

    public void preApprovePMOrder(){
        // 审核不通过，必须填写备注
        if (approvedDetails.stream().anyMatch(item -> !selectedMap.containsKey
                (item.getId()) || !selectedMap.get(item.getId())) && Strings.isNullOrEmpty(comments)) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ApprovalFallureReasonRequired"));
            RequestContext.getCurrentInstance().addCallbackParam("needComents", true);
            return;
        }

        // 备件审核是否需要签名
        WorkflowConfig workflowConfig = workflowService.getWorkflowByOrg(this.selectedPmOrder.getSiteUID());

        if(workflowConfig != null && workflowConfig.getPartsNeedSign() != null && workflowConfig.getPartsNeedSign()){
            // 如果勾选了‘备件审核需要签名’，校验是否已经签名 弹出签名窗口
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            response.setStatus(205);
            return;
        }
        approvePMorder();
    }

    public void saveSignatureAndSubmitApprove(){
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();

        if(StringUtils.isEmpty(this.signatureId) && (this.newSignature && StringUtils.isEmpty(this.signatureContent))){
            WebUtil.addErrorMessage("签名不可为空！");
            response.setStatus(206);
            return;
        }

        if(this.newSignature){
            if (StringUtils.isNotEmpty(signatureContent)) {

                try {
                    String base64 = signatureContent.substring(signatureContent.indexOf(",") + 1);
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte[] decode = decoder.decodeBuffer(base64);

                    V2_BlobObject blobObject = blobService.uploadFile(new ByteArrayInputStream(decode), "signature.png");
                    if (null != blobObject) {
                        this.signatureId = blobObject.getObjectStorageId();
                        woService.addUserSignature(this.signatureId);
                    }else{
                        WebUtil.addErrorMessage("签名保存失败！");
                        response.setStatus(206);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGGER.error(e.getMessage());
                    WebUtil.addErrorMessage("签名保存失败！");
                    response.setStatus(206);
                    return;
                }
            }
        }
        approvePMorder();
    }

    public void approvePMorder() {
        List<V2_WorkOrder_Detail> approvedDetailsNew = new ArrayList<>();
       if (selectedPmOrder != null) {
           selectedPmOrder.setPartApproveSignature(this.signatureId);
           for (int i = 0; i < approvedDetails.size(); i++) {
               V2_WorkOrder_Detail v2_workOrder_detail = approvedDetails.get(i);
               if (selectedMap.get(v2_workOrder_detail.getId())) {
                   if (v2_workOrder_detail.getPartsItemId() != null){
                       if (!v2_workOrder_detail.getApproved()) {
                           approvedDetailsNew.add(v2_workOrder_detail);
                       }
                       v2_workOrder_detail.setApproved(true);
                   }
               }
           }
           if(partsConfigService.isEnableIntegrationWithWorkOrder()){
               //最后一个审批通过的时候 生成相应单子
               List<V2_WorkOrder_Detail> accessorySparePartsBuyList = new ArrayList<>();//申购
               List<V2_WorkOrder_Detail> accessorySparePartsRequestList = new ArrayList<>();//申领
               if (!approvedDetailsNew.isEmpty()) {
                   for (int i = 0; i < approvedDetailsNew.size(); i++) {
                       V2_WorkOrder_Detail v2_workOrder_detail = approvedDetailsNew.get(i);
                       if (ApmConstant.TREASURY_USE.equals(v2_workOrder_detail.getPartsType())) {//库房领用
                           accessorySparePartsRequestList.add(v2_workOrder_detail);
                       } else if (ApmConstant.MEDICAL_WORKERS_PURCHASING.equals(v2_workOrder_detail.getPartsType())) {//医工采购
                           accessorySparePartsBuyList.add(v2_workOrder_detail);
                       }
                   }
                   if (!accessorySparePartsRequestList.isEmpty()) {
                       V2_PmOrder v2PmOrder = dao.findById(accessorySparePartsRequestList.get(0).getWoId());
                       partsRequestWorkOrderService.savePartsRequest(accessorySparePartsRequestList, SimplePartsConstant.PM_ORDER, userDao.getByUid(v2PmOrder.getOwnerId()));//生成申领单
                   }
                   if (!accessorySparePartsBuyList.isEmpty()) {
                       V2_PmOrder v2PmOrder = dao.findById(accessorySparePartsBuyList.get(0).getWoId());
                       partsRequestWorkOrderService.savePartsPurchase(accessorySparePartsBuyList, SimplePartsConstant.PM_ORDER, userDao.getByUid(v2PmOrder.getOwnerId()));//生成申购单
                   }
               }
           }
            approvedDetails.stream().filter(item -> Optional.ofNullable(selectedMap.get(item.getId())).orElse(false)).forEach(item -> item.setApproved(true));
            approvedDetails.forEach(item -> {
                item.setCreatedDate(null);
                if (null == item.getApproved()) {
                    item.setApproved(false);
                }
            });
         pmOrderService.approvePMorder(selectedPmOrder, approvedDetails, this.comments);
            this.comments = null;
            approvedDetails = null;
            resetSignature();
        }
    }

    // 走多级审批流,提交审核
    public void submitForApproval() throws Exception {
        if(selectedPmOrder ==null){
            return;
        }
        BigDecimal totalPriceToBigDecimal = BigDecimal.ZERO;
        if(accessoryList!=null && !accessoryList.isEmpty()){
            for (V2_WorkOrder_Detail detail:accessoryList) {
                if(detail.getApproved()==null || !detail.getApproved()){
                    BigDecimal bigDecimal = new BigDecimal(detail.getClosedPrice()==null?0:detail.getClosedPrice()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    totalPriceToBigDecimal = totalPriceToBigDecimal.add(bigDecimal);
                }
            }
        }
        selectedPmOrder.setTotalPriceToBigDecimal(totalPriceToBigDecimal);
        MultiLevelApproval approval = multiLevelApprovalService.getMultiLevelApprovalPersonAndSubmit(selectedPmOrder, null, false);
        if (approval == null) {
            WebUtil.addErrorMessage("审批流发起失败,请配置审批流!");
            return ;
        }
        selectedPmOrder.setApprovalStatus(PmConstant.PM_APPROVAL_STATUS_PENDING);
        dao.save(selectedPmOrder);
        WebUtil.addErrorMessage("审批发起成功");
        return ;
    }

    public void prepareCompleteOrder(String woid) throws Exception {
        accessoryList = getWorkOrderDetail(woid);
        selectedPmOrder = dao.findById(woid);
        pmHoursList=selectedPmOrder.getPmOrderManHoursDetails();
        MultiLevelApprovalConfig approval = multiLevelApprovalService.checkMultiLevelApprovalPersonAndSubmit(selectedPmOrder);
        hasConfigMultiLevelApproval = false;
        if (approval != null) {
            logger.info("保养------------多级审核已激活");
            hasConfigMultiLevelApproval = true;
        }
        confirmedUpDate = null;
        endTime = selectedPmOrder.getEndTime();
        planTime = selectedPmOrder.getPlanTime();
        accessoryList = getWorkOrderDetail(woid);
        coWorkerList = getWorkOrderCoworker(woid);
        loadHmCheck(selectedPmOrder);

        Integer oldEntryType=selectedPmOrder.getEntryType();
        Integer oldCoworkerEntryType = selectedPmOrder.getCoworkerEntryType();
//        if(selectedPmOrder.getEntryType() == null){
            String value = configItemValueRepository.findByKeyAndSiteForUseParent(selectedPmOrder.getSiteUID(), selectedPmOrder.getHospitalUID(), selectedPmOrder.getInstitutionUID(), selectedPmOrder.getTenantUID(), "pmEntryTypeSet");
            if(StrUtil.isNotEmpty(value)){
                FieldColumnDto fieldColumnDto = JSON.parseObject(value, FieldColumnDto.class);
                if(fieldColumnDto.getEntryType() != null){
                    if(selectedPmOrder.getEntryType() == null){
                        selectedPmOrder.setEntryType(fieldColumnDto.getEntryType());
                        if(fieldColumnDto.getEntryType()==0 || fieldColumnDto.getEntryType()==2){
                            selectedPmOrder.setManHours(fieldColumnDto.getHours());
                        }
                    }else{
                        if(fieldColumnDto.getEntryType()==2 || fieldColumnDto.getEntryType()==3){
                            selectedPmOrder.setEntryType(fieldColumnDto.getEntryType());
                        }
                        if((oldEntryType==1 || oldEntryType==3) &&  fieldColumnDto.getEntryType() ==2){
                            selectedPmOrder.setManHours(fieldColumnDto.getHours());
                        }
                        if((oldEntryType==0 || oldEntryType==2) && fieldColumnDto.getEntryType() ==3){
                            selectedPmOrder.setManHours(null);
                        }
                    }

                    if(selectedPmOrder.getCoworkerEntryType() == null){
                        selectedPmOrder.setCoworkerEntryType(fieldColumnDto.getEntryType());
                    }else{
                        if(fieldColumnDto.getEntryType()==2 || fieldColumnDto.getEntryType()==3){
                            selectedPmOrder.setCoworkerEntryType(fieldColumnDto.getEntryType());
                        }

                        if((oldCoworkerEntryType==0 &&  fieldColumnDto.getEntryType() ==3  ) ||  (oldCoworkerEntryType==1 &&  fieldColumnDto.getEntryType() ==2  )){
                            coWorkerList=new ArrayList<>();
                        }
                    }
                }
            }

//            if(selectedPmOrder.getManHours() == null){
//                selectedPmOrder.setManHours(0.1);
//            }
//        }

//        if (pmCheckItemList != null){
//        pmCheckItemList.stream().filter(item-> !hmCheck.containsKey(item.getUid()))
//                .forEach(item -> {
//                    UnstructuredData unData = new UnstructuredData();
//                    unData.setValue("pass");
//                    unData.setKey(item.getUid());
//                    unData.setName(item.getName());
//                    unData.setRemark(item.getRemark());
//                    hmCheck.put(item.getUid(), unData);
//                });
//
//        }
//        picList = blobDao.fetchBlobObjectByUuidAndType(selectedPmOrder.getId(), V2_BlobObject.BO_TYPE_PmRequest);
        // 验收人
        if (Boolean.TRUE.equals(selectedPmOrder.getSimplify())) {
            currentPersonIdBatch = loginUser.getUid();
        } else {
            currentPersonIdBatch = selectedPmOrder.getAcceptPerson();
        }
        if (ObjectUtil.isNotEmpty(pmCheckItemList)) {
            for (UnstructuredData data : pmCheckItemList) {
                hmCheck.put(data.getKey(), data);
            }
        }
        picList = blobDao.findByBoUuid(selectedPmOrder.getId());
        if(CollectionUtils.isNotEmpty(picList)){
            picList = picList.stream().filter(item -> !(item.getBoType().equals(V2_BlobObject.BO_TYPE_PrePmOrder)  ||  item.getBoType().equals(V2_BlobObject.BO_TYPE_PmOrder) )   ).collect(Collectors.toList());
        }
        picBeforeList = blobDao.fetchBlobObjectByUuidAndType(selectedPmOrder.getId(), V2_BlobObject.BO_TYPE_PrePmOrder);
        picAfterList = blobDao.fetchBlobObjectByUuidAndType(selectedPmOrder.getId(), V2_BlobObject.BO_TYPE_PmOrder);
        if( CollectionUtils.isEmpty(picList)){
           picList = new ArrayList<V2_BlobObject>();
       }
        if( CollectionUtils.isEmpty(picBeforeList)){
            picBeforeList = new ArrayList<V2_BlobObject>();
        }
        if( CollectionUtils.isEmpty(picAfterList)){
            picAfterList = new ArrayList<V2_BlobObject>();
        }
        picListForRP = new ArrayList<V2_BlobObject>();
    }


    public void onSelectAllOfPage() {
        boolean isSelectedAll = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        for (V2_PmOrder item : currentPagePmOrders) {
            selectedMap.put(item.getId(), !isSelectedAll);
        }
        pmIdList = new ArrayList<>();
        selectedMap.forEach((k, v) -> {
            if (v == true)
                pmIdList.add(k);

        });
    }

    public void onItemSelected() {
        isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        selectedPmOrder = null;//
    }

    @Override
    public void setSearchFilter() {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList();
        }
        this.updateSearchFilter(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getTenantUID(), "1*"));
        filterArrayList.clear();
        if (ObjectUtil.isNotEmpty(selectedAccessOrgNodes)) {
            List<String> selectedSiteList = ListUtil.newArrayList();
            for (TreeNode selectedAccessOrgNode : selectedAccessOrgNodes) {
                OrgInfo orgInfo =(OrgInfo) selectedAccessOrgNode.getData();
                selectedSiteList.add(orgInfo.getUid());
            }
            filterArrayList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
            filterArrayList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, selectedSiteList, "1*3+"));
        }
        updatePMRuleFilterChange();
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if ("MVS保养".equals(tabTitle)){
            if (StringUtils.isNotEmpty(siteUidFilterMVS)) {
                searchFilters.add((new SearchFilter("mvsIbMapping.assetInfo.siteUID", SearchFilter.Operator.EQ, siteUidFilterMVS, "1*")));
            }
            if (clinicalDeptOrgUidSetMVS != null && !clinicalDeptOrgUidSetMVS.isEmpty()) {
                filterArrayList.add((new SearchFilter("mvsIbMapping.assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSetMVS), "1*")));
            }
        } else {
            if (StringUtils.isNotEmpty(siteUidFilter)) {
                searchFilters.add((new SearchFilter("siteUID", SearchFilter.Operator.EQ, siteUidFilter, "1*")));
            }
            if (clinicalDeptOrgUidSet != null && !clinicalDeptOrgUidSet.isEmpty()) {
                filterArrayList.add((new SearchFilter("clinicalDeptId", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*")));
//                this.updateSearchFilter(new SearchFilter("clinicalDeptId", SearchFilter.Operator.IN, new ArrayList<>(clinicalDeptOrgUidSet), "1*"));
            } else {
//                searchFilters.removeAll(searchFilters.stream().filter(item -> "clinicalDeptId".equals(item.fieldName) && item.conditionPath.equals("1*")).collect(Collectors.toList()));
            }
        }
        filterArrayList.addAll(searchFilters);

            //先获取 filterOrgName-手术科的 uid
          //  List<String> orgUidList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
           // List<String> filterOrg = orgDao.getOrgBySite(orgUidList, fileterOrgName);

        if (!this.hasFilterOnField("assetInfo.siteUID") && !"MVS保养".equals(tabTitle)) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            filterArrayList.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            filterArrayList.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));//所属科室
            filterArrayList.add(new SearchFilter("assetInfo.assetDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));//使用科室
        }

        // 资产分类权限
        if (loginUser.isEnableAssetTypeGroup() && !CollectionUtils.isEmpty(assetTypeGroupList)) {
            List<String> encodedList = assetTypeGroupList.stream().map(AssetTypeGroup::getEncoded).collect(Collectors.toList());
            filterArrayList.add(new SearchFilter("assetInfo.assetType", SearchFilter.Operator.IN, new ArrayList<>(encodedList), "1*"));
        }
    }

    public void searchList(Integer buttonIndex) {
        pmIdList = new ArrayList<String>();
        this.selectedMap = new HashedMap<>();
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        cancel();
    }

    public void cancel() {
        selectedAsset = null;planTime  = null;planTimeForBatch = null;
        selectedPmOrder = null;assetName = null;endTime = null;
        ruleLevel = null;dptName = null;picList = null;
        planTimeForBatch = null;comments = null;assetUid = null;
        approvaler = null;
        resetSignature();
    }

    public void prepareActTakeGrap(String woid, String head) {
        this.setHead(head);
        if (woid != null && !woid.isEmpty()) {
            selectedPmOrder = dao.findById(woid);
            //关单时 默认设置为合格
            selectedPmOrder.setQualityChecking("1");
        }
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        picList = new ArrayList<V2_BlobObject>();
    }

    public void prepareAct(String woid) {
        if (woid != null && !woid.isEmpty()) {
            selectedPmOrder = dao.findById(woid);
            //关单时 默认设置为合格
            selectedPmOrder.setQualityChecking("1");
        }
        confirmedUpDate = null;
        confirmedDownTime = null;
        needConfirmTime = false;
        picList = new ArrayList<V2_BlobObject>();
    }


    //已计划-->批量保养完成
    public void repairPmOrderBatch() {
//        保存检查项
       // saveCheckItemDataBatch(orderNumList);
        pmOrderService.repairWorkOrderBatch(endTime, totalManHours, selectedMap, currentPersonIdBatch, comments);
        selectedMap = new HashMap<String, Boolean>();
    }


    //已计划-->保养完成
    public void repairPmOrder() {
        HashMap<String, String> hm = pmOrderService.fillInform(selectedPmOrder);
        //如果返回false的话就不用跳出提醒填写报告提示
        if(isFillRequired(hm)||!requiredCheck()){
            return;
        }

        //保存检查项
        saveCheckItemData();
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        selectedPmOrder.setQualityChecking(qaStatus);
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList, currentPersonIdBatch, picBeforeList, picAfterList, pmHoursList);
    }

    public void stagingPmOrder() {
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        pmOrderService.stagingPmOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList, currentPersonIdBatch, picBeforeList, picAfterList, pmHoursList);
        //保存检查项
        saveCheckItemData();
        cancel();
    }

    private void loadClinicalDeptList() {
        try {
            /*clinicalDeptList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid()).stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());*/
            clinicalDeptList = Collections.emptyList();
            List<OrgInfo> deptList = uaaService.getAllAccessibleClinicalDeptList();
            if (!org.springframework.util.CollectionUtils.isEmpty(deptList)) {
                clinicalDeptList = deptList.stream()
                        .filter(item ->
                        {
                            if (item == null) {
                                LOGGER.info("null");
                            }
                            return item.getOrgType() != null && item.getOrgType() == 4;
                        })
                        .collect(Collectors.toList());
            }

            if (null != selectSite && !clinicalDeptList.isEmpty()) {
                clinicalDeptList = clinicalDeptList.stream()
                        .filter(item -> com.alibaba.druid.util.StringUtils.equals(item.getSiteUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getHospitalUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getInstitutionUID(), selectSite.getSiteUID())
                                || com.alibaba.druid.util.StringUtils.equals(item.getTenantUID(), selectSite.getSiteUID()))
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error("loadClinicalDeptList occur exception", e);
            throw e;
        }
    }

    public void setSearchFilterForOrg(){
        filterArrayList.removeAll(filterArrayList.stream().filter(item -> item.conditionPath.equals("1*5+")).collect(Collectors.toList()));
        if (null != selectSite) {
            filterArrayList.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            filterArrayList.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            filterArrayList.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
            filterArrayList.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selectSite.getUid(), "1*5+"));
        }
    }
    public void onSelectSiteTreeNode(NodeSelectEvent event) {
        this.searchFilters.clear();
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        clinicalDeptOrgUidSet = null;
        setTreeNodes(event.getTreeNode(), true);
    }


    public void onUnSelectSiteTreeNode(NodeUnselectEvent event) {
        this.searchFilters.clear();
        loadClinicalDeptList();
        filterClinicalDeptOrg = null;
        filterAssetDeptOrg = null;
        clinicalDeptOrgUidSet = null;
        TreeNode node = event.getTreeNode();
        setTreeNodes(node, false);
        while (node.getParent() != null) {
            node.getParent().setSelected(false);
            node = node.getParent();
        }
    }

    private void setTreeNodes(TreeNode root, Boolean isSelected) {
        root.setSelected(isSelected);
        for (TreeNode item : root.getChildren()) {
            setTreeNodes(item, isSelected);
        }
    }

    private void loadClinicalDeptListForFilter() {
        clinicalDeptList = uaaService.getUserAccessibleSiteListWithClinicalDept(UserContextService.getCurrentUserAccount().getUid())
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        clinicalDeptList = uaaService.getAllAccessibleClinicalDeptList()
                .stream().filter(item -> item.getOrgType() == 4).collect(Collectors.toList());
        if (null != selectSite) {
            clinicalDeptList = clinicalDeptList.stream().filter(item -> item.getSiteUID().equals(selectSite.getSiteUID()) || item.getHospitalUID().equals(selectSite.getSiteUID())
                    || item.getInstitutionUID().equals(selectSite.getSiteUID()) || item.getTenantUID().equals(selectSite.getSiteUID())).collect(Collectors.toList());
        }
    }
    public List<String> autoCompleteDeptName(String query) {
         if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public List<String> autoCompleteDeptNameMVS(String query) {
        if (clinicalDeptListMVS == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptListMVS.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res.stream().map(item -> item.getName()).distinct().collect(Collectors.toList());
    }

    public void onClinicalDeptNameFilterChange(AjaxBehaviorEvent event) {
        selectedMap.clear();
        if (event instanceof SelectEvent) {
            fileterOrgName = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgName) && !clinicalDeptList.stream().anyMatch(item -> fileterOrgName.equals(item.getName()))) {
            fileterOrgName = null;
        }

        if (fileterOrgName != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptList.stream().filter(item -> item.getName().equals(fileterOrgName)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptList);
            clinicalDeptOrgUidSet = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSet = Collections.emptySet();
        }
    }

    public void onClinicalDeptNameFilterChangeMVS(AjaxBehaviorEvent event) {
        if (event instanceof SelectEvent) {
            fileterOrgNameMVS = (String) ((SelectEvent) event).getObject();
        }

        if (!Strings.isNullOrEmpty(fileterOrgNameMVS) && !clinicalDeptListMVS.stream().anyMatch(item -> fileterOrgNameMVS.equals(item.getName()))) {
            fileterOrgNameMVS = null;
        }

        if (fileterOrgNameMVS != null) {
            List<OrgInfo> orgInfosMatchingByName = this.clinicalDeptListMVS.stream().filter(item -> item.getName().equals(fileterOrgNameMVS)).collect(Collectors.toList());

            Set<OrgInfo> matchedList = orgInfoTreeService.collectChildClinicalDeptInExstingList(orgInfosMatchingByName, clinicalDeptListMVS);
            clinicalDeptOrgUidSetMVS = matchedList.stream().map(OrgInfo::getUid).collect(Collectors.toSet());
        } else {
            clinicalDeptOrgUidSetMVS = Collections.emptySet();
        }
    }


    public void autoCompleteRuleName(){
        System.out.println("00");
    }
/*
*  获取租户下的所有保养规则
*  ruleList
* */

    public void onRuleFilterChange(AjaxBehaviorEvent event){
        if (event instanceof SelectEvent) {
            filterRule = (String) ((SelectEvent) event).getObject();
        }
        if (!Strings.isNullOrEmpty(filterRule) && !ruleList.stream().anyMatch(item -> filterRule.equals(item.getName()))) {
            filterRule = null;
        }

        if (filterRule != null) {
            List<PmOrderRule> ruleMatchingByName = this.ruleList.stream().filter(item -> item.getName().equals(filterRule)).collect(Collectors.toList());
            ruleStrList = ruleMatchingByName.stream().map(PmOrderRule::getUid).collect(Collectors.toList());
        }
    }


    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        assetUid = asset.getUid();
        assetName = asset.getName();
        dptName = asset.getClinicalDeptName();//orgDao.getByUid(orgInfoUID).getName();
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;
        if(userContextService.isCountyArea()){
            OrgInfo orgInfo = configCacheService.findOrgByUid(asset.getSiteUID());
            customerName = orgInfo.getContactPerson();
            customerTel = orgInfo.getContactPhone();
            siteName = orgInfo.getName();
        }
        autoSetPmRuleUid(assetUid,ruleLevel);
    }

    private void autoSetPmRuleUid(String assetUid, Integer ruleLevel) {
        if(StringUtils.isNotEmpty(assetUid)&&ruleLevel != null){
            RelRuleAsset relRuleAsset = relRuleAssetRepository.findByAssetUid(assetUid);
            List<String> ruleUids = new ArrayList<>();
            if(relRuleAsset != null) {
                switch (ruleLevel) {
                    case 1:
                        if (relRuleAsset.getRuleUID1() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID1(), "|"));
                        break;
                    case 2:
                        if (relRuleAsset.getRuleUID2() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID2(), "|"));
                        break;
                    case 3:
                        if (relRuleAsset.getRuleUID() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID(), "|"));
                        break;
                    case 4:
                        if (relRuleAsset.getRuleUID4() != null)
                            ruleUids = Arrays.asList(StringUtils.split(relRuleAsset.getRuleUID4(), "|"));
                        break;
                    default:
                        LOGGER.warn("保养等级不符合要求，其值为{}", ruleLevel);
                }
            }
            if(ruleUids.size() == 1){
                pmRuleUid = ruleUids.get(0);
            }else{
                pmRuleUid = null;
            }

        }
    }


    public void prepareEditAccDetail(String itemId) throws Exception{
        statusFlag = WorkOrderConstant.STATUS_FLAG_OTHER;
        Object eval;
        if ("new".equals(itemId)) {
            switchTmp=true;
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setId(null);
            itemDetail.setWoId(selectedPmOrder.getId());
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            itemDetail.setApplyTime(sdf.parse(sdf.format(new Date())));
            selectedObjectMap  = new HashMap();
            supplierName = null;
            currentCustomConfig = customConfigService.getCurrentCustomConfig(selectedPmOrder.getSiteUID());
            eval = JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType");
            itemDetail.setPartsType(String.valueOf(JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType")));
            String partsType = String.valueOf(JSONPath.eval(currentCustomConfig, "$.mobilePage.pageWorkOrder.accessoryDefaultType"));
            if (WorkOrderConstant.PARTS_TYPE_RECEIVING.equals(partsType)) {
                statusFlag = WorkOrderConstant.STATUS_FLAG_RECEIVING;
            }
            if (WorkOrderConstant.PARTS_TYPE_PURCHASE.equals(partsType)) {
                statusFlag = WorkOrderConstant.STATUS_FLAG_PURCHASE;
            }
        } else {
            switchTmp=false;
            if (accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().isPresent()) {
                itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
            }
            supplierName = itemDetail.getSupplier();
        }
        supplierList = aimService.getSuplierListBySiteUid(selectedPmOrder.getTenantUID());
        nochange=false;
    }

    public void removeWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
        //调用微服务删除备件
        serviceRequestApiService.delAccess(token,itemId);
    }
    public void prepareBatchComplete() {
        this.endTime = null;
        this.totalManHours = null;
    }

    private V2_WorkOrderCoworker coWorkerDetailOld;
    public void prepareEditCoWorkerDetail(String itemId) {
        if ("new".equals(itemId)) {
            coWorkerDetail = new V2_WorkOrderCoworker();
            coWorkerDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            coWorkerDetail.setWoId(selectedPmOrder.getId());
            coWorkerDetailOld=null;
        } else {
            if (coWorkerList.stream().filter(item -> item.getId().equals(itemId)).findAny().isPresent()) {
                coWorkerDetail = coWorkerList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
                coWorkerDetailOld=new V2_WorkOrderCoworker();
                try {
                    BeanUtils.copyProperties(coWorkerDetailOld,coWorkerDetail);
                } catch (Exception e) {
                   logger.error("编辑责任人工时出现异常 e:",e);
                }
            }
        }
    }

    public void addAcessItem() {
        saveItem();
    }
    List<TableDefinition> tDefinitions;
    public List<TableDefinition> getTablesName(V2_PmOrder v2) {
        tDefinitions = pmOrderService.getTablesName(v2);
        return tDefinitions;
    }

    public  List<PmOrderRuleCheckList> getPmRuleCLclosed(V2_PmOrder v2PmOrder,String ruleUid){
        /*   unsDC:bo_uuid(工单)-->unsDC:id====>unsDC:id/unsD:collecitoId-->key
        * key就是检查项uid*/
        List<PmOrderRuleCheckList> pmcl =new ArrayList<>();
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2PmOrder.getId());
        if(unsD==null){
            return pmcl;
        }
        List<UnstructuredData> byCollectionId = unstructuredDataDao.findByCollectionId(unsD.getId());
        List<String> strList=new ArrayList<>();
        byCollectionId.forEach(item->strList.add(item.getKey()));
        if(strList.isEmpty()){
            strList.add("");
        }
        pmcl = pmckLstDao.findPMCL(strList,ruleUid);
        return pmcl;
    }

//    public List<PmOrderRuleCheckList> acPmOrderUleCheckList(V2_PmOrder v2PmOrder) {
//        PmOrderRule rule = Optional.of(v2PmOrder).map(pmOrderService::acqPmOrderRule).orElse(null);
//        if (rule == null) {
//            return Collections.emptyList();
//        }
//        getPmRuleCLclosed(v2PmOrder,rule.getUid());
//        if(v2PmOrder.isFinished() || "Acceptance".equals(v2PmOrder.getCurrentStepId())) {
//         return getPmRuleCLclosed(v2PmOrder,rule.getUid());
//        }
//        else {
//            return pmckLstDao.findAllByRuleUID(rule.getUid());
//        }
//    }

    public List<UnstructuredData> acPmOrderUleCheckList(V2_PmOrder v2PmOrder) {
        PmOrderRule rule = Optional.of(v2PmOrder).map(pmOrderService::acqPmOrderRule).orElse(null);
        if (rule == null) {
            return Collections.emptyList();
        }
        return getOrderCheckList(v2PmOrder,rule.getUid());
    }

    public  List<UnstructuredData> getOrderCheckList(V2_PmOrder v2PmOrder,String ruleUid){
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2PmOrder.getId());
        if(unsD==null){
            return Collections.emptyList();
        }
        return unstructuredDataDao.findByCollectionId(unsD.getId());
    }

    public  List<UnstructuredData> getOrderCheckList(V2_PmOrder v2PmOrder){
        UnstructuredDataCollection unsD = reportDao.findCKByUId(v2PmOrder.getId());
        if(unsD==null){
            return Collections.emptyList();
        }
        return unstructuredDataDao.findByCollectionId(unsD.getId());
    }

    //hmCheck 的key是每一个检查项的uid, value是unstructrued_data的key
    public void loadHmCheck(V2_PmOrder v2PmOrder) {
        hmCheck = new   HashMap<>();
        //pmCheckItemList:检查项
        pmCheckItemList = getOrderCheckList(v2PmOrder);
        unDataCollection = reportDao.findCKByBoUuid(v2PmOrder.getId(), "pm-checklist-report");//为0说明该工单的规则还没有绑定 通过和不通过
        if (ObjectUtil.isNotEmpty(unDataCollection)) {
            for (UnstructuredData data : pmCheckItemList) {
                hmCheck.put(data.getKey(), data);
            }
        }
//        if (pmCheckItemList == null) return ;
        //下面的代码是需要的 ，没有的话 检查项如改动后 在详细信息只会显示通过状态
//        for (PmOrderRuleCheckList pmCheckItem : pmCheckItemList) {
//            if (unDataCollection != null && !unDataCollection.isEmpty()) { //
//                unCollection = unDataCollection.get(0);
//                if (unCollection != null) {
//                    List<UnstructuredData> dataList = unCollection.getData();
//                    for (UnstructuredData unsData : dataList) {
//                        if (unsData.getKey().equalsIgnoreCase(pmCheckItem.getUid())) {
//                            if (v2PmOrder.isFinished() && ObjectUtil.isAllNotEmpty(unsData.getName(), unsData.getRemark())) {
//                                pmCheckItem.setName(unsData.getName());
//                                pmCheckItem.setRemark(unsData.getRemark());
//                            } else {
//                                unsData.setName(pmCheckItem.getName());
//                                unsData.setRemark(pmCheckItem.getRemark());
//                            }
//                            hmCheck.put(pmCheckItem.getUid(), unsData);
//                        }
//                    }
//                }
//            }
//        }
    }

    //流程配置项勾选批量保养完成 已计划显示批量保养完成按钮，同时勾选简化保养 待接单和待抢单才能显示批量保养完成按钮，其他情况均不显示。
    public boolean isAllowBatchComplate() {
        WorkflowConfig wfc = configService.findConfigBySite(loginUser.getSiteUID());
        if(wfc==null || wfc.getPmIsSupportBatchFinish() == null){
            return false;
        }
        if (wfc.getPmIsSupportBatchFinish()) {
            if (queryIndex == 0) {
                return true;
            }
            if(wfc.getPmSimplify() == null){
                return false;
            }
            if (wfc.getPmSimplify() && (queryIndex == 31 || queryIndex == 3)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }



    //map : item.uid(pmorderrulecheck.uid) value: unstrundata
    public List<V2_WorkOrder_Detail> getNeedApprovalDetails() {
        return approvedDetails == null ? null : approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).collect(Collectors.toList());
    }

    private V2_WorkOrderCoworkerRepository coWorkerDao;

    public void addCoWokerItem() {
        if(selectedPmOrder.getCoworkerEntryType() == 1 ||  selectedPmOrder.getCoworkerEntryType() == 3){
            // 重复性校验
            boolean check = false;
            for (V2_WorkOrderCoworker detail : coWorkerList) {
                if("000".equals(detail.getId())){
                    continue;
                }
                if(coWorkerDetail.getCowokerUserId().equals(detail.getCowokerUserId()) &&  !coWorkerDetail.getId().equals(detail.getId()) ){
                    // 开始时间结束时间不能相等
                    if(coWorkerDetail.getStartTime().getTime() == detail.getStartTime().getTime()
                            || coWorkerDetail.getEndTime().getTime() == detail.getEndTime().getTime()){
                        check = true;
                        break;
                    }
                    // 当前开始时间小于结束时间 && 大于开始时间
                    if(coWorkerDetail.getStartTime().getTime() < detail.getEndTime().getTime()
                            && coWorkerDetail.getStartTime().getTime() > detail.getStartTime().getTime()){
                        check = true;
                        break;
                    }
                    // 当前结束时间大于开始时间 && 小于结束时间
                    if(coWorkerDetail.getEndTime().getTime() > detail.getStartTime().getTime()
                            && coWorkerDetail.getStartTime().getTime() < detail.getEndTime().getTime()){
                        check = true;
                        break;
                    }
                }

            }
            if(check){
                WebUtil.addErrorMessage("工时段重复，请重新填写");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }

            String strHours = "0";
            long milliSeconds = coWorkerDetail.getEndTime().getTime() - coWorkerDetail.getStartTime().getTime();
            if (milliSeconds > 0) {
                DecimalFormat df = new DecimalFormat("0.0");
                strHours = df.format((float) milliSeconds / (1000 * 60 * 60));
                BigDecimal b = new BigDecimal(strHours);
                float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                f1 = Float.parseFloat(df.format(f1));
                strHours = df.format(f1);
            } else {
                WebUtil.addErrorMessage("结束时间不能小于开始时间");
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                return;
            }
            coWorkerDetail.setManHours(Double.valueOf(strHours));
        }
        if (!coWorkerList.contains(coWorkerDetail)) {
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
            coWorkerList.add(coWorkerDetail);
        } else {
            UserAccount coWorker = userDao.findById(coWorkerDetail.getCowokerUserId());
            coWorkerDetail.setCowokerUserName(coWorker.getName());
        }
        //   cancelWorkdetail();
    }
    public void updateAccessPrice(){
        pmOrderService.updateAccessPrice(itemDetail);
    }
    public void cancelWorkdetail() {
        if(coWorkerDetailOld!=null){
            if(CollectionUtils.isNotEmpty(coWorkerList)){
                coWorkerList.remove(coWorkerDetail);
                coWorkerList.add(coWorkerDetailOld);
            }
        }
        itemDetail = null;
        coWorkerDetail = null;
        selectedPmOrderManHoursDetail = null;
    }

    public boolean accessoryListEmpty() {
        if (accessoryList == null) {
            return true;
        }
        if (accessoryList.size() > 1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean approveFlag() {
        for (V2_WorkOrder_Detail v2_workOrder_detail :accessoryList){
            if(!"000".equals(v2_workOrder_detail.getId()) && (v2_workOrder_detail.getApproved()==null || !v2_workOrder_detail.getApproved())){
                return false;
            }
        }
        return true;
    }

    public List<String> autoCompleteSupplier(String query) {
        supplierList = aimService.getSuplierListBySiteUid(userContextService.getLoginUser().getTenantUID());
        List<Supplier> suList = autoCompleteService.autoCompleteVendor(query, supplierList);
        List<String> sList = new ArrayList<String>();
        suList.forEach(item -> sList.add(item.getName()));
        return sList;
    }
    public List<V2_WorkOrder_Detail> getAccessoryList() {
        if (accessoryList == null) {
            return null;
        }
        V2_WorkOrder_Detail emptyDetail = new V2_WorkOrder_Detail();
        emptyDetail.setId("000");
        if (!accessoryList.contains(emptyDetail)) {
            accessoryList.add(emptyDetail);
        } else {
            accessoryList.remove(emptyDetail);
            accessoryList.add(emptyDetail);
        }
        return accessoryList;
    }

    public List<V2_WorkOrderCoworker> getCoWorkerList() {
        if (coWorkerList == null) {
            return null;
        }
        V2_WorkOrderCoworker emptyDetail = new V2_WorkOrderCoworker();
        emptyDetail.setId("000");
        if (!coWorkerList.contains(emptyDetail)) {
            coWorkerList.add(emptyDetail);
        } else {
            coWorkerList.remove(emptyDetail);
            coWorkerList.add(emptyDetail);
        }
        return coWorkerList;
    }

    //1 验收通过 和 0验收不通过
    public void invokeAcceptance(boolean pass) {
        if (isChecked() == false) {
            pmOrderService.invokeAcceptance(selectedPmOrder, remarks, pass);
        }else { //批量
            pmOrderService.invokeAcceptanceInBatch(selectedMap, remarks, pass);
        }
    }

    public List<OrgInfo> LoadSiteNameList() {
        if (CollectionUtils.isEmpty(orgList)) {
            List<OrgInfo> orgInfos = uaaService.getUserAccessibleSiteList(loginUser.getUid());
            if (!CollectionUtils.isEmpty(orgInfos)) {
                orgList = orgInfos.stream().filter(item -> 3 == item.getOrgType()).collect(Collectors.toList());
            }
        }
        return orgList;
    }



    public List<PmOrderRule> getPmOrderRulesByAsset(){
        if(assetUid != null) {
            RelRuleAsset relRuleAsset = relRuleAssetRepository.findByAssetUid(assetUid);
            if(relRuleAsset != null && ruleLevel != null) {
                String ruleUID = "";
                switch (ruleLevel) {
                    case 1:
                        ruleUID = relRuleAsset.getRuleUID1();
                        break;
                    case 2:
                        ruleUID = relRuleAsset.getRuleUID2();
                        break;
                    case 3:
                        ruleUID = relRuleAsset.getRuleUID();
                        break;
                    case 4:
                        ruleUID = relRuleAsset.getRuleUID4();
                        break;
                    default:
                        ruleUID = "";
                }
                if (relRuleAsset != null && StringUtils.isNotEmpty(ruleUID)) {
                    String[] ruleUids = StringUtils.split(ruleUID, "|");
                    List<String> ruleUidLists = Arrays.asList(ruleUids);
//                    isCreatable = true;
                    if(ruleUidLists.size() == 1){
                        pmRuleUid = ruleUidLists.get(0);
                    }
                    return pmOrderRuleRepository.getPmOrderRulesByUids(ruleUidLists);
                }else{
//                    isCreatable = false;
//                    WebUtil.addErrorMessage("资产未绑定对应保养规则,无法创建保养工单");
//                    RequestContext requestContext = RequestContext.getCurrentInstance();
//                    requestContext.addCallbackParam("validationFailed", true);
                }
            }else{
//                isCreatable = false;
//                WebUtil.addErrorMessage("资产未绑定任何保养规则,无法创建保养工单");
//                RequestContext requestContext = RequestContext.getCurrentInstance();
//                requestContext.addCallbackParam("validationFailed", true);
            }
        }
        return null;
    }


    public String pmRulesListVisable(){
        if(getPmRuleList()){
            return "visibility:visible";
        }else {
            return "visibility:hidden";
        }
    }


    public List<String> getTypeList(){

        List<String> attachmentTypeList = V2_BlobObject.AttachmentType.getAttachmentTypeList();
        return attachmentTypeList;
    }


    public boolean verifyFileSuffixName(String fileName,String fileSuffixName){

        if(fileName.contains(fileSuffixName)){
            return true;
        }
        return false;
    }

    public V2_WorkOrder_Detail getItemDetail() {
        return itemDetail;
    }

    public void setItemDetail(V2_WorkOrder_Detail itemDetail) {
        this.itemDetail = itemDetail;
    }

    public V2_WorkOrderCoworker getCoWorkerDetail() {
        return coWorkerDetail;
    }

    public void setCoWorkerDetail(V2_WorkOrderCoworker coWorkerDetail) {
        this.coWorkerDetail = coWorkerDetail;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public Date getConfirmedDownTime() {
        return confirmedDownTime;
    }

    public void setConfirmedDownTime(Date confirmedDownTime) {
        this.confirmedDownTime = confirmedDownTime;
    }

    public List<V2_PmOrder> getSelectedPmOrderList() {
        return selectedPmOrderList;
    }

    public void setSelectedPmOrderList(List<V2_PmOrder> selectedPmOrderList) {
        this.selectedPmOrderList = selectedPmOrderList;
    }

    public String getEquipmentTaker() {
        return equipmentTaker;
    }

    public void setEquipmentTaker(String equipmentTaker) {
        this.equipmentTaker = equipmentTaker;
    }

    public Date getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(Date takeTime) {
        this.takeTime = takeTime;
    }

    public String getStepFileter() {
        return stepFileter;
    }

    public void setStepFileter(String stepFileter) {
        this.stepFileter = stepFileter;
    }

    public List<V2_BlobObject> getPicList() {
        return picList;
    }

    public void setPicList(List<V2_BlobObject> picList) {
        this.picList = picList;
    }

    public List<V2_BlobObject> getAudioList() {
        return audioList;
    }

    public void setAudioList(List<V2_BlobObject> audioList) {
        this.audioList = audioList;
    }

    public V2_PmOrder getSelectedPmOrder() {
        return selectedPmOrder;
    }

    public void setSelectedPmOrder(V2_PmOrder selectedPmOrder) {
        this.selectedPmOrder = selectedPmOrder;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setCoWorkerList(List<V2_WorkOrderCoworker> coWorkerList) {
        this.coWorkerList = coWorkerList;
    }

    public V2_WorkOrderCoworkerRepository getCoWorkerDao() {
        return coWorkerDao;
    }

    public void setCoWorkerDao(V2_WorkOrderCoworkerRepository coWorkerDao) {
        this.coWorkerDao = coWorkerDao;
    }

    public String getApprovaler() {
        return approvaler;
    }
    public void setNothing(){}

    public void setApprovaler(String approvaler) {
        this.approvaler = approvaler;
    }

    public Date getPlanTime() {
        return planTime;
    }

    public void setPlanTime(Date planTime) {
        this.planTime = planTime;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getRuleLevel() {
        return ruleLevel;
    }

    public void setRuleLevel(Integer ruleLevel) {
        this.ruleLevel = ruleLevel;
    }

    public String getFilterPlanTime() {
        return filterPlanTime;
    }

    public void setFilterPlanTime(String filterPlanTime) {
        this.filterPlanTime = filterPlanTime;
    }

    public String getPmOrderId() {
        return pmOrderId;
    }

    public void setPmOrderId(String pmOrderId) {
        this.pmOrderId = pmOrderId;
    }

    public Map<String, Boolean> getSelectedMap() {
        return selectedMap;
    }

    public void setSelectedMap(Map<String, Boolean> selectedMap) {
        this.selectedMap = selectedMap;
    }

    public void setAccessoryList(List<V2_WorkOrder_Detail> accessoryList) {
        this.accessoryList = accessoryList;
    }

    public List<V2_WorkOrder_Detail> getApprovedDetails() {
        return approvedDetails;
    }

    public void setApprovedDetails(List<V2_WorkOrder_Detail> approvedDetails) {
        this.approvedDetails = approvedDetails;
    }

    public Boolean getIsSelectedCurrentPage() {
        return isSelectedCurrentPage;
    }

    public void setIsSelectedCurrentPage(Boolean selectedCurrentPage) {
        isSelectedCurrentPage = selectedCurrentPage;
    }

    public Boolean getTerminate() {
        return terminate;
    }

    public void setTerminate(Boolean terminate) {
        this.terminate = terminate;
    }

    public Date getPlanTimeForBatch() {
        return planTimeForBatch;
    }

    public void setPlanTimeForBatch(Date planTimeForBatch) {
        this.planTimeForBatch = planTimeForBatch;
    }

    public String getCurrentPersonIdBatch() {
        return currentPersonIdBatch;
    }

    public void setCurrentPersonIdBatch(String currentPersonIdBatch) {
        this.currentPersonIdBatch = currentPersonIdBatch;
    }

    public Integer getPmType() {
        return pmType;
    }

    public void setPmType(Integer pmType) {
        this.pmType = pmType;
    }

    public String getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(String qaStatus) {
        this.qaStatus = qaStatus;
    }

    //getter and setter
    public Integer getQueryIndex() {
        return queryIndex;
    }

    public void setQueryIndex(Integer queryIndex) {
        this.queryIndex = queryIndex;
    }

    public AssetInfo getSelectedAsset() {
        return selectedAsset;
    }

    public void setSelectedAsset(AssetInfo selectedAsset) {
        this.selectedAsset = selectedAsset;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getNeedConfirmTime() {
        return needConfirmTime;
    }

    public void setNeedConfirmTime(Boolean needConfirmTime) {
        this.needConfirmTime = needConfirmTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public V2_ServiceRequest getNewServiceRequest() {
        return newServiceRequest;
    }

    public void setNewServiceRequest(V2_ServiceRequest newServiceRequest) {
        this.newServiceRequest = newServiceRequest;
    }

    public Date getEsTime() {
        return esTime;
    }

    public void setEsTime(Date esTime) {
        this.esTime = esTime;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getConfirmedUpDate() {
        return confirmedUpDate;
    }

    public void setConfirmedUpDate(Date confirmedUpDate) {
        this.confirmedUpDate = confirmedUpDate;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setPmCheckItemList(List<UnstructuredData> pmCheckItemList) {
        this.pmCheckItemList = pmCheckItemList;
    }

    public List<UnstructuredData> getPmCheckItemList() {
        return pmCheckItemList;
    }

    public List<V2_BlobObject> getPicListForRP() {
        return picListForRP;
    }

    public void setPicListForRP(List<V2_BlobObject> picListForRP) {
        this.picListForRP = picListForRP;
    }

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType;
    }

    public HashMap<String, UnstructuredData> getHmCheck() {
        return hmCheck;
    }

    public void setHmCheck(HashMap<String, UnstructuredData> hmCheck) {
        this.hmCheck = hmCheck;
    }

    public Integer getRepairTypeInt() {
        return repairTypeInt;
    }

    public void setRepairTypeInt(Integer repairTypeInt) {
        this.repairTypeInt = repairTypeInt;
    }

    public String getReportUid() {
        return reportUid;
    }

    public void setReportUid(String reportUid) {
        this.reportUid = reportUid;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public List<V2_PmOrder> getCurrentPagePmOrders() {
        return currentPagePmOrders;
    }

    public void setCurrentPagePmOrders(List<V2_PmOrder> currentPagePmOrders) {
        this.currentPagePmOrders = currentPagePmOrders;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Map<String, Object> getSelectedObjectMap() {
        return selectedObjectMap;
    }

    public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
        this.selectedObjectMap = selectedObjectMap;
    }

    public AutoCompleteService getAutoCompleteService() {
        return autoCompleteService;
    }

    public void setAutoCompleteService(AutoCompleteService autoCompleteService) {
        this.autoCompleteService = autoCompleteService;
    }

    public AssetInfoMgrService getAimService() {
        return aimService;
    }

    public void setAimService(AssetInfoMgrService aimService) {
        this.aimService = aimService;
    }

    public String getOneSearchStr() {
        return oneSearchStr;
    }

    public void setOneSearchStr(String oneSearchStr) {
        this.oneSearchStr = oneSearchStr;
    }

    public OrgInfo getSelectSite() {
        return selectSite;
    }

    public void setSelectSite(OrgInfo selectSite) {
        this.selectSite = selectSite;
    }

    public List<OrgInfo> getClinicalDeptList() {
        return clinicalDeptList;
    }

    public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
        this.clinicalDeptList = clinicalDeptList;
    }

    public OrgInfoTreeService getOrgInfoTreeService() {
        return orgInfoTreeService;
    }

    public void setOrgInfoTreeService(OrgInfoTreeService orgInfoTreeService) {
        this.orgInfoTreeService = orgInfoTreeService;
    }

    public String getCancelInt() {
        return cancelInt;
    }

    public void setCancelInt(String cancelInt) {
        this.cancelInt = cancelInt;
    }

    public List<V2_BlobObject> getPartPictures(String id) {
        if(StringUtils.isNotEmpty(id)) {
            accessoryList = getWorkOrderDetail(id);
            Set<String> ids = accessoryList.stream().map(p -> p.getId()).collect(Collectors.toSet());
            if(!ids.isEmpty()) {
                return blobObjectRepository.getPartPictures(ids);
            }
            return null;
        } else {
            logger.warn("V2PmOrderController selected is null");
            return null;
        }
    }

    public String getPartsItemCode(V2_WorkOrder_Detail v2WorkOrderDetail) {
        if(v2WorkOrderDetail == null){
            if(partsItem != null){
                return partsItem.getItemCode();
            }
        }else{
            SimplePartsItem simplePartsItem = partsItemService.getPartsItem(v2WorkOrderDetail.getPartsItemId(),v2WorkOrderDetail.getParts());
            if(simplePartsItem != null){
                return simplePartsItem.getItemCode();
            }
        }
        return null;
    }

    public String getPeriodUnit(String periodUnit){
        if (StringUtils.isNotEmpty(periodUnit)) {
            switch (periodUnit) {
                case "y":
                    return "年";
                case "m":
                    return "月";
                case "w":
                    return "周";
                case "d":
                    return "日";
                default:
                    break;
            }
        }
        return null;
    }

    public String getSignatureContent() {
        return signatureContent;
    }

    public void setSignatureContent(String signatureContent) {
        this.signatureContent = signatureContent;
    }

    public StreamedContent getSignatureContentStream() {
        return signatureContentStream;
    }

    public void setSignatureContentStream(StreamedContent signatureContentStream) {
        this.signatureContentStream = signatureContentStream;
    }

    public String getSignatureId() {
        return signatureId;
    }

    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }

    public String getAssetUid() {
        return assetUid;
    }

    public void setAssetUid(String assetUid) {
        this.assetUid = assetUid;
    }

    public String getPmRuleUid() {
        return pmRuleUid;
    }

    public void setPmRuleUid(String pmRuleUid) {
        this.pmRuleUid = pmRuleUid;
    }


    public String getFileTypeName() {
        return fileTypeName;
    }

    public void setFileTypeName(String fileTypeName) {
        this.fileTypeName = fileTypeName;
    }

    private void initExternalTempUser(){
        externalTempUser = externalTempUserDao.findByWoId(selected.getId());
        if(ObjectUtil.isNotEmpty(externalTempUser)){
            maintenancePersonInformation = new ArrayList<>();
            maintenancePersonInformation.add(initMap("userName","姓名",externalTempUser.getUserName()));
            maintenancePersonInformation.add(initMap("tel","联系人电话",externalTempUser.getTel()));
            maintenancePersonInformation.add(initMap("comName","公司名称",externalTempUser.getComName()));
        }

    }
    private Map<String,String> initMap(String fieldName,String key,String label){
        Map<String,String> row=new HashMap<>();
        row.put("fieldName",fieldName);
        row.put("key",key);
        row.put("label",label);
        return row;
    }

    public void onRowEditExternalTempUser(RowEditEvent event) {
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
        Map<String,String> row =(Map) event.getObject();
        String label = row.get("label");
        if(org.apache.commons.lang.StringUtils.isBlank(label)){
            WebUtil.addErrorMessage(row.get("key")+"不能为空");
            return;
        }
        try {
            Class<ExternalTempUser> externalTempUserClass = ExternalTempUser.class;
            Field fieldName = externalTempUserClass.getDeclaredField(row.get("fieldName"));
            fieldName.setAccessible(true);
            fieldName.set(externalTempUser,label);
            externalTempUserDao.save(externalTempUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestContext.getCurrentInstance().addCallbackParam("validationFailed", false);
    }

    public void onRowCancelExternalTempUser(RowEditEvent event) {
        try {
            Map<String,String> row =(Map) event.getObject();
            Class<ExternalTempUser> externalTempUserClass = ExternalTempUser.class;
            Field fieldName = externalTempUserClass.getDeclaredField(row.get("fieldName"));
            fieldName.setAccessible(true);
            Object o = fieldName.get(externalTempUser);
            if(!ObjectUtils.isEmpty(o)){
                row.put("label",o.toString());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private List<V2_PmOrderManHoursDetail> pmHoursList;
    public List<V2_PmOrderManHoursDetail> getPmHoursList() {
        if (pmHoursList == null) {
            pmHoursList = new ArrayList<>();
        }
        V2_PmOrderManHoursDetail newDetail = new V2_PmOrderManHoursDetail();
        newDetail.setId("000");
        boolean flag = false;
        for (V2_PmOrderManHoursDetail info : pmHoursList) {
            String id = info.getId();
            if ("000".equals(id)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            pmHoursList = pmHoursList.stream()
                    .filter(item -> !"000".equals(item.getId()))
                    .collect(Collectors.toList());
            pmHoursList.add(newDetail);
        } else {
            pmHoursList.add(newDetail);
        }
        return pmHoursList;
    }

    public void updateEntryType(Integer entryType) {
        if (selectedPmOrder == null) {
            selectedPmOrder = new V2_PmOrder();
        }
        selectedPmOrder.setEntryType(entryType);
        if (entryType == 1) {
            pmHoursList = null;
            selectedPmOrder.setManHours(null);
        }else if(entryType == 0){
            selectedPmOrder.setManHours(null);
            String value = configItemValueRepository.findByKeyAndSiteForUseParent(selectedPmOrder.getSiteUID(), selectedPmOrder.getHospitalUID(), selectedPmOrder.getInstitutionUID(), selectedPmOrder.getTenantUID(), "pmEntryTypeSet");
            if(StrUtil.isNotEmpty(value)){
                FieldColumnDto fieldColumnDto = JSON.parseObject(value, FieldColumnDto.class);
                if(fieldColumnDto != null){
                    selectedPmOrder.setManHours(fieldColumnDto.getHours());
                }
            }
//            if(selectedPmOrder.getManHours() == null){
//                selectedPmOrder.setManHours(0.1);
//            }
        }
    }

    public void updateCoworkerEntryType(Integer entryType) {
        if (selectedPmOrder == null) {
            selectedPmOrder = new V2_PmOrder();
        }
        selectedPmOrder.setCoworkerEntryType(entryType);
        coWorkerList = new ArrayList<>();
        V2_WorkOrderCoworker emptyDetail = new V2_WorkOrderCoworker();
        emptyDetail.setId("000");
        coWorkerList.add(emptyDetail);
    }

    public void prepareEditPmOrderManHoursDetail(String itemId) {
        if ("new".equals(itemId)) {
            selectedPmOrderManHoursDetail = new V2_PmOrderManHoursDetail();
            selectedPmOrderManHoursDetail.setId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            selectedPmOrderManHoursDetail = pmHoursList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void deletePmOrderManHoursDetail(String itemId) {
        pmHoursList.removeAll(pmHoursList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        Double hours = 0d;
        for (V2_PmOrderManHoursDetail info : pmHoursList) {
            if (info != null && info.getHours() != null) {
                hours = hours + info.getHours();
            }
        }
        selectedPmOrder.setManHours(hours);
    }

    public void addManHoursDetail(Date firstDate, Date secondDate) {
        String strHours = "0";
        long milliSeconds = secondDate.getTime() - firstDate.getTime();
        if (milliSeconds > 0) {
            DecimalFormat df = new DecimalFormat("0.0");
            strHours = df.format((float) milliSeconds / (1000 * 60 * 60));
            BigDecimal b = new BigDecimal(strHours);
            float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            f1 = Float.parseFloat(df.format(f1));
            strHours = df.format(f1);
        } else {
            WebUtil.addErrorMessage("结束时间不能小于开始时间");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        selectedPmOrderManHoursDetail.setHours(Double.valueOf(strHours));
        selectedPmOrderManHoursDetail.setStartTime(firstDate);
        selectedPmOrderManHoursDetail.setEndTime(secondDate);
        if (pmHoursList.contains(selectedPmOrderManHoursDetail)) {
            pmHoursList.remove(selectedPmOrderManHoursDetail);
        }

        // 重复性校验
        boolean check = false;
        for (V2_PmOrderManHoursDetail detail : pmHoursList) {
            if("000".equals(detail.getId())){
                continue;
            }
            // 开始时间结束时间不能相等
            if(firstDate.getTime() == detail.getStartTime().getTime()
                    || secondDate.getTime() == detail.getEndTime().getTime()){
                check = true;
                break;
            }
            // 当前开始时间小于结束时间 && 大于开始时间
            if(firstDate.getTime() < detail.getEndTime().getTime()
                    && firstDate.getTime() > detail.getStartTime().getTime()){
                check = true;
                break;
            }
            // 当前结束时间大于开始时间 && 小于结束时间
            if(secondDate.getTime() > detail.getStartTime().getTime()
                    && firstDate.getTime() < detail.getEndTime().getTime()){
                check = true;
                break;
            }
        }
        if(check){
            WebUtil.addErrorMessage("工时段重复，请重新填写");
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }
        pmHoursList.add(selectedPmOrderManHoursDetail);
        Double hours = 0d;
        for (V2_PmOrderManHoursDetail info : pmHoursList) {
            if (info != null && info.getHours() != null) {
                hours = hours + info.getHours();
            }
        }
        selectedPmOrder.setManHours(hours);
        cancelWorkdetail();
    }

    public Date getMaxDate(){
        if(selectedPmOrderManHoursDetail.getStartTime() != null){
            Date date = DateUtil.offset(selectedPmOrderManHoursDetail.getStartTime(), DateField.HOUR, 24);
            return date;
        }
        return TimeUtil.now();
    }

    public Date getCoWorkMaxDate(){
        if(coWorkerDetail.getStartTime() != null){
            Date date = DateUtil.offset(coWorkerDetail.getStartTime(), DateField.HOUR, 24);
            return date;
        }
        return TimeUtil.now();
    }

}

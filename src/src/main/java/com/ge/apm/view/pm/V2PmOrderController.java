/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.bo.MaintenanceTemplate;
import com.ge.apm.domain.bo.parser.SchemaParser;
import com.ge.apm.domain.bo.render.AccessoryTableRenderPolicy;
import com.ge.apm.domain.bo.render.AuditMsgTableRenderPolicy;
import com.ge.apm.domain.bo.render.CheckListTableRenderPolicy;
import com.ge.apm.domain.bo.render.MaintenanceTableRenderPolicy;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.service.wo.V2_WorkOrderService;
import com.ge.apm.view.sysutil.CheckItem;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.apm.web.PmOrderActionForm;
import com.google.common.base.Strings;
import org.apache.commons.collections4.map.HashedMap;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
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
public class V2PmOrderController extends GenericCRUDUUIDController<V2_PmOrder> implements Serializable, ServerEventInterface {

    private static final DateTimeFormatter DEFAULT_DAY = DateTimeFormat.forPattern("yyyy-MM-dd");

    V2_PmOrderRepository dao;
    private Double totalManHours;
    private String dptName;
    private OrgInfoRepository orgDao;
    private UserAccountRepository userDao;
    private V2_PmOrderService pmOrderService;
    private V2_PmOrder selectedPmOrder;
    private String assetName;
    private Integer ruleLevel;
    private Date startTime;
    private Date endTime;
    private Date planTime;
    private Date applyTime;
    private Date planTimeForBatch;
    private String currentPersonIdBatch;
    private String qualityCheckingBatch;
    Boolean terminate;
    private UaaService uaaService;
    private UserContextService userContextService;
    private PmOrderReportTemplateService pmOrderReportTemplateService;
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
    private I18nMessageRepository i18nMessageRepository;
    private String head;
    private TableDefinitionService tableService;

    // cache for maintenance report
    private StreamedContent maintenanceReport;

    private String reportUid; //selected table base uid

    private UserAccount user;
    private TenantInfoRepository tenantInfoRepository;
    Boolean isInsulate;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
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

    public void addReporter(String boUid) {
        UnstructuredDataCollection newReport = new UnstructuredDataCollection();
        newReport.setBoUuid(boUid);
        TableDefinition tbd = tableDao.findByBaseId(reportUid);
        newReport.setSchemaId(tbd.getId());
        PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(selectedPmOrder);
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
        OrgInfo site = orgDao.getByUid(siteUID);
        List<String> orgUids = Arrays.asList(site.getSiteUID(), site.getHospitalUID(), site.getInstitutionUID(), site.getTenantUID());
        return tableDao.getBySiteUIDIn(orgUids);
    }

    List<UnstructuredDataCollection> reportList;

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
        return this.selected != null;
    }

    /**
     * Generate maintenance report.
     */
    public void doExportMaintenanceReport() {
        if (this.selected != null) {
            maintenanceReport = null;
            try {
                PmOrderRule rule = pmOrderService.acqPmOrderRule(selected);
                if (rule == null) throw new BusinessException("系统未找到绑定的规则!");
                InputStream rawTemplate = null;
                if (!Strings.isNullOrEmpty(rule.getReportStorageId())) {
                    // get template if the report storage is set.
                    StreamedContent template = blobService.getBlobObjectByStorageId(rule.getReportStorageId());
                    rawTemplate = template.getStream();
                } else {
                    // generate template otherwise.
                    try {
                        byte[] activeTemplateBytes = pmOrderReportTemplateService.getActiveTemplateBytes(isInsulate, user);
                        InputStream activeTemplateStream = new ByteArrayInputStream(activeTemplateBytes);
                        rawTemplate = pmOrderReportTemplateService.generateReportTemplateByRule(activeTemplateStream, rule);
                    } catch (IOException e) {
                        throw new BusinessException("自动模版生成失败");
                    }
                }

                if (rawTemplate == null) throw new BusinessException("请先上传保养报告模版!");

                Map<String, Object> datas = populateReportContent(selected);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                XWPFTemplate template = XWPFTemplate.compile(rawTemplate);
                // render policy for accessory information.
                template.registerPolicy(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(), new AccessoryTableRenderPolicy());
                // render policy for maintenance workflow.
                template.registerPolicy(MaintenanceTemplate.Component.MAINTENANCE_WORKFLOW.toString(), new MaintenanceTableRenderPolicy());
                // render policy for check list
                template.registerPolicy(MaintenanceTemplate.Component.CHECK_LIST.toString(), new CheckListTableRenderPolicy());
                // render policy for audit record
                template.registerPolicy(MaintenanceTemplate.Component.AUDIT_RECORDS.toString(), new AuditMsgTableRenderPolicy());
                template.render(datas);
                template.write(out);

                InputStream doc = new ByteArrayInputStream(out.toByteArray());
                maintenanceReport = new DefaultStreamedContent(doc, "application/msword", "maintenance-report.docx");
            } catch (BusinessException e) {
                WebUtil.addErrorMessage(e.getMessage());
            } catch (Exception e) {
                Logger.getLogger(V2PmOrderController.class.getName()).log(Level.WARNING, "Error happens while generate maintenance report", e);
                WebUtil.addErrorMessage("保养报告生成失败");
            }
        }
    }

    private Map<String, Object> populateReportContent(V2_PmOrder pmOrder) {
        Map<String, Object> content = new HashMap<>();

        // Device information
        content.put(MaintenanceTemplate.Device.NAME.toString(), pmOrder.getAssetInfo().getName());
        content.put(MaintenanceTemplate.Device.DEPARTMENT.toString(), pmOrder.getAssetInfo().getClinicalDeptName());
        content.put(MaintenanceTemplate.Device.TYPE.toString(), pmOrder.getAssetInfo().getFunctionType());
        content.put(MaintenanceTemplate.Device.OEM.toString(), pmOrder.getAssetInfo().getManufacture());
        content.put(MaintenanceTemplate.Device.SERIAL.toString(), pmOrder.getAssetInfo().getSerialNum());
        content.put(MaintenanceTemplate.Device.NUMBER.toString(), pmOrder.getAssetInfo().getDepartNum());
        content.put(MaintenanceTemplate.Device.QR.toString(), pmOrder.getAssetInfo().getQrCode());

        // Maintenance information
        content.put(MaintenanceTemplate.Maintenance.NUMBER.toString(), pmOrder.getPmNum());
        content.put(MaintenanceTemplate.Maintenance.CREATOR.toString(), pmOrder.getCreatorName());
        content.put(MaintenanceTemplate.Maintenance.LEVEL.toString(), pmOrder.getRuleLevel());
        content.put(MaintenanceTemplate.Maintenance.PLAN_TIME.toString(), pmOrder.getPlanTime() != null ? DEFAULT_DAY.print(pmOrder.getPlanTime().getTime()) : "");
        content.put(MaintenanceTemplate.Maintenance.COMPLETE_TIME.toString(), pmOrder.getEndTime() != null ? DEFAULT_DAY.print(pmOrder.getEndTime().getTime()) : "");
        content.put(MaintenanceTemplate.Maintenance.TYPE.toString(), FieldValueMessageController.fieldValue("pmType", String.valueOf(pmOrder.getPmType())));
        content.put(MaintenanceTemplate.Maintenance.OPERATOR.toString(), pmOrder.getOwnerName());
        content.put(MaintenanceTemplate.Maintenance.OP_TIME.toString(), pmOrder.getManHours());
        content.put(MaintenanceTemplate.Maintenance.RESULT.toString(), FieldValueMessageController.fieldValue("qualityChecking", pmOrder.getQualityChecking()));
        // Coworker List
        List<V2_WorkOrderCoworker> coworkers = this.getCoWorkerList();
        if (coworkers != null) {
            int count = 0;
            for (V2_WorkOrderCoworker coworker : coworkers) {
                // ignore fake empty coworker
                if ("000".equalsIgnoreCase(coworker.getId())) continue;
                if (count == 0) {
                    content.put(MaintenanceTemplate.Maintenance.COWORKER1.toString(), coworker.getCowokerUserName());
                    content.put(MaintenanceTemplate.Maintenance.COWORKER1_TIME.toString(), coworker.getManHours().toString());
                } else if (count == 1) {
                    content.put(MaintenanceTemplate.Maintenance.COWORKER2.toString(), coworker.getCowokerUserName());
                    content.put(MaintenanceTemplate.Maintenance.COWORKER2_TIME.toString(), coworker.getManHours().toString());
                }
                count++;
            }
        }

        // Accessory Information
        List<Map<String, Object>> accessoryInfoTable = new ArrayList<>();
        if (this.getAccessoryList().size() > 0) {
            for (V2_WorkOrder_Detail item : this.getAccessoryList()) {
                if ("000".equalsIgnoreCase(item.getId())) continue;

                Map<String, Object> accessoryMap = new HashMap<>();

                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.NAME.toString(), item.getParts());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.QUANTITY.toString(), item.getApartsQuantity());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.PRICE.toString(), item.getPartsPrice());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.TYPE.toString(), FieldValueMessageController.fieldValue("partsType", item.getPartsType()));
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.SERIAL_NUM.toString(), item.getSerialNum());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.WARRANTY_DATE.toString(), item.getWarrantyDate());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.ARRIVED.toString(), item.getArrived());

                accessoryInfoTable.add(accessoryMap);
            }
        }
        content.put(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(), accessoryInfoTable);

        // Maintenance Workflow Information
        List<Map<String, Object>> maintenanceInfoTable = new ArrayList<>();
        for (PmOrderSteps item : this.aquirePmOrderStep()) {
            Map<String, Object> maintenanceInfoMap = new HashMap<>();
            maintenanceInfoMap.put(MaintenanceTemplate.MaintenanceInfo.STEP.toString(), item.getStepName());
            maintenanceInfoMap.put(MaintenanceTemplate.MaintenanceInfo.OWNER.toString(), item.getOwnerName());
            maintenanceInfoMap.put(MaintenanceTemplate.MaintenanceInfo.START_TIME.toString(), item.getStartTime());
            maintenanceInfoMap.put(MaintenanceTemplate.MaintenanceInfo.END_TIME.toString(), item.getEndTime());

            maintenanceInfoTable.add(maintenanceInfoMap);
        }
        content.put(MaintenanceTemplate.Component.MAINTENANCE_WORKFLOW.toString(), maintenanceInfoTable);

        // Check List
        List<Map<String, Object>> checkListTable = new ArrayList<>();
        if (this.getPmCheckItemList() != null) {
            for (PmOrderRuleCheckList item : this.getPmCheckItemList()) {
                Map<String, Object> checkListMap = new HashMap<>();
                checkListMap.put(MaintenanceTemplate.CheckListInfo.NAME.toString(), item.getName());
                checkListMap.put(MaintenanceTemplate.CheckListInfo.PERIOD.toString(), item.getPeriod());
                checkListMap.put(MaintenanceTemplate.CheckListInfo.CONTENT.toString(), item.getRemark());
                checkListMap.put(MaintenanceTemplate.CheckListInfo.RESULT.toString(), FieldValueMessageController.fieldValue("checkItem", getCheckListResult(item.getUid())));

                checkListTable.add(checkListMap);
            }
        }
        content.put(MaintenanceTemplate.Component.CHECK_LIST.toString(), checkListTable);


        // Audit Message List
        List<Map<String, Object>> auditMsgTable = new ArrayList<>();
        if (this.getServiceRequestMessageList(selected.getId()) != null) {
            for (ServiceRequestMsg item : this.getServiceRequestMessageList(selected.getId())) {
                Map<String, Object> auditMsgMap = new HashMap<>();
                auditMsgMap.put(MaintenanceTemplate.AuditMsgInfo.TIME.toString(), item.getSendTime());
                auditMsgMap.put(MaintenanceTemplate.AuditMsgInfo.OWNER.toString(), item.getSenderUserName());
                auditMsgMap.put(MaintenanceTemplate.AuditMsgInfo.INFO.toString(), item.getMessage());

                auditMsgTable.add(auditMsgMap);
            }
        }
        content.put(MaintenanceTemplate.Component.AUDIT_RECORDS.toString(), auditMsgTable);

        if (reportList != null) {
            for (UnstructuredDataCollection item : reportList) {
                // ignore invalid data
                if(item == null || Strings.isNullOrEmpty(item.getBoType())) {
                    continue;
                }
                MaintenanceTemplate.Component tableType;
                switch (item.getBoType()) {
                    case "pm-safety-report":
                        tableType = MaintenanceTemplate.Component.SAFETY_REPORT;
                        break;
                    case "pm-performance-report":
                        tableType = MaintenanceTemplate.Component.PERFORMANCE_REPORT;
                        break;
                    default:
                        continue;
                }
                renderDynamicTable(item, content, tableType);
            }
        }
        return content;
    }

    /**
     * Render Dynamic Table content.
     *
     * @param collection data collection
     * @param content content map
     */
    private void renderDynamicTable(UnstructuredDataCollection collection, Map<String, Object> content, MaintenanceTemplate.Component component) {
        TableDefinition tableDefinition = tableDao.findById(collection.getSchemaId());
        TableSchema table = JSONObject.parseObject(tableDefinition.getSchema(), TableSchema.class);
        List<UnstructuredData> dataList = collection.getData();

        // Get raw data
        Map<String, Object> rawTableValue = new HashMap<>();
        dataList.forEach(item -> {
            try {
                rawTableValue.put(item.getKey(), item.getValue());
            } catch(ClassNotFoundException e) {
                rawTableValue.put(item.getKey(), item.getValueSerialized());
            }
        });

        // update select box option values
        Map<String, Object> tableValue = tableService.changSelectFommDataByJSON(tableDefinition, rawTableValue);

        final String emptyStr = "";
        final String prefix = component.toString();
        for(TableGroup group : table.getGroups()) {
            for(TableItem item : group.getItems()) {
                if (item == null || item.getComponent() == null) {
                    continue;
                }
                switch (item.getComponent()) {
                    case Number:
                        if (item.getResKey() != null) {
                            try {
                                content.put(wrap(prefix, item.getResKey()), FieldValueMessageController.fieldValue("TableReportResult", tableValue.getOrDefault(item.getResKey(), emptyStr).toString()));
                            } catch (RuntimeException e) {
                                content.put(wrap(prefix, item.getResKey()), tableValue.getOrDefault(item.getResKey(), emptyStr));
                            }
                        }
                        if (item.getKey() != null) {
                            content.put(wrap(prefix, item.getKey()), tableValue.getOrDefault(item.getKey(), emptyStr));
                        }
                        break;
                    case Label:
                        if (item.getResKey() != null) {
                            try {
                                content.put(wrap(prefix, item.getResKey()), FieldValueMessageController.fieldValue("TableReportResult", tableValue.getOrDefault(item.getResKey(), emptyStr).toString()));
                            } catch (RuntimeException e) {
                                content.put(wrap(prefix, item.getResKey()), tableValue.getOrDefault(item.getResKey(), emptyStr));
                            }
                        }
                        break;
                    case Switch:
                        if (item.getKey() != null) {
                            try {
                                boolean res = (Boolean) tableValue.getOrDefault(item.getKey(), false);
                                content.put(wrap(prefix, item.getKey()), res ? "是" : "否");
                            } catch (RuntimeException e) {
                                content.put(wrap(prefix, item.getKey()), tableValue.getOrDefault(item.getKey(), emptyStr));
                            }
                        }
                        break;
                    case Date:
                        if (item.getKey() != null) {
                            try {
                                Date dt = (Date) tableValue.getOrDefault(item.getKey(), null);
                                content.put(wrap(prefix, item.getKey()), dt == null ? "" : DEFAULT_DAY.print(dt.getTime()));
                            } catch (RuntimeException e) {
                                content.put(wrap(prefix, item.getKey()), tableValue.getOrDefault(item.getKey(), emptyStr));
                            }
                        }
                        break;
                    case Select:
                    case Text:
                    case TextArea:
                    default:
                        if (item.getKey() != null) {
                            content.put(wrap(prefix, item.getKey()), tableValue.getOrDefault(item.getKey(), emptyStr));
                        }
                        break;
                }
            }
        }
    }

    private String wrap(String prefix, String key) {
        return SchemaParser.UNDERSCORE_JOINER.join(prefix, key);
    }

    /**
     * Translate check item description.
     *
     * @param uid check item uid
     * @return description, or 'n/a'
     */
    private String getCheckListResult(String uid) {
        UnstructuredData data = this.hmCheck.get(uid);
        if (data != null) {
            try {
                return (String) data.getValue();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "n/a";
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
        //System.out.println(result);
        boolean mark = false;
        List<V2_PmOrder> pmList = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId() == k && v == true)
                    pmList.add(item);
            });
        });
        //x3存放的是所选中pm工单
        if (pmList.size() > 0)
            mark = pmList.stream().allMatch(item -> item.getSimplify().equals(true));
        return mark;
    }

    public boolean isBatchTakePm() {
        // System.out.println(result);
        boolean mark = true;
        List<V2_PmOrder> x3 = new ArrayList<V2_PmOrder>();
        //选中的保养工单中是否都是批量接单货或批量完成
        selectedMap.forEach((k, v) -> {
            result.getContent().stream().forEach(item -> {
                if (item.getId() == k && v == true){
                    x3.add(item);
                }
            });
        });
        //x3存放的是所选中pm工单
        if (x3.size() > 0)
            mark = x3.stream().anyMatch(item -> item.getSimplify().equals(true));
        return mark;//==false?true:false;
    }

    public boolean isChecked() {
        Map<String, Boolean> map = this.selectedMap;
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
            return "未知报告: unstrurct data collection的 记录并未能在table defintion中找到 schema_Id is " + templateId;
        }
    }

    UnstructuredData untest;

    public void hmCheckSet(String key) throws Exception {
        //临时用hmCheck1  因为hmCheck的value 是String ?
        untest = (UnstructuredData) hmCheck.get(key);
    }

    private String qaStatus = "0";

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


    @Transactional
    public void saveCheckItemData() {
        UnstructuredDataCollection unColl = new UnstructuredDataCollection();
        ArrayList<UnstructuredData> unD = new ArrayList<UnstructuredData>();
        Iterator<Map.Entry<String, UnstructuredData>> it = hmCheck.entrySet().iterator();
        unDataCollection = reportDao.findCKByBoUuid(selectedPmOrder.getId(), "pm-checklist-report");
        try {
            if (unDataCollection.isEmpty()) {//检查项没有对应的unData即通过和不通过设置，保存新设置
                unColl.setBoUuid(selectedPmOrder.getId());
                unColl.setBoType("pm-checklist-report");
                PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(selectedPmOrder);
                if (pmOrderRule != null) {
                    unColl.setSchemaId(pmOrderRule.getPerformanceReport());
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
                pmOrderService.saveReport(unColl);
            } else { //更新已经有的检查项
                //清空原来的unstructuredDataList
                unDataCollection.get(0).setData(new ArrayList<UnstructuredData>());
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
        approvedDetails = getWorkOrderDetail(selectedPmOrder.getId());
        approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).forEach(item -> selectedMap.put((item.getId()), false));
    }

    public void onApprovedDetailsSelected(String id) {
        if ("All".equals(id)) {
            approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).forEach(item -> selectedMap.put(item.getId(), selectedMap.get("All")));
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
            blobObject.setBoType(V2_BlobObject.BO_TYPE_PmRequest);
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
        return orgDao.getByUid(orgUid);
    }

    public void savePM() {
        PmOrderActionForm formData = new PmOrderActionForm();
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能保养");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        }
        if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("没有使用科室的资产不能创建保养工单");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            if (null != planTime) {
                formData.setPlanTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(planTime));
            }
            formData.setRuleLevel(ruleLevel);
            formData.setAttachments(picList);
            formData.setPmType(pmType);
            Map<String, Object> res = pmOrderService.createPMrequestApi(selectedAsset.getUid(), formData);
            if (res != null && res.get("bizStatusCode") != null) {
                if (res.get("bizStatusCode").equals("INVALID_PARAM")) {
                    WebUtil.addErrorMessage(res.get("message").toString());
                    RequestContext requestContext = RequestContext.getCurrentInstance();
                    requestContext.addCallbackParam("invalidParam", true);
                }
            }
           // cancel();
        }
    }

    public List<PmOrderSteps> aquirePmOrderStep() {
        if (selected != null)
            return pmOrderService.getPmOrderStep(selected.getId());
        else
            return new ArrayList<PmOrderSteps>();
    }

    public void createPMRequest() {
        cancel();
    }

    public void onStatusChange() {
        if (null != selectedAsset) {//new sr
            needConfirmTime = currentStatus == 2 && selectedAsset.getStatus() != 2;
        } else {//update sr
            needConfirmTime = currentStatus == 2 && selected.getAssetInfo().getStatus() != 2;
        }
    }

    public boolean requiredCheck() {
        if (selectedPmOrder.getManHours() == null && endTime == null) {
            WebUtil.addErrorMessage("工时必填写");
            RequestContext.getCurrentInstance().addCallbackParam("needManhours", true);
            WebUtil.addErrorMessage("结束时间必须填写");
            RequestContext.getCurrentInstance().addCallbackParam("needendtime", true);
            return false;
        } else if (endTime == null) {
            WebUtil.addErrorMessage("结束时间必须填写");
            RequestContext.getCurrentInstance().addCallbackParam("needendtime", true);
            return false;
        } else if (selectedPmOrder.getManHours() == null) {
            WebUtil.addErrorMessage("工时必填写");
            RequestContext.getCurrentInstance().addCallbackParam("needManhours", true);
            return false;
        }
        if (selectedPmOrder.getPlanTime() == null) {
            WebUtil.addErrorMessage("计划完成时间必须填写");
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
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList);
        //保存保养清单中的数据
        saveCheckItemData();

        selected = null;
        newServiceRequest = new V2_ServiceRequest();
        newServiceRequest.setRequestorId(UserContextService.getCurrentUserAccount().getId());
        newServiceRequest.setFromDeptId(UserContextService.getCurrentUserAccount().getOrgInfoId());
        newServiceRequest.setRequestorName(UserContextService.getCurrentUserAccount().getName());
        newServiceRequest.setRequestTime(new Date());
        if (selectedPmOrder != null) {
            selectedAsset = selectedPmOrder.getAssetInfo();
            if (Strings.isNullOrEmpty(selectedAsset.getLocationName())) {
                final String orgInfoUID = userContextService.getLoginUser().getOrgInfoUID();
                dptName = orgDao.getByUid(orgInfoUID).getName();
            } else {
                dptName = selectedAsset.getLocationName();
            }
        }
        //  pmOrderService.closePM(selectedPmOrder,picList);
    }

    public void saveServiceRequest() {
        if (selectedAsset.getStatus() == 4) {
            WebUtil.addErrorMessage("所选设备待报废,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else if (Strings.isNullOrEmpty(selectedAsset.getClinicalDeptUID())) {
            WebUtil.addErrorMessage("所选设备没有使用科室,不能报修");
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("validationFailed", true);
        } else {
            //1表示主动报修
            newServiceRequest.setSrRepairType("1");
            selectedAsset.setStatus(currentStatus);
            newServiceRequest.setAssetLocation(dptName);
            woService.createServiceRequest(newServiceRequest, selectedAsset, picListForRP);
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
    }

    ///
    protected void init() {
        tableDao = WebUtil.getBean(TableDefinitionRepository.class);
        unstructuredDataDao = WebUtil.getBean(UnstructuredDataRepository.class);
        //  pmOrderRuleRepository = WebUtil.getBean(PmOrderRuleRepository.class);
        pmckLstDao = WebUtil.getBean(PmOrderRuleCheckListRepository.class);
        reportDao = WebUtil.getBean(UnstructuredDataCollectionRepository.class);
        woService = WebUtil.getBean(V2_WorkOrderService.class);
        dao = WebUtil.getBean(V2_PmOrderRepository.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        orgDao = WebUtil.getBean(OrgInfoRepository.class);
        pmOrderService = WebUtil.getBean(V2_PmOrderService.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        pmOrderReportTemplateService = WebUtil.getBean(PmOrderReportTemplateService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        coWorkerDao = WebUtil.getBean(V2_WorkOrderCoworkerRepository.class);
        blobDao = WebUtil.getBean(BlobObjectRepository.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        terminate = Boolean.valueOf(WebUtil.getRequestParameter("terminate"));
        tableService = WebUtil.getBean(TableDefinitionService.class);
        this.selectedMap = new HashMap();
        /*backup  if((userContextService.hasAnyRole("DeptHead") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) ||(userContextService.hasAnyRole("ClinicalStaff")&&!userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest") ) || (userContextService.hasAnyRole("DeptHead")&&userContextService.hasAnyRole("ClinicalStaff")&&!userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest"))||userContextService.hasAnyRole("HospitalHead") && !userContextService.hasAnyRole("AssetHead,AssetStaff,ITAdmin,Guest") || userContextService.hasAnyRole("ITAdmin") &&( userContextService.hasAnyRole("HospitalHead")|| userContextService.hasAnyRole("ClinicalStaff")|| userContextService.hasAnyRole("DeptHead")) )*/

        /* if( (userContextService.hasAnyRole("DeptHead")||userContextService.hasAnyRole("HospitalHead")||userContextService.hasAnyRole("ClinicalStaff")) &&(!userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")|| userContextService.hasAnyRole("ITAdmin")) || (userContextService.hasAnyRole("DeptHead")&&userContextService.hasAnyRole("ClinicalStaff")&&!userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")))*/

//如果是院长等管理层登陆进来queryIndex默认是8即当前就显示待审核
        if ((userContextService.hasAnyRole("DeptHead") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) || (userContextService.hasAnyRole("ClinicalStaff") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) || (userContextService.hasAnyRole("DeptHead") && userContextService.hasAnyRole("ClinicalStaff") && !userContextService.hasAnyRole("HospitalHead,AssetHead,AssetStaff,ITAdmin,Guest")) || userContextService.hasAnyRole("HospitalHead") && !userContextService.hasAnyRole("AssetHead,AssetStaff,ITAdmin,Guest") || userContextService.hasAnyRole("ITAdmin") && (userContextService.hasAnyRole("HospitalHead") || userContextService.hasAnyRole("ClinicalStaff") || userContextService.hasAnyRole("DeptHead"))) {
            String query = WebUtil.getRequestParameter("query");
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = 8;
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        } else {
            String query = WebUtil.getRequestParameter("query");
            if (Strings.isNullOrEmpty(query)) {
                queryIndex = -1;
            } else {
                queryIndex = Integer.valueOf(query);
                searchList(queryIndex);
            }
        }
        this.filterBySite = false;
        this.filterByHospital = false;
        hmCheck = new HashMap<String, UnstructuredData>();

        user = UserContextService.getCurrentUserAccount();
        isInsulate = tenantInfoRepository.findByuid(user.getTenantUID()).getInsulate();
        isInsulate = isInsulate == null ? false : isInsulate;
    }

    public void submitAppvalPmOrder() {
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

    public List<UserAccount> getApproveList() {
        if (this.selectedPmOrder != null)
            return pmOrderService.getApproveList(selectedPmOrder.getAssetUid());
        else
            return new ArrayList<UserAccount>();
    }

    private List<Integer> tagGroupAssets;

    @Override
    protected GenericRepositoryUUID<V2_PmOrder> getDAO() {
        return dao;
    }

    public void acceptWorkOrder() {
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
                WebUtil.addErrorMessage("您的批量中不能包含待接单或待抢单的保养工单");
                return;
            }
            pmOrderService.acceptWorkOrderBatch(currentPagePmOrders, selectedMap, planTimeForBatch);
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

    //获取当前登陆人可看到的设备, 这个人需要在标签组里面
    private List<Integer> getTagGroupAssets() {
        if (tagGroupAssets == null) {
            tagGroupAssets = dao.getAssetsByTagGroup(UserContextService.getCurrentUserAccount().getId());
        }
        return tagGroupAssets;
    }

    /*DISPATER(2,"待派工", "Assign"),ACCEPT(3,"待接单", "Accept"),
			CONFIRM(5,"待验收", "Ack"),CLOSE(6,"待关单", "Close"),EVALUATE(7,"待评价", "Rate"),APPROVAL(8, "待审核", "Approval"), MAINTAIN(0, "保养中", "Repair");*/
    Page<V2_PmOrder> result;

    @Override
    protected Page<V2_PmOrder> loadData(PageRequest pageRequest) {
        this.setSearchFilter();
        removeFilterOnField("currentStepId");
        removeFilterOnField("isFinished");
        removeFilterOnField("dispatchMode");
        removeFilterOnField("ownerId");
        removeFilterOnField("approvalStatus");
        if(queryIndex==10){
            Page<V2_PmOrder> result =dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPagePmOrders = result.getContent();
            return result;
        }
        if(queryIndex==-2){
            searchFilters.add(new SearchFilter("simplify", SearchFilter.Operator.EQ, true));
        }
        if (queryIndex == -1) {
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            Page<V2_PmOrder> result = dao.findBySearchFilter(this.searchFilters, pageRequest);
            currentPagePmOrders = result.getContent();
            return result;
        }
        if (queryIndex == 2) { //待派工  Assign
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 4)));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, sitelist, "1*3+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, sitelist, "1*3+"));

        } else if (queryIndex == 3) {//待接单
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            //someone could only access siteA,but assigned with asset on SiteB, therefore "OR" is added saying that one also could see the asset that is assigned to him.
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 31) {//待抢单 dispatch 2表示人工派工或抢单模式 3表示抢单模式 4表示人工派工
            //抢单tab中 ,已经派的单但是未被接单人接的单子也要显示出来 (已经派的单状态是Accept，所以也要加进来)
            /*  searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.IN, Arrays.asList("Assign","Accept")));*/
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.NE, 4));
            List<String> sitelist = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).distinct().collect(Collectors.toList());
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Assign", "1*3+"));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Accept", "1*3+7*"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+7*"));
            searchFilters.add(new SearchFilter("dispatchMode", SearchFilter.Operator.IN, Arrays.asList(2, 3)));
            /*searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.NE, UserContextService.getCurrentUserAccount().getUid(), "1*3+4*"));*/
            //用户必须对其可视范围内的设备都可见,包括打过标签的设备(如果该设备的标签和组绑定了，并且当前用户没有在该组内，那是看不到的)和没有打过标签的设备
            //打过标签的设备(该标签都和组绑定过)
            List<Integer> tagGroupAssets = getTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*4+"));
            } else {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*4+"));
            }
            //没有打过标签的设备
            tagGroupAssets = getNoTagGroupAssets();
            if (tagGroupAssets != null && !tagGroupAssets.isEmpty()) {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, tagGroupAssets, "1*4+owner*"));
            } else {
                searchFilters.add(new SearchFilter("assetId", SearchFilter.Operator.IN, Arrays.asList(0), "1*4+owner*"));
            }
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getId(), "1*4+owner*6+"));
            //维修责任人数量小于等于1的条件
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner1*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId3", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner2*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner3*"));
            searchFilters.add(new SearchFilter("assetInfo.assetOwnerId2", SearchFilter.Operator.IsNull, null, "1*4+owner*6+owner3*"));
            
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
        } else if (queryIndex == 6) { //待关单
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Close"));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 8) { //待审核
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
            //1表示该pm工单已被提交等待审核
            searchFilters.add(new SearchFilter("approvalStatus", SearchFilter.Operator.EQ, "1"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 0) { //已计划
            searchFilters.add(new SearchFilter("currentStepId", SearchFilter.Operator.EQ, "Maintain"));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*3+"));
            //   searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.NE, true));
            searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
        } else if (queryIndex == 7) { //已关单
            if (!userContextService.hasRole("AssetHead") && !userContextService.isPmDispatcher()) {
                searchFilters.add(new SearchFilter("ownerId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid()));
                searchFilters.add(new SearchFilter("currentPersonId", SearchFilter.Operator.EQ, UserContextService.getCurrentUserAccount().getUid(), "1*2+"));
            }
            searchFilters.add(new SearchFilter("isFinished", SearchFilter.Operator.EQ, true));
        }
        this.selected = null;
        //     Page<V2_PmOrder> result1 =dao.findBySearchFilter(this.searchFilters,null);
        result = dao.findBySearchFilter(this.searchFilters, pageRequest);
        currentPagePmOrders = result.getContent();
        this.isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
        return result;
    }

    public void onSelectPmOrder() {
        accessoryList = getWorkOrderDetail(selected.getId());
        coWorkerList = getWorkOrderCoworker(selected.getId());
        // 获取picList
        picList = blobDao.findByBoUuid(selected.getId());
        reportList = pmOrderService.getReportList(selected.getId());
        loadHmCheck(selected);
    }

    public List<V2_WorkOrder_Detail> getWorkOrderDetail(String woId) {
        return pmOrderService.getWorkOrderDetails(woId);
    }

    public List<V2_WorkOrderCoworker> getWorkOrderCoworker(String woId) {
        return pmOrderService.getWorkOrderCoworker(woId);
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

    public void approvePMorder() {
        if (selectedPmOrder != null) {
            approvedDetails.stream().filter(item -> Optional.ofNullable(selectedMap.get(item.getId())).orElse(false)).forEach(item -> item.setApproved(true));
            if (approvedDetails.stream().anyMatch(item -> item.getApproved() == null || !item.getApproved()) && Strings.isNullOrEmpty(comments)) {
                WebUtil.addErrorMessage("审核不通过原因必须填写");
                RequestContext.getCurrentInstance().addCallbackParam("needComents", true);
                return;
            }
            approvedDetails.forEach(item -> {
                item.setCreatedDate(null);
                if (null == item.getApproved()) {
                    item.setApproved(false);
                }
            });
            pmOrderService.approvePMorder(selectedPmOrder, approvedDetails, this.comments);
            this.comments = null;
        }
    }

    public void prepareCompleteOrder(String woid) throws Exception {
        accessoryList = getWorkOrderDetail(woid);
        selectedPmOrder = dao.findById(woid);
        if (selectedPmOrder.getManHours() == null) {
            selectedPmOrder.setManHours(0.1);
        }
        confirmedUpDate = null;
        endTime = selectedPmOrder.getEndTime();
        planTime = selectedPmOrder.getPlanTime();
        accessoryList = getWorkOrderDetail(woid);
        coWorkerList = getWorkOrderCoworker(woid);
        picList = new ArrayList<V2_BlobObject>();
        loadHmCheck(selectedPmOrder);
        if (pmCheckItemList != null) {
            if (hmCheck.isEmpty()) {
                for (PmOrderRuleCheckList pmList : pmCheckItemList) {
                    UnstructuredData unData = new UnstructuredData();
                    unData.setValue("pass");
                    unData.setKey(pmList.getUid());
                    hmCheck.put(pmList.getUid(), unData);
                }
            }
        }
        picList = new ArrayList<V2_BlobObject>();
        picListForRP = new ArrayList<V2_BlobObject>();
    }

    //
    public void onSelectAllOfPage() {
        for (V2_PmOrder item : currentPagePmOrders) {
            selectedMap.put(item.getId(), this.isSelectedCurrentPage);
        }
        isSelectedCurrentPage = !currentPagePmOrders.stream().anyMatch(item -> !selectedMap.containsKey(item.getId()) || !selectedMap.get(item.getId()));
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
        if (!this.hasFilterOnField("assetInfo.siteUID")) {
            List<String> orgUidList = uaaService.getOrgListByUser(UserContextService.getCurrentUserAccount().getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            searchFilters.add(new SearchFilter("assetInfo.siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            searchFilters.add(new SearchFilter("assetInfo.clinicalDeptUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
    }

    public void searchList(Integer buttonIndex) {
        this.selectedMap = new HashedMap<>();
        queryIndex = buttonIndex;
        this.searchFilters = null;
        this.stepFileter = null;
        cancel();
    }

    public void cancel() {
        this.selectedAsset = null;
        planTime = null;
        planTimeForBatch = null;
        selectedPmOrder = null;
        assetName = null;
        endTime = null;
        ruleLevel = null;
        dptName = null;
        picList = null;
        planTimeForBatch = null;
        comments = null;
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
        pmOrderService.repairWorkOrderBatch(endTime, totalManHours, currentPagePmOrders, selectedMap);
        selectedMap = new HashMap<String, Boolean>();
    }

/*    public void batchComplete() {
       *//* List<String> assetIdList = new ArrayList<String>();
        //批量的必须是能"完成"的而非"抢单"的操作 如果有抢单的工单在里面 需要提醒用户
        selectedMap.forEach((k,v)->{
            if(v==true)
                assetIdList.add(k);
        });
        List<V2_PmOrder> pmList = dao.checkSimplify(assetIdList);
        if(pmList.size()!=assetIdList.size()){//说明选定的工单全都是可以简化即simplify为3
            //不能选择"抢单"的来做批量"完成"
            WebUtil.addErrorMessage("您的批量中不能包含待接单或待抢单的保养工单");
            return;
        }*//*
        pmOrderService.repairWorkOrderBatch(endTime, totalManHours, currentPagePmOrders, selectedMap);
    }*/

    //已计划-->保养完成
    public void repairPmOrder() {
        if (!requiredCheck()) {
            return;
        }
        saveCheckItemData();
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        selectedPmOrder.setQualityChecking(qaStatus);
        pmOrderService.repairWorkOrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList);
        //保养合格的话 去关单

        //cancel();
    }

    public void stagingPmOrder() {
        accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals("000")).collect(Collectors.toList()));
        coWorkerList.forEach(item -> item.setCreatedDate(null));
        accessoryList.forEach(item -> item.setCreatedDate(null));
        pmOrderService.stagingPmrder(selectedPmOrder, accessoryList, coWorkerList, endTime, selectedPmOrder.getPlanTime(), picList);
        //保存检查项
        saveCheckItemData();
        cancel();
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        AssetInfo asset = (AssetInfo) eventObject;
        if (asset == null) {
            return;
        }
        assetName = asset.getName();
        if (Strings.isNullOrEmpty(asset.getClinicalDeptName())) {
            dptName = null;
        } else {
            dptName = asset.getClinicalDeptName();
        }
        this.selectedAsset = asset;
        currentStatus = asset.getStatus();
        if (currentStatus == 1) {
            currentStatus = 3;
        }
        needConfirmTime = false;
    }

    public void prepareEditAccDetail(String itemId) {
        if ("new".equals(itemId)) {
            itemDetail = new V2_WorkOrder_Detail();
            itemDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            itemDetail.setWoId(selectedPmOrder.getId());
        } else {
            itemDetail = accessoryList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void removeWoDetail(String itemId, Integer index) {
        if (index == 1) {
            accessoryList.removeAll(accessoryList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        } else if (index == 2) {
            coWorkerList.removeAll(coWorkerList.stream().filter(item -> item.getId().equals(itemId)).collect(Collectors.toList()));
        }
    }

    public void prepareBatchComplete() {
        this.endTime = null;
        this.totalManHours = null;
     //   this.selectedMap = new HashMap();
    }

    public void prepareEditCoWorkerDetail(String itemId) {
        if ("new".equals(itemId)) {
            coWorkerDetail = new V2_WorkOrderCoworker();
            coWorkerDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            coWorkerDetail.setWoId(selectedPmOrder.getId());
        } else {
            coWorkerDetail = coWorkerList.stream().filter(item -> item.getId().equals(itemId)).findAny().get();
        }
    }

    public void addAcessItem() {
        if (!accessoryList.contains(itemDetail)) {
            accessoryList.add(itemDetail);
        }
    }

    public List<TableDefinition> getTablesName(V2_PmOrder v2) {
        List<TableDefinition> tDefinitions = pmOrderService.getTablesName(v2);
        return tDefinitions;
    }

    public List<PmOrderRuleCheckList> acPmOrderUleCheckList(V2_PmOrder v2PmOrder) {
        if (v2PmOrder == null)
            return null;
        PmOrderRule pmOrderRule = pmOrderService.acqPmOrderRule(v2PmOrder);
        if (pmOrderRule == null) {
            return null;
        }
        return pmckLstDao.findAllByRuleUID(pmOrderRule.getUid());
    }

    List<UnstructuredDataCollection> unDataCollection;
    UnstructuredDataCollection unCollection;
    PmOrderRuleCheckList pmCheckItem;
    UnstructuredDataCollectionRepository reportDao;
    List<PmOrderRuleCheckList> pmCheckItemList;// 每条检查项记录

    public List<CheckItem> loadHmCheck(V2_PmOrder v2PmOrder) {
        hmCheck = new HashMap<String, UnstructuredData>();
        pmCheckItemList = acPmOrderUleCheckList(v2PmOrder);
        unDataCollection = reportDao.findCKByBoUuid(v2PmOrder.getId(), "pm-checklist-report");//为0说明该工单的规则还没有绑定 通过和不通过
        List<CheckItem> CheckItemList = new ArrayList<CheckItem>();
        if (pmCheckItemList == null) return null;
        for (int i = 0; i < pmCheckItemList.size(); i++) {
            pmCheckItem = (PmOrderRuleCheckList) pmCheckItemList.get(i);
            if (unDataCollection != null && !unDataCollection.isEmpty()) { //
                unCollection = (UnstructuredDataCollection) unDataCollection.get(0);
                if (unCollection != null) {
                    List<UnstructuredData> dataList = (List<UnstructuredData>) unCollection.getData();
                    for (int k = 0; k < dataList.size(); k++) {
                        UnstructuredData unsData = (UnstructuredData) dataList.get(k);
                        if (unsData.getKey().equalsIgnoreCase(pmCheckItem.getUid())) {
                            CheckItemList.add(new CheckItem(pmCheckItem.getUid(), unsData));
                            hmCheck.put(pmCheckItem.getUid(), unsData);
                        }
                    }
                }
            }
        }
        return CheckItemList;
    }

    List<PmOrderRuleCheckList> pmOrderRuleCheckList;

    //map : item.uid(pmorderrulecheck.uid) value: unstrundata
    public List<V2_WorkOrder_Detail> getNeedApprovalDetails() {
        return approvedDetails == null ? null : approvedDetails.stream().filter(item -> item.getApproved() == null || !item.getApproved()).collect(Collectors.toList());
    }

    private V2_WorkOrderCoworkerRepository coWorkerDao;

    public void addCoWokerItem() {
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

    public void cancelWorkdetail() {
        itemDetail = null;
        coWorkerDetail = null;
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

    public String getDptName() {
        return dptName;
    }

    public void setDptName(String dptName) {
        this.dptName = dptName;
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

    public String getQualityCheckingBatch() {
        return qualityCheckingBatch;
    }

    public void setQualityCheckingBatch(String qualityCheckingBatch) {
        this.qualityCheckingBatch = qualityCheckingBatch;
    }

    public String getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(String qaStatus) {
        this.qaStatus = qaStatus;
    }

    public Double getTotalManHours() {
        return totalManHours;
    }

    public void setTotalManHours(Double totalManHours) {
        this.totalManHours = totalManHours;
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

    public void setPmCheckItemList(List<PmOrderRuleCheckList> pmCheckItemList) {
        this.pmCheckItemList = pmCheckItemList;
    }

    public List<PmOrderRuleCheckList> getPmCheckItemList() {
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

    public List<UnstructuredDataCollection> getReportList() {
        return reportList;
    }

    public void setReportList(List<UnstructuredDataCollection> reportList) {
        this.reportList = reportList;
    }

    public List<PmOrderRuleCheckList> getPmOrderRuleCheckList() {
        return pmOrderRuleCheckList;
    }

    public void setPmOrderRuleCheckList(List<PmOrderRuleCheckList> pmOrderRuleCheckList) {
        this.pmOrderRuleCheckList = pmOrderRuleCheckList;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }
}

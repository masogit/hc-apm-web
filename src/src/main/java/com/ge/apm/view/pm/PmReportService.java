package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.PictureRenderData;
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
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.google.common.base.Strings;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class PmReportService {

    private static final DateTimeFormatter DEFAULT_DAY = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String SIGNATURE_HOLDER = "审核人签名：";
    private static final String EMPTY_SIGNATURE_HOLDER = "";

    @Autowired
    private V2_PmOrderService pmOrderService;

    @Autowired
    private PmOrderReportTemplateService pmOrderReportTemplateService;

    @Autowired
    private TableDefinitionService tableService;

    @Autowired
    private BlobFileService blobService;

    /**
     * Render Dynamic Table content.
     *
     * @param collection data collection
     * @param content    content map
     * @param component  MaintenanceTemplate Component
     */
    private void renderDynamicTable(UnstructuredDataCollection collection, Map<String, Object> content, MaintenanceTemplate.Component component) {
        TableDefinition tableDefinition = tableService.findById(collection.getSchemaId());
        TableSchema table = JSONObject.parseObject(tableDefinition.getSchema(), TableSchema.class);
        List<UnstructuredData> dataList = collection.getData();

        // Get raw data
        Map<String, Object> rawTableValue = new HashMap<>();
        dataList.forEach(item -> {
            try {
                rawTableValue.put(item.getKey(), item.getValue());
            } catch (ClassNotFoundException e) {
                rawTableValue.put(item.getKey(), item.getValueSerialized());
            }
        });

        // update select box option values
        Map<String, Object> tableValue = this.tableService.changSelectFommDataByJSON(tableDefinition, rawTableValue);
        final String emptyStr = "";
        final String prefix = component.toString();
        for (TableGroup group : table.getGroups()) {
            for (TableItem item : group.getItems()) {
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

    public String wrap(String prefix, String key) {
        return SchemaParser.UNDERSCORE_JOINER.join(prefix, key);
    }

    /**
     * Translate check item description.
     *
     * @param uid     check item uid
     * @param hmCheck hmCheck
     * @return description, or 'n/a'
     */
    private String getCheckListResult(String uid, Map<String, UnstructuredData> hmCheck) {
        UnstructuredData data = hmCheck.get(uid);
        if (data != null) {
            try {
                return (String) data.getValue();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "n/a";
    }

    private Map<String, Object> populateReportContent(V2_PmOrder pmOrder, List<UnstructuredDataCollection> reportList, Map<String, UnstructuredData> hmCheck, List<V2_WorkOrderCoworker> coWorkerList, List<V2_WorkOrder_Detail> accessoryList, List<PmOrderSteps> pmOrderSteps, List<PmOrderRuleCheckList> pmCheckItemList) {
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
        if (coWorkerList != null) {
            int count = 0;
            for (V2_WorkOrderCoworker coworker : coWorkerList) {
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
        if (accessoryList.size() > 0) {
            for (V2_WorkOrder_Detail item : accessoryList) {
                if ("000".equalsIgnoreCase(item.getId())) continue;

                Map<String, Object> accessoryMap = new HashMap<>();

                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.NAME.toString(), item.getParts());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.QUANTITY.toString(), item.getApartsQuantity());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.PRICE.toString(), item.getClosedPrice());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.TYPE.toString(), FieldValueMessageController.fieldValue("partsType", item.getPartsType()));
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.SERIAL_NUM.toString(), item.getSerialNum());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.WARRANTY_DATE.toString(), item.getWarrantyDate());
                accessoryMap.put(MaintenanceTemplate.AccessoryInfo.ARRIVED.toString(), item.getArrived());

                accessoryInfoTable.add(accessoryMap);
            }
        }
        content.put(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(), accessoryInfoTable);

        // Accessory Signature
        String signatureStorageId = pmOrder.getSignature();
        if (!Strings.isNullOrEmpty(signatureStorageId)) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getSignatureBytes(signatureStorageId);
            if (signatureBytes != null) {
                content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.toString(), SIGNATURE_HOLDER);
                content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE.toString(), new PictureRenderData(100, 80, "signature.png", signatureBytes));
            }
        }

        if (!content.containsKey(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE.toString())) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.toString(), EMPTY_SIGNATURE_HOLDER);
            content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE.toString(), new PictureRenderData(1, 1, "signature.png", signatureBytes));
        }

        // Maintenance Workflow Information
        List<Map<String, Object>> maintenanceInfoTable = new ArrayList<>();
        for (PmOrderSteps item : pmOrderSteps) {
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
        if (pmCheckItemList != null) {
            for (PmOrderRuleCheckList item : pmCheckItemList) {
                Map<String, Object> checkListMap = new HashMap<>();
                checkListMap.put(MaintenanceTemplate.CheckListInfo.NAME.toString(), item.getName());
                checkListMap.put(MaintenanceTemplate.CheckListInfo.PERIOD.toString(), item.getPeriod());
                checkListMap.put(MaintenanceTemplate.CheckListInfo.CONTENT.toString(), item.getRemark());
                checkListMap.put(MaintenanceTemplate.CheckListInfo.RESULT.toString(), FieldValueMessageController.fieldValue("checkItem", getCheckListResult(item.getUid(), hmCheck)));

                checkListTable.add(checkListMap);
            }
        }
        content.put(MaintenanceTemplate.Component.CHECK_LIST.toString(), checkListTable);


        // Audit Message List
        List<Map<String, Object>> auditMsgTable = new ArrayList<>();
        if (this.pmOrderService.getServiceRequestMessageList(pmOrder.getId()) != null) {
            for (ServiceRequestMsg item : this.pmOrderService.getServiceRequestMessageList(pmOrder.getId())) {
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
                if (item == null || Strings.isNullOrEmpty(item.getBoType())) {
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

    public DefaultStreamedContent generate(V2_PmOrder pmOrder, Boolean isInsulate, UserAccount user) throws IOException {
        byte[] activeTemplateBytes = this.pmOrderReportTemplateService.getActiveTemplateBytes(isInsulate, user);
        List<UnstructuredDataCollection> reportList = pmOrderService.getReportList(pmOrder.getId());
        List<V2_WorkOrderCoworker> coWorkerList = getCoWorkerList(pmOrderService.getWorkOrderCoworker(pmOrder.getId()));
        List<V2_WorkOrder_Detail> accessoryList = getAccessoryList(pmOrderService.getWorkOrderDetails(pmOrder.getId()));
        List<PmOrderSteps> pmOrderSteps = pmOrderService.getPmOrderStep(pmOrder.getId());
        PmOrderRule rule = this.pmOrderService.acqPmOrderRule(pmOrder);
        if (rule == null) throw new BusinessException("系统未找到绑定的规则!");
        // refer to V2PmOrderController.acPmOrderUleCheckList, when order.isFinished = true
        List<PmOrderRuleCheckList> pmCheckItemList = pmOrderService.getPmOrderRuleCheckLists(pmOrder, rule);
        Map<String, UnstructuredData> hmCheck = pmOrderService.fetchHmCheck(pmCheckItemList, pmOrder);
        InputStream rawTemplate;
        if (!Strings.isNullOrEmpty(rule.getReportStorageId())) {
            // get template if the report storage is set.
            StreamedContent template = this.blobService.getBlobObjectByStorageId(rule.getReportStorageId());
            rawTemplate = template.getStream();
        } else {
            // generate template otherwise.
            try {
                String safetySchema = reportList.stream().filter(item -> Objects.equals(item.getBoType(), "pm-safety-report"))
                        .findAny().map(UnstructuredDataCollection::getSchemaId).orElse(null);
                String performanceSchema = reportList.stream().filter(item -> Objects.equals(item.getBoType(), "pm-performance-report"))
                        .findAny().map(UnstructuredDataCollection::getSchemaId).orElse(null);
                rawTemplate = this.pmOrderReportTemplateService.generateReportTemplateBySchemaId(activeTemplateBytes, safetySchema, performanceSchema);
            } catch (IOException e) {
                throw new BusinessException("自动模版生成失败");
            }
        }

        if (rawTemplate == null) throw new BusinessException("请先上传保养报告模版!");

        Map<String, Object> datas = populateReportContent(pmOrder, reportList, hmCheck, coWorkerList, accessoryList, pmOrderSteps, pmCheckItemList);

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
        return new DefaultStreamedContent(doc, "application/msword", "maintenance-report.docx");
    }

    public List<V2_WorkOrder_Detail> getAccessoryList(List<V2_WorkOrder_Detail> accessoryList) {
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

    public List<V2_WorkOrderCoworker> getCoWorkerList(List<V2_WorkOrderCoworker> coWorkerList) {
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
}

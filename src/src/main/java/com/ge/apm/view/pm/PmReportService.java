package com.ge.apm.view.pm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.domain.bo.MaintenanceTemplate;
import com.ge.apm.domain.bo.parser.SchemaParser;
import com.ge.apm.domain.bo.render.*;
import com.ge.apm.domain.dto.FieldColumnDto;
import com.ge.apm.domain.exception.BusinessException;
import com.ge.apm.domain.pure.TableGroup;
import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.common.ConfigItemService;
import com.ge.apm.service.pm.PmOrderSteps;
import com.ge.apm.service.pm.V2_PmOrderService;
import com.ge.apm.service.report.TableDefinitionService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.wo.PmOrderReportTemplateService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.pdf.PDFConversion;
import com.ge.healthcare.cn.common.pdf.PDFConversionUtil;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.render.DynamicColumnTableRenderPolicy;
import com.ge.healthcare.cn.common.utils.render.PmPartsPictureTableRenderPolicy;
import com.google.common.base.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.framework.web.WebUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PmReportService {
    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PmReportService.class);

    private static final DateTimeFormatter DEFAULT_DAY = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String SIGNATURE_HOLDER = "审核人签名：";
    private static final String ACK_SIGNATURE_HOLDER = "保养验收签名：";
    private static final String WC_SIGNATURE_HOLDER = "保养完成签名：";
    private static final String MAINTENANCE_COMPLETION_SIGNATURE_TITLE = "保养完成签名：";
    private static final String ACCEPTANCE_SIGNATURE_TITLE = "验收签名：";
    private static final String EMPTY_SIGNATURE_HOLDER = "";

    @Autowired
    private V2_PmOrderService pmOrderService;

    @Autowired
    private PmOrderReportTemplateService pmOrderReportTemplateService;

    @Autowired
    private TableDefinitionService tableService;

    @Autowired
    private BlobFileService blobService;

    @Autowired
    private BlobObjectRepository blobObjectRepository;

    @Autowired
    private OrgInfoRepository orgInfoRepository;

    @Autowired
    private UnstructuredDataCollectionRepository dataCollectionRepository;

    @Autowired
    private UnstructuredDataRepository unstructuredDataDao;

    @Autowired
    private SystemTemplateService systemTemplateService;

    @Autowired
    private ConfigItemService configItemService;

    @Autowired
    private V2_WorkOrderCoworkerRepository workOrderCoworkerDao;

    @Autowired
    private ConfigCacheService cacheService;

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
                            } catch (ClassCastException e) {
                                String value = (String) tableValue.get(item.getKey());
                                Boolean res = value.equals("true") ? true : false;
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
                    case Picture:
                        if (item.getKey() != null) {
                            try {
                                String val = (String) tableValue.getOrDefault(item.getKey(), emptyStr);
                                byte[] pictureBytes = this.pmOrderReportTemplateService.getSignatureBytes(val);
                                content.put(wrap(prefix, item.getKey()), new PictureRenderData(100, 80, PictureType.PNG, pictureBytes));
                            } catch (Exception e) {
                                logger.info("自定义照片渲染出错:", e);
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

    private Map<String, Object> populateReportContent(V2_PmOrder pmOrder, List<UnstructuredDataCollection> reportList, Map<String, UnstructuredData> hmCheck, List<V2_WorkOrderCoworker> coWorkerList, List<V2_WorkOrder_Detail> accessoryList, List<PmOrderSteps> pmOrderSteps, List<PmOrderRuleCheckList> pmCheckItemList, List<UnstructuredData> dataList) {
        Map<String, Object> content = new HashMap<>();

        // Device information
        content.put(MaintenanceTemplate.Device.NAME.toString(), pmOrder.getAssetInfo().getName());
        content.put(MaintenanceTemplate.Device.DEPARTMENT.toString(), pmOrder.getAssetInfo().getClinicalDeptName());
        content.put(MaintenanceTemplate.Device.Site.toString(), orgInfoRepository.findByUid(pmOrder.getSiteUID()).getName());
        content.put(MaintenanceTemplate.Device.TYPE.toString(), pmOrder.getAssetInfo().getFunctionType());
        content.put(MaintenanceTemplate.Device.OEM.toString(), pmOrder.getAssetInfo().getManufacture());
        content.put(MaintenanceTemplate.Device.SERIAL.toString(), pmOrder.getAssetInfo().getSerialNum());
        content.put(MaintenanceTemplate.Device.NUMBER.toString(), pmOrder.getAssetInfo().getDepartNum());
        content.put(MaintenanceTemplate.Device.QR.toString(), pmOrder.getAssetInfo().getQrCode());
        content.put(MaintenanceTemplate.Device.MAKER.toString(), pmOrder.getAssetInfo().getMakerShortName());
        content.put(MaintenanceTemplate.Device.INSTALL_DATE.toString(), pmOrder.getAssetInfo().getInstallDate() != null ? DEFAULT_DAY.print(pmOrder.getAssetInfo().getInstallDate().getTime()) : "");
        content.put(MaintenanceTemplate.Device.LOCATION_NAME.toString(), pmOrder.getAssetInfo().getLocationName());
        content.put(MaintenanceTemplate.Device.GROUP.toString(), WebUtil.getFieldValueMessage("assetGroup",  pmOrder.getAssetInfo().getAssetGroup()!=null?  pmOrder.getAssetInfo().getAssetGroup().toString():""));
        String periodUnit = pmOrder.getPeriodUnit();
        String period = pmOrder.getPeriod() == null ?"":pmOrder.getPeriod().toString();
        periodUnit =(StringUtils.isEmpty(periodUnit)?"":"y".equalsIgnoreCase(periodUnit)?"年":"m".equalsIgnoreCase(periodUnit)?"月":"w".equalsIgnoreCase(periodUnit)?"周":"d".equalsIgnoreCase(periodUnit)?"日":"");
        content.put(MaintenanceTemplate.Device.PERIOD.toString(), period + periodUnit);


        // Maintenance information
        content.put(MaintenanceTemplate.Maintenance.NUMBER.toString(), pmOrder.getPmNum());
        content.put(MaintenanceTemplate.Maintenance.CREATOR.toString(), pmOrder.getCreatorName());
        if(pmOrder.getRuleLevel()==1){
            content.put(MaintenanceTemplate.Maintenance.LEVEL.toString(), "巡检");
        }else if(pmOrder.getRuleLevel()==4){
            content.put(MaintenanceTemplate.Maintenance.LEVEL.toString(), "质量控制");
        }else {
            content.put(MaintenanceTemplate.Maintenance.LEVEL.toString(), pmOrder.getRuleLevel());
        }
        content.put(MaintenanceTemplate.Maintenance.PLAN_TIME.toString(), pmOrder.getPlanTime() != null ? DEFAULT_DAY.print(pmOrder.getPlanTime().getTime()) : "");
        content.put(MaintenanceTemplate.Maintenance.COMPLETE_TIME.toString(), pmOrder.getEndTime() != null ? DEFAULT_DAY.print(pmOrder.getEndTime().getTime()) : "");
        content.put(MaintenanceTemplate.Maintenance.TYPE.toString(), FieldValueMessageController.fieldValue("pmType", String.valueOf(pmOrder.getPmType())));
        content.put(MaintenanceTemplate.Maintenance.OPERATOR.toString(), pmOrder.getOwnerName());
        content.put(MaintenanceTemplate.Maintenance.OP_TIME.toString(), pmOrder.getManHours());
        content.put(MaintenanceTemplate.Maintenance.RESULT.toString(), FieldValueMessageController.fieldValue("qualityChecking", pmOrder.getQualityChecking()));
        content.put(MaintenanceTemplate.Maintenance.COMMENTS.toString(), pmOrder.getComments());
        content.put(MaintenanceTemplate.Maintenance.TRAVELTIME.toString(), pmOrder.getTravelTime());
        // Coworker List
        if (coWorkerList != null) {
            int count = 0;
            for (V2_WorkOrderCoworker coworker : coWorkerList) {
                // ignore fake empty coworker
                if ("000".equalsIgnoreCase(coworker.getId())) continue;
                String manHours = String.valueOf(coworker.getManHours());
                if (count == 0) {
                    content.put(MaintenanceTemplate.Maintenance.COWORKER1.toString(), coworker.getCowokerUserName());
                    content.put(MaintenanceTemplate.Maintenance.COWORKER1_TIME.toString(), "null".equalsIgnoreCase(manHours)?"":manHours);
                } else if (count == 1) {
                    content.put(MaintenanceTemplate.Maintenance.COWORKER2.toString(), coworker.getCowokerUserName());
                    content.put(MaintenanceTemplate.Maintenance.COWORKER2_TIME.toString(), "null".equalsIgnoreCase(manHours)?"":manHours);
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
                content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE.toString(), new PictureRenderData(100, 80, PictureType.PNG, signatureBytes));
            }
        }

        //ack_signature
        String ackSignatureStorageId = pmOrder.getAckSignature();
        if (!Strings.isNullOrEmpty(ackSignatureStorageId)) {
            byte[] ackSignatureBytes = this.pmOrderReportTemplateService.getSignatureBytes(ackSignatureStorageId);
            if (ackSignatureBytes != null) {
                content.put(MaintenanceTemplate.Component.ACK_SIGNATURE_PLACE_HOLDER.toString(), ACK_SIGNATURE_HOLDER);
                content.put(MaintenanceTemplate.Component.ACK_SIGNATURE.toString(), new PictureRenderData(100, 80, PictureType.PNG, ackSignatureBytes));
            }
        }

        //wc_signature
        String wcSignatureStorageId = pmOrder.getWcSignature();
        if (!Strings.isNullOrEmpty(wcSignatureStorageId)) {
            byte[] wcSignatureBytes = this.pmOrderReportTemplateService.getSignatureBytes(wcSignatureStorageId);
            if (wcSignatureBytes != null) {
                content.put(MaintenanceTemplate.Component.WC_SIGNATURE_PLACE_HOLDER.toString(), WC_SIGNATURE_HOLDER);
                content.put(MaintenanceTemplate.Component.WC_SIGNATURE.toString(), new PictureRenderData(100, 80, PictureType.PNG, wcSignatureBytes));
            }
        }
        Boolean empty = false;
        Set<String> ids = accessoryList.stream().map(p -> p.getId()).collect(Collectors.toSet());
        List partPictureStorageIds = blobObjectRepository.getPartPictures(ids).stream().map(blob -> { if (".jpg,.jpeg,.png".contains(blob.getObjectName().substring(blob.getObjectName().lastIndexOf(".") + 1).toLowerCase())) return blob.getObjectStorageId();return null; }).collect(Collectors.toList());
        for (int i = 0; i < 5; i++) {
            if (i >= partPictureStorageIds.size()) empty = true; //实际图片少于5个
            byte[] pictureBytes = null;
            if (empty) {
                pictureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            } else {
                pictureBytes = this.pmOrderReportTemplateService.getSignatureBytes((String) partPictureStorageIds.get(i));
            }
            if (pictureBytes != null) {
                content.put("parts_picture_holder" + i, BooleanUtils.isTrue(empty) ? EMPTY_SIGNATURE_HOLDER : "备件图片");
                content.put("parts_picture" + i, new PictureRenderData(100, 80, PictureType.PNG, pictureBytes));
            }
        }

        List pictureBeforeStorageIds = blobObjectRepository.fetchBlobObjectByUuidAndType(pmOrder.getId(), "PrePmOrder").stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toList());
        empty = false;
        List<byte[]>  pmPrePicture = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i >= pictureBeforeStorageIds.size()) empty = true; //实际图片少于5个
            byte[] pictureBytes = null;
            if (empty) {
                pictureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            } else {
                pictureBytes = this.pmOrderReportTemplateService.getSignatureBytes((String) pictureBeforeStorageIds.get(i));
                pmPrePicture.add(pictureBytes);
            }
            if (pictureBytes != null) {
                content.put("pm_pre_picture" + i, new PictureRenderData(100, 80, PictureType.PNG, pictureBytes));
            }
        }
        content.put("pm_pre_picture_table",pmPrePicture);

        List pictureAfterStorageIds = blobObjectRepository.fetchBlobObjectByUuidAndType(pmOrder.getId(), "PmOrder").stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toList());
        empty = false;
        List<byte[]>  pmAfterPicture = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i >= pictureAfterStorageIds.size()) empty = true; //实际图片少于5个
            byte[] pictureBytes = null;
            if (empty) {
                pictureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            } else {
                pictureBytes = this.pmOrderReportTemplateService.getSignatureBytes((String) pictureAfterStorageIds.get(i));
                pmAfterPicture.add(pictureBytes);
            }
            if (pictureBytes != null) {
                content.put("pm_after_picture" + i, new PictureRenderData(100, 80, PictureType.PNG, pictureBytes));
            }
        }
        content.put("pm_after_picture_table",pmAfterPicture);
        empty = false;
        List pictureStorageIds = blobObjectRepository.findByBoUuid(pmOrder.getId()).stream().map(blob -> {
            if (".jpg,.jpeg,.png".contains(blob.getObjectName().substring(blob.getObjectName().lastIndexOf(".") + 1).toLowerCase())) {
                if (StringUtils.isNotEmpty(blob.getBoType()) && (blob.getBoType().equals("PrePmOrder") || blob.getBoType().equals("PmOrder"))) {
                    return null;
                }
                return blob.getObjectStorageId();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        for (int i = 0; i < 30; i++) {
            if (i >= pictureStorageIds.size()) empty = true; //实际图片少于30个
            byte[] pictureBytes = null;
            if (empty) {
                pictureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            } else {
                pictureBytes = this.pmOrderReportTemplateService.getSignatureBytes((String) pictureStorageIds.get(i));
            }
            if (pictureBytes != null) {
                content.put("pm_picture_holder" + i, BooleanUtils.isTrue(empty) ? EMPTY_SIGNATURE_HOLDER : "工单图片");
                content.put("pm_picture" + i, new PictureRenderData(100, 80,PictureType.PNG, pictureBytes));
            }
        }

        //保养完成签名
        String wcSignature = pmOrder.getWcSignature();
        if (!Strings.isNullOrEmpty(wcSignature)) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getSignatureBytes(wcSignature);
            if (signatureBytes != null) {
                content.put(MaintenanceTemplate.Component.MAINTENANCE_COMPLETION_SIGNATURE_TITLE.toString(), MAINTENANCE_COMPLETION_SIGNATURE_TITLE);
                content.put(MaintenanceTemplate.Component.MAINTENANCE_COMPLETION_SIGNATURE.toString(), new PictureRenderData(100, 80, PictureType.PNG, signatureBytes));
            }
        }
        //验收签名
        String ackSignature = pmOrder.getAckSignature();
        if (!Strings.isNullOrEmpty(ackSignature)) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getSignatureBytes(ackSignature);
            if (signatureBytes != null) {
                content.put(MaintenanceTemplate.Component.ACCEPTANCE_SIGNATURE_TITLE.toString(), ACCEPTANCE_SIGNATURE_TITLE);
                content.put(MaintenanceTemplate.Component.ACCEPTANCE_SIGNATURE.toString(), new PictureRenderData(100, 80, PictureType.PNG, signatureBytes));
            }
        }

        if (!content.containsKey(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE.toString())) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE_PLACE_HOLDER.toString(), EMPTY_SIGNATURE_HOLDER);
            content.put(MaintenanceTemplate.Component.ACCESSORY_SIGNATURE.toString(), new PictureRenderData(1, 1, PictureType.PNG, signatureBytes));
        }
        if (!content.containsKey(MaintenanceTemplate.Component.ACK_SIGNATURE.toString())) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            content.put(MaintenanceTemplate.Component.ACK_SIGNATURE_PLACE_HOLDER.toString(), EMPTY_SIGNATURE_HOLDER);
            content.put(MaintenanceTemplate.Component.ACK_SIGNATURE.toString(), new PictureRenderData(1, 1, PictureType.PNG, signatureBytes));
        }
        if (!content.containsKey(MaintenanceTemplate.Component.WC_SIGNATURE.toString())) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            content.put(MaintenanceTemplate.Component.WC_SIGNATURE_PLACE_HOLDER.toString(), EMPTY_SIGNATURE_HOLDER);
            content.put(MaintenanceTemplate.Component.WC_SIGNATURE.toString(), new PictureRenderData(1, 1, PictureType.PNG, signatureBytes));
        }
        if (!content.containsKey(MaintenanceTemplate.Component.MAINTENANCE_COMPLETION_SIGNATURE.toString())) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            content.put(MaintenanceTemplate.Component.MAINTENANCE_COMPLETION_SIGNATURE_TITLE.toString(), EMPTY_SIGNATURE_HOLDER);
            content.put(MaintenanceTemplate.Component.MAINTENANCE_COMPLETION_SIGNATURE.toString(), new PictureRenderData(1, 1, PictureType.PNG, signatureBytes));
        }
        if (!content.containsKey(MaintenanceTemplate.Component.ACCEPTANCE_SIGNATURE.toString())) {
            byte[] signatureBytes = this.pmOrderReportTemplateService.getDefaultSignatureBytes();
            content.put(MaintenanceTemplate.Component.ACCEPTANCE_SIGNATURE_TITLE.toString(), EMPTY_SIGNATURE_HOLDER);
            content.put(MaintenanceTemplate.Component.ACCEPTANCE_SIGNATURE.toString(), new PictureRenderData(1, 1, PictureType.PNG, signatureBytes));
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

        // coworker hours pmOrder
        List<Map<String, Object>> coworkerHoursInfoTable = new ArrayList<>();
        if(pmOrder.getCoworkerEntryType()!=null &&  pmOrder.getCoworkerEntryType() == 1){
            List<V2_WorkOrderCoworker> v2WorkOrderCoworkers = workOrderCoworkerDao.findByWoIdOrderByCreatedDateDesc(pmOrder.getId());
            for (V2_WorkOrderCoworker item : v2WorkOrderCoworkers) {
                Map<String, Object> coworkerHoursInfoMap = new HashMap<>();
                coworkerHoursInfoMap.put(MaintenanceTemplate.CoworkerHours.COWORKER_USER_NAME.toString(), item.getCowokerUserName());
                coworkerHoursInfoMap.put(MaintenanceTemplate.CoworkerHours.START_TIME.toString(), item.getStartTime());
                coworkerHoursInfoMap.put(MaintenanceTemplate.CoworkerHours.END_TIME.toString(), item.getEndTime());
                coworkerHoursInfoMap.put(MaintenanceTemplate.CoworkerHours.MAN_HOURS.toString(), item.getManHours());
                coworkerHoursInfoMap.put(MaintenanceTemplate.CoworkerHours.REMARK.toString(), item.getRemark());

                coworkerHoursInfoTable.add(coworkerHoursInfoMap);
            }
        }
        content.put(MaintenanceTemplate.Component.COWORKER_HOURS_TABLE.toString(), coworkerHoursInfoTable);
        List<Map<String, Object>> manHoursDetail = new ArrayList<>();
        if(pmOrder.getEntryType()!=null &&  pmOrder.getEntryType() == 1){
            List<V2_PmOrderManHoursDetail> pmOrderManHoursDetails = pmOrder.getPmOrderManHoursDetails();
            if(CollectionUtils.isNotEmpty(pmOrderManHoursDetails)){
                for (V2_PmOrderManHoursDetail item : pmOrderManHoursDetails) {
                    Map<String, Object> manHoursInfoMap = new HashMap<>();
                    manHoursInfoMap.put(MaintenanceTemplate.ManHours.START_TIME.toString(), item.getStartTime());
                    manHoursInfoMap.put(MaintenanceTemplate.ManHours.END_TIME.toString(), item.getEndTime());
                    manHoursInfoMap.put(MaintenanceTemplate.ManHours.MAN_HOURS.toString(), item.getHours());
                    manHoursInfoMap.put(MaintenanceTemplate.ManHours.REMARK.toString(), item.getRemark());
                    manHoursDetail.add(manHoursInfoMap);
                }
            }
        }
        content.put(MaintenanceTemplate.Component.MAN_HOURS_DETAIL.toString(), manHoursDetail);

        // Check List
        List<Map<String, Object>> checkListTable = new ArrayList<>();
        for (UnstructuredData data : dataList) {
            Map<String, Object> checkListMap = new HashMap<>();
            checkListMap.put(MaintenanceTemplate.CheckListInfo.NAME.toString(), data.getName());
            checkListMap.put(MaintenanceTemplate.CheckListInfo.PERIOD.toString(), data.getPeriod());
            checkListMap.put(MaintenanceTemplate.CheckListInfo.CONTENT.toString(), data.getRemark());
            String valueData = "n/a";
            try {
                valueData = (String) data.getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkListMap.put(MaintenanceTemplate.CheckListInfo.RESULT.toString(), FieldValueMessageController.fieldValue("checkItem", valueData));
            checkListTable.add(checkListMap);
        }
//        if (pmCheckItemList != null) {
//            for (PmOrderRuleCheckList item : pmCheckItemList) {
//                Map<String, Object> checkListMap = new HashMap<>();
//                checkListMap.put(MaintenanceTemplate.CheckListInfo.NAME.toString(), item.getName());
//                checkListMap.put(MaintenanceTemplate.CheckListInfo.PERIOD.toString(), item.getPeriod());
//                checkListMap.put(MaintenanceTemplate.CheckListInfo.CONTENT.toString(), item.getRemark());
//                checkListMap.put(MaintenanceTemplate.CheckListInfo.RESULT.toString(), FieldValueMessageController.fieldValue("checkItem", getCheckListResult(item.getUid(), hmCheck)));
//
//                checkListTable.add(checkListMap);
//            }
//        }
        content.put(MaintenanceTemplate.Component.CHECK_LIST.toString(), checkListTable);


        // Audit Message List
        List<Map<String, Object>> auditMsgTable = new ArrayList<>();
        if (this.pmOrderService.getServiceRequestMessageList(pmOrder.getId()) != null) {
            for (ServiceRequestMsg item : this.pmOrderService.getServiceRequestMessageList(pmOrder.getId())) {
                Map<String, Object> auditMsgMap = new HashMap<>();
                auditMsgMap.put(MaintenanceTemplate.AuditMsgInfo.TIME.toString(), DateFormatUtils.format(item.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
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

    public boolean checkDownloadPDF(Map<String, Boolean> pmOrders){
        AtomicBoolean r = new AtomicBoolean(true);
        pmOrders.forEach((id, selected) -> {
            if (selected) {
                V2_PmOrder pmOrder = pmOrderService.findById(id);
                if (!pmOrder.getCurrentStepName().equals("已关单")) {
                    //WebUtil.addErrorMessage(pmOrder.getPmNum().concat(" 保养记录未关单！"));
                    r.set(false);
                }
            }
        });
        return r.get();
    }

    public DefaultStreamedContent generate(Map<String, Boolean> pmOrders, Boolean isInsulate, UserAccount user) throws IOException {
        if (!pmOrders.values().contains(true)) {
            throw new BusinessException("未选中保养记录");
        }
        long count = pmOrders.values().stream().filter(i -> i).count();
//        byte[] activeTemplateBytes = this.pmOrderReportTemplateService.getActiveTemplateBytes(isInsulate, user);
        String pmOrderExportType = configItemService.getConfigItemByKeyToString("PMOrderExportType", user);
        String  PmOrderReportNameConfigurationString = configItemService.getConfigItemByKeyToString("PmOrderReportNameConfiguration",user);
        List<FieldColumnDto> fieldColumnDtos = JSON.parseArray(PmOrderReportNameConfigurationString, FieldColumnDto.class);

        byte[] activeTemplateBytes =  systemTemplateService.getTemplateFileByte(SystemTemplateEnum.PM_ORDER_REPORT, user);

        String reportName = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "保养工单.zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(bout)) {
            List<String> ids = new ArrayList<>();
            pmOrders.forEach((id, selected) -> {
                if (selected) {
                    try {
                        V2_PmOrder pmOrder = pmOrderService.findById(id);
                        OrgInfo orgByUid = cacheService.findOrgByUid(pmOrder.getSiteUID());
//                        String name = "保养-"+orgByUid.getName()+"-"+pmOrder.getAssetName() + "-" + pmOrder.getPmNum() + ".docx";

                        StringBuilder fileName = new StringBuilder();
                        for (FieldColumnDto fieldColumnDto : fieldColumnDtos) {
                            String fieldValue = null;
                            if ("orderType".equals(fieldColumnDto.getField())) {
                                fieldValue = "保养";
                            } else if ("siteName".equals(fieldColumnDto.getField())) {
                                fieldValue = orgByUid.getName();
                            } else if ("assetName".equals(fieldColumnDto.getField())) {
                                fieldValue = pmOrder.getAssetName();
                            } else if ("srNum".equals(fieldColumnDto.getField())) {
                                fieldValue = pmOrder.getPmNum();
                            }

                            if (fieldValue != null) {
                                if (fileName.length() > 0) {
                                    fileName.append("-");
                                }
                                fileName.append(fieldValue);
                            }
                        }
                        String name = fileName + ".docx";

                        byte[] bytes = generate(pmOrder, activeTemplateBytes);
                        if("pdf".equalsIgnoreCase(pmOrderExportType)){
                            bytes = getPDF(bytes);
                            name = name.substring(0, name.lastIndexOf(".")).concat(".pdf");
                        }
                        if (bytes != null) {
                            List<V2_BlobObject> byBoUuid = blobObjectRepository.findByBoUuid(id);
                            if(CollectionUtils.isNotEmpty(byBoUuid)){
                                byBoUuid = byBoUuid.stream().filter(item -> !(item.getBoType().equals(V2_BlobObject.BO_TYPE_PrePmOrder)  ||  item.getBoType().equals(V2_BlobObject.BO_TYPE_PmOrder) )   ).collect(Collectors.toList());
                            }

                            zout.putNextEntry(new ZipEntry(name));
                            IOUtils.write(bytes, zout);
                            java.util.Set<String> addedEntryNames = new java.util.HashSet<>();
                            addedEntryNames.add(name);
                            for (V2_BlobObject v2BlobObject : byBoUuid) {
                                String vname = pmOrder.getAssetName() + "-" + pmOrder.getPmNum() + "-" + v2BlobObject.getObjectName();
                                // 防止同名条目导致 ZipException: duplicate entry
                                if (!addedEntryNames.add(vname)) {
                                    int seq = 1;
                                    while (addedEntryNames.contains(vname + "(" + seq + ")")) {
                                        seq++;
                                    }
                                    vname = vname + "(" + seq + ")";
                                    addedEntryNames.add(vname);
                                }
                                zout.putNextEntry(new ZipEntry(vname));
                                byte[] bytess  = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(v2BlobObject.getObjectStorageId()).getStream());
                                IOUtils.write(bytess, zout);
                            }
                            zout.closeEntry();
                            ids.add(id);
                        }
                    } catch (BusinessException e) {
                        if (count == 1) {
                            throw e;
                        }
                    } catch (Exception e) {
                        logger.error("报告下载失败 e :",e);
//                        throw new BusinessException("报告下载失败", e);
                    }
                }
            });
            if (ids.isEmpty()) {
                throw new BusinessException("报告下载失败");
            }
            zout.close();
            return new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", reportName);
        }
    }
    private byte[] getPDF(byte[] docByte) throws Exception {
      return   PDFConversionUtil.getPDF(new ByteArrayInputStream(docByte),PDFConversion.Type.doc);
    }
    public String generateMessageTip(Map<String, Boolean> woOrders) {
        StringBuilder messageTipBuilder = new StringBuilder();
        for (Map.Entry<String, Boolean> entry : woOrders.entrySet()) {
            if (!entry.getValue()) {
                continue; // 跳过未选中的工单
            }
            String id = entry.getKey();
            V2_PmOrder v2PmOrder = pmOrderService.findById(id);
            if (v2PmOrder == null  ) {
                continue; // 跳过无效工单或服务请求
            }
            List<V2_BlobObject> blobObjects = blobObjectRepository.findByBoUuid(v2PmOrder.getId());
            // 过滤掉包含 "-工单报告.pdf" 的对象
            List<V2_BlobObject> filteredBlobObjects = blobObjects != null
                    ? blobObjects.stream()
                    .filter(item -> item.getObjectName().contains("-工单报告.pdf")).collect(Collectors.toList())
                    : Collections.emptyList();
            // 如果过滤后列表为空，则添加 SR 编号到提示信息中
            if (org.apache.commons.collections.CollectionUtils.isEmpty(filteredBlobObjects)) {
                messageTipBuilder.append(v2PmOrder.getPmNum()).append(",");
            }
        }
        // 去掉最后多余的逗号并补充提示信息
        if (messageTipBuilder.length() > 0) {
            messageTipBuilder.deleteCharAt(messageTipBuilder.length() - 1); // 删除最后一个逗号
            messageTipBuilder.append("不存在工单报告！");
        }
        messageTipBuilder.append("请确认是否需要下载？");
        return messageTipBuilder.toString();
    }

    public boolean enableDisplayByKey(String key){
        UserAccount user = UserContextService.getCurrentUserAccount();
        String value = WebUtil.getBean(ConfigItemValueRepository.class).findByKeyAndSiteForUseParent(user.getSiteUID(), user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID(), key);
        return Boolean.TRUE.toString().equalsIgnoreCase(value);
    }

    public DefaultStreamedContent generateEhome(Map<String, Boolean> woOrders, UserAccount userAccount ) throws IOException {
        if (!woOrders.values().contains(true)) {
            throw new BusinessException("未选中工单记录");
        }
        String reportName = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "工单报告.zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(bout)) {
            woOrders.forEach((id, selected) -> {
                if (selected) {
                    try {
                        V2_PmOrder v2PmOrder = pmOrderService.findById(id);
                        List<V2_BlobObject> byBoUuid;
                        if (enableDisplayByKey("mvsPmReportOnlyDownloadPmItems")) {
                            byBoUuid = blobObjectRepository.fetchBlobObjectNotType(v2PmOrder.getId(), Arrays.asList(new String[]{"SignatureEHome"}).stream().collect(Collectors.toSet()));
                            // 按照名称在过滤一边 标准保养报告、巡检报告、质控报告
                            byBoUuid = byBoUuid.stream()
                                    .filter(item -> item.getObjectName().contains("-标准保养报告") ||
                                            item.getObjectName().contains("-巡检报告") ||
                                            item.getObjectName().contains("-质控报告"))
                                    .collect(Collectors.toList());
                        } else {
                            byBoUuid = blobObjectRepository.findByBoUuid(v2PmOrder.getId());
                        }
//                        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(byBoUuid)){
//                            byBoUuid = byBoUuid.stream()
//                                    .filter(item -> item.getObjectName().contains("-工单报告.pdf"))
//                                    .collect(Collectors.toList());
//                        }
                        java.util.Set<String> addedEntryNames = new java.util.HashSet<>();
                        for (V2_BlobObject v2BlobObject : byBoUuid) {
                            String vname = v2PmOrder.getPmNum() + "-" + v2BlobObject.getObjectName();
                            // 防止同名条目导致 ZipException: duplicate entry
                            if (!addedEntryNames.add(vname)) {
                                // 如果名称已存在，追加序号去重
                                int seq = 1;
                                while (addedEntryNames.contains(vname + "(" + seq + ")")) {
                                    seq++;
                                }
                                vname = vname + "(" + seq + ")";
                                addedEntryNames.add(vname);
                            }
                            zout.putNextEntry(new ZipEntry(vname));
                            byte[] bytess  = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(v2BlobObject.getObjectStorageId()).getStream());
                            IOUtils.write(bytess, zout);
                        }
                        zout.closeEntry();
                    } catch (Exception e) {
                        logger.error("报告下载失败 e :",e);
                    }
                }
            });
            zout.close();
            return new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", reportName);
        }
    }

    /**
     * 全部下载：将筛选条件下的前maxCount条工单附件打包成ZIP
     */
    public DefaultStreamedContent generateEhomeAll(List<V2_PmOrder> pmOrders, UserAccount userAccount) throws IOException {
        if (pmOrders == null || pmOrders.isEmpty()) {
            throw new BusinessException("未查询到工单记录");
        }
        String reportName = DateFormatUtils.format(new Date(), "yyyy-MM-dd") + "工单报告_全部.zip";
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             ZipOutputStream zout = new ZipOutputStream(bout)) {
            for (V2_PmOrder v2PmOrder : pmOrders) {
                try {
                    List<V2_BlobObject> byBoUuid;
                    if (enableDisplayByKey("mvsPmReportOnlyDownloadPmItems")) {
                        byBoUuid = blobObjectRepository.fetchBlobObjectNotType(v2PmOrder.getId(), Arrays.asList(new String[]{"PmOrderEHome", "SignatureEHome"}).stream().collect(Collectors.toSet()));
                    } else {
                        byBoUuid = blobObjectRepository.findByBoUuid(v2PmOrder.getId());
                    }
                    java.util.Set<String> addedEntryNames = new java.util.HashSet<>();
                    for (V2_BlobObject v2BlobObject : byBoUuid) {
                        String vname = v2PmOrder.getPmNum() + "-" + v2BlobObject.getObjectName();
                        if (!addedEntryNames.add(vname)) {
                            int seq = 1;
                            while (addedEntryNames.contains(vname + "(" + seq + ")")) {
                                seq++;
                            }
                            vname = vname + "(" + seq + ")";
                            addedEntryNames.add(vname);
                        }
                        zout.putNextEntry(new ZipEntry(vname));
                        byte[] bytess = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(v2BlobObject.getObjectStorageId()).getStream());
                        IOUtils.write(bytess, zout);
                    }
                    zout.closeEntry();
                } catch (Exception e) {
                    logger.error("全部下载报告下载失败, 工单ID: " + v2PmOrder.getId() + " e :", e);
                }
            }
            zout.close();
            return new DefaultStreamedContent(new ByteArrayInputStream(bout.toByteArray()), "application/zip", reportName);
        }
    }

    public byte[] generate(V2_PmOrder pmOrder, byte[] activeTemplateBytes) throws IOException {
        PmOrderRule rule = this.pmOrderService.acqPmOrderRule(pmOrder);
        List<UnstructuredDataCollection> reportList = pmOrderService.getReportList(pmOrder.getId());
        List<V2_WorkOrderCoworker> coWorkerList = getCoWorkerList(pmOrderService.getWorkOrderCoworker(pmOrder.getId()));
        List<V2_WorkOrder_Detail> accessoryList = getAccessoryList(pmOrderService.getWorkOrderDetails(pmOrder.getId()));
        List<PmOrderSteps> pmOrderSteps = pmOrderService.getPmOrderStep0(pmOrder.getId());

         List<PmOrderRuleCheckList> pmCheckItemList = null;
        Map<String, UnstructuredData> hmCheck = null;
        UnstructuredDataCollection unsD = dataCollectionRepository.findCKByUId(pmOrder.getId());
        List<UnstructuredData> dataList = Optional.ofNullable(unsD).map(UnstructuredDataCollection::getId).map(unstructuredDataDao::findByCollectionId).orElse(ListUtil.empty());
        if(rule != null){
            pmCheckItemList = pmOrderService.getPmOrderRuleCheckLists(pmOrder, rule);
            hmCheck = pmOrderService.fetchHmCheck(pmCheckItemList, pmOrder);
        }

        String safetySchema = reportList.stream().filter(item -> Objects.equals(item.getBoType(), "pm-safety-report"))
                .findAny().map(UnstructuredDataCollection::getSchemaId).orElse(null);
        String performanceSchema = reportList.stream().filter(item -> Objects.equals(item.getBoType(), "pm-performance-report"))
                .findAny().map(UnstructuredDataCollection::getSchemaId).orElse(null);
        InputStream rawTemplate;

        if (rule != null && !Strings.isNullOrEmpty(rule.getReportStorageId())) {
            // get template if the report storage is set.
            StreamedContent template = this.blobService.getBlobObjectByStorageId(rule.getReportStorageId());
            rawTemplate = template.getStream();
            //rawTemplate = this.pmOrderReportTemplateService.updateReportTemplateBySchemaId(blobInputStream, safetySchema, performanceSchema);
        } else {
            // generate template otherwise.
            try {
                rawTemplate = this.pmOrderReportTemplateService.generateReportTemplateBySchemaId(activeTemplateBytes, safetySchema, performanceSchema);
            } catch (IOException e) {
                throw new BusinessException("自动模版生成失败");
            }
        }

        if (rawTemplate == null) throw new BusinessException("请先上传保养报告模版!");

        Map<String, Object> datas = populateReportContent(pmOrder, reportList, hmCheck, coWorkerList, accessoryList, pmOrderSteps, pmCheckItemList, dataList);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        XWPFTemplate template = XWPFTemplate.compile(rawTemplate);
        // render policy for accessory information.
//        template.registerPolicy(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(), new AccessoryTableRenderPolicy());
//        // render policy for maintenance workflow.
//        template.registerPolicy(MaintenanceTemplate.Component.MAINTENANCE_WORKFLOW.toString(), new MaintenanceTableRenderPolicy());
//        // render policy for check list
//        template.registerPolicy(MaintenanceTemplate.Component.CHECK_LIST.toString(), new CheckListTableRenderPolicy());
//        // render policy for audit record
//        template.registerPolicy(MaintenanceTemplate.Component.AUDIT_RECORDS.toString(), new AuditMsgTableRenderPolicy());
//        template.registerPolicy(MaintenanceTemplate.Component.PM_PRE_PICTURE_TABLE.toString(), new PmPartsPictureTableRenderPolicy(1));
//        template.registerPolicy(MaintenanceTemplate.Component.PM_AFTER_PICTURE_TABLE.toString(), new PmPartsPictureTableRenderPolicy(1));
//


//        Configure config = Configure.newBuilder()
//                .bind(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(), new AccessoryTableRenderPolicy())
//                .bind(MaintenanceTemplate.Component.MAINTENANCE_WORKFLOW.toString(), new MaintenanceTableRenderPolicy())
//                .bind(MaintenanceTemplate.Component.CHECK_LIST.toString(), new CheckListTableRenderPolicy())
//                .bind(MaintenanceTemplate.Component.AUDIT_RECORDS.toString(), new AuditMsgTableRenderPolicy())
//                .bind(MaintenanceTemplate.Component.PM_PRE_PICTURE_TABLE.toString(),new PmPartsPictureTableRenderPolicy(1))
//                .bind(MaintenanceTemplate.Component.PM_AFTER_PICTURE_TABLE.toString(),  new PmPartsPictureTableRenderPolicy(1))
//                .build();
//        XWPFTemplate template = XWPFTemplate.compile(rawTemplate, config);
//        template.render(datas);
//        template.write(out);
//        return out.toByteArray();

        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(), new AccessoryTableRenderPolicy());
        bindings.put(MaintenanceTemplate.Component.MAINTENANCE_WORKFLOW.toString(), new MaintenanceTableRenderPolicy());
        bindings.put(MaintenanceTemplate.Component.COWORKER_HOURS_TABLE.toString(), new CoworkerHoursRenderPolicy());
        bindings.put(MaintenanceTemplate.Component.MAN_HOURS_DETAIL.toString(), new ManHoursRenderPolicy());
        bindings.put(MaintenanceTemplate.Component.CHECK_LIST.toString(), new CheckListTableRenderPolicy());
        bindings.put(MaintenanceTemplate.Component.AUDIT_RECORDS.toString(), new AuditMsgTableRenderPolicy());
        bindings.put(MaintenanceTemplate.Component.PM_PRE_PICTURE_TABLE.toString(),new PmPartsPictureTableRenderPolicy(1));
        bindings.put(MaintenanceTemplate.Component.PM_AFTER_PICTURE_TABLE.toString(),  new PmPartsPictureTableRenderPolicy(1));

        Map<Character, RenderPolicy> plugins = new HashMap<>();
        plugins.put('&',new DynamicColumnTableRenderPolicy(datas));
        return  WordExportServiceUtil.generate(datas, IOUtils.toByteArray(rawTemplate), bindings,plugins);
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

package com.ge.apm.service;

import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.dao.ExportRepository;
import com.ge.apm.dao.mapper.ExportMapper;
import com.ge.apm.domain.BoTypeRelation;
import com.ge.apm.domain.ExportRecord;
import com.ge.apm.domain.RelationTenantUid;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.utils.FileUtils;
import com.ge.apm.service.utils.MicroServiceUtil;
import com.ge.apm.service.utils.ZipUtil;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.web.WebUtil;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 503310146
 */
@Service
public class ExportService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExportService.class);

    @Autowired
    private ExportMapper exportMapper;
    @Autowired
    private ExportRepository dao;
    @Autowired
    private MicroServiceUtil microService;
    @Autowired
    private BlobObjectRepository blobDao;

    @Value("#{systemEnvironment['BATCH_FILE_FOLDER'] ?: urlProperties.BatchFileFolder}")
    private String bathFileUpload;

    List<String> tableNameList = Arrays.asList("acc_org_info", "adverse_events", "arrival_list", "asset_contract", "asset_history", "asset_info", "asset_lifecycle", "asset_opt_record", "asset_purchase_list", "asset_seria_num_config", "asset_tag", "asset_termination_report_template", "attribute",
            "biomed_group", "brand", "click_rate", "contract_info", "custom_config", "custom_role", "dept_transaction_category", "dept_transaction_log", "electric_safety_data",
            "inspection_checklist", "inspection_order", "inspection_order_detail", "inventory_check_list", "inventory_issue", "inventory_issue_detail", "inventory_order", "inventory_order_detail",
            "measure_order_rule", "measure_order_rule_check_list", "measure_order_rule_item", "measuring_certificate_detail", "measuring_order", "measuring_order_coworker", "measuring_order_detail",
            "measuring_order_rule", "measuring_order_rule_item", "measuring_type_config", "message_subscriber", "msg_tenant", "multi_level_approval", "multi_level_approval_config", "non_asset_info", "nurse_site_maintain",
            "nurse_site_maintain_check_item", "nurse_site_maintain_config", "nurse_site_maintain_config_item", "nurse_site_maintain_config_item_jn", "nurse_site_maintain_config_jn",
            "org_info", "owner_config", "pcc_examine_report", "pm_order_report_template", "pm_order_rule", "pm_order_rule_check_list", "pm_order_rule_item", "qr_code_lib", "qr_code_rule_config", "qr_code_template", "qr_code_template_element",
            "rel_measure_rule", "rel_rule_asset", "role_authority", "shared_asset_config", "shared_asset_request", "shared_asset_swap_index", "shared_asset_type",
            "simple_parts_category", "simple_parts_config", "simple_parts_inventory", "simple_parts_item", "simple_parts_request", "simple_parts_request_line_item", "simple_parts_transaction", "simple_parts_warehouse",
            "small_asset_performance", "small_asset_performance_config", "small_asset_performance_report", "supplier", "supplier_qr_code",
            "table_definition", "tenant_option_config", "user_account", "user_account_history", "user_behavior", "user_login_info", "user_schedule", "user_schedule_stage_config", "user_tags", "user_token_history", "v2_pm_order", "v2_service_request", "v2_wechat_message_log",
            "v2_work_order", "v2_work_order_coworker", "v2_work_order_detail", "v2_work_order_step", "v2_work_order_workflow_config", "v2_workflow_config", "v2_workflow_config_bak", "wechat_message_log",
            "work_order_export_template", "workflow_config", "workorder_workflow_config");

    public void queryData(List<ExportRecord> itemBatch, String token, String uid) {
        for (ExportRecord exportRecord : itemBatch) {
            try {
                // 获取导出记录租户 id
                String tenantUid = exportRecord.getTenantUid();

                // type -> 1
                // wechat_report : ERROR: column "tenant_uid" does not exist :
                Map<String, String> writeMap = new HashMap<>(200);
                for (String tableName : tableNameList) {
                    writeMap.put(tableName, JSON.toJSONString(exportMapper.findByTenantUid(tableName, tenantUid)));
                }
                // tenant_info 租户信息
                writeMap.put("tenant_info", JSON.toJSONString(exportMapper.findByField("tenant_info", "uid", tenantUid)));

                // type -> 2
                List<String> assetTableNameList = Stream.of("asset_lifecycle_event", "asset_service_member", "asset_tags", "external_work_order", "pm_order_pre_create", "site_to_site_allocation", "event_collection", "nurse_site_maintain_item").collect(Collectors.toList());
                for (String assetTableName : assetTableNameList) {
                    writeMap.put(assetTableName, JSON.toJSONString(exportMapper.findByAssetInfoTenantUid(assetTableName, tenantUid)));
                }

                // type -> 3
                for (RelationTenantUid relationTenantUid : RelationTenantUid.values()) {
                    writeMap.put(relationTenantUid.getTableA(), JSON.toJSONString(exportMapper.findByRelationTableTenantUid(relationTenantUid.getTableA(), relationTenantUid.getTableB(), relationTenantUid.getFieldA(), relationTenantUid.getFieldB(), tenantUid)));
                }

                // type -> 4
                writeMap.put("unstructured_data", JSON.toJSONString(exportMapper.findByParentRelationTableTenantUid("unstructured_data", "unstructured_data_collection", "collection_id", "id",
                        "bo_uuid", "v2_pm_order", "id", tenantUid)));

                // type -> 5
                writeMap.put("gb_code", JSON.toJSONString(exportMapper.findAll("gb_code")));
                writeMap.put("role_msg_rel", JSON.toJSONString(exportMapper.findAll("role_msg_rel")));
                writeMap.put("sys_auth_group", JSON.toJSONString(exportMapper.findAll("sys_auth_group")));
                writeMap.put("sys_authority", JSON.toJSONString(exportMapper.findAll("sys_authority")));
                writeMap.put("sys_role", JSON.toJSONString(exportMapper.findAll("sys_role")));

                Map<String, List<String>> tableBlobObjectMap = MapUtil.newHashMap();
                for (Map.Entry<String, String> entry : writeMap.entrySet()) {
                    JSONArray objects = JSON.parseArray(entry.getValue());
                    BoTypeRelation boTypeRelation = BoTypeRelation.getBoTypeRelation(entry.getKey());
                    if (!CollectionUtils.isEmpty(objects) && boTypeRelation != null) {
                        List<String> uuids = objects.stream().map(object -> JSON.parseObject(object.toString()).getString(boTypeRelation.getTableField())).collect(Collectors.toList());
                        List<String> objectStorageIds = exportMapper.fetchObjectIds(boTypeRelation.name(), boTypeRelation.getBoField(), uuids);
                        tableBlobObjectMap.put(entry.getKey(), objectStorageIds);
                    }
                }
                writeData(exportRecord, writeMap, tableBlobObjectMap, token, uid);
            } catch (Exception e) {
                exportRecord.setUpdateDate(new Date());
                exportRecord.setStatus(ExportRecord.Status.FAIL.name());
                dao.save(exportRecord);
                LOGGER.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    private void writeData(ExportRecord exportRecord, Map<String, String> writeMap, Map<String, List<String>> tableBlobObjectMap, String token, String uid) throws IOException {
        // 随机生成文件名
        String fileName = UUID.randomUUID().toString().replace("-", "");
        // 写入文件路径
        String suffix = File.separator;
        String bathTmpPath = new File(bathFileUpload).getPath() + suffix;
        String src = bathTmpPath + fileName + suffix;
        String dataSrc = src + "data" + suffix;
        String fileSrc = src + "file" + suffix;
        // 存放压缩包路径
        fileName = fileName + ".zip";
        String dest = bathTmpPath + fileName;
        // 写文件
        List<String> tableNames = new ArrayList<>(writeMap.keySet());
        for (String tableName : tableNames) {
            FileUtils.writeFile(dataSrc, tableName + ".txt", writeMap.get(tableName));
            List<String> objectStorageIds = tableBlobObjectMap.get(tableName);
            if (!CollectionUtils.isEmpty(objectStorageIds)) {
                String writeFilePath = fileSrc + ZipUtil.underlineToCamel(tableName, false) + suffix;
                File writeFile = new File(writeFilePath);
                writeFile.mkdirs();

                for (String objectStorageId : objectStorageIds) {
                        StreamedContent file = microService.downloadSingleFile(objectStorageId);
                        String name = file.getName();
                    try (FileOutputStream fileOutputStream = new FileOutputStream(writeFilePath + name.substring(1, name.length() - 1))) {
                        StreamUtils.copy(file.getStream(), fileOutputStream);
                        fileOutputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.error(e.getMessage(), e);
                    }
                }
            }
        }
        // 生成随机密码
        String randomPassword = ZipUtil.getRandomPassword(8);
        // 压缩
        ZipUtil.zip(src, dest, randomPassword);

        if (token == null || token.isEmpty()) {
            WebUtil.addErrorMessage("do not have permission");
        }
        Map<String, Object> fileObject = microService.uploadSingleFile(token, new File(dest));

        if (null != fileObject) {
            V2_BlobObject blobObject = new V2_BlobObject();
            blobObject.setObjectName((String) fileObject.get("objectName"));
            blobObject.setObjectSize(Long.parseLong(String.valueOf(fileObject.get("objectSize"))));
            blobObject.setObjectStorageId((String) fileObject.get("objectId"));
            blobObject.setObjectType((String) fileObject.get("objectType"));
            blobObject.setCreatedDate(new Date());
            blobObject.setCreatedBy(uid);
            blobObject.setBoId(Integer.parseInt(exportRecord.getId().toString()));
            blobObject.setBoType(V2_BlobObject.BO_TYPE_EXPORT_RECORD);
            blobObject.setObjectName(fileName);
            blobDao.save(blobObject);
        }

        // 修改导出记录表
        exportRecord.setStatus(ExportRecord.Status.FINISHED.name());
        exportRecord.setPassword(randomPassword);
        exportRecord.setUpdateDate(new Date());
        dao.save(exportRecord);

        // 删除文件夹
        FileUtils.deleteFolder(dataSrc);
        FileUtils.deleteFolder(dest);
    }

    public V2_BlobObject getBlob(Integer id) {
        return blobDao.findByBoIdAndBoType(id, V2_BlobObject.BO_TYPE_EXPORT_RECORD);
    }

}

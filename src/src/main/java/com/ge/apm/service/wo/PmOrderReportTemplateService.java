package com.ge.apm.service.wo;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.policy.RenderPolicy;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.dao.PmOrderReportTemplateRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.bo.MaintenanceTemplate;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.context.LocaleHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


@Component
public class PmOrderReportTemplateService {

    private static final Logger logger = Logger.getLogger(PmOrderReportTemplateService.class.getName());

    @Autowired
    private PmOrderReportTemplateRepository templateRepository;

    @Autowired
    private TableDefinitionRepository templateDao;

    @Autowired
    private BlobFileService blobFileService;

    @Autowired
    private SystemTemplateService systemTemplateService;


    public boolean isLocale(){
        Locale locale = LocaleHolder.getLocale();
        //System.out.println("locale  is "+locale);
        if(locale.getLanguage().equalsIgnoreCase("zh")){
            return true;
        } else {
            return false;
        }
    }

    public byte[] getActiveTemplateBytes(boolean isInsulate, UserAccount user) throws IOException {
        byte[] templateBytes = null;
        List<String> uids = new ArrayList<>();
        popUid(uids, user.getSiteUID());
        if (!isInsulate) {
            for (String id : Arrays.asList(user.getHospitalUID(), user.getInstitutionUID(), user.getTenantUID())) {
                popUid(uids, id);
            }
        }
        List<PmOrderReportTemplate> templates = templateRepository.findBySiteUIDList(uids);
        PmOrderReportTemplate template = getPrioritizedTemplate(templates, uids);

        if (template != null && StringUtils.isNotEmpty(template.getStorageId())) {
            templateBytes = IOUtils.toByteArray(blobFileService.getBlobObjectByStorageId(template.getStorageId()).getStream());
        }
        if (templateBytes == null) {
//            String fileName = null;
//            if (isLocale()){
//                fileName = "maintenance-template_zh.docx";
//            } else {
//                fileName = "maintenance-template_en.docx";
//
//            }
//            templateBytes = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("report/" + fileName ));

            templateBytes =  systemTemplateService.getTemplateFileByte(SystemTemplateEnum.PM_ORDER_REPORT, user);

        }


        return templateBytes;
    }

    public byte[] getSignatureBytes(String storageId) {
        byte[] signatureBytes = null;

        try {
            signatureBytes = IOUtils.toByteArray(blobFileService.getBlobObjectByStorageId(storageId).getStream());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error happens while get signature bytes", e);
        }

        return signatureBytes;
    }

    public byte[] getDefaultSignatureBytes() {
        byte[] data = null;
        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/image/dot.png")) {
            data = IOUtils.toByteArray(inputStream);
        } catch (IOException ignore) {
            // ignore
        }
        return data;
    }

    private PmOrderReportTemplate getPrioritizedTemplate(List<PmOrderReportTemplate> templates, List<String> uids) {
        for (String uid : uids) {
            Optional<PmOrderReportTemplate> op = templates.stream().filter(i -> StringUtils.equals(i.getSiteUID(), uid)).findFirst();
            if (op.isPresent()) {
                return op.get();
            }
        }
        return null;
    }

    private void popUid(List<String> uids, String id) {
        if (StringUtils.isNotEmpty(id)) {
            uids.add(id);
        }
    }

    public boolean existsSiteTemplate(String siteUid) {
        List list = templateRepository.findBySiteUID(siteUid);
        return list != null && !list.isEmpty();
    }

    /**
     * Generate PmOrder report template by rule.
     *
     * @param stream {@link InputStream} object
     * @param rule {@link PmOrderRule} object
     * @return {@link InputStream} object
     * @throws IOException
     */
    public InputStream generateReportTemplateByRule(InputStream stream, PmOrderRule rule) throws IOException {
        MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();
        // add dynamic table.
        if (rule != null) {
            if(rule.getId() != null){
                TableSchema schema;
                schema = getTableSchemaById(rule.getSafetyReport());
                if (schema != null) {
                    builder.safetyReport(schema);
                }
                schema = getTableSchemaById(rule.getPerformanceReport());
                if (schema != null) {
                    builder.performanceReport(schema);
                }
            }
        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        XWPFTemplate template = XWPFTemplate.compile(stream).render(builder.build().data());
//        template.write(out);

        Map<String, RenderPolicy> bindings = new HashMap<>();
        byte[] generate = WordExportServiceUtil.generate(builder.build().data(), IOUtils.toByteArray(stream), bindings);
        return new ByteArrayInputStream(generate);
    }

    public InputStream generateMeasureReportTemplateByRule(InputStream stream, MeasureOrderRule rule) throws IOException {
        MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();
        // add dynamic table.
        if (rule != null) {
            if(rule.getId() != null){
                TableSchema schema;
                schema = getTableSchemaById(rule.getSafetyReport());
                if (schema != null) {
                    builder.safetyReport(schema);
                }
                schema = getTableSchemaById(rule.getPerformanceReport());
                if (schema != null) {
                    builder.performanceReport(schema);
                }
            }
        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        XWPFTemplate template = XWPFTemplate.compile(stream).render(builder.build().data());
//        template.write(out);

        Map<String, RenderPolicy> bindings = new HashMap<>();
        byte[] generate = WordExportServiceUtil.generate(builder.build().data(), IOUtils.toByteArray(stream), bindings);
        return new ByteArrayInputStream(generate);
    }

    public InputStream generateReportTemplateBySchemaId(byte[] activeTemplateBytes, String fReportId, String pReportId) throws IOException {
        return this.generateReportTemplateBySchemaId(new ByteArrayInputStream(activeTemplateBytes), fReportId, pReportId);
    }

    public InputStream generateReportTemplateBySchemaId(InputStream  stream, String fReportId, String pReportId) throws IOException {
        MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();
        // add dynamic table.
        TableSchema schema;
        if (fReportId != null) {
            TableDefinition td = templateDao.findById(fReportId);
            schema = JSONObject.parseObject(td.getSchema(), TableSchema.class);
            if (schema != null) {
                builder.safetyReport(schema);
            }
        }
        if (pReportId != null) {
            TableDefinition td = templateDao.findById(pReportId);
            schema = JSONObject.parseObject(td.getSchema(), TableSchema.class);
            if (schema != null) {
                builder.performanceReport(schema);
            }
        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        XWPFTemplate template = XWPFTemplate.compile(stream).render(builder.build().data());
//        template.write(out);
//        return new ByteArrayInputStream(out.toByteArray());
        Map<String, RenderPolicy> bindings = new HashMap<>();
        byte[] generate = WordExportServiceUtil.generate(builder.build().data(), IOUtils.toByteArray(stream), bindings);
        return new ByteArrayInputStream(generate);
    }

    public InputStream updateReportTemplateBySchemaId(InputStream  stream, String fReportId, String pReportId) throws IOException {
        MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();
        // add dynamic table.
        TableSchema schema;
        if (fReportId != null) {
            TableDefinition td = templateDao.findById(fReportId);
            schema = JSONObject.parseObject(td.getSchema(), TableSchema.class);
            if (schema != null) {
                builder.safetyReport(schema);
            }
        }
        if (pReportId != null) {
            TableDefinition td = templateDao.findById(pReportId);
            schema = JSONObject.parseObject(td.getSchema(), TableSchema.class);
            if (schema != null) {
                builder.performanceReport(schema);
            }
        }
        Map<String, Object> data = builder.build().data();
        data.put(MaintenanceTemplate.Component.ACCESSORY_INFORMATION.toString(),null);
        data.put(MaintenanceTemplate.Component.MAINTENANCE_WORKFLOW.toString(),null);
        data.put(MaintenanceTemplate.Component.COWORKER_HOURS_TABLE.toString(),null);
        data.put(MaintenanceTemplate.Component.AUDIT_RECORDS.toString(),null);
        data.put(MaintenanceTemplate.Component.CHECK_LIST.toString(),null);

//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        XWPFTemplate template = XWPFTemplate.compile(stream).render(data);
//        template.write(out);
//        return new ByteArrayInputStream(out.toByteArray());
        Map<String, RenderPolicy> bindings = new HashMap<>();
        byte[] generate = WordExportServiceUtil.generate(data, IOUtils.toByteArray(stream), bindings);
        return new ByteArrayInputStream(generate);
    }


    private TableSchema getTableSchemaById(String id) {
        TableSchema schema = null;
        if (!Strings.isNullOrEmpty(id)) {
            // use base Id to get the latest report.
            TableDefinition definition = templateDao.findByBaseId(id);
            if (definition != null && !Strings.isNullOrEmpty(definition.getSchema())) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    schema = objectMapper.readValue(definition.getSchema(), TableSchema.class);
                } catch (JsonParseException e) {
                    logger.log(Level.WARNING, "Error happens while parsing schema in table definition", e);
                } catch (JsonMappingException e) {
                    logger.log(Level.WARNING, "Wrong Json mapping", e);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Bad IO while parsing Json", e);
                }
            }
        }

        return schema;
    }
}

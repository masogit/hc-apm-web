package com.ge.apm.service.wo;

import com.deepoove.poi.XWPFTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.apm.dao.PmOrderReportTemplateRepository;
import com.ge.apm.dao.TableDefinitionRepository;
import com.ge.apm.domain.PmOrderReportTemplate;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.TableDefinition;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.bo.MaintenanceTemplate;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.service.asset.BlobFileService;
import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
            templateBytes = IOUtils.toByteArray(this.getClass().getClassLoader().getResourceAsStream("report/maintenance-template.docx"));
        }
        return templateBytes;
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
     * @param rule   {@link PmOrderRule} object
     * @return {@link InputStream} object
     * @throws IOException
     */
    public InputStream generateReportTemplateByRule(InputStream stream, PmOrderRule rule) throws IOException {
        MaintenanceTemplate.Builder builder = new MaintenanceTemplate.Builder();
        // add dynamic table.
        if (rule != null) {
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

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XWPFTemplate template = XWPFTemplate.compile(stream).render(builder.build().data());
        template.write(out);
        return new ByteArrayInputStream(out.toByteArray());
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

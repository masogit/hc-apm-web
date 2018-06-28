package com.ge.apm.service.wo;

import com.ge.apm.dao.WorkOrderExportTemplateRepository;
import com.ge.apm.domain.WorkOrderExportTemplate;
import com.ge.apm.service.asset.BlobFileService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 212688574
 */

@Component
public class WorkOrderExportTemplateService {

    @Autowired
    private WorkOrderExportTemplateRepository templateRepository;

    @Autowired
    private BlobFileService blobService;

    public byte[] getActiveTemplateBytes(String siteUID, String hospitalUID, String institutionUID, String tenantUID) throws IOException {
        byte[] templateBytes = null;
        List<String> uids = new ArrayList<>();
        populateUidList(uids, siteUID);
        populateUidList(uids, hospitalUID);
        populateUidList(uids, institutionUID);
        populateUidList(uids, tenantUID);

        List<WorkOrderExportTemplate> templates = templateRepository.findBySiteUIDList(uids);
        WorkOrderExportTemplate template = getPrioritizedTemplate(templates, uids);

        if (template != null && StringUtils.isNotEmpty(template.getStorageId())) {
            templateBytes = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(template.getStorageId()).getStream());
        }
        if (templateBytes == null) {
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExportTemplate/template.docx"));
        }
        return templateBytes;
    }

    private WorkOrderExportTemplate getPrioritizedTemplate(List<WorkOrderExportTemplate> templates, List<String> uids) {
        for (String uid : uids) {
            Optional<WorkOrderExportTemplate> optional = templates.stream().filter(i -> StringUtils.equals(i.getSiteUID(), uid)).findFirst();
            if (optional.isPresent()) {
                return optional.get();
            }
        }
        return null;
    }

    private void populateUidList(List<String> uids, String siteUID) {
        if (StringUtils.isNotEmpty(siteUID)) {
            uids.add(siteUID);
        }
    }

    public boolean existsSiteTemplate(String siteUid) {
        List list = templateRepository.findBySiteUID(siteUid);
        return list != null && !list.isEmpty();
    }
}

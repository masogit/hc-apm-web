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

/**
 * Created by 212688574
 */

@Component
public class WorkOrderExportTemplateService {

    @Autowired
    private WorkOrderExportTemplateRepository templateRepository;

    @Autowired
    private BlobFileService blobService;

    public byte[] getActiveTemplateBytes(String currentSiteUID) throws IOException {
        byte[] templateBytes = null;
        WorkOrderExportTemplate template = templateRepository.findTopBySiteUID(currentSiteUID);
        if (template != null && StringUtils.isNotEmpty(template.getStorageId())) {
            templateBytes = IOUtils.toByteArray(blobService.getBlobObjectByStorageId(template.getStorageId()).getStream());
        }
        if (templateBytes == null) {
            templateBytes = IOUtils.toByteArray(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/wo/woExportTemplate/template.docx"));
        }
        return templateBytes;
    }
}

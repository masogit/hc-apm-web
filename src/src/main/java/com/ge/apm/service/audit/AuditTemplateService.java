package com.ge.apm.service.audit;

import com.ge.apm.dao.AuditTemplateRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.AuditTemplate;
import com.ge.apm.domain.V2_BlobObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AuditTemplateService {

    @Autowired
    private BlobObjectRepository blobDao;

    @Autowired
    private AuditTemplateRepository auditTemplateRepository;

    @Transactional(rollbackFor = Exception.class)
    public void saveAuditTemplate(AuditTemplate auditTemplate, V2_BlobObject fileObj){
        auditTemplateRepository.save(auditTemplate);
        if(fileObj != null){
            fileObj.setBoUuid(auditTemplate.getUid());
            blobDao.save(fileObj);
        }
    }
}

package com.ge.apm.service.audit;

import com.ge.apm.dao.AuditCaseRepository;
import com.ge.apm.dao.AuditEntryRepository;
import com.ge.apm.dao.AuditSystemEntryRepository;
import com.ge.apm.domain.AuditCase;
import com.ge.apm.domain.AuditEntry;
import com.ge.apm.domain.AuditSystemEntry;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AuditUserCaseService {

    @Autowired
    private AuditCaseRepository auditCaseRepository;
    @Autowired
    private AuditEntryRepository auditEntryRepository;
    @Autowired
    private AuditSystemEntryRepository auditSystemEntryRepository;

    public void saveUserCase(AuditCase auditCase){
        // uid为空，说明是新增
        if(StringUtils.isEmpty(auditCase.getUid())){
            auditCase.setScore(0.0);
            auditCase.setStatus(1);
            auditCase.setProcess(0.0);
            auditCase.setCreator(UserContextService.getCurrentUserAccount().getName());
            auditCase.setCreateTime(new Date());
        }
        auditCase.setUpdater(UserContextService.getCurrentUserAccount().getName());
        auditCase.setUpdateTime(new Date());
        auditCaseRepository.save(auditCase);
        // 保存entry
        List<AuditSystemEntry> systemEntryList = auditSystemEntryRepository.findByCaseUid(auditCase.getSystemCaseUid());
        if(CollectionUtils.isNotEmpty(systemEntryList)){
            List<AuditEntry> auditEntryList = new ArrayList<>();
            for (AuditSystemEntry auditSystemEntry : systemEntryList) {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setCaseUid(auditCase.getUid());
                auditEntry.setSystemEntryUid(auditSystemEntry.getUid());
                auditEntry.setScore(0.0);
                auditEntry.setProcess(0.0);
                auditEntry.setCreator(UserContextService.getCurrentUserAccount().getName());
                auditEntry.setCreateTime(new Date());
                auditEntryList.add(auditEntry);
            }
            auditEntryRepository.save(auditEntryList);
        }
    }
}

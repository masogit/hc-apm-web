package com.ge.apm.service.audit;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private AuditEntryTemplateRelRepository auditEntryTemplateRelRepository;
    @Autowired
    private AuditTemplateRepository auditTemplateRepository;
    @Autowired
    private AuditUploadedFileRepository auditUploadedFileRepository;
    @Autowired
    private AuditResultRepository auditResultRepository;
    @Autowired
    private AuditSelfEvaluationRepository auditSelfEvaluationRepository;
    @Autowired
    private AuditSystemCaseRepository auditSystemCaseRepository;


    @Transactional(rollbackFor = Exception.class)
    public void saveUserCase(AuditCase auditCase){
        // uid为空，说明是新增
        if(StringUtils.isEmpty(auditCase.getUid())){
            // 判断，一个院区只能有一个质控/评审配置
            AuditSystemCase systemCase = auditSystemCaseRepository.findByUid(auditCase.getSystemCaseUid());

            int count = auditCaseRepository.preSaveJudge(auditCase.getSiteUid(), systemCase.getType());

            if(count > 0){
                throw new RuntimeException("该院区已有已激活的该类型配置，无法再次配置!");
            }

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

    @Transactional(rollbackFor = Exception.class)
    public void initialize(String caseUid){
        AuditCase auditCase = auditCaseRepository.findById(caseUid);
        // 重新初始化case
        auditCase.setScore(0.0);
        auditCase.setStatus(1);
        auditCase.setProcess(0.0);
        auditCase.setUpdater(UserContextService.getCurrentUserAccount().getName());
        auditCase.setUpdateTime(new Date());

        // 初始化entry
        List<AuditEntry> auditEntryList = auditEntryRepository.findByCaseUid(caseUid);

        if(CollectionUtils.isNotEmpty(auditEntryList)){
            for (AuditEntry auditEntry : auditEntryList) {
                auditEntry.setScore(0.0);
                auditEntry.setProcess(0.0);
                auditEntry.setOwner(null);
                auditEntry.setComments(null);
                auditEntryRepository.save(auditEntry);

                // 删除用户上传模板
                List<AuditEntryTemplateRel> templateRelList = auditEntryTemplateRelRepository.findByEntryUid(auditEntry.getUid());
                if(CollectionUtils.isNotEmpty(templateRelList)){
                    for (AuditEntryTemplateRel auditEntryTemplateRel : templateRelList) {
                        auditTemplateRepository.deleteUserTemplateByUid(auditEntryTemplateRel.getTemplateUid());
                    }
                }
                auditEntryTemplateRelRepository.delete(templateRelList);

                // 删除用户上传文件
                auditUploadedFileRepository.deleteByEntryUid(auditEntry.getUid());
            }
        }
        // 删除结果记录
        auditResultRepository.deleteByCaseUid(caseUid);
        // 删除用户自评
        auditSelfEvaluationRepository.deleteByCaseUid(caseUid);
    }
}

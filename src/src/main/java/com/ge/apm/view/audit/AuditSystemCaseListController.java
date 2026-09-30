package com.ge.apm.view.audit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 503189829
 * @create 2022/2/24
 * @since 1.0.0
 */
@ManagedBean
@ViewScoped
public class AuditSystemCaseListController extends GenericCRUDUUIDController<AuditSystemCase> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditSystemCaseListController.class);

    private AuditSystemCaseRepository dao;
    private AuditEventRepository auditEventRepository;
    private AuditSystemEntryRepository auditSystemEntryRepository;
    private AuditConfigItemRepository auditConfigItemRepository;
    private AuditTargetRepository auditTargetRepository;
    private AuditEntryTemplateRelRepository auditEntryTemplateRelRepository;
    private AuditEntryItemRelRepository auditEntryItemRelRepository;
    private AuditCaseRepository auditCaseRepository;
    private AuditEntryRepository auditEntryRepository;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AuditSystemCaseRepository.class);
        auditEventRepository = WebUtil.getBean(AuditEventRepository.class);
        auditSystemEntryRepository = WebUtil.getBean(AuditSystemEntryRepository.class);
        auditConfigItemRepository = WebUtil.getBean(AuditConfigItemRepository.class);
        auditTargetRepository = WebUtil.getBean(AuditTargetRepository.class);
        auditEntryTemplateRelRepository = WebUtil.getBean(AuditEntryTemplateRelRepository.class);
        auditEntryItemRelRepository = WebUtil.getBean(AuditEntryItemRelRepository.class);
        auditCaseRepository = WebUtil.getBean(AuditCaseRepository.class);
        auditEntryRepository = WebUtil.getBean(AuditEntryRepository.class);

    }

    @Override
    protected GenericRepositoryUUID<AuditSystemCase> getDAO() {
        return dao;
    }
    @Override
    protected Page<AuditSystemCase> loadData(PageRequest pageRequest) {
        Page<AuditSystemCase> searchFilter = dao.findBySearchFilter(searchFilters, pageRequest);
        if(searchFilter!=null){
            List<AuditSystemCase> list = searchFilter.getContent();
            if (list==null && list.isEmpty()){
                return searchFilter;
            }
            for(AuditSystemCase auditSystemCase:list){
                Integer stripNum=0;//条总数
                Integer paymentNum=0;//款总数
                Integer targetNum=0;//指标总数
                Double scoreNum=0D;//总分值
                List<AuditSystemEntry> auditSystemEntries = auditSystemEntryRepository.findByCaseUid(auditSystemCase.getUid());
                if (auditSystemEntries!=null && !auditSystemEntries.isEmpty()){
                    for (AuditSystemEntry auditSystemEntry : auditSystemEntries){
                        if (Objects.equals(auditSystemEntry.getType(),1)){
                            stripNum++;
                        }else if(Objects.equals(auditSystemEntry.getType(),2)){
                            BigDecimal score = new BigDecimal(scoreNum);
                            BigDecimal scoreStandard = new BigDecimal(auditSystemEntry.getScoreStandard()==null ? 0D :auditSystemEntry.getScoreStandard());
                            scoreNum=score.add(scoreStandard).doubleValue();
                            paymentNum++;
                        }else {
                            String parentUid = auditSystemEntry.getParentUid();
                            AuditSystemEntry data = auditSystemEntryRepository.findByUid(parentUid);
                            if(data!=null && Objects.equals(data.getType(),1)){
                                BigDecimal score = new BigDecimal(scoreNum);
                                BigDecimal scoreStandard = new BigDecimal(auditSystemEntry.getScoreStandard()==null ? 0D :auditSystemEntry.getScoreStandard());
                                scoreNum=score.add(scoreStandard).doubleValue();
                            }
                            targetNum++;
                        }
                    }
                }
                auditSystemCase.setStripNum(stripNum);
                auditSystemCase.setPaymentNum(paymentNum);
                auditSystemCase.setTargetNum(targetNum);
                auditSystemCase.setScoreNum(scoreNum);
            }
        }

        return searchFilter;
    }
    public void onCreateCopy(AuditSystemCase auditSystemCase) {
        if (selected == null) {
            selected = auditSystemCase;
        }
    }
    @Transactional
    public void createCopy() {
        if(selected==null){
            return;
        }
        UserAccount userAccount = UserContextService.getCurrentUserAccount();
        String accountName = userAccount.getName();
        AuditSystemCase AuditSystemCaseNew = new AuditSystemCase();
        BeanUtils.copyProperties(selected,AuditSystemCaseNew);
        AuditSystemCaseNew.setUid(null);
        AuditSystemCaseNew.setCreator(accountName);
        AuditSystemCaseNew.setCreateTime(new Date());
        try {
            AuditSystemCaseNew= dao.save(AuditSystemCaseNew);
            createEventSysytemCase("ADD_SYSTEM_CASE", AuditSystemCaseNew, "success");
        } catch (Exception e){
            createEventSysytemCase("ADD_SYSTEM_CASE", AuditSystemCaseNew, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
        }
        String caseUid = AuditSystemCaseNew.getUid();
        List<AuditTarget> auditTargetList = auditTargetRepository.findBySystemCaseUid(selected.getUid());
        if (auditTargetList!=null && !auditTargetList.isEmpty()){
            for (AuditTarget auditTarget :auditTargetList){
                auditTarget.setSystemCaseUid(caseUid);
                auditTarget.setUid(null);
                auditTargetRepository.save(auditTarget);
            }
        }

        List<AuditSystemEntry> auditSystemEntries = auditSystemEntryRepository.findByCaseUidAndParentUid(selected.getUid(),"1");
        setEntry(auditSystemEntries,caseUid,"1",accountName);
        String operation= "AuditSystemCaseEdit.xhtml?faces-redirect=true&uid=" + caseUid ;
        WebUtil.navigateTo(operation);
    }
    private void setEntry(List<AuditSystemEntry> auditSystemEntries,String caseUid,String parentUid,String accountName){
        if(auditSystemEntries!=null && !auditSystemEntries.isEmpty()){
            for (AuditSystemEntry auditSystemEntry : auditSystemEntries){
                List<AuditSystemEntry> systemEntries = auditSystemEntryRepository.findByParentUid(auditSystemEntry.getUid());
                AuditSystemEntry data = setData(auditSystemEntry, caseUid, parentUid, accountName);
                setEntry(systemEntries,caseUid,data.getUid(),accountName);
            }
        }
    }
    private AuditSystemEntry setData(AuditSystemEntry auditSystemEntry,String caseUid,String parentUid,String accountName){
        if (auditSystemEntry.getUid()==null){
            return null ;
        }
         List<AuditConfigItem> auditConfigItemList = auditConfigItemRepository.AuditConfigItemtList(auditSystemEntry.getUid());
        List<AuditTemplate> auditTemplateList = auditEntryTemplateRelRepository.auditTemplateList(auditSystemEntry.getUid());
        auditSystemEntry.setCaseUid(caseUid);
        auditSystemEntry.setUid(null);
        auditSystemEntry.setParentUid(parentUid);
        auditSystemEntry.setCreateTime(new Date());
        auditSystemEntry.setCreator(accountName);
        AuditSystemEntry systemEntry= new AuditSystemEntry();
        try {
            systemEntry = auditSystemEntryRepository.save(auditSystemEntry);
            createEventSysytemEntry("ADD_SYSTEM_ENTRY", systemEntry, "success");
        } catch (Exception e){
            createEventSysytemEntry("ADD_SYSTEM_ENTRY", systemEntry, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("save") + WebUtil.getMessage("FAILED"));
        }
        if (auditConfigItemList!=null && !auditConfigItemList.isEmpty()){
            for (AuditConfigItem auditConfigItem:auditConfigItemList){
                AuditEntryItemRel auditEntryItemRel = new AuditEntryItemRel();
                auditEntryItemRel.setItemUid(auditConfigItem.getUid());
                auditEntryItemRel.setSystemEntryUid(systemEntry.getUid());
                auditEntryItemRel.setOrderBy(systemEntry.getOrderBy());
                auditEntryItemRelRepository.save(auditEntryItemRel);
            }
        }
        if (auditTemplateList!=null && !auditTemplateList.isEmpty()){
            for (AuditTemplate auditTemplate:auditTemplateList){
                AuditEntryTemplateRel auditEntryTemplateRel = new AuditEntryTemplateRel();
                auditEntryTemplateRel.setEntryUid(systemEntry.getUid());
                auditEntryTemplateRel.setTemplateUid(auditTemplate.getUid());
                auditEntryTemplateRel.setOrderBy(3);
                auditEntryTemplateRelRepository.save(auditEntryTemplateRel);
            }
        }

        return systemEntry;
    }
    public void editAction() {
        String operation= "AuditSystemCaseEdit.xhtml?faces-redirect=true&uid=" + selected.getUid() ;
        WebUtil.navigateTo(operation);
    }
    public AuditSystemCase onSelectAuditSystemCase() {
        return selected;
    }
    public String getStatusName(int status){
        switch (status){
            case 1 : return "草稿";
            case 2 : return "发布";
            default: return "过期";
        }
    }

    public void deleteAction() {
        if(Objects.equals(selected.getStatus(),1)){
            try {
                String caseUid = selected.getUid();
                List<AuditSystemEntry> systemEntries = auditSystemEntryRepository.findByCaseUid(caseUid);
                if (systemEntries!=null && !systemEntries.isEmpty()){
                    for (AuditSystemEntry auditSystemEntry :systemEntries){
                        auditEntryTemplateRelRepository.deleteByEntryUid(auditSystemEntry.getUid());
                        auditEntryItemRelRepository.deleteBySystemEntryUid(auditSystemEntry.getUid());
                    }
                    auditSystemEntryRepository.deleteByCaseUid(caseUid);
                }
                auditTargetRepository.deleteBySystemCaseUid(caseUid);
                dao.delete(selected);
                createEventSysytemCase("DELETE_SYSTEM_CASE", selected, "success");
            } catch (Exception e){
                createEventSysytemCase("DELETE_SYSTEM_CASE", selected, "fail");
                WebUtil.addErrorMessage(WebUtil.getMessage("delete") + WebUtil.getMessage("FAILED"));
            }
        }else {
            if(Objects.equals(selected.getStatus(),2)){
                WebUtil.addErrorMessage("发布状态数据不能删除!");
            }else {
                WebUtil.addErrorMessage("过期状态数据不能删除!");
            }

            return ;
        }

    }
    public void listAction() {
        try {
            if(selected.getUid()!=null){
                selected.setStatus(3);
                dao.save(selected);
            }
            createEventSysytemCase("EXPIRE_SYSTEM_CASE", selected, "success");
        } catch (Exception e){
            createEventSysytemCase("EXPIRE_SYSTEM_CASE", selected, "fail");
            WebUtil.addErrorMessage(WebUtil.getMessage("expire") + WebUtil.getMessage("FAILED"));
        }
        this.selected=null;
    }
    private void createEventSysytemCase(String eventType, AuditSystemCase auditSystemCase, String result){
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setOperator(UserContextService.getCurrentUserAccount().getName());
        event.setContent(JSON.toJSONString(auditSystemCase));
        event.setEventObjectUid(auditSystemCase.getUid());
        event.setResult(result);
        event.setEventTime(new Date());
        auditEventRepository.save(event);
    }
    private void createEventSysytemEntry(String eventType, AuditSystemEntry data, String result){
        AuditEvent event = new AuditEvent();
        event.setEventType(eventType);
        event.setOperator(UserContextService.getCurrentUserAccount().getName());
        event.setContent(JSON.toJSONString(data));
        event.setEventObjectUid(data.getUid());
        event.setResult(result);
        event.setEventTime(new Date());
        auditEventRepository.save(event);
    }

    public void initializeNewNode(AuditSystemCase auditSystemCase){
        LOGGER.info("Initialize new node, start：{}", auditSystemCase);
        String systemCaseUid = auditSystemCase.getUid();
        if(StringUtils.isEmpty(systemCaseUid)) return ;
        List<AuditCase> auditCaseList = auditCaseRepository.findBySystemCaseUid(systemCaseUid);
        LOGGER.info("Initialize new node, auditCaseList：{}", JSONObject.toJSONString(auditCaseList));
        if(CollectionUtils.isEmpty(auditCaseList)) return ;
        List<AuditSystemEntry> systemEntryList = auditSystemEntryRepository.entryList(systemCaseUid);
        LOGGER.info("Initialize new node, systemEntryList：{}", JSONObject.toJSONString(systemEntryList));
        if(CollectionUtils.isEmpty(systemEntryList)) return ;
        Set<String> collect = auditCaseList.stream().map(AuditCase::getUid).collect(Collectors.toSet());
        LOGGER.info("Initialize new node, auditCaseCollect：{}", JSONObject.toJSONString(collect));
        List<AuditEntry> auditEventList=auditEntryRepository.findByCaseUidList(collect);
        LOGGER.info("Initialize new node, auditEventList：{}", JSONObject.toJSONString(auditEventList));
        if(CollectionUtils.isEmpty(auditEventList)) return ;
        Map<String, List<AuditEntry>> auditEntryMap = auditEventList.stream().collect(Collectors.groupingBy(item -> item.getCaseUid()));
        for (Map.Entry<String, List<AuditEntry>> stringListEntry : auditEntryMap.entrySet()) {
            String key = stringListEntry.getKey();
            List<AuditEntry> entryValue = stringListEntry.getValue();
            saveAuditEntry(key,entryValue,systemEntryList);
        }

    }

    private void saveAuditEntry(String caseUid,List<AuditEntry> auditEntryList,List<AuditSystemEntry> systemEntryList){
        String createName = UserContextService.getCurrentUserAccount().getName();
        Map<String, List<AuditEntry>> collect = auditEntryList.stream().collect(Collectors.groupingBy(AuditEntry::getSystemEntryUid));
        if(collect == null || collect.isEmpty()) return;
        List<AuditEntry> saveList=new ArrayList<>();
        for (AuditSystemEntry auditSystemEntry : systemEntryList) {
            String uid = auditSystemEntry.getUid();
            if(collect.containsKey(uid)){
                continue;
            }else{
                saveList.add(createAuditEntry(caseUid, auditSystemEntry, createName));
            }
        }
        if(CollectionUtils.isNotEmpty(saveList)){
            auditEntryRepository.save(saveList);
        }
        LOGGER.info("Initialize new node data：{}", JSONObject.toJSONString(saveList));
    }

    private AuditEntry createAuditEntry(String caseUid,AuditSystemEntry auditSystemEntry,String createName){
        AuditEntry auditEntry = new AuditEntry();
        auditEntry.setCaseUid(caseUid);
        auditEntry.setSystemEntryUid(auditSystemEntry.getUid());
        auditEntry.setScore(0.0);
        auditEntry.setProcess(0.0);
        auditEntry.setCreator(createName);
        auditEntry.setCreateTime(new Date());
        return auditEntry;
    }
}

package com.ge.apm.view.audit;

import com.ge.apm.dao.AuditSystemCaseRepository;
import com.ge.apm.dao.AuditSystemEntryRepository;
import com.ge.apm.domain.AuditSystemCase;
import com.ge.apm.domain.AuditSystemEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 503189829
 * @create 2022/2/24
 * @since 1.0.0
 */
@ManagedBean
@ViewScoped
public class AuditSystemCaseListController extends GenericCRUDUUIDController<AuditSystemCase> {

    private AuditSystemCaseRepository dao;
    private AuditSystemEntryRepository auditSystemEntryRepository;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AuditSystemCaseRepository.class);
        auditSystemEntryRepository = WebUtil.getBean(AuditSystemEntryRepository.class);

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
                        if (auditSystemEntry.getType()==1){
                            stripNum++;
                        }else if(auditSystemEntry.getType()==2){
                            BigDecimal score = new BigDecimal(scoreNum);
                            BigDecimal scoreStandard = new BigDecimal(auditSystemEntry.getScoreStandard()==null ? 0D :auditSystemEntry.getScoreStandard());
                            scoreNum=score.add(scoreStandard).doubleValue();
                            paymentNum++;
                        }else {
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
        if(selected.getUid()==null){
            WebUtil.addErrorMessage("请选择数据!");
            return ;
        }
        dao.delete(selected);
    }
    public void listAction(AuditSystemCase auditSystemCase) {
        if(auditSystemCase.getUid()!=null){
            auditSystemCase.setStatus(3);
            dao.save(auditSystemCase);
        }
    }

}

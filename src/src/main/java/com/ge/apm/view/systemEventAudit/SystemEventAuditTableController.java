package com.ge.apm.view.systemEventAudit;

import com.ge.apm.dao.SystemEventAuditRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.SystemEventAudit;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.AuditBoTypeEnum;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SystemEventAuditTableController extends GenericCRUDUUIDController<SystemEventAudit> {

    private SystemEventAuditRepository systemEventAuditRepository;
    private UserAccountRepository userAccountRepository;
    private UaaService uaaService;
    private UserAccount loginUser;

    @Override
    protected void init() {
        super.init();
        systemEventAuditRepository = WebUtil.getBean(SystemEventAuditRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        loginUser = UserContextService.getCurrentUserAccount();

    }

    @Override
    protected Page<SystemEventAudit> loadData(PageRequest pageRequest) {
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("boType", SearchFilter.Operator.EQ, AuditBoTypeEnum.TABLE_DOWNLOAD.getBoType()));
        searchFilters.removeAll(searchFilters.stream().filter(item -> "siteUID".equals(item.fieldName)).collect(Collectors.toList()));
        if (!this.hasFilterOnField("siteUid")) {
            List<String> orgUidList = uaaService.getOrgListByUser(loginUser.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
            updateSearchFilter(new SearchFilter("siteUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("institutionUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
            updateSearchFilter(new SearchFilter("hospitalUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        }
        if(this.hasFilterOnField("operatorUid")){
            String name = searchFilters.stream().filter(sf -> "operatorUid".equals(sf.fieldName)).findFirst().orElse(new SearchFilter("operatorUid", SearchFilter.Operator.EQ, "0")).value.toString();
            List<UserAccount> list = userAccountRepository.findByTenantUIDAndNameList(loginUser.getTenantUID(), name);
            if(CollectionUtils.isNotEmpty(list)){
                searchFilters.add(new SearchFilter("operatorUid", SearchFilter.Operator.IN, list.stream().filter(item -> item.getUid().equals(loginUser.getUid())).collect(Collectors.toList()), "1*"));
            }else{
                //就默认查不到数据
                searchFilters.add(new SearchFilter("operatorUid", SearchFilter.Operator.EQ, "0"));
            }

        }
        return systemEventAuditRepository.findBySearchFilter(searchFilters, pageRequest);
    }

    public String getUserName(String uid) {
        UserAccount userAccount = userAccountRepository.findByUid(uid);
        if(userAccount == null){
            return "";
        }
        return userAccount.getName();
    }

    public String getEventDataName(String eventData){
        return SystemEventAuditEnum.getNameByCode(eventData);
    }
    @Override
    protected GenericRepositoryUUID<SystemEventAudit> getDAO() {
        return systemEventAuditRepository;
    }
}

package com.ge.apm.view;

import com.ge.apm.dao.VenderCompanyRepository;
import com.ge.apm.dao.VenderNoticeRepository;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class VenderNoticeController extends GenericCRUDUUIDController<VenderNotice> {

    private VenderNoticeRepository dao;
    private VenderCompanyRepository venderCompanyRepository;

    @Getter @Setter
    private UserAccount loginUser;
    @Getter @Setter
    private VenderCompany venderCompany;
    @Getter @Setter
    private List<VenderCompany> venderCompanies;
    @Getter @Setter
    private Map<String, String> companyMap;
    @Override
    protected void init() {
        dao = WebUtil.getBean(VenderNoticeRepository.class);
        venderCompanyRepository = WebUtil.getBean(VenderCompanyRepository.class);
        venderCompanies = venderCompanyRepository.find();
        companyMap = venderCompanies.stream().collect(Collectors.toMap(VenderCompany::getId, VenderCompany::getName));
        loginUser = UserContextService.getCurrentUserAccount();
        this.filterBySite = false;
    }

    @Override
    protected VenderNoticeRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<VenderNotice> loadData(PageRequest pageRequest) {
        if(this.searchFilters == null){
            searchFilters =  new ArrayList<>();
        }
        if (this.hasFilterOnField("venderCompanyId")) {
            String value = searchFilters.stream().filter(sf -> "venderCompanyId".equals(sf.fieldName)).findFirst().get().value.toString();
            List<String> venderCompanyIds = ListUtil.newArrayList("-1");
            for (Map.Entry<String, String> entry : companyMap.entrySet()) {
                if (entry.getValue().contains(value)) {
                    venderCompanyIds.add(entry.getKey());
                }
            }
            this.removeFilterOnField("venderCompanyId");
            searchFilters.add(new SearchFilter("venderCompanyId", SearchFilter.Operator.IN, venderCompanyIds));
        }
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), new Sort(Sort.Direction.DESC,"lastModifiedDate"));
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
    }

    public void saveSubmit(){
        Date now = DateUtil.now();
        this.selected.setStatus(0);
        this.selected.setTenantUid(loginUser.getTenantUID());
        this.selected.setNoticeDate(now);
        this.selected.setNoticeUser(loginUser.getName());
        this.selected.setLastModifiedBy(loginUser.getName());
        this.selected.setLastModifiedDate(now);
        dao.save(this.selected);
    }

    public List<VenderCompany> autoCompleteVenderCompany(String query) {
        return venderCompanies.stream().filter(t -> ObjectUtil.isEmpty(query) || t.getName().contains(query)).collect(Collectors.toList());
    }

    public void onSelectedObjectChange() {
        System.out.println("111");
        this.selected.setVenderCompanyId(venderCompany.getId());
    }

}
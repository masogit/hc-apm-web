package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.AssetSeriaNumConfigRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.AssetSeriaNumConfig;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class AssetSeriaNumConfigController extends JpaCRUDController<AssetSeriaNumConfig> {
	private static final long serialVersionUID = -1L;
	private AssetSeriaNumConfigRepository dao = null;
	private UserAccount user;
	private List<OrgInfo> institutionList;
	private OrgInfoRepository orgInfoRepository;
	private TenantInfoRepository tenantInfoRepository;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetSeriaNumConfigRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        user = UserContextService.getCurrentUserAccount();
        institutionList = orgInfoRepository.findByTenantUIDAndOrgType(user.getTenantUID(),1);
    }

    @Override
    protected AssetSeriaNumConfigRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<AssetSeriaNumConfig> loadData(PageRequest pageRequest) {
    	 List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
         searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
         return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public List<AssetSeriaNumConfig> getItemList() {
        return dao.find();
    }
    
	public String getTenantName(String tenantUID){
		return tenantInfoRepository.findByuid(tenantUID).getName();
	}
	
	public String getOrgNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		return oi.getName();
	}
	
	public List<OrgInfo> getIncludeOrgInfo(String parentUID,Integer orgType){
		return orgInfoRepository.findByParentUIDAndOrgType(parentUID,orgType);
	}

	public List<OrgInfo> getInstitutionList() {
		return institutionList;
	}

	public void setInstitutionList(List<OrgInfo> institutionList) {
		this.institutionList = institutionList;
	}
	
	@Transactional
	@Override
	public void onBeforeSave(AssetSeriaNumConfig config) {
		selected.setTenantUID(user.getTenantUID());
		if(selected.getInstitutionUID() == null){//租户级别
			selected.setInstitutionUID(selected.getTenantUID());
			selected.setHospitalUID(selected.getTenantUID());
			selected.setSiteUID(selected.getTenantUID());
		}
		if(selected.getHospitalUID() == null){//集团级别
			selected.setHospitalUID(selected.getInstitutionUID());
			selected.setSiteUID(selected.getInstitutionUID());
		}
		if(selected.getSiteUID() == null){//医院级别
			selected.setSiteUID(selected.getHospitalUID());
		}
		dao.deleteBySiteUID(selected.getSiteUID());
	}
	
	@Override
	public void save(){
		onBeforeSave(selected);
		selected.setTenantUID(user.getTenantUID());
		selected.setCreatedBy(user.getName());
		selected.setLastModifiedBy(user.getName());
		super.save();
	}
	
	
}
package com.ge.apm.view.sysutil;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.service.common.UserCommonService;
import org.primefaces.context.RequestContext;
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
	private UserCommonService userCommonService;

    @Override
    protected void init() {
        dao = WebUtil.getBean(AssetSeriaNumConfigRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
		userCommonService = WebUtil.getBean(UserCommonService.class);
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
		return orgInfoRepository.findByParentUIDAndOrgTypeOrderByNameAsc(parentUID,orgType);
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
			selected.setInstitutionUID(user.getTenantUID());
			selected.setHospitalUID(user.getTenantUID());
			selected.setSiteUID(user.getTenantUID());
			selected.setLevel(4);
//			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//   		 	WebUtil.addErrorMessage(WebUtil.getMessage("NeedInstitution"));
//   		 	return;
		}
		if(selected.getHospitalUID() == null){//集团级别
			selected.setInstitutionUID(selected.getInstitutionUID());
			selected.setHospitalUID(selected.getInstitutionUID());
			selected.setSiteUID(selected.getInstitutionUID());
			selected.setLevel(3);
//			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//   		 	WebUtil.addErrorMessage(WebUtil.getMessage("NeedHospital"));
//   		 	return;
		}
		if(selected.getSiteUID() == null){//医院级别
			selected.setHospitalUID(selected.getHospitalUID());
			selected.setSiteUID(selected.getHospitalUID());
			selected.setLevel(2);
//			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//   		 	WebUtil.addErrorMessage(WebUtil.getMessage("NeedSite"));
//   		 	return;
		}
	}
	
	@Override
	public void save(){
		//判断是否有权限(只能操作当前登陆用户级别以下的和自己相同siteuid的数据)
		if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected.getSiteUID())){
			return;
		}

		selected.setTenantUID(user.getTenantUID());
		selected.setCreatedBy(user.getName());
		selected.setLastModifiedBy(user.getName());
		dao.deleteBySiteUID(selected.getSiteUID());
		super.save();
	}
	
	@Transactional
	public void applyChange(){
		onBeforeSave(selected);
		save();
	}

	@Override
	public void delete() {
		//判断是否有权限(只能操作当前登陆用户级别以下的和自己相同siteuid的数据)
		if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected.getSiteUID())){
			return;
		}
		super.delete();
	}
}
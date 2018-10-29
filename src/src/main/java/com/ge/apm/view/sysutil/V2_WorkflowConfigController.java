package com.ge.apm.view.sysutil;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.domain.*;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.TreeNodeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.service.common.UserCommonService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class V2_WorkflowConfigController extends JpaCRUDController<WorkflowConfig> {

	private static final long serialVersionUID = 1L;
	private WorkflowConfigRepository dao = null;
	private UserAccountRepository userDao;
	private UserAccount user;
	private UserAccount owner;
	private List<UserAccount> dispatchUserList = new ArrayList<UserAccount>();
	private List<UserInfo> userList;
	private List<V2_DeviceType> deviceTypeList;
	private OrgInfoRepository orgInfoRepository;
	private TenantInfoRepository tenantInfoRepository;
	private Boolean isInsulate;
	private Integer loginUserOrgType;
	private UserCommonService userCommonService;
	private OrgInfo selectedUserOrg;
	//private TenantInfo selectedTenant;
	private TreeNode orgTree;
	private TreeNode selectedNode;
	private UaaService uaaService;
	private OrgInfo selectedOrg;
	private List<UserAccount> approveUserList = new ArrayList<UserAccount>();
	private List<OrgInfo> dispatherClinical = new ArrayList<OrgInfo>();
	private Date telRepStart;
	private Date telRepEnd;
	private UserContextService userContextService;

	@Override
	protected void init() {
        filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(WorkflowConfigRepository.class);
		userDao = WebUtil.getBean(UserAccountRepository.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
		tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
		userCommonService = WebUtil.getBean(UserCommonService.class);
		uaaService = WebUtil.getBean(UaaService.class);
		userContextService =  WebUtil.getBean(UserContextService.class);
		isInsulate = UserContextService.isInsulate();
		loginUserOrgType = orgInfoRepository.findByUid(user.getSiteUID()).getOrgType();
		selectedUserOrg = orgInfoRepository.getByUid(user.getOrgInfoUID());
		setTenantUid(user.getTenantUID());

	}

	public String getOrgFullName(OrgInfo org) {
		if (null == org || org.getId() == -1) {
			return null;
		}
		String name = org.getName();
		while (org.getParentOrg() != null) {
			org = org.getParentOrg();
			name = org.getName() + "/" + name;
		}

		return name;
	}

	public void onSelectOrgForUser(NodeSelectEvent event) {
		TreeNode node = event.getTreeNode();
		selectedUserOrg = (OrgInfo) node.getData();
		updateDispater();
		updateApproverList();
		updateDispatherClinical();
		selected.setSiteUID(selectedUserOrg.getSiteUID());
	}

	private void updateDispatherClinical() {
		dispatherClinical.clear();
		if(selected == null || selectedUserOrg == null){
			return;
		}
		if(isInsulate){
			Integer orgType = selectedUserOrg.getOrgType();
			dispatherClinical = orgInfoRepository.findBySiteUIDAndOrgType(selectedUserOrg.getSiteUID(),4);
		}else{
			dispatherClinical = orgInfoRepository.findByParentUIDAndOrgType(selectedUserOrg.getSiteUID(),4);
		}
	}

	public void setTenantUid(String tenantUid) {
		//searchFilters.clear();
		//this.tenantUid = tenantUid;
		TenantInfo tempTenantInfo = tenantInfoRepository.findByuid(user.getTenantUID());
		//判断登陆用户是否是LocalITAdmin(本地后台运营管理)
		if(UserContextService.isInsulate()){
			//selectedTenant = tempTenantInfo;
			orgTree = uaaService.getOrgTreeBySiteWithoutClinical(user.getSiteUID());
		}else{
			//selectedTenant = tenantInfoRepository.findByuid(tenantUid);
			OrgInfo rootOrg = new OrgInfo();
			rootOrg.setId(-1);
			rootOrg.setName(WebUtil.getMessage("AllHospitals"));
			orgTree = new DefaultTreeNode("root", null, null);
			TreeNode rootNode = new DefaultTreeNode("org", rootOrg, orgTree);
			rootNode.getChildren().addAll(uaaService.getOrgFullTreeByTenantWithoutClinical(tenantUid).getChildren());
		}

		TreeNode selectedUserOrg = TreeNodeUtil.findNodeFromTree(orgTree, selectedOrg);
		if (null != selectedUserOrg) {
			selectedUserOrg.setSelected(true);
			TreeNodeUtil.expandParentsNodes(selectedUserOrg);
		}
	}
	
/*	private boolean isNullOrBelongs(String uid,Set<String> uids){
		if(uid == null){
			return true;
		}
		if(uids.contains(uid)){
			return true;
		}
		return false;
	}*/

	public List<OrgInfo> getIncludeOrgInfo(String uid,String parentUID,Integer orgType){
		return userCommonService.getIncludeOrgInfo(isInsulate,loginUserOrgType,uid,parentUID,orgType);
	}

	public void updateDispater(){
		dispatchUserList.clear();
		if(selected == null || selectedUserOrg == null){
			return;
		}
		dispatchUserList.addAll(userCommonService.getWorkerList(0,selectedUserOrg.getUid()));
	}

	public void updateApproverList(){
		approveUserList.clear();
		if(selectedUserOrg == null || selected == null){
			return;
		}
        Integer orgType = selectedUserOrg.getOrgType();
        if(orgType == 0){
            approveUserList = userDao.getAssetHeadAndStaffByTenantUID(selectedUserOrg.getTenantUID());
        }else if(orgType == 1) {
            approveUserList = userDao.getAssetHeadAndStaffByInstitutionUID(selectedUserOrg.getInstitutionUID());
        }else if(orgType == 2){
            approveUserList = userDao.getAssetHeadAndStaffByHospitalUID(selectedUserOrg.getHospitalUID());
        }else if(orgType == 3){
            approveUserList = userDao.getAssetHeadAndStaffBySiteUID(selectedUserOrg.getSiteUID());
        }
/*		if(isInsulate){
			Integer orgType = selectedUserOrg.getOrgType();
			if(orgType == 0){
				approveUserList = userDao.getAssetHeadAndStaffByTenantUID(selectedUserOrg.getTenantUID());
			}else if(orgType == 1) {
				approveUserList = userDao.getAssetHeadAndStaffByInstitutionUID(selectedUserOrg.getInstitutionUID());
			}else if(orgType == 2){
				approveUserList = userDao.getAssetHeadAndStaffByHospitalUID(selectedUserOrg.getHospitalUID());
			}else if(orgType == 3){
				approveUserList = userDao.getAssetHeadAndStaffBySiteUID(selectedUserOrg.getSiteUID());
			}
		}else{
			approveUserList = userDao.getAssetHeadAndStaffByTenantUID(user.getTenantUID());
		}*/
		approveUserList = approveUserList.stream().distinct().collect(Collectors.toList());
	}
	
	public List<UserInfo> getUserList() {
		return userList;
	}

	public void setUserList(List<UserInfo> userList) {
		this.userList = userList;
	}

	@Override
	protected WorkflowConfigRepository getDAO() {
		return dao;
	}
	
	public String getOrgNameById(Integer orgId){
		OrgInfo oi = orgInfoRepository.findById(orgId);
		if(oi == null){
			return null;
		}
		return oi.getName();
	}
	
	public String getInsNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		if(oi.getOrgLevel() == 1){
			return oi.getName();
		}
		return null;
	}

	public String getHospitalNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		if(oi.getOrgLevel() == 2){
			return oi.getName();
		}
		return null;
	}

	public String getSiteNameByUId(String uid){
		OrgInfo oi =  orgInfoRepository.getByUid(uid);
		if(oi == null){
			return null;
		}
		if(oi.getOrgLevel() == 3){
			return oi.getName();
		}
		return null;
	}

	public String getTenantName(String tenantUID){
		return tenantInfoRepository.findByuid(tenantUID).getName();
	}

	@Override
	protected Page<WorkflowConfig> loadData(PageRequest pageRequest) {
		List<SearchFilter> searchFilters = new ArrayList<>();
		if(isInsulate){
			searchFilters = userCommonService.getLoginUserFilters(isInsulate,loginUserOrgType);
			if(CollectionUtils.isNotEmpty(this.searchFilters)){
				searchFilters.addAll(this.searchFilters);
			}
		}else {
			searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
			if(CollectionUtils.isNotEmpty(this.searchFilters)){
				searchFilters.addAll(this.searchFilters);
			}
		}
        return dao.findBySearchFilter(searchFilters, pageRequest);
	}

	public void onOwnerChange() {
		if (null != owner) {
			selected.setDispatchUserId(owner.getId());
			selected.setDispatchUserName(owner.getName());
		}
	}
	
	public String getTypeName(Integer deviceType){
		for (V2_DeviceType dt : deviceTypeList) {
			if(dt.getId().equals(deviceType)){
				return dt.getName();
			}
		}
		return null;
	}

	@Override
	public List<WorkflowConfig> getItemList() {
		return dao.find();
	}

	public UserAccount getUser() {
		return user;
	}

	public void setUser(UserAccount user) {
		this.user = user;
	}

	public List<UserAccount> getDispatchUserList() {
		return dispatchUserList;
	}

	public void setDispatchUserList(List<UserAccount> dispatchUserList) {
		this.dispatchUserList = dispatchUserList;
	}

	@Transactional
	@Override
	public void onBeforeSave(WorkflowConfig config) {
		selected.setTenantUID(selectedUserOrg.getTenantUID());
		selected.setSiteId(user.getSiteId());
		selected.setHospitalId(user.getHospitalId());
		selected.setInstitutionUID(selectedUserOrg.getInstitutionUID());
		selected.setHospitalUID(selectedUserOrg.getHospitalUID());
		selected.setSiteUID(selectedUserOrg.getSiteUID());
		int orgType = selectedUserOrg.getOrgType();
		if(orgType == 0){
			selected.setAssetSeriaNumLevel(4);//租户级别
		}else if(orgType == 1){
			selected.setAssetSeriaNumLevel(3);//集团级别
		}else if(orgType == 2){
			selected.setAssetSeriaNumLevel(2);//医院级别
		}else if(orgType == 3){
			selected.setAssetSeriaNumLevel(1);//科室级别
		}

		UserAccountRepository uaDao = WebUtil.getBean(UserAccountRepository.class);
		if (selected.getDispatchUserId() != null) {
			UserAccount dispather1 = uaDao.getById(config.getDispatchUserId());
			if (dispather1 != null) {
				selected.setDispatchUserName(dispather1.getName());
			}
		}else{
			selected.setDispatchUserName(null);
		}
		if (selected.getDispatchUserId2() != null) {
			UserAccount dispather2 = uaDao.getById(config.getDispatchUserId2());
			if (dispather2 != null) {
				selected.setDispatchUserName2(dispather2.getName());
			}
		}else{
			selected.setDispatchUserName2(null);
		}

		//pm
		if (selected.getPmDispatchUserId() != null) {
			UserAccount pmDispather1 = uaDao.getById(config.getPmDispatchUserId());
			if (pmDispather1 != null) {
				selected.setPmDispatchUserName(pmDispather1.getName());
			}
		}else{
			selected.setPmDispatchUserName(null);
		}
		if (selected.getPmDispatchUserId2() != null) {
			UserAccount pmDispather2 = uaDao.getById(config.getPmDispatchUserId2());
			if (pmDispather2 != null) {
				selected.setPmDispatchUserName2(pmDispather2.getName());
			}
		}else{
			selected.setPmDispatchUserName2(null);
		}

		if (this.telRepStart != null) {
			selected.setTelReportStart(new DateTime(telRepStart).toString("HH:mm:ss"));
		}
		if (this.telRepEnd != null) {
			selected.setTelReportEnd(new DateTime(telRepEnd).toString("HH:mm:ss"));
		}
	}
	
	@Transactional
    public void applyChange() {
		if(selectedUserOrg == null){
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("selectedOrg"));
			return;
		}
    	Boolean isManual = selected.getIsManualEnabled();
    	Boolean isTakeOrder = selected.getIsTakeOrderEnabled();
		Boolean isPmManual = selected.getIsPmManualEnabled();
		Boolean isPmTakeOrder = selected.getIsPmTakeOrderEnabled();

    	if(isManual != null && isManual){
    		if(selected.getDispatchUserId() == null && selected.getDispatchUserId2() == null){
    			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
       		 	WebUtil.addErrorMessage(WebUtil.getMessage("WoNeedDispather"));
       		 	return;
    		}
    	}
    	if(isPmManual != null && isPmManual){
			if(selected.getPmDispatchUserId() == null && selected.getPmDispatchUserId2() == null){
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				WebUtil.addErrorMessage(WebUtil.getMessage("PmNeedDispather"));
				return;
			}
		}


    	if(isManual != null && isTakeOrder != null && !isManual && !isTakeOrder){
    		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
    		 WebUtil.addErrorMessage(WebUtil.getMessage("WoManualAndGrapMsg"));
    		 return;
    	}

    	if(isPmManual != null && isPmTakeOrder != null && !isPmManual && !isPmTakeOrder){
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("PmManualAndGrapMsg"));
			return;
		}

/*		if(selected.getId() != null){
    		if(selected.getFormat() != null && newFormat != null){
    			if(selected.getFormat().length() > newFormat.length()){
					RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
					WebUtil.addErrorMessage(WebUtil.getMessage("FormatIllegal"));
					return;
				}
			}
		}*/

		if(selected.getGenerateMethod() != null && selected.getGenerateMethod() ==1){
    		if(StringUtils.isEmpty(selected.getPrefix())){
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				WebUtil.addErrorMessage(WebUtil.getMessage("prefixIsNull"));
				return;
			}else{
    			if(selected.getPrefix().length() > 3){
					RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
					WebUtil.addErrorMessage(WebUtil.getMessage("moreThanThree"));
					return;
				}
    			List<WorkflowConfig> prefixList = dao.findAllPrefixByTenantUID(user.getTenantUID());
    			if(CollectionUtils.isNotEmpty(prefixList)){
					for (WorkflowConfig temp:prefixList) {
						if((!temp.equals(selected)) && temp.getPrefix().equals(selected.getPrefix())){
							RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
							WebUtil.addErrorMessage(WebUtil.getMessage("prefixExistsAlready"));
							return;
						}
					}
					/*boolean isExist = prefixList.stream().anyMatch(config -> config.getPrefix().equals(selected.getPrefix()));
					if(isExist){
						RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
						WebUtil.addErrorMessage(WebUtil.getMessage("prefixExistsAlready"));
						return;
					}*/
				}
			}

		}else if(selected.getGenerateMethod() != null && selected.getGenerateMethod() == 3){
			if(StringUtils.isEmpty(selected.getPrefix())){
				RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
				WebUtil.addErrorMessage(WebUtil.getMessage("prefixIsNull"));
				return;
			}
		}
		//前缀+年份+流水号不能超过16位
		int preLength = selected.getPrefix() == null ? 0 : selected.getPrefix().length();
		int flowNum = selected.getFormat() == null ? 0 : selected.getFormat().length();
		if(selected.getGenerateMethod() == 1){//国标码
            if(preLength + 7 + flowNum > 16){
                RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
                WebUtil.addErrorMessage(WebUtil.getMessage("srNumTooLong"));
                return;
            }
        }
		if(preLength + 4 + flowNum > 16){
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("srNumTooLong"));
			return;
		}

        onBeforeSave(selected);

        //判断是否有权限(只能操作当前登陆用户级别以下的和自己相同siteuid的数据)
//        if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected.getSiteUID())){
//            return;
//        }

        dao.deleteBySiteUID(selected.getSiteUID());
        super.save();
		selectedUserOrg = null; //orgInfoRepository.getByUid(user.getOrgInfoUID());
    }

	@Override
	public void delete() {
		//判断是否有权限(只能操作当前登陆用户级别以下的和自己相同siteuid的数据)
//		if(!userCommonService.isCanOperate(UserContextService.getCurrentUserAccount(), selected.getSiteUID())){
//			return;
//		}
		super.delete();
		selectedUserOrg = null;
		dispatchUserList.clear();
	}

	@Override
	public void prepareEdit(){
		dispatchUserList.clear();
		approveUserList.clear();
		dispatchUserList.clear();
		super.prepareEdit();
		selectedUserOrg = orgInfoRepository.getByUid(selected.getSiteUID());
		updateDispater();
		updateApproverList();
		updateDispatherClinical();
	}

	public void prepareCreate(){
		dispatchUserList.clear();
		approveUserList.clear();
		dispatchUserList.clear();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			super.prepareCreate();
			telRepStart = sdf.parse(DateTime.now().toString("yyyy-MM-dd") + " 20:00:00");
			telRepEnd = sdf.parse(DateTime.now().toString("yyyy-MM-dd") + " 08:00:00");
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		selectedUserOrg = null;

/*		updateDispater();
		updateApproverList();*/
	}
	
	public void onSelect(){
		if(selected != null && selected.getId() != null){
			selected = dao.findById(selected.getId());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				if (selected != null) {
					this.telRepStart = selected.getTelReportStart() != null ? (sdf.parse(DateTime.now().toString("yyyy-MM-dd") + " " + selected.getTelReportStart()))
							: (sdf.parse(DateTime.now().toString("yyyy-MM-dd") + " 20:00:00"));
					this.telRepEnd = selected.getTelReportEnd() != null ? (sdf.parse(DateTime.now().toString("yyyy-MM-dd") + " " + selected.getTelReportEnd()))
							: (sdf.parse(DateTime.now().toString("yyyy-MM-dd") + " 08:00:00"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public UserAccount getOwner() {
		return owner;
	}

	public void setOwner(UserAccount owner) {
		this.owner = owner;
	}

	public List<V2_DeviceType> getDeviceTypeList() {
		return deviceTypeList;
	}

	public void setDeviceTypeList(List<V2_DeviceType> deviceTypeList) {
		this.deviceTypeList = deviceTypeList;
	}

	public List<OrgInfo> getHospitalList(String orgLevel) {
		List<OrgInfo> result = null;

		if(StringUtils.isNotEmpty(orgLevel)){
			int level = Integer.parseInt(orgLevel);
			Integer userLevel =  user.getOrgLevel() == null ? 0 : user.getOrgLevel();
			List<SearchFilter> searchFilters = new ArrayList<>(5);
			if(!isInsulate){
				if(0 == level){
					searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
					searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
				}else if(1 == level){
					searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
					searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
				}else if(2 == level){
					searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
					searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
				}else if(3 == level){
					searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
					searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
				}
			}else{
				switch (userLevel){
					case 0:  //当前人属于租户级
						searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
						if(0 == level){
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
						}else if(1 == level){
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
						}else if(2 == level){
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
						}else if(3 == level){
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
						}
						break;
					case 1://当前人属于集团
						if(0 == level){
							searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
						}else if(1 == level){
							searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
						}else if(2 == level){
							searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
						}else if(3 == level){
							searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
						}
						break;
					case 2:
						if(0 == level){
							searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
						}else if(1 == level){
							searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
						}else if(2 == level){
							searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
						}else if(3 == level){
							searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
						}
						break;
					default:
						if(0 == level){
							searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 0));
						}else if(1 == level){
							searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, user.getInstitutionUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 1));
						}else if(2 == level){
							searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, user.getHospitalUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 2));
						}else if(3 == level){
							searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, user.getSiteUID()));
							searchFilters.add(new SearchFilter("orgType", SearchFilter.Operator.EQ, 3));
						}
						break;
				}
			}
			result = orgInfoRepository.findBySearchFilter(searchFilters);
		}
		return result;
	}

	public OrgInfo getSelectedUserOrg() {
		return selectedUserOrg;
	}

	public void setSelectedUserOrg(OrgInfo selectedUserOrg) {
		this.selectedUserOrg = selectedUserOrg;
	}

	public TreeNode getOrgTree() {
		return orgTree;
	}

	public void setOrgTree(TreeNode orgTree) {
		this.orgTree = orgTree;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

/*	public TenantInfo getSelectedTenant() {
		return selectedTenant;
	}

	public void setSelectedTenant(TenantInfo selectedTenant) {
		this.selectedTenant = selectedTenant;
	}*/

	public OrgInfo getSelectedOrg() {
		return selectedOrg;
	}

	public void setSelectedOrg(OrgInfo selectedOrg) {
		this.selectedOrg = selectedOrg;
	}

	public List<UserAccount> getApproveUserList() {
		return approveUserList;
	}

	public void setApproveUserList(List<UserAccount> approveUserList) {
		this.approveUserList = approveUserList;
	}

	public void setDispatherClinical(List<OrgInfo> dispatherClinical) {
		this.dispatherClinical = dispatherClinical;
	}

	public List<OrgInfo> getDispatherClinical() {
		return dispatherClinical;
	}

	public Date getTelRepStart() {
		return telRepStart;
	}

	public void setTelRepStart(Date telRepStart) {
		this.telRepStart = telRepStart;
	}

	public Date getTelRepEnd() {
		return telRepEnd;
	}

	public void setTelRepEnd(Date telRepEnd) {
		this.telRepEnd = telRepEnd;
	}
}
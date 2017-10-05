package com.ge.apm.view.uaa;

import com.ge.apm.dao.AssetInfoRepository;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.web.mvc.JpaCRUDController;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.dao.WorkflowConfigRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.WorkflowConfig;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.dao.DataIntegrityViolationException;
import webapp.framework.web.WebUtil;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.QrCodeLib;
import java.util.UUID;
import webapp.framework.dao.SearchFilter;

@ManagedBean
@ViewScoped
public class OrgInfoController extends JpaCRUDController<OrgInfo> {

    OrgInfoRepository dao = null;
    UaaService uaaService;
    private TenantInfo selectedTenant;
    UserAccount userAccount;

    private boolean acShow=false;

    @Override
    protected void init() {
        filterBySite = false;
        
        dao = WebUtil.getBean(OrgInfoRepository.class);
        uaaService = WebUtil.getBean(UaaService.class);
        userDao = WebUtil.getBean(UserAccountRepository.class);
        userAccount = new UserAccount();
        //userAccount.set
        findPerson();
        UserAccount loginUser = UserContextService.getCurrentUserAccount();
        setTenantId(loginUser.getSiteId());
        buildOrdTree();
    }

    @Override
    protected OrgInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<OrgInfo> loadData(PageRequest pageRequest) {

        return dao.getBySiteId(pageRequest, tenantId);
    }

    private int tenantId;

    public int getTenantId() {
        return tenantId;
    }
    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
        TenantInfoRepository siteDao = WebUtil.getBean(TenantInfoRepository.class);
        selectedTenant = siteDao.findById(tenantId);
        
        this.selected = null;
        buildOrdTree();
    }
UserAccountRepository userDao;
    public TenantInfo getSelectedTenant() {
        return selectedTenant;
    }
    public List<UserAccount> findPerson(){
        List<UserAccount> userList = new ArrayList<UserAccount>();
        if(this.selected!=null && this.selected.getOrgType()==4) {//类型是科室
                if(this.selected.getParentOrg()!=null)
                    userList= userDao.findBySiteUID(this.selected.getSiteUID());
        }
        return userList;
    }
    public void buildOrdTree(){
//        if(selected!=null)
//            orgTree = uaaService.getFullOrgTreeBySiteId(this.tenantId, selected.getId());
//        else
//            orgTree = uaaService.getFullOrgTreeBySiteId(this.tenantId, null);

        orgTree = uaaService.getOrgFullTreeByTenant(selectedTenant==null?UserContextService.getCurrentUserAccount().getTenantUID():selectedTenant.getUid());
        try{
            this.selectedNode = (TreeNode)orgTree.getData();
        }
        catch(Exception ex){
        }
    }

    public List<OrgInfo> getHospitalListBySiteId(){
        return uaaService.getHospitalListBySiteId(tenantId);
    }
    
    private TreeNode orgTree;
    public TreeNode getOrgTree(){
        return orgTree;
    }
    private TreeNode selectedNode;

    public TreeNode getSelectedNode() {
        return selectedNode;
    }
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
    
    public void prepareCreateInstitution(){
        OrgInfo org = new OrgInfo();
        org.setSiteId(tenantId);

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
       org.setInstitutionUID(org.getUid());
        org.setHospitalUID(org.getUid());
        org.setSiteUID(org.getUid());
        
        org.setOrgLevel(1);
        org.setOrgType(1);
        
        org.setParentUID(selectedTenant.getUid());
        org.setParentOrg(dao.getByUid(selectedTenant.getUid()));
        this.selected = org;
    }

    public void prepareCreateHospital(){
        if(selected==null) return;
        
        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getHospitalId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(org.getUid());
        org.setSiteUID(org.getUid());
        
        org.setOrgLevel(2);
        org.setOrgType(2);
        this.selected = org;
    }
        
    public void prepareCreateSite(){
        if(selected==null) return;
        
        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getHospitalId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(selected.getHospitalUID());
        org.setSiteUID(org.getUid());
        
        org.setOrgLevel(3);
        org.setOrgType(3);
        this.selected = org;
    }
    
    public void prepareCreateDepartment(){
        if(selected==null) return;
        
        OrgInfo org = new OrgInfo();
        org.setParentOrg(selected);
        org.setParentUID(selected.getUid());

        org.setSiteId(selected.getSiteId());
        org.setHospitalId(selected.getHospitalId());

        //UIDs
        org.setUid(UUID.randomUUID().toString().replace("-", ""));
        org.setTenantUID(selectedTenant.getUid());
        org.setInstitutionUID(selected.getInstitutionUID());
        org.setHospitalUID(selected.getHospitalUID());
        org.setSiteUID(selected.getSiteUID());
        
        org.setOrgLevel(selected.getOrgLevel());
        org.setOrgType(4);
        this.selected = org;
    }

    public void saveSelectedOrg(){
        super.save();
        initWorkflowConfig();
        
        buildOrdTree();
    }
    
    public void initWorkflowConfig(){
        if(!selected.getHospitalId().equals(selected.getId())) return;
        
        WorkflowConfigRepository wfConfigDao = WebUtil.getBean(WorkflowConfigRepository.class);
        WorkflowConfig wfConfig = wfConfigDao.findById(selected.getHospitalId());
        if(wfConfig==null){
            wfConfig = new WorkflowConfig();
            wfConfig.setHospitalId(selected.getHospitalId());
            wfConfig.setSiteId(selected.getSiteId());
            
            wfConfig.setDispatchMode(1);
            wfConfig.setTimeoutDispatch(30);
            wfConfig.setTimeoutAccept(30);
            wfConfig.setTimeoutRepair(300);  //5 hours
            wfConfig.setTimeoutClose(30);
            wfConfig.setOrderReopenTimeframe(7); //day
            wfConfig.setMaxMessageCount(2); 
            
            wfConfigDao.save(wfConfig);
        }
    }

    public void deleteSelectedOrg() {
        if (canDelete()) {
            super.delete();
            buildOrdTree();
        } 
    }

    private Boolean canDelete(){
        List<SearchFilter> checkFilters = new ArrayList<>();
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("orgInfoUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        List<UserAccount> userList = userDao.findBySearchFilter(checkFilters);
        if(userList!=null && !userList.isEmpty()){
            WebUtil.addErrorMessage("该组织下有用户存在,不能删除.");
            return false;
        }
        AssetInfoRepository assetDao = WebUtil.getBean(AssetInfoRepository.class);
        checkFilters.clear();
        checkFilters.add(new SearchFilter("clinicalDeptUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        List<AssetInfo> assetList = assetDao.findBySearchFilter(checkFilters);
        if(assetList!=null && !assetList.isEmpty()){
            WebUtil.addErrorMessage("该组织下有资产存在,不能删除.");
            return false;
        }
        QrCodeLibRepository qrcodeLibDao = WebUtil.getBean(QrCodeLibRepository.class);
        checkFilters.clear();
        checkFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        checkFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, selected.getUid(),"1+"));
        List<QrCodeLib> qrcodeList = qrcodeLibDao.findBySearchFilter(checkFilters);
        if(qrcodeList!=null && !qrcodeList.isEmpty()){
            WebUtil.addErrorMessage("该组织下有二维码存在,不能删除.");
            return false;
        }
        return true;
    }
    public void onSelectTreeNode(NodeSelectEvent event){
        TreeNode node = event.getTreeNode();
        //node.setExpanded(!node.isExpanded());
        
        selected = (OrgInfo)node.getData();
        if(selected!=null) {
            if (selected.getOrgType() == 4) {
                acShow = true;
            } else {
                acShow = false;
            }
        }
    }
 
    @Override
    public void onBeforeNewObject(OrgInfo object) {
        object.setHospitalId(UserContextService.getCurrentUserAccount().getHospitalId());
    }

    @Override
    protected boolean onPersistException(Exception ex, OrgInfo org){
        if(ex.getClass().equals(DataIntegrityViolationException.class)){
            DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
            if(exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")){
                String errMessage = WebUtil.getMessage("ForeighKeyErrorWithOrgId");
                WebUtil.addErrorMessage(errMessage);

                return true;
            }
        }
                
        return false;
    }

    public boolean getAcShow() {
        return acShow;
    }

    public void setAcShow(boolean acShow) {
        this.acShow = acShow;
    }

    public boolean getIsCreateInstitutionDisabled(){
        return selected.getId()==null;
    }
    
    public boolean getIsCreateHospitalDisabled(){
        return (selected==null) || (selected.getOrgType()==null) || (selected.getOrgType()!=1) || selected.getId()==null;
    }
    public boolean getIsCreateSiteDisabled(){
        return (selected==null) || (selected.getOrgType()==null) || (selected.getOrgType()!=2) || selected.getId()==null;
    }
    public boolean getIsCreateDepartmentDisabled(){
        return (selected==null) || (selected.getOrgType()==null) || (selected.getOrgType()==4) || selected.getId()==null;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}
package com.ge.apm.view;

import com.ge.apm.dao.RoleAuthorityRepository;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.SysAuthorityRepository;
import com.ge.apm.domain.RoleAuthority;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.SysAuthority;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class CustomRoleController extends JpaCRUDController<CustomRole> {

    private CustomRoleRepository dao = null;
    private RoleAuthorityRepository roleAuthorityRepository;
    private SysAuthorityRepository sysAuthorityRepository;
    //private List<String> authIds = new ArrayList<>(10);
    private String[] authIds = new String[20];
    private UserAccount user;
    public static List<String> keyRoles = new ArrayList<>();

    static {
        keyRoles.add("院长");
        keyRoles.add("医学工程处主任");
        keyRoles.add("工程师");
        keyRoles.add("临床科室主任");
        keyRoles.add("后台运营管理");
        keyRoles.add("多院区管理");
        keyRoles.add("临床科室人员");
        keyRoles.add("仅报修权限账号");
        keyRoles.add("设备调剂管理人员");
        keyRoles.add("本地后台运营管理");
        keyRoles.add("维修商医工");
        keyRoles.add("护理人员");
        keyRoles.add("系统激活专用");
    }


    @Override
    protected void init() {
        filterBySite = false;
        user = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(CustomRoleRepository.class);
        roleAuthorityRepository = WebUtil.getBean(RoleAuthorityRepository.class);
        sysAuthorityRepository = WebUtil.getBean(SysAuthorityRepository.class);
    }

    @Override
    protected CustomRoleRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<CustomRole> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = new ArrayList();
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
        if(CollectionUtils.isNotEmpty(this.searchFilters)){
            searchFilters.addAll(this.searchFilters);
        }
        Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
        pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
        return dao.findBySearchFilter(this.searchFilters, pageRequest);
/*        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }*/
    }

    public String getRoleAuth(Integer id){
        List<SysAuthority> authorities = sysAuthorityRepository.findSysAuthorityByRoleId(id);
        if(CollectionUtils.isNotEmpty(authorities)){
            StringBuilder sb = new StringBuilder();
            for (SysAuthority sa :authorities) {
                sb.append(sa.getName()).append(",");
            }
            String result = sb.toString();
            if(result.endsWith(",")){
                result = result.substring(0,result.length()-1);
            }
            return result;
        }
        return null;
    }

    @Override
    public List<CustomRole> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    public String[] getAuthIds() {
        return authIds;
    }

    public void setAuthIds(String[] authIds) {
        this.authIds = authIds;
    }

    public void onBeforeNewObject(CustomRole object) {

    }

    @Override
    public void onBeforeSave(CustomRole role) {
        role.setTenantUID(user.getTenantUID());
        role.setInstitutionUID(user.getInstitutionUID());
        role.setHospitalUID(user.getHospitalUID());
        role.setSiteUID(user.getSiteUID());
        if(role.getCreatedBy() == null){
            role.setCreatedBy(user.getLoginName());
        }
        if(role.getCreatedDate() == null){
            role.setCreatedDate(new Date());
        }
        role.setLastModifiedBy(user.getLoginName());
        role.setLastModifiedDate(new Date());
    }


    @Transactional
    public void save(){
        if(keyRoles.contains(selected.getName())){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("containsKeyRole"));
            return;
        }
        super.save();
        if(selected.getId() != null){
            roleAuthorityRepository.deleteByRoleId(selected.getId());
        }
        Integer id = selected.getId();
        if(authIds.length > 0){
            List<RoleAuthority> authorities = new ArrayList<>(10);
            for (String s : authIds) {
                RoleAuthority auth = new RoleAuthority();
                auth.setCreatedBy(user.getLoginName());
                auth.setCreatedDate(new Date());
                auth.setLastModifiedBy(user.getLoginName());
                auth.setLastModifiedDate(new Date());
                auth.setRoleId(id);
                auth.setSysAuthId(Integer.parseInt(s));
                auth.setTenantUID(user.getTenantUID());
                auth.setInstitutionUID(user.getInstitutionUID());
                auth.setHospitalUID(user.getHospitalUID());
                auth.setSiteUID(user.getSiteUID());
                authorities.add(auth);
            }
            roleAuthorityRepository.save(authorities);
        }
        authIds = new String[20];
    }

    public void prepareEdit(){
        if(selected.getId() != null){
            List<Integer> auths = roleAuthorityRepository.findByAuthIds(selected.getId());
            if(CollectionUtils.isNotEmpty(auths)){
                authIds = convert(auths);
            }
        }

    }

    private String[] convert(List<Integer> auths) {
        String[] s = new String[auths.size()];
        for (int i = 0 ; i< auths.size(); i++) {
            s[i] = auths.get(i).toString();
        }
        return s;
    }

    @Transactional
    public void delete(){
        Integer roleId = selected.getId();
        roleAuthorityRepository.deleteByRoleId(roleId);
        super.delete();
    }

    public static List<String> getKeyRoles() {
        return keyRoles;
    }

    public static void setKeyRoles(List<String> keyRoles) {
        CustomRoleController.keyRoles = keyRoles;
    }

    /*
    @Override

    
    @Override
    public void onAfterNewObject(CustomRole object, boolean isOK) {
    }

    @Override
    public void onBeforeUpdateObject(CustomRole object) {
    }
    
    @Override
    public void onAfterUpdateObject(CustomRole object, boolean isOK) {
    }
    
    @Override
    public void onBeforeDeleteObject(CustomRole object) {
    }
    
    @Override
    public void onAfterDeleteObject(CustomRole object, boolean isOK) {
    }
    

    
    @Override
    public void onAfterDataChanged(){
    };
*/
}
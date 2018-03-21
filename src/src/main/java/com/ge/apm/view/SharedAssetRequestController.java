package com.ge.apm.view;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.SharedAssetReqDetailRepository;
import com.ge.apm.dao.SharedAssetRequestRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.SharedAssetReqDetail;
import com.ge.apm.domain.SharedAssetRequest;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.service.utils.UrlParamUtil;
import com.ge.apm.view.asset.AssetInfoController;
import com.ge.apm.view.sysutil.UrlEncryptController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class SharedAssetRequestController extends GenericCRUDUUIDController<SharedAssetRequest> {
    private UaaService uuaService;
    SharedAssetRequestRepository dao = null;
    AssetInfoRepository assetInfoDao = null;
    SharedAssetReqDetailRepository detailDao = null;
    private List<AssetInfo> selectInfo ;
    private String Num;
    private boolean confirm;
    @Override
    protected void init() {
        uuaService = WebUtil.getBean(UaaService.class);
        assetInfoDao = WebUtil.getBean(AssetInfoRepository.class);
        dao = WebUtil.getBean(SharedAssetRequestRepository.class);
        detailDao = WebUtil.getBean(SharedAssetReqDetailRepository.class);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String encodeStr = request.getParameter("str");
        String actionName = (String)UrlEncryptController.getValueFromMap(encodeStr, "actionName");
        if ("Create".equalsIgnoreCase(actionName)) {
            try {
                prepareCreate();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(AssetInfoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
        else if ("View".equalsIgnoreCase(actionName)) {
            prepareView();
        }
        else if ("Edit".equalsIgnoreCase(actionName)) {
            
            setSelected(dao.findById((String)UrlEncryptController.getValueFromMap(encodeStr, "selectedid")));
            prepareEdit();
        }
        else if ("Delete".equalsIgnoreCase(actionName)) {
            prepareDelete();
        } 
        if (searchFilters == null) {
            searchFilters = new ArrayList<SearchFilter>();
        }
    }
    @Override
    public void save(){
        this.selected.setState(0);
        super.save();
    }
    public void confirm(){
        this.selected.setState(1);
        if(!detailDao.findBySharedId(this.selected.getId()).isEmpty()){
            List<SharedAssetReqDetail> detail = detailDao.findBySharedId(this.selected.getId());
            for(SharedAssetReqDetail details:detail){
            details.setState("0");
            detailDao.save(details);
        }
        }
        super.save();
    }

    @Override
    protected SharedAssetRequestRepository getDAO() {
        return dao;
    }
    public void onRowEdit() throws IOException {
        int state =this.selected.getState();
       
        System.out.println(state);
        if(state != 6){
        String url = "actionName=Edit&selectedid="
                + selected.getId();
        url = "Edit.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        //FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        }
        else{
             String url = "actionName=Edit&selectedid="
                + selected.getId();
        url = "Edit_1.xhtml?str=" + UrlParamUtil.encodeUrlQueryString(url);

        //FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.addCallbackParam("url", url);
        }
    }
    @Override
    protected Page<SharedAssetRequest> loadData(PageRequest pageRequest) {
//        if (this.searchFilters == null) {
//            return dao.findAll(pageRequest);
//        } else {
//            return dao.findBySearchFilter(this.searchFilters, pageRequest);
//        }
        setUserSearchFilters();
        return dao.find(pageRequest);
    }
    private void setUserSearchFilters() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        List<String> orgUidList = uuaService.getOrgListByUser(user.getUid()).stream().map(item -> item.getUid()).collect(Collectors.toList());
        searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.IN, orgUidList, "1*2+"));
        searchFilters.add(new SearchFilter("clinicalDeptUid", SearchFilter.Operator.IN, orgUidList, "1*2+"));
    }
    @Override
    public void onBeforeNewObject(SharedAssetRequest object) {
        object.setId(UUID.randomUUID().toString().replace("-", ""));
        object.setTenantUID(UserContextService.getCurrentUserAccount().getTenantUID());
        object.setHospitalUID(UserContextService.getCurrentUserAccount().getHospitalUID());
        object.setInstitutionUID(UserContextService.getCurrentUserAccount().getInstitutionUID());
        object.setSiteUID(UserContextService.getCurrentUserAccount().getSiteUID());
        object.setApplicant(UserContextService.getCurrentUserAccount().getName());
    }
    

    @Override
    public List<SharedAssetRequest> getItemList() {
        //to do: change the code if necessary
        return dao.find();
    }

    /**
     * @return the selectInfo
     */
    public List<AssetInfo> getSelectInfo() {
        return selectInfo;
    }

    /**
     * @param selectInfo the selectInfo to set
     */
    public void setSelectInfo(List<AssetInfo> selectInfo) {
        this.selectInfo = selectInfo;
    }

    /**
     * @return the Num
     */
    public String getNum() {
        return Num;
    }

    /**
     * @param Num the Num to set
     */
    public void setNum(String Num) {
        this.Num = Num;
    }

    /**
     * @return the confirm
     */
  
}
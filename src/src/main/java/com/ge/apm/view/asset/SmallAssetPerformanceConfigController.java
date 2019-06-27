package com.ge.apm.view.asset;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.SmallAssetPerformanceConfigRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.SmallAssetPerformanceConfig;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.AssetInfoService;
import com.ge.apm.service.asset.AutoCompleteService;
import com.ge.apm.service.asset.SmallAssetPerformanceConfigService;
import com.ge.apm.service.utils.PinYinComparatorByName;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;

import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class SmallAssetPerformanceConfigController extends GenericCRUDUUIDController<SmallAssetPerformanceConfig> implements Serializable, ServerEventInterface {

    private SmallAssetPerformanceConfigRepository dao;
    private I18nMessageRepository i18nDao;
    private UserAccount currentUser;
    private SmallAssetPerformanceConfigService service;
    private UploadedFile file;
    private List<SmallAssetPerformanceConfig> importFailedDataList;
    private Map<String,Object> selectedObjectMap;
    private List<SmallAssetPerformanceConfig> listNewConfig;
    private List<OrgInfo> clinicalDeptList;
    private List<AssetInfo> assetInfoList;
    private int type;//0:create,1:edit
    private SmallAssetPerformanceConfig editConfig;
    private AssetInfoService assetInfoService;
    private boolean blnSaveable=false;
    private AutoCompleteService autoCompleteService;
    @Override
    protected void init() {
        dao = WebUtil.getBean(SmallAssetPerformanceConfigRepository.class);
        i18nDao = WebUtil.getBean(I18nMessageRepository.class);
        service = WebUtil.getBean(SmallAssetPerformanceConfigService.class);
        service.init();
        currentUser = UserContextService.getCurrentUserAccount();
        assetInfoService = WebUtil.getBean(AssetInfoService.class);
        clinicalDeptList = assetInfoService.getHierachyClinicalDeptList(currentUser.getSiteUID());
        assetInfoList=service.getAccessAssetInfo(currentUser.getSiteUID(),true);
        autoCompleteService=WebUtil.getBean(AutoCompleteService.class);
    }

    @Override
    protected GenericRepositoryUUID<SmallAssetPerformanceConfig> getDAO() {
        return dao;
    }

    @Override
    protected Page<SmallAssetPerformanceConfig> loadData(PageRequest pageRequest) {
    	List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
        if(currentUser.getOrgLevel() != 0){
            if(currentUser.getOrgLevel() == 1){
                searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
            }else if(currentUser.getOrgLevel() == 2){
                searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
            }else{
                searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
            }
        }
        return dao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {

    }
    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
    	// TODO Auto-generated method stub
    	super.prepareCreate();
    	type=0;
    	blnSaveable=false;
    	listNewConfig=new LinkedList<SmallAssetPerformanceConfig>();
        editConfig=new SmallAssetPerformanceConfig();
        listNewConfig.add(editConfig);
        selectedObjectMap=new HashMap<String,Object>();
    }
    @Override
    public void prepareEdit() {
    	// TODO Auto-generated method stub
    	super.prepareEdit();
    	type=1;
    	blnSaveable=true;
        selectedObjectMap=new HashMap<String,Object>();
    	String departName=selected.getDepartment();
    	String assetName=selected.getAssetName();
    	String clinicalUid=selected.getClinicalDeptUID();
    	if(StringUtils.isNotEmpty(departName)){
    		selectedObjectMap.put("clinicalDept", service.getOrgInfoByName(clinicalDeptList, departName));
    	}
    	if(StringUtils.isNotEmpty(assetName)){
    		selectedObjectMap.put("asset", service.getAssetInfoByName(assetInfoList, assetName));
    		if(StringUtils.isNotEmpty(clinicalUid)){
    			assetInfoList=service.getAccessAssetInfo(currentUser.getSiteUID(),clinicalUid);
    		}
    	}
    }

    @Override
    public void onBeforeNewObject(SmallAssetPerformanceConfig object) {
        super.onBeforeNewObject(object);
        object.setTenantUID(currentUser.getTenantUID());
        object.setInstitutionUID(currentUser.getInstitutionUID());
        object.setHospitalUID(currentUser.getHospitalUID());
        object.setSiteUID(currentUser.getSiteUID());
    }

    @Override
    public void save() {
    	if(type==0){
    		for(int i=0;i<listNewConfig.size();++i){
    			SmallAssetPerformanceConfig tempConfig=listNewConfig.get(i);
    			if(StringUtils.isEmpty(tempConfig.getAssetName())){
    				continue;
    			}
    			tempConfig.setDepartment(selected.getDepartment());
    			tempConfig.setProjectName(selected.getProjectName());
    			tempConfig.setProjectNumber(selected.getProjectNumber());
    			tempConfig.setTenantUID(currentUser.getTenantUID());
    			tempConfig.setInstitutionUID(currentUser.getInstitutionUID());
    			tempConfig.setHospitalUID(currentUser.getHospitalUID());
    			tempConfig.setSiteUID(currentUser.getSiteUID());
    			tempConfig.setAverage(selected.getAverage());
    			tempConfig.setClinicalDeptUID(selected.getClinicalDeptUID());
    			if(service.checkConfigExists(tempConfig)){
    				WebUtil.addErrorMessage("项目名称,科室名称，设备名称唯一，请勿重复添加！");
        			this.selected=null;
        			return;
    			}
    			service.checkData(tempConfig);
    			service.save(tempConfig);
    		}
    		listNewConfig.clear();
    		editConfig=new SmallAssetPerformanceConfig();
            listNewConfig.add(editConfig);
    	}
    	else if(type==1){
    		if(service.checkConfigExists(this.selected)){
    			WebUtil.addErrorMessage("项目名称,科室名称，设备名称唯一，请勿重复添加！");
    			this.selected=null;
    			return;
    		}
        	service.checkData(this.selected);
        	super.save();
    	}
    	WebUtil.addSuccessMessage("保存成功！");
    	this.selected=null;
    	selectedObjectMap=new HashMap<String,Object>();
    }
    
    @Override
    public void delete() {
    	// TODO Auto-generated method stub
    	service.checkListConfig(selected,true);
    	super.delete();
    	this.selected=null;
    }
    public void rateCalculate(){
    	String projectName=selected.getProjectName();
    	String department=selected.getDepartment();
    	if(StringUtils.isEmpty(projectName)){
    		WebUtil.addErrorMessage("项目名称不能为空！");
    		return;
    	}
    	if(StringUtils.isEmpty(department)){
    		WebUtil.addErrorMessage("科室名称不能为空！");
    		return;
    	}
    	service.rateCalculate(listNewConfig,projectName, department,currentUser.getSiteUID());
    }
    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        FacesMessage message = new FacesMessage("导入完成", event.getFile().getFileName() + " 处理完成！");
        this.file = event.getFile();
        importFailedDataList=service.handleExcel(file, currentUser);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

	public List<SmallAssetPerformanceConfig> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<SmallAssetPerformanceConfig> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}
	public List<OrgInfo> autoCompleteDept(String query) {
        if (clinicalDeptList == null) {
            return Collections.emptyList();
        }
        List<OrgInfo> res = this.clinicalDeptList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
	public List<AssetInfo> autoCompleteAsset(String query) {
        if (assetInfoList == null) {
            return Collections.emptyList();
        }
        List<AssetInfo> res = this.assetInfoList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)
                || (item.getNamePyCode() == null ? false : item.getNamePyCode().contains(query.toLowerCase()))).collect(Collectors.toList());
        res.sort(new PinYinComparatorByName());
        return res;
    }
	public void setObjCheckItem(SmallAssetPerformanceConfig config){
		editConfig=config;
		selectedObjectMap.put("asset", editConfig.getAssetInfo());
	}
	public void deleteItemDetail(SmallAssetPerformanceConfig config){
		for(int i=0;i<listNewConfig.size();++i){
			SmallAssetPerformanceConfig tempConfig=listNewConfig.get(i);
			if(tempConfig.getAssetName().equals(config.getAssetName())){
				listNewConfig.remove(i);
				break;
			}
		}
		checkSaveable();
	}
	public void saveCheckListItem(){
		boolean add=true;
		SmallAssetPerformanceConfig tempConfig=null;
		for(int i=0;i<listNewConfig.size();++i){
			tempConfig=listNewConfig.get(i);
			if(StringUtils.isEmpty(tempConfig.getAssetName())){
				add=false;
				break;
			}
		}
		if(add){
			editConfig=new SmallAssetPerformanceConfig();
			listNewConfig.add(editConfig);
			selectedObjectMap.put("asset",null);
		}
		else{
			editConfig=tempConfig;
		}
		checkSaveable();
	}
	
	public void checkSaveable(){
		blnSaveable=false;
		if(type==0){
			if(listNewConfig.size()>1){
				if(selectedObjectMap.get("clinicalDept")!=null && StringUtils.isNotEmpty(selected.getDepartment())){
					blnSaveable=true;
				}
			}
		}
		else if(type==1){
			if(selectedObjectMap.get("clinicalDept")!=null &&StringUtils.isNotEmpty(selected.getDepartment())){
				if(selectedObjectMap.get("asset")!=null &&StringUtils.isNotEmpty(selected.getAssetName())){
					blnSaveable=true;
				}
			}
		}
	}
	public void onAssetDeptChange(String idx) {
        OrgInfo clinicalDept = null;
        switch (idx) {
            case "clinicalDept":
                clinicalDept = (OrgInfo) selectedObjectMap.get("clinicalDept");
                if(clinicalDept==null){
                	selected.setDepartment("");
                	selected.setClinicalDeptUID("");
                	assetInfoList=service.getAccessAssetInfo(currentUser.getSiteUID(),true);
                }
                else{
                	selected.setDepartment(clinicalDept.getName());
                	selected.setClinicalDeptUID(clinicalDept.getUid());
                    assetInfoList=service.getAccessAssetInfo(currentUser.getSiteUID(),clinicalDept.getUid());
                }
                
                break;
            case "assetDept":
                break;
        }
        checkSaveable();
    }
	public void onAssetChange(String idx) {
        AssetInfo tempAsset = null;
        switch (idx) {
            case "asset":
            	tempAsset = (AssetInfo) selectedObjectMap.get("asset");
                if(type==0){
                	if(tempAsset!=null){
                		editConfig.setAssetName(tempAsset.getName());
                        editConfig.setAssetInfo(tempAsset);
                	}
                	else{
                		editConfig.setAssetName("");
                        editConfig.setAssetInfo(null);
                	}
                }
                else if(type==1){
                	if(tempAsset!=null){
                		selected.setAssetName(tempAsset.getName());
                	}
                	else{
                		selected.setAssetName("");
                	}
                }
                break;
            case "assetDept":
                break;
        }
        checkSaveable();
    }
	
	public List<AssetInfo> autoCompleteAssetName(String query){
		return autoCompleteService.completeAssetInfoList(query, assetInfoList);
	}
	@Override
	protected String getActionMessage(String actionName) {
		// TODO Auto-generated method stub
		return "设备绩效配置"+" "+WebUtil.getMessage(actionName);
	}

	public Map<String, Object> getSelectedObjectMap() {
		return selectedObjectMap;
	}

	public void setSelectedObjectMap(Map<String, Object> selectedObjectMap) {
		this.selectedObjectMap = selectedObjectMap;
	}

	public List<OrgInfo> getClinicalDeptList() {
		return clinicalDeptList;
	}

	public void setClinicalDeptList(List<OrgInfo> clinicalDeptList) {
		this.clinicalDeptList = clinicalDeptList;
	}

	public List<SmallAssetPerformanceConfig> getListNewConfig() {
		return listNewConfig;
	}

	public void setListNewConfig(List<SmallAssetPerformanceConfig> listNewConfig) {
		this.listNewConfig = listNewConfig;
	}

	public SmallAssetPerformanceConfig getEditConfig() {
		return editConfig;
	}

	public void setEditConfig(SmallAssetPerformanceConfig editConfig) {
		this.editConfig = editConfig;
	}

	public boolean isBlnSaveable() {
		return blnSaveable;
	}

	public void setBlnSaveable(boolean blnSaveable) {
		this.blnSaveable = blnSaveable;
	}

	public List<AssetInfo> getAssetInfoList() {
		return assetInfoList;
	}

	public void setAssetInfoList(List<AssetInfo> assetInfoList) {
		this.assetInfoList = assetInfoList;
	}
    
}

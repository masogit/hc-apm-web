package com.ge.apm.view.uaa;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class TenantInfoController extends JpaCRUDController<TenantInfo> {
	private static final Logger logger = Logger.getLogger(TenantInfoController.class);
    TenantInfoRepository dao = null;
    UserAccountRepository userAccountRepository;
    UserAccount loginUser;
    TenantInfoConfig tenantConfig;
    List<String> listVersions;
    String selectedVersion;
    AssetInfoRepository assetInfoRepository;
    TableDefinitionRepository tbDao;
    boolean tempAlready=false;
    String type="";


    @Getter
    @Setter
    private boolean disableCreateTemplateBt=false; //如果编辑状态初是'通用版' ，添加模版按钮就设置为灰色

    public boolean createOrEdit(){
        if("create".equalsIgnoreCase(type)){
            return false;
        }else{
            return true;
        }
    }

    @Override
    protected void init() {

        filterBySite = false;
        loginUser = UserContextService.getCurrentUserAccount();
        dao = WebUtil.getBean(TenantInfoRepository.class);
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
        assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
        tbDao=WebUtil.getBean(TableDefinitionRepository.class);
    }

    @Override
    protected TenantInfoRepository getDAO() {
        return dao;
    }

    @Override
    protected Page<TenantInfo> loadData(PageRequest pageRequest) {
        selected = null;

        if (this.searchFilters == null) {
            return dao.findAll(pageRequest);
        } else {
            return dao.findBySearchFilter(this.searchFilters, pageRequest);
        }
    }
    public static File[] getResourceFolderFiles(String folder) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();

    }
    private HashMap<String,String> fhMap = new HashMap<String,String>();
    public void loadTemplateFile(){
        String rstr="";
        fhMap = new HashMap<String,String>();
        for (File f : getResourceFolderFiles("/templateFile/")) {
            try {
                FileInputStream in = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(in,"GBK"));
                String str = null;
                int count;
                StringBuffer xmlTemplate = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    rstr += str;
                }
                fhMap.put(f.getName(),rstr);
                rstr="";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public List<TenantInfo> getItemList() {
        return dao.getAllOrderByName();
    }


    public void prepareCreate(String type) throws InstantiationException, IllegalAccessException {
    	// TODO Auto-generated method stub
    	super.prepareCreate();
    	setDefaultValueForConfig();
    	this.type=type;
    }

    @Override
    public void prepareEdit() {
        // TODO Auto-generated method stub
        super.prepareEdit();
        parseTenantConfig();
    }

    public void prepareEdit(String type) {
    	// TODO Auto-generated method stub
    	super.prepareEdit();
    	parseTenantConfig();
        this.type=type;
        if(selectedVersion.equalsIgnoreCase("通用版")){
            isGeneralVersion=true;
            tempAlready=true;
        }else{
            isGeneralVersion=false;
            disableCreateTemplateBt=true;
        }
        tempAlready=false;
    }

    @Transactional
    @Override
    public void save() {
        if(type.equalsIgnoreCase("create"))
        {//如果租户已经创建过则退出
            List<TenantInfo> byName = dao.getByName(this.selected.getName());
            if(byName.size()>0) {
                WebUtil.addErrorMessage(byName.get(0).getName()+"租户已经创建过了");
                RequestContext.getCurrentInstance().addCallbackParam("tenantEstablished", true);
              return ;
                }
        }
        this.selected.setTenantConfig(generateTenantConfig());
    	super.save();
    }
    List<TableDefinition> tbList = new ArrayList<TableDefinition>();
    public void addTemplateFile(){
        if(tempAlready){  //该条件判断只能防止在修改租户对话框中反复'添加模版'，但是当'通用'改成'非通用' 再改回'通用'这种情况下 添加模版还是会重复添加
            WebUtil.addSuccessMessage("该租户模版已经添加");
            RequestContext.getCurrentInstance().addCallbackParam("tmplateFilled", true);
            return ;
        }else{
            if (selectedVersion.contains("通用版")) {
                //加载模版文件
                loadTemplateFile();
                //将模版插入TableDefinition, 先从数据库里查有没有该模版，没有的话就将其加入tbList to be saved
                tbList = new ArrayList<>();
                fhMap.entrySet().stream().forEach(item -> {
                    List<TableDefinition> td = tbDao.findTD(item.getKey(), selected.getUid());
                    if (td.size() == 0) {
                        TableDefinition tb = new TableDefinition();
                        tb.setTenantUID(selected.getUid());
                        tb.setInstitutionUID(selected.getUid());
                        tb.setHospitalUID(selected.getUid());
                        tb.setSiteUID(selected.getUid());
                        tb.setStatus(TableDefinition.TableDefinitionStatus.Draft.toString());
                        tb.setInstrumentKeyValues("{}");
                        tb.setCreatedDate(new Date());
                        tb.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
                        tb.setName(item.getKey());
                        tb.setSchema(item.getValue());
                        tbList.add(tb);
                    }
                });
                if (tbList.size() > 0) {
                    tbDao.save(tbList);
                }
                tbList.stream().forEach(item->item.setBaseId(item.getId()));
                tbDao.save(tbList);
            }
            tempAlready = true;
            //模版添加完后 将'添加模版'按钮变灰
           // disableCreateTemplateBt=true;
            WebUtil.addSuccessMessage("模版添加完成");
        }
    }

    @Override
    public void delete(){
        String uid = selected.getUid();
        boolean hasAssets = assetInfoRepository.findRecordByTenantUid(uid);
        if(hasAssets){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("hasAssets"));
            return;
        }
        boolean hasUsers = userAccountRepository.findRecordByTenantUid(uid);
        if(hasUsers){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            WebUtil.addErrorMessage(WebUtil.getMessage("hasUsers"));
            return;
        }
        super.delete();
    }
    
    @Override
    public String getErrorMessageForDuplicateKey(TenantInfo site){
        return String.format(WebUtil.getMessage("DuplicateSiteName"), site.getName());
    }
    
    @Override
    public String getKeyFieldNameValue(TenantInfo site){
        return WebUtil.getMessage("name")+"="+site.getName();
    }

    @Override
    public void onAfterNewObject(TenantInfo tenant, boolean isOK) {
        if(!isOK) return;
/*
        // import field code type data for this site
        try{
            String sql = "insert into i18n_message(msg_type, msg_key, value_zh, value_en, value_tw, site_id) select distinct msg_type, msg_key, value_zh, value_en, value_tw, %d as site_id from i18n_message where msg_type in (select msg_type from field_code_type)";
            NativeSqlUtil.execute(String.format(sql, site.getId()), null);
        }
        catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
*/

        // and create an default org for this site
//        String orgUID = UUID.randomUUID().toString().replace("-", "");
        OrgInfo org = new OrgInfo();
        org.setSiteId(tenant.getId());
        
        //for new UUIDs
        org.setOrgLevel(0);
        org.setOrgType(0);
        org.setUid(tenant.getUid());
        org.setTenantUID(tenant.getUid());
        org.setInstitutionUID(tenant.getUid());
        org.setHospitalUID(tenant.getUid());
        org.setSiteUID(tenant.getUid());

        org.setShortName(tenant.getName());
        org.setName(tenant.getName());
        
        OrgInfoRepository orgDao = WebUtil.getBean(OrgInfoRepository.class);
        orgDao.save(org);

        String config=generateTenantConfig();
        //如果是通用版 则要增加模版配置

        if(config.contains("通用版")){
            List<TableDefinition> tbList= new ArrayList<TableDefinition>();
            //加载模版文件
            loadTemplateFile();
            //将模版插入TableDefinition, 先从数据库里查有没有该模版，没有的话就将其加入tbList to be saved
            fhMap.entrySet().stream().forEach(item->{
                List<TableDefinition> td =tbDao.findTD(item.getKey(),tenant.getUid());
                if(td.size()==0 ) {
                    TableDefinition tb = new TableDefinition();
                    tb.setTenantUID(tenant.getUid());
                    tb.setInstitutionUID(tenant.getUid());
                    tb.setHospitalUID(tenant.getUid());
                    tb.setSiteUID(tenant.getUid());
                    tb.setStatus(TableDefinition.TableDefinitionStatus.Draft.toString());
                    tb.setInstrumentKeyValues("{}");
                    tb.setCreatedDate(new Date());
                    tb.setCreatedBy(UserContextService.getCurrentUserAccount().getUid());
                    tb.setName(item.getKey());
                    tb.setSchema(item.getValue());
                    tbList.add(tb);
                }
            });
            if (tbList.size() > 0) {
                tbDao.save(tbList);
            }
            tbList.stream().forEach(item->item.setBaseId(item.getId()));
            tbDao.save(tbList);
        }

    }

    @Transactional
    public void onAfterUpdateObject(TenantInfo tenant, boolean isOK) {
        /*如果由非通用版改成通用版，查找tabledef表(tenantUid,name)有没有存在模版，没有的话添加*/
        String config=generateTenantConfig();

        List<TableDefinition> tbList= new ArrayList<TableDefinition>();
        if(!isOK){
            return;
        }
        if(selected.getIsEnabled() != null && selected.getIsEnabled()){
            //如果超级管理员激活某个租户，则激活该租户下的所有用户
            userAccountRepository.updateMembersStatus(selected.getUid());
        }else{
            //如果超级管理员关闭某个租户，则关闭该租户下的所有用户
            userAccountRepository.deactiveTenantMembersStatus(selected.getUid());
        }
    }

    @Override
    public void onBeforeNewObject(TenantInfo tenant) {
        tenant.setIsEnabled(false);
        super.onBeforeNewObject(tenant);
    }

    @Override
    protected boolean onPersistException(Exception ex, TenantInfo site){
        if(ex.getClass().equals(DataIntegrityViolationException.class)){
            DataIntegrityViolationException exFkey = (DataIntegrityViolationException) ex;
            if(exFkey.getRootCause().getMessage().toLowerCase().contains("foreign key constraint ")){
                String errMessage = WebUtil.getMessage("ForeighKeyErrorWithSiteId");
                WebUtil.addErrorMessage(errMessage);

                return true;
            }
        }
                
        return false;
    }

    public void onBeforeSave(TenantInfo tenantInfo) {
        if(tenantInfo.getIsEnabled() && tenantInfo.getActiveTime() == null){
            tenantInfo.setActiveTime(new Date());
            tenantInfo.setActiveType(0);
            tenantInfo.setActiveUserId(loginUser.getId());
        }
        if(!tenantInfo.getIsEnabled() && tenantInfo.getActiveTime() != null){
            tenantInfo.setActiveTime(null);
        }

    }
    
    public void onWorkScheduleChange(){
    	if(tenantConfig.getBlnWorkSchedule()){
    		tenantConfig.setBlnWorkOrderMgmtWithoutWorkSchedule(true);
    	}
    }
    public void onWorkOrderMgmtWithoutWorkScheduleChange(){
    	if(tenantConfig.getBlnWorkOrderMgmtWithoutWorkSchedule()==false){
    		tenantConfig.setBlnWorkSchedule(false);
    	}
    }
    public boolean isGeneralVersion;
    public void onVersionChange(){
        if (this.selectedVersion.equalsIgnoreCase("通用版")) {
            disableCreateTemplateBt = false;
        }else{
            disableCreateTemplateBt=true;
        }
    	tenantConfig.setValueWithVersion(this.selectedVersion);

    }
    
    private void setDefaultValueForConfig(){
    	this.selectedVersion=TenantInfoConfig.VERSION_COMMON;
    	this.listVersions=TenantInfoConfig.getVersions();
    	tenantConfig=new TenantInfoConfig();
    	tenantConfig.setValueWithVersion(this.selectedVersion);
    }
    private void parseTenantConfig(){
    	String configJsonStr=this.selected.getTenantConfig();
    	this.listVersions=TenantInfoConfig.getVersions();
    	tenantConfig=new TenantInfoConfig();
    	if(StringUtils.isNotEmpty(configJsonStr)){
    		JSONObject jsonObj=JSONObject.parseObject(configJsonStr);
    		try {
				this.selectedVersion=jsonObj.getString("edition");
				JSONArray jsonArr=jsonObj.getJSONArray("menuList");
				for(int i=0;i<jsonArr.size();++i){
					tenantConfig.putStr(jsonArr.getString(i));
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.error(e.getMessage());
			}
    	}
    }
    
    private String generateTenantConfig(){
    	JSONObject jsonObj=new JSONObject();
    	JSONArray jsonArray=new JSONArray(tenantConfig.getListStr());
    	jsonObj.put("edition", this.selectedVersion);
    	jsonObj.put("menuList", jsonArray);
    	return jsonObj.toString();
    }

	public TenantInfoConfig getTenantConfig() {
		return tenantConfig;
	}

	public void setTenantConfig(TenantInfoConfig tenantConfig) {
		this.tenantConfig = tenantConfig;
	}

	public List<String> getListVersions() {
		return listVersions;
	}

	public void setListVersions(List<String> listVersions) {
		this.listVersions = listVersions;
	}

	public String getSelectedVersion() {
		return selectedVersion;
	}

	public void setSelectedVersion(String selectedVersion) {
		this.selectedVersion = selectedVersion;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

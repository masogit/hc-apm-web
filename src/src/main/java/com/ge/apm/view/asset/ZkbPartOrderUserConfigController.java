package com.ge.apm.view.asset;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.ZkbFePartOrderUserConfigRepository;
import com.ge.apm.dao.ZkbPartOrderRepository;
import com.ge.apm.dao.ZkbPartOrderUserConfigRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.zkb.ZkbFePartOrderService;
import com.ge.apm.service.zkb.ZkbPartOrderUserConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author 503202755 2020/9/25
 */
@ManagedBean
@ViewScoped
public class ZkbPartOrderUserConfigController extends GenericCRUDUUIDController<ZkbPartOrderUserConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbPartOrderUserConfigController.class);

    /**
     * service
     */
    private ZkbPartOrderUserConfigService zkbPartOrderUserConfigService;

    /**
     * dao
     */
    private ZkbPartOrderUserConfigRepository zkbPartOrderUserConfigRepository;

    private ZkbPartOrderRepository zkbPartOrderRepository;

    /**
     * 目录关系分页数据
     */
    private Page<ZkbPartOrderUserConfig> zkbPartOrderUserConfigPage;

    /**
     * 导入文件
     */
    private UploadedFile file;

    /**
     * 导出文件
     */
    private StreamedContent exportFile;

    /**
     * 错误列表
     */
    private List<Map<String, Object>> errorList;

    private ZkbPartOrderUserConfig backUpUser = new ZkbPartOrderUserConfig();
    private List<ZkbPartOrderUserConfig> backUpList = new ArrayList<>();

    /**
     * 当前登录用户信息service
     */
    private UserAccount currentUser;
    /**
     * 当前用户登录名
     */
    private String loginName = "";

    private UserAccountRepository userAccountRepository;

    private List<String> configTypeList = Arrays.asList("OrderExtraPending","OrderPending","CommercialPending","ClosePending", "OperationsTeam");

    /**
     * 是否同步创建或更新GeFeConfig用户角色
     */
    private Boolean synGeFeConfigFlag = false;
    /**
     * 初始化
     */
    @Override
    protected void init() {
        super.init();
        zkbPartOrderUserConfigService = WebUtil.getBean(ZkbPartOrderUserConfigService.class);
        zkbPartOrderUserConfigRepository = WebUtil.getBean(ZkbPartOrderUserConfigRepository.class);
        zkbPartOrderRepository=WebUtil.getBean(ZkbPartOrderRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        this.loginName=currentUser.getLoginName();
        userAccountRepository = WebUtil.getBean(UserAccountRepository.class);

    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        super.prepareCreate();
        //initBackUpList();
        if(selected != null){
            if(CollectionUtils.isNotEmpty(backUpList))
            {
                for(ZkbPartOrderUserConfig c : backUpList){
                    if(c.getId().equals(selected.getId())){
                        backUpList.remove(c);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        initBackUpList(selected.getConfigType());
        if(selected != null){
            for(ZkbPartOrderUserConfig c : backUpList){
                if(selected.getId().equals(c.getId())){
                    backUpList.remove(c);
                    break;
                }
            }
        }
        if(StringUtils.isNotEmpty(selected.getUsBackUpId())){
            backUpUser = zkbPartOrderUserConfigRepository.findById(selected.getUsBackUpId());
        }else {
            backUpUser =new ZkbPartOrderUserConfig();
        }
    }

    private void initBackUpList(String configType)
    {
        if(this.loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
        {
            backUpList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetType(configType,"US");
        }else
        {
            backUpList = zkbPartOrderUserConfigRepository.findByConfigTypeAndAssetTypeAndLoginNameNotLike(configType,"US",ZkbFePartOrderService.testLoginNameStartWith+"%");
        }
    }

    @Override
    protected GenericRepositoryUUID<ZkbPartOrderUserConfig> getDAO() {
        return this.zkbPartOrderUserConfigRepository;
    }

    /**
     * 列表查询数据
     * @param pageRequest
     * @return
     */
    @Override
    protected Page<ZkbPartOrderUserConfig> loadData(PageRequest pageRequest) {
        List<SearchFilter> searchFilters = this.searchFilters;
        if (searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        if(!this.loginName.startsWith(ZkbFePartOrderService.testLoginNameStartWith))
        {
            searchFilters.add(new SearchFilter("testStatus", SearchFilter.Operator.EQ, "f"));
        }
        zkbPartOrderUserConfigPage=zkbPartOrderUserConfigRepository.findBySearchFilter(searchFilters, pageRequest);
        return zkbPartOrderUserConfigPage;
    }

    /**
     * 文件上传
     * @param event  文件流
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));
            int maxRowNum = ZkbPartOrderUserConfigService.EXCEL_USER_CONFIG_HEADER_ROW + ZkbPartOrderUserConfigService.EXCEL_USER_CONFIG_IMPORT_MAX_ROW;
            if(doc.exceedMaxRowNum(ZkbPartOrderUserConfigService.SHEET_SERVICE_KEY_USER_CONFIG_INDEX, maxRowNum)){
                WebUtil.addErrorMessage(WebUtil.getMessage("MaximumFiveThDataLinesInTheExcel"));
                return;
            }
            List<Map<String, Object>> importUserConfigMapList = zkbPartOrderUserConfigService.getConfigMapFromTemplate(doc);
            zkbPartOrderUserConfigService.importData(importUserConfigMapList);
            errorList = zkbPartOrderUserConfigService.filterErrorRecords(importUserConfigMapList);
            if(!errorList.isEmpty()){
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, WebUtil.getMessage("ImportCompletedNoteFailureRecord"), WebUtil.getMessage("ThereAreATotalOf")+errorList.size()+WebUtil.getMessage("RecordsOfFailure"));
                FacesContext.getCurrentInstance().addMessage(null, message);
            }else {
                WebUtil.addSuccessMessage(WebUtil.getMessage("ImportComplete"));
            }
        } catch (IOException ex) {
            WebUtil.addErrorMessage("File can not be read!");
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            WebUtil.addErrorMessage(WebUtil.getMessage("ParsingImportFileError") +"--" +  e.getMessage());
            java.util.logging.Logger.getLogger(ContractImportController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    public void configTypeChange(){
        if(ZkbPartOrderStep.PartOrderStep.TsePending.toString().equals(selected.getConfigType()) || ZkbPartOrderStep.PartOrderStep.TechnicalPending.toString().equals(selected.getConfigType())){
            initBackUpList(selected.getConfigType());
        }
    }


    /**
     * 跳转导入页面
     * @return
     */
    public String importAction() {
        return "Import?faces-redirect=true";
    }

    /**
     * 文件下载
     * @return
     */
    public String getTemplateName(){
        return ZkbPartOrderUserConfigService.EXCEL_TEMPLATE_IMPORT_NAME;
    }

    /**
     * 取消
     */
    public void cancel() {
        this.selected = null;
    }

    @Override
    public void save() {
        boolean isNull = this.selected.isNull();
        if(isNull){
            WebUtil.addErrorMessage("保存失败,不能保存空数据!");
            return;
        }

        UserAccount userAccount = userAccountRepository.getByLoginName(this.selected.getLoginName());
        if(userAccount==null) {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameNotExist"));
            return;
        }

        if(this.selected.getLoginName()!=null&&this.selected.getLoginName().startsWith(ZkbFePartOrderService.testLoginNameStartWith))
        {
            this.selected.setTestStatus("t");
        }else
            {
                this.selected.setTestStatus("f");
            }
        if(!("OrderExtraPending".equals(this.selected.getConfigType())||"OrderPending".equals(this.selected.getConfigType())))
        {
            List<ZkbPartOrderUserConfig> zkbPartOrderUserConfigList = null;
            if(StringUtils.isNotBlank(this.selected.getId())){
                zkbPartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findByLoginNameAndIdNot(this.selected.getLoginName(),this.selected.getId());
            }else
            {
                zkbPartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findByLoginName(this.selected.getLoginName());
            }
            if(CollectionUtils.isNotEmpty(zkbPartOrderUserConfigList))
            {
                //WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_loginNameError"));
                //return;
            }
        }
        if(backUpUser != null && StringUtils.isNotEmpty(backUpUser.getId())){
            selected.setUsBackUpId(backUpUser.getId());
            selected.setUsBackUpName(backUpUser.getName());
        }
        super.save();
        if(synGeFeConfigFlag){
            SynCreateFePartOrderUser(selected, userAccount.getTenantUID());
        }
        this.selected = null;
        this.backUpUser = null;
    }

    /**
     * 400开单、400下单、GE Admin、Commercial4个角色自动同步信息到GE FE User Config中
     * 注意：GE FE User Config中已存在用户，则不能在新增，不能修改用户配置的角色，其他信息可修改。
     * @param zkbPartOrderUserConfig
     */
    @Transactional(rollbackFor = Exception.class)
    public void  SynCreateFePartOrderUser(ZkbPartOrderUserConfig zkbPartOrderUserConfig, String tenantUid){
        String geFeConfigType = null;
        if("OrderExtraPending".equals(zkbPartOrderUserConfig.getConfigType())||"OrderPending".equals(zkbPartOrderUserConfig.getConfigType())){
            if(selected.getOrderType() == null || !selected.getOrderType().equals("Probe")){
                geFeConfigType = "FourHundredsTeam";
            }else{
                geFeConfigType = "FourHundredsTeam_Probe";
            }
        }
        if(zkbPartOrderUserConfig.getConfigType().equals("CommercialPending")){
            geFeConfigType = "Commercial";
        }else if(zkbPartOrderUserConfig.getConfigType().equals("ClosePending")){
            geFeConfigType = "GEAdmin";
        }else if(zkbPartOrderUserConfig.getConfigType().equals("OperationsTeam")){
            geFeConfigType = "OperationsTeam";
        }
        if(geFeConfigType != null){
            List<ZkbFePartOrderUserConfig> list = null;
            ZkbFePartOrderUserConfigRepository zkbFePartOrderUserConfigRepository = WebUtil.getBean(ZkbFePartOrderUserConfigRepository.class);
            list = zkbFePartOrderUserConfigRepository.findByLoginName(zkbPartOrderUserConfig.getLoginName());
            ZkbFePartOrderUserConfig zkbFePartOrderUserConfig = null;
            if(CollectionUtils.isEmpty(list)){
                zkbFePartOrderUserConfig = new ZkbFePartOrderUserConfig();
            }else{
                zkbFePartOrderUserConfig = list.get(0);
                //GE FE User Config中已存在用户，不能修改用户配置的角色，其他信息可修改。
                if(!zkbFePartOrderUserConfig.getConfigType().equals(geFeConfigType)){
                   WebUtil.addErrorMessage("此用户在FE USER CONFIG中已有其他角色："+WebUtil.getMessage(zkbFePartOrderUserConfig.getConfigType()) +"，不能修改其信息。");
                   return;
                }
            }
            zkbFePartOrderUserConfig.setLoginName(zkbPartOrderUserConfig.getLoginName());
            zkbFePartOrderUserConfig.setName(zkbPartOrderUserConfig.getName());
            zkbFePartOrderUserConfig.setEmail(zkbPartOrderUserConfig.getEmail());
            zkbFePartOrderUserConfig.setPhone(zkbPartOrderUserConfig.getPhone());
//            zkbFePartOrderUserConfig.setSso(zkbPartOrderUserConfig.getSso());
            zkbFePartOrderUserConfig.setConfigType(geFeConfigType);
            zkbFePartOrderUserConfig.setIsVacation(zkbPartOrderUserConfig.getIsVacation());
            zkbFePartOrderUserConfig.setRemark("本用户由《USER CONFIG配置》页面同步创建");
            zkbFePartOrderUserConfig.setTenantUid(tenantUid);
            zkbFePartOrderUserConfigRepository.save(zkbFePartOrderUserConfig);
        }
    }

    public Boolean isGeFeConfigType(){
        if(selected != null && selected.getConfigType() != null){
            if(configTypeList.contains(selected.getConfigType())){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public List<ZkbPartOrderUserConfig> autoCompleteBackUpList(String query){
        List<ZkbPartOrderUserConfig> res = backUpList.stream().filter(item -> Strings.isNullOrEmpty(query) || item.getName().contains(query)).collect(Collectors.toList());
        return res;
    }

    public void exportAll(){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<ZkbPartOrderUserConfig> zkbFePartOrderUserConfigList = this.zkbPartOrderUserConfigRepository.findBySearchFilter(this.searchFilters,sort);
        try {
            InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(ZkbPartOrderUserConfigService.USER_CONFIG_PATH+ZkbPartOrderUserConfigService.EXCEL_TEMPLATE_NAME);
            ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
            List<Map<String, Object>> zkbPartOrderList = this.zkbPartOrderUserConfigService.createExportPartOrderUserConfigData(zkbFePartOrderUserConfigList);
            zkbPartOrderUserConfigService.exportData(doc, zkbPartOrderList);
            exportFile = new DefaultStreamedContent(doc.getStream(), "application/vnd.ms-excel", ZkbPartOrderUserConfigService.EXCEL_TEMPLATE_NAME);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ZkbPartOrderUserConfigController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void delete() {
        List<ZkbPartOrder> zkbPartOrderList = this.zkbPartOrderRepository.findByOnline(this.selected.getId());
        if(CollectionUtils.isNotEmpty(zkbPartOrderList))
        {
            WebUtil.addErrorMessage(WebUtil.getMessage("zkbKeyUserConfig_deleteError"));
            return;
        }
        super.delete();
    }

    public ZkbPartOrderUserConfig getBackUpUser() {
        return backUpUser;
    }

    public void setBackUpUser(ZkbPartOrderUserConfig backUpUser) {
        this.backUpUser = backUpUser;
    }

    public List<ZkbPartOrderUserConfig> getBackUpList() {
        return backUpList;
    }

    public void setBackUpList(List<ZkbPartOrderUserConfig> backUpList) {
        this.backUpList = backUpList;
    }

    public List<Map<String, Object>> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<Map<String, Object>> errorList) {
        this.errorList = errorList;
    }

    public StreamedContent getExportFile() {
        return exportFile;
    }

    public void setExportFile(StreamedContent exportFile) {
        this.exportFile = exportFile;
    }

    public Boolean getSynGeFeConfigFlag() {
        return synGeFeConfigFlag;
    }

    public void setSynGeFeConfigFlag(Boolean synGeFeConfigFlag) {
        this.synGeFeConfigFlag = synGeFeConfigFlag;
    }
}

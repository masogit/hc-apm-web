package com.ge.apm.view;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.I18nTypeManageRepository;
import com.ge.apm.dao.I18nTypeManageTenantRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.service.i18n.I18nTypeManageService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.WrapperSearchFilterUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.service.DbMessageSource;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class I18nTypeManageController extends GenericCRUDUUIDController<I18nTypeManage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nTypeManageController.class);
    private I18nMessageRepository i18nMessageRepository;
    private UserAccount currentUser;
    private I18nTypeManageRepository dao;
    private TenantInfoRepository tenantInfoRepository;
    private ConfigCacheService configCacheService;
    private I18nTypeManageTenantRepository typeManageTenantRepository;
    private I18nTypeManageService i18nTypeManageService;
    private TenantInfo selectTenant;
    private Integer selectTenantId;
    private List<I18nTypeManageTenant> typeManageTenants;

    private String selectActionName;

    private I18nMessage selectedI18n;

    private boolean supperAdmin;

    private UploadedFile file;

    @Override
    protected void init() {
        dao = WebUtil.getBean(I18nTypeManageRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        typeManageTenantRepository = WebUtil.getBean(I18nTypeManageTenantRepository.class);
        i18nTypeManageService = WebUtil.getBean(I18nTypeManageService.class);
        selectTenant = tenantInfoRepository.findByuid(currentUser.getTenantUID());
        selectTenantId = selectTenant.getId();
        this.searchFilters = new ArrayList<>();
        supperAdmin = currentUser.getIsSuperAdmin();
        initI18nTypeManageTenant();
    }

    @Override
    protected GenericRepositoryUUID<I18nTypeManage> getDAO() {
        return dao;
    }

    @Override
    protected Page<I18nTypeManage> doLoadData(PageRequest pageRequest) {
        if(!supperAdmin){
            initParameter();
        }
        Wrapper<I18nTypeManage>  wrapper = i18nTypeManageService.buildDefaultFilters(searchFilters);
        int totalRowCount = i18nTypeManageService.getTotalRowCount(wrapper,selectTenant.getUid());
        List<I18nTypeManage> list = i18nTypeManageService.loadI18nTypes(pageRequest, wrapper,selectTenant.getUid());
        Page<I18nTypeManage> result=null;
        if (list== null) {
            result=new PageImpl(new ArrayList(),pageRequest,totalRowCount);
        } else {
            result= new PageImpl(list,pageRequest,totalRowCount);
        }
        searchFilters.clear();
        return result;
    }
    private void initParameter(){
        updateSearchFilter(new SearchFilter("admin_edit", SearchFilter.Operator.EQ,Boolean.TRUE));

    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selectedI18n = null;
        this.file = null;
        prepareEditInit(this.selected,selectTenantId);
    }

    public void changeSelectTenant(){
        if(selectTenantId != null && selectTenantId != -1){
            selectTenant = tenantInfoRepository.findById(selectTenantId);
            initI18nTypeManageTenant();
        }
    }
    private void prepareEditInit(I18nTypeManage selected,Integer siteId){
        String msgType = selected.getMsgType();
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("msgType", SearchFilter.Operator.EQ,msgType));
        searchFilters.add(new SearchFilter("siteId", SearchFilter.Operator.IN, Arrays.asList(-1,siteId)));
        List<I18nMessage> i18nMessageList = i18nMessageRepository.findBySearchFilter(searchFilters);
        Map<Integer, List<I18nMessage>> listMap = i18nMessageList.stream().collect(Collectors.groupingBy(I18nMessage::getSiteId));
        boolean defaultValue = true;
        boolean alone = false;
        List<I18nMessage> defaultMsgList = listMap.get(-1); // 获取默认值
        if(CollectionUtils.isNotEmpty(defaultMsgList)){
            selected.setDefaultMsgList(defaultMsgList);
            selected.setI18nMessageList(copy(defaultMsgList));
            selected.setDefaultValue(defaultValue);
        }
        List<I18nMessage> msgList = listMap.get(siteId); // 获取当前租户
        if(CollectionUtils.isNotEmpty(msgList)){
            I18nMessage i18nMessage = msgList.get(0);
            defaultValue =Integer.valueOf(-1).equals(i18nMessage.getSiteId());
            selected.setDefaultValue(defaultValue);
            selected.setI18nMessageList(msgList);
            if(CollectionUtils.isEmpty(selected.getDefaultMsgList())){
                selected.setDefaultMsgList(copy(msgList));
            }
        }

        if(CollectionUtils.isEmpty(selected.getDefaultMsgList()) && CollectionUtils.isEmpty(selected.getI18nMessageList())){
            searchFilters.clear();
            searchFilters.add(new SearchFilter("msgType", SearchFilter.Operator.EQ,msgType));
            i18nMessageList = i18nMessageRepository.findBySearchFilter(searchFilters);
            selected.setDefaultMsgList(i18nMessageList);
            selected.setI18nMessageList(copy(i18nMessageList));
        }
        alone = CollectionUtils.isEmpty(defaultMsgList);
        selected.setAlone(alone);
        LOGGER.info("msgType:{},siteId:{},default:{},alone:{}",msgType,siteId,defaultValue,alone);
    }
    private List<I18nMessage> copy(List<I18nMessage> source){
        return source.stream().map(item -> {
            I18nMessage i18nMessage = new I18nMessage();
            BeanUtils.copyProperties(item, i18nMessage);
            return i18nMessage;
        }).collect(Collectors.toList());
    }
    public void editI18nConfig(){
        LOGGER.info("editI18nConfig:{}", JSON.toJSONString(selectedI18n));
    }
    public void prepareAddI18n(){
        //TODO
        this.selectActionName="Created";
        selectedI18n = new I18nMessage();
        selectedI18n.setMsgType(selected.getMsgType());
        selected.getI18nMessageList().add(selectedI18n);
        LOGGER.info("prepareAddI18n:{}", JSON.toJSONString(selectedI18n));
    }
    public void prepareEditI18n(){
        //TODO
        this.selectActionName="Edit";
        LOGGER.info("prepareEditI18n:{}", JSON.toJSONString(selectedI18n));
    }
    public void deleteI18n(){
        this.selectActionName="Deleted";
        List<I18nMessage> i18nMessageList = selected.getI18nMessageList().stream().filter(item->!item.getMsgKey().equals(selectedI18n.getMsgKey())).collect(Collectors.toList());
        selected.setI18nMessageList(i18nMessageList);
        selectedI18n = null;
        LOGGER.info("deleteI18n:{}", JSON.toJSONString(selectedI18n));
    }
    public void  reset(){
        try {
            selected.setI18nMessageList(ListUtils.newArrayList());
            LOGGER.info("reset:{},",selected.getMsgType());
            saveConfig();
            LOGGER.info("end reset:{}",selected.getMsgType());
            WebUtil.addSuccessMessage("恢复默认值成功！");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("恢复默认值失败！");
        }
    }
    public void saveConfig(){
        try {
            LOGGER.info("saveConfig:{}",selected.getMsgType());
            doSaveI18nMessage();
            resetCache();
            LOGGER.info("end saveConfig:{}",selected.getMsgType());
            WebUtil.addSuccessMessage("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            WebUtil.addErrorMessage("保存失败");
        }
    }
    public void doSaveI18nTypeMessageTenant(){
        String id = selected.getId();
        String uid = selectTenant.getUid();
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("typeManageId", SearchFilter.Operator.EQ,id));
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,uid));
        List<I18nTypeManageTenant> i18nTypeManageTenants = typeManageTenantRepository.findBySearchFilter(searchFilters);
        if(CollectionUtils.isNotEmpty(i18nTypeManageTenants)){
            I18nTypeManageTenant i18nTypeManageTenant = i18nTypeManageTenants.get(0);
            i18nTypeManageTenant.setType(selected.getType());
            i18nTypeManageTenant.setAdminEdit(selected.getAdminEdit());
            typeManageTenantRepository.save(i18nTypeManageTenant);
        }else{
            I18nTypeManageTenant i18nTypeManageTenant = new I18nTypeManageTenant();
            i18nTypeManageTenant.setTenantId(selectTenant.getId());
            i18nTypeManageTenant.setType(selected.getType());
            i18nTypeManageTenant.setAdminEdit(selected.getAdminEdit());
            i18nTypeManageTenant.setTypeManageId(selected.getId());
            i18nTypeManageTenant.setTenantUid(selectTenant.getUid());
            typeManageTenantRepository.save(i18nTypeManageTenant);
        }
        initI18nTypeManageTenant();
    }
    public void doSaveI18nMessage(){
        List<I18nMessage> i18nMessageList = selected.getI18nMessageList();
        List<I18nMessage> defaultMsgList = selected.getDefaultMsgList();
        boolean b = i18nMessageList.stream().anyMatch(item -> {
            if(item.getId() == null) return Boolean.TRUE;
            Optional<I18nMessage> first = defaultMsgList.stream().filter(deItem -> deItem.getMsgKey().equals(item.getMsgKey())).findFirst();
            if (first.isPresent()) {
                I18nMessage i18nMessage = first.get();
                return !i18nEquals(i18nMessage, item);
            }
            return Boolean.TRUE;
        });
        if(b || i18nMessageList.size() != defaultMsgList.size()) {
            i18nMessageList.stream().forEach(item->{
                Integer siteId = item.getSiteId();
                if(siteId == null || Integer.valueOf(-1).equals(siteId)){
                    item.setId(null);
                    item.setSiteId(selectTenantId);
                }
            });
            if(CollectionUtils.isNotEmpty(i18nMessageList)){
                i18nMessageRepository.save(i18nMessageList);
            }
        }
        List<I18nMessage> dbI18nMessageList = i18nMessageRepository.getByMsgTypeAndSiteId(selected.getMsgType(), selectTenantId);
        List<String> msgKeyList = i18nMessageList.stream().map(I18nMessage::getMsgKey).collect(Collectors.toList());
        List<I18nMessage> deleteI18nMessageList = dbI18nMessageList.stream().filter(item -> !msgKeyList.contains(item.getMsgKey())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(deleteI18nMessageList)){
            i18nMessageRepository.delete(deleteI18nMessageList);
        }
        dao.save(selected);
        doSaveI18nTypeMessageTenant();
    }
    public void uploadDataFile(FileUploadEvent event) {
        // 配置键,名称-中文,名称-英文,名称-繁体
        List<I18nMessage> dbI18nMessageList = i18nMessageRepository.getByMsgTypeAndSiteId(selected.getMsgType(), selectTenantId);
        this.file = event.getFile();
        try(ExcelDocument reader = new ExcelDocument(file.getInputstream(),ExcelDocument.parseTypeByFileName(file.getFileName()))){
            List<Map<String, Object>> allDataRows = reader.getAllDataRows(0,1,2);
            List<I18nMessage> dataList = allDataRows.stream().map(item -> {
                I18nMessage i18nMessage = new I18nMessage();
                i18nMessage.setMsgType(selected.getMsgType());
                i18nMessage.setSiteId(selectTenantId);
                ExcelDocument.ValueWrapper key = (ExcelDocument.ValueWrapper) item.get("配置键");
                if (Objects.nonNull(key) && Objects.nonNull(key.getRawValue())) {
                    i18nMessage.setMsgKey(key.getRawValue());
                }
                ExcelDocument.ValueWrapper zh = (ExcelDocument.ValueWrapper) item.get("名称-中文");
                if (Objects.nonNull(zh) && Objects.nonNull(zh.getRawValue())) {
                    i18nMessage.setValueZh(zh.getRawValue());
                }
                ExcelDocument.ValueWrapper en = (ExcelDocument.ValueWrapper) item.get("名称-英文");
                if (Objects.nonNull(en) && Objects.nonNull(en.getRawValue())) {
                    i18nMessage.setValueEn(en.getRawValue());
                }
                ExcelDocument.ValueWrapper tw = (ExcelDocument.ValueWrapper) item.get("名称-繁体");
                if (Objects.nonNull(tw) && Objects.nonNull(tw.getRawValue())) {
                    i18nMessage.setValueTw(tw.getRawValue());
                }
                return i18nMessage;
            }).collect(Collectors.toList());
            List<I18nMessage> msgKeyNull = dataList.stream().filter(item -> Objects.isNull(item.getMsgKey())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(msgKeyNull)){
                WebUtil.addErrorMessage("上传失败,配置键不能为空");
                return;
            }
            if(CollectionUtils.isNotEmpty(dbI18nMessageList)){
                dataList.stream().forEach(item-> {
                    Optional<I18nMessage> first = dbI18nMessageList.stream().filter(i18n -> item.getMsgKey().equals(i18n.getMsgKey())).findFirst();
                    if(first.isPresent()){
                        I18nMessage i18nMessage = first.get();
                        item.setId(i18nMessage.getId());
                    }
                });
            }
            selected.setI18nMessageList(dataList);
            WebUtil.addErrorMessage("上传成功,列表已更新");
        }catch (Exception e){
           e.printStackTrace();
            WebUtil.addErrorMessage("上传失败");
        }

    }
    public void resetCache(){
        DbMessageSource.reLoadMessages();
        configCacheService.cleanCacheNames();
    }
    private boolean i18nEquals(I18nMessage o1,I18nMessage o2){
        StringBuilder stringBuilder_o1 = new StringBuilder();
        String o1Str = stringBuilder_o1.append(o1.getId()).append(o1.getMsgType()).append(o1.getMsgKey()).append(o1.getValueZh()).append(o1.getValueEn()).append(o1.getValueTw()).toString();
        StringBuilder stringBuilder_o2 = new StringBuilder();
        String o2Str = stringBuilder_o2.append(o2.getId()).append(o2.getMsgType()).append(o2.getMsgKey()).append(o2.getValueZh()).append(o2.getValueEn()).append(o2.getValueTw()).toString();
        return o1Str.equals(o2Str);
    }

    public void initI18nTypeManageTenant(){
        typeManageTenants = new ArrayList<>();
        String uid = selectTenant.getUid();
        List<SearchFilter> searchFilters = new ArrayList<>();
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ,uid));
        typeManageTenants = typeManageTenantRepository.findBySearchFilter(searchFilters);
    }


    public TenantInfo getSelectTenant() {
        return selectTenant;
    }

    public void setSelectTenant(TenantInfo selectTenant) {
        this.selectTenant = selectTenant;
    }

    public Integer getSelectTenantId() {
        return selectTenantId;
    }

    public void setSelectTenantId(Integer selectTenantId) {
        this.selectTenantId = selectTenantId;
    }

    public I18nMessage getSelectedI18n() {
        return selectedI18n;
    }

    public void setSelectedI18n(I18nMessage selectedI18n) {
        this.selectedI18n = selectedI18n;
    }

    public String getSelectActionName() {
        return selectActionName;
    }

    public boolean isSupperAdmin() {
        return supperAdmin;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}

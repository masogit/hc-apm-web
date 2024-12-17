package com.ge.apm.view;

import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.I18nTypeManageRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.I18nTypeManage;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.ConfigCacheService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
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
    private TenantInfo selectTenant;
    private Integer selectTenantId;

    private String selectActionName;

    private I18nMessage selectedI18n;

    @Override
    protected void init() {
        dao = WebUtil.getBean(I18nTypeManageRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
        configCacheService = WebUtil.getBean(ConfigCacheService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        selectTenant = tenantInfoRepository.findByuid(UserContextService.getCurrentUserAccount().getTenantUID());
        selectTenantId = selectTenant.getId();
        this.searchFilters = new ArrayList<>();
    }

    @Override
    protected GenericRepositoryUUID<I18nTypeManage> getDAO() {
        return dao;
    }

    @Override
    protected Page<I18nTypeManage> doLoadData(PageRequest pageRequest) {
        Page<I18nTypeManage> result = super.loadData(pageRequest);
        return result;
    }

    @Override
    public void prepareEdit() {
        super.prepareEdit();
        this.selectedI18n = null;
        prepareEditInit(this.selected,selectTenantId);
    }

    public void changeSelectTenant(){
        if(selectTenantId != null && selectTenantId != -1){
            selectTenant = tenantInfoRepository.findById(selectTenantId);
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
        selected.setI18nMessageList(ListUtils.newArrayList());
        saveConfig();
    }
    public void saveConfig(){
        logger.info("saveConfig:"+selected.getMsgType());
        doSaveI18nMessage();
        logger.info("end saveConfig:"+selected.getMsgType());
    }
    private void doSaveI18nMessage(){
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
        DbMessageSource.reLoadMessages();
        configCacheService.cleanCacheNames();
    }
    private boolean i18nEquals(I18nMessage o1,I18nMessage o2){
        StringBuilder stringBuilder_o1 = new StringBuilder();
        String o1Str = stringBuilder_o1.append(o1.getMsgType()).append(o1.getMsgKey()).append(o1.getValueZh()).append(o1.getValueEn()).append(o1.getValueTw()).toString();
        StringBuilder stringBuilder_o2 = new StringBuilder();
        String o2Str = stringBuilder_o2.append(o2.getMsgType()).append(o2.getMsgKey()).append(o2.getValueZh()).append(o2.getValueEn()).append(o1.getValueTw()).toString();
        return o1Str.equals(o2Str);
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

}

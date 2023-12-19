package com.ge.apm.view;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.i18n.I18nConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepository;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDController;
import webapp.framework.web.service.DbMessageSource;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 503266962
 */
@ManagedBean
@ViewScoped
public class I18nConfigController extends GenericCRUDController<I18nMessage> {

    private I18nMessageRepository dao;
    private TenantInfoRepository tenantInfoRepository;
    private UserContextService userContextService;
    private I18nConfigService i18nConfigService;

    private TenantInfo selectTenant;
    private Integer selectTenantId;
    private I18nMessage selectedI18n;
    private I18nMessage editI18n;
    private boolean editFlag;
    private boolean initFlag;

    private List<I18nMessage> i18nList;
    private List<I18nMessage> removeList;

    private String search;

    @Override
    protected void init() {
        dao = WebUtil.getBean(I18nMessageRepository.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        userContextService = WebUtil.getBean(UserContextService.class);
        i18nConfigService = WebUtil.getBean(I18nConfigService.class);
        selectTenant = tenantInfoRepository.findByuid(UserContextService.getCurrentUserAccount().getTenantUID());
        selectTenantId = selectTenant.getId();
    }

    @Override
    protected GenericRepository<I18nMessage> getDAO() {
        return dao;
    }

    @Override
    protected Page<I18nMessage> loadData(PageRequest pageRequest) {

        Integer pageSize=pageRequest.getPageSize();
        Integer pageNum=pageRequest.getPageNumber()==0?pageRequest.getPageNumber():pageRequest.getPageNumber()+pageRequest.getPageSize();
        Integer count = dao.getMsgTypeAndSiteIdCount("customI18n",selectTenant.getId());
        List<I18nMessage> list = dao.getMsgTypeAndSiteId("customI18n",selectTenant.getId(),pageSize,pageNum);
        if(CollectionUtils.isNotEmpty(list)){
            setShowValue(list);
        }
        Page<I18nMessage> page = new PageImpl<>(list, pageRequest, count);
        return page;
    }

    private void setShowValue(List<I18nMessage> list){
        boolean isLocale = userContextService.isLocale();
        for (I18nMessage i18nMessage : list) {
            // customI18n中，msg_key保存的是需要配置的i18n的msg_type
            List<I18nMessage> messageList = dao.getByTypeAndSiteId(i18nMessage.getMsgKey(), selectTenant.getId());

            if(CollectionUtils.isEmpty(messageList)){
                messageList = dao.getByTypeAndSiteId(i18nMessage.getMsgKey(), -1);
            }
            if(CollectionUtils.isNotEmpty(messageList)){
                StringBuilder customConfig = new StringBuilder();
                messageList.forEach(message -> {
                    if(isLocale){
                        customConfig.append(message.getValueZh()).append(",");
                    }else{
                        customConfig.append(message.getValueEn()).append(",");
                    }
                });
                i18nMessage.setCustomConfig(customConfig.substring(0,customConfig.length() - 1>30?30:customConfig.length() - 1)+"...");
            }
        }
    }

    public void prepareAddI18n() {
        editI18n = new I18nMessage();
        editFlag = false;
    }

    public void prepareEditI18n() {
        editFlag = true;
        editI18n = selectedI18n;
    }

    public void deleteI18n() {
        if(i18nConfigService.beforeDeleteCheck(this.selected.getMsgKey(),selectedI18n.getMsgKey(), this.selectTenantId)){
            // 删除前校验，是否有数据引用
            i18nList.remove(selectedI18n);
            // 如果不是首次配置，需要删除对应租户下的该项配置
            if(!initFlag){
                removeList.add(selectedI18n);
            }
        }else{
            WebUtil.addErrorMessage("该配置存在引用，无法删除!");
        }

    }

    public void prepareEdit() {
        initFlag = false;
        i18nList = dao.getByTypeAndSiteId(this.selected.getMsgKey(), this.selectTenantId);
        if(CollectionUtils.isEmpty(i18nList)){
            initFlag = true;
            i18nList = dao.getByTypeAndSiteId(this.selected.getMsgKey(), -1);
        }

        i18nList = i18nList.stream().sorted(Comparator.comparing(I18nMessage::getMsgKey)).collect(Collectors.toList());


        removeList = new ArrayList<>();
        // 重置
        selectedI18n = null;
        editI18n = null;
        super.prepareEdit();
    }

    public void addI18nConfig(){
        // 校验msg_key重复
        boolean repeat = false;
        for (I18nMessage i18nMessage : i18nList) {
            if(!editFlag && i18nMessage.getMsgKey().equals(editI18n.getMsgKey())) {
                WebUtil.addErrorMessage("键值重复，请重新设置！");
                repeat = true;
                break;
            }
        }
        if(repeat){
            RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
            return;
        }

        if(StringUtils.isEmpty(editI18n.getValueEn())){
            editI18n.setValueEn(editI18n.getValueZh());
        }
        if(StringUtils.isEmpty(editI18n.getValueTw())){
            editI18n.setValueTw(editI18n.getValueZh());
        }

        if(!editFlag){
            I18nMessage i18nMessage = new I18nMessage();
            BeanUtils.copyProperties(editI18n, i18nMessage);
            i18nMessage.setMsgType(this.selected.getMsgKey());
            i18nList.add(i18nMessage);
        }
    }

    public void saveConfig(){
        i18nList.forEach(i18nMessage -> {
            i18nMessage.setSiteId(this.selectTenantId);
            // 如果是首次配置，id设置为空，则插入
            if(initFlag){
                i18nMessage.setId(null);
            }
        });
        // TODO 逻辑优化
        i18nConfigService.deleteAndInsertI18n(i18nList, removeList);
        DbMessageSource.reLoadMessages();
    }

    public void changeSelectTenant(){
        if(selectTenantId != null && selectTenantId != -1){
            selectTenant = tenantInfoRepository.findById(selectTenantId);
        }
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

    public List<I18nMessage> getI18nList() {
        return i18nList;
    }

    public void setI18nList(List<I18nMessage> i18nList) {
        this.i18nList = i18nList;
    }

    public I18nMessage getSelectedI18n() {
        return selectedI18n;
    }

    public void setSelectedI18n(I18nMessage selectedI18n) {
        this.selectedI18n = selectedI18n;
    }

    public I18nMessage getEditI18n() {
        return editI18n;
    }

    public void setEditI18n(I18nMessage editI18n) {
        this.editI18n = editI18n;
    }

    public boolean isEditFlag() {
        return editFlag;
    }

    public void setEditFlag(boolean editFlag) {
        this.editFlag = editFlag;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}

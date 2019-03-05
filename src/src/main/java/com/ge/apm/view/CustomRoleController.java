package com.ge.apm.view;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.RoleAuthorityRepository;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.SysAuthorityRepository;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.StringUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.*;

@ManagedBean
@ViewScoped
public class CustomRoleController extends JpaCRUDController<CustomRole> {

    private CustomRoleRepository dao = null;
    private String[] authorities = new String[200];
    private String[] cards = new String[100];
    private UserAccount user;
    private I18nMessageRepository i18nMessageRepository;
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
        i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
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
    }

    @Override
    public List<CustomRole> getItemList() {
        return dao.find();
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
        List<I18nMessage> messages;
        Map<String,I18nMessage> map = new HashMap<>();
        if(authorities.length > 0 || cards.length > 0){
            List<String> param = new ArrayList<>(500);
            param.addAll(Arrays.asList(authorities)) ;
            param.addAll(Arrays.asList(cards));
            if(CollectionUtils.isNotEmpty(param)){
                messages = i18nMessageRepository.findI18ns(param);
                if(CollectionUtils.isNotEmpty(messages)){
                    for (I18nMessage i18nMessage: messages) {
                        map.put(i18nMessage.getMsgKey(),i18nMessage);
                    }
                }
            }
        }
        if(authorities.length > 0){
            StringBuilder sb1 = new StringBuilder();
            for (String authority: authorities) {
                if(map.containsKey(authority)){
                    sb1.append(map.get(authority).getValue()+",");
                }
            }
            selected.setAuthoritiesCH(removeComma(sb1));
            selected.setAuthorities( StringUtils.join(authorities,","));
        }
        if(cards.length > 0){
            StringBuilder sb2 = new StringBuilder();
            for (String card: cards) {
                if(map.containsKey(card)){
                    sb2.append(map.get(card).getValue()+",");
                }
            }
            selected.setCardsCH(removeComma(sb2));
            selected.setCards(StringUtils.join(cards,","));
        }

        dao.save(selected);
        authorities = new String[200];
        cards = new String[100];
    }

    public void prepareEdit(){
        if(selected.getId() != null){
            if(StringUtils.isNotEmpty(selected.getAuthorities())){
                authorities = selected.getAuthorities().split(",");
            }
            if(StringUtils.isNotEmpty(selected.getCards())){
                cards = selected.getCards().split(",");
            }
        }

    }

    public String removeComma(StringBuilder sb){
        String s = sb.toString();
        if(StringUtils.isEmpty(s)){
            return "";
        }
        if(s.endsWith(",")){
            return s.substring(0,s.length() - 2);
        }
        if(s.length() > 15){
            return s.substring(0,14)+"...";
        }
        return s;
    }
    public String[] getCards() {
        return cards;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public void setCards(String[] cards) {
        this.cards = cards;
    }
}
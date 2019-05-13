package com.ge.apm.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.uaa.CustomRoleService;
import com.ge.apm.view.sysutil.UserContextService;

import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

@ManagedBean
@ViewScoped
public class CustomRoleController extends JpaCRUDController<CustomRole> {

	private CustomRoleRepository dao = null;
	private String[] authorities = new String[200];
	private String[] cards = new String[100];
	private String[] menus = new String[100];
	private UserAccount user;
	private I18nMessageRepository i18nMessageRepository;
	public static List<String> keyRoles = new ArrayList<>();
	private CustomRoleService service;
	private List<I18nMessage> listI18nMenus;
	private List<I18nMessage> listI18nCards;
	private JSONArray authJSONArr;
	private Map<String,String[]> mapAuthCard=new HashMap<String,String[]>();
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
		service = WebUtil.getBean(CustomRoleService.class);
		listI18nMenus = service.getMenusByTenant(user.getTenantUID());
		listI18nCards=service.getCardsByTenant(user.getTenantUID());
		authJSONArr = service.getAuth();
	}

	@Override
	protected CustomRoleRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<CustomRole> loadData(PageRequest pageRequest) {
		List<SearchFilter> searchFilters = new ArrayList();
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, user.getTenantUID()));
		if (CollectionUtils.isNotEmpty(this.searchFilters)) {
			searchFilters.addAll(this.searchFilters);
		}
		Sort sort = new Sort(Sort.Direction.DESC, "createdDate");
		pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
		return dao.findBySearchFilter(searchFilters, pageRequest);
	}

	@Override
	public List<CustomRole> getItemList() {
		return dao.find();
	}

	/*
	 * @Override public void onBeforeSave(CustomRole role) {
	 * role.setTenantUID(user.getTenantUID());
	 * role.setInstitutionUID(user.getInstitutionUID());
	 * role.setHospitalUID(user.getHospitalUID());
	 * role.setSiteUID(user.getSiteUID()); if(role.getCreatedBy() == null){
	 * role.setCreatedBy(user.getLoginName()); } if(role.getCreatedDate() ==
	 * null){ role.setCreatedDate(new Date()); }
	 * role.setLastModifiedBy(user.getLoginName()); role.setLastModifiedDate(new
	 * Date()); }
	 */
	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.prepareCreate();
		clear();
	}

	@Override
	public void prepareEdit() {
		super.prepareEdit();
		if (selected.getId() != null) {
			if (StringUtils.isNotEmpty(selected.getAuthorities())) {
				authorities = selected.getAuthorities().split(",");
				generateAuthMap(authorities);
			}
			if (StringUtils.isNotEmpty(selected.getCards())) {
				cards = selected.getCards().split(",");
			}
			if(StringUtils.isNotEmpty(selected.getJsonAuthorities())){
				authJSONArr=JSONArray.parseArray(selected.getJsonAuthorities());
				JSONArray initArr=service.getAuth();
				for(int i=0;i<initArr.size();++i){
					JSONObject tempObj=initArr.getJSONObject(i);
					String tempKey=tempObj.getString("k");
					boolean contains=false;
					for(int j=0;j<authJSONArr.size();++j){
						JSONObject userObj=authJSONArr.getJSONObject(j);
						if(tempKey.equals(userObj.getString("k"))){
							contains=true;
							break;
						}
					}
					if(!contains){
						authJSONArr.add(tempObj);
					}
				}
			}
			if (StringUtils.isNotEmpty(selected.getMenus())) {
				menus = selected.getMenus().split(",");
			}
		}
	}
	
	private void generateAuthMap(String[] auths){
		Map<String,List<String>> tempMap=new HashMap<String,List<String>>();
		for(int i=0;i<auths.length;++i){
			String temp=auths[i];
			if(StringUtils.isNotEmpty(temp)){
				String[] arr=temp.split("\\.");
				String key=arr[0];
				if(tempMap.containsKey(key)){
					tempMap.get(key).add(temp);
				}
				else{
					List<String> list=new ArrayList<String>();
					list.add(temp);
					tempMap.put(key, list);
				}
			}
		}
		if(tempMap.size()>0){
			Iterator<String> it=tempMap.keySet().iterator();
			while(it.hasNext()){
				String key=it.next();
				String[] strArr=new String[1];
				mapAuthCard.put(key, tempMap.get(key).toArray(strArr));
			}
		}
	}
	
	private String[] parseAuthMap(){
		List<String> listAll=new ArrayList<String>();
		Iterator<String> it=mapAuthCard.keySet().iterator();
		while(it.hasNext()){
			String key=it.next();
			String[]  list=mapAuthCard.get(key);
			for(int i=0;i<list.length;++i){
				listAll.add(list[i]);
			}
		}
		String[] strArr=new String[0];
		return listAll.toArray(strArr);
	}
	
	private String parseMenus(String[] menus){
		StringBuilder sb=new StringBuilder();
		Set<String> set=new HashSet<String>();
		for(int i=0;i<menus.length;++i){
			set.add(menus[i]);
			int index=menus[i].indexOf(".");
			if(index>0){
				set.add(menus[i].substring(0, index));
			}
		}
		for(String str:set){
			sb.append(str);
			sb.append(",");
		}
		String str=sb.toString();
		if(str.length()>0){
			str=str.substring(0,str.length()-1);
		}
		return str;
	}

	@Transactional
	public void save() {
		authorities=parseAuthMap();
		if (keyRoles.contains(selected.getName())) {
			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
			WebUtil.addErrorMessage(WebUtil.getMessage("containsKeyRole"));
			return;
		}
//		if (cards.length == 0 && menus.length == 0) {
//			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//			WebUtil.addErrorMessage(WebUtil.getMessage("cardsIsBlank"));
//			return;
//		}
		selected.setTenantUID(user.getTenantUID());
		selected.setInstitutionUID(user.getInstitutionUID());
		selected.setHospitalUID(user.getHospitalUID());
		selected.setSiteUID(user.getSiteUID());
		if (selected.getCreatedBy() == null) {
			selected.setCreatedBy(user.getLoginName());
		}
		if (selected.getCreatedDate() == null) {
			selected.setCreatedDate(new Date());
		}
		selected.setLastModifiedBy(user.getLoginName());
		selected.setLastModifiedDate(new Date());
		List<I18nMessage> messages;
		Map<String, I18nMessage> map = new HashMap<>();
		if (authorities.length > 0 || cards.length > 0 || menus.length > 0) {
			List<String> param = new ArrayList<>(500);
			param.addAll(Arrays.asList(authorities));
			param.addAll(Arrays.asList(cards));
			param.addAll(Arrays.asList(menus));
			if (CollectionUtils.isNotEmpty(param)) {
				messages = i18nMessageRepository.findI18ns(param);
				if (CollectionUtils.isNotEmpty(messages)) {
					for (I18nMessage i18nMessage : messages) {
						map.put(i18nMessage.getMsgKey(), i18nMessage);
					}
				}
			}
		}
		if (authorities.length > 0) {
			StringBuilder sb1 = new StringBuilder();
			Set<String> group = new HashSet<>();
			for (String authority : authorities) {
				if (map.containsKey(authority)) {
					group.add(authority.split("\\.")[0]);
					sb1.append(map.get(authority).getValue() + ",");
				}
			}
			selected.setGroups(StringUtils.join(group, ","));
			selected.setAuthoritiesCH(removeComma(sb1));
			selected.setAuthorities(StringUtils.join(authorities, ","));
		}
		else{
			selected.setGroups("");
			selected.setAuthoritiesCH("");
			selected.setAuthorities("");
		}
		authChange();
		selected.setJsonAuthorities(authJSONArr.toString());
		if (cards.length > 0) {
			StringBuilder sb2 = new StringBuilder();
			for (String card : cards) {
				if (map.containsKey(card)) {
					sb2.append(map.get(card).getValue() + ",");
				}
			}
			selected.setCardsCH(removeComma(sb2));
			selected.setCards(StringUtils.join(cards, ","));
		}
		else{
			selected.setCardsCH("");
			selected.setCards("");
		}
		if (menus.length > 0) {
			StringBuilder sb2 = new StringBuilder();
			for (String card : menus) {
				if (map.containsKey(card)) {
					sb2.append(map.get(card).getValue() + ",");
				}
			}
			selected.setMenusCH(removeComma(sb2));
			selected.setMenus(parseMenus(menus));
		}
		else{
			selected.setMenusCH("");
			selected.setMenus("");
		}
//		if (StringUtils.isEmpty(selected.getAuthorities()) && StringUtils.isEmpty(selected.getJsonAuthorities())) {
//			RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
//			WebUtil.addErrorMessage(WebUtil.getMessage("authoritiesIsBlank"));
//			return;
//		}
		dao.save(selected);
		authorities = new String[200];
		cards = new String[100];
		menus = new String[100];
	}
	public void authChange(){
		if(authJSONArr!=null){
			for(int i=0;i<authJSONArr.size();++i){
				JSONObject authObj=authJSONArr.getJSONObject(i);
				if(authObj.getBooleanValue("a") ||authObj.getBooleanValue("d") ||authObj.getBooleanValue("m") ||authObj.getBooleanValue("i") ||authObj.getBooleanValue("o")){
					authObj.put("s", true);
				}
			}
		}
	}
	public void clear() {
		mapAuthCard.clear();
		authorities = new String[200];
		cards = new String[100];
		menus=new String[100];
		for (int i = 0; i < listI18nMenus.size(); ++i) {
			menus[i] = listI18nMenus.get(i).getMsgKey();
		}
		for (int i = 0; i < listI18nCards.size(); ++i) {
			cards[i] = listI18nCards.get(i).getMsgKey();
		}
		authJSONArr=service.getAuth();
	}

	public String removeComma(StringBuilder sb) {
		String s = sb.toString();
		if (StringUtils.isEmpty(s)) {
			return "";
		}
		if (s.endsWith(",")) {
			return s.substring(0, s.length() - 1);
		}
		if (s.length() > 15) {
			return s.substring(0, 14) + "...";
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
		if(this.authorities!=null && this.authorities.length>0){
			List<String> list=Arrays.asList(this.authorities);
			for(int j=0;j<authorities.length;++j){
				if(!list.contains(authorities[j])){
					list.add(authorities[j]);
				}
			}
			this.authorities=(String[]) list.toArray();
		}
		else{
			this.authorities = authorities;
		}
	}

	public void setCards(String[] cards) {
		this.cards = cards;
	}

	public String[] getMenus() {
		return menus;
	}

	public void setMenus(String[] menus) {
		this.menus = menus;
	}

	public List<I18nMessage> getListI18nMenus() {
		return listI18nMenus;
	}

	public void setListI18nMenus(List<I18nMessage> listI18nMenus) {
		this.listI18nMenus = listI18nMenus;
	}

	public JSONArray getAuthJSONArr() {
		return authJSONArr;
	}

	public void setAuthJSONArr(JSONArray authJSONArr) {
		this.authJSONArr = authJSONArr;
	}

	public List<I18nMessage> getListI18nCards() {
		return listI18nCards;
	}

	public void setListI18nCards(List<I18nMessage> listI18nCards) {
		this.listI18nCards = listI18nCards;
	}

	public Map<String, String[]> getMapAuthCard() {
		return mapAuthCard;
	}

	public void setMapAuthCard(Map<String, String[]> mapAuthCard) {
		this.mapAuthCard = mapAuthCard;
	}


}
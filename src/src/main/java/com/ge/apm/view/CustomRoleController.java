package com.ge.apm.view;

import java.util.*;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.alibaba.fastjson.JSON;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.service.uaa.AdminConsoleService;
import com.ge.apm.service.uaa.UaaService;
import com.ge.healthcare.cn.common.cons.ApmFunction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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
import com.ge.apm.service.uaa.TenantInfoConfigService;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
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
	private TenantInfoConfigService tenantConfigService;
	private UaaService uaaService;
	@Getter
	@Setter
	private List<String> versionLabelKey;
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

	@Getter@Setter
	private String filterFunctionStr;
	@Getter@Setter
	private List<ApmFunction> allFunction;
	@Getter@Setter
	private List<ApmFunction> functions;
	@Getter@Setter
	private List<ApmFunction> selectedFunctions;
	@Getter@Setter
	private List<ApmFunction> customSelectedFunctions;
	@Getter@Setter
	private List<ApmFunction> customFunctions;

	@Override
	protected void init() {
		filterBySite = false;
		user = UserContextService.getCurrentUserAccount();
		dao = WebUtil.getBean(CustomRoleRepository.class);
		i18nMessageRepository = WebUtil.getBean(I18nMessageRepository.class);
		service = WebUtil.getBean(CustomRoleService.class);
		uaaService = WebUtil.getBean(UaaService.class);
		listI18nMenus = service.getMenusByTenant(user.getTenantUID());
		listI18nCards=service.getCardI18n();
		authJSONArr = service.getAuth();
		service.init();
		Set<String> apmFunctions = uaaService.getFunctionListByTenantUid(user.getTenantUID());
		allFunction = Arrays.stream(ApmFunction.values()).filter(f -> apmFunctions.contains(f.name())
				&& !f.name().startsWith("PLATFORM_")
				&& !f.name().startsWith("CONFIG_")
				&& !f.name().equals("REPAIR_ORDER_DISPATCH")
				&& !f.name().startsWith("REPAIR_ORDER_TAKE")
				&& !f.name().startsWith("REPAIR_ORDER_ACCEPT")
				&& !f.name().equals("REPAIR_ORDER_APPROVE")
				&& !f.name().startsWith("REPAIR_ORDER_CANCEL")
				&& !f.name().startsWith("MAINTAIN_ORDER_ACCEPT")
				&& !f.name().equals("MAINTAIN_ORDER_DISPATCH")
				&& !f.name().startsWith("MAINTAIN_ORDER_CANCEL")
				&& !f.name().startsWith("MAINTAIN_ORDER_TAKE")
				&& !f.name().equals("MAINTAIN_ORDER_APPROVE")
				&& !f.name().equals("MAINTAIN_ORDER_VIEW_CLOSED")
//				&& !f.name().equals("MULTILEVEL_APPROVE_APPROVE")
				&& !f.name().startsWith("AUDIT_CONFIG")
				&& !f.name().startsWith("ASSET_LOCATION_")

		).collect(Collectors.toList());
		postprocessing();
		functions = allFunction;
	}
	private void postprocessing(){
		allFunction.add(ApmFunction.CONFIG_USER_EDIT);
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
			if(StringUtils.isNotEmpty(selected.getWebAuthorities())){
				authJSONArr=JSONArray.parseArray(selected.getWebAuthorities());
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
//				tenantConfigService.parseMenuList(selected.getMenus());
			}
			try {
				if (StringUtils.isNotEmpty(selected.getFunctionList())) {
					List<String> functions = (List<String>) JSONObject.parse(selected.getFunctionList());
					selectedFunctions = allFunction.stream().filter(f -> functions.contains(f.name())).collect(Collectors.toList());
					customFunctions = selectedFunctions;
					customSelectedFunctions = customFunctions;
				} else {
					selectedFunctions = new ArrayList<>();
				}
			} catch (Exception e) {
				selectedFunctions = new ArrayList<>();
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
	private void initMenu(){
		tenantConfigService=WebUtil.getBean(TenantInfoConfigService.class);
		tenantConfigService.init();
		formatVersionInfo();
	}
	private void formatVersionInfo(){
		List<String> listKey=tenantConfigService.getListKey();
		List<String> listLabel=tenantConfigService.getListLabel();
		versionLabelKey=new ArrayList<String>();
		for(int i=0;i<listKey.size();++i){
			versionLabelKey.add(listLabel.get(i)+","+listKey.get(i));
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

	public static boolean isAllNull(String[] array) {
		for (String element : array) {
			if (element != null) {
				return false;  // 如果有一个元素不为 null，则返回 false
			}
		}
		return true;  // 如果所有元素都为 null，则返回 true
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
		if (authorities.length > 0 || cards.length > 0 || BooleanUtils.isFalse(isAllNull(menus))) {
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
		} else{
			selected.setGroups("");
			selected.setAuthoritiesCH("");
			selected.setAuthorities("");
		}
		if (isOpenNewMenuRoles()) {
			if (CollectionUtils.isNotEmpty(customSelectedFunctions)) {
				Set<ApmFunction> apmFunctions = Arrays.stream(ApmFunction.values()).filter(f -> customSelectedFunctions.contains(f.name())).collect(Collectors.toSet());
				selected.setWebAuthoritiesCH(JSONObject.toJSONString(apmFunctions.stream().map(ApmFunction::getFunctionName).collect(Collectors.toSet())).replaceAll("[\\[\\]\"]", ""));
				selected.setFunctionList(JSONObject.toJSONString(customSelectedFunctions));
			} else {
				selected.setWebAuthoritiesCH("");
				selected.setFunctionList("[]");
			}
		} else {
			authChange();
			selected.setWebAuthoritiesCH(service.parseAuthArray(authJSONArr));
			selected.setWebAuthorities(authJSONArr.toString());
		}
		if (BooleanUtils.isFalse(isAllNull(menus)) && !isOpenNewMenuRoles()) {
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
		dao.save(selected);
		authorities = new String[200];
		cards = new String[100];
		menus = new String[100];
		if (!isOpenNewMenuRoles()) {
			analyseMenus(selected);
		}
	}

	private void analyseMenus(CustomRole customRole) {
		WebUtil.getBean(AdminConsoleService.class).analyseCustomMenu(customRole);
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
		selectedFunctions = new ArrayList<>();
		customSelectedFunctions = new ArrayList<>();
		customFunctions = new ArrayList<>();
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

	public TenantInfoConfigService getTenantConfigService() {
		return tenantConfigService;
	}

	public void setTenantConfigService(TenantInfoConfigService tenantConfigService) {
		this.tenantConfigService = tenantConfigService;
	}

	public boolean isOpenNewMenuRoles() {
		if (selected == null) {
			return false;
		}
		UserContextService userContextService = WebUtil.getBean(UserContextService.class);
		TenantInfo tenantInfo = userContextService.getTenantInfo();
		return  tenantInfo.getEnablesNewMenu() && StringUtils.isNotEmpty(tenantInfo.getFeatureList());
	}

	public void filterFunction() {
		if (StringUtils.isEmpty(filterFunctionStr)) {
			functions = allFunction;
			return;
		} else {
			functions = new ArrayList<>();
			if (selectedFunctions.size() > 0) {
				List<String> filterFunctions = (List<String>) JSON.parse(JSON.toJSONString(selectedFunctions));
				functions.addAll(allFunction.stream().filter(f -> filterFunctions.contains(f.name())).collect(Collectors.toList()));
			}
			functions.addAll(allFunction.stream().filter(item -> item.getFunctionName().toLowerCase().contains(filterFunctionStr.toLowerCase())).collect(Collectors.toList()));
			functions = functions.stream().distinct().collect(Collectors.toList());
		}
	}

	public void selectChekBox() {
		customSelectedFunctions = selectedFunctions;
		customFunctions.clear();
		customFunctions.addAll(Arrays.stream(ApmFunction.values()).filter(f -> selectedFunctions.contains(f.name())).collect(Collectors.toList()));
	}

	public void unSelectChekBox() {
		selectedFunctions = customSelectedFunctions;
	}

	public String getAuthorityByType(String authority, String authorityType) {
		if (authorityType.equals("web")) {
			return Arrays.asList(authority.split(",")).stream().filter(v -> !v.contains("移动端.")).collect(Collectors.joining(","));
		} else {
			return Arrays.asList(authority.split(",")).stream().filter(v -> v.contains("移动端.")).collect(Collectors.joining(","));
		}
	}

}
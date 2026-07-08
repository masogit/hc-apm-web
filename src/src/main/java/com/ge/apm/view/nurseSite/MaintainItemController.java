package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.TableOption;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ibm.icu.util.Calendar;

import org.springframework.beans.BeanUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

@ManagedBean
@ViewScoped
public class MaintainItemController implements Serializable, ServerEventInterface {
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainItemController.class);
	private I18nMessageRepository i18nDao;
	private UserAccount currentUser;
	private NurseSiteMaintainService nurseSiteMaintainService;
	private NurseSiteMaintain nurseSiteMaintain;
	private List<NurseSiteMaintainItem> lazyModel;
	private UserAccountRepository userAccountRepository;
	// config
	private int page=0;
	private int pageSize=8;
	private int size=0;
	private boolean blnPre;
	private boolean blnNext;
	private Map<String,Boolean> mapCheckData;
	private List<NurseSiteMaintainConfigCheckItem> listCheckData=new ArrayList<>();
	private MaintainController maintainController;
	private Boolean checkRequired=Boolean.TRUE;

	private NurseSiteMaintainConfigCheckItem selectCheckItem;
	@PostConstruct
	public void init() {
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		nurseSiteMaintainService.init();
		mapCheckData=new HashMap<String,Boolean>();
		userAccountRepository=WebUtil.getBean(UserAccountRepository.class);
	}

	public void reset(NurseSiteMaintain nurseSiteMaintain,MaintainController maintainController) {
		this.nurseSiteMaintain = nurseSiteMaintain;
		mapCheckData.clear();
		page=0;
		generateNurseSiteMaintainData(pageSize, page);
		generateLabel();
		size=getItemSize();
		pageButtonChange();
		this.maintainController=maintainController;
	}
	@Override
	public void onServerEvent(String eventName, Object eventObject) {
	}
	
	public void next(){
		generateNurseSiteMaintainData(pageSize, ++page);
		if(lazyModel==null || lazyModel.size()==0){
			--page;
		}
		pageButtonChange();
	}
	
	public void pre(){
		generateNurseSiteMaintainData(pageSize, --page);
		if(page<0){
			page=0;
		}
		pageButtonChange();
	}
	public void checkRightAll(){
		nurseSiteMaintainService.checkRightAll(nurseSiteMaintain);
	}
	public void completeMaintain(){
		if(nurseSiteMaintainService.completeMaintain(nurseSiteMaintain, new Date(), currentUser)) {
			this.maintainController.updateEvent();
			addMessage("工单已保存");
		} else {
			return;
		}
	}
	public void saveCheckData(){
//		List<String> listCheckData=new ArrayList<String>();
//		for(String label: mapCheckData.keySet()){
//			if(mapCheckData.get(label)){
//				listCheckData.add(label);
//			}
//		}
		if(!checkRequired()) return;
		//nurseSiteMaintain.setCheckData(listCheckData);
		nurseSiteMaintain.setListCheckData(this.listCheckData);
		completeMaintain();
	}

	private boolean checkRequired() {
		checkRequired = Boolean.TRUE;
		String str="必填检查项未填写，不允许保存";
		List<NurseSiteMaintainConfigCheckItem> listCheckData = this.listCheckData;
		for (NurseSiteMaintainConfigCheckItem listDatum : listCheckData) {
			Boolean required = listDatum.getRequired();
			if(Boolean.TRUE.equals(required)){
				Boolean checkValue = listDatum.getCheckValue();
				String defaultValue = listDatum.getDefaultValue();
				if(!Boolean.TRUE.equals(checkValue)|| StringUtils.isBlank(defaultValue)){
					checkRequired = Boolean.FALSE;
					String checkItem = listDatum.getName();
					addErrMessage("检查项："+checkItem+" (勾选项、输入项), "+str);
				}
			}
		}
		return checkRequired;
	}
	private void addErrMessage(String str) {
		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
		WebUtil.addErrorMessageKey(str);
	}
	public void resetCheckData(){
//		for(String label: mapCheckData.keySet()){
//			mapCheckData.put(label, true);
//		}
		for (NurseSiteMaintainConfigCheckItem listCheckDatum : listCheckData) {
			listCheckDatum.setCheckValue(true);
		}
	}
	public void resetCheckData2(){
//		for(String label: mapCheckData.keySet()){
//			mapCheckData.put(label, false);
//		}
		for (NurseSiteMaintainConfigCheckItem listCheckDatum : listCheckData) {
			listCheckDatum.setCheckValue(false);
		}
	}
	private void pageButtonChange(){
		if(size<=pageSize){
			blnNext=false;
			blnPre=false;
		}
		else{
			if((page+1)*pageSize<size){
				blnNext=true;
			}
			else{
				blnNext=false;
			}
			if(page==0){
				blnPre=false;
			}
			else if(page>0){
				blnPre=true;
			}
		}
	}
	private int getItemSize(){
		return nurseSiteMaintainService.findCountMaintainItemByParentId(nurseSiteMaintain.getId());
	}
	private void generateNurseSiteMaintainData(int pageSize, int page) {
		if(nurseSiteMaintain==null){
			return;
		}
		nurseSiteMaintainService.supplementMaintain(nurseSiteMaintain, pageSize, page,false);
		lazyModel = nurseSiteMaintain.getListNurseSiteMaintainItem();
	}
	private void generateLabel(){
		List<NurseSiteMaintainConfigCheckItem> showLables = nurseSiteMaintain.getShowLables();
		if (showLables == null) {
			showLables = new ArrayList<>();
		} else {
			showLables.clear();

		}
		listCheckData.clear();
		try {
			List<NurseSiteMaintainData> listTempData = lazyModel.iterator().next().getListData();
			if (listTempData != null) {
				List<String> collect = listTempData.stream().map(NurseSiteMaintainData::getCheckItem).collect(Collectors.toList());
				NurseSiteMaintainConfig maintainConfigById = nurseSiteMaintainService.getMaintainConfigById(nurseSiteMaintain.getConfigId());
				String strTemp = maintainConfigById.getCheckItemsDetail();
				if(StringUtils.isNotEmpty(strTemp)){
					JSONArray jsonArray= JSONObject.parseArray(strTemp);
					for(int i=0;i<jsonArray.size();++i){
						NurseSiteMaintainCheckItem checkItem= JSONObject.parseObject(jsonArray.getString(i), NurseSiteMaintainCheckItem.class);
						if(collect.contains(checkItem.getName())){
							NurseSiteMaintainConfigCheckItem p = initConfigCheckItem(checkItem);
							showLables.add(p);
							NurseSiteMaintainConfigCheckItem nurseSiteMaintainConfigCheckItem = new NurseSiteMaintainConfigCheckItem();
							BeanUtils.copyProperties(p,nurseSiteMaintainConfigCheckItem);
							Optional<Boolean> first = listTempData.stream().filter(item -> item.getCheckItem().equals(p.getName())).map(item -> item.getRequired()).findFirst();
							if (first.isPresent()) {
								nurseSiteMaintainConfigCheckItem.setRequired(first.get());
							}else{
								nurseSiteMaintainConfigCheckItem.setRequired(Boolean.FALSE);
							}
							listCheckData.add(nurseSiteMaintainConfigCheckItem);
						}
					}
				}
			}
			nurseSiteMaintain.setShowLables(showLables);
//			for(int i=0;i<showLables.size();++i){
//				mapCheckData.put(showLables.get(i).getName(), true);
//			}
		} catch (Exception e) {
			LOGGER.error("getLables error {}", e.getMessage(), e);
		}
	}
	private NurseSiteMaintainConfigCheckItem initConfigCheckItem(NurseSiteMaintainCheckItem checkItem){
		NurseSiteMaintainConfigCheckItem p=new NurseSiteMaintainConfigCheckItem();
		p.setId(checkItem.getId());
		p.setName(checkItem.getName());
		p.setDefaultValue(checkItem.getDefaultValue());
		p.setDescription(checkItem.getDescription());
		p.setFieldType(checkItem.getFieldType());
		p.setRequired(checkItem.getRequired());
		String options = checkItem.getOptions();
		if(StringUtils.isBlank(options) || options.contains("[]")){
			p.setOptions(null);
		}else{
			JSONArray objects = JSONArray.parseArray(options);
			List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
			p.setOptions(tableOptions);
		}
		return p;
	}
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public NurseSiteMaintain getNurseSiteMaintain() {
		return nurseSiteMaintain;
	}

	public void setNurseSiteMaintain(NurseSiteMaintain nurseSiteMaintain) {
		this.nurseSiteMaintain = nurseSiteMaintain;
	}

	public List<NurseSiteMaintainItem> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(List<NurseSiteMaintainItem> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public boolean isBlnPre() {
		return blnPre;
	}

	public void setBlnPre(boolean blnPre) {
		this.blnPre = blnPre;
	}

	public boolean isBlnNext() {
		return blnNext;
	}

	public void setBlnNext(boolean blnNext) {
		this.blnNext = blnNext;
	}

	public Map<String, Boolean> getMapCheckData() {
		return mapCheckData;
	}

	public void setMapCheckData(Map<String, Boolean> mapCheckData) {
		this.mapCheckData = mapCheckData;
	}

	public List<NurseSiteMaintainConfigCheckItem> getListCheckData() {
		if(CollectionUtils.isNotEmpty(listCheckData)){
			Map<String,NurseSiteMaintainConfigCheckItem> result=new HashMap<>();
			for (NurseSiteMaintainConfigCheckItem listCheckDatum : listCheckData) {
				result.put(listCheckDatum.getName(),listCheckDatum);
			}
			return new ArrayList<>(result.values());
		}
		return listCheckData;
	}

	public void setListCheckData(List<NurseSiteMaintainConfigCheckItem> listCheckData) {
		this.listCheckData = listCheckData;
	}
	public String getUpdateByName(NurseSiteMaintainItem item){
		String name="";
		if(item != null && StringUtils.isNotBlank(item.getUpdateByUid())){
			String updateByUid=item.getUpdateByUid();
			UserAccount byUid = userAccountRepository.getByUid(updateByUid);
			if(byUid != null){
				name=byUid.getName();
			}
		}
		return name;
	}

	public void showDescription(NurseSiteMaintainConfigCheckItem item){
		selectCheckItem = item;
	}

	public NurseSiteMaintainConfigCheckItem getSelectCheckItem() {
		return selectCheckItem;
	}

	public void setSelectCheckItem(NurseSiteMaintainConfigCheckItem selectCheckItem) {
		this.selectCheckItem = selectCheckItem;
	}

	/**
	 * 判断当前组织是否启用了 dailyCheckIgnorable 配置项（值为true表示启用）
	 */
	public boolean isDailyCheckIgnorable() {
		UserContextService userContextService = WebUtil.getBean(UserContextService.class);
		String value = userContextService.getConfigItemValue("dailyCheckIgnorable");
		return Boolean.TRUE.toString().equalsIgnoreCase(value);
	}

	/**
	 * 勾选"忽略"时，自动清除该检查项的勾选状态（通过/不通过），避免两个勾选框同时被锁定的死锁
	 */
	public void onSkipChange(NurseSiteMaintainData data) {
		if (data != null && Boolean.TRUE.equals(data.getIsSkip())) {
			data.setCheckValue(null);
			data.setCheckValueWeb(0);
		}
	}
}

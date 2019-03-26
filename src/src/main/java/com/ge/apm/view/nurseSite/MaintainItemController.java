package com.ge.apm.view.nurseSite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ibm.icu.util.Calendar;

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
	// config
	private int page=0;
	private int pageSize=8;
	private int size=0;
	private boolean blnPre;
	private boolean blnNext;
	private Map<String,Boolean> mapCheckData;
	private MaintainController maintainController;
	@PostConstruct
	public void init() {
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		mapCheckData=new HashMap<String,Boolean>();
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
		nurseSiteMaintainService.completeMaintain(nurseSiteMaintain, new Date(), currentUser);
		this.maintainController.updateEvent();
		addMessage("工单已保存");
	}
	public void saveCheckData(){
		List<String> listCheckData=new ArrayList<String>();
		for(String label: mapCheckData.keySet()){
			if(mapCheckData.get(label)){
				listCheckData.add(label);
			}
		}
		nurseSiteMaintain.setCheckData(listCheckData);
	}
	public void resetCheckData(){
		for(String label: mapCheckData.keySet()){
			mapCheckData.put(label, true);
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
		List<String> showLables = nurseSiteMaintain.getShowLables();
		if (showLables == null) {
			showLables = new ArrayList<String>();
		} else {
			showLables.clear();
		}
		try {
			List<NurseSiteMaintainData> listTempData = lazyModel.iterator().next().getListData();
			if (listTempData != null) {
				Iterator<NurseSiteMaintainData> it = listTempData.iterator();
				while (it.hasNext()) {
					NurseSiteMaintainData tempData = it.next();
					showLables.add(tempData.getCheckItem());
				}
			}
			nurseSiteMaintain.setShowLables(showLables);
			for(int i=0;i<showLables.size();++i){
				mapCheckData.put(showLables.get(i), true);
			}
		} catch (Exception e) {
			// TODO: handle exception
			LOGGER.error("getLables error" + e.getMessage());
		}
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
	
}

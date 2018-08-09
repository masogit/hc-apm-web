package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.primefaces.context.RequestContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class TagSubscriberController extends JpaCRUDController<AssetTagMsgSubscriber> implements Serializable,
		ServerEventInterface {


	private static final long serialVersionUID = 1L;
	private List<String> cities;
	private AssetTagMsgSubscriberRepository dao = null;
	private AssetTagRepository assetTagRepository;
	private I18nMessageRepository i18Dao;
	private UserAccount currentUser;
	private UserAccountRepository userAccountRepository;
	private List<UserAccount> tenantUserList;
	List<Integer> modeList;
	private List<I18nMessage>   msgModeList;
	private List<AssetTag> assetTags;
	UserContextService userContextService;
	List<AssetTagMsgSubscriber> assetTagMsgSubscribers;
	HashMap<Integer,String> hmMsgMode = new HashMap<Integer,String>();
	private Integer receiveMsgMode =new Integer(0);
	private int selectType=1;
	private Boolean isReceiveTimeoutMsg;

	//如果是新增，只有后台itadmin进来 可以修改。 如果是修改 全灰色
	public Boolean subscribe(){
		if(this.getCRUDActionName().equals("修改")){
			return true; //不可修改
		}else if(userContextService.hasRole("ITAdmin")){
			return false;
		}else
		{
			return true;
		}
	}


	// 关注人tentant下的tag
	public void takeAssetTags(){
		//获得的是:和标签绑定的用户的院区下的所有标签
		assetTags =  assetTagRepository.getTagBySite(userAccountRepository.getById(this.selected.getSubscribeUserId()).getSiteUID());

	}


	public void cancel(){
		selected=null;
	}
	public boolean requiredCheck(){
		//不推送工单消息  情况下 必须选工单超时消息
		if(receiveMsgMode==2 && !isReceiveTimeoutMsg){
			WebUtil.addErrorMessage("务必选择工单超时消息推送");
			RequestContext.getCurrentInstance().addCallbackParam("xyz", true);
			return false;
		}

		//选了工单超时消息 必须选择紧急程度
		if(isReceiveTimeoutMsg && receiveMsgMode==2){
			if(!level0 && !level1 && !level2) {
				WebUtil.addErrorMessage("务必选择工单紧急程度");
				RequestContext.getCurrentInstance().addCallbackParam("xyz", true);
				return false;
			}
		}
		///
		if(receiveMsgMode==0 || receiveMsgMode==1){
			if(!level0 && !level1 && !level2){
				WebUtil.addErrorMessage("务必选择紧急程度");
				RequestContext.getCurrentInstance().addCallbackParam("xyz", true);
				return false;
			}
		}
		/*如果 消息推送选项是"不推..",并且超时推送也不选，那么紧急度不应该选上*/
		if(receiveMsgMode==2 && isReceiveTimeoutMsg==false){
			if(level0 || level1 || level2){
				RequestContext.getCurrentInstance().addCallbackParam("xyz", true);
				return false;
			}
		}
		return true;
	}
	public void saveTagSub4(){
		if (!requiredCheck()) {
			return;
		}
		if(this.getCRUDActionName().equals("新增")) {
			if (selectType == 1) {//维修
				List<AssetTagMsgSubscriber> tagUserMode012 = dao.getTagUserMode012(selected.getTagId(), selected.getSubscribeUserId());
				if(tagUserMode012.isEmpty()) {
					selected.setReceiveMsgMode(receiveMsgMode);
					selected.setIsReceiveTimeoutMsg(isReceiveTimeoutMsg);
					selected.setLevel0(level0);
					selected.setLevel1(level1);
					selected.setLevel2(level2);
					super.save();
					return;
				}
				else{
					WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
					return;
				}
			} else {//EE报警
				List<AssetTagMsgSubscriber> tagUserMode3 = dao.getTagUserMode3(selected.getTagId(), selected.getSubscribeUserId());
				if(tagUserMode3.isEmpty()) {
					selected.setReceiveMsgMode(3);
					dao.save(selected);
					return;
				}else{
					WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
					return;
				}
			}
		}else{
			selected.setReceiveMsgMode(receiveMsgMode);
			selected.setIsReceiveTimeoutMsg(isReceiveTimeoutMsg);
			selected.setLevel0(level0);
			selected.setLevel1(level1);
			selected.setLevel2(level2);
			super.save();
		}
	}
	@Override
	protected void init() {
		cities = new ArrayList<String>();
		cities.add("Miami");
		cities.add("London");
		cities.add("Paris");
		cities.add("Istanbul");
		userContextService  = (UserContextService) WebUtil.getBean(UserContextService.class);
		this.filterBySite =false;
		dao= WebUtil.getBean(AssetTagMsgSubscriberRepository.class);
		assetTagRepository =WebUtil.getBean(AssetTagRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		i18Dao = WebUtil.getBean(I18nMessageRepository.class);
		FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
		msgModeList = fieldMsg.getFieldValueList("focusOption");
		for (I18nMessage local1 : msgModeList) {
			hmMsgMode.put(Integer.parseInt(local1.getMsgKey()), local1.getValue());
		}
		msgModeList=msgModeList.stream().filter(item->!item.getMsgKey().equals("3")).collect((Collectors.toList()));
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		//获取当前租户下的所有用户 --获取关注人--当前租户下除了Guest ClincialStaff Activation..角色以外的其它人 都可以成为关注人
		tenantUserList =userAccountRepository.getByTenantUID(currentUser.getTenantUID());
		tenantUserList = tenantUserList.stream().distinct().collect(Collectors.toList());
		modeList =new ArrayList<Integer>();
	}
	public String getTagName(Integer tagId){
		try{
			return assetTagRepository.findById(tagId).getName();
		}
		catch(Exception ex){
			return "";
		}
	}

	public String getMsgMode(int msgmode){
		return hmMsgMode.get(msgmode);
	}

	@Override
	protected String getActionMessage(String actionName) {
		return WebUtil.getMessage("标签"+this.getTagName(this.selected.getTagId())+"  "+WebUtil.getMessage(actionName));
	}

	@Override
	protected AssetTagMsgSubscriberRepository getDAO() {
		return dao;
	}

	@Override
	protected Page<AssetTagMsgSubscriber> loadData(PageRequest pageRequest) {
		//	selected = null;
		if(this.searchFilters ==null){
			searchFilters =  new ArrayList<>();
		}else{
			this.removeFilterOnField("subscribeUserId");
			this.removeFilterOnField("subscriberUser.tenantUID");
		}
		//当前租户下的标签
		//IT管理员可以看到所有关注标签的用户,
		if(!userContextService.hasRole("ITAdmin")) {
			searchFilters.add(new SearchFilter("subscribeUserId", SearchFilter.Operator.EQ, currentUser.getId()));
		}else{
			searchFilters.add(new SearchFilter("subscriberUser.tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
		}
		if(searchFilters.size() <= 0){
			return dao.findAll(pageRequest);
		}else{
			return dao.findBySearchFilter(searchFilters, pageRequest);
		}
	}

	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		super.prepareCreate();
		selectType=0;
		level0=true;
		level1=true;
		level2=true;
		isReceiveTimeoutMsg=true;
		this.selected.setReceiveMsgMode(null);
		assetTags =  assetTagRepository.getUnSubscriberTagBySiteUID(selected.getSubscribeUserId());
	}

	List<AssetTagMsgSubscriber> tagUserModeBasic;
	AssetTagMsgSubscriber currentAtms;
	@Override
	public void prepareEdit() {
		super.prepareEdit();
		Integer se=selected.getSelectType();
		if(selected.getLevel0()!=null){
			level0=selected.getLevel0();
		}else{
			level0=false;
		}
		if(selected.getLevel1()!=null){
			level1=selected.getLevel1();
		}else{
			level1=false;
		}
		if(selected.getLevel2()!=null){
			level2=selected.getLevel2();
		}else{
			level2=false;
		}

		receiveMsgMode= selected.getReceiveMsgMode();
		isReceiveTimeoutMsg=selected.getIsReceiveTimeoutMsg();
		if(se!=null)
			selectType=se.intValue();

		tagUserModeBasic=dao.getTagUserMode(selected.getTagId(),selected.getSubscribeUserId());
		currentAtms=dao.findById(selected.getId());
		assetTags= assetTagRepository.getUnSubscriberTagBySiteUID(selected.getSubscribeUserId());
	}

	@Override
	public void onBeforeNewObject(AssetTagMsgSubscriber object) {
		super.onBeforeNewObject(object);
		object.setSubscribeUserId(currentUser.getId());
		object.setIsReceiveChatMsg(true);
	}

	public List<I18nMessage> getMsgModeList() {
		return msgModeList;
	}

	public void setMsgModeList(List<I18nMessage> msgModeList) {
		this.msgModeList = msgModeList;
	}

	public List<AssetTag> getAssetTags() {
		return assetTags;
	}

	public void setAssetTags(List<AssetTag> assetTags) {
		this.assetTags = assetTags;
	}

	public Integer getReceiveMsgMode() {
		return receiveMsgMode;
	}

	public List<UserAccount> getTenantUserList() {
		return tenantUserList;
	}

	public void setTenantUserList(List<UserAccount> tenantUserList) {
		this.tenantUserList = tenantUserList;
	}

	public void setReceiveMsgMode(Integer receiveMsgMode) {
		this.receiveMsgMode = receiveMsgMode;
	}

	public int getSelectType() {
		return selectType;
	}

	public void setSelectType(int selectType) {
		this.selectType = selectType;
	}
	private String[] selectedCities2;


	private Boolean level0;
	private Boolean level1;
	private Boolean level2;

	public Boolean getLevel0() {
		return level0;
	}

	public void setLevel0(Boolean level0) {
		this.level0 = level0;
	}

	public Boolean getLevel1() {
		return level1;
	}

	public void setLevel1(Boolean level1) {
		this.level1 = level1;
	}

	public Boolean getLevel2() {
		return level2;
	}

	public void setLevel2(Boolean level2) {
		this.level2 = level2;
	}

	public Boolean getIsReceiveTimeoutMsg() {
		return isReceiveTimeoutMsg;
	}

	public void setIsReceiveTimeoutMsg(Boolean receiveTimeoutMsg) {
		isReceiveTimeoutMsg = receiveTimeoutMsg;
	}

}

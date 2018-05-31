package com.ge.apm.view.pm;

import com.ge.apm.dao.*;
import com.ge.apm.domain.*;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.JpaCRUDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@ManagedBean
@ViewScoped
public class TagSubscriberController extends JpaCRUDController<AssetTagMsgSubscriber> {

	private static final long serialVersionUID = 1L;

	private AssetTagMsgSubscriberRepository dao = null;
	private AssetTagRepository assetTagRepository;
	private I18nMessageRepository i18Dao;
	private UserAccount currentUser;
	private UserAccountRepository userAccountRepository;
	private List<UserAccount> tenantUserList;

	private List<I18nMessage>   msgModeList;
	private List<AssetTag> assetTags;
	UserContextService userContextService;
	List<AssetTagMsgSubscriber> assetTagMsgSubscribers;
	HashMap<Integer,String> hmMsgMode = new HashMap<Integer,String>();
	private Integer receiveMsgMode;

	/*public boolean subscriberShow(){
		//xiugai   if(xiu gai and fei admin) return true
		if(!userContextService.hasRole("ITAdmin")&  WebUtil.getMessage(crudActionName)=="修改")
			return true;
		else
			 return false;
	}*/
	//选择关注人后，被其关注过的不会出现在设备标签的下拉框中
	public void checkSelectedEmpty(){
		if(selected==null){
			WebUtil.addErrorMessage("设备消息订阅没有被选中");
		}
	}
// 关注人tentant下的tag
	public void takeAssetTags(){
		//获得的是:和标签绑定的用户的院区下的所有标签
		assetTags =  assetTagRepository.getTagBySite(userAccountRepository.getById(this.selected.getSubscribeUserId()).getSiteUID());

	}
	public void test(){
		System.out.println(selected.getReceiveMsgMode());
	}


	public void cancel(){
		selected=null;
	}


	public void saveTagSub3(){
		List<AssetTagMsgSubscriber> modeBasic=dao.getTagUserMode(selected.getTagId(), selected.getSubscribeUserId());
		if(this.getCRUDActionName().equals("新增")) {
			if (modeBasic.isEmpty()) {
				super.save();
				return;
			} else {
				if (modeBasic.size() == 1) {
					AssetTagMsgSubscriber dy = modeBasic.get(0);
					if (dy.getReceiveMsgMode() != 3) {
						if (selected.getReceiveMsgMode() == 3) {
							super.save();
						} else {
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
						}

					} else {
						if (selected.getReceiveMsgMode() != 3) {
							super.save();
						} else {
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
						}
					}
				}else{//已经有两条不同组合的 不能再创建了
					WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
				}
			}
		}else{//修改
			if (modeBasic.isEmpty()) {
				super.save();
				return;
			}
			if(modeBasic.size() == 1){
				AssetTagMsgSubscriber dy = modeBasic.get(0);
				if(currentAtms.getTagId().intValue()!=selected.getTagId().intValue()){
					if(dy.getReceiveMsgMode().intValue()==3){
						if(selected.getReceiveMsgMode().intValue()!=3){
							super.save();
							return;
						}else{
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
							return;
						}

					}else{
						if(selected.getReceiveMsgMode().intValue()==3){
							super.save();
							return;
						}else{
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
							return;
						}
					}

				}
				if(currentAtms.getSubscribeUserId().intValue()!=selected.getSubscribeUserId().intValue()){
					if(dy.getReceiveMsgMode().intValue()==3){
						if(selected.getReceiveMsgMode().intValue()!=3){
							super.save();
							return;
						}else{
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
							return;
						}

					}else{
						if(selected.getReceiveMsgMode().intValue()==3){
							super.save();
							return;
						}else{
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
							return;
						}
					}

				}
				if(dy.getTagId().intValue()==selected.getTagId().intValue()){//如果不是改动设备标签 而是改动 选项 则随便改
					  if(currentAtms.getTagId().intValue()!=selected.getTagId().intValue()){

					  }
					  super.save();
						return;
				  }else{
					  if (dy.getReceiveMsgMode() != 3){
						  if (selected.getReceiveMsgMode() == 3) {
							  super.save();
						  } else {
							  WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
						  }
					  }else{
						  if (selected.getReceiveMsgMode() != 3){
						  	super.save();
						  	return;
						  }else{
							  WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
						  }
					  }
				  }
			}else {//如果有两条数据存在
				if(selected.getSubscriberUser().getId().intValue()!=selected.getSubscribeUserId().intValue()){
					WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
					return;
				}
				/*
				*从表中获取当前要改的数据的 选项
				*如果改动的是ee报警的那条数据 那 就无法改动 refuse,
				* 如果改动的是3and1的 ,那么direct 不为3 就可以save 否则 refuse
				*
				* */
				//
				AssetTagMsgSubscriber atmtmp=null;
				if(tagUserModeBasic.size()>1){
					if(currentAtms.getReceiveMsgMode()==3){
						WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
						return;
					}else{
						if(selected.getReceiveMsgMode()!=3){
							super.save();
							return;
						}else{
							WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
							return;
						}
					}
				}
				if (selected.getReceiveMsgMode() != 3) {
					super.save();
				} else {
					WebUtil.addErrorMessage("不能重复相同的设备消息订阅");
				}
			}
		}
	}

	public AssetTagMsgSubscriber getATMS(Integer id1,List<AssetTagMsgSubscriber> alist){
		for(int i=0; i<alist.size();i++){
			if(alist.get(i).getId().equals(id1))
				return alist.get(i);
		}
		return null;
	}

	List<I18nMessage> fousOptionWithOutEE;
	List<Integer> modeList;



	@Override
	protected void init() {
		userContextService  = (UserContextService) WebUtil.getBean(UserContextService.class);
		this.filterBySite =false;
		dao= WebUtil.getBean(AssetTagMsgSubscriberRepository.class);
		assetTagRepository =WebUtil.getBean(AssetTagRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
		i18Dao = WebUtil.getBean(I18nMessageRepository.class);
		FieldValueMessageController fieldMsg = WebUtil.getBean(FieldValueMessageController.class, "fieldMsg");
		msgModeList = fieldMsg.getFieldValueList("focusOption");
		userAccountRepository = WebUtil.getBean(UserAccountRepository.class);
		//获取当前租户下的所有用户 --获取关注人--当前租户下除了Guest ClincialStaff Activation..角色以外的其它人 都可以成为关注人
		tenantUserList =userAccountRepository.getByTenantUID(currentUser.getTenantUID());

		tenantUserList = tenantUserList.stream().distinct().collect(Collectors.toList());
		for (I18nMessage local1 : msgModeList) {
			hmMsgMode.put(Integer.parseInt(local1.getMsgKey()), local1.getValue());
		}

		fousOptionWithOutEE = i18Dao.getFousOptionWithOutEE();
		modeList =new ArrayList<Integer>();
		fousOptionWithOutEE.forEach(item->{modeList.add(Integer.valueOf(item.getMsgKey()));});

	}
	public String getTagName(Integer tagId){
            try{
		return assetTagRepository.findById(tagId).getName();
            }
            catch(Exception ex){
                return "";
            }
	}
	public String getSubscriberName(Integer userId){
		return userAccountRepository.findById(userId).getName();
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
		selected = null;
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
		this.selected.setReceiveMsgMode(null);
		assetTags =  assetTagRepository.getUnSubscriberTagBySiteUID(selected.getSubscribeUserId());
	}

	List<AssetTagMsgSubscriber> tagUserMode3and1;
	List<AssetTagMsgSubscriber> tagUserModeEE;
	List<AssetTagMsgSubscriber> tagUserModeBasic;
	AssetTagMsgSubscriber currentAtms;
	@Override
	public void prepareEdit() {
		super.prepareEdit();

		tagUserModeBasic=dao.getTagUserMode(selected.getTagId(),selected.getSubscribeUserId());
		currentAtms=dao.findById(selected.getId());
		assetTags= assetTagRepository.getUnSubscriberTagBySiteUID(selected.getSubscribeUserId());
		//assetTags =  assetTagRepository.getUnSbuscriberTag(currentUser.getHospitalId(), this.selected.getSubscribeUserId());



	}

	@Override
	public void onBeforeNewObject(AssetTagMsgSubscriber object) {
		super.onBeforeNewObject(object);
		object.setSubscribeUserId(currentUser.getId());
		object.setIsReceiveChatMsg(true);
	}

	public HashMap<Integer, String> getHmMsgMode() {
		return hmMsgMode;
	}

	public void setHmMsgMode(HashMap<Integer, String> hmMsgMode) {
		this.hmMsgMode = hmMsgMode;
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
		if(this.selected!=null)
		{
			return  this.selected.getReceiveMsgMode();
		}else

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
}
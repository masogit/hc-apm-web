package com.ge.apm.view.nurseSite;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.mapper.NurseSiteMaintainConfigMapper;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.service.nurseSite.NurseSiteMaintainService;
import com.ge.apm.service.nurseSite.export.NurseSiteMaintainExport;
import com.ge.apm.service.uaa.UaaService;
import com.ge.apm.view.SearchController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleModel;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class MaintainController implements ServerEventInterface{
	private static final Logger LOGGER = LoggerFactory.getLogger(MaintainController.class);
	private NurseSiteMaintainService nurseSiteMaintainService;
	private UserAccount currentUser;
	private ScheduleModel lazyEventModel;
	private DefaultScheduleEvent event;
	private List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig;
	private List<NurseSiteMaintainConfig> showListNurseSiteMaintainConfig;
	private List<NurseSiteMaintainConfig> listCheckedNurseSiteMaintainConfig;
	private NurseSiteMaintainConfig nurseSiteMaintainConfig;
	private List<AssetInfo> listCheckAssets;
	private List<OrgInfo> listCheckClinicalOrgInfo;
	private OrgInfo selectedParentOrgInfo;
	private NurseSiteMaintainConfigMapper configMapper;

	private List<Integer> listSequence;
	private Integer selectSequence = 1;
	// from config default
	private NurseSiteMaintain nurseSiteMaintain;
	private List<NurseSiteMaintain> listMaintainData;
	private Date initStartDate;
	private Date initEndDate;
	private Date startBatchDate;
	private Date endBatchDate;
	private Date currentDate;

	private Date userClickDate;
	// validate
	private boolean isDeleteDisable;
	private boolean isSaveDisable;
	// show
	private List<OrgInfo> listOrgInfo;
	private List<OrgInfo> checkListParentOrgInfo;
	// inner
	private String signatureContent;
	private StreamedContent signatureContentStream;
	private StreamedContent signatureContentWeiXin;
	private boolean blnWebSignatureEidt;
	private List<V2_BlobObject> signatureList;
	private int currentSignatureIndex;
	private StreamedContent currentSignatureContent;
	private boolean showSignaturePad;
	// 批量签名
	private List<String> batchSignatureList;
	private int batchCurrentSignatureIndex;
	private boolean showBatchSignaturePad;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private List<String> orgUidList = Collections.singletonList("-1");
	private SearchController searchController;
	private MaintainItemController maintainItemController;
	private UserAuth customRoleAuth;
	private UserContextService userContextService;

	private boolean isSelectAll = Boolean.FALSE;

	private UaaService uaaService;

	private OrgInfoRepository orgInfoRepository;

	private List<String> userDeptScopeOrg;

	private Boolean checkRequired=Boolean.TRUE;

	private String selectedStatus;

	@PostConstruct
	protected void init() {
		uaaService = WebUtil.getBean(UaaService.class);
		orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);

		userContextService = WebUtil.getBean(UserContextService.class);
		nurseSiteMaintainService = WebUtil.getBean(NurseSiteMaintainService.class);
		nurseSiteMaintainService.init();
		searchController = WebUtil.getBean(SearchController.class);
		maintainItemController = WebUtil.getBean(MaintainItemController.class);
		configMapper = WebUtil.getBean(NurseSiteMaintainConfigMapper.class);
		searchController.setType(3);
		searchController.setBlnDepartOrgUpdate(true);
		searchController.setBlnOrgTreeUpdate(true);
		searchController.setUpdateViewIDs(":maintainForm:maintain");
		searchController.updateData();
		searchController.setCallingController(this);
		currentUser = UserContextService.getCurrentUserAccount();
		this.loadMaintainData();
		selectedSite = searchController.getSelectedOrg();
		orgUidList = uaaService.getOrgListByUser(currentUser.getUid()).stream().map(OrgInfo::getUid).collect(Collectors.toList());

		userDeptScopeOrg=orgInfoRepository.getUserDeptScope(currentUser.getUid());
		Map<String, Object> param = MapUtil.newHashMap();
		param.put("tenantUid", currentUser.getTenantUID());
//		param.put("orgUidList", orgUidList);
		param.put("userUid", currentUser.getUid());
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			listNurseSiteMaintainConfig = ListUtil.empty();
		} else {
			if (currentUser.isEnableAssetTypeGroup()) {
				param.put("assetTypes", currentUser.getAssetTypeGroupMap().keySet());
			}
            listNurseSiteMaintainConfig = configMapper.searchMaintainConfig(param);
        }
		showListNurseSiteMaintainConfig = new ArrayList<>();
		List<NurseSiteMaintainConfig> currentSiteConfig = listNurseSiteMaintainConfig.stream().filter(item -> selectedSite.getSiteUID().equals(item.getSiteUID())).collect(Collectors.toList());
		showListNurseSiteMaintainConfig.addAll(currentSiteConfig);
		listCheckClinicalOrgInfo = nurseSiteMaintainService.findAccessOrgDeptList(selectedSite.getSiteUID());
		loadUserParentOrgInfo();
		setDefaultParentOrgInfo();
		customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.NURSE_SITE_MAINTAIN);
	}
	public List<NurseSiteMaintainExport> getMaintainDataList(String assetUid){
		return nurseSiteMaintainService.getMaintainDataList(assetUid);
	}

	private List<NurseSiteMaintain> searchMaintainData(Date start, Date end) {
		if (currentUser.isEnableAssetTypeGroup() && ObjectUtil.isEmpty(currentUser.getAssetTypeGroupList())) {
			return ListUtil.empty();
		}
		Calendar ca = Calendar.getInstance();
		if (start == null) {
			ca.set(Calendar.DAY_OF_MONTH, 1);
			start = ca.getTime();
		}
		if (end == null) {
			ca.add(Calendar.MONTH, 1);
			end = ca.getTime();
		}

		Map<String, Object> param = MapUtil.newHashMap();
		param.put("scheduleDateStart", start);
		param.put("scheduleDateEnd", end);
		param.put("tenantUid", currentUser.getTenantUID());
		param.put("orgUidList", orgUidList);
		if (ObjectUtil.isNotEmpty(selectedDept)) {
			param.put("deptUid", selectedDept.getUid());
		}
		if (ObjectUtil.isNotEmpty(selectedSite)) {
			param.put("siteUid", selectedSite.getUid());
		}
		if (StringUtils.isNotEmpty(selectedStatus)) {
			param.put("status", selectedStatus);
		}
//		if (currentUser.isEnableAssetTypeGroup()) {
//			param.put("assetTypes", currentUser.getAssetTypeGroupMap().keySet());
//		}
		return nurseSiteMaintainService.searchMaintain(param);
	}

	private void loadMaintainData() {
		if (initStartDate != null && initEndDate != null) {
			listMaintainData = searchMaintainData(initStartDate, initEndDate);
		}
		lazyEventModel = new LazyScheduleModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void loadEvents(Date start, Date end) {
				if (initStartDate == null || initEndDate == null || start.getTime() != initStartDate.getTime() || end.getTime() != initEndDate.getTime()) {
					listMaintainData = searchMaintainData(start, end);
					initStartDate = start;
					initEndDate = end;
				}
				if (listMaintainData != null) {
					for (NurseSiteMaintain data : listMaintainData) {
						DefaultScheduleEvent tempEvent = new DefaultScheduleEvent(data.getShowTitle(), data.getScheduleDate(), data.getScheduleDate(), true);
						tempEvent.setData(data);
						Integer status = data.getStatus();
						if (status != null) {
							if (status == 0) {
								tempEvent.setStyleClass("schedule-item-nocheck");
							} else if (status == 2) {
								tempEvent.setStyleClass("schedule-item-pass");
							} else if (status == 5) {
								tempEvent.setStyleClass("schedule-item-pass-c");
							} else if (status == 6) {
								tempEvent.setStyleClass("schedule-item-pass-cd");
							} else if (status == 7) {
								tempEvent.setStyleClass("schedule-item-pass-d");
							} else if (status == 3) {
								tempEvent.setStyleClass("schedule-item-nopass-d");
							} else if (status == 8) {
								tempEvent.setStyleClass("schedule-item-nopass");
							} else if (status == 4) {
								tempEvent.setStyleClass("schedule-item-partialpass");
							}else if (status == 9) {
								tempEvent.setStyleClass("schedule-item-nopass-partialpass");
							}
						}
						tempEvent.setId(UUID.randomUUID().toString());
						if (lazyEventModel.getEvents().add(tempEvent)) {
						} else {
							updateEvent(tempEvent);
						}
					}
				}
			}
		};
	}

	private void loadUserParentOrgInfo() {
		checkListParentOrgInfo = nurseSiteMaintainService.getListParentSiteByUid(orgUidList);
		listOrgInfo = new ArrayList<>();
	}

	private void setDefaultParentOrgInfo() {
		selectedParentOrgInfo = nurseSiteMaintainService.findOrgInfoByUid(currentUser.getSiteUID());
		loadUserOrgInfo();
	}

	private void loadUserOrgInfo() {
		listOrgInfo = nurseSiteMaintainService.getListClinicalByUidAndParent(orgUidList, selectedParentOrgInfo.getUid());
	}

	private void loadMaintainConfig() {
		if (nurseSiteMaintainConfig != null) {
			String configId = nurseSiteMaintainConfig.getId();
			listCheckAssets = nurseSiteMaintainService.getListAssetInfoByMaintainConfigParentId(configId);
			if (userClickDate == null) {
				userClickDate = new Date();
			}
			Integer tempType = nurseSiteMaintainConfig.getType();
			String tempTypeValue = nurseSiteMaintainConfig.getTypeValue();
			Integer tempIntTypeValue = null;
			if (listSequence == null) {
				listSequence = new ArrayList<>();
			} else {
				listSequence.clear();
			}
			if (tempType != null && tempType == 1) {
				tempIntTypeValue = Integer.parseInt(tempTypeValue);
				for (int i = 1; i <= tempIntTypeValue; ++i) {
					listSequence.add(i);
				}
			}
			nurseSiteMaintain = nurseSiteMaintainService.createMaintain(userClickDate, currentUser, nurseSiteMaintainConfig, listCheckAssets, selectSequence);
		}
	}

	private void batchCreateMaintainData(Date date, List<NurseSiteMaintain> listMaintainData,NurseSiteMaintainConfig nurseSiteMaintainConfig, Integer sequence) {
		NurseSiteMaintain tempNurseSiteMaintain = nurseSiteMaintainService.createMaintainWithoutItem(date, currentUser, nurseSiteMaintainConfig, sequence);
		listMaintainData.add(tempNurseSiteMaintain);
	}

	private void addErrMessage(String str) {
		RequestContext.getCurrentInstance().addCallbackParam("validationFailed", true);
		WebUtil.addErrorMessageKey(str);
	}
	private void addMessage(String str) {
		FacesMessage message = new FacesMessage(str, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	private void insertMaintain(NurseSiteMaintain data) {
		if (listMaintainData == null) {
			listMaintainData = new ArrayList<NurseSiteMaintain>();
		}
		int beforeSize = listMaintainData.size();
		Date afterDate = data.getScheduleDate();
		boolean add = false;
		for (int i = 0; i < listMaintainData.size(); ++i) {
			Date tempDate = listMaintainData.get(i).getScheduleDate();
			try {
				if (sdf.parse(sdf.format(tempDate)).compareTo(sdf.parse(sdf.format(afterDate))) == 0) {
					add = true;
				} else {
					if (add) {
						listMaintainData.add(i, data);
						return;
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				LOGGER.error(e.getMessage());
			}
		}
		int afterSize = listMaintainData.size();
		if (afterSize == beforeSize) {
			listMaintainData.add(data);
		}
	}

	// private void generateNurseSiteMaintainData(){
	// List<String> showLables=nurseSiteMaintain.getShowLables();
	// nurseSiteMaintainService.supplementMaintain(nurseSiteMaintain);
	// List<NurseSiteMaintainItem>
	// listItem=nurseSiteMaintain.getListNurseSiteMaintainItem();
	// if(showLables==null){
	// showLables=new ArrayList<String>();
	// }
	// else{
	// showLables.clear();
	// }
	// try {
	// if(listItem!=null && listItem.size()>0){
	// List<NurseSiteMaintainData>
	// listTempData=listItem.iterator().next().getListData();
	// if(listTempData!=null){
	// Iterator<NurseSiteMaintainData> it=listTempData.iterator();
	// while(it.hasNext()){
	// NurseSiteMaintainData tempData=it.next();
	// showLables.add(tempData.getCheckItem());
	// }
	//// showLables.sort(new StringComparator());
	// }
	// }
	// nurseSiteMaintain.setShowLables(showLables);
	// } catch (Exception e) {
	// // TODO: handle exception
	// LOGGER.error("getLables error"+e.getMessage());
	// }
	// }

	public void checkRightAll() {
		nurseSiteMaintainService.checkRightAll(nurseSiteMaintain);
	}

	public void saveMaintain() {
		if(!checkRequired())return;
		Date currentDate = new Date();
		if (nurseSiteMaintainService.saveMaintain(nurseSiteMaintain, currentDate, currentUser)) {
			lazyEventModel.updateEvent(event);
			addMessage("工单已保存");
		}
	}
	private boolean checkRequired(){
		checkRequired = Boolean.TRUE;
		if(nurseSiteMaintain == null) return checkRequired;
		List<NurseSiteMaintainItem> listNurseSiteMaintainItem = nurseSiteMaintain.getListNurseSiteMaintainItem();
		if(CollectionUtils.isEmpty(listNurseSiteMaintainItem)) return checkRequired;
		for (NurseSiteMaintainItem nurseSiteMaintainItem : listNurseSiteMaintainItem) {
			List<NurseSiteMaintainData> listData = nurseSiteMaintainItem.getListData();
			if(CollectionUtils.isEmpty(listData)) continue;
			checkItemDataRequired(nurseSiteMaintainItem,listData);
		}
		return checkRequired;
	}

	private void checkItemDataRequired(NurseSiteMaintainItem nurseSiteMaintainItem,List<NurseSiteMaintainData> listData) {
		String str="必填检查项未填写，不允许保存";
		String assetName = nurseSiteMaintainItem.getAssetInfo().getName();
		boolean hasChecked = listData.stream().anyMatch(item ->
				!Boolean.TRUE.equals(item.getIsSkip()) &&
				(item.getCheckValue() != null || (StringUtils.isNotBlank(item.getVariableText()) && !Boolean.FALSE.toString().equalsIgnoreCase(item.getVariableText()))));
		if(!hasChecked) return;
		for (NurseSiteMaintainData listDatum : listData) {
			// 忽略的检查项不参与校验
			if(Boolean.TRUE.equals(listDatum.getIsSkip())) continue;
			Boolean required = listDatum.getRequired();
			if(Boolean.TRUE.equals(required)){
				Boolean checkValue = listDatum.getCheckValue();
				String variableText = listDatum.getVariableText();
				if(checkValue == null || StringUtils.isBlank(variableText)){
					checkRequired = Boolean.FALSE;
					String checkItem = listDatum.getCheckItem();
					addErrMessage(assetName+",检查项："+checkItem+" (勾选项、输入项), "+str);
				}
			}
			if(Boolean.FALSE.equals(listDatum.getCheckValue()) && StringUtils.isEmpty(listDatum.getErrorText())){
				checkRequired = Boolean.FALSE;
				String checkItem = listDatum.getCheckItem();
				addErrMessage(assetName+",检查项："+checkItem+" 异常说明不能为空");
			}
		}
	}

	public void createMaintain() {
		if (nurseSiteMaintainConfig == null) {
			addMessage("请创建并选择配置模板");
			return;
		}
		// Calendar ca=Calendar.getInstance();
		// int currentHour=ca.get(Calendar.HOUR_OF_DAY);
		// ca.setTime(userClickDate);
		// ca.set(Calendar.HOUR_OF_DAY,currentHour);
		nurseSiteMaintain = nurseSiteMaintainService.createMaintain(userClickDate, currentUser, nurseSiteMaintainConfig, listCheckAssets, selectSequence);
		if (nurseSiteMaintainService.existMaintain(nurseSiteMaintain)) {
			addMessage("工单已存在");
			return;
		}
		nurseSiteMaintainService.saveMaintain(nurseSiteMaintain);
		// event = new
		// DefaultScheduleEvent(getShowTitle(nurseSiteMaintain),userClickDate,
		// userClickDate, true);
		// event.setData(nurseSiteMaintain);
		// lazyEventModel.addEvent(event);
		insertMaintain(nurseSiteMaintain);
		addMessage("工单已创建");
	}

	public void onCommentClick(NurseSiteMaintainItem item) {
		boolean temp = item.isBlnEditComment();
		item.setBlnEditComment(!temp);
	}

	public void deleteMaintain() {
		if (event.getId() != null) {
			if (lazyEventModel.deleteEvent(event)) {
				nurseSiteMaintain = (NurseSiteMaintain) event.getData();
				nurseSiteMaintainService.deleteMaintain(nurseSiteMaintain, currentUser.getUid());
				listMaintainData.remove(nurseSiteMaintain);
				addMessage("工单已删除");
			}
		}
	}

	public void updateEvent() {
		if (event.getId() != null) {
			lazyEventModel.updateEvent(event);
		}
	}

	public void saveMaintainSignature() {
		if (StringUtils.isNotEmpty(signatureContent)) {
//			String objectStorageId = nurseSiteMaintainService.saveMaintainSignature(signatureContent, nurseSiteMaintain.getId());
//			nurseSiteMaintain.setSignatureId(objectStorageId);
			// 同时保存为新附件形式（多签名支持）
			nurseSiteMaintainService.saveMaintainSignatureAttachment(signatureContent, nurseSiteMaintain.getId());
			// 刷新签名列表并定位到最新签名，保持签名板可继续输入
			signatureContent = null;
			signatureContentStream = null;
			loadSignatureList();
			if (signatureList != null && !signatureList.isEmpty()) {
				currentSignatureIndex = signatureList.size() - 1;
				loadCurrentSignatureImage();
			}
			showSignaturePad = false;
		}
	}

	public void getMaintainSignature(String signatureId, Integer signatureSourceType) {
		signatureContent = null;
		signatureContentWeiXin = null;
		signatureContentStream = null;
		currentSignatureContent = null;
		currentSignatureIndex = 0;
		showSignaturePad = false;
		loadSignatureList();
		if (signatureList != null && !signatureList.isEmpty()) {
			currentSignatureIndex = signatureList.size() - 1;
			loadCurrentSignatureImage();
		} else if (StringUtils.isNotEmpty(signatureId)) {
			// 兼容旧签名
			if (signatureSourceType == null) {
				blnWebSignatureEidt = true;
			} else if (signatureSourceType == 0) {
				signatureContentStream = nurseSiteMaintainService.getMaintainSignatureWebToStream(signatureId);
				blnWebSignatureEidt = false;
			} else if (signatureSourceType == 1) {
				signatureContentWeiXin = nurseSiteMaintainService.getMaintainSignatureWeiXin(signatureId);
				blnWebSignatureEidt = false;
			}
		} else {
			blnWebSignatureEidt = true;
		}
	}

	private void loadSignatureList() {
		signatureList = new ArrayList<>();
		if (nurseSiteMaintain != null && nurseSiteMaintain.getId() != null) {
			List<V2_BlobObject> attachmentSignatures = nurseSiteMaintainService.findMaintainSignatures(nurseSiteMaintain.getId());
			if (attachmentSignatures != null) {
				signatureList.addAll(attachmentSignatures);
			}
		}
	}

	private void loadCurrentSignatureImage() {
		if (signatureList == null || signatureList.isEmpty() || currentSignatureIndex < 0 || currentSignatureIndex >= signatureList.size()) {
			currentSignatureContent = null;
			return;
		}
		try {
			V2_BlobObject blobObject = signatureList.get(currentSignatureIndex);
			String objectStorageId = blobObject.getObjectStorageId();
			String objectType = blobObject.getObjectType();
			if (objectType != null && objectType.contains("text")) {
				currentSignatureContent = nurseSiteMaintainService.getMaintainSignatureWebToStream(objectStorageId);
			} else {
				currentSignatureContent = nurseSiteMaintainService.getMaintainSignatureWeiXin(objectStorageId);
			}
			blnWebSignatureEidt = false;
		} catch (Exception e) {
			LOGGER.error("loadCurrentSignatureImage error: {}", e.getMessage());
			currentSignatureContent = null;
		}
	}

	public void nextSignature() {
		if (signatureList != null && currentSignatureIndex < signatureList.size() - 1) {
			currentSignatureIndex++;
			loadCurrentSignatureImage();
		}
	}

	public void previousSignature() {
		if (signatureList != null && currentSignatureIndex > 0) {
			currentSignatureIndex--;
			loadCurrentSignatureImage();
		}
	}

	public void selectSignature(int index) {
		if (signatureList != null && index >= 0 && index < signatureList.size()) {
			currentSignatureIndex = index;
			loadCurrentSignatureImage();
		}
	}

	public void addNewSignature() {
		signatureContent = null;
		signatureContentStream = null;
		showSignaturePad = true;
	}

	private static final int MAX_SIGNATURE_COUNT = 6;

	public boolean isCanAddSignature() {
		return signatureList == null || signatureList.size() < MAX_SIGNATURE_COUNT;
	}

	public void deleteSignature() {
		if (signatureList == null || signatureList.isEmpty()) {
			return;
		}

		V2_BlobObject currentBlob = signatureList.get(currentSignatureIndex);
		boolean deleted = nurseSiteMaintainService.deleteMaintainSignature(currentBlob.getId());
		if (deleted) {
			signatureList.remove(currentSignatureIndex);
			if (signatureList.isEmpty()) {
				currentSignatureContent = null;
				showSignaturePad = true;
			} else {
				if (currentSignatureIndex >= signatureList.size()) {
					currentSignatureIndex = signatureList.size() - 1;
				}
				loadCurrentSignatureImage();
			}
		}
	}

	public String getSignatureCountText() {
		if (signatureList == null || signatureList.isEmpty()) {
			return "";
		}
		return "签名 " + (currentSignatureIndex + 1) + "/" + signatureList.size();
	}

	public int getSignatureCount() {
		return signatureList == null ? 0 : signatureList.size();
	}

	public boolean isHasMultipleSignatures() {
		return signatureList != null && signatureList.size() > 1;
	}

	public boolean isHasPreviousSignature() {
		return signatureList != null && currentSignatureIndex > 0;
	}

	public boolean isHasNextSignature() {
		return signatureList != null && currentSignatureIndex < signatureList.size() - 1;
	}

	public boolean isShowSignaturePad() {
		return showSignaturePad;
	}

	public void setShowSignaturePad(boolean showSignaturePad) {
		this.showSignaturePad = showSignaturePad;
	}

	public void resetMaintainSignature() {
		signatureContent = null;
		signatureContentStream = null;
	}

	public void resetBatchSignatures() {
		batchSignatureList = new ArrayList<>();
		batchCurrentSignatureIndex = 0;
		showBatchSignaturePad = true;
		signatureContent = null;
	}

	public void saveBatchSignature() {
		if (StringUtils.isNotEmpty(signatureContent)) {
			if (batchSignatureList == null) {
				batchSignatureList = new ArrayList<>();
			}
			batchSignatureList.add(signatureContent);
			signatureContent = null;
			batchCurrentSignatureIndex = batchSignatureList.size() - 1;
			showBatchSignaturePad = false;
		}
	}

	public int getBatchSignatureCount() {
		return batchSignatureList == null ? 0 : batchSignatureList.size();
	}

	public String getBatchSignatureCountText() {
		if (batchSignatureList == null || batchSignatureList.isEmpty()) return "";
		return "签名 " + (batchCurrentSignatureIndex + 1) + "/" + batchSignatureList.size();
	}

	public boolean isHasBatchMultipleSignatures() {
		return batchSignatureList != null && batchSignatureList.size() > 1;
	}

	public boolean isHasBatchPreviousSignature() {
		return batchSignatureList != null && batchCurrentSignatureIndex > 0;
	}

	public boolean isHasBatchNextSignature() {
		return batchSignatureList != null && batchCurrentSignatureIndex < batchSignatureList.size() - 1;
	}

	public void batchNextSignature() {
		if (batchSignatureList != null && batchCurrentSignatureIndex < batchSignatureList.size() - 1) {
			batchCurrentSignatureIndex++;
		}
	}

	public void batchPreviousSignature() {
		if (batchSignatureList != null && batchCurrentSignatureIndex > 0) {
			batchCurrentSignatureIndex--;
		}
	}

	public void batchSelectSignature(int index) {
		if (batchSignatureList != null && index >= 0 && index < batchSignatureList.size()) {
			batchCurrentSignatureIndex = index;
		}
	}

	public void addBatchNewSignature() {
		signatureContent = null;
		showBatchSignaturePad = true;
	}

	public boolean isCanAddBatchSignature() {
		return batchSignatureList == null || batchSignatureList.size() < MAX_SIGNATURE_COUNT;
	}

	public void deleteBatchSignature() {
		if (batchSignatureList == null || batchSignatureList.isEmpty()) {
			return;
		}
		batchSignatureList.remove(batchCurrentSignatureIndex);
		if (batchSignatureList.isEmpty()) {
			batchCurrentSignatureIndex = 0;
			showBatchSignaturePad = true;
			signatureContent = null;
		} else {
			if (batchCurrentSignatureIndex >= batchSignatureList.size()) {
				batchCurrentSignatureIndex = batchSignatureList.size() - 1;
			}
		}
	}

	public StreamedContent getBatchCurrentSignatureImage() {
		if (batchSignatureList == null || batchSignatureList.isEmpty()
				|| batchCurrentSignatureIndex < 0 || batchCurrentSignatureIndex >= batchSignatureList.size()) {
			return null;
		}
		try {
			String base64 = batchSignatureList.get(batchCurrentSignatureIndex);
			DefaultStreamedContent streamedContent = new DefaultStreamedContent();
			streamedContent.setStream(new ByteArrayInputStream(ImageUtils.convertPrimefacesBase64ToByte(base64)));
			return streamedContent;
		} catch (Exception e) {
			LOGGER.error("getBatchCurrentSignatureImage error: {}", e.getMessage());
			return null;
		}
	}

	public boolean isShowBatchSignaturePad() {
		return showBatchSignaturePad;
	}

	public void setShowBatchSignaturePad(boolean showBatchSignaturePad) {
		this.showBatchSignaturePad = showBatchSignaturePad;
	}

	public void onNurseSiteMaintainConfigChange() {
		loadMaintainConfig();
	}

	public void onClinicalOrgInfoChange() {
		if (showListNurseSiteMaintainConfig == null) {
			showListNurseSiteMaintainConfig = new ArrayList<>();
		} else {
			showListNurseSiteMaintainConfig.clear();
		}

		if (ObjectUtil.isNotEmpty(selectedDept)) {
			showListNurseSiteMaintainConfig = listNurseSiteMaintainConfig.stream().filter(item -> selectedDept.getUid().equals(item.getClinicalDeptUID())).collect(Collectors.toList());
		} else if (ObjectUtil.isNotEmpty(selectedSite)) {
			showListNurseSiteMaintainConfig = listNurseSiteMaintainConfig.stream().filter(item -> selectedSite.getUid().equals(item.getSiteUID())).collect(Collectors.toList());
		}
		loadMaintainData();
	}

	public void onClinicalParentOrgInfoChange() {
		loadUserOrgInfo();
	}

	public void onEventSelect(SelectEvent selectEvent) {
		event = (DefaultScheduleEvent) selectEvent.getObject();
//		nurseSiteMaintain = (NurseSiteMaintain) event.getData();
		nurseSiteMaintain = nurseSiteMaintainService.findByIds(Collections.singleton(((NurseSiteMaintain) event.getData()).getId())).get(0);
		// generateNurseSiteMaintainData();
		maintainItemController.reset(nurseSiteMaintain, this);
	}

	public void onDateSelect(SelectEvent selectEvent) {
		userClickDate = (Date) selectEvent.getObject();
	}

	// public void onEventMove(ScheduleEntryMoveEvent event) {
	// FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" +
	// event.getMinuteDelta());
	// }
	// public void onEventResize(ScheduleEntryResizeEvent event) {
	// FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
	// "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" +
	// event.getMinuteDelta());
	// }

	public String batchSaveMaintainData() {
		if (listCheckedNurseSiteMaintainConfig == null || listCheckedNurseSiteMaintainConfig.size()<1) {
			addMessage("请创建并选择配置模板");
			return "maintainMain?faces-redirect=false";
		}
		if (startBatchDate == null) {
			startBatchDate = new Date();
		}
		if (endBatchDate == null) {
			endBatchDate = new Date();
		}
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endBatchDate);
		Calendar maxCalendar = Calendar.getInstance();
		maxCalendar.setTime(startBatchDate);
		maxCalendar.add(Calendar.YEAR, 1);
		if (endCalendar.getTimeInMillis() > maxCalendar.getTimeInMillis()) {
			addMessage("请选择一年之内的日期");
			return "maintainMain?faces-redirect=false";
		}
		for(int i=0;i<listCheckedNurseSiteMaintainConfig.size();++i){
			batchSaveMaintainData(listCheckedNurseSiteMaintainConfig.get(i));
		}
		addMessage("工单详情后台生成中，请耐心等待。。。");
		return "maintainMain?faces-redirect=true";
	}

	public String batchDeleteMaintainData(){
		if (listCheckedNurseSiteMaintainConfig == null || listCheckedNurseSiteMaintainConfig.size()<1) {
			addMessage("请创建并选择配置模板");
			return "maintainMain?faces-redirect=false";
		}
		if (startBatchDate == null) {
			startBatchDate = new Date();
		}
		if (endBatchDate == null) {
			endBatchDate = new Date();
		}
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endBatchDate);
		Calendar maxCalendar = Calendar.getInstance();
		maxCalendar.setTime(startBatchDate);
		maxCalendar.add(Calendar.YEAR, 1);
		if (endCalendar.getTimeInMillis() > maxCalendar.getTimeInMillis()) {
			addMessage("请选择一年之内的日期");
			return "maintainMain?faces-redirect=false";
		}
		nurseSiteMaintainService.batchDeleteMaintainData(startBatchDate,endBatchDate,listCheckedNurseSiteMaintainConfig,currentUser.getUid());
		addMessage("工单详情后台删除中，请耐心等待。。。");
		return "maintainMain?faces-redirect=true";
	}

	private void batchSaveMaintainData(NurseSiteMaintainConfig nurseSiteMaintainConfig){
		Calendar currentCalendar = Calendar.getInstance();
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startBatchDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endBatchDate);
		Integer type = nurseSiteMaintainConfig.getType();
		String typeValue = nurseSiteMaintainConfig.getTypeValue();
		List<NurseSiteMaintain> listMaintainData = new ArrayList<>();
		Integer sum = 0;
		if (StringUtils.isNotEmpty(typeValue)) {
			String[] arr = typeValue.split(",");
			if (type == 1) {
				// value,hours,1-24
				int intTypeValue = Integer.parseInt(typeValue);
				while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
					String currName = nurseSiteMaintainConfig.getName();
					List<NurseSiteMaintain> ns = nurseSiteMaintainService.checkAgain(currName,startCalendar.getTime());
					if(!ns.isEmpty()){
						sum++;
						startCalendar.add(Calendar.DAY_OF_YEAR, 1);
						continue;
					}
					for (int i = 0; i < intTypeValue; ++i) {
						Date date = null;
						try {
							date = startCalendar.getTime();
						} catch (Exception e) {
							LOGGER.error(e.getMessage());
						}
						batchCreateMaintainData(date, listMaintainData, nurseSiteMaintainConfig,i + 1);
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
					if (listMaintainData.size() > 30) {
						nurseSiteMaintainService.save(listMaintainData);
						listMaintainData.clear();
					}
				}
			} else if (type == 2) {
				// weekday,1-7
				while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
					String currName = nurseSiteMaintainConfig.getName();
					List<NurseSiteMaintain> ns = nurseSiteMaintainService.checkAgain(currName,startCalendar.getTime());
					if(ns.size()>0){
						sum++;
						startCalendar.add(Calendar.DAY_OF_YEAR, 1);
						continue;
					}
					for (int i = 0; i < arr.length; ++i) {
						try {
							if (startCalendar.get(Calendar.DAY_OF_WEEK) == Integer.parseInt(arr[i])) {
								startCalendar.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY));
								batchCreateMaintainData(startCalendar.getTime(), listMaintainData,nurseSiteMaintainConfig, null);
							}
						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.error(e.getMessage());
						}
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
					if (listMaintainData.size() > 30) {
						nurseSiteMaintainService.save(listMaintainData);
						listMaintainData.clear();
					}
				}
			} else if (type == 3) {
				// month day,1-31
				while (startCalendar.getTimeInMillis() <= endCalendar.getTimeInMillis()) {
					String currName = nurseSiteMaintainConfig.getName();
					List<NurseSiteMaintain> ns = nurseSiteMaintainService.checkAgain(currName,startCalendar.getTime());
					if(ns.size()>0){
						sum++;
						startCalendar.add(Calendar.DAY_OF_YEAR, 1);
						continue;
					}
					int currentDay = startCalendar.get(Calendar.DAY_OF_MONTH);
					int currentMonth = startCalendar.get(Calendar.MONTH);
					int currentValue = 1;
					for (int i = 0; i < arr.length; ++i) {
						try {
							currentValue = Integer.parseInt(arr[i]);
							if (currentDay == currentValue) {
								startCalendar.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY));
								batchCreateMaintainData(startCalendar.getTime(), listMaintainData,nurseSiteMaintainConfig, null);
							}
						} catch (Exception e) {
							// TODO: handle exception
							LOGGER.error(e.getMessage());
						}
					}
					startCalendar.add(Calendar.DAY_OF_YEAR, 1);
					int nextMonth = startCalendar.get(Calendar.MONTH);
					if (nextMonth != currentMonth) {
						if (currentDay < currentValue) {
							startCalendar.add(Calendar.DAY_OF_YEAR, -1);
							startCalendar.set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY));
							batchCreateMaintainData(startCalendar.getTime(), listMaintainData, nurseSiteMaintainConfig,null);
							startCalendar.add(Calendar.DAY_OF_YEAR, 1);
						}
					}
					if (listMaintainData.size() > 30) {
						nurseSiteMaintainService.save(listMaintainData);
						listMaintainData.clear();
					}
				}
			}
		}
		if(!sum.equals(0)){
			addMessage("部分工单已创建，已跳过");
		}
		if (!listMaintainData.isEmpty()) {
			nurseSiteMaintainService.save(listMaintainData);
		}
		nurseSiteMaintainService.addBatchJob("create", nurseSiteMaintainConfig, startBatchDate, endBatchDate, currentUser.getUid(), null);
	}

	public String batchCompleteMaintainData() {
		if (startBatchDate == null) {
			startBatchDate = new Date();
		}
		if (endBatchDate == null) {
			endBatchDate = new Date();
		}
		Date currentDate = new Date();
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(currentDate); calendar2.setTime(startBatchDate);
		if(calendar1.get(Calendar.MONTH)>calendar2.get(Calendar.MONTH) && userContextService.accessContainsOfJiangNing() && !userContextService.hasRole("AssetHead")) {
			WebUtil.addErrorMessage("只能批量完成当月的单子！");
			return "maintainMain?faces-redirect=true";
		}
		List<NurseSiteMaintain> listNewNurseSiteMaintain = ListUtil.newArrayList();
		for (NurseSiteMaintain temp : listMaintainData) {
			long scheduleTime = temp.getScheduleDate().getTime();
			boolean flag = scheduleTime >= startBatchDate.getTime()
					&& scheduleTime <= endBatchDate.getTime()
					&& (ObjectUtil.isNotEmpty(selectedDept) ? temp.getClinicalDeptUID().equals(selectedDept.getUid()) : temp.getSiteUID().equals(selectedSite.getUid()));
			if (flag) {
				listNewNurseSiteMaintain.add(temp);
			}
		}
		if (!listNewNurseSiteMaintain.isEmpty()) {
			// 合并待签名的签名内容：已确认的批量签名 + 当前签名板上未确认的
			List<String> allSignatures = new ArrayList<>();
			if (batchSignatureList != null) {
				allSignatures.addAll(batchSignatureList);
			}
			if (StringUtils.isNotEmpty(signatureContent)) {
				allSignatures.add(signatureContent);
			}
			String signatureId = null;
			for (String sig : allSignatures) {
				signatureId = nurseSiteMaintainService.saveMaintainSignature(sig, "");
			}
			if (signatureId == null) {
				signatureId = "";
			}
			nurseSiteMaintainService.completeMaintain(listNewNurseSiteMaintain, signatureId, currentDate, currentUser);
			// 保存所有签名为附件（多签名支持）
			if (!allSignatures.isEmpty()) {
				for (NurseSiteMaintain wo : listNewNurseSiteMaintain) {
					for (String sig : allSignatures) {
						nurseSiteMaintainService.saveMaintainSignatureAttachment(sig, wo.getId());
					}
				}
			}
			Set<String> clinicalDeptSet = listNewNurseSiteMaintain.stream().map(NurseSiteMaintain::getClinicalDeptUID).collect(Collectors.toSet());
			for (String clinicalDeptUid : clinicalDeptSet) {
				nurseSiteMaintainService.addBatchJob("complete", null, startBatchDate, endBatchDate, currentUser.getUid(),
						signatureId + "," + clinicalDeptUid);
			}
			addMessage("工单后台完成中，请稍候刷新页面查看。");
			// 清空批量签名状态
			batchSignatureList = null;
			signatureContent = null;
		} else {
			addMessage("未找到符合条件的工单");
		}
		return "maintainMain?faces-redirect=true";
	}

	private OrgInfo selectedSite;
	private OrgInfo selectedDept;

	@Override
	public void onServerEvent(String eventName, Object eventObject) {
		OrgInfo tempOrg = (OrgInfo) eventObject;
		if (listCheckClinicalOrgInfo != null) {
			listCheckClinicalOrgInfo.clear();
		} else {
			listCheckClinicalOrgInfo = new ArrayList<>();
		}
		if (tempOrg != null) {
			if (!Objects.equals(tempOrg.getOrgType(), 4)) {
				listCheckClinicalOrgInfo.addAll(nurseSiteMaintainService.findAccessOrgDeptList(tempOrg.getSiteUID()));
				selectedSite = tempOrg;
			} else {
				listCheckClinicalOrgInfo.add(tempOrg);
				selectedDept = tempOrg;
			}
		} else {
			selectedDept = null;
		}
		onClinicalOrgInfoChange();
	}

	public void onCheckChange(){

		listCheckedNurseSiteMaintainConfig = new ArrayList<>();

		if(this.isSelectAll){
			listCheckedNurseSiteMaintainConfig.addAll(listNurseSiteMaintainConfig);
		}

	}

	public void onCheckDefault(){
		listCheckedNurseSiteMaintainConfig = new ArrayList<>();
		setSelectAll(false);
	}

	public void onStatusChange() {
		loadMaintainData();
	}

	public String getSelectedStatus() {
		return selectedStatus;
	}

	public void setSelectedStatus(String selectedStatus) {
		this.selectedStatus = selectedStatus;
	}

	public UserAccount getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(UserAccount currentUser) {
		this.currentUser = currentUser;
	}

	public ScheduleModel getLazyEventModel() {
		return lazyEventModel;
	}

	public void setLazyEventModel(ScheduleModel lazyEventModel) {
		this.lazyEventModel = lazyEventModel;
	}

	public DefaultScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(DefaultScheduleEvent event) {
		this.event = event;
	}

	public NurseSiteMaintain getNurseSiteMaintain() {
		return nurseSiteMaintain;
	}

	public void setNurseSiteMaintain(NurseSiteMaintain nurseSiteMaintain) {
		this.nurseSiteMaintain = nurseSiteMaintain;
	}

	public Date getStartBatchDate() {
		return startBatchDate;
	}

	public void setStartBatchDate(Date startBatchDate) {
		this.startBatchDate = startBatchDate;
	}

	public Date getEndBatchDate() {
		return endBatchDate;
	}

	public void setEndBatchDate(Date endBatchDate) {
		this.endBatchDate = endBatchDate;
	}

	public boolean isDeleteDisable() {
		return isDeleteDisable;
	}

	public void setDeleteDisable(boolean isDeleteDisable) {
		this.isDeleteDisable = isDeleteDisable;
	}

	public boolean isSaveDisable() {
		return isSaveDisable;
	}

	public void setSaveDisable(boolean isSaveDisable) {
		this.isSaveDisable = isSaveDisable;
	}

	public List<NurseSiteMaintainConfig> getListNurseSiteMaintainConfig() {
		return listNurseSiteMaintainConfig;
	}

	public void setListNurseSiteMaintainConfig(List<NurseSiteMaintainConfig> listNurseSiteMaintainConfig) {
		this.listNurseSiteMaintainConfig = listNurseSiteMaintainConfig;
	}

	public NurseSiteMaintainConfig getNurseSiteMaintainConfig() {
		return nurseSiteMaintainConfig;
	}

	public void setNurseSiteMaintainConfig(NurseSiteMaintainConfig nurseSiteMaintainConfig) {
		this.nurseSiteMaintainConfig = nurseSiteMaintainConfig;
	}

	public List<AssetInfo> getListCheckAssets() {
		return listCheckAssets;
	}

	public void setListCheckAssets(List<AssetInfo> listCheckAssets) {
		this.listCheckAssets = listCheckAssets;
	}

	public List<OrgInfo> getListOrgInfo() {
		return listOrgInfo;
	}

	public void setListOrgInfo(List<OrgInfo> listOrgInfo) {
		this.listOrgInfo = listOrgInfo;
	}

	public List<OrgInfo> getListCheckClinicalOrgInfo() {
		return listCheckClinicalOrgInfo;
	}

	public void setListCheckClinicalOrgInfo(List<OrgInfo> listCheckClinicalOrgInfo) {
		this.listCheckClinicalOrgInfo = listCheckClinicalOrgInfo;
	}

	public List<NurseSiteMaintainConfig> getShowListNurseSiteMaintainConfig() {
		return showListNurseSiteMaintainConfig;
	}

	public void setShowListNurseSiteMaintainConfig(List<NurseSiteMaintainConfig> showListNurseSiteMaintainConfig) {
		this.showListNurseSiteMaintainConfig = showListNurseSiteMaintainConfig;
	}

	public String getSignatureContent() {
		return signatureContent;
	}

	public void setSignatureContent(String signatureContent) {
		this.signatureContent = signatureContent;
	}

	public StreamedContent getSignatureContentStream() {
		return signatureContentStream;
	}

	public void setSignatureContentStream(StreamedContent signatureContentStream) {
		this.signatureContentStream = signatureContentStream;
	}

	public StreamedContent getSignatureContentWeiXin() {
		return signatureContentWeiXin;
	}

	public void setSignatureContentWeiXin(StreamedContent signatureContentWeiXin) {
		this.signatureContentWeiXin = signatureContentWeiXin;
	}

	public boolean isBlnWebSignatureEidt() {
		return blnWebSignatureEidt;
	}

	public void setBlnWebSignatureEidt(boolean blnWebSignatureEidt) {
		this.blnWebSignatureEidt = blnWebSignatureEidt;
	}

	public List<Integer> getListSequence() {
		return listSequence;
	}

	public void setListSequence(List<Integer> listSequence) {
		this.listSequence = listSequence;
	}

	public Integer getSelectSequence() {
		return selectSequence;
	}

	public void setSelectSequence(Integer selectSequence) {
		this.selectSequence = selectSequence;
	}

	public OrgInfo getSelectedParentOrgInfo() {
		return selectedParentOrgInfo;
	}

	public void setSelectedParentOrgInfo(OrgInfo selectedParentOrgInfo) {
		this.selectedParentOrgInfo = selectedParentOrgInfo;
	}

	public List<OrgInfo> getCheckListParentOrgInfo() {
		return checkListParentOrgInfo;
	}

	public void setCheckListParentOrgInfo(List<OrgInfo> checkListParentOrgInfo) {
		this.checkListParentOrgInfo = checkListParentOrgInfo;
	}

	public List<NurseSiteMaintainConfig> getListCheckedNurseSiteMaintainConfig() {
		return listCheckedNurseSiteMaintainConfig;
	}

	public void setListCheckedNurseSiteMaintainConfig(List<NurseSiteMaintainConfig> listCheckedNurseSiteMaintainConfig) {
		this.listCheckedNurseSiteMaintainConfig = listCheckedNurseSiteMaintainConfig;
	}

	public UserAuth getCustomRoleAuth() {
		return customRoleAuth;
	}

	public void setCustomRoleAuth(UserAuth customRoleAuth) {
		this.customRoleAuth = customRoleAuth;
	}

	public boolean isSelectAll() {
		return isSelectAll;
	}

	public void setSelectAll(boolean selectAll) {
		isSelectAll = selectAll;
	}

	public Date getCurrentDate() {
		return new Date();
	}

	public StreamedContent getCurrentSignatureContent() {
		return currentSignatureContent;
	}

	public void setCurrentSignatureContent(StreamedContent currentSignatureContent) {
		this.currentSignatureContent = currentSignatureContent;
	}

	public List<V2_BlobObject> getSignatureList() {
		return signatureList;
	}

	public void setSignatureList(List<V2_BlobObject> signatureList) {
		this.signatureList = signatureList;
	}

	public int getCurrentSignatureIndex() {
		return currentSignatureIndex;
	}

	public void setCurrentSignatureIndex(int currentSignatureIndex) {
		this.currentSignatureIndex = currentSignatureIndex;
	}

	public List<String> getBatchSignatureList() {
		return batchSignatureList;
	}

	public int getBatchCurrentSignatureIndex() {
		return batchCurrentSignatureIndex;
	}
}

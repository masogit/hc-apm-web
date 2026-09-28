package com.ge.apm.view.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.CaptureDataMessagesRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.CaptureDataMessages;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.CaptureDataMessagesService;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.view.sysutil.UserContextService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by 503035627
 */
@ManagedBean
@ViewScoped
public class CaptureDataMessagesController extends GenericCRUDUUIDController<CaptureDataMessages>{
	private static final long serialVersionUID = 1L;
	private CaptureDataMessagesRepository dao;
	private CaptureDataMessagesService service;
	private AssetInfoRepository assetInfoRepository;
	private I18nMessageRepository i18nDao;
	private UserAccount currentUser;
	private String content;
	private int conentTextLength = 120;
	private UploadedFile file;
	private String fileName;
	private List<CaptureDataMessages> importFailedDataList;
	private String tempImageContent;
	private AssetInfo assetInfo;
	private String checkedSystemId;

	@Override
	protected void init() {
		dao = WebUtil.getBean(CaptureDataMessagesRepository.class);
		assetInfoRepository = WebUtil.getBean(AssetInfoRepository.class);
		service = WebUtil.getBean(CaptureDataMessagesService.class);
		i18nDao = WebUtil.getBean(I18nMessageRepository.class);
		currentUser = UserContextService.getCurrentUserAccount();
	}

	@Override
	protected GenericRepositoryUUID<CaptureDataMessages> getDAO() {
		return dao;
	}

	@Override
	public void prepareCreate() throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		super.prepareCreate();
		resetValue();
	}

	public void prepareGEIBData() {
		if (this.selected != null) {
			this.prepareEdit();
			if (Objects.equals(this.selected.getType(), 1)) {
				this.selected.setGeDeviceType(0);
				this.selected.setIbStatus(0);
				this.selected.setType(0);
			}
		} else {
			try {
				this.prepareCreate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage());
			}
		}
	}

	public void geIBDataSave() {
		if (assetInfo != null) {
			assetInfo.setIbStatus(this.selected.getIbStatus());
			assetInfo.setGeDeviceType(this.selected.getGeDeviceType());
		} else {
			getAssetInfo();
			if (assetInfo == null) {
				FacesMessage message = new FacesMessage("注意！保存失败,SystemID对应设备未找到");
				FacesContext.getCurrentInstance().addMessage("other", message);
				return;
			} else {
				assetInfo.setIbStatus(this.selected.getIbStatus());
				assetInfo.setGeDeviceType(this.selected.getGeDeviceType());
			}
		}
		assetInfoRepository.save(assetInfo);
		this.selected = null;
		FacesMessage message = new FacesMessage("保存成功！");
		FacesContext.getCurrentInstance().addMessage("other", message);
	}

	@Override
	public void prepareEdit() {
		// TODO Auto-generated method stub
		super.prepareEdit();
		resetValue();
		if (this.selected.getType() != null && Objects.equals(this.selected.getType(), 1))
			return;
		assetInfo = service.getAssetInfoBySystemId(this.selected.getSystemId());
		if (assetInfo != null) {
			setAssetInfoValue();
		}
	}

	private void resetValue() {
		assetInfo = null;
		file = null;
		fileName = null;
		tempImageContent = null;
		importFailedDataList = null;
		checkedSystemId = null;
	}

	@Override
	protected Page<CaptureDataMessages> loadData(PageRequest pageRequest) {
		if (searchFilters == null) {
			searchFilters = new ArrayList<>();
		}
		searchFilters.add(new SearchFilter("tenantUID", SearchFilter.Operator.EQ, currentUser.getTenantUID()));
		if (Objects.equals(currentUser.getOrgLevel(), 1)) {
			searchFilters.add(new SearchFilter("institutionUID", SearchFilter.Operator.EQ, currentUser.getInstitutionUID()));
		} else if (Objects.equals(currentUser.getOrgLevel(), 2)) {
			searchFilters.add(new SearchFilter("hospitalUID", SearchFilter.Operator.EQ, currentUser.getHospitalUID()));
		} else {
			searchFilters.add(new SearchFilter("siteUID", SearchFilter.Operator.EQ, currentUser.getSiteUID()));
		}
		return dao.findBySearchFilter(searchFilters, pageRequest);
	}
	@Override
	public void onBeforeNewObject(CaptureDataMessages object) {
		super.onBeforeNewObject(object);
		object.setTenantUID(currentUser.getTenantUID());
		object.setInstitutionUID(currentUser.getInstitutionUID());
		object.setHospitalUID(currentUser.getHospitalUID());
		object.setSiteUID(currentUser.getSiteUID());
	}

	public void onTabChanged(TabChangeEvent event) {
		String tabName = event.getTab().getId();
		if ("type0".equals(tabName)) {
			this.selected.setType(0);
			checkSystemId();
		} else if ("type1".equals(tabName)) {
			this.selected.setType(1);
		}
	}

	private void getAssetInfo() {
		checkedSystemId = this.selected.getSystemId();
		if (StringUtils.isNotEmpty(checkedSystemId)) {
			assetInfo = service.getAssetInfoBySystemId(checkedSystemId);
		}
	}

	private void setAssetInfoValue() {
		this.selected.setIbStatus(assetInfo.getIbStatus());
		this.selected.setGeDeviceType(assetInfo.getGeDeviceType());
		this.selected.setAssetUID(assetInfo.getUid());
	}

	public void checkSystemId() {

		if (this.selected.getType() != null && Objects.equals(this.selected.getType(), 1))
			return;
		getAssetInfo();
		if (assetInfo == null) {
			FacesMessage message = new FacesMessage("注意", "找不到输入SystemId对应的设备");
			FacesContext.getCurrentInstance().addMessage("systemID", message);
		} else {
			setAssetInfoValue();
			OrgInfo orgInfo = assetInfo.getHospital();
			if (orgInfo != null && orgInfo.getName() != null) {
				FacesMessage message = new FacesMessage("注意",
						"该systemID对应" + "医院:" + orgInfo.getName() + "，设备名：" + assetInfo.getName() + "，对应使用科室：" + assetInfo.getClinicalDeptName());
				FacesContext.getCurrentInstance().addMessage("systemID", message);
			} else {
				FacesMessage message = new FacesMessage("注意", "该systemID对应" + "设备名：" + assetInfo.getName() + "，对应使用科室：" + assetInfo.getClinicalDeptName());
				FacesContext.getCurrentInstance().addMessage("systemID", message);
			}
		}
	}

	@Override
	public void save() {
		if (this.selected.getType() == null) {
			this.selected.setType(0);
		}
		if (this.selected.getContent() != null){
			String replacedContent  = StringEscapeUtils.escapeHtml4(this.selected.getContent());
			this.selected.setContent(replacedContent);
		}
		if (Objects.equals(this.selected.getType(), 0)) {
			if (checkedSystemId == null || checkedSystemId.equals(this.selected.getSystemId()) == false) {
				getAssetInfo();
			}
			if (assetInfo != null) {
				setIndexValue();
				super.save();
				assetInfo.setIbStatus(this.selected.getIbStatus());
				assetInfo.setGeDeviceType(this.selected.getGeDeviceType());
				assetInfoRepository.save(assetInfo);
			} else {
				FacesMessage message = new FacesMessage("注意！保存失败,SystemID对应设备未找到");
				FacesContext.getCurrentInstance().addMessage("other", message);
			}
		} else if (Objects.equals(this.selected.getType(), 1)) {
			if (this.selected.getGeDeviceType() == null) {
				FacesMessage message = new FacesMessage("注意！保存失败，设备类型不能为空");
				FacesContext.getCurrentInstance().addMessage("other", message);
			} else {
				setIndexValue();
				this.selected.setSystemId("");
				this.selected.setIbStatus(0);
				super.save();
			}
		} else{
			String content = this.selected.getContent();
			if(!content.contains("data:image/jpeg;base64")){
				FacesMessage message = new FacesMessage("注意！保存失败，请插入图片");
				FacesContext.getCurrentInstance().addMessage("other", message);
			}else {
				setIndexValue();
				this.selected.setSystemId("");
				this.selected.setIbStatus(0);
				this.selected.setComment("banner image");
				super.save();
			}

		}
		this.selected = null;
	}

	public boolean validateRequiredField() {
		if (Objects.equals(this.selected.getType(), 0)) {
			if (this.selected.getSystemId() == null) {
				return false;
			}
			if (this.selected.getIbStatus() == null) {
				return false;
			}
		} else if (Objects.equals(this.selected.getType(), 1)) {
			if (this.selected.getGeDeviceType() == null) {
				return false;
			}
		}
		if (StringUtils.isEmpty(this.selected.getContent())) {
			return false;
		}
		return true;
	}

	private void setIndexValue() {
		CaptureDataMessages captureDataMessages = null;
		if (Objects.equals(this.selected.getType(), 0)) {
			captureDataMessages = dao.findBySystemId(this.selected.getSystemId());
		} else if (Objects.equals(this.selected.getType(), 1)) {
			captureDataMessages = dao.findByGeDeviceType(this.selected.getGeDeviceType());
		}
		if (captureDataMessages != null) {
			Integer index = captureDataMessages.getIndex();
			if (index != null) {
				this.selected.setIndex(index + 1);
			} else {
				this.selected.setIndex(0);
			}
		} else {
			this.selected.setIndex(0);
		}
	}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		super.delete();
		this.selected = null;
	}

	public void handleImageUpload(FileUploadEvent event) {
		this.file = event.getFile();
		fileName = null;
		try {
			if (file == null || file.getInputstream() == null) {
				FacesMessage message = new FacesMessage("注意！解析图片失败！");
				FacesContext.getCurrentInstance().addMessage("other", message);
				tempImageContent = null;
				return;
			} else {
				String fileName = file.getFileName();
				if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".png")) {

				} else {
					FacesMessage message = new FacesMessage("注意！请上传jpg或者png格式的图片！");
					FacesContext.getCurrentInstance().addMessage("other", message);
					tempImageContent = null;
					return;
				}
				// 780831
				if (file.getInputstream().available() > 5242880) {
					FacesMessage message = new FacesMessage("注意！请上传大小不超过5M的图片！");
					FacesContext.getCurrentInstance().addMessage("other", message);
					tempImageContent = null;
					return;
				}
				String imageString = ImageUtils.getBase64(file.getInputstream());
				imageString = "<img src=\"data:image/jpeg;base64," + imageString + "\">";
				tempImageContent = imageString;
				setFileName(file.getFileName());
				FacesMessage message = new FacesMessage("图片上传成功！");
				FacesContext.getCurrentInstance().addMessage("other", message);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			FacesMessage message = new FacesMessage("注意！解析图片失败！");
			FacesContext.getCurrentInstance().addMessage("other", message);
			tempImageContent = null;
			return;
		}

	}

	public void insertImage() {
		if (StringUtils.isNotEmpty(tempImageContent)) {
			this.selected.setContent(this.selected.getContent() + tempImageContent);
		}
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTempImageContent() {
		return tempImageContent;
	}

	public void setTempImageContent(String tempImageContent) {
		this.tempImageContent = tempImageContent;
	}

	public List<CaptureDataMessages> getImportFailedDataList() {
		return importFailedDataList;
	}

	public void setImportFailedDataList(List<CaptureDataMessages> importFailedDataList) {
		this.importFailedDataList = importFailedDataList;
	}

	public void setContent(String id) {
		content = dao.getContentById(id);
	}

	public String getContent() {
		if (StringUtils.isNotEmpty(content)) {
			return content;
		}
		return "";
	}
}

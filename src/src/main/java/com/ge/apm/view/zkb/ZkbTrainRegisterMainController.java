package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbEngineerService;
import com.ge.apm.service.zkb.ZkbTrainCourseQuotaService;
import com.ge.apm.service.zkb.ZkbTrainPublishRegisterService;
import com.ge.apm.view.sysutil.UserContextService;
import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class ZkbTrainRegisterMainController<E> extends GenericCRUDUUIDController<E> {

    private ZkbTrainPublishRegisterService zkbTrainPublishRegisterService;
    private ZkbTrainPublishRepository dao;
    protected UserAccount currentUser = UserContextService.getCurrentUserAccount();

    @Getter
    @Setter
    private ZkbEngineer selectedEngineer;
    @Getter
    @Setter
    private ZkbTrainPublishRegisterDetail selectedDetail;
    @Getter
    @Setter
    private ZkbTrainPublishRegister zkbTrainPublishRegister;
    @Getter
    @Setter
    private List<ZkbEngineer> engineerList;
    @Getter
    @Setter
    private List<ZkbTrainPublishRegisterDetail> zkbTrainPublishRegisterDetailList = new ArrayList<>();
    @Getter
    @Setter
    private String actionName = "";
    @Getter
    @Setter
    private List<V2_BlobObject> v2_blobObjectList = new ArrayList<>();
    @Getter
    @Setter
    private List<ZkbTrainPublishRegister> allRegistrations;

    // 专项附件引用
    @Getter
    @Setter
    private V2_BlobObject physicalExamFile;
    @Getter
    @Setter
    private V2_BlobObject safetyCertificateFile;
    @Getter
    @Setter
    private V2_BlobObject recommendationFile;

    @Getter
    @Setter
    private ZkbEngineerService zkbEngineerService;
    @Getter
    @Setter
    private ZkbEngineer newEngineer = new ZkbEngineer();
    @Getter
    @Setter
    private List<V2_BlobObject> newEngineerV2BlobObjectList = new ArrayList<>();
    @Getter
    @Setter
    private List<V2_BlobObject> removeNewEngineerFileList = new ArrayList<>();
    @Getter
    @Setter
    private String selectionTenantInfo;

    private static final String ssoStartWith = "ZKB";

    // 控制当前视图：edit（编辑）或 create（新建）
    @Getter
    @Setter
    private String engineerFormView = "edit"; // 默认为编辑视图

    private BlobFileService blobService;

    private ZkbTrainCourseQuotaService zkbTrainCourseQuotaService;

    @Override
    protected void init() {
        super.init();
        zkbTrainPublishRegisterService = WebUtil.getBean(ZkbTrainPublishRegisterService.class);
        zkbEngineerService = WebUtil.getBean(ZkbEngineerService.class);
        zkbTrainCourseQuotaService = WebUtil.getBean(ZkbTrainCourseQuotaService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        this.selectionTenantInfo = UserContextService.getCurrentUserAccount().getTenantUID();
        this.selectedDetail = null;
    }


    public void prepareRegister(String pid) {
        zkbTrainPublishRegister = new ZkbTrainPublishRegister();
        zkbTrainPublishRegister.setPid(pid);
        this.actionName = "Register";
    }

    public void prepareModifyRegister(String id) {
        zkbTrainPublishRegister = zkbTrainPublishRegisterService.getRegisterById(id);
        zkbTrainPublishRegisterDetailList = zkbTrainPublishRegister.getZkbTrainPublishRegisterDetails();
        this.actionName = "Modify";
    }

    public void prepareDetailCreate() {
        initEngineerList();
        selectedEngineer = null;
        selectedDetail = new ZkbTrainPublishRegisterDetail();
        v2_blobObjectList = new ArrayList<>();
        engineerFormView = "edit"; // 重置为编辑视图
    }

    public void prepareDetailEdit() {
        initEngineerList();
        selectedEngineer = zkbTrainPublishRegisterService.findEngineerById(selectedDetail.getEngineerId());
        if ("Register".equals(actionName)) {
            v2_blobObjectList = selectedDetail.getV2_blobObjectList();
        } else {
            v2_blobObjectList = zkbTrainPublishRegisterService.queryBlobObject(selectedDetail.getId());
            selectedDetail.setV2_blobObjectList(v2_blobObjectList);
        }
        refreshSpecialFiles();
        engineerFormView = "edit";
    }

    public List<ZkbEngineer> autoCompleteEngineer(String query) {
        return engineerList.stream()
                .filter(item -> Strings.isNullOrEmpty(query) ||
                        item.getName().contains(query) || item.getSso().contains(query))
                .collect(Collectors.toList());
    }

    public void initEngineerList() {
        engineerList = zkbTrainPublishRegisterService.queryEngineerListByTenant(currentUser);
    }

    public void deleteDetail() {
        if ("Register".equals(actionName)) {
            zkbTrainPublishRegisterDetailList.remove(selectedDetail);
        } else {
            zkbTrainPublishRegisterService.deleteDetail(selectedDetail, currentUser);
            zkbTrainPublishRegisterDetailList.remove(selectedDetail);
        }
        selectedDetail = null;
    }

    public void removeFile(V2_BlobObject v2BlobObject) {
        v2_blobObjectList.remove(v2BlobObject);
        blobService.deleteBlobFile(v2BlobObject);
        refreshSpecialFiles(); // 重新绑定引用
    }

    public void engineerChange() {
        if (selectedEngineer != null) {
            selectedDetail.setEngineerId(selectedEngineer.getId());
            selectedDetail.setEngineerName(selectedEngineer.getName());
            selectedDetail.setEngineerPhone(selectedEngineer.getPhone());
            selectedDetail.setEngineerEmail(selectedEngineer.getEmail());
            selectedDetail.setEngineerAge(selectedEngineer.getAge());
            selectedDetail.setEngineerSso(selectedEngineer.getSso());
        }
        physicalExamFile = new V2_BlobObject();
        safetyCertificateFile = new V2_BlobObject();
        recommendationFile = new V2_BlobObject();
        selectedDetail.setV2_blobObjectList(new ArrayList<>());
        refreshSpecialFiles();
    }

    public void handleFileUpload(FileUploadEvent event) {
        V2_BlobObject file = blobService.uploadFile(event);
        if (file != null) {
            file.setBoType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            file.setBoSubType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            v2_blobObjectList.add(file);
            WebUtil.addSuccessMessage("上传成功: " + file.getObjectName());
        } else {
            WebUtil.addErrorMessage("上传失败");
        }
    }

    public void handlePhysicalExamUpload(FileUploadEvent event) {
        V2_BlobObject file = blobService.uploadFile(event);
        if (file != null) {
            file.setBoType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            file.setBoSubType(V2_BlobObject.BO_SUB_TYPE_PHYSICAL_EXAM);
            v2_blobObjectList.removeIf(f -> V2_BlobObject.BO_SUB_TYPE_PHYSICAL_EXAM.equals(f.getBoSubType()));
            v2_blobObjectList.add(file);
            physicalExamFile = file;
            WebUtil.addSuccessMessage("放射体检证明上传成功");
        }
    }

    public void handleSafetyCertificateUpload(FileUploadEvent event) {
        V2_BlobObject file = blobService.uploadFile(event);
        if (file != null) {
            file.setBoType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            file.setBoSubType(V2_BlobObject.BO_SUB_TYPE_SAFETY_CERT);
            v2_blobObjectList.removeIf(f -> V2_BlobObject.BO_SUB_TYPE_SAFETY_CERT.equals(f.getBoSubType()));
            v2_blobObjectList.add(file);
            safetyCertificateFile = file;
            WebUtil.addSuccessMessage("安全证书上传成功");
        }
    }

    public void handleRecommendationUpload(FileUploadEvent event) {
        V2_BlobObject file = blobService.uploadFile(event);
        if (file != null) {
            file.setBoType(V2_BlobObject.BO_TYPE_TRAIN_REGISTER);
            file.setBoSubType(V2_BlobObject.BO_SUB_TYPE_RECOMMENDATION);
            v2_blobObjectList.removeIf(f -> V2_BlobObject.BO_SUB_TYPE_RECOMMENDATION.equals(f.getBoSubType()));
            v2_blobObjectList.add(file);
            recommendationFile = file;
            WebUtil.addSuccessMessage("推荐函上传成功");
        }
    }

    private void refreshSpecialFiles() {
        if (v2_blobObjectList == null) return;
        physicalExamFile = getFileBySubType(v2_blobObjectList, V2_BlobObject.BO_SUB_TYPE_PHYSICAL_EXAM);
        safetyCertificateFile = getFileBySubType(v2_blobObjectList, V2_BlobObject.BO_SUB_TYPE_SAFETY_CERT);
        recommendationFile = getFileBySubType(v2_blobObjectList, V2_BlobObject.BO_SUB_TYPE_RECOMMENDATION);
    }

    private V2_BlobObject getFileBySubType(List<V2_BlobObject> list, String subType) {
        return list.stream()
                .filter(f -> subType.equals(f.getBoSubType()))
                .findFirst()
                .orElse(null);
    }

    public void saveEngineer() {
        selectedDetail.setV2_blobObjectList(v2_blobObjectList);
        selectedDetail.setRegisterId(zkbTrainPublishRegister.getId());
        if (StringUtils.isNotBlank(selectedDetail.getId())) {
            selectedDetail.setCreatedBy(currentUser.getName());
            selectedDetail.setCreatedDate(new Date());
        } else {
            selectedDetail.setLastModifiedBy(currentUser.getName());
            selectedDetail.setLastModifiedDate(new Date());
        }
        if ("Register".equals(actionName)) {
            selectedDetail.setId(UUID.randomUUID().toString());
            zkbTrainPublishRegisterDetailList.add(selectedDetail);
        } else {
            zkbTrainPublishRegisterService.modifyDetail(selectedDetail, v2_blobObjectList, currentUser);
            zkbTrainPublishRegisterDetailList = zkbTrainPublishRegisterService.queryDetailByRegister(zkbTrainPublishRegister.getId());
        }
        selectedDetail = null;
    }

    public void saveData() {
        zkbTrainPublishRegisterService.saveData(zkbTrainPublishRegister, zkbTrainPublishRegisterDetailList, currentUser);
    }


    @Override
    protected GenericRepositoryUUID<E> getDAO() {
        return null;
    }

    public List<ZkbTrainPublishRegister> getRegisterByPid(String pid) {
        allRegistrations = zkbTrainPublishRegisterService.getRegisterByPid(pid);
        return allRegistrations;
    }

    // ========== 新增工程师逻辑 ==========
    public void prepareCreateEngineer() {
        this.newEngineer = new ZkbEngineer();
        this.newEngineer.setTenantUID(this.selectionTenantInfo);
        this.newEngineer.setCreatedBy(this.currentUser.getLoginName());
        this.newEngineer.setCreatedDate(new Date());
        this.zkbEngineerService.fullSso(this.newEngineer, ssoStartWith);
        this.newEngineerV2BlobObjectList = new ArrayList<>();
        this.removeNewEngineerFileList = new ArrayList<>();
        this.engineerFormView = "create"; // 切换到新建视图
    }

    public void createEngineer() {
        try {
            if (newEngineer == null || StringUtils.isBlank(newEngineer.getName())) {
                WebUtil.addErrorMessage("工程师姓名不能为空");
                return;
            }
            zkbEngineerService.save(newEngineer);
            zkbEngineerService.saveUploadFile(newEngineerV2BlobObjectList, newEngineer.getId());

            this.selectedEngineer = newEngineer;
            if (this.selectedEngineer == null) {
                WebUtil.addErrorMessage("工程师保存后无法查询，请检查数据库");
                return;
            }

            this.engineerList = zkbTrainPublishRegisterService.queryEngineerListByTenant(currentUser);

            WebUtil.addSuccessMessage("工程师 [" + newEngineer.getName() + "] 创建成功");

            this.engineerChange();

            this.newEngineer = new ZkbEngineer();
            this.v2_blobObjectList = new ArrayList<>();
            this.newEngineerV2BlobObjectList.clear();
            this.removeNewEngineerFileList.clear();
            refreshSpecialFiles();
            this.engineerFormView = "edit";

            PrimeFaces.current().executeScript("PF('registerEngineerEditDlg').show();");

        } catch (Exception e) {
            WebUtil.addErrorMessage("创建失败: " + e.getMessage());
        }
    }

    public void handleNewEngineerFileUpload(FileUploadEvent event) {
        V2_BlobObject file = blobService.uploadFile(event);
        if (file != null) {
            file.setBoType(V2_BlobObject.BO_TYPE_ZKB_ENGINEER);
            file.setBoSubType(V2_BlobObject.BO_TYPE_ZKB_ENGINEER);
            newEngineerV2BlobObjectList.add(file);
            WebUtil.addSuccessMessage("上传成功: " + file.getObjectName());
        }
    }

    public void removeNewEngineerFile(V2_BlobObject file) {
        newEngineerV2BlobObjectList.remove(file);
        if (StringUtils.isNotBlank(file.getId())) {
            removeNewEngineerFileList.add(file);
        }
        WebUtil.addSuccessMessage("已删除: " + file.getObjectName());
    }

    public void cancelCreateEngineer() {
        this.newEngineer = new ZkbEngineer();
        this.newEngineerV2BlobObjectList.clear();
        this.removeNewEngineerFileList.clear();
        this.engineerFormView = "edit";
    }

    public TenantInfo getTenantInfo(String tenantInfoUID) {
        return zkbEngineerService.getTenantInfo(tenantInfoUID);
    }

    @Getter
    @Setter
    private StreamedContent exportFile;

    public void downLoadTemplate() {
        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/zkb/trainV2/register/template_1.doc");
        exportFile = new DefaultStreamedContent(stream, "application/vnd.ms-word", "公司培训学员推荐函.doc");
    }


    /**
     * 取消报名：删除指定的报名记录
     * 限制：培训开始前3天内不允许取消
     *
     * @param id ZkbTrainPublishRegister 的主键 ID
     */
    public void cancelRegister(String id) {
        if (Strings.isNullOrEmpty(id)) {
            WebUtil.addErrorMessage("报名记录ID无效，无法取消。");
            return;
        }
        try {
            ZkbTrainPublishRegister register = zkbTrainPublishRegisterService.getRegisterById(id);
            if (register == null) {
                WebUtil.addErrorMessage("未找到该报名记录，无法取消。");
                return;
            }
            ZkbTrainPublishInfo publishInfo = register.getZkbTrainPublishInfo();
            if (publishInfo == null) {
                WebUtil.addErrorMessage("报名信息不完整，无法处理取消请求。");
                return;
            }
            Date trainStartDate = publishInfo.getTrainStartDate();
            if (trainStartDate == null) {
                WebUtil.addErrorMessage("培训开始时间未设置，无法判断是否可取消。");
                return;
            }
            // 计算培训开始时间减去3天（即最后可取消时间）
            Calendar cal = Calendar.getInstance();
            cal.setTime(trainStartDate);
            cal.add(Calendar.DAY_OF_MONTH, -3); // 培训开始前3天
            Date latestCancelDate = cal.getTime();
            Date now = new Date();
            // 如果当前时间 > 最后可取消时间，则不允许取消
            if (now.after(latestCancelDate)) {
                WebUtil.addErrorMessage("报名已进入不可取消期（培训开始前3天内），无法取消。");
                return;
            }
            // === 关键修改点：获取该报名下的学员数量 ===
            List<ZkbTrainPublishRegisterDetail> details = register.getZkbTrainPublishRegisterDetails();
            int detailCount = CollectionUtils.isNotEmpty(details) ? details.size() : 0;

            if (detailCount <= 0) {
                logger.warn("报名记录没有关联的学员信息，直接删除。");
            }

            zkbTrainPublishRegisterService.cancelRegister(register, currentUser);
            zkbTrainPublishRegisterDetailList = new ArrayList<>();
            WebUtil.addSuccessMessage("报名已成功取消，归还 " + detailCount + " 个名额。");


        } catch (Exception e) {
            logger.error("取消报名失败，ID: " + id, e);
            WebUtil.addErrorMessage("取消报名失败。");
        }
    }

}
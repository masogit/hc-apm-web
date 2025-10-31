package com.ge.apm.view.zkb;

import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.zkb.ZkbEngineerService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    private Logger logger = LoggerFactory.getLogger(ZkbTrainRegisterMainController.class);

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

    @Getter
    @Setter
    private String viewMode = "";

    private BlobFileService blobService;

    @Getter
    @Setter
    private ZkbTrainPublishRegisterDetail editingDetail; // 用于当前正在编辑的临时对象

    @Override
    protected void init() {
        super.init();
        zkbTrainPublishRegisterService = WebUtil.getBean(ZkbTrainPublishRegisterService.class);
        zkbEngineerService = WebUtil.getBean(ZkbEngineerService.class);
        blobService = WebUtil.getBean(BlobFileService.class);
        this.selectionTenantInfo = UserContextService.getCurrentUserAccount().getTenantUID();
        this.selectedDetail = null;
    }


    public void prepareRegister(String pid) {
        zkbTrainPublishRegister = new ZkbTrainPublishRegister();
        zkbTrainPublishRegister.setPid(pid);
        this.actionName = "Register";
        this.zkbTrainPublishRegisterDetailList = new ArrayList<>(); // 重置详情列表
    }

    public void prepareModifyRegister(String id) {
        zkbTrainPublishRegister = zkbTrainPublishRegisterService.getRegisterById(id);
        zkbTrainPublishRegisterDetailList = zkbTrainPublishRegister.getZkbTrainPublishRegisterDetails();
        this.actionName = "Modify";
    }

    public void prepareDetailCreate() {
        initEngineerList();
        selectedEngineer = null;

        // 创建一个新的临时对象，而不是直接赋给 selectedDetail
        this.editingDetail = new ZkbTrainPublishRegisterDetail();
        this.v2_blobObjectList = new ArrayList<>();
        this.physicalExamFile = null;
        this.safetyCertificateFile = null;
        this.recommendationFile = null;

        engineerFormView = "edit";
    }

    public void prepareDetailEdit() {
        if (selectedDetail == null) return;

        initEngineerList();

        // 复制 selectedDetail 到 editingDetail
        this.editingDetail = new ZkbTrainPublishRegisterDetail();
        BeanUtils.copyProperties(selectedDetail, editingDetail);

        // 设置工程师信息
        selectedEngineer = zkbTrainPublishRegisterService.findEngineerById(selectedDetail.getEngineerId());

        // 加载附件 - 区分不同操作模式
        if ("Register".equals(actionName)) {
            // 在注册模式下，使用 selectedDetail 的附件
            this.v2_blobObjectList = new ArrayList<>(selectedDetail.getV2_blobObjectList());
        } else {
            // 在修改模式下，查询数据库获取附件
            this.v2_blobObjectList = zkbTrainPublishRegisterService.queryBlobObject(selectedDetail.getId());
            editingDetail.setV2_blobObjectList(new ArrayList<>(v2_blobObjectList));
        }

        // 刷新专项附件引用
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
        if (selectedEngineer != null && editingDetail != null) {
            editingDetail.setEngineerId(selectedEngineer.getId());
            editingDetail.setEngineerName(selectedEngineer.getName());
            editingDetail.setEngineerPhone(selectedEngineer.getPhone());
            editingDetail.setEngineerEmail(selectedEngineer.getEmail());
            editingDetail.setEngineerAge(selectedEngineer.getAge());
            editingDetail.setEngineerSso(selectedEngineer.getSso());

            // 清空附件（因为选择了新的工程师）
            v2_blobObjectList.clear();
            physicalExamFile = null;
            safetyCertificateFile = null;
            recommendationFile = null;

            PrimeFaces.current().ajax().update("engineerEditForm");
        }
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
            // 移除已存在的同类附件
            if (CollectionUtils.isNotEmpty(v2_blobObjectList)) {
                v2_blobObjectList.removeIf(f -> V2_BlobObject.BO_SUB_TYPE_PHYSICAL_EXAM.equals(f.getBoSubType()));
            }
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
            // 移除已存在的同类附件
            if (CollectionUtils.isNotEmpty(v2_blobObjectList)) {
                v2_blobObjectList.removeIf(f -> V2_BlobObject.BO_SUB_TYPE_SAFETY_CERT.equals(f.getBoSubType()));
            }
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
            // 移除已存在的同类附件
            if (CollectionUtils.isNotEmpty(v2_blobObjectList)) {
                v2_blobObjectList.removeIf(f -> V2_BlobObject.BO_SUB_TYPE_RECOMMENDATION.equals(f.getBoSubType()));
            }
            v2_blobObjectList.add(file);
            recommendationFile = file;
            WebUtil.addSuccessMessage("推荐函上传成功");
        }
    }

    private void refreshSpecialFiles() {
        if (CollectionUtils.isEmpty(v2_blobObjectList)) {
            physicalExamFile = null;
            safetyCertificateFile = null;
            recommendationFile = null;
            return;
        }

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
        if (!checkAttachments()) {
            WebUtil.addErrorMessage("附件不全，请上传放射体检、安全证书和推荐函");
            return;
        }

        // 将附件绑定到 editingDetail
        editingDetail.setV2_blobObjectList(new ArrayList<>(v2_blobObjectList));

        // 设置 registerId（临时）
        editingDetail.setRegisterId(zkbTrainPublishRegister.getId());

        // 如果是新建，生成 ID
        if (StringUtils.isBlank(editingDetail.getId())) {
            editingDetail.setId(UUID.randomUUID().toString());
            editingDetail.setCreatedBy(currentUser.getName());
            editingDetail.setCreatedDate(new Date());
        } else {
            // 如果是编辑，更新信息
            editingDetail.setLastModifiedBy(currentUser.getName());
            editingDetail.setLastModifiedDate(new Date());

            // 从主列表中移除旧的详情记录
            zkbTrainPublishRegisterDetailList.removeIf(detail -> detail.getId().equals(editingDetail.getId()));
        }

        // 添加到主列表
        zkbTrainPublishRegisterDetailList.add(editingDetail);

        // 重置编辑状态
        this.editingDetail = null;
        this.selectedDetail = null;
        this.v2_blobObjectList.clear();
        this.physicalExamFile = null;
        this.safetyCertificateFile = null;
        this.recommendationFile = null;

        // 刷新 UI 列表
        PrimeFaces.current().ajax().update("engineerListForm:detailDatalist");
    }

    public void saveData() {
        if (!checkAttachments()) {
            WebUtil.addErrorMessage("附件不全，请上传放射体检、安全证书和推荐函");
            return;
        }
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

    /**
     * 判断当前用户是否可以修改或取消该报名
     */
    public boolean canModify(String registerId) {
        if (Strings.isNullOrEmpty(registerId)) return false;

        try {
            ZkbTrainPublishRegister register = zkbTrainPublishRegisterService.getRegisterById(registerId);
            if (register == null || register.getZkbTrainPublishInfo() == null) {
                return false;
            }
            Date trainStartDate = register.getZkbTrainPublishInfo().getTrainStartDate();
            if (trainStartDate == null) return true; // 若无日期，默认允许

            Calendar cal = Calendar.getInstance();
            cal.setTime(trainStartDate);
            cal.add(Calendar.DAY_OF_MONTH, -3); // 开始前3天为最后修改时间
            Date latestCancelDate = cal.getTime();

            return new Date().before(latestCancelDate);
        } catch (Exception e) {
            logger.error("检查修改权限失败", e);
            return false;
        }
    }

    /**
     * 准备查看报名信息（只读）
     */
    public void prepareViewRegister(String id) {
        zkbTrainPublishRegister = zkbTrainPublishRegisterService.getRegisterById(id);
        if (zkbTrainPublishRegister != null) {
            zkbTrainPublishRegisterDetailList = zkbTrainPublishRegister.getZkbTrainPublishRegisterDetails();
            // === 关键：为每个 detail 加载附件 ===
//            for (ZkbTrainPublishRegisterDetail detail : zkbTrainPublishRegisterDetailList) {
//                List<V2_BlobObject> files = zkbTrainPublishRegisterService.queryBlobObject(detail.getId());
//                detail.setV2_blobObjectList(files);
//            }
            this.actionName = "View";
            this.viewMode = "view";
        }
    }

    public void prepareViewDetail(String id) {
        this.selectedDetail = zkbTrainPublishRegisterService.getDetailById(id);
        this.editingDetail = this.selectedDetail;
        this.actionName = "View";
        this.viewMode = "view";
    }

    public boolean checkAttachments() {
        boolean hasRecommendation = v2_blobObjectList.stream()
                .anyMatch(f -> "RECOMMENDATION".equals(f.getBoSubType()));
        boolean hasPhysical = v2_blobObjectList.stream()
                .anyMatch(f -> "PHYSICAL_EXAM".equals(f.getBoSubType()));
        boolean hasSafety = v2_blobObjectList.stream()
                .anyMatch(f -> "SAFETY_CERT".equals(f.getBoSubType()));
        return hasPhysical && hasSafety && hasRecommendation;
    }

}
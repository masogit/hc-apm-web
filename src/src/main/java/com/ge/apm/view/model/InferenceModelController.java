package com.ge.apm.view.model;

import com.ge.apm.dao.InferenceModelRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.InferenceModel;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.model.InferenceModelService;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.onnx.ModelTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;
import webapp.framework.dao.SearchFilter;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 推理模型管理页面控制器
 * 提供推理模型的列表、上传、下载、删除功能
 */
@ManagedBean
@ViewScoped
public class InferenceModelController extends GenericCRUDUUIDController<InferenceModel> {

    private static final Logger logger = LoggerFactory.getLogger(InferenceModelController.class);

    private InferenceModelRepository inferenceModelRepository;
    private InferenceModelService inferenceModelService;
    private BlobFileService blobFileService;
    private TenantInfoRepository tenantInfoRepository;
    private UserAccount currentUser;

    @Getter @Setter
    private TenantInfo selectTenant;

    @Getter @Setter
    private String selectTenantUid;

    @Getter @Setter
    private UploadedFile file;

    @Getter @Setter
    private V2_BlobObject uploadedFileObject;

    @Getter @Setter
    private InferenceModel editingModel;

    /** 列表过滤：模型类型 */
    @Getter @Setter
    private String filterType;

    /** 列表过滤：状态 */
    @Getter @Setter
    private Boolean filterEnabled;

    @Override
    protected GenericRepositoryUUID<InferenceModel> getDAO() {
        return inferenceModelRepository;
    }

    @Override
    protected void init() {
        inferenceModelRepository = WebUtil.getBean(InferenceModelRepository.class);
        inferenceModelService = WebUtil.getBean(InferenceModelService.class);
        blobFileService = WebUtil.getBean(BlobFileService.class);
        tenantInfoRepository = WebUtil.getBean(TenantInfoRepository.class);
        currentUser = UserContextService.getCurrentUserAccount();
        selectTenant = tenantInfoRepository.findByuid(currentUser.getTenantUID());
        selectTenantUid = selectTenant.getUid();
    }

    @Override
    protected Page<InferenceModel> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            this.searchFilters = new ArrayList<>();
        }
        String tenantUid = selectTenant != null ? selectTenant.getUid() : currentUser.getTenantUID();

        updateSearchFilter(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, tenantUid));


        // 默认按 createTime 倒序排序
        if (pageRequest.getSort() == null || !pageRequest.getSort().iterator().hasNext()) {
            pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(),
                org.springframework.data.domain.Sort.Direction.DESC, "createTime");
        }

        return getDAO().findBySearchFilter(this.searchFilters, pageRequest);
    }

    /**
     * 切换选中的租户时触发，更新selectTenant并刷新列表
     */
    public void changeSelectTenant() {
        if (StringUtils.isNotEmpty(selectTenantUid)) {
            selectTenant = tenantInfoRepository.findByuid(selectTenantUid);
        }
    }

    public void uploadModelFile(FileUploadEvent event) {
        this.file = event.getFile();
        uploadedFileObject = blobFileService.uploadFile(file);
        if (uploadedFileObject != null) {
            logger.info("Inference model uploaded, storageId={}", uploadedFileObject.getObjectStorageId());
        } else {
            WebUtil.addErrorMessage("文件上传失败，请重试");
        }
    }

    @Override
    public void prepareCreate() throws InstantiationException, IllegalAccessException {
        this.editingModel = new InferenceModel();
//        this.editingModel.setEnabled(true);
        this.file = null;
        this.uploadedFileObject = null;
    }

    @Transactional
    public void saveNewModel() {
        try {
            if (uploadedFileObject == null) {
                WebUtil.addErrorMessage("请上传模型文件！");
                return;
            }
            if (StringUtils.isEmpty(editingModel.getType())) {
                WebUtil.addErrorMessage("请填写模型类型！");
                return;
            }
            if (StringUtils.isEmpty(editingModel.getVersion())) {
                WebUtil.addErrorMessage("请填写模型版本！");
                return;
            }
            editingModel.setTenantUid(selectTenant != null ? selectTenant.getUid() : currentUser.getTenantUID());
            editingModel.setUploadUserUid(currentUser.getUid());
            editingModel.setUploadUserName(currentUser.getName());
            editingModel.setCreateTime(new Date());
            editingModel.setUpdateTime(new Date());
            editingModel.setFileName(uploadedFileObject.getObjectName());
            editingModel.setStorageId(uploadedFileObject.getObjectStorageId());

            if (Boolean.TRUE.equals(editingModel.getEnabled())
                    && !checkUniqueEnabled(editingModel.getTenantUid(), editingModel.getType(), null)) {
                return;
            }
            if (!checkUniqueVersion(editingModel.getTenantUid(), editingModel.getType(), editingModel.getVersion(), null)) {
                return;
            }

            inferenceModelRepository.save(editingModel);
            uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_INFERENCE_MODEL);
            uploadedFileObject.setBoUuid(editingModel.getUid());
            blobFileService.saveBlobObject(uploadedFileObject);

            WebUtil.addSuccessMessage("推理模型上传成功");
        } catch (Exception e) {
            logger.error("保存推理模型失败:", e);
            WebUtil.addErrorMessage("保存失败：" + e.getMessage());
        }
        file = null;
        uploadedFileObject = null;
    }

    @Override
    public void prepareEdit() {
        if (selected != null) {
            this.editingModel = inferenceModelRepository.findByUid(selected.getUid());
        }
        this.file = null;
        this.uploadedFileObject = null;
    }

    @Transactional
    public void saveEditModel() {
        try {
            if (editingModel == null) {
                WebUtil.addErrorMessage("未选择要编辑的模型");
                return;
            }
            if (StringUtils.isEmpty(editingModel.getVersion())) {
                WebUtil.addErrorMessage("请填写模型版本！");
                return;
            }
            editingModel.setUpdateTime(new Date());

            if (Boolean.TRUE.equals(editingModel.getEnabled())
                    && !checkUniqueEnabled(editingModel.getTenantUid(), editingModel.getType(), editingModel.getUid())) {
                return;
            }
            if (!checkUniqueVersion(editingModel.getTenantUid(), editingModel.getType(), editingModel.getVersion(), editingModel.getUid())) {
                return;
            }

            if (uploadedFileObject != null) {
                editingModel.setFileName(uploadedFileObject.getObjectName());
                editingModel.setStorageId(uploadedFileObject.getObjectStorageId());
                uploadedFileObject.setBoType(V2_BlobObject.BO_TYPE_INFERENCE_MODEL);

                uploadedFileObject.setBoUuid(editingModel.getUid());
                blobFileService.saveBlobObject(uploadedFileObject);
            }

            inferenceModelRepository.save(editingModel);
            WebUtil.addSuccessMessage("推理模型更新成功");
        } catch (Exception e) {
            logger.error("编辑推理模型失败:", e);
            WebUtil.addErrorMessage("编辑失败：" + e.getMessage());
        }
        file = null;
        uploadedFileObject = null;
    }

    @Transactional
    public void deleteModel() {
        if (selected == null) {
            WebUtil.addErrorMessage("请先选择要删除的模型");
            return;
        }
        try {
            inferenceModelService.deleteByUid(selected.getUid());
            WebUtil.addSuccessMessage("推理模型删除成功");
        } catch (Exception e) {
            logger.error("删除推理模型失败:", e);
            WebUtil.addErrorMessage("删除失败：" + e.getMessage());
        }
    }

    public void downloadModel(InferenceModel model) {
        if (model == null || StringUtils.isEmpty(model.getStorageId())) {
            WebUtil.addErrorMessage("该模型没有关联文件，无法下载");
            return;
        }
        try {
            org.primefaces.model.StreamedContent sc = blobFileService.getBlobObjectByStorageId(model.getStorageId());
            if (sc != null && sc.getStream() != null) {
                javax.faces.context.FacesContext fc = javax.faces.context.FacesContext.getCurrentInstance();
                javax.servlet.http.HttpServletResponse resp =
                        (javax.servlet.http.HttpServletResponse) fc.getExternalContext().getResponse();
                resp.setHeader("Content-Disposition", "attachment;filename=" + model.getFileName());
                org.springframework.util.StreamUtils.copy(sc.getStream(), resp.getOutputStream());
                fc.responseComplete();
                fc.renderResponse();
            } else {
                WebUtil.addErrorMessage("文件不存在或已被删除");
            }
        } catch (Exception e) {
            logger.error("下载推理模型文件失败:", e);
            WebUtil.addErrorMessage("下载失败：" + e.getMessage());
        }
    }

    @Transactional
    public void toggleEnabled(InferenceModel model) {
        if (model != null) {
            boolean willEnable = !model.getEnabled();
            if (willEnable && !checkUniqueEnabled(model.getTenantUid(), model.getType(), model.getUid())) {
                return;
            }
            model.setEnabled(willEnable);
            model.setUpdateTime(new Date());
            inferenceModelRepository.save(model);
            WebUtil.addSuccessMessage(willEnable ? "模型已启用" : "模型已禁用");
        }
    }

    // ==================== 校验：同租户同类型模型只能有一个启用 ====================

    /**
     * 校验同一租户下同一类型是否已有启用的模型（排除指定UID）。
     * 校验通过返回true；不通过时提示错误并返回false。
     */
    private boolean checkUniqueEnabled(String tenantUid, String type, String excludeUid) {
        List<InferenceModel> existing = inferenceModelRepository
                .findEnabledByTenantUidAndTypeExcludeUid(tenantUid, type, excludeUid != null ? excludeUid : "");
        if (!existing.isEmpty()) {
            String typeName = getModelTypeName(type);
            WebUtil.addErrorMessage("当前租户下【" + typeName + "】已有启用的模型，同类型只能有一个启用");
            return false;
        }
        return true;
    }

    /**
     * 校验同一租户下同一类型同一版本号是否已存在（排除指定UID）。
     * 校验通过返回true；不通过时提示错误并返回false。
     */
    private boolean checkUniqueVersion(String tenantUid, String type, String version, String excludeUid) {
        if (StringUtils.isEmpty(version)) {
            return true;
        }
        List<InferenceModel> existing = inferenceModelRepository
                .findByTenantUidAndTypeAndVersionExcludeUid(tenantUid, type, version, excludeUid != null ? excludeUid : "");
        if (!existing.isEmpty()) {
            String typeName = getModelTypeName(type);
            WebUtil.addErrorMessage("当前租户下【" + typeName + "】已存在版本 " + version + "，版本号不能重复");
            return false;
        }
        return true;
    }

    // ==================== 模型类型下拉选项 ====================

    /**
     * 获取模型类型下拉选项列表
     */
    public List<SelectItem> getModelTypeOptions() {
        List<SelectItem> options = new ArrayList<>();
        for (ModelTypeEnum e : ModelTypeEnum.values()) {
            options.add(new SelectItem(e.getCode(), e.getName()));
        }
        return options;
    }

    /**
     * 获取状态下拉选项列表
     */
    public List<SelectItem> getEnabledOptions() {
        List<SelectItem> options = new ArrayList<>();
        options.add(new SelectItem(Boolean.TRUE, "已启用"));
        options.add(new SelectItem(Boolean.FALSE, "已禁用"));
        return options;
    }

    /**
     * 根据code获取模型类型的中文名称（用于列表展示）
     */
    public String getModelTypeName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        try {
            return ModelTypeEnum.valueOf(code).getName();
        } catch (IllegalArgumentException ex) {
            return code;
        }
    }
}

package com.ge.apm.view.measuring;

import com.ge.apm.domain.*;
import com.ge.apm.service.asset.MeasuringService;
import com.ge.apm.view.component.FilterSiteOrgTreeComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 212605082 on 2017/10/11.
 */
@ManagedBean
@ViewScoped
public class MeasureFromExcelController implements ServerEventInterface {

    private MeasuringService measuringService;
    private UserAccount currentUser;
    private UploadedFile file;
    private List<MeasuringCertificateDetail> noFoundList;
    private UserAuth customRoleAuth;
    @Getter @Setter
    private OrgInfo selectedOrg;
    private FilterSiteOrgTreeComponentController filterSiteOrgTreeComponentController;

    /** 校验后的全部数据列表 */
    private List<MeasuringCertificateDetail> displayList;
    /** 校验失败的数据列表 */
    private List<MeasuringCertificateDetail> displayErrorList;
    /** 是否可以导入（全部校验通过时为true） */
    private Boolean canbeImported = false;
    /** 是否已执行导入 */
    private Boolean isImported = false;
    /** 是否仅显示错误记录 */
    private boolean displayErrorsOnly = false;

    @PostConstruct
    protected void init() {
        currentUser = UserContextService.getCurrentUserAccount();
        measuringService = WebUtil.getBean(MeasuringService.class);
        currentUser = UserContextService.getCurrentUserAccount();
        customRoleAuth=UserContextService.getCustomRoleAuth(currentUser.getId(), CustomRoleAuthConstant.MEASURE_INFO);
        filterSiteOrgTreeComponentController = WebUtil.getBean(FilterSiteOrgTreeComponentController.class);
        filterSiteOrgTreeComponentController.registerController(this);
    }

    /**
     * 文件上传后仅做校验，不保存数据库
     */
    public void handleFileUpload(FileUploadEvent event) throws IOException, ParseException {
        if (ObjectUtil.isEmpty(selectedOrg)) {
            WebUtil.addErrorMessage("所属院区必填！");
            return;
        }
        this.file = event.getFile();
        this.isImported = false;
        this.canbeImported = false;
        this.displayErrorsOnly = false;

        try {
            displayList = measuringService.validateExcel(file, selectedOrg);
        } catch (Exception e) {
            WebUtil.addErrorMessage("文件解析失败：" + e.getMessage());
            displayList = new ArrayList<>();
            return;
        }

        if (CollectionUtils.isEmpty(displayList)) {
            WebUtil.addErrorMessage("Excel中没有有效数据行");
            return;
        }

        filterErrorRecords();

        // 判断是否全部校验通过
        boolean hasError = false;
        for (MeasuringCertificateDetail detail : displayList) {
            if (MeasuringService.IMPORT_STATUS_CHECK_FAIL.equals(detail.getImportStatus())) {
                hasError = true;
                break;
            }
        }
        canbeImported = !hasError;

        if (canbeImported) {
            WebUtil.addSuccessMessage("校验完成", "共 " + displayList.size() + " 条数据全部校验通过，请点击【确认导入】按钮完成导入。");
        } else {
            int failCount = displayErrorList != null ? displayErrorList.size() : 0;
            WebUtil.addWarnMessage("校验完成", "共 " + displayList.size() + " 条数据，其中 " + failCount + " 条校验失败，请修正后重新上传。");
        }
    }

    /**
     * 确认导入 - 实际保存数据库
     */
    public void importData() {
        if (!canbeImported || CollectionUtils.isEmpty(displayList)) {
            WebUtil.addErrorMessage("数据校验未通过，无法导入");
            return;
        }
        try {
            List<MeasuringCertificateDetail> failedList = measuringService.importValidatedData(displayList);
            isImported = true;
            canbeImported = false;

            filterErrorRecords();

            if (CollectionUtils.isEmpty(failedList)) {
                WebUtil.addSuccessMessage("导入成功", "共成功导入 " + displayList.size() + " 条计量数据。");
            } else {
                int successCount = displayList.size() - failedList.size();
                WebUtil.addWarnMessage("导入完成", "成功 " + successCount + " 条，失败 " + failedList.size() + " 条。");
            }
        } catch (Exception e) {
            isImported = true;
            canbeImported = false;
            WebUtil.addErrorMessage("导入失败：" + e.getMessage());
        }
    }

    /**
     * 切换显示全部/仅错误
     */
    public void switchDisplayMode() {
        filterErrorRecords();
    }

    private void filterErrorRecords() {
        if (CollectionUtils.isEmpty(displayList)) {
            displayErrorList = new ArrayList<>();
            return;
        }
        List<MeasuringCertificateDetail> errors = new ArrayList<>();
        for (MeasuringCertificateDetail detail : displayList) {
            String st = detail.getImportStatus();
            if (MeasuringService.IMPORT_STATUS_CHECK_FAIL.equals(st) || MeasuringService.IMPORT_STATUS_FAILURE.equals(st)) {
                errors.add(detail);
            }
        }
        displayErrorList = errors;
    }

    public List<MeasuringCertificateDetail> getDisplayList() {
        if (displayErrorsOnly) {
            return displayErrorList;
        }
        return displayList;
    }

    public void setDisplayList(List<MeasuringCertificateDetail> displayList) {
        this.displayList = displayList;
    }

    public Boolean getCanbeImported() {
        return canbeImported;
    }

    public void setCanbeImported(Boolean canbeImported) {
        this.canbeImported = canbeImported;
    }

    public Boolean getIsImported() {
        return isImported;
    }

    public void setIsImported(Boolean isImported) {
        this.isImported = isImported;
    }

    public boolean isDisplayErrorsOnly() {
        return displayErrorsOnly;
    }

    public void setDisplayErrorsOnly(boolean displayErrorsOnly) {
        this.displayErrorsOnly = displayErrorsOnly;
    }

    public boolean isHasData() {
        return displayList != null && !displayList.isEmpty();
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<MeasuringCertificateDetail> getNoFoundList() {
        return noFoundList;
    }

    public void setNoFoundList(List<MeasuringCertificateDetail> noFoundList) {
        this.noFoundList = noFoundList;
    }

    public UserAuth getCustomRoleAuth() {
        return customRoleAuth;
    }

    public void setCustomRoleAuth(UserAuth customRoleAuth) {
        this.customRoleAuth = customRoleAuth;
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        selectedOrg = (OrgInfo) eventObject;
    }
}

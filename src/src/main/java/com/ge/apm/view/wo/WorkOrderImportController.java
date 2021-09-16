package com.ge.apm.view.wo;

import com.ge.apm.domain.ImportWorkOrderForm;
import com.ge.apm.service.wo.WorkOrderImportService;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import webapp.framework.web.WebUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.util.List;

@ManagedBean
@ViewScoped
public class WorkOrderImportController  {
    @Getter @Setter
    private UploadedFile file;
    @Getter @Setter
    private StreamedContent templateFile;
    @Getter @Setter
    private List<ImportWorkOrderForm> failImportList;
    private WorkOrderImportService workOrderImportService;

    public void uploadDataFile(FileUploadEvent event) {
        file = event.getFile();
        try {
            failImportList = workOrderImportService.importWorkOrder(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            WebUtil.addErrorMessage("导入数据异常，请重新导入！");
            return;
        }
    }

    @PostConstruct
    public void init() {
        workOrderImportService = WebUtil.getBean(WorkOrderImportService.class);
    }



    public void exportExcelTemplate() {
        try {
            templateFile = workOrderImportService.getWorkOrderTemplate();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            templateFile = null;
        }
    }
}

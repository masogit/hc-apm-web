package com.ge.apm.view.pm;

import com.ge.apm.domain.ImportPmOrderForm;
import com.ge.apm.service.wo.PmOrderImportService;
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
public class PmOrderImportController {
    @Getter
    @Setter
    private UploadedFile file;
    @Getter @Setter
    private StreamedContent templateFile;

    @Getter @Setter
    private List<ImportPmOrderForm> failImportList;

    private PmOrderImportService pmOrderImportService;

    public void uploadDataFile(FileUploadEvent event) {
        file = event.getFile();
        try {
            failImportList = pmOrderImportService.importWorkOrder(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            WebUtil.addErrorMessage("导入数据异常，请重新导入！");
            return;
        }
    }

    @PostConstruct
    public void init() {
        pmOrderImportService = WebUtil.getBean(PmOrderImportService.class);
    }

    public void exportExcelTemplate() {
        try {
            templateFile = pmOrderImportService.getWorkOrderTemplate();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            templateFile = null;
        }
    }
}

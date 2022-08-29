/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.domain.DownloadTemplateFileType;
import com.ge.apm.service.utils.MicroServiceUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.springframework.util.StreamUtils;
import webapp.framework.web.WebUtil;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.StreamedContent;
import java.io.InputStream;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class FileDownloadService {

    private MicroServiceUtil msService;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
    }

    public StreamedContent getFile(String fileId) {
        return msService.downloadSingleFile(fileId);
    }

    public void downloadFile(String fileId) {
        StreamedContent file = getFile(fileId);
        exportFile(file.getStream(), file.getName());
    }

    public String getFileUrl(String fileId) {
        return msService.getUrl_downloadFile().concat("/").concat(fileId);
    }

    public String getImgUrl(String fileId) {
        return msService.getImgUrl().concat("/").concat(fileId);
    }

    public String getImgBase64(String fileId) {
        return msService.getImgBase64(fileId);
    }

    public void exportTemplateFile(String type) {
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        String fileName = DownloadTemplateFileType.getTemplateFile(type, ucs.isLocale());
        InputStream inputStream = FileDownloadService.class.getClassLoader().getResourceAsStream("template/" + fileName);
        exportFile(inputStream, fileName);
    }

    private void exportFile(InputStream inputStream, String fileName) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            StreamUtils.copy(inputStream, response.getOutputStream());
            facesContext.responseComplete();
            facesContext.renderResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.DownloadTemplateFileType;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.MicroServiceUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.context.RequestContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.web.WebUtil;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
public class FileDownloadService {

    private MicroServiceUtil msService;
    private CustomRoleRepository customRoleRepository;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
        customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
    }

    public StreamedContent getFile(String fileId) {
        try {
            return msService.downloadSingleFile(fileId);
        } catch (Exception e) {
            return null;
        }
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

    public void getImgBase64ForReq() {
        String objectId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectId");
        if(StringUtils.isNotBlank(objectId)){
            RequestContext.getCurrentInstance().addCallbackParam("objectId", objectId);
            RequestContext.getCurrentInstance().addCallbackParam("objectBase64", msService.getImgBase64(objectId));
        }
    }

    public void exportTemplateFile(String type) {
        UserContextService ucs = WebUtil.getBean(UserContextService.class);
        String fileName = DownloadTemplateFileType.getTemplateFile(type, ucs.isLocale());
        InputStream inputStream = FileDownloadService.class.getClassLoader().getResourceAsStream("template/" + fileName);
        inputStream =processorTemplateFile(inputStream);
        exportFile(inputStream, fileName);
    }

    private InputStream processorTemplateFile(InputStream inputStream)  {
        return getCustomRole(inputStream);
    }

    private InputStream getCustomRole(InputStream inputStream) {
        UserAccount currentUserAccount = UserContextService.getCurrentUserAccount();
        String tenantUID = currentUserAccount.getTenantUID();
        List<CustomRole> customRoles = customRoleRepository.findByTenantUID(tenantUID);
        if(CollectionUtils.isEmpty(customRoles)){
            return inputStream;
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)){
            String lastRow="资产分类非必填(租户开启资产分类值为资产分类的编码例如    ：01-01,01-02多项用,号分割 )";
            XSSFSheet explanation = workbook.getSheetAt(1);
            int lastRowNum = explanation.getLastRowNum();
            XSSFCellStyle cellStyle = explanation.getRow(lastRowNum-1).getCell(0).getCellStyle();
            for (int i = 0; i < customRoles.size(); i++) {
                XSSFRow row = explanation.createRow(lastRowNum + i);
                XSSFCell cell = row.createCell(0);
                 cell.setCellValue(customRoles.get(i).getName());
                cell.setCellStyle(cellStyle);
            }
            XSSFRow row = explanation.createRow(lastRowNum + customRoles.size() - 1);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(lastRow);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
           return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return inputStream;
        }
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

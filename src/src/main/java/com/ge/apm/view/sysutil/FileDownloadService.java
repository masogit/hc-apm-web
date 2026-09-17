/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.view.sysutil;

import com.ge.apm.dao.CustomRoleRepository;
import com.ge.apm.dao.SystemTemplateValueRepository;
import com.ge.apm.domain.CustomRole;
import com.ge.apm.domain.DownloadTemplateFileType;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.dto.FileInfoResult;
import com.ge.apm.service.utils.MicroServiceUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.ge.apm.service.utils.TempFileUtil;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.primefaces.context.RequestContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import webapp.framework.web.WebUtil;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * @author 212579464
 */
@ManagedBean
@ViewScoped
@Slf4j
public class FileDownloadService {

    private MicroServiceUtil msService;
    private CustomRoleRepository customRoleRepository;


    private SystemTemplateValueRepository systemTemplateValueRepository;
    private UserContextService userContextService;

    @PostConstruct
    protected void init() {
        msService = WebUtil.getBean(MicroServiceUtil.class);
        customRoleRepository = WebUtil.getBean(CustomRoleRepository.class);
        systemTemplateValueRepository = WebUtil.getBean(SystemTemplateValueRepository.class);

         userContextService = WebUtil.getBean(UserContextService.class);

    }

    public StreamedContent getFile(String fileId) {
        try {
            return msService.downloadSingleFile(fileId);
        } catch (Exception e) {
            return null;
        }
    }
    public StreamedContent getFileForEhome(String fileId) {
        try {
            return msService.downloadSingleFileForEhome(fileId);
        } catch (Exception e) {
            return null;
        }
    }

    public List<FileInfoResult> getFileInfoForEhome(Collection<String> fileIds) {
        try {
            return msService.getFiles(fileIds);
        } catch (Exception e) {
            return null;
        }
    }

    public List<FileInfoResult> getNotByteFileInfoForEhome(Collection<String> fileIds) {
        try {
            return msService.getNotByteFiles(fileIds);
        } catch (Exception e) {
            return null;
        }
    }

    public void downloadFile(String fileId) {
        StreamedContent file = getFile(fileId);
        exportFile(file.getStream(), file.getName());
    }
    public void downloadFileForEhome(String fileId) {
        StreamedContent file = getFileForEhome(fileId);
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

    public String getImgBase64ByStream(TempFileUtil tempFileUtil) {
        return msService.getImgBase64ByStream(tempFileUtil);
    }

    public void getImgBase64ForReq() {
        String objectId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("objectId");
        if(StringUtils.isNotBlank(objectId)){
            RequestContext.getCurrentInstance().addCallbackParam("objectId", objectId);
            RequestContext.getCurrentInstance().addCallbackParam("objectBase64", msService.getImgBase64(objectId));
        }
    }

    public void exportTemplateFile(String type) {
        try {
            UserContextService ucs = WebUtil.getBean(UserContextService.class);
            SystemTemplateEnum systemTemplate = DownloadTemplateFileType.getSystemTemplate(type, ucs.isLocale());
            InputStream inputStream = getTemplateFileInputStream(systemTemplate.getCode(), systemTemplate.getFileType());
            inputStream = processorTemplateFile(inputStream, type);
            exportFile(inputStream, systemTemplate.getFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 供 p:fileDownload 使用，通过 AJAX 方式下载模板，避免 response.reset() 在 appendTo dialog 中失败的问题
     */
    public StreamedContent getTemplateFileStream() {
        try {
            UserContextService ucs = WebUtil.getBean(UserContextService.class);
            SystemTemplateEnum systemTemplate = DownloadTemplateFileType.getSystemTemplate("DEPT_TRANSACTION_LOG", ucs.isLocale());
            InputStream inputStream = getTemplateFileInputStream(systemTemplate.getCode(), systemTemplate.getFileType());
            inputStream = processorTemplateFile(inputStream, "DEPT_TRANSACTION_LOG");
            return new DefaultStreamedContent(inputStream,
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    systemTemplate.getFileName());
        } catch (Exception e) {
            log.error("Failed to prepare template file stream", e);
            return null;
        }
    }

    private InputStream processorTemplateFile(InputStream inputStream,String type)  {
        if(DownloadTemplateFileType.USER_ACCOUNT.name().equals(type)){
            return getCustomRole(inputStream);
        }
        if(DownloadTemplateFileType.DEPT_TRANSACTION_LOG.name().equals(type)){
            return getDeptTransationLog(inputStream);
        }
        return inputStream;
    }

    private InputStream getDeptTransationLog(InputStream inputStream) {
        // 获取类别下拉选项
        int instId = -1;
        try {
            UserAccount user = UserContextService.getCurrentUserAccount();
            if (user != null && user.getSiteId() != null) {
                instId = user.getSiteId();
            }
        } catch (Exception ex) {
            log.error("Failed to get siteId for template export", ex);
        }
        List<I18nMessage> categoryList = FieldValueMessageController.getFieldValueList("transaction",instId);
        if(CollectionUtils.isEmpty(categoryList)){
            return inputStream;
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)){
            XSSFSheet sheet = workbook.getSheetAt(0);

            String[] categoryValues = new String[categoryList.size()];
            for (int i = 0; i < categoryList.size(); i++) {
                categoryValues[i] = categoryList.get(i).getValue();
            }

            // 为类别列添加下拉选择
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(categoryValues);
            CellRangeAddressList addressList = new CellRangeAddressList(1, 5000, 2, 2);
            DataValidation validation = validationHelper.createValidation(constraint, addressList);
            validation.setShowErrorBox(true);
            sheet.addValidationData(validation);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return inputStream;
        }
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
            XSSFRow row = explanation.createRow(lastRowNum + customRoles.size());
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

    public void exportFile(InputStream inputStream, String fileName) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            StreamUtils.copy(inputStream, response.getOutputStream());
            response.getOutputStream().flush();
            facesContext.responseComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public InputStream getTemplateFileInputStream(String templateType,String fileType){
        UserAccount user = UserContextService.getCurrentUserAccount();
        String storageId= systemTemplateValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),templateType,fileType);
        byte[] templateBytes = msService.downloadSingleFileByte(storageId);
        return new ByteArrayInputStream(templateBytes);
    }

    public byte[] getTemplateFileByte(String templateType,String fileType){
        UserAccount user = UserContextService.getCurrentUserAccount();
        String storageId= systemTemplateValueRepository.findByKeyAndSiteForUseParent(user.getSiteUID(),user.getHospitalUID(),user.getInstitutionUID(),user.getTenantUID(),templateType,fileType);
        return   msService.downloadSingleFileByte(storageId);
    }

}

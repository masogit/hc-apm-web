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
import webapp.framework.context.LocaleHolder;
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
        List<I18nMessage> categoryList = FieldValueMessageController.getFieldValueList("transaction", instId);
        if (CollectionUtils.isEmpty(categoryList)) {
            return inputStream;
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
    
            // 根据locale获取对应的i18n值，参考AssetFileDynamicTemplate
            List<String> categoryValues;
            if (LocaleHolder.getLocale().toString().equalsIgnoreCase("zh")) {
                categoryValues = new java.util.ArrayList<>(categoryList.size());
                for (I18nMessage msg : categoryList) {
                    categoryValues.add(msg.getValueZh());
                }
            } else {
                categoryValues = new java.util.ArrayList<>(categoryList.size());
                for (I18nMessage msg : categoryList) {
                    categoryValues.add(msg.getValueEn());
                }
            }
    
            // 创建隐藏sheet写入类别值，通过公式引用方式生成下拉列表
            String hiddenSheetName = "_category_values";
            XSSFSheet hiddenSheet = workbook.createSheet(hiddenSheetName);
            for (int i = 0; i < categoryValues.size(); i++) {
                XSSFRow row = hiddenSheet.createRow(i);
                row.createCell(0).setCellValue(categoryValues.get(i));
            }
            int hiddenSheetIndex = workbook.getSheetIndex(hiddenSheetName);
            workbook.setSheetHidden(hiddenSheetIndex, true);
    
            // 移除模板中已存在的覆盖C列（索引2）的验证规则，避免旧下拉列表覆盖新的i18n值
            org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWorksheet ctWorksheet = sheet.getCTWorksheet();
            if (ctWorksheet.isSetDataValidations()) {
                org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidations ctDataValidations = ctWorksheet.getDataValidations();
                java.util.Iterator<org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation> it = ctDataValidations.getDataValidationList().iterator();
                while (it.hasNext()) {
                    org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataValidation ctDv = it.next();
                    String sqref = ctDv.getSqref() != null ? ctDv.getSqref().toString() : "";
                    // sqref格式如 "C2:C200" 或 "$C$2:$C$200"，检查是否包含C列引用
                    if (sqref.matches(".*(?<![A-Z])[A-C](?![A-Z]).*")) {
                        it.remove();
                    }
                }
                if (ctDataValidations.sizeOfDataValidationArray() == 0) {
                    ctWorksheet.unsetDataValidations();
                }
            }

            // 通过createFormulaListConstraint公式引用隐藏sheet区域生成下拉列表
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            String formulaRef = hiddenSheetName + "!$A$1:$A$" + categoryValues.size();
            DataValidationConstraint dvConstraint = validationHelper.createFormulaListConstraint(formulaRef);
            // 第三列（C列，索引2）从第2行开始设置下拉
            CellRangeAddressList addressList = new CellRangeAddressList(1, 200, 2, 2);
            DataValidation dataValidation = validationHelper.createValidation(dvConstraint, addressList);
            dataValidation.setShowErrorBox(true);
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowPromptBox(true);
            sheet.addValidationData(dataValidation);
    
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            log.error("Failed to process dept transaction log template", e);
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

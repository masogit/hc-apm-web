package com.ge.apm.view.surgeryAnesthesia;

import com.ge.apm.dao.ExcelImportRecordRepository;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.SurgeryAnesthesiaDataSourceRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.his.ExcelImportRecord;
import com.ge.apm.domain.surgeryAnesthesia.SurgeryAnesthesiaDataSource;
import com.ge.apm.service.surgeryAnesthesia.SurgeryAnesthesiaDataSourceImportService;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.his.HisOrgComponentController;
import com.ge.apm.view.sysutil.UserContextService;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StreamUtils;
import webapp.framework.dao.SearchFilter;
import webapp.framework.util.DateUtil;
import webapp.framework.web.WebUtil;
import webapp.framework.web.mvc.GenericCRUDUUIDController;
import webapp.framework.web.mvc.ServerEventInterface;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 手术室绩效管理 - 绩效明细源数据Controller
 * 用于手动上传接口数据并查看源数据
 */
@ManagedBean
@ViewScoped
public class SurgeryAnesthesiaDataSourceController extends GenericCRUDUUIDController<SurgeryAnesthesiaDataSource> implements ServerEventInterface {

    private OrgInfoRepository orgInfoRepository;
    private SurgeryAnesthesiaDataSourceRepository sourceDao;
    private ExcelImportRecordRepository excelImportRecordRepository;
    private HisOrgComponentController orgComponentController;

    private UserAccount loginUser;
    private OrgInfo selectedOrg;

    @Getter
    @Setter
    private Date startDate;

    @Getter
    @Setter
    private Date endDate;

    private List<ExcelImportRecord> excelImportRecords;

    @Override
    protected SurgeryAnesthesiaDataSourceRepository getDAO() {
        return sourceDao;
    }

    @Override
    protected void init() {
        sourceDao = WebUtil.getBean(SurgeryAnesthesiaDataSourceRepository.class);
        orgInfoRepository = WebUtil.getBean(OrgInfoRepository.class);
        excelImportRecordRepository = WebUtil.getBean(ExcelImportRecordRepository.class);
        orgComponentController = WebUtil.getBean(HisOrgComponentController.class);

        loginUser = UserContextService.getCurrentUserAccount();
        selectedOrg = orgInfoRepository.getByUid(loginUser.getOrgInfoUID());

        orgComponentController.registerController(this);
        initExcelImportRecords();

        // 默认查询最近3个月的数据
        endDate = new Date();
        startDate = DateUtil.addMonth(new Date(), -3);
    }

    public void initExcelImportRecords() {
        UserAccount user = UserContextService.getCurrentUserAccount();
        excelImportRecords = excelImportRecordRepository.findByModuleAndTenantUid("SurgeryAnesthesiaDataSource", user.getTenantUID());
    }

    @Override
    protected Page<SurgeryAnesthesiaDataSource> loadData(PageRequest pageRequest) {
        if (this.searchFilters == null) {
            searchFilters = new ArrayList<>();
        }
        removeFilterOnField("orgCode");
        removeFilterOnField("siteUid");
        removeFilterOnField("tenantUid");
        removeFilterOnField("surgeryDate");

        OrgInfo selectedOrg = orgComponentController.getSelectedOrg();
        searchFilters.add(new SearchFilter("tenantUid", SearchFilter.Operator.EQ, loginUser.getTenantUID()));
        if (selectedOrg != null) {
            searchFilters.add(new SearchFilter("orgCode", SearchFilter.Operator.EQ, selectedOrg.getOrgCode()));
            searchFilters.add(new SearchFilter("siteUid", SearchFilter.Operator.EQ, selectedOrg.getSiteUID()));
        }
        if (startDate != null) {
            searchFilters.add(new SearchFilter("surgeryDate", SearchFilter.Operator.GTE, startDate));
        }
        if (endDate != null) {
            searchFilters.add(new SearchFilter("surgeryDate", SearchFilter.Operator.LT, DateUtil.getDateAddOneDay(endDate)));
        }

        return sourceDao.findBySearchFilter(searchFilters, pageRequest);
    }

    @Override
    public List<SurgeryAnesthesiaDataSource> getItemList() {
        return sourceDao.find();
    }

    public List<ExcelImportRecord> getExcelImportRecords() {
        return excelImportRecords;
    }

    public void setExcelImportRecords(List<ExcelImportRecord> excelImportRecords) {
        this.excelImportRecords = excelImportRecords;
    }

    /**
     * 获取租户最新的手术日期
     */
    public String tenantMaxSurgeryDate() {
        String tenantUID = loginUser.getTenantUID();
        Date surgeryDate = sourceDao.getTenantMaxSurgeryDate(tenantUID);
        if (surgeryDate != null) {
            String format = "yyyy-MM-dd";
            String dateStr = DateUtil.getFormatStrByDate(surgeryDate, format);
            return dateStr;
        }
        return null;
    }

    public Date getMaxDate() {
        if (startDate != null) {
            return DateUtil.addMonth(startDate, 12);
        }
        return null;
    }

    public void getEndDateChange() {
        if (startDate != null) {
            if (endDate != null && !endDate.before(DateUtil.addMonth(startDate, 12))) {
                endDate = DateUtil.addMonth(startDate, 12);
            }
        }
    }

    // ========== 文件上传相关 ==========

    @Getter
    @Setter
    private UploadedFile file;

    @Getter
    @Setter
    private List<Map<String, Object>> errorList;

    private SurgeryAnesthesiaDataSourceImportService surgeryAnesthesiaDataSourceImportService;

    /**
     * 下载手术麻醉源数据导入模板
     */
    public void downloadTemplate() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(SurgeryAnesthesiaDataSourceImportService.SHEET_NAME);

            // 表头行: 写在row index 0, 对应 EXCEL_HEADER_ROW=1 (1-based, 内部减1得index 0)
            // 数据从row index 1开始, 对应 EXCEL_DATA_ROW=2 (1-based, 内部减1得index 1)
            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"机构代码", "设备代码", "手术日期", "计划开始时间", "手术开始时间",
                    "手术结束时间", "离开手术室时间", "手术室名称", "手术室代码", "住院号", "系统来源"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
                sheet.setColumnWidth(i, 20 * 256);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setHeader("Content-Disposition", "attachment;filename=SurgeryDataSource_Import_Template.xlsx");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            StreamUtils.copy(inputStream, response.getOutputStream());
            facesContext.responseComplete();
        } catch (Exception e) {
            Logger.getLogger(SurgeryAnesthesiaDataSourceController.class.getName()).log(Level.SEVERE, null, e);
            WebUtil.addErrorMessage("下载模板失败--" + e.getMessage());
        }
    }

    /**
     * 上传手术麻醉源数据Excel文件
     */
    public void uploadDataFile(FileUploadEvent event) {
        ExcelDocument doc = null;
        this.file = event.getFile();
        try {
            if (surgeryAnesthesiaDataSourceImportService == null) {
                surgeryAnesthesiaDataSourceImportService = WebUtil.getBean(SurgeryAnesthesiaDataSourceImportService.class);
            }

            doc = new ExcelDocument(file.getInputstream(), ExcelDocument.parseTypeByFileName(file.getFileName()));

            int maxRowNum = SurgeryAnesthesiaDataSourceImportService.EXCEL_HEADER_ROW + 30000;
            if (doc.exceedMaxRowNum(SurgeryAnesthesiaDataSourceImportService.SHEET_NAME, maxRowNum)) {
                WebUtil.addErrorMessage("导入Excel中数据行数最多30000条，请分批导入");
                return;
            }

            List<Map<String, Object>> importMapList = surgeryAnesthesiaDataSourceImportService.getMapFromTemplate(doc);
            surgeryAnesthesiaDataSourceImportService.importData(importMapList);
            errorList = surgeryAnesthesiaDataSourceImportService.filterErrorRecords(importMapList);
            surgeryAnesthesiaDataSourceImportService.saveToDB(importMapList);

            if (errorList.size() < importMapList.size()) {
                UserAccount user = UserContextService.getCurrentUserAccount();
                ExcelImportRecord excelImportRecord = new ExcelImportRecord();
                excelImportRecord.setImportName(user.getLoginName());
                excelImportRecord.setCreatedDate(new Date());
                excelImportRecord.setFileName(file.getFileName());
                excelImportRecord.setModule("SurgeryAnesthesiaDataSource");
                excelImportRecord.setTenantUid(user.getTenantUID());
                excelImportRecordRepository.save(excelImportRecord);
                initExcelImportRecords();
            }

            if (!errorList.isEmpty()) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "导入完成，存在失败记录",
                        "共有" + errorList.size() + "条失败记录");
                FacesContext.getCurrentInstance().addMessage(null, message);
            } else {
                WebUtil.addSuccessMessage("导入完成");
            }
        } catch (Exception e) {
            WebUtil.addErrorMessage("解析导入文件出错--" + e.getMessage());
            Logger.getLogger(SurgeryAnesthesiaDataSourceController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (doc != null) {
                doc.release();
            }
        }
    }

    @Override
    public void onServerEvent(String eventName, Object eventObject) {
        logger.info(this);
    }
}
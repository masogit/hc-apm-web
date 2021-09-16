package com.ge.apm.service.zkb;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.ZkbCourseRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ZkbCourseImExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbCourseImExportService.class);

    private static final String SHEET_ZKB_COURSE = "智康宝课程";//WebUtil.getMessage("zkbCourse");

    private final String HEADER_CID = "课程ID";
    private final String HEADER_CNAME = "课程名称";
    private final String HEADER_MODALITY = "modality";
    private final String HEADER_DESCRIPTION = "课程描述";

    @Autowired
    private ZkbCourseRepository zkbCourseDao;
    
    @Autowired
    private TenantInfoRepository tenantInfoDao;

    @Autowired
    private ZkbCourseService zkbCourseService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public List<Map<String, Object>> createZkbCourseData(List<ZkbCourse> zkbCourseList) {

        List<Map<String, Object>> zkbCourseDataList = new ArrayList<>();
        for (ZkbCourse zkbCourse : zkbCourseList) {

            Map<String, Object> zkbCourseDataMap = new HashMap<>();
            TenantInfo tempTenantInfo = tenantInfoDao.findByuid(zkbCourse.getTenantUID());
            //zkbCourseDataMap.put("租户", tempTenantInfo != null ? tempTenantInfo.getName() : "");//WebUtil.getMessage("zkbEngineer_tenant")
            zkbCourseDataMap.put("课程ID", zkbCourse.getCid());
            zkbCourseDataMap.put("课程名称", zkbCourse.getCname());
            zkbCourseDataMap.put("课程描述",zkbCourse.getDescription());
            zkbCourseDataMap.put("modality",zkbCourse.getModality());

            zkbCourseDataList.add(zkbCourseDataMap);

        }

        return zkbCourseDataList;
    }

    public void exportData(ExcelDocument doc, List<Map<String, Object>> zkbCourseData) {

        Integer keyRow_zkbCourse = 1;
        Integer dataRow_zkbCourse = 2;

        for (Map<String, Object> item : zkbCourseData) {
            doc.writeRowData(SHEET_ZKB_COURSE, keyRow_zkbCourse, dataRow_zkbCourse++, item);
        }

    }

    public List<ZkbCourse> handleExcel(UploadedFile file, UserAccount user) throws IOException {

        List<ZkbCourse> importFailedDataList = new ArrayList<>();
        Workbook workbook = null;
        if (file.getFileName().toLowerCase().endsWith(".xls")) {
            workbook = new HSSFWorkbook(file.getInputstream());
        } else if (file.getFileName().toLowerCase().endsWith(".xlsx")) {
            workbook = new XSSFWorkbook(file.getInputstream());
        }
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            return null;
        }
        List<String> headers = new ArrayList<>();
        headers.add(HEADER_CID);
        headers.add(HEADER_CNAME);
        headers.add(HEADER_MODALITY);
        headers.add(HEADER_DESCRIPTION);
        Map<String, Integer> header = ExcelParser.findHeader(sheet, headers);
        if (header.isEmpty()) {
            return null;
        }
        String cid;
        String cname;
        String modality;
        String description;

        int cidIdx = header.get(HEADER_CID);
        int cnameIdx = header.get(HEADER_CNAME);
        int modalityIdx = header.get(HEADER_MODALITY);
        int descriptionIdx = header.get(HEADER_DESCRIPTION);
        List<ZkbCourse> listRe = new ArrayList<ZkbCourse>();
        int len = sheet.getLastRowNum();
        for (int j = header.get("HEADER_ROW") + 1; j <= len; j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            boolean pass = true;
            ZkbCourse detail = new ZkbCourse();
            try {
                detail.setExcelIdex(j + 1);
                detail.setTenantUID(user.getTenantUID());
                cid = StringUtils.trim(ExcelParser.getCellStringValue(row, cidIdx));
                cname = StringUtils.trim(ExcelParser.getCellStringValue(row, cnameIdx));
                modality = StringUtils.trim(ExcelParser.getCellStringValue(row, modalityIdx));
                description = StringUtils.trim(ExcelParser.getCellStringValue(row, descriptionIdx));
                if(StringUtils.isEmpty(cid)){
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程ID不能为空；");
                    pass = false;
                }else {
                    try {
                        ZkbCourse course = new ZkbCourse();course.setCid(cid);
                        if(zkbCourseService.isCourseExist(course)){
                            detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程ID已存在；");
                            pass = false;
                        }

                    }catch (Exception e){
                        LOGGER.info(e.getMessage());
                        pass = false;
                    }
                }

                if (StringUtils.isEmpty(cname)) {
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程名称不能为空；");
                    pass = false;
                }

                if (StringUtils.isEmpty(modality)) {
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "modality不能为空；");
                    pass = false;
                }

                detail.setCid(cid);
                detail.setCname(cname);
                detail.setModality(modality);
                detail.setDescription(description);

                if (!pass) {
                    importFailedDataList.add(detail);
                    continue;
                }else {
                    detail.setCreatedBy(user.getLoginName());
                    listRe.add(detail);
                }

            } catch (Exception e) {
                LOGGER.info(e.getMessage());
                importFailedDataList.add(detail);
            }


        }

        if (importFailedDataList.size() == 0) {
            saveZkbCourseList(listRe);
        }
        return importFailedDataList;

    }
    private void saveZkbCourseList(List<ZkbCourse> list) {
        for (int i = 0; i < list.size(); ++i) {
            ZkbCourse tempobj = list.get(i);
            tempobj.setCreatedDate(new Date());
            zkbCourseDao.save(tempobj);
        }
    }
}

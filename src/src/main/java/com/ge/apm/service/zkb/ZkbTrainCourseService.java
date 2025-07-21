package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbTrainCourseRepository;
import com.ge.apm.domain.UploadFileErrorTypeEnum;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.ZkbCourse;
import com.ge.apm.domain.ZkbTrainCourse;
import com.ge.apm.service.utils.ExcelParser;
import com.ge.healthcare.cn.common.utils.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ZkbTrainCourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbTrainCourseService.class);

    @Autowired
    ZkbTrainCourseRepository zkbTrainCourseRepository;

    public boolean checkCourse(ZkbTrainCourse zkbTrainCourse){
        if(StrUtil.isEmpty(zkbTrainCourse.getId())){
            if(CollectionUtils.isNotEmpty(zkbTrainCourseRepository.findByName(zkbTrainCourse.getName()))){
                return false;
            }
        }else{
            if(CollectionUtils.isNotEmpty(zkbTrainCourseRepository.findByNameAndIdNot(zkbTrainCourse.getName(), zkbTrainCourse.getId()))){
                return false;
            }
        }

        return true;
    }

    public void saveCourse(ZkbTrainCourse zkbTrainCourse){
        zkbTrainCourseRepository.save(zkbTrainCourse);
    }

    public List<ZkbTrainCourse> handleExcel(UploadedFile file, UserAccount user) throws IOException {
        List<ZkbTrainCourse> importFailedDataList = new ArrayList<>();
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
        headers.add("课程编号");
        headers.add("课程名称");
        headers.add("课程简称");
        headers.add("课程描述");
        Map<String, Integer> header = ExcelParser.findHeader(sheet, headers);
        if (header.isEmpty()) {
            return null;
        }
        String code;
        String name;
        String shortName;
        String description;

        int cidIdx = header.get("课程编号");
        int cnameIdx = header.get("课程名称");
        int smdx = header.get("课程简称");
        int descriptionIdx = header.get("课程描述");
        List<ZkbTrainCourse> listRe = new ArrayList<ZkbTrainCourse>();
        int len = sheet.getLastRowNum();
        for (int j = header.get("HEADER_ROW") + 1; j <= len; j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            boolean pass = true;
            ZkbTrainCourse detail = new ZkbTrainCourse();
            try {
                detail.setExcelIdex(j + 1);
                code = StringUtils.trim(ExcelParser.getCellStringValue(row, cidIdx));
                name = StringUtils.trim(ExcelParser.getCellStringValue(row, cnameIdx));
                shortName = StringUtils.trim(ExcelParser.getCellStringValue(row, smdx));
                description = StringUtils.trim(ExcelParser.getCellStringValue(row, descriptionIdx));
                if(StringUtils.isEmpty(code)){
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程ID不能为空；");
                    pass = false;
                }else {
                    try {
                        ZkbTrainCourse course = new ZkbTrainCourse();
                        course.setCode(code);
                       /* if(zkbCourseService.isCourseExist(course)){
                            detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程ID已存在；");
                            pass = false;
                        }*/

                    }catch (Exception e){
                        LOGGER.info(e.getMessage());
                        pass = false;
                    }
                }

                if (StringUtils.isEmpty(name)) {
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程名称不能为空；");
                    pass = false;
                }



                detail.setCode(code);
                detail.setName(name);
                detail.setShortName(shortName);
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
            //saveZkbCourseList(listRe);
        }
        return importFailedDataList;
    }
}

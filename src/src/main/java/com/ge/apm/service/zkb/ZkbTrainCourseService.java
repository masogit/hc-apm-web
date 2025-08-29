package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbTrainCourseQuotaRepository;
import com.ge.apm.dao.ZkbTrainCourseRepository;
import com.ge.apm.dao.ZkbTrainPublishRepository;
import com.ge.apm.domain.*;
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
import java.util.*;

@Service
public class ZkbTrainCourseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZkbTrainCourseService.class);

    @Autowired
    ZkbTrainCourseRepository zkbTrainCourseRepository;

    @Autowired
    ZkbTrainCourseQuotaRepository zkbTrainCourseQuotaRepository;

    @Autowired
    ZkbTrainPublishRepository zkbTrainPublishRepository;

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
        headers.add("CatNo");
        headers.add("Modality");
        headers.add("课程名称");
        headers.add("课程简称");
        headers.add("课程描述");
        headers.add("课程类型");
        headers.add("基础课程");

        Map<String, Integer> header = ExcelParser.findHeader(sheet, headers);
        if (header.isEmpty()) {
            return null;
        }
        String catNo;
        String name;
        String shortName;
        String description;
        String courseType;
        String normalCourse;
        String modality;

        int catNoIdx = header.get("CatNo");
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
                catNo = StringUtils.trim(ExcelParser.getCellStringValue(row, catNoIdx));
                name = StringUtils.trim(ExcelParser.getCellStringValue(row, cnameIdx));
                shortName = StringUtils.trim(ExcelParser.getCellStringValue(row, smdx));
                description = StringUtils.trim(ExcelParser.getCellStringValue(row, descriptionIdx));
                courseType = StringUtils.trim(ExcelParser.getCellStringValue(row, header.get("课程类型")));
                normalCourse = StringUtils.trim(ExcelParser.getCellStringValue(row, header.get("基础课程")));
                modality = StringUtils.trim(ExcelParser.getCellStringValue(row, header.get("Modality")));
                if(StrUtil.isEmpty(catNo)){
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "CatNo不能为空；");
                    pass = false;
                }
                if(StrUtil.isEmpty(modality)){
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "Modality不能为空；");
                    pass = false;
                }

                if (StringUtils.isEmpty(name)) {
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程名称不能为空；");
                    pass = false;
                }

                if(StringUtils.isEmpty(courseType)){
                    detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程类型不能为空；");
                    pass = false;
                }else{
                    if("基础课程".equals(courseType)){
                        detail.setCourseType(ZkbTrainCourse.CourseTypeEnum.normal.name());
                    }else if("进阶课程".equals(courseType)){
                        detail.setCourseType(ZkbTrainCourse.CourseTypeEnum.advance.name());
                    }else {
                        detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "课程类型填写有误；");
                        pass = false;
                    }
                }
                if(StringUtils.isNotEmpty(detail.getCourseType()) && detail.getCourseType().equals(ZkbTrainCourse.CourseTypeEnum.advance.name())){
                    if(StringUtils.isEmpty(normalCourse)){
                        detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "基础课程不能为空；");
                        pass = false;
                    }else{
                        List<ZkbTrainCourse> zkbTrainCourses = zkbTrainCourseRepository.findByCatNo(normalCourse);
                        if(CollectionUtils.isEmpty(zkbTrainCourses)){
                            detail.setErrorMsg(UploadFileErrorTypeEnum.OTHER_ERROR, "基础课程填写有误；");
                            pass = false;
                        }else{
                            detail.setNormalCourseId(zkbTrainCourses.get(0).getId());
                        }
                    }
                }
                detail.setCatNo(catNo);
                detail.setName(name);
                detail.setShortName(shortName);
                detail.setDescription(description);
                detail.setModality(modality);

                if (!pass) {
                    importFailedDataList.add(detail);
                }else {
                    detail.setCreatedBy(user.getLoginName());
                    detail.setCreatedDate(new Date());
                    zkbTrainCourseRepository.save(detail);
                    //listRe.add(detail);
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


    public List<HashMap<String, String>> queryNormalCourse(){
        List<HashMap<String, String>> rs = new ArrayList<>();
        List<ZkbTrainCourse> courseList = zkbTrainCourseRepository.findByCourseTypeOrderByCreatedDateDesc(ZkbTrainCourse.CourseTypeEnum.normal.name());
        if(CollectionUtils.isNotEmpty(courseList)){
            for(ZkbTrainCourse zkbTrainCourse : courseList){
                HashMap<String, String> mm = new HashMap<>();
                mm.put("text", zkbTrainCourse.getName());
                mm.put("id", zkbTrainCourse.getId());
                rs.add(mm);
            }
        }
        return rs;
    }

    public boolean checkDelete(ZkbTrainCourse zkbTrainCourse){
        boolean rs = true;
        List<ZkbTrainCourseQuota> zkbTrainCourseQuotas = zkbTrainCourseQuotaRepository.findByCourseId(zkbTrainCourse.getId());
        if(CollectionUtils.isNotEmpty(zkbTrainCourseQuotas)){
            rs = false;
        }

        List<ZkbTrainPublishInfo> zkbTrainPublishInfos = zkbTrainPublishRepository.findByCourseId(zkbTrainCourse.getId());
        if(CollectionUtils.isNotEmpty(zkbTrainPublishInfos)){
            rs = false;
        }
        return rs;
    }


}

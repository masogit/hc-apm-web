package com.ge.apm.service.utils;

import com.ge.apm.domain.ImportFiles;
import com.ge.apm.domain.ImportFilesVO;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.common.UserCommonService;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FileScanExportService {

    @Autowired
    private UserCommonService userService;

    private static final String SHEET_IMPORT_FILES_LIST = "ImportFilesList";
    private static final String I18N_BATCH_FILE_TYPE = "batchFileType";
    private static final String I18N_BATCH_FILE_PROCESS_STATUS = "batchFileProcessStatus";

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public List<Map<String, Object>> createFileScanData(List<ImportFilesVO> fileScanList){

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (ImportFilesVO importFilesVO : fileScanList) {
            Map<String, Object> resultMap = new HashedMap();

            resultMap.put("文件名称", importFilesVO.getFileName());
            resultMap.put("文件类型", FieldValueMessageController.fieldValue(I18N_BATCH_FILE_TYPE, importFilesVO.getFileType()));
            resultMap.put("上传时间", formatDate(importFilesVO.getUploadedTime()));
            resultMap.put("创建人", getUserName(importFilesVO.getCreatorUID()));
            resultMap.put("当前状态", FieldValueMessageController.fieldValue(I18N_BATCH_FILE_PROCESS_STATUS, importFilesVO.getStatus()));

            resultList.add(resultMap);
        }

        return resultList;

    }

    private String formatDate(Date date) {
        return date != null ? sdf.format(date) : "";
    }

    public String getUserName(String userUid) {
        UserAccount user = userService.getUserByUid(userUid);
        return null == user ? "" : user.getName();
    }

    public Boolean exportData(ExcelDocument doc, List<Map<String, Object>> dataList) {

        Integer keyRow_fileScanReport = 1;
        Integer dataRow_fileScanReport = 2;

        for (Map<String, Object> item : dataList) {
            doc.writeRowData(SHEET_IMPORT_FILES_LIST, keyRow_fileScanReport, dataRow_fileScanReport++, item);
        }

        return Boolean.TRUE;
    }

}

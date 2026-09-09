package com.ge.apm.service.zkb;

import com.ge.apm.dao.ZkbHighPartInfoRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.utils.ExcelDocument;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.web.WebUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : 503189362
 * @date : 2023/1/3
 * @description :
 */
@Service
public class ZkbHighPartService {

    private static final Logger logger = LoggerFactory.getLogger(ZkbHighPartService.class);


    public static final int EXCEL_HEADER_ROW = 1;
    public static final int EXCEL_DATA_ROW = 2;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    @Autowired
    private ZkbHighPartInfoRepository zkbHighPartInfoRepository;

    public List<Map<String, Object>> getZkbHighPartMapFromTemplate(ExcelDocument doc) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = EXCEL_DATA_ROW; i <= doc.getWorkBook().getSheetAt(0).getLastRowNum() + 1; i++) {
            Map< String, Object> rowMap = doc.getDataRowMap(EXCEL_HEADER_ROW, i);
            Map<String, Object> map = new LinkedHashMap();
            ZkbHighPartVO zkbHighPartVO = new ZkbHighPartVO();
            zkbHighPartVO.setLineNum(i);
            ZkbHighPartRow row = new ZkbHighPartRow(zkbHighPartVO,rowMap);
            row.setFieldStringValue("备件号", "setPartNo");
            row.setFieldStringValue("类别","setPartType");
            if(zkbHighPartVO.isNull()){
                break;
            }
            map.put("zkbHighPartVO", zkbHighPartVO);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(zkbHighPartVO.getId()) ? ZkbHighPartService.ImportStatus.New.name() : ZkbHighPartService.ImportStatus.Exist.name());
            map.put("hashCode", zkbHighPartVO.hashCode());

            result.add(map);
        }
        return result;
    }

    public void importData(List<Map<String, Object>> importContractMapList, UserAccount currentUser) {

        int row = 0;
        try {
            for (Map<String, Object> item : importContractMapList) {
                ++row;
                if(checkData(item, importContractMapList)) {
                    ZkbHighPartVO zkbHighPartVO = (ZkbHighPartVO)item.get("zkbHighPartVO");
                    importOneZkbHighPart(zkbHighPartVO,currentUser);
                }
            }
        } catch (Exception e) {
            logger.info("error occurred when importing the {} row", row);
            throw e;
        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for importData: {} seconds", duration.getSeconds());
    }


    @Transactional(rollbackFor = Exception.class)
    protected void importOneZkbHighPart(ZkbHighPartVO zkbHighPartVO,UserAccount currentUser) {
        List<ZkbHighPartInfo> list = zkbHighPartInfoRepository.findByPartNo(zkbHighPartVO.getPartNo());
        if(list.size() == 0){
            ZkbHighPartInfo zkbHighPartInfo = new ZkbHighPartInfo();
            BeanUtils.copyProperties(zkbHighPartVO, zkbHighPartInfo);
            zkbHighPartInfo.setCreatedBy(currentUser.getLoginName());
            zkbHighPartInfo.setCreatedDate(new Date());
            if(StringUtils.isNotEmpty(zkbHighPartInfo.getPartType())){
                if("高值备件".equals(zkbHighPartInfo.getPartType())){
                    zkbHighPartInfo.setPartType(ZkbHighPartInfo.PartTypeEnum.highPart.toString());
                }else if("球管".equals(zkbHighPartInfo.getPartType())){
                    zkbHighPartInfo.setPartType(ZkbHighPartInfo.PartTypeEnum.bulbTube.toString());
                }else if("耗材".equals(zkbHighPartInfo.getPartType())){
                    zkbHighPartInfo.setPartType(ZkbHighPartInfo.PartTypeEnum.consumable.toString());
                }else if("工具".equals(zkbHighPartInfo.getPartType())){
                    zkbHighPartInfo.setPartType(ZkbHighPartInfo.PartTypeEnum.tool.toString());
                }else {
                    zkbHighPartInfo.setPartType("");
                }
            }
            zkbHighPartInfoRepository.save(zkbHighPartInfo);
        }
    }

    public boolean checkData(Map<String, Object> item, List<Map<String, Object>> importContractMapList) {

        boolean isValidateData = true;
        ZkbHighPartVO zkbHighPartVO = (ZkbHighPartVO)item.get("zkbHighPartVO");
        Integer hashCode = Integer.valueOf(item.get("hashCode").toString());

        ZkbContractRow.clearErrMessage(item);
        if(zkbHighPartVO.getPartNo() == null){
            item.put("status", ZkbContractImportService.ImportStatus.Failure);
            ZkbHighPartRow.addErrMessage(item, "备件号不能为空！");
            isValidateData = false;
        }

        if(zkbHighPartVO.getPartType() == null){
            item.put("status", ZkbContractImportService.ImportStatus.Failure);
            ZkbHighPartRow.addErrMessage(item, "类别不能为空！");
            isValidateData = false;
        }

       /* for (Map<String, Object> tempContractMap: importContractMapList) {
            ZkbHighPartVO tempHighPartVO = (ZkbHighPartVO)tempContractMap.get("zkbHighPartVO");
            Integer tempHashCode = Integer.valueOf(tempContractMap.get("hashCode").toString());
            if(hashCode.equals(tempHashCode) && zkbHighPartVO.getLineNum() != tempHighPartVO.getLineNum()){
                item.put("status", ZkbContractImportService.ImportStatus.Failure);
                ZkbHighPartRow.addErrMessage(item, WebUtil.getMessage("ErrorDuplicatedData"));
                isValidateData = false;
                break;
            }
        }*/

        return isValidateData;
    }


    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map: list) {
            String importStatus = map.get("status").toString();
            if (importStatus == null) {
                continue;
            }

            if (ZkbHighPartService.ImportStatus.CheckFail.name().equals(importStatus) || ZkbHighPartService.ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }
        }
        return result;
    }
}
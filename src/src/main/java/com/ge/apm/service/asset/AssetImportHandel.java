package com.ge.apm.service.asset;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty;
import com.alibaba.excel.util.ConverterUtils;
import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.Progress;
import com.ge.apm.domain.dto.AssetImport;
import org.apache.commons.lang3.StringUtils;
import webapp.framework.web.WebUtil;

import java.util.*;

public class AssetImportHandel implements ReadListener<AssetImport> {
    private List<AssetImport> importData;

    private boolean crossSite;

    private AssetImportDataCheckService assetImportDataCheckService;

    private List<Map<String, Object>> importOrgMap;
    private List<AssetInfo> unCheckNurseSiteMaintainList;

    private OrgInfo selectedOrgToImport;

    private OrgInfoRepository orgDao;
    private Map<String, Map<String, Object>> i18nMap;
    private Progress progress;

    private Integer rowNum = 1;

    public AssetImportHandel(List<AssetImport> importData, boolean crossSite, AssetImportDataCheckService assetImportDataCheckService, List<Map<String, Object>> importOrgMap, List<AssetInfo> unCheckNurseSiteMaintainList, OrgInfo selectedOrgToImport, OrgInfoRepository orgDao, Progress progress) {
        this.importData = importData;
        this.crossSite = crossSite;
        this.assetImportDataCheckService = assetImportDataCheckService;
        this.importOrgMap = importOrgMap;
        this.unCheckNurseSiteMaintainList = unCheckNurseSiteMaintainList;
        this.selectedOrgToImport = selectedOrgToImport;
        this.orgDao = orgDao;
        this.progress = progress;

    }

    @Override
    public void invoke(AssetImport assetImport, AnalysisContext analysisContext) {
        assetImport.setRowNum(rowNum++);
        Map<Integer, Cell> cellMap = analysisContext.readRowHolder().getCellMap();
        dataFormatErrorHandel(cellMap, assetImport);
        deletedFieldHandel(cellMap, assetImport);
        if (StringUtils.isNotEmpty(assetImport.getUid())) {
            assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.Exist);
        } else {
            assetImport.setImportStatus(AssetFileImportBaseService.ImportStatus.New);
        }
        this.assetImportDataCheckService.i18nConvert(assetImport);

        this.importData.add(assetImport);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public void dataFormatErrorHandel(Map<Integer, Cell> cellMap, AssetImport assetImport) {
        if (Objects.nonNull(cellMap) && Objects.nonNull(cellMap.get(ApmEasyExcelConvert.ERROR_MSG_INDEX))) {
            Cell cell = cellMap.get(ApmEasyExcelConvert.ERROR_MSG_INDEX);
            if (cell instanceof ReadCellData) {
                ReadCellData readCellData = (ReadCellData) cell;
                String errorStr = readCellData.getStringValue();
                if (StringUtils.isNotEmpty(errorStr)) {
                    assetImport.setErrMsg(Arrays.asList(errorStr.split(";")));
                }
            }

        }
    }

    public void deletedFieldHandel(Map<Integer, Cell> cellMap, AssetImport assetImport) {
        if (Objects.nonNull(cellMap) && Objects.nonNull(cellMap.get(ApmEasyExcelConvert.DELETE_FIELD_INDEX))) {
            Cell cell = cellMap.get(ApmEasyExcelConvert.DELETE_FIELD_INDEX);
            if (cell instanceof ReadCellData) {
                ReadCellData readCellData = (ReadCellData) cell;
                String errorStr = readCellData.getStringValue();
                if (StringUtils.isNotEmpty(errorStr)) {
                    assetImport.setDeletedField(Arrays.asList(errorStr.split(";")));
                }
            }
        }
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        //实体配置
        ExcelReadHeadProperty excelReadHeadProperty = new ExcelReadHeadProperty(context.currentReadHolder(), AssetImport.class, null);
        //转换过的excel头配置
        Map<Integer, Head> headMapConverted = new HashMap<>();
        //从excel文件中读取的head配置，key：顺序,value：列名
        Map<Integer, String> excelHeadDataMap = ConverterUtils.convertToStringMap(headMap, context);
        for (Map.Entry<Integer, Head> entry : excelReadHeadProperty.getHeadMap().entrySet()) {
            Head value = entry.getValue();
            ExcelProperty excelProperty = value.getField().getAnnotation(ExcelProperty.class);
            if (excelProperty != null && excelProperty.value().length > 0 && StringUtils.isNotEmpty(excelProperty.value()[0])) {
                String columnName = WebUtil.getMessage(excelProperty.value()[0]);
                if (StringUtils.isNotEmpty(columnName)) {
                    excelHeadDataMap.entrySet().stream().filter(e -> columnName.equals(e.getValue())).findFirst().ifPresent(config -> {
                        value.setColumnIndex(config.getKey());
                        headMapConverted.put(config.getKey(), value);
                    });
                }
            }
        }
        ExcelReadHeadProperty readHeadProperty = context.readSheetHolder().getExcelReadHeadProperty();
        readHeadProperty.setHeadMap(headMapConverted);
        progress.setTotal(context.getTotalCount() - 1);
    }

    public List<AssetImport> getImportData() {
        return importData;
    }

    public void setImportData(List<AssetImport> importData) {
        this.importData = importData;
    }

    public boolean isCrossSite() {
        return crossSite;
    }

    public void setCrossSite(boolean crossSite) {
        this.crossSite = crossSite;
    }

    public AssetImportDataCheckService getAssetImportDataCheckService() {
        return assetImportDataCheckService;
    }

    public void setAssetImportDataCheckService(AssetImportDataCheckService assetImportDataCheckService) {
        this.assetImportDataCheckService = assetImportDataCheckService;
    }

    public List<Map<String, Object>> getImportOrgMap() {
        return importOrgMap;
    }

    public void setImportOrgMap(List<Map<String, Object>> importOrgMap) {
        this.importOrgMap = importOrgMap;
    }

    public List<AssetInfo> getUnCheckNurseSiteMaintainList() {
        return unCheckNurseSiteMaintainList;
    }

    public void setUnCheckNurseSiteMaintainList(List<AssetInfo> unCheckNurseSiteMaintainList) {
        this.unCheckNurseSiteMaintainList = unCheckNurseSiteMaintainList;
    }

    public void orgCheck() {
        if (!crossSite) {
            //only selected site can import clinical dept, otherwise clinical dept excel sheet is meaningless.
            importOrgMap.forEach(item -> {
                OrgInfo newOrgInfo = (OrgInfo) item.get("org");

                //parent uid is not working for child clinical dept, so we use site id
                OrgInfo orgInfo = orgDao.findFirstBySiteUIDAndNameAndOrgType(selectedOrgToImport.getUid(), newOrgInfo.getName(), 4);

                item.put("status", orgInfo == null ? AssetFileImportBaseService.ImportStatus.New : AssetFileImportBaseService.ImportStatus.Exist);
            });
        }
    }

    public OrgInfo getSelectedOrgToImport() {
        return selectedOrgToImport;
    }

    public void setSelectedOrgToImport(OrgInfo selectedOrgToImport) {
        this.selectedOrgToImport = selectedOrgToImport;
    }
}

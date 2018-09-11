/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.pure.AssetInfoFormatter;
import com.ge.apm.service.common.CustomConfigConstant;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.view.sysutil.FieldValueMessageController;
import com.ge.apm.view.sysutil.UserContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author 212579464
 */
@Service
@Scope("prototype")
public class AssetFileExportService extends AssetFileImportBaseService {
    private static final Logger logger = LoggerFactory.getLogger(AssetFileExportService.class);

    /**
     * export asset based on the asset import excel template
     *
     *     @Autowired
     *     private AssetInfoRepository assetDao;ate
     * @param assetList
     * @return
     * @throws IOException
     */
    public ExcelDocument exportAsset(List<AssetInfo> assetList, List<OrgInfo> sites, List<OrgInfo> clinicalDeptList, List<String> exportFields) throws IOException {

        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/assetImport/APM_Asset_Template.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);
        //fix template version
        doc.writeCellData(SHEET_ASSET, 2, 3, EXCEL_ASSET_TEMPLATE_VERSION);
        //remove fake data row
        doc.clearRow(SHEET_ASSET, EXCEL_ASSET_DATA_ROW);
        
        String[] columnKeys = doc.getKeyRow(SHEET_ASSET, EXCEL_ASSET_HEADER_ROW);
        List<String> fields = Arrays.asList(columnKeys).stream().filter(item-> !CustomConfigConstant.getExportFields().containsKey(item) || 
                exportFields.contains(CustomConfigConstant.getExportFields().get(item))).collect(Collectors.toList());
        for(int i=0;i<columnKeys.length;i++){
            if(!fields.contains(columnKeys[i])){
                doc.writeCellData(SHEET_ASSET, EXCEL_ASSET_HEADER_ROW, i+1, "");
                columnKeys[i]="";
            }
        }

        int startRow = AssetFileExportService.EXCEL_ASSET_DATA_ROW;
        for (AssetInfo assetInfo: assetList) {
            AssetInfoFormatter formatter = new AssetInfoFormatter(assetInfo,  sites ,this, assetDao, orgDao, i18nDao);
            Map<String, Object> rowMap = formatter.buildExcelColumnKeyValueMap(true);

            doc.writeRowData(AssetFileExportService.SHEET_ASSET, columnKeys, startRow, rowMap);

            ++startRow;
        }

        String countFormula = String.format("COUNTA('Asset List'!$B$7:$B$%d)", assetList.size() + 6);

        String sumFormula = String.format("SUM('Asset List'!$AA$7:$AA$%d)", assetList.size() + 6);

        doc.writeCellFormula(AssetFileExportService.SHEET_ASSET, 5, 1, countFormula);
        doc.writeCellFormula(AssetFileExportService.SHEET_ASSET, 5, 2, sumFormula);
        doc.getWorkBook().setForceFormulaRecalculation(true);
        doc.calculateFormula();

        //export org info list
        for (startRow = AssetFileExportService.EXCEL_ORG_DATA_ROW; startRow < 20; startRow++) {
            doc.clearRow(AssetFileExportService.SHEET_ORG, startRow);
        }

        startRow = AssetFileExportService.EXCEL_ORG_DATA_ROW;
        for (OrgInfo orgInfo: clinicalDeptList) {
            doc.writeCellData(AssetFileExportService.SHEET_ORG, startRow, 1, orgInfo.getName());

            ++startRow;
        }
        doc.writeCellData(AssetFileExportService.SHEET_ORG, startRow, 1, "#");

        return doc;

    }

    public Boolean exportFailData(ExcelDocument doc, List<Map<String, Object>> importOrgMap, List<Map<String, Object>> importAssetMap) {
        Integer keyRowOrg = 4;
        Integer dataRowOrg = 5;
        Boolean hasFailData = false;
        doc.clearRowsData(SHEET_ORG, 5, 4 + importOrgMap.size());
        for (Map<String, Object> item : importOrgMap) {
            doc.writeRowData(SHEET_ORG, keyRowOrg, dataRowOrg++, (Map<String, Object>) item.get("rowData"));
            hasFailData = item.get("status").equals(ImportStatus.Failure);
        }

        Integer keyRowAsset = 6;
        Integer dataRowAsset = 7;
        doc.clearRowsData(SHEET_ASSET, 7, 6 + importAssetMap.size());
        for (Map<String, Object> item : importAssetMap) {
            if (item.get("status").equals(ImportStatus.Failure)) {
                doc.writeRowData(SHEET_ASSET, keyRowAsset, dataRowAsset++, (Map<String, Object>) item.get("rowData"));
                hasFailData = true;
            }
        }

        return hasFailData;
    }

    public ExcelDocument exportAssetTemplate() throws IOException {
        InputStream stream = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/portal/asset/assetImport/APM_Asset_Template.xlsx");
        ExcelDocument doc = new ExcelDocument(stream, ExcelDocument.ExcelType.XLSX);

        int siteId = UserContextService.getCurrentUserAccount().getSiteId();
        List<I18nMessage> assetGroupList = FieldValueMessageController.getFieldValueList("assetGroup", siteId);

        int rowNum = 2;
        while(true) {
            String cellStringValue = doc.getCellStringValue("设备分类", rowNum, 1);

            if (cellStringValue == null) {
                break;
            }
            doc.clearCell("设备分类", rowNum, 1);

            ++rowNum;
        }
        rowNum = 2;
        for(I18nMessage msg: assetGroupList) {
            doc.writeCellData("设备分类", rowNum, 1, msg.getValueZh());
            ++rowNum;
        }

        return doc;
    }

}

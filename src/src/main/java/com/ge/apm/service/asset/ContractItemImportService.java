package com.ge.apm.service.asset;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.ContractInfoItemRepository;
import com.ge.apm.dao.ContractInfoRepository;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.ContractInfo;
import com.ge.apm.domain.ContractInfoItem;
import com.ge.apm.service.utils.ExcelDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 212605082 on 2018/9/18.
 */
@Service
public class ContractItemImportService {

    private static final Logger logger = LoggerFactory.getLogger(ContractItemImportService.class);

    public static final String SHEET_CONTRACTITEM = "ContractItem List";
    public static final int EXCEL_CONTRACTITEM_HEADER_ROW = 1;
    public static final int EXCEL_CONTRACTITEM_DATA_ROW = 2;

    public static final int EXCEL_EXPORT_CONTRACTITEM_HEADER_ROW = 1;
    public static final int EXCEL_EXPORT_CONTRACTITEM_DATA_ROW = 2;

    @Autowired
    public ContractInfoRepository contractInfoDao;
    @Autowired
    public ContractInfoItemRepository contractInfoItemDao;
    @Autowired
    public AssetInfoRepository assetInfoDao;

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    public List<Map<String, Object>> getContractItemMapFromTemplate(ExcelDocument doc, String contractType) throws Exception {

        List<Map<String, Object>> result = new ArrayList<>();

        for (int i = EXCEL_CONTRACTITEM_DATA_ROW ; i <= doc.getWorkBook().getSheet(SHEET_CONTRACTITEM).getLastRowNum() + 1 ; i++) {

            Map< String, Object> rowMap = doc.getDataRowMap(SHEET_CONTRACTITEM, EXCEL_CONTRACTITEM_HEADER_ROW, i);

            Map<String, Object> map = new LinkedHashMap();
            ContractInfoItem contractInfoItem = new ContractInfoItem();

            contractInfoItem.setLineNum(i);
            contractInfoItem.setItemType(ContractInfoItem.ContractInfoItemType.ExistAsset.name());

            ContractItemRow contractItemRow = new ContractItemRow(contractInfoItem, rowMap);
            contractItemRow.setFieldStringValue("合同UID", "setContractId");
            contractItemRow.setFieldStringValue("相关设备UID", "setAssetUid");

            if(contractInfoItem.getContractId() == null && contractInfoItem.getAssetUid() == null){
                break;
            }

            map.put("contractInfoItem", contractInfoItem);
            map.put("rowData", rowMap);
            map.put("status", StringUtils.isEmpty(contractInfoItem.getId()) ? ContractItemImportService.ImportStatus.New.name() : ContractItemImportService.ImportStatus.Exist.name());

            result.add(map);

        }

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getContractMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }

    public void importData(List<Map<String, Object>> importContractItemMapList, String contractType, String tenantUID) {

        int row = 0;
        try {
            for (Map<String, Object> item : importContractItemMapList) {
                ++row;
                if(this.checkData(item, contractType, tenantUID)){
                    importOneContractItem(item);
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

    @Transactional
    protected void importOneContractItem(Map<String, Object> item) {

        ContractInfoItem contractInfoItem = (ContractInfoItem)item.get("contractInfoItem");

        //ContractItemRow.clearErrMessage(item);
        //item.put("status", StringUtils.isEmpty(contractInfoItem.getId()) ? ImportStatus.Created.name() : ImportStatus.Exist.name());

        contractInfoItemDao.save(contractInfoItem);

    }

    public boolean checkData(Map<String, Object> item, String contractType, String tenantUID) {

        boolean isValidateData = true;

        ContractInfoItem contractInfoItem = (ContractInfoItem)item.get("contractInfoItem");

        ContractItemRow.clearErrMessage(item);

        ContractInfo contractInfo = null;
        if(contractInfoItem.getContractId() == null){
            item.put("status", ImportStatus.Failure);
            ContractItemRow.addErrMessage(item, "错误: 合同UID不能为空");
            isValidateData = false;
        }else{
            contractInfo = contractInfoDao.findByIdAndContractTypeAndTenantUID(contractInfoItem.getContractId(), contractType, tenantUID);
            if(contractInfo == null){
                item.put("status", ImportStatus.Failure);
                ContractItemRow.addErrMessage(item, "错误: 无效的合同UID");
                isValidateData = false;
            }
        }

        AssetInfo assetInfo = null;
        if(contractInfoItem.getAssetUid() == null){
            item.put("status", ImportStatus.Failure);
            ContractItemRow.addErrMessage(item, "错误: 相关设备UID不能为空");
            isValidateData = false;
        }else{
            if(contractInfo != null){
                assetInfo = assetInfoDao.findByUidAndTenantUID(contractInfoItem.getAssetUid(), tenantUID);
                if(assetInfo == null || !assetInfo.getSiteUID().equals(contractInfo.getSiteUID())){
                    item.put("status", ImportStatus.Failure);
                    ContractItemRow.addErrMessage(item, "错误: 无效的相关设备UID");
                    isValidateData = false;
                }
            }
        }

        if(contractInfo != null && assetInfo != null){
            List<ContractInfoItem> contractInfoItemList = contractInfoItemDao.findByContractIdAndAssetUid(contractInfoItem.getContractId(), contractInfoItem.getAssetUid());
            if(!contractInfoItemList.isEmpty()){
                item.put("status", ImportStatus.Failure);
                ContractItemRow.addErrMessage(item, "错误: 合同中已经存在该设备");
                isValidateData = false;
            }
        }

        return isValidateData;

    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importContractItemMapList) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> map: importContractItemMapList) {

            String importStatus = map.get("status").toString();

            if (importStatus == null) {
                continue;
            }

            if (ImportStatus.CheckFail.name().equals(importStatus) || ImportStatus.Failure.name().equals(importStatus)) {
                result.add(map);
            }

        }

        return result;
    }


}

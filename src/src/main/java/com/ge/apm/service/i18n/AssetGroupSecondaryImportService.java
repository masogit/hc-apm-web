package com.ge.apm.service.i18n;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.service.asset.AssetFileImportBaseService;
import com.ge.apm.service.asset.AssetFileImportStreamingService;
import com.ge.apm.service.asset.AssetRow;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Transactional
public class AssetGroupSecondaryImportService  {
    private static final Logger logger = LoggerFactory.getLogger(AssetGroupSecondaryImportService.class);

    @Autowired
    TenantInfoRepository tenantInfoDao;
    @Autowired
    UserAccountRepository userAccountDao;
    @Autowired
    I18nMessageRepository i18nMessageDao;

    public void importAssetGroupSecondary(ExcelDocumentStreamingReader excelDoc) {
        Iterator<Map<String, ExcelDocument.ValueWrapper>> iterator = excelDoc.iterator();
        while (iterator.hasNext()) {
            Map<String, ExcelDocument.ValueWrapper> rowMap = iterator.next();


            String assetGroup = ExcelDocument.getCellStringValue(rowMap.get("设备分类"));
            String assetGroupSecondary = ExcelDocument.getCellStringValue(rowMap.get("设备分类2级"));

            if (assetGroup == null || assetGroup.isEmpty()) {
                break;
            }

            logger.info(assetGroup + ' ' + assetGroupSecondary);


        }
    }
}


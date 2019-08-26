package com.ge.apm.service.i18n;

import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
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

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AssetGroupSecondaryImportService  {
    private static final Logger logger = LoggerFactory.getLogger(AssetGroupSecondaryImportService.class);

    private static final String MSG_TYPE_ASSET_GROUP = "assetGroup";
    private static final String MSG_TYPE_ASSET_GROUP_SECONDARY_PREFIX = "assetGroupSecondary_";

    @Autowired
    TenantInfoRepository tenantInfoDao;
    @Autowired
    UserAccountRepository userAccountDao;
    @Autowired
    I18nMessageRepository i18nMessageDao;

    public void importAssetGroupSecondary(String tenantUid, ExcelDocumentStreamingReader excelDoc) {
        TenantInfo tenantInfo = tenantInfoDao.findByuid(tenantUid);

        Iterator<Map<String, ExcelDocument.ValueWrapper>> iterator = excelDoc.iterator();
        while (iterator.hasNext()) {
            Map<String, ExcelDocument.ValueWrapper> rowMap = iterator.next();


            String assetGroup = ExcelDocument.getCellStringValue(rowMap.get("设备分类"));
            String assetGroupSecondary = ExcelDocument.getCellStringValue(rowMap.get("设备分类2级"));

            if (assetGroup == null || assetGroup.isEmpty()) {
                break;
            }

            logger.info(assetGroup + ' ' + assetGroupSecondary);

            I18nMessage foundAssetGroup = i18nMessageDao.findFirstByMsgTypeAndValueZhAndSiteId(MSG_TYPE_ASSET_GROUP, assetGroup, tenantInfo.getId());

            if (foundAssetGroup == null) {
                int nextMsgKey = 1;
                List<BigDecimal> maxMsgKey = i18nMessageDao.getMaxMsgKeyByMsgTypeAndSiteId(MSG_TYPE_ASSET_GROUP, tenantInfo.getId());
                if (maxMsgKey.get(0) != null) {
                    nextMsgKey = maxMsgKey.get(0).intValue() + 1;
                }

                foundAssetGroup = new I18nMessage();
                foundAssetGroup.setMsgType(MSG_TYPE_ASSET_GROUP);
                foundAssetGroup.setValueZh(assetGroup);
                foundAssetGroup.setSiteId(tenantInfo.getId());
                foundAssetGroup.setMsgKey(String.valueOf(nextMsgKey));

                i18nMessageDao.save(foundAssetGroup);
            }

            String msgTypeOfAssetGroupSecondary = MSG_TYPE_ASSET_GROUP_SECONDARY_PREFIX + foundAssetGroup.getMsgKey();
            I18nMessage foundAssetGroupSecondary = i18nMessageDao.findFirstByMsgTypeAndValueZhAndSiteId(msgTypeOfAssetGroupSecondary, assetGroupSecondary, tenantInfo.getId());

            if (foundAssetGroupSecondary == null) {
                int nextMsgKey = 1;
                List<BigDecimal> maxMsgKey = i18nMessageDao.getMaxMsgKeyByMsgTypeAndSiteId(msgTypeOfAssetGroupSecondary, tenantInfo.getId());
                if (maxMsgKey.get(0) != null) {
                    nextMsgKey = maxMsgKey.get(0).intValue() + 1;
                }

                foundAssetGroupSecondary = new I18nMessage();
                foundAssetGroupSecondary.setMsgType(msgTypeOfAssetGroupSecondary);
                foundAssetGroupSecondary.setValueZh(assetGroupSecondary);
                foundAssetGroupSecondary.setSiteId(tenantInfo.getId());
                foundAssetGroupSecondary.setMsgKey(String.valueOf(nextMsgKey));

                i18nMessageDao.save(foundAssetGroupSecondary);
            }


        }
    }
}


package com.ge.apm.service.i18n;

import com.ge.apm.dao.AssetInfoRepository;
import com.ge.apm.dao.DeptTransactionLogRepository;
import com.ge.apm.dao.I18nMessageRepository;
import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.domain.I18nMessage;
import com.ge.apm.domain.TenantInfo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class I18nConfigService {

    @Autowired
    private I18nMessageRepository i18nMessageRepository;
    @Autowired
    private TenantInfoRepository tenantInfoRepository;
    @Autowired
    private DeptTransactionLogRepository deptTransactionLogRepository;
    @Autowired
    private AssetInfoRepository assetInfoRepository;

    @Transactional(rollbackFor = Exception.class)
    public void deleteAndInsertI18n(List<I18nMessage> list, List<I18nMessage> removeList){
        if(CollectionUtils.isNotEmpty(removeList)){
            i18nMessageRepository.delete(removeList);
        }
        if(CollectionUtils.isNotEmpty(list)){
            i18nMessageRepository.save(list);
        }
    }

    @Transactional(readOnly = true)
    public boolean beforeDeleteCheck(String msgType, String msgKey, Integer tenantId){

        TenantInfo tenantInfo = tenantInfoRepository.findById(tenantId);

        switch (msgType){
            case "transaction":
                int count = deptTransactionLogRepository.countByCategoryAndTenant(Integer.valueOf(msgKey), tenantInfo.getUid());
                if(count > 0){
                    return false;
                }
                break;
            case "assetClass":
                int assetCount = assetInfoRepository.countByAssetClassAndTenant(Integer.valueOf(msgKey), tenantInfo.getUid());
                if(assetCount > 0){
                    return false;
                }
                break;
            default:
        }
        return true;
    }

}


package com.ge.apm.service.audit;

import com.ge.apm.dao.AuditConfigItemRepository;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.AuditConfigItem;
import com.ge.apm.domain.V2_BlobObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuditItemConfigService {

    @Autowired
    private BlobObjectRepository blobDao;

    @Autowired
    private AuditConfigItemRepository auditConfigItemDao;

    @Transactional(rollbackFor = Exception.class)
    public void saveAuditConfigItem(AuditConfigItem auditConfigItem, List<V2_BlobObject> list){
        auditConfigItem=auditConfigItemDao.save(auditConfigItem);
        List<V2_BlobObject> attachList = new ArrayList();
        if(CollectionUtils.isNotEmpty(list)){
            for (V2_BlobObject item : list) {
                V2_BlobObject blobObject = blobDao.findAllByObjectStorageId(item.getObjectStorageId()).stream().findFirst().get();
                blobObject.setBoType(V2_BlobObject.BO_TYPE_AUDIT_CONFIG_ITEM);
                blobObject.setBoSubType(V2_BlobObject.BO_SUB_TYPE_IMG);
                blobObject.setBoUuid(auditConfigItem.getUid());
                attachList.add(blobObject);
            }
            blobDao.save(attachList);
        }
    }
}

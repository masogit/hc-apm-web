package com.ge.apm.aop.audit;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.BlobObjectRepository;
import com.ge.apm.domain.V2_BlobObject;
import com.ge.healthcare.cn.common.cons.SystemEventAuditEnum;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("v2_blob_object")
public class BlobObjectAuditHandler implements AuditHandler {
    @Autowired
    private BlobObjectRepository blobDao;


    @Override
    public String getBoType() {
        return "v2_blob_object";
    }

    @Override
    public SystemEventAuditEnum determineEventEnum(Object[] args, String boType, String operation) {
        SystemEventAuditEnum systemEventAuditEnum=null;
        List<V2_BlobObject> blobList = new ArrayList<>();
        if (args.length > 0 && args[0] instanceof Iterable) {
            Iterable<V2_BlobObject> iterable = (Iterable<V2_BlobObject>) args[0];
            iterable.forEach(blobList::add);
        } else {
            if(args[0] instanceof V2_BlobObject ){
                V2_BlobObject v2BlobObject = (V2_BlobObject) args[0];
                blobList.add(v2BlobObject);
            }else  if(args[0] instanceof String ){
                String id = (String) args[0];
                V2_BlobObject byId = blobDao.findById(id);
                if(byId!=null){
                    blobList.add(byId);
                }

            }
        }
        blobList = blobList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(blobList)) {
            return null;
        } else {
            V2_BlobObject v2BlobObject = blobList.get(0);

            switch (v2BlobObject.getBoType()) {
                case V2_BlobObject.BO_TYPE_AssetContract:
                    if ("delete".equals(operation)) {
                        systemEventAuditEnum = SystemEventAuditEnum.CONTRACT_ATTACHMENT_DELETE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.CONTRACT_ATTACHMENT_ADD;
                    }
                    break;
                case V2_BlobObject.BO_TYPE_ArrivalList:
                    if ("delete".equals(operation)) {
                        systemEventAuditEnum = SystemEventAuditEnum.ARRIVAL_LIST_ATTACHMENT_DELETE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.ARRIVAL_LIST_ATTACHMENT_ADD;
                    }
                    break;
                case V2_BlobObject.BO_TYPE_ArrivalListItem:
                    if ("delete".equals(operation)) {
                        systemEventAuditEnum = SystemEventAuditEnum.ARRIVAL_ITEM_ATTACHMENT_DELETE;
                    } else {
                        systemEventAuditEnum = SystemEventAuditEnum.ARRIVAL_ITEM_ATTACHMENT_ADD;
                    }
                    break;
            }
            return systemEventAuditEnum;
        }
    }

    @Override
    public String generateEventData(Object[] args) {

        List<V2_BlobObject> blobList = new ArrayList<>();
        if (args.length > 0 && args[0] instanceof Iterable) {
            Iterable<V2_BlobObject> iterable = (Iterable<V2_BlobObject>) args[0];
            iterable.forEach(blobList::add);
        } else {
            if(args[0] instanceof V2_BlobObject ){
                V2_BlobObject v2BlobObject = (V2_BlobObject) args[0];
                blobList.add(v2BlobObject);
            }else  if(args[0] instanceof String ){
                String id = (String) args[0];
                V2_BlobObject byId = blobDao.findById(id);
                if(byId!=null){
                    blobList.add(byId);
                }

            }
        }
        blobList = blobList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(blobList)) {
            return null;
        }
        Map<String, List<V2_BlobObject>> blobMap = blobList.stream().collect(Collectors.groupingBy(blob -> blob.getBoSubType() == null ? "unknown" : blob.getBoSubType()));
        HashMap<Object, Object> eventMap = new HashMap<>();
        for (Map.Entry<String, List<V2_BlobObject>> stringListEntry : blobMap.entrySet()) {
            List<V2_BlobObject> value = stringListEntry.getValue();
            List<String> objectStorageIds = value.stream().map(V2_BlobObject::getObjectStorageId).collect(Collectors.toList());
            eventMap.put(stringListEntry.getKey(), objectStorageIds);
        }
        return JSON.toJSONString(eventMap);
    }

    @Override
    public String extractBoUid(MethodSignature signature,Object[] args) {

        List<V2_BlobObject> blobList = new ArrayList<>();
        if (args.length > 0 && args[0] instanceof Iterable) {
            Iterable<V2_BlobObject> iterable = (Iterable<V2_BlobObject>) args[0];
            iterable.forEach(blobList::add);
        } else {
            if(args[0] instanceof V2_BlobObject ){
                V2_BlobObject v2BlobObject = (V2_BlobObject) args[0];
                blobList.add(v2BlobObject);
            }else  if(args[0] instanceof String ){
                String id = (String) args[0];
                V2_BlobObject byId = blobDao.findById(id);
                if(byId!=null){
                    blobList.add(byId);
                }

            }
        }
        blobList = blobList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(blobList)) {
            return null;
        } else {
            V2_BlobObject v2BlobObject = blobList.get(0);
            return v2BlobObject.getBoUuid();

        }
    }

    @Override
    public boolean isSave(Object[] args, String boType, String operation) {
        List<V2_BlobObject> blobList = new ArrayList<>();
        if (args.length > 0 && args[0] instanceof Iterable) {
            Iterable<V2_BlobObject> iterable = (Iterable<V2_BlobObject>) args[0];
            iterable.forEach(blobList::add);
        } else {
            if(args[0] instanceof V2_BlobObject ){
                V2_BlobObject v2BlobObject = (V2_BlobObject) args[0];
                blobList.add(v2BlobObject);
            }else  if(args[0] instanceof String ){
                String id = (String) args[0];
                V2_BlobObject byId = blobDao.findById(id);
                if(byId!=null){
                    blobList.add(byId);
                }

            }
        }
        blobList = blobList.stream().filter(item -> item.getBoUuid() != null).collect(Collectors.toList());

        return !CollectionUtils.isEmpty(blobList);
    }
}

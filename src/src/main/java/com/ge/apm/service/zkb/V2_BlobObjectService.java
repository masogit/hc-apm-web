package com.ge.apm.service.zkb;

import com.ge.apm.dao.mapper.V2_BlobObjectMapper;
import com.ge.apm.domain.V2_BlobObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import webapp.framework.dao.SearchFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 503202755 2020/9/8
 */
@Component
public class V2_BlobObjectService {

    private static final Logger logger = LoggerFactory.getLogger(V2_BlobObjectService.class);


    @Autowired
    private V2_BlobObjectMapper v2_BlobObjectMapper;


    /**
     * 根据条件搜索
     * @param mainDirectory 主目录
     * @param secondDirectory 子目录
     * @param objectName 名称
     * @param boSubType 文件类型
     * @param operationType 操作类型
     * @param tenantUid 用户ID
     * @return
     */
    public List<V2_BlobObject> loadZkbOrderHistoryData(String mainDirectory, String secondDirectory, String objectName, String boSubType, String operationType, String tenantUid) {
        List<SearchFilter> searchFilters = new ArrayList<SearchFilter>();
        if (mainDirectory != null) {
            SearchFilter dataTypeFilter = new SearchFilter("mainDirectory", SearchFilter.Operator.LIKE, mainDirectory);
            searchFilters.add(dataTypeFilter);
        }
        if (secondDirectory != null) {
            SearchFilter secondDirectoryFilter = new SearchFilter("secondDirectory", SearchFilter.Operator.LIKE, secondDirectory);
            searchFilters.add(secondDirectoryFilter);
        }
        if (objectName != null) {
            SearchFilter objectNameFilter = new SearchFilter("objectName", SearchFilter.Operator.LIKE, objectName);
            searchFilters.add(objectNameFilter);
        }
        if (boSubType != null) {
            SearchFilter boSubTypeFilter = new SearchFilter("boSubType", SearchFilter.Operator.EQ, boSubType);
            searchFilters.add(boSubTypeFilter);
        }
        if (operationType != null) {
            SearchFilter operationTypeFilter = new SearchFilter("operationType", SearchFilter.Operator.EQ, operationType);
            searchFilters.add(operationTypeFilter);
        }
        return v2_BlobObjectMapper.loadV2_BlobObject(searchFilters, tenantUid, V2_BlobObject.BO_TYPE_ZKB);
    }

}

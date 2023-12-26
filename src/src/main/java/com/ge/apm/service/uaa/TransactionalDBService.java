package com.ge.apm.service.uaa;

import com.alibaba.fastjson.JSON;
import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.dao.mapper.AdminConsoleMapper;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.V2_PmOrder;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionalDBService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AdminConsoleMapper adminConsoleMapper;
    @Autowired
    private UnstructuredDataCollectionRepository collectionRepository;

    @Transactional
    public void savePmOrderDB(Map<String, PmOrderRule> ruleMap, List<V2_PmOrder> saveList, List<UnstructuredDataCollection> saveDataCollectionList) throws InterruptedException {
//        Thread.sleep(1000);
        if(ObjectUtil.isNotEmpty(saveDataCollectionList)){
            for (UnstructuredDataCollection dataCollection : saveDataCollectionList) {
                try {
                    collectionRepository.save(dataCollection);
                } catch (Exception e) {
                    logger.error("savePmOrderDBerr dataCollection : {}", JSON.toJSONString(dataCollection), e);
                    logger.error("savePmOrderDBerr saveDataCollectionList : {}", JSON.toJSONString(saveDataCollectionList), e);
                    throw new RuntimeException(e);
                }
            }
        }
        Map<String, List<V2_PmOrder>> pmOrderMap = saveList.stream().collect(Collectors.groupingBy(V2_PmOrder::getPmRuleUid));
        for (Map.Entry<String, List<V2_PmOrder>> entry : pmOrderMap.entrySet()) {
            List<String> orderIds = entry.getValue().stream().map(V2_PmOrder::getId).collect(Collectors.toList());
            PmOrderRule pmOrderRule = ruleMap.get(entry.getKey());
            if(ObjectUtil.isNotEmpty(pmOrderRule)){
//                Thread.sleep(500);
                adminConsoleMapper.updatePmOrderByIds(pmOrderRule, orderIds);
            }
        }
    }
}

package com.ge.apm.service.uaa;

import com.ge.apm.dao.UnstructuredDataCollectionRepository;
import com.ge.apm.dao.mapper.AdminConsoleMapper;
import com.ge.apm.domain.PmOrderRule;
import com.ge.apm.domain.UnstructuredDataCollection;
import com.ge.apm.domain.V2_PmOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionalDBService {

    @Autowired
    private AdminConsoleMapper adminConsoleMapper;
    @Autowired
    private UnstructuredDataCollectionRepository collectionRepository;

    @Transactional
    public void savePmOrderDB(Map<String, PmOrderRule> ruleMap, List<V2_PmOrder> saveList, List<UnstructuredDataCollection> saveDataCollectionList, Set<String> collectionIds) {
        if(!CollectionUtils.isEmpty(collectionIds)){
            adminConsoleMapper.deleteUnStructureDataByCollectionIds(collectionIds);
            adminConsoleMapper.deleteUnStructureDataCollectionByIds(collectionIds);
        }
        System.out.println("save =============================================");
        if(!CollectionUtils.isEmpty(saveDataCollectionList)){
            try {
                collectionRepository.save(saveDataCollectionList);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        Map<String, List<V2_PmOrder>> pmOrderMap = saveList.stream().collect(Collectors.groupingBy(V2_PmOrder::getPmRuleUid));
        for (Map.Entry<String, List<V2_PmOrder>> entry : pmOrderMap.entrySet()) {
            List<String> orderIds = entry.getValue().stream().map(V2_PmOrder::getId).collect(Collectors.toList());
            PmOrderRule pmOrderRule = ruleMap.get(entry.getKey());
            if(!CollectionUtils.isEmpty(orderIds) && pmOrderRule!=null){
                adminConsoleMapper.updatePmOrderByIds(pmOrderRule, orderIds);
            }
        }
    }
}

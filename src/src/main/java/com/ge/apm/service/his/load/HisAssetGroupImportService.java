package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisAssetGroupRepository;
import com.ge.apm.domain.his.HisAssetGroup;
import com.ge.apm.service.utils.ExcelDocument;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HisAssetGroupImportService {

    @Autowired
    HisAssetGroupRepository hisAssetGroupRepository;

    public List<HisAssetGroup> parse(ExcelDocument doc){

        List<HisAssetGroup> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String uuid = ExcelDocument.getCellStringValue(rowMap.get("uuid"));
            String name = ExcelDocument.getCellStringValue(rowMap.get("name"));

            if (null == uuid) {
                break;
            }

            HisAssetGroup data = new HisAssetGroup();
            data.setUuid(uuid);
            data.setName(name);


            resultList.add(data);
        }

        return resultList;
    }

    public List<HisAssetGroup> saveAll(List<HisAssetGroup> data){
        Iterable<HisAssetGroup> iterable = hisAssetGroupRepository.save(data);

        return Lists.newArrayList(iterable);
    }
}

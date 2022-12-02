package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisEquipmentConnectRepository;
import com.ge.apm.domain.his.HisEquipmentConnect;
import com.ge.apm.service.utils.ExcelDocument;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HisEquipmentImportService {

    @Autowired
    HisEquipmentConnectRepository hisEquipmentConnectRepository;

    public List<HisEquipmentConnect> parse(ExcelDocument doc){

        List<HisEquipmentConnect> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String id = ExcelDocument.getCellStringValue(rowMap.get("id"));
            String eqId = ExcelDocument.getCellStringValue(rowMap.get("eq_id"));
            String eqName = ExcelDocument.getCellStringValue(rowMap.get("eq_name"));
            String eqLb = ExcelDocument.getCellStringValue(rowMap.get("eq_lb"));
            String eqHosid = ExcelDocument.getCellStringValue(rowMap.get("eq_hosid"));
//            String createdDate = ExcelDocument.getCellStringValue(rowMap.get("create_date"));
//            String isMatched = ExcelDocument.getCellStringValue(rowMap.get("is_matched"));

            if (null == id) {
                break;
            }

            HisEquipmentConnect data = new HisEquipmentConnect();
            data.setId(id);
            data.setEqId(eqId);
            data.setEqName(eqName);
            data.setEqLb(eqLb);
            data.setEqHosid(eqHosid);
            data.setIsMatched(false);
            data.setCreatedDate(new Date());

            resultList.add(data);
        }

        return resultList;
    }

    public List<HisEquipmentConnect> saveAll(List<HisEquipmentConnect> data){
        Iterable<HisEquipmentConnect> iterable = hisEquipmentConnectRepository.save(data);

        return Lists.newArrayList(iterable);
    }

}

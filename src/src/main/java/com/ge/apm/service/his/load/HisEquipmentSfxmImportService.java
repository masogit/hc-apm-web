package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisEquipmentSfxmRepository;
import com.ge.apm.domain.his.HisEquipmentSfxm;
import com.ge.apm.service.utils.ExcelDocument;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HisEquipmentSfxmImportService {

    @Autowired
    HisEquipmentSfxmRepository hisEquipmentSfxmRepository;

    public List<HisEquipmentSfxm> parse(ExcelDocument doc){

        List<HisEquipmentSfxm> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String id = ExcelDocument.getCellStringValue(rowMap.get("id"));
            String sfxmId = ExcelDocument.getCellStringValue(rowMap.get("sfxm_id"));
            String sfxmName = ExcelDocument.getCellStringValue(rowMap.get("sfxm_name"));
            String sfxmLb = ExcelDocument.getCellStringValue(rowMap.get("sfxm_lb"));
            String hosId = ExcelDocument.getCellStringValue(rowMap.get("hos_id"));
//            String isMatched = ExcelDocument.getCellStringValue(rowMap.get("is_matched"));

            if (null == id) {
                break;
            }

            HisEquipmentSfxm data = new HisEquipmentSfxm();
            data.setId(id);
            data.setSfxmId(sfxmId);
            data.setSfxmName(sfxmName);
            data.setSfxmLb(sfxmLb);
            data.setHosId(hosId);
            data.setIsMatched(false);

            resultList.add(data);
        }

        return resultList;
    }

    public List<HisEquipmentSfxm> saveAll(List<HisEquipmentSfxm> data){
        Iterable<HisEquipmentSfxm> iterable = hisEquipmentSfxmRepository.save(data);

        return Lists.newArrayList(iterable);
    }

    public List<Map<String, Object>> getMapFromTemplate(ExcelDocument doc) {
        return null;
    }

    public void importData(List<Map<String, Object>> importMapList) {
    }

    public List<Map<String, Object>> filterErrorRecords(List<Map<String, Object>> importMapList) {
        return null;
    }
}

package com.ge.apm.service.his.load;

import com.ge.apm.dao.HisAdministrativeOfficesRepository;
import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.service.utils.ExcelDocument;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HisAdministrativeOfficeImportService {

    @Autowired
    HisAdministrativeOfficesRepository hisAdministrativeOfficesRepository;

    public List<HisAdministrativeOffices> parse(ExcelDocument doc){

        List<HisAdministrativeOffices> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            String id = ExcelDocument.getCellStringValue(rowMap.get("id"));
            String hisKsId = ExcelDocument.getCellStringValue(rowMap.get("his_ksid"));
            String hisKsName = ExcelDocument.getCellStringValue(rowMap.get("his_ksname"));
            String hosId = ExcelDocument.getCellStringValue(rowMap.get("hos_id"));
            String tyBz = ExcelDocument.getCellStringValue(rowMap.get("ty_bz"));
//            String isMatched = ExcelDocument.getCellStringValue(rowMap.get("is_matched"));

            if (null == id) {
                break;
            }

            HisAdministrativeOffices hisAdministrativeOffices = new HisAdministrativeOffices();
            hisAdministrativeOffices.setId(id);
            hisAdministrativeOffices.setHisKsid(hisKsId);
            hisAdministrativeOffices.setHisKsname(hisKsName);
            hisAdministrativeOffices.setHosId(hosId);
            hisAdministrativeOffices.setTyBz(tyBz);
            hisAdministrativeOffices.setIsMatched(false);

            resultList.add(hisAdministrativeOffices);
        }

        return resultList;
    }

    public List<HisAdministrativeOffices> saveAll(List<HisAdministrativeOffices> data){
        Iterable<HisAdministrativeOffices> iterable = hisAdministrativeOfficesRepository.save(data);

        return Lists.newArrayList(iterable);
    }
}

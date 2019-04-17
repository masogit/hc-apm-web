package com.ge.apm.service.nurseSite;

import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainItem;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class WordReportExportService {

    public byte[] writeMaintainDetailToDocx(byte[] templateBytes, List<NurseSiteMaintain> listMaintain) throws IOException {
        List<Map<String, Object>> datasList = new ArrayList<>();
        datasList.add(new HashMap<>());

        TreeMap<String, List<NurseSiteMaintainItem>> map = new TreeMap<>();
        for (NurseSiteMaintain maintain : listMaintain) {
            String checkDate = DateFormatUtils.format(maintain.getCheckDate(), "yyyy-MM-dd");
            map.put(checkDate, maintain.getListNurseSiteMaintainItem());
        }

        map.entrySet().stream().forEach(i -> {
            Map<String, Object> datas = datasList.get(datasList.size() - 1);
            TreeMap<String, List<NurseSiteMaintainItem>> subMap = (TreeMap<String, List<NurseSiteMaintainItem>>) datas.computeIfAbsent("tables", k -> new ArrayList<>());
            subMap.put(i.getKey(), i.getValue());
            if (subMap.size() == 10) {
                datasList.add(new HashMap<>());
            }
        });

        for (Map<String, Object> datas : datasList) {

        }

        return null;
    }
}

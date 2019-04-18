package com.ge.apm.service.nurseSite;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainItem;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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

            //date+i first row
            datas.put("date" + subMap.size(), i.getKey());

            //asset pass area
            if (subMap.size() == 10) {
                datasList.add(new HashMap<>());
            }
        });

        for (Map<String, Object> datas : datasList) {
            export(templateBytes, datas);
        }

        return null;
    }

    public byte[] export(byte[] templateBytes, Map<String, Object> datas) {
        XWPFTemplate template = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream inputStream = new ByteArrayInputStream(templateBytes)) {
            template = XWPFTemplate.compile(inputStream);
            template.registerPolicy("tables", new AssetCheckTableRenderPolicy());
            template.render(datas);
            template.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (template != null) {
                    template.close();
                }
            } catch (Exception ignore) {

            }
        }
    }

    private class AssetCheckTableRenderPolicy extends DynamicTableRenderPolicy {
        @Override
        public void render(XWPFTable table, Object data) {
            Map<String, List<NurseSiteMaintainItem>> itemMap = (TreeMap<String, List<NurseSiteMaintainItem>>) data;

            List<String> columnHeaders = getColumnHeaders(table);

            Map<Integer, AssetInfo> assetInfos = getIdAssetInfoMap(itemMap);
            List<Integer> assetRowHeaders = assetInfos.keySet().stream().collect(Collectors.toList());

            populateAssetInfoColumns(table, assetInfos.values());
            populateCheckPass(table, columnHeaders, itemMap, assetRowHeaders);

            Map<String, String> checkItems = new TreeMap<>(Comparator.naturalOrder());
            itemMap.values().forEach(i -> i.forEach(j -> {
                j.getListData().forEach(k -> checkItems.put(k.getId(), k.getCheckItem()));
            }));
            List<String> checkItemRowHeaders = checkItems.keySet().stream().collect(Collectors.toList());
            populateCheckItemColumns(table, checkItems.values());

        }

        public List<String> getColumnHeaders(XWPFTable table) {
            List<XWPFTableCell> tableCells = table.getRow(0).getTableCells();
            List<String> columnHeaders = new ArrayList<>();
            for (int i = 0; i < tableCells.size(); i++) {
                XWPFTableCell cell = tableCells.get(i);
                columnHeaders.add(cell.getText());
            }
            return columnHeaders;
        }

        public Map<Integer, AssetInfo> getIdAssetInfoMap(Map<String, List<NurseSiteMaintainItem>> itemMap) {
            Map<Integer, AssetInfo> assetInfos = new TreeMap<>();
            itemMap.values().forEach(i -> i.forEach(j -> assetInfos.put(j.getAssetInfo().getId(), j.getAssetInfo())));
            return assetInfos;
        }

        public void populateCheckPass(XWPFTable table, List<String> columnHeaders, Map<String, List<NurseSiteMaintainItem>> itemMap, List<Integer> rowHeaders) {
            itemMap.entrySet().forEach(i -> {
                int columnIndex = columnHeaders.indexOf(i.getKey());
                List<NurseSiteMaintainItem> items = i.getValue();
                for (int j = 0; j < items.size(); j++) {
                    NurseSiteMaintainItem item = items.get(j);
                    Integer assetId = item.getAssetInfo().getId();
                    int rowIndex = rowHeaders.indexOf(assetId) + 1;
                    table.getRow(rowIndex).getCell(columnIndex).setText("√");
                }
            });
        }

        public void populateAssetInfoColumns(XWPFTable table, Collection<AssetInfo> assetInfos) {
            XWPFTableRow firstRow = table.getRow(0);
            int size = firstRow.getTableCells().size();
            for (AssetInfo assetInfo : assetInfos) {
                XWPFTableRow row = table.insertNewTableRow(1);
                row.createCell().setText(assetInfo.getName());
                row.createCell().setText(assetInfo.getDepartNum());
                for (int i = 2; i < size; i++) {
                    row.createCell();
                }
            }
        }


        public void populateCheckItemColumns(XWPFTable table, Collection<String> checkItems) {
            XWPFTableRow firstRow = table.getRow(0);
            int size = firstRow.getTableCells().size();
            for (String checkItem : checkItems) {
                XWPFTableRow row = table.insertNewTableRow(table.getNumberOfRows() - 1);
                row.createCell().setText(checkItem);
                for (int i = 1; i < size; i++) {
                    row.createCell();
                }
            }
        }
    }
}

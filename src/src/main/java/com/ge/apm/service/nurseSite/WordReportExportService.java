package com.ge.apm.service.nurseSite;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.utils.ImageUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class WordReportExportService {

    @Autowired
    BlobFileService blobFileService;

    private final static List<Integer> PASS_STATUS = Arrays.asList(2, 5, 6, 7);
    private final static List<Integer> FAILURE_STATUS = Arrays.asList(3, 8);

    public byte[] writeMaintainDetailToDocx(byte[] templateBytes, List<NurseSiteMaintain> listMaintain) throws IOException {

        if (listMaintain.isEmpty()) {
            throw new IllegalStateException("所选检查没有完成");
        }

        List<Map<String, Object>> datasList = new ArrayList<>();
        datasList.add(new HashMap<>());

        String deptUid = null;
        String deptName = null;

        TreeMap<String, List<NurseSiteMaintainItem>> map = new TreeMap<>();
        for (NurseSiteMaintain maintain : listMaintain) {
            if (deptUid == null) {
                deptUid = maintain.getClinicalDeptUID();
                deptName = maintain.getClinicalDeptName();
            } else if (!StringUtils.equals(deptUid, maintain.getClinicalDeptUID())) {
                throw new IllegalStateException("所属科室不唯一");
            }

            Date scheduleDate = maintain.getScheduleDate();
            if (scheduleDate == null) {
                continue;
            }
            String dateStr = DateFormatUtils.format(scheduleDate, "yyyy-MM-dd");
            String signatureId = maintain.getSignatureId();
            int signatureType = maintain.getSignatureSourceType();
            List<NurseSiteMaintainItem> items = maintain.getListNurseSiteMaintainItem();
            if (StringUtils.isNotBlank(signatureId)) {
                items.forEach(i -> {
                    if (StringUtils.isBlank(i.getSignatureId())) {
                        i.setSignatureId(signatureId);
                        i.setSignatureSourceType(signatureType);
                    }
                });
            }
            if (map.containsKey(dateStr)) {
                items.addAll(map.get(dateStr));
            }
            map.put(dateStr, items);
        }

        for (Map.Entry<String, List<NurseSiteMaintainItem>> i : map.entrySet()) {
            Map<String, Object> datas = datasList.get(datasList.size() - 1);
            datas.put("dept", deptName);
            TreeMap<String, List<NurseSiteMaintainItem>> subMap = (TreeMap<String, List<NurseSiteMaintainItem>>) datas.computeIfAbsent("tables", k -> new TreeMap<>());
            subMap.put(i.getKey(), i.getValue());

            //date+i first rowa
            datas.put("date" + subMap.size(), i.getKey());

            //asset pass area
            if (subMap.size() == 10) {
                datasList.add(new HashMap<>());
            }
        }

        List<byte[]> reports = new ArrayList<>();
        for (Map<String, Object> datas : datasList) {
            reports.add(export(templateBytes, datas));
        }

        return zip(reports);
    }

    public byte[] zip(List<byte[]> reports) throws IOException {
        try (OutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(out)) {
            for (int i = 0; i < reports.size(); i++) {
                zos.putNextEntry(new ZipEntry("report" + (i + 1) + ".docx"));
                IOUtils.write(reports.get(i), zos);
            }
            zos.closeEntry();
            zos.close();
            return ((ByteArrayOutputStream) out).toByteArray();
        }
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

            Map<String, AssetInfo> assetInfos = getIdAssetInfoMap(itemMap);
            Map<String, String> checkItems = getCheckItemsMap(itemMap);

            List<String> columnHeaders = getColumnHeaders(table);
            List<String> rowHeaders = new ArrayList<>();
            rowHeaders.add("place_holder_for_first_row");

            rowHeaders.addAll(assetInfos.keySet().stream().collect(Collectors.toList()));
            populateAssetInfoColumns(table, assetInfos.keySet().stream().map(i -> assetInfos.get(i)).collect(Collectors.toList()));
            populateCheckPass(table, columnHeaders, itemMap, rowHeaders);

            populateEmptyRows(table, rowHeaders);

            rowHeaders.addAll(checkItems.keySet().stream().collect(Collectors.toList()));
            populateCheckItemColumns(table, checkItems.keySet().stream().map(i -> checkItems.get(i)).collect(Collectors.toList()));
            populateSignatures(table, columnHeaders, itemMap, rowHeaders);

            table.removeRow(table.getNumberOfRows() - 1);

        }

        public Map<String, String> getCheckItemsMap(Map<String, List<NurseSiteMaintainItem>> itemMap) {
            Map<String, String> checkItems = new TreeMap<>(Comparator.naturalOrder());
            itemMap.values().forEach(i -> i.forEach(j -> {
                j.getListData().forEach(k -> checkItems.put(k.getCheckItem(), k.getCheckItem()));
            }));
            return checkItems;
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

        public Map<String, AssetInfo> getIdAssetInfoMap(Map<String, List<NurseSiteMaintainItem>> itemMap) {
            Map<String, AssetInfo> assetInfos = new TreeMap<>();
            itemMap.values().forEach(i -> i.forEach(j -> {
                assetInfos.put(j.getAssetInfo().getUid(), j.getAssetInfo());
            }));
            return assetInfos;
        }

        public void populateCheckPass(XWPFTable table, List<String> columnHeaders, Map<String, List<NurseSiteMaintainItem>> itemMap, List<String> rowHeaders) {
            itemMap.entrySet().forEach(i -> {
                int columnIndex = columnHeaders.indexOf(i.getKey());
                List<NurseSiteMaintainItem> items = i.getValue();
                for (int j = 0; j < items.size(); j++) {
                    NurseSiteMaintainItem item = items.get(j);
                    Integer status = item.getStatus();
                    String text = null;
                    if (PASS_STATUS.contains(status)) {
                        text = "√";
                    } else if (FAILURE_STATUS.contains(status)) {
                        text = "x";
                    }
                    if (text != null) {
                        String assetUid = item.getAssetInfo().getUid();
                        int rowIndex = rowHeaders.indexOf(assetUid);
                        XWPFTableCell cell = table.getRow(rowIndex).getCell(columnIndex);
                        String originalText = cell.getText();
                        if (StringUtils.isBlank(originalText) || (!StringUtils.equals(originalText, text) && "x".equalsIgnoreCase(text))) {
                            cell.removeParagraph(0);
                            XWPFParagraph xwpfParagraph = cell.addParagraph();
                            XWPFRun run = xwpfParagraph.createRun();
                            run.setText(text);
                        }
                    }
                }
            });
        }

        public void populateAssetInfoColumns(XWPFTable table, Collection<AssetInfo> assetInfos) {
            XWPFTableRow firstRow = table.getRow(0);
            int size = firstRow.getTableCells().size();
            for (AssetInfo assetInfo : assetInfos) {
                XWPFTableRow row = table.insertNewTableRow(table.getNumberOfRows() - 1);
                row.createCell().setText(assetInfo.getName());
                row.createCell().setText(assetInfo.getDepartNum());
                for (int i = 2; i < size; i++) {
                    row.createCell();
                }
            }
        }

        public void populateSignatures(XWPFTable table, List<String> columnHeaders, Map<String, List<NurseSiteMaintainItem>> itemMap, List<String> rowHeaders) {

            int[][] mirror = new int[columnHeaders.size()][rowHeaders.size()];

            for (int i = 0; i < mirror.length; i++) {
                for (int j = 0; j < mirror[0].length; j++) {
                    mirror[i][j] = 0;
                }
            }

            itemMap.entrySet().forEach(i -> {
                int columnIndex = columnHeaders.indexOf(i.getKey());
                List<NurseSiteMaintainItem> items = i.getValue();
                for (int j = 0; j < items.size(); j++) {
                    NurseSiteMaintainItem item = items.get(j);
                    String signatureId = item.getSignatureId();
                    if (StringUtils.isNotBlank(signatureId)) {
                        StreamedContent signature = blobFileService.getBlobObjectByStorageId(signatureId);
                        if (signature != null) {
                            for (NurseSiteMaintainData data : item.getListData()) {
                                int rowIndex = rowHeaders.indexOf(data.getCheckItem());
                                if (mirror[columnIndex][rowIndex] == 0) {
                                    XWPFTableCell cell = table.getRow(rowIndex).getCell(columnIndex);
                                    insertSignature(cell, signature, item.getSignatureSourceType());
                                    mirror[columnIndex][rowIndex] = 1;
                                }
                            }
                        }
                    }
                }
            });
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

        public void populateEmptyRows(XWPFTable table, List<String> rowHeaders) {
            XWPFTableRow firstRow = table.getRow(0);
            int size = firstRow.getTableCells().size();
            for (int i = 0; i < 3; i++) {
                XWPFTableRow row = table.insertNewTableRow(table.getNumberOfRows() - 1);
                for (int j = 0; j < size; j++) {
                    row.createCell();
                }
                rowHeaders.add("place_holder_for_empty");
            }
        }

        public void insertSignature(XWPFTableCell cell, StreamedContent content, Integer signatureType) {
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            if (content != null) {
                try {
                    byte[] bytes = IOUtils.toByteArray(content.getStream());
                    if (bytes.length > 0) {
                        if (signatureType == 0) {
                            run.addPicture(new ByteArrayInputStream(ImageUtils.convertPrimefacesBase64ToByte(new String(bytes))), XWPFDocument.PICTURE_TYPE_PNG, "dummy", Units.toEMU(40), Units.toEMU(40));
                        } else {
                            run.addPicture(new ByteArrayInputStream(bytes), XWPFDocument.PICTURE_TYPE_PNG, "dummy", Units.toEMU(40), Units.toEMU(40));
                        }
                    }
                } catch (InvalidFormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

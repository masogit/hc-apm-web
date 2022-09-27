package com.ge.apm.service.nurseSite;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.service.asset.BlobFileService;
import com.ge.apm.service.utils.ImageUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class WordReportExportService {

    @Autowired
    BlobFileService blobFileService;

    @Autowired
    UserAccountRepository userAccountRepository;

    private final static List<Integer> PASS_STATUS = Arrays.asList(2, 5, 6, 7);
    private final static List<Integer> FAILURE_STATUS = Arrays.asList(3, 8);

    public byte[] writeMaintainDetailToDocx( List<NurseSiteMaintain> listMaintain) throws IOException {

        if (listMaintain.isEmpty()) {
            throw new IllegalStateException("所选检查没有完成");
        }
        List<NurseSiteMaintain> nurseSiteMaintains =null;
        Map<Integer,List<byte[]>>reportMap = null;
        Map<String,Map<Integer,List<byte[]>>> reportMapMap = new LinkedHashMap<>();
        List<byte[]> reports= null;
        Map<String,List<NurseSiteMaintain>>listMap = new LinkedHashMap<>();
        for (NurseSiteMaintain maintain : listMaintain) {
            String name;
            Integer sequence = maintain.getSequence();
            String year = DateFormatUtils.format(maintain.getScheduleDate(), "yyyy");
            if (sequence==0){
                 name = maintain.getName();
            }else {
                 name = maintain.getName()+"-"+sequence;
            }
            name = name  + " " + year;
            if(listMap.containsKey(name)){
                listMap.get(name).add(maintain);
                listMap.put(name,listMap.get(name));
            }else {
                nurseSiteMaintains = new ArrayList<>();
                nurseSiteMaintains.add(maintain);
                listMap.put(name,nurseSiteMaintains);
            }

        }
        String name=null;
        for (Map.Entry<String, List<NurseSiteMaintain>> a : listMap.entrySet()) {
            reportMap = new LinkedHashMap<>();
            name = a.getKey();
            TreeMap<Date, List<NurseSiteMaintainItem>> map = new TreeMap<>();
            Map<Date,Map<String,List<String>>> stringMap = new HashMap<>();
            Map<Date,Map<String,String>> commentMapMap = new HashMap<>();
            Map<Date,Map<String,String>> updateByMapMap = new HashMap<>();
            Map<String,List<String>> assetNameMap = null;
            Map<String,String> commentMap = null;
            Map<String,String> updateByMap = null;
            for (NurseSiteMaintain maintain : a.getValue()) {
                Date scheduleDate = maintain.getScheduleDate();
                if (scheduleDate == null) {
                    continue;
                }
                List<NurseSiteMaintainItem> items = maintain.getListNurseSiteMaintainItem();
                assetNameMap = new HashMap<>();
                commentMap = new HashMap<>();
                updateByMap = new HashMap<>();
                for (int i = 0;i < items.size();i++) {
                    NurseSiteMaintainItem nurseSiteMaintainItem = items.get(i);
                    AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
                    List<NurseSiteMaintainData> listData = nurseSiteMaintainItem.getListData();
                    List<String> stringList = new ArrayList<>();
                    for (int j = 0 ;j < listData.size();j++) {
                        NurseSiteMaintainData nurseSiteMaintainData = listData.get(j);
                        Boolean checkValue = nurseSiteMaintainData.getCheckValue();
                        if(checkValue!=null && checkValue){
                            stringList.add("√");
                        }else if(checkValue!=null && !checkValue){
                            stringList.add("x");
                        }else {
                            stringList.add(null);
                        }
                    }
                    assetNameMap.put(assetInfo.getName(),stringList);
                    commentMap.put(assetInfo.getName(),nurseSiteMaintainItem.getComment());
                    updateByMap.put(assetInfo.getName(),nurseSiteMaintainItem.getUpdateByUid());

                }
                map.put(scheduleDate, items);
                stringMap.put(scheduleDate,assetNameMap);
                commentMapMap.put(scheduleDate,commentMap);
                updateByMapMap.put(scheduleDate,updateByMap);
            }
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList= null;
            int num =0;
            int month=0;
            Map<Integer ,Date> dataMap = new LinkedHashMap<>();
            Map<Integer,Map<Integer ,Date>> stringMapMap= new LinkedHashMap();
            for (Map.Entry<Date, List<NurseSiteMaintainItem>> i : map.entrySet()) {
                int secondCount =0;
                nurseSiteMaintainItemList = i.getValue();
                num++;
                Date date = i.getKey();
                if (num==1){
                    month = date.getMonth()+1;
                    dataMap.put(num,i.getKey());
                    stringMapMap.put(month,dataMap);
                }else {
                    if(month!=date.getMonth()+1){
                        dataMap = new LinkedHashMap<>();
                        secondCount++;
                        month=date.getMonth()+1;
                        num=secondCount;
                        dataMap.put(secondCount,i.getKey());
                        stringMapMap.put(date.getMonth()+1,dataMap);
                    }else {
                        dataMap.put(num,i.getKey());
                        stringMapMap.put(month,dataMap);
                    }
                }
            }
            for (Map.Entry<Integer, Map<Integer, Date>> m : stringMapMap.entrySet()) {
                reports = new ArrayList<>();
                int count = m.getValue().size();
                dataMap = m.getValue();
                XWPFDocument document = new XWPFDocument();
                for (int i=0;i<nurseSiteMaintainItemList.size();i++) {
                    if (i > 0) {
                        //不同设备分页
                        document.createParagraph().setPageBreak(true);
                    }
                        creatPage(i,count,dataMap,document,nurseSiteMaintainItemList,stringMap,commentMapMap,updateByMapMap);
                }
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                document.write(outStream);
                outStream.close();
                byte[] bytes = outStream.toByteArray();
                reports.add(bytes);
                reportMap.put(m.getKey(), reports);
            }
            reportMapMap.put(name,reportMap);
        }

        return zip(reportMapMap);
    }

    public byte[] writeMaintainHistoryDetailToDocx( List<NurseSiteMaintainHistory> listMaintain) throws IOException {

        if (listMaintain.isEmpty()) {
            throw new IllegalStateException("所选检查没有完成");
        }
        List<NurseSiteMaintainHistory> nurseSiteMaintains =null;
        Map<Integer,List<byte[]>>reportMap = null;
        Map<String,Map<Integer,List<byte[]>>> reportMapMap = new LinkedHashMap<>();
        List<byte[]> reports= null;
        Map<String,List<NurseSiteMaintainHistory>>listMap = new LinkedHashMap<>();
        for (NurseSiteMaintainHistory maintain : listMaintain) {
            String name = maintain.getName() + " " + DateFormatUtils.format(maintain.getScheduleDate(), "yyyy");
            if(listMap.containsKey(name)){
                listMap.get(name).add(maintain);
                listMap.put(name,listMap.get(name));
            }else {
                nurseSiteMaintains = new ArrayList<>();
                nurseSiteMaintains.add(maintain);
                listMap.put(name,nurseSiteMaintains);
            }

        }
        String name=null;
        for (Map.Entry<String, List<NurseSiteMaintainHistory>> a : listMap.entrySet()) {
            reportMap = new LinkedHashMap<>();
            name = a.getKey();
            TreeMap<Date, List<NurseSiteMaintainItemHistory>> map = new TreeMap<>();
            Map<Date,Map<String,List<String>>> stringMap = new HashMap<>();
            Map<Date,Map<String,String>> commentMapMap = new HashMap<>();
            Map<Date,Map<String,String>> updateByMapMap = new HashMap<>();
            Map<String,List<String>> assetNameMap = null;
            Map<String,String> commentMap = null;
            Map<String,String> updateByMap = null;
            for (NurseSiteMaintainHistory maintain : a.getValue()) {
                Date scheduleDate = maintain.getScheduleDate();
                if (scheduleDate == null) {
                    continue;
                }
                List<NurseSiteMaintainItemHistory> items = maintain.getListNurseSiteMaintainItem();
                assetNameMap = new HashMap<>();
                commentMap = new HashMap<>();
                updateByMap = new HashMap<>();
                for (int i = 0;i < items.size();i++) {
                    NurseSiteMaintainItemHistory nurseSiteMaintainItem = items.get(i);
                    AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
                    List<NurseSiteMaintainDataHistory> listData = nurseSiteMaintainItem.getListData();
                    List<String> stringList = new ArrayList<>();
                    for (int j = 0 ;j < listData.size();j++) {
                        NurseSiteMaintainDataHistory nurseSiteMaintainData = listData.get(j);
                        Boolean checkValue = nurseSiteMaintainData.getCheckValue();
                        if(checkValue!=null && checkValue){
                            stringList.add("√");
                        }else if(checkValue!=null && !checkValue){
                            stringList.add("x");
                        }else {
                            stringList.add(null);
                        }
                    }
                    assetNameMap.put(assetInfo.getName(),stringList);
                    commentMap.put(assetInfo.getName(),nurseSiteMaintainItem.getComment());
                    updateByMap.put(assetInfo.getName(),nurseSiteMaintainItem.getUpdateByUid());

                }
                map.put(scheduleDate, items);
                stringMap.put(scheduleDate,assetNameMap);
                commentMapMap.put(scheduleDate,commentMap);
                updateByMapMap.put(scheduleDate,updateByMap);
            }
            List<NurseSiteMaintainItemHistory> nurseSiteMaintainItemList= null;
            int num =0;
            int month=0;
            Map<Integer ,Date> dataMap = new LinkedHashMap<>();
            Map<Integer,Map<Integer ,Date>> stringMapMap= new LinkedHashMap();
            for (Map.Entry<Date, List<NurseSiteMaintainItemHistory>> i : map.entrySet()) {
                int secondCount =0;
                nurseSiteMaintainItemList = i.getValue();
                num++;
                Date date = i.getKey();
                if (num==1){
                    month = date.getMonth()+1;
                    dataMap.put(num,i.getKey());
                    stringMapMap.put(month,dataMap);
                }else {
                    if(month!=date.getMonth()+1){
                        dataMap = new LinkedHashMap<>();
                        secondCount++;
                        month=date.getMonth()+1;
                        num=secondCount;
                        dataMap.put(secondCount,i.getKey());
                        stringMapMap.put(date.getMonth()+1,dataMap);
                    }else {
                        dataMap.put(num,i.getKey());
                        stringMapMap.put(month,dataMap);
                    }
                }
            }
            for (Map.Entry<Integer, Map<Integer, Date>> m : stringMapMap.entrySet()) {
                reports = new ArrayList<>();
                int count = m.getValue().size();
                dataMap = m.getValue();
                XWPFDocument document = new XWPFDocument();
                for (int i=0;i<nurseSiteMaintainItemList.size();i++) {
                    if (i > 0) {
                        //不同设备分页
                        document.createParagraph().setPageBreak(true);
                    }
                    creatPageForHistory(i,count,dataMap,document,nurseSiteMaintainItemList,stringMap,commentMapMap,updateByMapMap);
                }
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                document.write(outStream);
                outStream.close();
                byte[] bytes = outStream.toByteArray();
                reports.add(bytes);
                reportMap.put(m.getKey(),reports);
            }
            reportMapMap.put(name,reportMap);
        }

        return zip(reportMapMap);
    }

    public void creatPageForHistory( int i,int size,Map<Integer ,Date> dataMap,XWPFDocument document, List<NurseSiteMaintainItemHistory> nurseSiteMaintainItemList,
                           Map<Date,Map<String,List<String>>>  stringMap,Map<Date,Map<String,String>> commentMapMap,Map<Date,Map<String,String>> updateByMapMap){
        XWPFTable table = document.createTable();
        XWPFTableRow tableOneRowOne = table.getRow(0);
        XWPFTableRow tableOneRowTwo = table.createRow();
        NurseSiteMaintainItemHistory nurseSiteMaintainItem = nurseSiteMaintainItemList.get(i);
        AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
        String assetInfoName = assetInfo.getName();
        tableOneRowOne.getCell(0).setText(assetInfoName +"("+assetInfo.getDepartNum()+")"
                + "-"+ DateFormatUtils.format(dataMap.get(1), "yyyy-MM"));
        tableOneRowTwo.getCell(0).setText("日期");
        List<NurseSiteMaintainDataHistory> listData = nurseSiteMaintainItem.getListData();
        int num = listData.size();
        for (int m = 0; m < num; m++) {
            NurseSiteMaintainDataHistory nurseSiteMaintainData = listData.get(m);
            String checkItem = nurseSiteMaintainData.getCheckItem();
            tableOneRowTwo.addNewTableCell().setText(checkItem);
        }
        tableOneRowTwo.addNewTableCell().setText("检查者");
        tableOneRowTwo.addNewTableCell().setText("备注");
        num = num + 2;
        for (int n = 1; n <= size; n++) {
            XWPFTableRow tableOneRow = table.createRow();
            Date date = dataMap.get(n);
            String dateStr = DateFormatUtils.format(date, "yyyy-MM-dd");
            tableOneRow.getCell(0).setText(dateStr);//时间
            Map<String, List<String>> stringListMap = stringMap.get(date);
            List<String> stringList = stringListMap.get(assetInfoName);
            if (stringList!=null){
                for (int j = 0; j <stringList.size() ; j++) {
                    tableOneRow.addNewTableCell().setText(stringList.get(j));
                }
                for (int m = 1; m <= 2; m++) {
                    if( m==1){
                        Map<String, String> map = updateByMapMap.get(date);
                        String updateByUid = map.get(assetInfoName);
                        if(updateByUid == null){
                            tableOneRow.addNewTableCell().setText(null);
                        }else {
                            UserAccount userAccount = userAccountRepository.getByUid(updateByUid);
                            tableOneRow.addNewTableCell().setText(userAccount.getName());
                        }
                    }else if( m==2){
                        Map<String, String> map = commentMapMap.get(date);
                        String comment = map.get(assetInfoName);
                        tableOneRow.addNewTableCell().setText(comment);
                    }else {
                        tableOneRow.addNewTableCell().setText(null);
                    }
                }
            }else {
                for (int m = 1; m <= num; m++) {
                    tableOneRow.addNewTableCell().setText(null);
                }
            }

        }
        for (int m = 1; m <= num; m++) {
            tableOneRowOne.addNewTableCell().setText(null);
        }
        for (int m = 0; m <= num; m++) {
            List<XWPFTableCell> tableCells = tableOneRowOne.getTableCells();
            if (m == 0)
                tableCells.get(m).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);//这是起点
            else
                tableCells.get(m).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);//继续合并
        }
        //第一行居中
        CTTc ctTc = tableOneRowOne.getCell(0).getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();
        ctTcPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        ctTc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
        //表格宽度
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(new BigInteger("9370"));
    }

    public void creatPage( int i,int size,Map<Integer ,Date> dataMap,XWPFDocument document, List<NurseSiteMaintainItem> nurseSiteMaintainItemList,
                           Map<Date,Map<String,List<String>>>  stringMap,Map<Date,Map<String,String>> commentMapMap,Map<Date,Map<String,String>> updateByMapMap){
        XWPFTable table = document.createTable();
        XWPFTableRow tableOneRowOne = table.getRow(0);
        XWPFTableRow tableOneRowTwo = table.createRow();
        NurseSiteMaintainItem nurseSiteMaintainItem = nurseSiteMaintainItemList.get(i);
        AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
        String assetInfoName = assetInfo.getName();
        tableOneRowOne.getCell(0).setText(assetInfoName +"("+assetInfo.getDepartNum()+")"
                + "-"+ DateFormatUtils.format(dataMap.get(1), "yyyy-MM"));
        tableOneRowTwo.getCell(0).setText("日期");
        List<NurseSiteMaintainData> listData = nurseSiteMaintainItem.getListData();
        int num = listData.size();
        for (int m = 0; m < num; m++) {
            NurseSiteMaintainData nurseSiteMaintainData = listData.get(m);
            String checkItem = nurseSiteMaintainData.getCheckItem();
            tableOneRowTwo.addNewTableCell().setText(checkItem);
        }
        tableOneRowTwo.addNewTableCell().setText("检查者");
        tableOneRowTwo.addNewTableCell().setText("备注");
        num = num + 2;
        for (int n = 1; n <= size; n++) {
            XWPFTableRow tableOneRow = table.createRow();
            Date date = dataMap.get(n);
            String dateStr = DateFormatUtils.format(date, "yyyy-MM-dd");
            tableOneRow.getCell(0).setText(dateStr);//时间
            Map<String, List<String>> stringListMap = stringMap.get(date);
            List<String> stringList = stringListMap.get(assetInfoName);
            if (stringList!=null){
                for (int j = 0; j <stringList.size() ; j++) {
                    tableOneRow.addNewTableCell().setText(stringList.get(j));
                }
                for (int m = 1; m <= 2; m++) {
                    if( m==1){
                        Map<String, String> map = updateByMapMap.get(date);
                        String updateByUid = map.get(assetInfoName);
                        if(updateByUid == null){
                            tableOneRow.addNewTableCell().setText(null);
                        }else {
                            UserAccount userAccount = userAccountRepository.getByUid(updateByUid);
                            tableOneRow.addNewTableCell().setText(userAccount.getName());
                        }
                    }else if( m==2){
                        Map<String, String> map = commentMapMap.get(date);
                        String comment = map.get(assetInfoName);
                        tableOneRow.addNewTableCell().setText(comment);
                    }else {
                        tableOneRow.addNewTableCell().setText(null);
                    }
                }
            }else {
                for (int m = 1; m <= num; m++) {
                    tableOneRow.addNewTableCell().setText(null);
                }
            }

        }
        for (int m = 1; m <= num; m++) {
            tableOneRowOne.addNewTableCell().setText(null);
        }
        for (int m = 0; m <= num; m++) {
            List<XWPFTableCell> tableCells = tableOneRowOne.getTableCells();
            if (m == 0)
                tableCells.get(m).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);//这是起点
            else
                tableCells.get(m).getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);//继续合并
        }
        //第一行居中
        CTTc ctTc = tableOneRowOne.getCell(0).getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();
        ctTcPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        ctTc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
        //表格宽度
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(new BigInteger("9370"));
    }

    public byte[] zip(Map<String,Map<Integer,List<byte[]>>> reportMapMap) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(out)) {
            for (Map.Entry<String, Map<Integer, List<byte[]>>> n : reportMapMap.entrySet()) {
                for (Map.Entry<Integer, List<byte[]>> m : n.getValue().entrySet()) {
                    List<byte[]> reports = m.getValue();
                    Integer key = m.getKey();
                    for (int i = 0; i < reports.size(); i++) {
                        zos.putNextEntry(new ZipEntry(n.getKey()+"-"+key+"月" +".docx"));
                        IOUtils.write(reports.get(i), zos);
                    }
                }
            }
            zos.closeEntry();
            zos.close();
            return out.toByteArray();
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

            int[][] mirror = new int[rowHeaders.size()][columnHeaders.size()];

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
                        byte[] bytes = getSignature(signatureId);
                        if (bytes != null && bytes.length > 0) {
                            for (NurseSiteMaintainData data : item.getListData()) {
                                String checkItem = data.getCheckItem();
                                int rowIndex = rowHeaders.indexOf(checkItem);
                                if (mirror[rowIndex][columnIndex] == 0) {
                                    XWPFTableCell cell = table.getRow(rowIndex).getCell(columnIndex);
                                    insertSignature(cell, bytes, item.getSignatureSourceType());
                                    mirror[rowIndex][columnIndex] = 1;
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

        public void insertSignature(XWPFTableCell cell, byte[] bytes, Integer signatureType) {
            cell.removeParagraph(0);
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            try {
                if (signatureType == 0) {
                    run.addPicture(new ByteArrayInputStream(ImageUtils.convertPrimefacesBase64ToByte(new String(bytes))), XWPFDocument.PICTURE_TYPE_PNG, "dummy", Units.toEMU(40), Units.toEMU(40));
                } else {
                    run.addPicture(new ByteArrayInputStream(bytes), XWPFDocument.PICTURE_TYPE_PNG, "dummy", Units.toEMU(40), Units.toEMU(40));
                }
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getSignature(String signatureId) {
        StreamedContent content = blobFileService.getBlobObjectByStorageId(signatureId);
        try {
            return IOUtils.toByteArray(content.getStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.ge.apm.service.nurseSite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.deepoove.poi.plugin.table.LoopColumnTableRenderPolicy;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.RenderPolicy;
import com.ge.apm.dao.NurseSiteMaintainConfigRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.*;
import com.ge.apm.domain.pure.TableOption;
import com.ge.apm.service.WordExportServiceUtil;
import com.ge.apm.service.asset.*;
import com.ge.apm.service.systemTempate.SystemTemplateService;
import com.ge.apm.service.utils.ImageUtils;
import com.ge.apm.view.sysutil.UserContextService;
import com.ge.healthcare.cn.common.cons.SystemTemplateEnum;
import com.ge.healthcare.cn.common.utils.collection.ListUtil;
import com.ge.healthcare.cn.common.utils.date.DateUtil;
import com.ge.healthcare.cn.common.utils.map.MapUtil;
import com.ge.healthcare.cn.common.utils.util.ObjectUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class WordReportExportService {

    public static  final Logger logger = LoggerFactory.getLogger(WordReportExportService.class);

    @Autowired
    private BlobFileService blobFileService;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private NurseSiteMaintainConfigRepository nurseSiteMaintainConfigRepository;

    @Autowired
    private SystemTemplateService systemTemplateService;

    private final static List<Integer> PASS_STATUS = Arrays.asList(2, 5, 6, 7);
    private final static List<Integer> FAILURE_STATUS = Arrays.asList(3, 8);

    public byte[] writeMaintainDetailToDocx( List<NurseSiteMaintain> listMaintain) throws IOException {

        if (listMaintain.isEmpty()) {
            throw new IllegalStateException("所选检查没有完成");
        }
        List<NurseSiteMaintainConfig> configs = nurseSiteMaintainConfigRepository.fetchListByIds(listMaintain.stream().map(NurseSiteMaintain::getConfigId).collect(Collectors.toSet()));
        Map<String, String> configMap = configs.stream().collect(Collectors.toMap(NurseSiteMaintainConfig::getId, NurseSiteMaintainConfig::getCheckItemsDetail));
        List<NurseSiteMaintain> nurseSiteMaintains;
        Map<Integer,List<byte[]>>reportMap;
        Map<String,Map<Integer,List<byte[]>>> reportMapMap = new LinkedHashMap<>();
        List<byte[]> reports;
        Map<String,List<NurseSiteMaintain>>listMap = new LinkedHashMap<>();
        Map<String, Integer> nameSuffixMap = MapUtil.newHashMap();
        Map<String, Map<String, String>> nameKeyMap = MapUtil.newHashMap();
        for (NurseSiteMaintain maintain : listMaintain) {
            String name;
            Integer sequence = maintain.getSequence();
            String year = DateFormatUtils.format(maintain.getScheduleDate(), "yyyy");
            String month = DateFormatUtils.format(maintain.getScheduleDate(), "MM");
            if (sequence==0){
                 name = maintain.getName();
            }else {
                 name = maintain.getName() + "-" + sequence;
            }
            name += " " + year + "-" + month + "月";
            String nameKey = name + "#" + maintain.getConfigId();
            if (listMap.containsKey(name)) {
                Map<String, String> nameKeys = nameKeyMap.get(name);
                if (nameKeys.containsKey(nameKey)) {
                    listMap.get(nameKeys.get(nameKey)).add(maintain);
                } else {
                    nurseSiteMaintains = ListUtil.newArrayList(maintain);
                    Integer suffix = nameSuffixMap.get(name);
                    ++suffix;
                    name += "(" + suffix + ")";
                    nameKeys.put(nameKey, name);
                    nameSuffixMap.put(name, suffix);
                    listMap.put(name, nurseSiteMaintains);
                }
            } else {
                nurseSiteMaintains = new ArrayList<>();
                nurseSiteMaintains.add(maintain);
                listMap.put(name, nurseSiteMaintains);
                nameKeyMap.put(name, MapUtil.newHashMap(nameKey, name));
                nameSuffixMap.put(name, 0);
            }
        }
        for (Map.Entry<String, List<NurseSiteMaintain>> a : listMap.entrySet()) {
            String name = a.getKey();
            reportMap = new LinkedHashMap<>();
            TreeMap<Date, List<NurseSiteMaintainItem>> map = new TreeMap<>();
            Map<Date,Map<String,Map<String, String>>> stringMap = new HashMap<>();
            Map<Date,Map<String,String>> commentMapMap = new HashMap<>();
            Map<Date,Map<String,String>> updateByMapMap = new HashMap<>();
            for (NurseSiteMaintain maintain : a.getValue()) {
                Date scheduleDate = maintain.getScheduleDate();
                if (scheduleDate == null) {
                    continue;
                }
                List<NurseSiteMaintainItem> items = maintain.getListNurseSiteMaintainItem();
                Map<String,Map<String, String>> assetUidMap = new HashMap<>();
                Map<String,String> commentMap = new HashMap<>();
                Map<String,String> updateByMap = new HashMap<>();
                for (NurseSiteMaintainItem nurseSiteMaintainItem : items) {
                    Map<String, List<NurseSiteMaintainCheckItem>> checkItemMap = Optional.ofNullable(configMap.get(nurseSiteMaintainItem.getConfigId())).map(
                            checkItemsDetail -> {
                                if(StringUtils.isBlank(checkItemsDetail)){
                                    return new  HashMap<String, List<NurseSiteMaintainCheckItem>>();
                                }
                                List<NurseSiteMaintainCheckItem> nurseSiteMaintainCheckItems = JSONObject.parseObject(checkItemsDetail, new TypeReference<List<NurseSiteMaintainCheckItem>>() {});
                                return nurseSiteMaintainCheckItems.stream().filter(i->i.getName()!=null || i.getKey()!=null).collect(Collectors.groupingBy(item->item.getKey()!=null?item.getKey().toString():item.getName()));
                            }
                    ).orElse(new HashMap<>());
                    AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
                    List<NurseSiteMaintainData> listData = nurseSiteMaintainItem.getListData();
                    Map<String, String> itemMap = new HashMap<>();
                    for (NurseSiteMaintainData nurseSiteMaintainData : listData) {

                        List<NurseSiteMaintainCheckItem> nurseSiteMaintainCheckItems1 = checkItemMap.get(nurseSiteMaintainData.getIndex()!=null?nurseSiteMaintainData.getIndex().toString():nurseSiteMaintainData.getCheckItem());
                        NurseSiteMaintainCheckItem nurseSiteMaintainCheckItem = Optional.ofNullable(nurseSiteMaintainCheckItems1)
                                .map(list -> list.isEmpty() ? null : list.get(0))
                                .orElse(new NurseSiteMaintainCheckItem());
                        String description="";
                        if(nurseSiteMaintainData.getVariableText()!=null  && !"false".equals(nurseSiteMaintainData.getVariableText()) && !"true".equals(nurseSiteMaintainData.getVariableText()) && nurseSiteMaintainCheckItem.getFieldType()!=null){
                            switch (nurseSiteMaintainCheckItem.getFieldType()){
                                case "Select":
                                    String options = nurseSiteMaintainCheckItem.getOptions();
                                    JSONArray objects = JSONArray.parseArray(options);
                                    List<TableOption> tableOptions = objects.toJavaList(TableOption.class);
                                    Optional<TableOption> first = tableOptions.stream().filter(item -> item.getKey().equals(nurseSiteMaintainData.getVariableText())).findFirst();
                                    if(first.isPresent()){
                                        description=" "+first.get().getValue();
                                    }
                                    break;
                                case "Date":
                                    try {
                                        Instant instant = Instant.ofEpochMilli(Long.parseLong(nurseSiteMaintainData.getVariableText()));
                                        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                        description = " "+localDateTime.format(formatter);

                                    } catch (Exception e) {
                                        logger.error("日期转换异常 tempData:{},nurseSiteMaintainCheckItem:{}", JSON.toJSONString(nurseSiteMaintainData),JSON.toJSONString(nurseSiteMaintainCheckItem));
                                    }
                                    break;
                                case "Switch":
                                    break;
                                default:
                                    description=" "+nurseSiteMaintainData.getVariableText();
                                    break;
                            }
                        }

                        Boolean checkValue = nurseSiteMaintainData.getCheckValue();
                        if (Boolean.TRUE.equals(checkValue)) {
                            itemMap.put(nurseSiteMaintainData.getCheckItem(), "√"+description);
                        } else if (Boolean.FALSE.equals(checkValue)) {
                            itemMap.put(nurseSiteMaintainData.getCheckItem(), "x"+description);
                        } else {
                            itemMap.put(nurseSiteMaintainData.getCheckItem(), null);
                        }
                    }
                    assetUidMap.put(assetInfo.getUid(), itemMap);
                    commentMap.put(assetInfo.getUid(), nurseSiteMaintainItem.getComment());
                    updateByMap.put(assetInfo.getUid(), nurseSiteMaintainItem.getUpdateByUid());

                }
                if (map.containsKey(scheduleDate)) {
                    map.get(scheduleDate).addAll(items);
                    stringMap.get(scheduleDate).putAll(assetUidMap);
                    commentMapMap.get(scheduleDate).putAll(commentMap);
                    updateByMapMap.get(scheduleDate).putAll(updateByMap);
                } else {
                    map.put(scheduleDate, items);
                    stringMap.put(scheduleDate,assetUidMap);
                    commentMapMap.put(scheduleDate,commentMap);
                    updateByMapMap.put(scheduleDate,updateByMap);
                }
            }
            List<NurseSiteMaintainItem> nurseSiteMaintainItemList= null;
            int num =0;
            int month=0;
            Map<Integer ,Date> dataMap = new LinkedHashMap<>();
            Map<Integer,Map<Integer ,Date>> stringMapMap= new LinkedHashMap<>();
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
//                int count = m.getValue().size();
                dataMap = m.getValue();
//                XWPFDocument document = new XWPFDocument();
//                for (int i=0;i<nurseSiteMaintainItemList.size();i++) {
//                    if (i > 0) {
//                        //不同设备分页
//                        document.createParagraph().setPageBreak(true);
//                    }
//                        creatPage(i,count,dataMap,document,nurseSiteMaintainItemList,stringMap,commentMapMap,updateByMapMap, configMap);
//                }
                List<String> items = new ArrayList<>();
                Map<String, Object> paramMap = checkDataBuildMap(dataMap, nurseSiteMaintainItemList, stringMap, commentMapMap, updateByMapMap, configMap,items);
                UserAccount user = UserContextService.getCurrentUserAccount();
                byte[] byteArray = systemTemplateService.getTemplateFileByte(SystemTemplateEnum.DAILY_CHECK, user);
                Map<String, RenderPolicy> bindings = new HashMap<>();
                bindings.put("checkItems", new LoopColumnTableRenderPolicy());
                bindings.put("checkResult",new CheckConvertRenderPolicy(items));
                bindings.put("pageBreak", new PageBreakPluginFactory(nurseSiteMaintainItemList.size(),"{{pageBreak}}"));
                byte[] bytes = WordExportServiceUtil.generate(paramMap, byteArray, bindings);
//
//                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//                document.write(outStream);
//                outStream.close();
//                byte[] bytes = outStream.toByteArray();
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
        List<NurseSiteMaintainHistory> nurseSiteMaintains;
        Map<Integer,List<byte[]>>reportMap;
        Map<String,Map<Integer,List<byte[]>>> reportMapMap = new LinkedHashMap<>();
        List<byte[]> reports;
        Map<String,List<NurseSiteMaintainHistory>>listMap = new LinkedHashMap<>();
        Map<String, Integer> nameSuffixMap = MapUtil.newHashMap();
        Map<String, Map<String, String>> nameKeyMap = MapUtil.newHashMap();
        for (NurseSiteMaintainHistory maintain : listMaintain) {
            String year = DateFormatUtils.format(maintain.getScheduleDate(), "yyyy");
            String month = DateFormatUtils.format(maintain.getScheduleDate(), "MM");
            String name = maintain.getName() + " " + year + "-" + month + "月";
            String nameKey = name + "#" + maintain.getConfigId();
            if (listMap.containsKey(name)) {
                Map<String, String> nameKeys = nameKeyMap.get(name);
                if (nameKeys.containsKey(nameKey)) {
                    listMap.get(nameKeys.get(nameKey)).add(maintain);
                } else {
                    nurseSiteMaintains = ListUtil.newArrayList(maintain);
                    Integer suffix = nameSuffixMap.get(name);
                    ++suffix;
                    name += "(" + suffix + ")";
                    nameKeys.put(nameKey, name);
                    nameSuffixMap.put(name, suffix);
                    listMap.put(name, nurseSiteMaintains);
                }
            } else {
                nurseSiteMaintains = new ArrayList<>();
                nurseSiteMaintains.add(maintain);
                listMap.put(name, nurseSiteMaintains);
                nameKeyMap.put(name, MapUtil.newHashMap(nameKey, name));
                nameSuffixMap.put(name, 0);
            }
        }

        for (Map.Entry<String, List<NurseSiteMaintainHistory>> a : listMap.entrySet()) {
            String name = a.getKey();
            reportMap = new LinkedHashMap<>();
            TreeMap<Date, List<NurseSiteMaintainItemHistory>> map = new TreeMap<>();
            Map<Date,Map<String,List<String>>> stringMap = new HashMap<>();
            Map<Date,Map<String,String>> commentMapMap = new HashMap<>();
            Map<Date,Map<String,String>> updateByMapMap = new HashMap<>();
            for (NurseSiteMaintainHistory maintain : a.getValue()) {
                Date scheduleDate = maintain.getScheduleDate();
                if (scheduleDate == null) {
                    continue;
                }
                List<NurseSiteMaintainItemHistory> items = maintain.getListNurseSiteMaintainItem();
                Map<String,List<String>> assetNameMap = new HashMap<>();
                Map<String,String> commentMap = new HashMap<>();
                Map<String,String> updateByMap = new HashMap<>();
                for (NurseSiteMaintainItemHistory nurseSiteMaintainItem : items) {
                    AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
                    List<NurseSiteMaintainDataHistory> listData = nurseSiteMaintainItem.getListData();
                    List<String> stringList = new ArrayList<>();
                    for (NurseSiteMaintainDataHistory nurseSiteMaintainData : listData) {
                        Boolean checkValue = nurseSiteMaintainData.getCheckValue();
                        if (Boolean.TRUE.equals(checkValue)) {
                            stringList.add("√");
                        } else if (Boolean.FALSE.equals(checkValue)) {
                            stringList.add("x");
                        } else {
                            stringList.add(null);
                        }
                    }
                    assetNameMap.put(assetInfo.getName(), stringList);
                    commentMap.put(assetInfo.getName(), nurseSiteMaintainItem.getComment());
                    updateByMap.put(assetInfo.getName(), nurseSiteMaintainItem.getUpdateByUid());

                }
                if (map.containsKey(scheduleDate)) {
                    map.get(scheduleDate).addAll(items);
                    stringMap.get(scheduleDate).putAll(assetNameMap);
                    commentMapMap.get(scheduleDate).putAll(commentMap);
                    updateByMapMap.get(scheduleDate).putAll(updateByMap);
                } else {
                    map.put(scheduleDate, items);
                    stringMap.put(scheduleDate,assetNameMap);
                    commentMapMap.put(scheduleDate,commentMap);
                    updateByMapMap.put(scheduleDate,updateByMap);
                }
            }
            List<NurseSiteMaintainItemHistory> nurseSiteMaintainItemList= null;
            int num =0;
            int month=0;
            Map<Integer ,Date> dataMap = new LinkedHashMap<>();
            Map<Integer,Map<Integer ,Date>> stringMapMap= new LinkedHashMap<>();
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
                           Map<Date,Map<String,Map<String, String>>> stringMap,Map<Date,Map<String,String>> commentMapMap,Map<Date,Map<String,String>> updateByMapMap, Map<String, String> configMap){
        // 获取或创建页面设置的CTSectPr对象
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageMar pageMar = sectPr.addNewPgMar();
        // 设置上、下、左、右的页边距，单位是毫米（mm）
        pageMar.setTop(BigInteger.valueOf(500));
        pageMar.setBottom(BigInteger.valueOf(500));
        pageMar.setLeft(BigInteger.valueOf(500));
        pageMar.setRight(BigInteger.valueOf(500));

        XWPFTable table = document.createTable();
        XWPFTableRow tableOneRowOne = table.getRow(0);
        XWPFTableRow tableOneRowTwo = table.createRow();
        NurseSiteMaintainItem nurseSiteMaintainItem = nurseSiteMaintainItemList.get(i);
        AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
        String assetUid = assetInfo.getUid();
        tableOneRowOne.getCell(0).setText(assetInfo.getName() +"("+assetInfo.getDepartNum()+")"
                + "-"+ DateFormatUtils.format(dataMap.get(1), "yyyy-MM"));
//        tableOneRowTwo.getCell(0).setText("日期");
        XWPFTableCell dateCell = tableOneRowTwo.getCell(0);
        dateCell.setText("日期");
        CTTblWidth dateWidth = dateCell.getCTTc().addNewTcPr().addNewTcW();
//        dateWidth.setW(BigInteger.valueOf(600));
        dateWidth.setType(STTblWidth.DXA);
        String itemDetail = configMap.get(nurseSiteMaintainItem.getConfigId());
        JSONArray itemDetails = JSON.parseArray(itemDetail);
        int num = itemDetails.size();
        for (int m = 0; m < num; m++) {
            JSONObject jsonObject = (JSONObject) itemDetails.get(m);
            tableOneRowTwo.addNewTableCell().setText(jsonObject.getString("name"));
        }
//        tableOneRowTwo.addNewTableCell().setText("检查者");
        XWPFTableCell userCell = tableOneRowTwo.addNewTableCell();
        userCell.setText("检查者");
        CTTblWidth userWidth = userCell.getCTTc().addNewTcPr().addNewTcW();
//        userWidth.setW(BigInteger.valueOf(1000));
        userWidth.setType(STTblWidth.DXA);
//        tableOneRowTwo.addNewTableCell().setText("备注");
        XWPFTableCell remarkCell = tableOneRowTwo.addNewTableCell();
        remarkCell.setText("备注");
        CTTblWidth remarkWidth = remarkCell.getCTTc().addNewTcPr().addNewTcW();
//        remarkWidth.setW(BigInteger.valueOf(1500));
        remarkWidth.setType(STTblWidth.DXA);
        num = num + 2;
        for (int n = 1; n <= size; n++) {
            XWPFTableRow tableOneRow = table.createRow();
            Date date = dataMap.get(n);
            String dateStr = DateFormatUtils.format(date, "MMdd");
//            tableOneRow.getCell(0).setText(dateStr);
            XWPFTableCell dateTempCell = tableOneRow.getCell(0);
            dateTempCell.setText(dateStr);//时间
            CTTblWidth dateTempWidth = dateTempCell.getCTTc().addNewTcPr().addNewTcW();
//            dateTempWidth.setW(BigInteger.valueOf(600));
            dateTempWidth.setType(STTblWidth.DXA);
            Map<String, Map<String, String>> assetUidMap = stringMap.get(date);
            Map<String, String> itemMap = assetUidMap.get(assetUid);
            for (Object detail : itemDetails) {
                String valText = "";
                if (ObjectUtil.isNotEmpty(itemMap)) {
                    valText = itemMap.get(JSON.parseObject(JSON.toJSONString(detail)).getString("name"));
                }
                tableOneRow.addNewTableCell().setText(valText);
            }
            for (int m = 1; m <= 2; m++) {
                if (m==1) {
                    Map<String, String> map = updateByMapMap.get(date);
                    String updateByUid = map.get(assetUid);
                    XWPFTableCell userTempCell = tableOneRow.addNewTableCell();
                    if(updateByUid == null){
                        userTempCell.setText(null);
                    }else {
                        UserAccount userAccount = userAccountRepository.getByUid(updateByUid);
                        userTempCell.setText(userAccount.getName());
                    }
//                    CTTblWidth userTempWidth = userTempCell.getCTTc().addNewTcPr().addNewTcW();
//                    userTempWidth.setW(BigInteger.valueOf(1000));
//                    userTempWidth.setType(STTblWidth.DXA);
                } else {
                    Map<String, String> map = commentMapMap.get(date);
                    String comment = map.get(assetUid);
                    tableOneRow.addNewTableCell().setText(comment);
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
        XWPFTableCell oneRowCell = tableOneRowOne.getCell(0);
        CTTc ctTc = oneRowCell.getCTTc();
        CTTcPr ctTcPr = ctTc.addNewTcPr();
        ctTcPr.addNewVAlign().setVal(STVerticalJc.CENTER);
        ctTc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
        CTTblWidth oneRowWidth = oneRowCell.getCTTc().addNewTcPr().addNewTcW();
        oneRowWidth.setW(BigInteger.valueOf(11250));
        oneRowWidth.setType(STTblWidth.DXA);
        //表格宽度
        CTTblPr tblPr = table.getCTTbl().getTblPr();
        tblPr.getTblW().setType(STTblWidth.DXA);
        tblPr.getTblW().setW(new BigInteger("11250"));
    }

    private Map<String, Object> checkDataBuildMap(Map<Integer, Date> dataMap , List<NurseSiteMaintainItem> nurseSiteMaintainItemList, Map<Date, Map<String, Map<String, String>>> stringMap, Map<Date, Map<String, String>> commentMapMap, Map<Date, Map<String, String>> updateByMapMap, Map<String, String> configMap, List<String> items) {


        Map<String, Object> sectionsMap = new HashMap<>();

        List<Object> sections = new ArrayList<>();
        List<String> objects1 = new ArrayList<>();
        for (NurseSiteMaintainItem nurseSiteMaintainItem : nurseSiteMaintainItemList) {
            Map<String, Object> checkItemMap = new HashMap<>();
            String itemDetail = configMap.get(nurseSiteMaintainItem.getConfigId());
            JSONArray itemDetails = JSON.parseArray(itemDetail);
            int num = itemDetails.size();
            List<String> checkItems = itemDetails.stream().map(obj -> ((JSONObject) obj).getString("name")).collect(Collectors.toList());
//            checkItems.add(0,"日期");
//            checkItems.add("检查者");
//            checkItems.add("备注");
            List<Object> itemTitleList = new ArrayList<>();
            for (String checkItem : checkItems) {
                Map<String, String> itemTitleMap = new HashMap<>();
                itemTitleMap.put("checkItem",checkItem);
                itemTitleList.add(itemTitleMap);
            }

            objects1 = new ArrayList<>();
            objects1.add("日期");
            objects1.addAll(checkItems);
            objects1.add("检查者");
            objects1.add("备注");
            checkItemMap.put("checkItems",itemTitleList);

            AssetInfo assetInfo = nurseSiteMaintainItem.getAssetInfo();
            String assetUid = assetInfo.getUid();
            String functionType = ObjectUtil.isNotEmpty(assetInfo.getFunctionType()) ? "-" + assetInfo.getFunctionType() : "";
            String title = assetInfo.getName() + functionType + "(" + assetInfo.getDepartNum() + ")" + "-" + DateFormatUtils.format(dataMap.get(1), "yyyy-MM");
            checkItemMap.put("checkTitle",title);

            List<Object> objects = new ArrayList<>();
            for (int n = 1; n <= dataMap.size(); n++) {
                Map<String, Object> paramMap = new HashMap<>();

                Date date = dataMap.get(n);
                String dateStr = DateFormatUtils.format(date, "MMdd");
                paramMap.put("日期",dateStr);
                Map<String, Map<String, String>> assetUidMap = stringMap.get(date);
                Map<String, String> itemMap = assetUidMap.get(assetUid);
                if(itemMap!=null){
                    paramMap.putAll(itemMap);
                }
                for (int m = 1; m <= 2; m++) {
                    if (m==1) {
                        Map<String, String> map = updateByMapMap.get(date);
                        String updateByUid = map.get(assetUid);
                        if(updateByUid != null){
                            UserAccount userAccount = userAccountRepository.getByUid(updateByUid);
                            paramMap.put("检查者",userAccount.getName());
                        }
                    } else {
                        Map<String, String> map = commentMapMap.get(date);
                        String comment = map.get(assetUid);
                        paramMap.put("备注",comment);
                    }
                }
                objects.add(paramMap);
            }
            checkItemMap.put("checkResult",objects);
            sections.add(checkItemMap);
        }
        sectionsMap.put("sections",sections);
        String dataStr = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        sectionsMap.put("exportDate","导出时间:"+dataStr);
        items.addAll(objects1);
        return sectionsMap;
    }



    public byte[] zip(Map<String,Map<Integer,List<byte[]>>> reportMapMap) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(out)) {
            for (Map.Entry<String, Map<Integer, List<byte[]>>> n : reportMapMap.entrySet()) {
                for (Map.Entry<Integer, List<byte[]>> m : n.getValue().entrySet()) {
                    List<byte[]> reports = m.getValue();
                    Integer key = m.getKey();
                    for (int i = 0; i < reports.size(); i++) {
                        zos.putNextEntry(new ZipEntry(n.getKey() + ".docx"));
                        IOUtils.write(reports.get(i), zos);
                    }
                }
            }
            zos.closeEntry();
            zos.close();
            return out.toByteArray();
        }
    }

    public byte[] docxZip(Map<String, byte[]> maps) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(out)) {
            maps.forEach((key, val) -> {
                try {
                    zos.putNextEntry(new ZipEntry(key));
                    IOUtils.write(val, zos);
                } catch (IOException e) {
                    return;
                }
            });
            zos.closeEntry();
            zos.close();
            return out.toByteArray();
        }
    }

    public byte[] export(byte[] templateBytes, Map<String, Object> datas) {
        Map<String, RenderPolicy> bindings = new HashMap<>();
        bindings.put("tables", new AssetCheckTableRenderPolicy());
        return WordExportServiceUtil.generate(datas, templateBytes, bindings);
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

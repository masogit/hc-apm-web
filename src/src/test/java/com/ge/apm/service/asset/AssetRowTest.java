package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.Assert.*;

public class AssetRowTest {
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static List<I18nMessage> list = new ArrayList<>();
    private static final Date today = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());


    @BeforeClass
    public static void setupClass() {

        I18nMessage message1 = new I18nMessage();
        message1.setValueZh("Option1");
        message1.setMsgKey("1");
        list.add(message1);

        I18nMessage message2 = new I18nMessage();
        message2.setValueZh("Option2");
        message2.setMsgKey("2");
        list.add(message2);
    }

    @Test
    public void testSetFieldValue_IsNew() {

        AssetInfo assetInfo = new AssetInfo();

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "CT");
        rowMap.put("出厂日期", "2017-12-12");
        rowMap.put("医疗器械分类", "Option1");
        rowMap.put("最终残值", "1.6");
        rowMap.put("功能等级", "1");

        AssetRow assetRow = new AssetRow(assetInfo, true, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");

        assertEquals("CT", assetInfo.getAliasName());
        assertEquals("2017-12-12", df.format(assetInfo.getManufactDate()));
        assertEquals(1, assetInfo.getFunctionGroup().intValue());
        assertEquals(1.6, assetInfo.getSalvageValue().doubleValue(), 0.1);
        assertEquals(1, assetInfo.getFunctionGrade().intValue());

    }

    @Test
    public void testSetFieldValue_IsOld_Clear_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAliasName("CT");
        assetInfo.setManufactDate(today);
        assetInfo.setFunctionGroup(1);
        assetInfo.setSalvageValue(1.6);
        assetInfo.setFunctionGrade(1);

        assertNotNull(assetInfo.getAliasName());
        assertNotNull(assetInfo.getManufactDate());
        assertNotNull(assetInfo.getFunctionGroup());
        assertNotNull(assetInfo.getSalvageValue());
        assertNotNull(assetInfo.getFunctionGrade());

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "#");
        rowMap.put("出厂日期", "#");
        rowMap.put("医疗器械分类", "#");
        rowMap.put("最终残值", "#");
        rowMap.put("功能等级", "#");

        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");

        assertNull(assetInfo.getAliasName());
        assertNull(assetInfo.getManufactDate());
        assertNull(assetInfo.getFunctionGroup());
        assertNull(assetInfo.getSalvageValue());
        assertNull(assetInfo.getFunctionGrade());
    }

    @Test
    public void testSetFieldValue_IsOld_Update_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAliasName("CT");
        assetInfo.setManufactDate(today);
        assetInfo.setFunctionGroup(1);
        assetInfo.setSalvageValue(1.6);
        assetInfo.setFunctionGrade(1);

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "New CT");
        rowMap.put("出厂日期", "2017-12-01");
        rowMap.put("医疗器械分类", "Option2");
        rowMap.put("最终残值", "10.6");
        rowMap.put("功能等级", "2");

        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");

        assertEquals("New CT", assetInfo.getAliasName());
        assertEquals("2017-12-01", df.format(assetInfo.getManufactDate()));
        assertEquals(2, assetInfo.getFunctionGroup().intValue());
        assertEquals(10.6, assetInfo.getSalvageValue().doubleValue(), 0.1);
        assertEquals(2, assetInfo.getFunctionGrade().intValue());

    }

    @Test
    public void testSetFieldValue_IsOld_Keep_Old_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAliasName("CT");
        assetInfo.setManufactDate(today);
        assetInfo.setFunctionGroup(1);
        assetInfo.setSalvageValue(1.6);
        assetInfo.setFunctionGrade(1);

        Map<String, Object> rowMap = new HashMap<>();
//        rowMap.put("设备别名", "");

        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");

        assertEquals("CT", assetInfo.getAliasName());
        assertEquals(today, assetInfo.getManufactDate());
        assertEquals(1, assetInfo.getFunctionGroup().intValue());
        assertEquals(1.6, assetInfo.getSalvageValue().doubleValue(), 0.1);
        assertEquals(1, assetInfo.getFunctionGrade().intValue());

    }

}

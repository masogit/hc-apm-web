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
        rowMap.put("是否PM", "否");

        AssetRow assetRow = new AssetRow(assetInfo, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");
        assetRow.setFieldBooleanValue("是否PM", "setIsPm", null);

        assertEquals("CT", assetInfo.getAliasName());
        assertEquals("2017-12-12", df.format(assetInfo.getManufactDate()));
        assertEquals(1, assetInfo.getFunctionGroup().intValue());
        assertEquals(1.6, assetInfo.getSalvageValue().doubleValue(), 0.1);
        assertEquals(1, assetInfo.getFunctionGrade().intValue());
        assertFalse(assetInfo.getIsPm());

    }

    @Test
    public void testSetFieldValue_IsOld_Clear_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setUid("uid");
        assetInfo.setAliasName("CT");
        assetInfo.setManufactDate(today);
        assetInfo.setFunctionGroup(1);
        assetInfo.setSalvageValue(1.6);
        assetInfo.setFunctionGrade(1);
        assetInfo.setIsPm(true);

        assertNotNull(assetInfo.getAliasName());
        assertNotNull(assetInfo.getManufactDate());
        assertNotNull(assetInfo.getFunctionGroup());
        assertNotNull(assetInfo.getSalvageValue());
        assertNotNull(assetInfo.getFunctionGrade());
        assertNotNull(assetInfo.getIsPm());

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "#");
        rowMap.put("出厂日期", "#");
        rowMap.put("医疗器械分类", "#");
        rowMap.put("最终残值", "#");
        rowMap.put("功能等级", "#");
        rowMap.put("是否PM", "#");

        AssetRow assetRow = new AssetRow(assetInfo, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");
        assetRow.setFieldBooleanValue("是否PM", "setIsPm", null);

        assertNull(assetInfo.getAliasName());
        assertNull(assetInfo.getManufactDate());
        assertNull(assetInfo.getFunctionGroup());
        assertNull(assetInfo.getSalvageValue());
        assertNull(assetInfo.getFunctionGrade());
        assertNull(assetInfo.getIsPm());
    }

    @Test
    public void testSetFieldValue_IsOld_Update_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setUid("uid");
        assetInfo.setAliasName("CT");
        assetInfo.setManufactDate(today);
        assetInfo.setFunctionGroup(1);
        assetInfo.setSalvageValue(1.6);
        assetInfo.setFunctionGrade(1);
        assetInfo.setIsPm(true);

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "New CT");
        rowMap.put("出厂日期", "2017-12-01");
        rowMap.put("医疗器械分类", "Option2");
        rowMap.put("最终残值", "10.6");
        rowMap.put("功能等级", "2");
        rowMap.put("是否PM", "否");

        AssetRow assetRow = new AssetRow(assetInfo, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");
        assetRow.setFieldBooleanValue("是否PM", "setIsPm", null);

        assertEquals("New CT", assetInfo.getAliasName());
        assertEquals("2017-12-01", df.format(assetInfo.getManufactDate()));
        assertEquals(2, assetInfo.getFunctionGroup().intValue());
        assertEquals(10.6, assetInfo.getSalvageValue().doubleValue(), 0.1);
        assertEquals(2, assetInfo.getFunctionGrade().intValue());
        assertFalse(assetInfo.getIsPm());

    }

    @Test
    public void testSetFieldValue_IsOld_Keep_Old_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setUid("uid");
        assetInfo.setAliasName("CT");
        assetInfo.setManufactDate(today);
        assetInfo.setFunctionGroup(1);
        assetInfo.setSalvageValue(1.6);
        assetInfo.setFunctionGrade(1);
        assetInfo.setIsPm(true);

        Map<String, Object> rowMap = new HashMap<>();
//        rowMap.put("设备别名", "");

        AssetRow assetRow = new AssetRow(assetInfo, rowMap);
        assetRow.setFieldStringValue("设备别名", "setAliasName");
        assetRow.setFieldDateValue("出厂日期", "setManufactDate");
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null, null);
        assetRow.setFieldDoubleValue("最终残值", "setSalvageValue");
        assetRow.setFieldIntegerValue("功能等级", "setFunctionGrade");
        assetRow.setFieldBooleanValue("是否PM", "setIsPm", null);

        assertEquals("CT", assetInfo.getAliasName());
        assertEquals(today, assetInfo.getManufactDate());
        assertEquals(1, assetInfo.getFunctionGroup().intValue());
        assertEquals(1.6, assetInfo.getSalvageValue().doubleValue(), 0.1);
        assertEquals(1, assetInfo.getFunctionGrade().intValue());
        assertTrue(assetInfo.getIsPm());
    }

    @Test
    public void testSetFieldValue_use_default_value() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setUid("uid");

        Map<String, Object> rowMap = new HashMap<>();

        AssetRow assetRow = new AssetRow(assetInfo, rowMap);
        assetRow.setFieldBooleanValue("是否PM", "setIsPm", null);

        assertTrue(assetInfo.getIsPm());
    }

}

package com.ge.apm.service.asset;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.I18nMessage;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AssetRowTest {

    private static List<I18nMessage> list = new ArrayList<>();

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
    public void testSetFieldValue_setAliasName_IsNew() {

        AssetInfo assetInfo = new AssetInfo();

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "CT");


        AssetRow assetRow = new AssetRow(assetInfo, true, rowMap);
        assetRow.setAliasName();
        assertEquals("CT", assetInfo.getAliasName());

    }

    @Test
    public void testSetFieldValue_setAliasName_IsOld_Clear_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAliasName("CT");
        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "#");


        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setAliasName();
        assertNull(assetInfo.getAliasName());

    }

    @Test
    public void testSetFieldValue_setAliasName_IsOld_Update_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAliasName("CT");
        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("设备别名", "New CT");


        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setAliasName();
        assertEquals("New CT", assetInfo.getAliasName());

    }

    @Test
    public void testSetFieldValue_setAliasName_IsOld_Keep_Old_Data() {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAliasName("CT");
        Map<String, Object> rowMap = new HashMap<>();
//        rowMap.put("设备别名", "");


        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setAliasName();
        assertEquals("CT", assetInfo.getAliasName());

    }

    @Test
    public void testSetFieldValue_setFunctionGroup_IsNew() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        AssetInfo assetInfo = new AssetInfo();

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("医疗器械分类", "Option1");


        AssetRow assetRow = new AssetRow(assetInfo, true, rowMap);
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null);
        assertEquals(1, assetInfo.getFunctionGroup().intValue());

    }

    @Test
    public void testSetFieldValue_setFunctionGroup_IsOld_Clear_Data() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setFunctionGroup(1);

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("医疗器械分类", "#");


        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null);
        assertNull(assetInfo.getFunctionGroup());

    }

    @Test
    public void testSetFieldValue_setFunctionGroup_IsOld_Update_Data() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setFunctionGroup(1);

        Map<String, Object> rowMap = new HashMap<>();
        rowMap.put("医疗器械分类", "Option2");


        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null);
        assertEquals(2, assetInfo.getFunctionGroup().intValue());

    }

    @Test
    public void testSetFieldValue_setFunctionGroup_IsOld_Keep_Data() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setFunctionGroup(1);

        Map<String, Object> rowMap = new HashMap<>();
//        rowMap.put("医疗器械分类", "Option2");


        AssetRow assetRow = new AssetRow(assetInfo, false, rowMap);
        assetRow.setFieldDropdownValue("医疗器械分类", "setFunctionGroup", list, null);
        assertEquals(1, assetInfo.getFunctionGroup().intValue());

    }

}

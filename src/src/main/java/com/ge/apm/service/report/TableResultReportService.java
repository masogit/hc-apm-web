/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.report;

import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.ge.apm.domain.pure.compute.Calculator;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 212579464
 */
public class TableResultReportService {

    private static final Logger logger = LoggerFactory.getLogger(TableResultReportService.class);

    private static final Double MINIMUM = .00001;

    private static final Pattern FORMULA_PATTERN = Pattern.compile("(?<=\\[).+?(?=\\])");

    /**
     *
     * @param tableValue
     * @param table
     * @return 含有resKey的<K,V>
     */
    public static Map<String, Object> getRuleResultData(Map<String, Object> tableValue, TableSchema table) {
        Map<String, Object> resMap = new HashMap<>();
        Map<String, Integer> orderMap = getFormulaOrder(table);
        table.getGroups().stream().flatMap(group-> group.getItems().stream())
                        .filter(item-> item.getExp() != null && !Strings.isNullOrEmpty(item.getExp().get("rule")))
                        .sorted(Comparator.comparingInt(item->  orderMap.get(item.getKey())))
                        .forEach(item-> {
                                    switch (item.getExp().get("rule")) {
                                        case "RangeRule":
                                            if(!Strings.isNullOrEmpty(item.getResKey())) {
                                                resMap.put(item.getResKey(), getResultForRangeRule(tableValue, item));
                                            }
                                            break;
                                        case "ScopeRule":
                                            if(!Strings.isNullOrEmpty(item.getResKey())) {
                                                resMap.put(item.getResKey(), getResultForScopeRule(tableValue, item));
                                            }
                                            break;
                                        case "VerifyRule":
                                            if(!Strings.isNullOrEmpty(item.getResKey())) {
                                                resMap.put(item.getResKey(), getResultForVerifyRule(tableValue, item));
                                            }
                                            break;
                                        case "DeltaRule":
                                            if(!Strings.isNullOrEmpty(item.getResKey())) {
                                                resMap.put(item.getResKey(), getResultForDeltaVerifyRule(tableValue, item));
                                            }
                                            break;
                                        case "FormulaValue":
                                            resMap.put(item.getKey(), getResultForFormulaValue(tableValue, item));
                                            break;
                                        case "FormulaRule": {
                                            if(!Strings.isNullOrEmpty(item.getResKey())) {
                                                resMap.put(item.getKey(), getResultForFormulaRule(tableValue, item));
                                            }
                                            break;
                                        }
                                    }
                                });


        return resMap;
    }

    private static String getResultForFormulaRule(Map<String, Object> tableValue, TableItem item) {

         String resultForFormulaValue = getResultForFormulaValue(tableValue, item);
         if(!Strings.isNullOrEmpty(resultForFormulaValue)){
             return  resultForFormulaValue;
         }
         return "";
    }

    private static Map<String, Integer> getFormulaOrder(TableSchema table){

        Map<String, Integer> orderMap = new HashMap<>();
        table.getGroups().stream().flatMap(group-> group.getItems().stream())
                .filter(item-> item.getExp() != null && !Strings.isNullOrEmpty(item.getExp().get("rule")))
                .forEach(item-> {
                        switch (item.getExp().get("rule")) {
                            case "RangeRule":
                            case "ScopeRule":
                            case "VerifyRule":
                            case "DeltaRule":
                                orderMap.put(item.getKey(), 0);
                                break;
                            case "FormulaValue":
                            case "FormulaRule":
                                orderMap.put(item.getKey(), 1);
                                break;
                            }
                    });

        //最多支持3层引用, 循环引用会不确定
        for(int i=0;i<3;i++) {
            table.getGroups().stream().flatMap(group -> group.getItems().stream())
                    .filter(item -> item.getExp() != null && !Strings.isNullOrEmpty(item.getExp().get("rule")))
                    .filter(item -> orderMap.get(item.getKey()) >= 1)
                    .forEach(item ->
                            getSubKeys(item).stream()
                                    .filter(l-> orderMap.containsKey(l))
                                    .map(l -> orderMap.get(l))
                                    .max(Integer::compareTo).ifPresent(max -> orderMap.put(item.getKey(), max + 1))
                    );
        }
        return orderMap;
    }

    private static List<String> getSubKeys(TableItem item){
        String formula = item.getExp().get("formula");

        List<String> keyList = new ArrayList<>();
        Matcher matcher = FORMULA_PATTERN.matcher(formula);
        while (matcher.find()){
            keyList.add(matcher.group(0));
        }

        return keyList;
    }

    private static String getResultForFormulaValue(Map<String, Object> tableValue, TableItem item) {

        String formula = item.getExp().get("formula");

        Matcher matcher = FORMULA_PATTERN.matcher(formula);
        while (matcher.find()){
            String key = matcher.group(0);
            formula = formula.replace("[".concat(key).concat("]"), String.valueOf(tableValue.get(key)));
        }

        Calculator calculator = new Calculator();

        String res = "";
        try{
            res = calculator.calculate(formula).toString();
        }catch (Exception e){
            logger.error("formula compute error ",e);
        }
        return res;

    }

    /**
     * 计算相对误差是否超限 合格: 偏差下限范围*FS<|Y-X|/X\<偏差上限范围 误差下限范围*FS<|Y-Z|/Z<误差上限范围
     * @
     *
     * param tableValue
     * @param tableItem
     * @return
     */
    private static String getResultForVerifyRule(Map<String, Object> tableValue, TableItem tableItem) {
        Double x = toDouble(tableItem.getExp().get("norm"));
        Double y = toDouble(tableValue.get(tableItem.getExp().get("Y_Key")));
        Double z = toDouble(tableValue.get(tableItem.getExp().get("Z_Key")));
//        Double fs = toDouble(tableItem.getExp().get("FS"));
        if (x == null || y == null || z == null) {
            return null;
        }

        Double delta = x == 0 ? y - x : (y - x) / x;
        Double mu = z == 0 ? y - z : (y - z) / z;

        Double deltaMin = Optional.ofNullable(toDouble(tableItem.getExp().get("deltaMinPercent"))).orElse(0.0) / 100;
        Double deltaMax = Optional.ofNullable(toDouble(tableItem.getExp().get("deltaMaxPercent"))).orElse(0.0) / 100;

        Double muMin = Optional.ofNullable(toDouble(tableItem.getExp().get("MuMinPercent"))).orElse(0.0) / 100;
        Double muMax = Optional.ofNullable(toDouble(tableItem.getExp().get("MuMaxPercent"))).orElse(0.0) / 100;

        if (delta < deltaMin || delta > deltaMax) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "DeltaException");
        }
        if (mu < muMin || mu > muMax) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "MuException");
        }

        return "Normal";
    }

    /**
     * 实际值和示值差
     *
     * @param tableValue
     * @param tableItem
     * @return
     */
    private static String getResultForDeltaVerifyRule(Map<String, Object> tableValue, TableItem tableItem) {
        Double y = toDouble(tableValue.get(tableItem.getExp().get("Y_Key")));
        Double z = toDouble(tableValue.get(tableItem.getExp().get("Z_Key")));
        Double max = toDouble(tableItem.getExp().get("deltaMax"));
        if (y == null || z == null || max == null) {
            return null;
        }

        Double delta = Math.abs(y - z);
        if (delta - max > MINIMUM) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "DeltaException");
        }

        return "Normal";
    }

    /**
     * 测量值是否超出设定值的上下限范围
     *
     * @param tableValue
     * @param tableItem
     * @return
     */
    private static String getResultForScopeRule(Map<String, Object> tableValue, TableItem tableItem) {
        Double value = toDouble(tableValue.get(tableItem.getKey()));
        if (value == null) {
            return null;
        }
        Double norm = toDouble(tableItem.getExp().get("norm"));
        if (norm == null) {
            return null;
        }
        Double min = Math.min(norm + Optional.ofNullable(toDouble(tableItem.getExp().get("min"))).orElse(0.0),
                norm * (1 + Optional.ofNullable(toDouble(tableItem.getExp().get("minPercent"))).orElse(0.0) / 100));
        Double max = Math.max(norm + Optional.ofNullable(toDouble(tableItem.getExp().get("max"))).orElse(0.0),
                norm * (1 + Optional.ofNullable(toDouble(tableItem.getExp().get("maxPercent"))).orElse(0.0) / 100));

        if (value < min + ("GT".equals(tableItem.getExp().get("minCompare")) ? MINIMUM : 0 - MINIMUM)) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "Low");
        }

        if (value > max - ("LT".equals(tableItem.getExp().get("maxCompare")) ? MINIMUM : 0 - MINIMUM)) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "High");
        }
        return "Normal";
    }

    /**
     * 测量值是否超出给定上下限
     *
     * @param tableValue
     * @param tableItem
     * @return
     */
    private static String getResultForRangeRule(Map<String, Object> tableValue, TableItem tableItem) {

        Double value = toDouble(tableValue.get(tableItem.getKey()));
        if (value == null) {
            return null;
        }
        Double min = Optional.ofNullable(toDouble(tableItem.getExp().get("min"))).orElse(0.0);
        Double max = Optional.ofNullable(toDouble(tableItem.getExp().get("max"))).orElse(0.0);

        if (value < min + ("GT".equals(tableItem.getExp().get("minCompare")) ? MINIMUM : 0 - MINIMUM)) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "Low");
        }

        if (value > max - ("LT".equals(tableItem.getExp().get("maxCompare")) ? MINIMUM : 0 - MINIMUM)) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "High");
        }

        return "Normal";
    }

    private static Double toDouble(Object input) {
        try {
            return input == null ? null : Double.parseDouble(input.toString().replaceAll("\"", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 返回值: 合格/不合格 : resIdentify_1: 'Normal' / 'Abnormal' 合格/过高/过低 :
     * resIdentify_2: 'Normal' / 'High' / 'Low' 合格/误差超限/偏差超限 :resIdentify_3:
     * 'Normal' / 'deltaException' / 'muException'
     */
    private static String getReturnValue(String resIdentify, String result) {
        switch (resIdentify) {
            case "resIdentify_1":
                return result.equals("0") ? "Normal" : "Abnormal";
            default:
                return result;
        }
    }
}

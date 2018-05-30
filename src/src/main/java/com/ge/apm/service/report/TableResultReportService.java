/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.report;

import com.ge.apm.domain.pure.TableItem;
import com.ge.apm.domain.pure.TableSchema;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author 212579464
 */
public class TableResultReportService {

    static final Double MINIMUM = .00001;

    /**
     *
     * @param tableValue
     * @param table
     * @return 含有resKey的<K,V>
     */
    public static Map<String, Object> getRuleResultData(Map<String, Object> tableValue, TableSchema table) {
        Map<String, Object> resMap = new HashMap<>();
        table.getGroups().forEach(group -> {
            group.getItems().stream().filter(item -> !Strings.isNullOrEmpty(item.getResKey()) && item.getExp() != null).forEach(item -> {
                switch (item.getExp().get("rule")) {
                    case "RangeRule": {
                        resMap.put(item.getResKey(), getResultForRangeRule(tableValue, item));
                        break;
                    }
                    case "ScopeRule": {
                        resMap.put(item.getResKey(), getResultForScopeRule(tableValue, item));
                        break;
                    }
                    case "VerifyRule": {
                        resMap.put(item.getResKey(), getResultForVerifyRule(tableValue, item));
                        break;
                    }
                    case "DeltaRule": {
                        resMap.put(item.getResKey(), getResultForDeltaVerifyRule(tableValue, item));
                        break;
                    }
                }
            });
        });
        return resMap;
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

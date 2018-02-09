/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

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
                        String res = getResultForRangeRule(tableValue, item);
                        if (res != null) {
                            resMap.put(item.getResKey(), res);
                        }
                        break;
                    }
                    case "ScopeRule": {
                        String res = getResultForScopeRule(tableValue, item);
                        if (res != null) {
                            resMap.put(item.getResKey(), res);
                        }
                        break;
                    }
                    case "VerifyRule": {
                        String res = getResultForVerifyRule(tableValue, item);
                        if (res != null) {
                            resMap.put(item.getResKey(), res);
                        }
                        break;
                    }
                    case "DeltaRule": {
                        String res = getResultForDeltaVerifyRule(tableValue, item);
                        if (res != null) {
                            resMap.put(item.getResKey(), res);
                        }
                        break;
                    }
                }
            });
        });
        return resMap;
    }

    /**
     * 计算示值误差是否超限 
     * 合格: 偏差下限范围*FS<|Y-X|/X<偏差上限范围*FS 误差下限范围*FS<|Y-Z|/Z<误差上限范围*FS 
     * @param tableValue
     * @param tableItem
     * @return
     */
    private static String getResultForVerifyRule(Map<String, Object> tableValue, TableItem tableItem) {
        Double x = toDouble(tableItem.getExp().get("norm"));
        Double y = toDouble(tableValue.get(tableItem.getExp().get("Y_Key")));
        Double z = toDouble(tableValue.get(tableItem.getExp().get("Z_Key")));
        Double fs = toDouble(tableValue.get(tableItem.getExp().get("FS")));
        if (x == null || y == null || z == null) {
            return null;
        }

        Double delta = x == 0 ? y - x : (y - x) / x;
        Double mu = z == 0 ? y - z : (y - z) / z;

        Double deltaMin = Optional.ofNullable(toDouble(tableItem.getExp().get("deltaMinPercent"))).orElse(0.0) / 100 *fs;
        Double deltaMax = Optional.ofNullable(toDouble(tableItem.getExp().get("deltaMaxPercent"))).orElse(0.0) / 100 *fs;

        Double muMin = Optional.ofNullable(toDouble(tableItem.getExp().get("MuMinPercent"))).orElse(0.0) / 100 *fs;
        Double muMax = Optional.ofNullable(toDouble(tableItem.getExp().get("MuMaxPercent"))).orElse(0.0) / 100 *fs;

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
        if (delta - max > 0.0001) {
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
                norm * (1 + Optional.ofNullable(toDouble(tableItem.getExp().get("minPercent"))).orElse(0.0)) / 100);
        Double max = Math.max(norm + Optional.ofNullable(toDouble(tableItem.getExp().get("max"))).orElse(0.0),
                norm * (1 + Optional.ofNullable(toDouble(tableItem.getExp().get("maxPercent"))).orElse(0.0)) / 100);
        if (value < min) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "Low");
        }
        if (value > max) {
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
        Double min = toDouble(tableItem.getExp().get("min"));
        Double max = toDouble(tableItem.getExp().get("max"));
        if (min != null && value < min) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "Low");
        }
        if (max != null && value > max) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "High");
        }
        return "Normal";
    }

    private static Double toDouble(Object input) {
        try {
            return input == null ? null : Double.parseDouble(input.toString());
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

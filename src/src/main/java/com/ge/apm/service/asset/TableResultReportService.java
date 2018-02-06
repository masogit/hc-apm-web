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
                }
            });
        });
        return resMap;
    }

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
                norm * (1 + Optional.ofNullable(toDouble(tableItem.getExp().get("minPercent"))).orElse(0.0)));
        Double max = Math.max(norm + Optional.ofNullable(toDouble(tableItem.getExp().get("max"))).orElse(0.0),
                norm * (1 + Optional.ofNullable(toDouble(tableItem.getExp().get("maxPercent"))).orElse(0.0)));
        if (value < min) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "-1");
        }
        if (value > max) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "1");
        }
        return "Normal";
    }

    /**
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
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "-1");
        }
        if (max != null && value > max) {
            return getReturnValue((String) tableItem.getExp().get("resIdentify"), "1");
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
     * resIdentify_2: 'Normal' / 'High' / 'Low'
     */
    private static String getReturnValue(String resIdentify, String result) {
        switch (resIdentify) {
            case "resIdentify_1":
                return result.equals("0") ? "Normal" : "Abnormal";
            case "resIdentify_2":
                switch (result) {
                    case "1":
                        return "High";
                    case "-1":
                        return "Low";
                    default:
                        return "Normal";
                }
            default:
                return null;
        }
    }
}

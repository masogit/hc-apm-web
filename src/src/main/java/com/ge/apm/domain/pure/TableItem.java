/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 *
 * @author 212579464
 */
@Data
public class TableItem{
    public static enum ComponentType {
        Text,Number, Switch,Select,TextArea,Date,Label, Compute, Picture
    }
    private String key;
    private String label;
    
    //参考值说明
    private String ref;
    //计算结果key
    private String resKey;
    private ComponentType component;
    private Map<String, String> props;
    private List<TableOption> options;
    private Map<String, String> exp;
    private Integer decimalLength =3;
    private String defaultValue ;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.domain.pure;

import java.util.List;
import java.util.Map;

/**
 *
 * @author 212579464
 */
public class TableItem{
    public static enum ComponentType {
        Text,Number, Switch,Select,TextArea,Date,Label
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ComponentType getComponent() {
        return component;
    }

    public void setComponent(ComponentType component) {
        this.component = component;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }

    public List<TableOption> getOptions() {
        return options;
    }

    public void setOptions(List<TableOption> options) {
        this.options = options;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getResKey() {
        return resKey;
    }

    public void setResKey(String resKey) {
        this.resKey = resKey;
    }

    public Map<String, String> getExp() {
        return exp;
    }

    public void setExp(Map<String, String> exp) {
        this.exp = exp;
    }

    public Integer getDecimalLength() {
        return decimalLength;
    }

    public void setDecimalLength(Integer decimalLength) {
        this.decimalLength = decimalLength;
    }
    
    
}

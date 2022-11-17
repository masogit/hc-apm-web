package com.ge.apm.view.budget;

import com.ge.apm.domain.*;
import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.Getter;
import lombok.Setter;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class DeptPurchaseController {
    @Getter@Setter
    private List<DeptPurchaseLog> summary = new ArrayList<>();
    @Getter@Setter
    private List<DeptPurchaseLog> logList = new ArrayList<>();

    public List<DeptPurchaseLog> getSummary(){
        logList.add(DeptPurchaseLog.builder().yearPurchase("2022").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2022预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2022").amount(new BigDecimal("100")).clinicalDeptName("影像科").remark("2022预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2021").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2021预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2021").amount(new BigDecimal("100")).clinicalDeptName("影像科").remark("2021预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2020").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2020预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2020").amount(new BigDecimal("100")).clinicalDeptName("影像科").remark("2020预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2019").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2019预算").recordDate("2022-01-01 10:20:30").build());
        summary = logList.stream().collect(Collectors.toMap(key -> summaryKey(key), value -> value
                , (o, n) -> summaryAmount(o, n))).values().stream().collect(Collectors.toList());
        return summary;
    }

    public List<DeptPurchaseLog> getLogList(){
        logList.add(DeptPurchaseLog.builder().yearPurchase("2022").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2022预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2022").amount(new BigDecimal("100")).clinicalDeptName("影像科").remark("2022预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2021").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2021预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2021").amount(new BigDecimal("100")).clinicalDeptName("影像科").remark("2021预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2020").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2020预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2020").amount(new BigDecimal("100")).clinicalDeptName("影像科").remark("2020预算").recordDate("2022-01-01 10:20:30").build());
        logList.add(DeptPurchaseLog.builder().yearPurchase("2019").amount(new BigDecimal("100")).clinicalDeptName("放射科").remark("2019预算").recordDate("2022-01-01 10:20:30").build());
        summary = logList.stream().collect(Collectors.toMap(key -> summaryKey(key), value -> value
                , (o, n) -> summaryAmount(o, n))).values().stream().collect(Collectors.toList());
        return summary;
    }



    private String summaryKey(DeptPurchaseLog key) {
        return key.getClinicalDeptName() + key.getYearPurchase();
    }

    private DeptPurchaseLog summaryAmount(DeptPurchaseLog o, DeptPurchaseLog n) {
        n.setAmount(n.getAmount().add(o.getAmount()));
        return n;
    }

    public void save(){

    }

    public void delete(){

    }

    public  Integer getKey(HashMap<Integer,String> map,String value){
        Integer key = null;
        for(Integer getKey: map.keySet()){
            if(map.get(getKey).equals(value)){
                key = getKey;
            }
        }
        return key;
    }

}

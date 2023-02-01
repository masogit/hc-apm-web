package com.ge.apm.service.constant;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

public class HisConstant {
   private static  LinkedHashMap<String,String> taskTypeMap;

    public static synchronized LinkedHashMap<String, String> getTaskTypeMap() {
        taskTypeMap = new LinkedHashMap<>();
        taskTypeMap.put("科室信息","cronDept");
        taskTypeMap.put("PACS系统设备信息","cronAssetOracle");
        taskTypeMap.put("LIS系统仪器信息","cronAssetSQLServer");
        taskTypeMap.put("收费项目","cronEquipmentSfxmOracle");
        taskTypeMap.put("PACS系统收费明细","cronPACSDetailsOracle");
        taskTypeMap.put("LIS系统收费明细","cronLisDetailsSQLServer");
        taskTypeMap.put("门诊收费明细","cronEquipmentSfxmDetailsOracle");
        return  taskTypeMap;
    }
    //任务类型
    public static synchronized List<String> getTaskTypeList() {
        List taskTypeList = Arrays.asList("科室信息","PACS系统设备信息","LIS系统仪器信息","收费项目","PACS系统收费明细","LIS系统收费明细","门诊收费明细");
        return taskTypeList;
    }

    //执行结果
    public static synchronized List<String> getExecResultList() {
        List taskTypeList = Arrays.asList("fail","success");
        return taskTypeList;
    }

    //需要添加时间的类型
    public static synchronized List<String> getTaskTypeByTimeList() {
        List taskTypeList = Arrays.asList("PACS系统收费明细","LIS系统收费明细","门诊收费明细");
        return taskTypeList;
    }
    public static final String STATUS_INIT = "init";
    public static final String STATUS_INIT_NAME = "新建";
    public static final String STATUS_WAIT = "wait";
    public static final String STATUS_WAIT_NAME = "等待执行";
    public static final String STATUS_RUNING = "runing";
    public static final String STATUS_RUNING_NAME = "执行中";
    public static final String STATUS_DONE = "done";
    public static final String STATUS_DONE_NAME = "执行完毕";

}

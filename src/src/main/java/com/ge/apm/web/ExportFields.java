package com.ge.apm.web;

<<<<<<< HEAD
=======
public enum ExportFields {
    ASSETNAME("资产名称"),
    SERIALNUM("制造商序列号"),
    ACTUALTIME("实际完成时间"),
    ASSETNUM("设备编号(院方)"),
    RUNLEVEL("保养等级"),
    PMTYPE("保养类型"),
    CLINICALNAME("所属科室"),
    CREATOR("创建人"),
    OWNER("负责人"),
    PMNUM("保养编号"),
    CURRENTSTEP("当前处理步骤"),
    ORDERTYPE("工单类型"),
    PLANTIME("计划完成时间");
>>>>>>> ab4f83c38c069fae32f7c9f326492b62fc36144e

public enum ExportFields {
    ASSETNAME("资产名称", "Asset Name"),
    SERIALNUM("制造商序列号", "Manufacturer Serial Number"),
    ASSETNUM("设备编号(院方)", "Equipment ID (Hospital)"),
    RUNLEVEL("保养等级", "Measurement Grade"),
    PMTYPE("保养类型", "Maintenance Type"),
    CLINICALNAME("所属科室", "Clinical Department"),
    CREATOR("创建人", "Creator"),
    OWNER("负责人", "Owner"),
    PMNUM("保养编号", "Maintenance ID"),
    CURRENTSTEP("当前处理步骤", "Current Processing Step"),
    ORDERTYPE("工单类型", "Workorder Type"),
    PLANTIME("计划完成时间", "Planned Completion Time");

    private final String english;

    private final String chinese;




    private ExportFields(String chinese, String english) {
        this.chinese = chinese;
        this.english = english;
    }

    public String getEnglish() {
        return english;
    }

    public String getChinese() {
        return chinese;
    }

    public static void main(String[] args) {
        System.out.println(ExportFields.values());
        for (ExportFields exs: ExportFields.values()
             ) {

        }

    }

}

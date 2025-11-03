package com.ge.apm.web;


public enum ExportFields {
    ASSETNAME("资产名称", "Asset Name"),
    SERIALNUM("制造商序列号", "Manufacturer Serial Number"),
    ASSETNUM("设备编号(院方)", "Equipment ID (Hospital)"),
    RUNLEVEL("保养等级", "Measurement Grade"),
    PMTYPE("保养类型", "Maintenance Type"),
    CLINICALNAME("所属科室", "Clinical Department"),
    PMRULE("保养规则", "PM Rule"),
    MAINTENANCEPERIOD("保养周期(月)", "Maintenance Period"),
    CREATOR("创建人", "Creator"),
    OWNER("负责人", "Owner"),
    PMNUM("保养编号", "Maintenance ID"),
    CURRENTSTEP("当前处理步骤", "Current Processing Step"),
    ORDERTYPE("工单类型", "Workorder Type"),
    ORDERCOMMENTS("工单备注", "Workorder Comments"),
    PLANTIME("计划完成时间", "Planned Completion Time"),
    ACTUALCOMPLETIONTIME("实际完成时间", "Actual Completion Time"),

    SITENAME("所属院区","Site Name");

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

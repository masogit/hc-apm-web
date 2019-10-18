package com.ge.apm.web;

public enum ExportFields {
    ASSETNAME("资产名称"),
    SERIALNUM("制造商序列号"),
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


    private final String chinese;

    private ExportFields(String chinese) {
        this.chinese = chinese;
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

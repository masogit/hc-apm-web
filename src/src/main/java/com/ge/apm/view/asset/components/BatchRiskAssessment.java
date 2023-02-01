package com.ge.apm.view.asset.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BatchRiskAssessment {
    private Integer clinicalFunctionScore;//临床功能得分
    private Integer tangibleRiskScore;//有形风险得分
    private Integer problemAvoidanceRateScore;//问题避免率得分
    private Integer accidentHistoryScore;//事故历史得分
    private Integer specialRequirementScore;//制造商/管理部门特殊要求得分
    private String checkCycle; //检查周期
    private String detectionCycle; //检测周期
    private String maintenancePeriod; //保养周期
    private String sumRiskScore; //风险总分
}

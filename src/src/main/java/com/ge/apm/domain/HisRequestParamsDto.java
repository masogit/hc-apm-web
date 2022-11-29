package com.ge.apm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 503191723
 * kong.weigen
 * 页面请求参数HIS
 */
@Data
public class HisRequestParamsDto {

    private static final long serialVersionUID = 9L;

    //apm科室名称
    private String apmOrgName;

    //任务类型
    private String taskTypeName;

    //执行结果
    private String taskResultStatus;

    //apm机构级别
    private Integer orgLevel;

    //apm机构编码
    private String apmOrgCode;

    //设备编号
    private String departNum;

    //设备型号
    private String functionType;

    //开始日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    //结束日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}

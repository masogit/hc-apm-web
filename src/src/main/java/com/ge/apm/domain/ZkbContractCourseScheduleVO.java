package com.ge.apm.domain;

/**
 * Created by 503202755 on 2021/4/28
 */
public class ZkbContractCourseScheduleVO extends ZkbContractCourseSchedule{
    /**
     * excel行数
     */
    private int lineNum;

    /**
     * 是否删除
     */
    private String delete;


    public boolean isNull() {
        boolean flag = false;
        if(this.getCid() == null
                &&this.getCname() == null
                &&this.getModality() == null
                &&this.getStartDate()==null
                &&this.getEndDate()==null
                &&this.getTrainingDays()==null
                &&this.getInstructor()==null
                &&this.getAudience()==null
                &&this.getLocation()==null
                &&this.getStudentClass()==null
                &&this.getBatchNo()==null
                &&this.getValid()==null
                &&this.getDeductCount()==null
                &&this.getType()==null){
            flag = true;
        }
        return flag;
    }

    @Override
    public int hashCode() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer=stringBuffer.append(this.getCid() != null ? this.getCid().toString() : "");
        stringBuffer=stringBuffer.append(this.getCname() != null ? this.getCname().toString() : "");
        stringBuffer=stringBuffer.append(this.getModality() != null ? this.getModality().toString() : "");
        stringBuffer=stringBuffer.append(this.getStartDate() != null ? this.getStartDate().toString() : "");
        stringBuffer=stringBuffer.append(this.getEndDate() != null ? this.getEndDate().toString() : "");
        stringBuffer=stringBuffer.append(this.getTrainingDays() != null ? this.getTrainingDays().toString() : "");
        stringBuffer=stringBuffer.append(this.getInstructor() != null ? this.getInstructor().toString() : "");
        stringBuffer=stringBuffer.append(this.getAudience() != null ? this.getAudience().toString() : "");
        stringBuffer=stringBuffer.append(this.getLocation() != null ? this.getLocation().toString() : "");
        stringBuffer=stringBuffer.append(this.getStudentClass() != null ? this.getStudentClass().toString() : "");
        stringBuffer=stringBuffer.append(this.getBatchNo() != null ? this.getBatchNo().toString() : "");
        stringBuffer=stringBuffer.append(this.getValid() != null ? this.getValid().toString() : "");
        stringBuffer=stringBuffer.append(this.getDeductCount() != null ? this.getDeductCount().toString() : "");
        stringBuffer=stringBuffer.append(this.getType() != null ? this.getType().toString() : "");
        return stringBuffer.toString().hashCode();
    }

    public int getLineNum() {
        return lineNum;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }
}

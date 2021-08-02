package com.ge.apm.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.HashMap;
import java.util.List;



public interface AeMapper  {

    @Select("select DW_AE_ID from ae_mpping where ASSET_ID = #{assetId}")
    public String  selectAeMapperBean(String assetId);

    @Select("select * from ae_workload where aet = #{aet} and studydate = #{studydate}")
    public List<HashMap>  selectAeWorkloadBeanByDay(@Param("aet") String aet, @Param("studydate") Date studydate);

    @Select("select dashboard_page from ae_workload where aet = #{aet} ")
    public List<String>  selectAeWorkloadBeanByDashboardPage(@Param("aet") String aet);

    @Select("select min(studydate),max(studydate) from ae_workload where aet = #{aet} and studydate >= #{studydate} and studydate <= #{studydateMax}")
    public List<HashMap>  selectAeWorkloadBeanByMonthMin(@Param("aet") String aet, @Param("studydate") Date studydate,@Param("studydateMax") Date studydateMax);

    @Select("select sum(studyexposuretime) from ae_workload where aet = #{aet} and studydate >= #{studydate} and studydate <= #{studydateMax}")
    public List<HashMap>  selectAeWorkloadBeanSumStudyexposuretime(@Param("aet") String aet, @Param("studydate") Date studydate,@Param("studydateMax") Date studydateMax);
}

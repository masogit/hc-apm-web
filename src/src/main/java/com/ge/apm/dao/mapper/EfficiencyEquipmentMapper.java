package com.ge.apm.dao.mapper;

import com.ge.apm.domain.EfficiencyEquipmentInfo;
import com.ge.apm.domain.OrgInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.*;

public interface EfficiencyEquipmentMapper {


//    @Select("    WITH equipment_summary AS (" +
//            "       select ai.uid assetUid,sum(hdr.total_amount)  income ,sum(ipi.check_number_people) checkNumberPeople ,sum(acai.cost_amount) as cost from efficiency_equipment_info eei left join asset_info ai on eei.asset_uid=ai.uid   left join his_equipment_data_result hdr  on eei.asset_uid=hdr.asset_uid  and hdr.exec_time between #{startDate} and #{endDate} left join iot_performance_info  ipi on    eei.asset_uid=ipi.asset_uid   and ipi.date between #{startDate} and #{endDate} left join asset_cost_amount_info acai on eei.asset_uid=acai.asset_uid    and acai.statistical_date between #{startDate} and #{endDate}   where ai.tenant_uid=#{tenantUid}   group by ai.uid )" +
//            " select  es.income ,es.cost,es.checkNumberPeople,ai.name as assetName,ai.serial_num  serialNum , ai.qr_code qrCode,ai.function_type functionType ,ai.purchase_price purchasePrice,ai.clinical_dept_name clinicalDeptName ,ai.asset_group assetGroup,brand.name as  brandName    from  equipment_summary es join asset_info ai on es.assetUid=ai.uid   left join brand on  ai.brand_id=brand.id"  )
//    List<EfficiencyEquipmentInfo>  getEfficiencyEquipmentInfo(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("tenantUid") String tenantUid);





//    @Select("WITH income_summary AS ( SELECT asset_uid, SUM(total_amount) AS income FROM his_equipment_data_result WHERE  tenant_uid=#{tenantUid}   and    exec_time  between #{startDate} and #{endDate} GROUP BY asset_uid )," +
//            "expense_summary AS ( SELECT asset_uid, SUM(cost_amount) AS cost FROM asset_cost_amount_info WHERE  tenant_uid=#{tenantUid}   and    statistical_date  between #{startDate} and #{endDate} GROUP BY asset_uid )," +
//            "check_summary AS ( SELECT asset_uid, SUM(check_number_people) AS checkNumberPeople FROM iot_performance_info WHERE  tenant_uid=#{tenantUid}   and    date  between #{startDate} and #{endDate} GROUP BY asset_uid )"+
//            "select es.asset_uid assetUid,  COALESCE(isum.income, 0) AS income, COALESCE(esum.cost, 0) AS cost,COALESCE(csum.checkNumberPeople, 0) AS  checkNumberPeople,ai.name as assetName,ai.serial_num  serialNum , ai.qr_code qrCode,ai.function_type functionType ,ai.purchase_price purchasePrice,ai.clinical_dept_name clinicalDeptName ,ai.asset_group assetGroup,brand.name as  brandName    from  efficiency_equipment_info es join asset_info ai on es.asset_uid=ai.uid   left join brand on  ai.brand_id=brand.id " +
//            "LEFT JOIN income_summary isum ON es.asset_uid = isum.asset_uid LEFT JOIN expense_summary esum ON es.asset_uid = esum.asset_uid LEFT JOIN check_summary csum ON es.asset_uid = csum.asset_uid")
//        List<EfficiencyEquipmentInfo>  getEfficiencyEquipmentInfo(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("tenantUid") String tenantUid,@Param("assetUids") List<String> assetUids);



//        List<EfficiencyEquipmentInfo>  getEfficiencyEquipmentInfo(@Param("startDate")Date startDate,@Param("endDate")Date endDate,@Param("tenantUid") String tenantUid,@Param("assetUids") List<String> assetUids,@Param("orgUid") String orgUid,@Param("orgUidList") List<String> orgUidList);

        List<EfficiencyEquipmentInfo>  getEfficiencyEquipmentInfo(@Param("startDate")Date startDate, @Param("endDate")Date endDate, @Param("tenantUid") String tenantUid, @Param("assetUids") Set<String> assetUids, @Param("orgUid") String orgUid, @Param("orgUidList") List<String> orgUidList, @Param("qrCodes") List<String> qrCodes);




        List<EfficiencyEquipmentInfo>  selectList(Map<String, Object> param);


        Long selectListCount(Map<String, Object> param);

        int updateBatchSmart(@Param("assetUids") Set<String> assetUids,
                             @Param("positiveRate") BigDecimal positiveRate,
                             @Param("displayName") String displayName,
                             @Param("expectedAnnualIncome") BigDecimal expectedAnnualIncome,
                             @Param("expectedCheckNumberPeople") Integer expectedCheckNumberPeople,
                             @Param("deptExpectedPaybackPeriod") BigDecimal deptExpectedPaybackPeriod);


        List<OrgInfo> getDeptList(@Param("userUID")String userUID, @Param("tenantUid")String tenantUid, @Param("siteUid") String siteUid);

}

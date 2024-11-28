package com.ge.apm.dao;

import com.ge.apm.domain.KeyAssetReport;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;
import java.util.Set;

/**
 * Created by 503266962
 */
public interface KeyAssetReportRepository extends GenericRepositoryUUID<KeyAssetReport> {

    @Query(nativeQuery = true, value = "select count(1) from key_asset_report where asset_name = ?1 and dept_id = ?2 and report_year = ?3")
    int countByReport(String assetName, Integer deptId, Integer reportYear);

    @Query(nativeQuery = true, value = "select count(1) from key_asset_report where asset_name = ?1 and dept_id = ?2 and report_year = ?3 and id != ?4")
    int countByReportAndId(String assetName, Integer deptId, Integer reportYear, String uid);



    @Query(nativeQuery = true, value = "select count(1) from key_asset_report where asset_name = ?1 and asset_dept_name = ?2 and report_year = ?3   and report_type = ?4 ")
    int countByReport(String assetName, String deptName, Integer reportYear,Integer reportType);
    @Query(nativeQuery = true, value = "select count(1) from key_asset_report where asset_name = ?1 and asset_dept_name = ?2 and report_year = ?3    and report_type = ?5   and id != ?4")
    int countByReportAndId(String assetName, String deptName,  Integer reportYear, String uid,Integer reportType);

    @Query(nativeQuery = true, value = "select count(1) from key_asset_report kar left join key_asset_report_org karo  on kar.id=karo.key_asset_report_id    where kar.asset_name = ?1 and karo.org_id in ?2 and kar.report_year = ?3  and kar.report_type = ?4   ")
    int countByReport(String assetName, Set<String> orgUids, Integer reportYear,Integer reportType);


    @Query(nativeQuery = true, value = "select count(1) from key_asset_report kar left join key_asset_report_org karo on kar.id=karo.key_asset_report_id  where kar.asset_name = ?1 and karo.org_id in ?2 and kar.report_year = ?3 and kar.id != ?4  and kar.report_type = ?5   ")
    int countByReportAndId(String assetName,  Set<String> orgUids, Integer reportYear, String uid,Integer reportType);


    @Query(nativeQuery = true,value = "WITH other_users_with_matching_permissions AS (\n" +
            "    SELECT up.key_asset_report_id\n" +
            "    FROM key_asset_report_org up where  up.org_id in ('226d8b0e63624a979b6dc753566cca6a','444','33')\n" +
            "    GROUP BY up.key_asset_report_id\n" +
            "    HAVING\n" +
            "\t\t   (select  count(*) from   key_asset_report_org where key_asset_report_id  =up.key_asset_report_id )<=count(*)\n" +
            ")\n" +
            "SELECT u.*\n" +
            "FROM key_asset_report u\n" +
            "JOIN other_users_with_matching_permissions oump ON u.id = oump.key_asset_report_id;")
    List<KeyAssetReport> selectList();



}

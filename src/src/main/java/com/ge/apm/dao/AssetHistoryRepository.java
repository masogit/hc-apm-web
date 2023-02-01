package com.ge.apm.dao;

import com.ge.apm.domain.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepositoryUUID;

import java.util.List;

/**
 * @author csh
 * @version 1.0
 * @date 2020/5/15
 * @descript AssetHistory历史留痕操作
 * @package com.ge.apm.dao
 */
public interface AssetHistoryRepository extends GenericRepositoryUUID<AssetHistory>, JpaRepository<AssetHistory, String> {

    /**
     * @return void
     * @throws
     * @params [assetUid]
     * @author csh
     * @createTime 2020/5/15 15:14
     * @description 根据uid查询数据
     */
    @Query(value = "select * from asset_history where asset_uid = ?1 order by created_date DESC limit 5", nativeQuery = true)
    List<AssetHistory> findByUidOrderByCreatedDateDescAtMostFive(String assetUid);

    @Query(value = "select * from asset_history where asset_uid = ?1 order by created_date DESC limit 1", nativeQuery = true)
    AssetHistory getAssetHistoryByAssetUidOne(String uid);

    @Query(value = "select * from asset_history order by created_date DESC  limit ?2 offset ?1", nativeQuery = true)
    List<AssetHistory> getAssetHistory(int pageNum, int pageSize);

    @Transactional
    @Modifying
    @Query(value = "update " +
            " asset_history " +
            "set " +
            " asset_json = ai.aiJson " +
            "from " +
            " ( " +
            " select " +
            "  a.uid, " +
            "  row_to_json(a) aiJson " +
            " from " +
            "  ( " +
            "  select " +
            "   uid, " +
            "   asset_dept_id as \"assetDeptId\", " +
            "   asset_dept_uid as \"assetDeptUID\", " +
            "   clinical_dept_id as \"clinicalDeptId\", " +
            "   clinical_dept_uid as \"clinicalDeptUID\", " +
            "   clinical_dept_name as \"clinicalDeptName\", " +
            "   location_name as \"locationName\", " +
            "   clinical_owner_name as \"clinicalOwnerName\", " +
            "   device_status as \"deviceStatus\", " +
            "   purchase_date as \"purchaseDate\", " +
            "   purchase_price as  \"purchasePrice\", " +
            "   qr_code as \"qrCode\", " +
            "   install_date as \"installDate\", " +
            "   depart_num as \"departNum\", " +
            "   name, " +
            "   financing_num as \"financingNum\", " +
            "   location_notes as \"locationNotes\", " +
            "   function_type as \"functionType\", " +
            "   status, " +
            "   is_valid as \"isValid\", " +
            "   asset_disposal_results as \"assetDisposalResults\" " +
            "  from " +
            "   asset_info) a) ai " +
            "where " +
            " asset_history.asset_uid = ai.uid", nativeQuery = true)
    public void batchUpdateAssetHistoryEvent();

}

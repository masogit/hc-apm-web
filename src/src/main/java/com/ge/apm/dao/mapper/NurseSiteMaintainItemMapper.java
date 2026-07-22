package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.NurseSiteMaintainData;
import com.ge.apm.domain.NurseSiteMaintainItem;
import com.ge.apm.domain.UserAccount;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface NurseSiteMaintainItemMapper extends BaseMapper<NurseSiteMaintainItem> {
    List<NurseSiteMaintainItem> findJoinAssetList(Pagination pagination, @Param("ew") Wrapper<NurseSiteMaintainItem> wrapper);

    /**
     * Search daily inspection items that have attachments.
     * Returns data at the equipment item level with asset info and check date.
     *
     * @param param query parameters: tenantUid, siteUid, deptUid, orgUidList,
     *              assetName, serialNum, qrCode, clinicalDeptName, usingDeptName,
     *              checkDateStart, checkDateEnd
     * @return list of maintain items with attachment info
     */
    List<Map<String, Object>> searchMaintainItemsWithAttachment(Map<String, Object> param);

    @Select({"<script>" +
                "select * from nurse_site_maintain_data where parent_id in " +
                    "<foreach item = 'item' index = 'index' collection = 'ids' open='(' separator=',' close=')'>" +
                    "#{item}" +
                    "</foreach>"+
            "</script>"})
    List<NurseSiteMaintainData> findDataList(@Param("ids") Collection<String> ids);

    @Select({"<script>" +
            "select uid, name, clinical_dept_name, depart_num, serial_num, function_type from asset_info where uid in " +
            "<foreach item = 'item' index = 'index' collection = 'assetUidList' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>"+
            "</script>"})
    List<AssetInfo> findAssetList(@Param("assetUidList") Collection<String> assetUidList);

    @Select({"<script>" +
            "select uid, name from user_account where uid in " +
            "<foreach item = 'item' index = 'index' collection = 'userUidList' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>"+
            "</script>"})
    List<UserAccount> findUserList(@Param("userUidList") Collection<String> userUidList);
}

package com.ge.apm.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.ge.apm.domain.NurseSiteMaintain;
import com.ge.apm.service.nurseSite.export.NurseSiteMaintainExport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface NurseSiteMaintainMapper extends BaseMapper<NurseSiteMaintain> {
    List<NurseSiteMaintain> findJoinAssetList(@Param("ew") Wrapper<NurseSiteMaintain> wrapper);

    List<NurseSiteMaintain> findJoinAssetList1(@Param("ew") Wrapper<NurseSiteMaintain> wrapper, @Param("assetTypes") Collection<String> assetTypes);

    List<NurseSiteMaintain> searchMaintain(Map<String, Object> param);

    @Select("SELECT nsmi.status, nsmi.check_date checkDate, nsmi.schedule_date scheduleDate, ua.name checker FROM nurse_site_maintain_item nsmi LEFT JOIN nurse_site_maintain nsm ON nsmi.parent_id = nsm.ID left join user_account ua on nsmi.update_by = ua.uid WHERE 1=1 ${ew.sqlSegment} ")
    List<NurseSiteMaintainExport> getEventRecord(Pagination pagination, @Param("ew") Wrapper wrapper);
}

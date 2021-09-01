package com.ge.apm.dao.mapper;

import java.util.List;

import com.ge.apm.domain.MsgConfig;
import com.ge.apm.domain.MsgTenant;
import org.apache.ibatis.annotations.Select;
import com.ge.apm.domain.TenantInfo;

public interface TenantInfoMapper {
	
	@Select("select id,uid,manhour_price from tenant_info")
	List<TenantInfo> fetchTenantManHourPrice();

	@Select("select * from tenant_info where uid = #{uid}")
	public TenantInfo findByUid(String uid);

	@Select("select * from msg_config where id in(select msgconfig_id from msg_tenant where tenant_uid = #{tenantUid}) order by id")
    List<MsgConfig> findMsgTenants(String tenantUid);

	@Select("select * from msg_config where msg_status = '0' order by id")
	List<MsgConfig> findDefultConfig();
}
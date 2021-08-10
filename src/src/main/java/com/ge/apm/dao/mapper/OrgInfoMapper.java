package com.ge.apm.dao.mapper;

import com.ge.apm.domain.OrgInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrgInfoMapper {

	@Select("with recursive cte_org as (\n" +
			"    select oi.*\n" +
			"    from org_info oi\n" +
			"    where oi.uid = #{orgUid} \n" +
			"\n" +
			"    union\n" +
			"\n" +
			"    select oi.*\n" +
			"    from org_info oi\n" +
			"    join cte_org cte on oi.parent_uid = cte.uid\n" +
			") select * from cte_org")
	List<OrgInfo> listOrgHierarchyUnderCurrentOrg(@Param("orgUid") String orgUid);

	@Select("with recursive cte_org as (\n" +
			"    select oi.*\n" +
			"    from org_info oi\n" +
			"    where oi.uid = #{orgUid} and oi.org_type = 4\n" +
			"\n" +
			"    union\n" +
			"\n" +
			"    select oi.*\n" +
			"    from org_info oi\n" +
			"    join cte_org cte on oi.uid = cte.parent_uid and oi.org_type = 4\n" +
			") select * from cte_org")
	List<OrgInfo> listParentClinicalDeptsWithSelectedClinicalDept(@Param("orgUid") String orgUid);

	@Select("select uid, name, org_type, org_level, parent_uid from org_info where uid = #{orgUid}")
	OrgInfo getByUid(@Param("orgUid") String orgUid);

}

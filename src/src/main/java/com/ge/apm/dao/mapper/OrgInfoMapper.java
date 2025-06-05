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

	@Select({
			"<script>" +
					"select oi.* from org_info oi join asset_info ai on oi.uid = ai.clinical_dept_uid " +
					"where ai.is_checked = TRUE AND ai.is_deleted = FALSE AND ai.is_valid = TRUE AND ai.clinical_dept_uid in " +
					"<foreach item = 'item' index = 'index' collection = 'orgUidList' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach> group by oi.id"+
					"</script>"})
    List<OrgInfo> filterNoneAsset(@Param("orgUidList") List<String> orgUidList);
}

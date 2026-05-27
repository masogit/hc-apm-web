package com.ge.apm.dao.mapper;

import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.dto.UserOrgInfoDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOrgInfoMapper {

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
					"select o.*,oi.uid as orgInfoUid  from user_org o left join  org_info oi on  o.org_uid in (oi.tenant_uid,oi.institution_uid,oi.hospital_uid,oi.site_uid,oi.uid)   where o.user_uid in " +
					"<foreach item = 'item' index = 'index' collection = 'userUids' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"</script>"})
	List<UserOrgInfoDto> findAccessUidByUserUids(@Param("userUids") List<String> userUids);



	@Select("with recursive cte_org as (\n" +
			"    select oi.*\n" +
			"    from user_org o, org_info oi \n" +
			"    where oi.uid = #{orgInfoUid}   and o.user_uid=#{userUid}   \n" +
			"\n" +
			"    union\n" +
			"\n" +
			"    select oi.*\n" +
			"    from org_info oi\n" +
			"    join cte_org cte on oi.parent_uid = cte.uid\n" +
			") select * from cte_org")
	List<OrgInfo> findAccessUidByUserUidsAndOrgInfoUid(@Param("userUid") String userUid, @Param("orgInfoUid") String orgInfoUid);

	@Select({
			"<script>" +
					"select uo.*, ua.id userId from user_org uo, user_account ua where uo.user_uid = ua.uid and ua.id in " +
					"<foreach item = 'item' index = 'index' collection = 'userIds' open='(' separator=',' close=')'>" +
					"#{item}" +
					"</foreach>"+
					"</script>"})
	List<UserOrgInfo> findByUserIds(@Param("userIds") List<Integer> userIds);

}

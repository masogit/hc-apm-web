package com.ge.apm.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import com.ge.apm.dao.mapper.provider.UserAccountProvider;
import com.ge.apm.domain.SysRole;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserModel;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.UserRole;

public interface UserAccountMapper {
	@Select("select * from user_account")
	public List<UserAccount> getAllUser();
	
	@SelectProvider(type = UserAccountProvider.class,method = "getUserWechatId")
	public List<UserAccount> getUserWechatId(@Param("ids") List<Integer> ids);

	@Select("select ua.id user_id, ua.login_name,ua.name user_name,oi.name hospital_name from user_account ua left join "+
					"org_info oi on ua.hospital_id = oi.id where ua.id=#{userId}")
	public UserModel getUserAccoutByUserId(Integer userId);

	@Select("select id,role_desc from sys_role where id in(select role_id from user_role where user_id = #{userId})")
	public List<SysRole> getUserRoles(Integer userId);

	@Select("select id,name,wechat_id,login_name from user_account where id =#{id}")
	public UserAccount getUserById(Integer id);
	
	@Update("update user_account set wechat_id = null where wechat_id=#{openId}")
	public void updateUserWechatIdNull(String openId);

	@Select("select subscribe_user_id from message_subscriber where asset_id = #{asset_id} and is_receive_timeout_msg= true ")
	public List<Integer> getAssetSubscriber(Integer assetId);

	@Select("select user_id from user_role where role_id=#{sysRole} and user_id in (select id from user_account where hospital_id = "+
			"(select hospital_id from user_account where id=#{requestorId}))")
	public List<Integer> fetchDispaterUser(@Param("requestorId") Integer requestorId,@Param("sysRole") Integer sysRole);
	
	@Select("select id from user_account where wechat_id =#{wechatId}")
	public UserAccount getUserByWechatId(String wechatId);
	
	@Select("select id from user_account where login_name =#{loginName}")
	public UserAccount getUserByLoginName(String loginName);
	
	@Select("select id from user_account where uid =#{uid}")
	public UserAccount getUserByUId(String uid);

	@Insert("insert into user_account(telephone,site_id,hospital_id,hospital_uid,org_id,login_name,name,pwd_salt,password,institution_uid,site_uid,tenant_uid,org_level,org_uid,uid,email,is_super_admin,is_site_admin,is_local_admin,is_active,is_locked,wechat_id,created_by,created_date,password_error_count,active_time,name_py_code,last_modified_by,last_modified_date) "+
			 " values "+
			" (#{telephone},#{siteId},#{hospitalId},#{hospitalUID},#{orgInfoId},#{loginName},#{name},#{pwdSalt},#{password},#{institutionUID},#{siteUID},#{tenantUID},#{orgLevel},#{orgInfoUID},#{uid},#{email},false,false,false,#{isActive},#{isLocked},#{weChatId},#{createdBy},#{createdDate},0,#{activeTime},#{namePyCode},#{lastModifiedBy},#{lastModifiedDate})")
	@Options(useGeneratedKeys=true)
	public Integer saveUserAccount(UserAccount user);
	
	@Update("update user_account set site_id=#{siteId},hospital_id=#{hospitalId},tenant_uid=#{tenantUID},institution_uid=#{institutionUID},hospital_uid=#{hospitalUID},site_uid=#{siteUID},telephone=#{telephone},org_id=#{orgInfoId},login_name=#{loginName},name=#{name},org_uid=#{orgInfoUID},email=#{email},is_active=#{isActive},is_locked=#{isLocked},last_modified_by=#{lastModifiedBy},last_modified_date=#{lastModifiedDate},password_error_count=#{passwordErrorCount},active_time=#{activeTime},name_py_code=#{namePyCode} where uid=#{uid}")
	public void updateUserAccount(UserAccount user);

	@Insert("insert into user_role(user_id,role_id) values (#{userId},#{roleId})")
	public void saveUserRole(UserRole userRole);
	
	@Delete("delete from user_role where user_id =#{userId}")
	public void deleteUserRole(UserRole userRole);
	
	@Insert("insert into user_org(id,org_uid,user_uid,org_type) values (#{id},#{orgUid},#{userUid},#{orgType})")
	public void saveUserOrgInfo(UserOrgInfo userOrgInfo);
	
	@Delete("delete from user_org where user_uid =#{userUid}")
	public void deleteUserOrgInfo(UserOrgInfo userOrgInfo);
} 

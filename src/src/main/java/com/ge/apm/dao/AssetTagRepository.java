package com.ge.apm.dao;

import com.ge.apm.domain.AssetTag;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 212605082 on 2017/5/12.
 */
public interface AssetTagRepository extends GenericRepository<AssetTag> {
     //当前用户没有关注过的标签
    @Query(value = "select * from asset_tag where id=-999 or tenant_uid=?1  and id not in (select tag_id from asset_tag_msg_subscriber where subscribe_user_id=?2)",nativeQuery =true)
    public List<AssetTag> getUnSubscriberTag(String tenantUID, int userId);
    
    //@Query(value = "select * from asset_tag where id=-999 or site_uid=?1",nativeQuery =true)
    @Query(value = "select tag.* from asset_tag tag, user_account ua where  (ua.site_uid =tag.tenant_uid or ua.site_uid =tag.institution_uid or ua.site_uid =tag.hospital_uid or ua.site_uid =tag.site_uid) and ua.id=?1 union select * from asset_tag where id =-999",nativeQuery =true)
    public List<AssetTag> getUnSubscriberTagBySiteUID(int userId);


    @Query(value = "select * from asset_tag where site_uid=?1 or id=-999",nativeQuery =true)
    public List<AssetTag> getTagBySite(String siteUID);

}

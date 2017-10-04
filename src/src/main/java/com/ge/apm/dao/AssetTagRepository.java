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
    @Query(value = "select * from asset_tag where hospital_id=?1 or hospital_id is null and id not in (select tag_id from asset_tag_msg_subscriber where subscribe_user_id=?2)",nativeQuery =true)
    public List<AssetTag> getUnSbuscriberTag(int hospitalId, int userId);
}

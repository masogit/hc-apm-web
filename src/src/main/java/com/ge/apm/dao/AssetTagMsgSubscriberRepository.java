package com.ge.apm.dao;


import com.ge.apm.domain.AssetTag;
import com.ge.apm.domain.AssetTagMsgSubscriber;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import webapp.framework.dao.GenericRepository;

import java.util.Date;
import java.util.List;

public interface AssetTagMsgSubscriberRepository extends GenericRepository<AssetTagMsgSubscriber> {


    @Query("select o from AssetTagMsgSubscriber o where o.subscribeUserId = ?1")
    public List<AssetTagMsgSubscriber> getAssetTagMsgSubscriber(Integer userId);

    public AssetTagMsgSubscriber getByTagId(Integer tagId);

    @Query(value = "select * from Asset_Tag_Msg_Subscriber",nativeQuery = true)
    public List<AssetTagMsgSubscriber> getAssetTagMsgSubscriberAll();


    @Query(value = "select * from Asset_Tag_Msg_Subscriber where tag_id=?1 and subscribe_user_id=?2",nativeQuery = true)
    public List<AssetTagMsgSubscriber> getTagUserMode(Integer tagId,Integer userId);

    //有没有tagId 和subscriber 并且 mode is 3的
    @Query(value = "select * from Asset_Tag_Msg_Subscriber where tag_id=?1 and subscribe_user_id=?2 and receive_msg_mode = 3",nativeQuery = true)
    public List<AssetTagMsgSubscriber> getTagUserMode3(Integer tagId,Integer userId);

    ////有没有tagId 和subscriber 并且 mode is 0 ,1 ,2的
    @Query(value = "select * from Asset_Tag_Msg_Subscriber where tag_id=?1 and subscribe_user_id=?2 and (receive_msg_mode = 0 or receive_msg_mode = 1 or receive_msg_mode = 2)",nativeQuery = true)
    public List<AssetTagMsgSubscriber> getTagUserMode012(Integer tagId,Integer userId);
}


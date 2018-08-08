package com.ge.apm.dao;

import com.ge.apm.domain.MessageSubscriber;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface MessageSubscriberRepository extends GenericRepository<MessageSubscriber> {

    @Query("select o from MessageSubscriber o where o.assetId = ?1 and o.subscribeUserId = ?2")
    public MessageSubscriber getByAssetIdAndUserId(Integer assetId, Integer userId);
    
    public List<MessageSubscriber> getByAssetIdAndReceiveMsgModeIn(Integer assetId, Collection<Integer> c);
    
    public List<MessageSubscriber> getByAssetIdAndReceiveMsgMode(Integer assetId, Integer msgMode);

    @Query(value="select ms.* from message_subscriber ms where ms.asset_id in ?1 and ms.subscribe_user_id=?2",nativeQuery = true)
    public  List<MessageSubscriber> getMsgListByAssetIdAndUserId(Collection<Integer> assetId, Integer userId);


}

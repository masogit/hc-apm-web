package com.ge.apm.dao;

import com.ge.apm.domain.I18nMessage;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

public interface I18nMessageRepository extends GenericRepository<I18nMessage> {
    @Query("select o from I18nMessage o")
    public List<I18nMessage> getI18nMessages();

    public List<I18nMessage> getByMsgTypeAndSiteIdAndMsgKey(String msgType, Integer siteId, String msgKey);

    public List<I18nMessage> getByMsgType(String msgType);

    public I18nMessage getByMsgTypeAndMsgKey(String msgType, String msgKey);

    @Query("select i18n from I18nMessage i18n where i18n.msgType='measuringItemCategory' and i18n.siteId=-1 and i18n.msgKey not in (select mtc.measuringItemCategory from MeasuringTypeConfig mtc where mtc.tenantUID=?1)")
    List<I18nMessage> findI18nMessageByMeasuringAndTenantUID(String tenantUID);

    @Query(value = "select i18n.* from I18n_Message i18n where msg_type='focusOption' and msg_key != '3'",nativeQuery = true)
    List<I18nMessage> getFousOptionWithOutEE();

    /* @Query(value="select im.* from i18n_message im where exists (select * from asset_type_faulty where asset_type_id = ?1 and im.id= fault_id and hospital_id=?2 and site_id=?3)", nativeQuery = true)
    public List<I18nMessage> getFaultTypeByAssetType(Integer astypeId);*/
}

package com.ge.apm.dao;

import com.ge.apm.domain.AssetInfo;
import com.ge.apm.domain.QrCodeLib;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepository;

/**
 * Created by 212605082 on 2017/3/22.
 */
public interface QrCodeLibRepository extends GenericRepository<QrCodeLib> {

    @Query("select a from QrCodeLib a where a.qrCode=:qrCodeLib and a.siteUID =:siteUid    ")
    public QrCodeLib findByQrCode(@Param("qrCodeLib")String qrCodeLib,@Param("siteUid")String siteUid);
    
     @Query("select a from QrCodeLib a where a.uid is null or  a.uid='' ")
    public List<QrCodeLib> getNeedUpdateUidEntity();

}

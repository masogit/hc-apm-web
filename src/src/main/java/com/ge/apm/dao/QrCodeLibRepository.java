package com.ge.apm.dao;

import com.ge.apm.domain.QrCodeLib;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import webapp.framework.dao.GenericRepository;

import java.util.List;

/**
 * Created by 212605082 on 2017/3/22.
 */
public interface QrCodeLibRepository extends GenericRepository<QrCodeLib> {

    @Query("select a from QrCodeLib a where a.qrCode=:qrCodeLib and a.siteUID =:siteUid    ")
    public QrCodeLib findByQrCode(@Param("qrCodeLib")String qrCodeLib,@Param("siteUid")String siteUid);

    @Query("select a from QrCodeLib a where a.qrCode=:qrCodeLib ")
    public QrCodeLib findByQrCode(@Param("qrCodeLib")String qrCodeLib);
    
     @Query("select a from QrCodeLib a where a.uid is null or  a.uid='' ")
    public List<QrCodeLib> getNeedUpdateUidEntity();

    @Transactional
    @Modifying
    @Query(value = "delete from qr_code_lib where uid = ?1",nativeQuery = true)
    void deleteQrCodeLib(String uid);
}

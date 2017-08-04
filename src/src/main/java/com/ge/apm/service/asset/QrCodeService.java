/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ge.apm.service.asset;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.dao.QrCodeLibRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.QrCodeLib;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author 212579464
 */
@Component
public class QrCodeService {

    @Autowired
    QrCodeLibRepository qrCodeLibDao;

    @Autowired
    OrgInfoRepository orgInfoDao;

    public QrCodeLib createOneQrCode(String tenantUID, String siteUID,Integer status) {
        if (tenantUID == null || siteUID == null) {
            return null;
        }
        OrgInfo currentOrg = orgInfoDao.getByUid(siteUID);
        return createOneQrCode(currentOrg,status);
    }

    public QrCodeLib createOneQrCode(OrgInfo currentOrg, Integer status) {
        if (currentOrg == null) {
            return null;
        }
        String dtStr = new DateTime().toString("yyMMdd");
        String qrCode = dtStr + Long.valueOf((long) ((Math.random() * 9 + 1) * 1000000000L));
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode);
        if (null == qrCodeLib) {
            qrCodeLib = new QrCodeLib();
            qrCodeLib.setSiteId(currentOrg.getSiteId());
            qrCodeLib.setHospitalId(currentOrg.getHospitalId());

            qrCodeLib.setSiteUID(currentOrg.getSiteUID());
            qrCodeLib.setHospitalUID(currentOrg.getHospitalUID());
            qrCodeLib.setTenantUID(currentOrg.getTenantUID());
            qrCodeLib.setInstitutionUID(currentOrg.getInstitutionUID());
            /*-- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)*/
            qrCodeLib.setStatus(status);
            qrCodeLib.setIssueDate(new Date());
            qrCodeLib.setQrCode(qrCode);
            qrCodeLibDao.save(qrCodeLib);
        }
        return qrCodeLib;
    }
    
    public List<QrCodeLib> createManyQrCode(String tenantUID, String siteUID,Integer count){
        if (tenantUID == null || siteUID == null) {
            return null;
        }
        OrgInfo currentOrg = orgInfoDao.getByUid(siteUID);
        
        List<QrCodeLib> result = new ArrayList();
        for(int i=0;i<count;i++){
            result.add(createOneQrCode(currentOrg,1));
        }
        return result;
    }

    public void discardQrCode(String qrCode) {
        QrCodeLib qrCodeLib = qrCodeLibDao.findByQrCode(qrCode);
        qrCodeLib.setStatus(4);
        qrCodeLibDao.save(qrCodeLib);
    }

}

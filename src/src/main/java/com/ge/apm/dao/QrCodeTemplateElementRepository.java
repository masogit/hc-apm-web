package com.ge.apm.dao;

import com.ge.apm.domain.QrCodeTemplateElement;
import java.util.List;
import webapp.framework.dao.GenericRepositoryUUID;


/**
 * Created by 212579464
 */
public interface QrCodeTemplateElementRepository extends GenericRepositoryUUID<QrCodeTemplateElement> {

    public List<QrCodeTemplateElement> findByTemplateId(String templateId);
//    
//     @Query("select a from QrCodeLib a where a.uid is null or  a.uid='' ")
//    public List<QrCodeLib> getNeedUpdateUidEntity();

}

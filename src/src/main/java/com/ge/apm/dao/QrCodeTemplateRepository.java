package com.ge.apm.dao;

import com.ge.apm.domain.QrCodeTemplate;
import org.springframework.data.jpa.repository.Query;
import webapp.framework.dao.GenericRepositoryUUID;


/**
 * Created by 212579464
 */
public interface QrCodeTemplateRepository extends GenericRepositoryUUID<QrCodeTemplate> {

    @Query("select a from QrCodeTemplate a where a.isUsing=true and  a.siteUID=?1 ")
    public QrCodeTemplate findSiteTemplate(String siteUID);

}

package com.ge.apm.service.systemTempate;


import com.ge.apm.dao.SystemTemplateValueRepository;
import com.ge.apm.dao.mapper.SystemTemplateMapper;
import com.ge.apm.domain.SystemTemplate;
import com.ge.apm.domain.SystemTemplateValue;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.asset.BlobFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class SystemTemplateService {



    @Autowired
    private SystemTemplateMapper systemTemplateMapper;

    @Autowired
    private SystemTemplateValueRepository systemTemplateValueRepository;

    @Autowired
    private BlobFileService blobService;


    public List<SystemTemplate> findByKeyAndSiteForUseParent(String templateType,UserAccount userAccount){

        return systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,userAccount.getSiteUID(),userAccount.getHospitalUID(),userAccount.getInstitutionUID(),userAccount.getTenantUID());
    }

    public List<SystemTemplateValue> getOrgInfoSubSystemTemplate(String tenantUID,String templateType) {
        List<SystemTemplateValue> systemTemplateValues=  systemTemplateValueRepository.findByTenantUidAndTemplateType(tenantUID,templateType);
        return systemTemplateValues;
    }


    public List<SystemTemplate> findByKeyAndSiteForUseParent(String templateType, String siteUid, String hospitalUid,  String institutionUid,  String tenantUid){

        return systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,siteUid,hospitalUid,institutionUid,tenantUid);
    }
    public byte[] getTemplateFileByte(SystemTemplate.SystemTemplateEnum templateEnum , UserAccount user) throws IOException {
        List<SystemTemplate> byKeyAndSiteForUseParent = this.findByKeyAndSiteForUseParent(templateEnum.getCode(), user);
        if(CollectionUtils.isNotEmpty(byKeyAndSiteForUseParent)){
            SystemTemplate systemTemplate = byKeyAndSiteForUseParent.get(0);
            String  storageId= systemTemplate.getValueStorageId()!=null?systemTemplate.getValueStorageId():systemTemplate.getStorageId();
            return IOUtils.toByteArray(blobService.getBlobObjectByStorageId(storageId).getStream());
        }
        return null;
    }


    public InputStream getTemplateInputStream(SystemTemplate.SystemTemplateEnum templateEnum , UserAccount user) throws IOException {
        List<SystemTemplate> byKeyAndSiteForUseParent = this.findByKeyAndSiteForUseParent(templateEnum.getCode(), user);
        if(CollectionUtils.isNotEmpty(byKeyAndSiteForUseParent)){
            SystemTemplate systemTemplate = byKeyAndSiteForUseParent.get(0);
            String  storageId= systemTemplate.getValueStorageId()!=null?systemTemplate.getValueStorageId():systemTemplate.getStorageId();
            return blobService.getBlobObjectByStorageId(storageId).getStream();
        }
        return null;
    }

    public boolean existsSiteTemplate(String templateType, String siteUid) {
       List<SystemTemplateValue> list= systemTemplateValueRepository.findBySiteUidAndTemplateType(siteUid,templateType);
        return list != null && !list.isEmpty();
    }
}

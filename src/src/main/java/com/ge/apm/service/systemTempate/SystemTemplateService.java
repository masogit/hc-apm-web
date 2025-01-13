package com.ge.apm.service.systemTempate;


import com.ge.apm.dao.SystemTemplateValueRepository;
import com.ge.apm.dao.mapper.SystemTemplateMapper;
import com.ge.apm.domain.SystemTemplate;
import com.ge.apm.domain.SystemTemplateValue;
import com.ge.apm.domain.UserAccount;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemTemplateService {



    @Autowired
    private SystemTemplateMapper systemTemplateMapper;

    @Autowired
    private SystemTemplateValueRepository systemTemplateValueRepository;


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
}

package com.ge.apm.service.systemTempate;


import com.ge.apm.dao.mapper.SystemTemplateMapper;
import com.ge.apm.domain.SystemTemplate;
import com.ge.apm.domain.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SystemTemplateService {



    @Autowired
    private SystemTemplateMapper systemTemplateMapper;


    public List<SystemTemplate> findByKeyAndSiteForUseParent(String templateType,UserAccount userAccount){

        return systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,userAccount.getSiteUID(),userAccount.getHospitalUID(),userAccount.getInstitutionUID(),userAccount.getTenantUID());
    }
}

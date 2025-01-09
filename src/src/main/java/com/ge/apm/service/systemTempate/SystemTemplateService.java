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

        List<SystemTemplate> systemTemplates=systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,userAccount.getSiteUID());
        if(CollectionUtils.isEmpty(systemTemplates) ||   systemTemplates.stream().anyMatch(item->item.getValueId()==null)){
            systemTemplates=systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,userAccount.getHospitalUID());
            if(CollectionUtils.isEmpty(systemTemplates) ||   systemTemplates.stream().anyMatch(item->item.getValueId()==null)){
                systemTemplates=systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,userAccount.getInstitutionUID());
            }
            if(CollectionUtils.isEmpty(systemTemplates) ||   systemTemplates.stream().anyMatch(item->item.getValueId()==null)){
                systemTemplates=systemTemplateMapper.findByKeyAndSiteForUseParentList(templateType,userAccount.getTenantUID());
            }
        }
        return systemTemplates;
    }
}

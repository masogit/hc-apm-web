package com.ge.apm.service.pm;

import com.ge.apm.dao.PmOrderPreCreateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PmOrderPreCreateService {

    @Autowired
    private PmOrderPreCreateRepository pmOrderPreCreateRepository;
    public void deleteBySiteUid(String  siteUid) {


        pmOrderPreCreateRepository.deleteBySiteUid(siteUid);
    }

    public void deleteByPmOrderRuleId(String ruleUid) {
        pmOrderPreCreateRepository.deleteByPmOrderRuleId(ruleUid);
    }
}

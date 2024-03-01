package com.ge.apm.service.his;

import com.ge.apm.dao.HisAdministrativeOfficesMatchRepository;
import com.ge.apm.dao.HisAdministrativeOfficesRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.his.HisAdministrativeOffices;
import com.ge.apm.domain.his.HisAdministrativeOfficesMatch;
import com.ge.apm.service.utils.ExcelDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HisClinicalService {

    @Autowired
    private HisAdministrativeOfficesMatchRepository matchDao;
    @Autowired
    private HisAdministrativeOfficesRepository hisDeptDao;

    @Transactional
    public void match(OrgInfo source, HisAdministrativeOffices target) {
        HisAdministrativeOfficesMatch match = new HisAdministrativeOfficesMatch();
        match.setHisKsname(target.getHisKsname());
        match.setHisKsid(target.getHisKsid());
        match.setHisAdmId(target.getId());
        match.setHosId(target.getHosId());
        match.setOrgInfoUid(source.getUid());
        match.setOrgName(source.getName());
        match.setNamePyCode(source.getNamePyCode());
        match.setCreateDate(new Date());
        match.setTenantUid(target.getTenantUid());
        match.setSiteUid(target.getSiteUid());
        matchDao.save(match);

        target.setIsMatched(true);
        hisDeptDao.save(target);
    }

    @Transactional
    public void unmatch(String matchId) {
        HisAdministrativeOfficesMatch match = matchDao.findById(matchId);
        HisAdministrativeOffices office = hisDeptDao.findById(match.getHisAdmId());
        office.setIsMatched(false);
        hisDeptDao.save(office);
        matchDao.deleteById(matchId);
    }
}

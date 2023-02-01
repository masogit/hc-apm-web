package com.ge.apm.service.his.load;

import com.ge.apm.dao.OrgInfoRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.ExcelDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HisOrgInfoImportService {

    public final int ORG_INFO_ID_MINIMUM = 1_000_000_000;

    @Autowired
    OrgInfoRepository orgInfoRepository;

    public List<OrgInfo> parse(ExcelDocument doc, UserAccount currentUser){
        if (null == currentUser) {
            throw new IllegalArgumentException("没有找到当前用户");
        }

        List<OrgInfo> resultList = new ArrayList<>();

        int keyRow = 1;
        int dataRow = 2;
        for (Map< String, Object> rowMap = doc.getDataRowMap(keyRow, dataRow); rowMap != null; rowMap = doc.getDataRowMap(keyRow, ++dataRow)) {
            Integer id = ExcelDocument.getCellIntegerValue(rowMap.get("id"));
            String uid = ExcelDocument.getCellStringValue(rowMap.get("uid"));
            String name = ExcelDocument.getCellStringValue(rowMap.get("name"));
            String nameEn = ExcelDocument.getCellStringValue(rowMap.get("name_en"));
            String namePyCode = ExcelDocument.getCellStringValue(rowMap.get("name_py_code"));
            String shortName = ExcelDocument.getCellStringValue(rowMap.get("short_name"));
            String shortNamePyCode = ExcelDocument.getCellStringValue(rowMap.get("short_name_py_code"));
            String orgCode = ExcelDocument.getCellStringValue(rowMap.get("org_code"));

            Integer orgType = ExcelDocument.getCellIntegerValue(rowMap.get("org_type"));
            Integer orgLevel = ExcelDocument.getCellIntegerValue(rowMap.get("org_level"));

            Integer parentId = ExcelDocument.getCellIntegerValue(rowMap.get("parent_id"));
            String parentUid = ExcelDocument.getCellStringValue(rowMap.get("parent_uid"));

            String siteUid = ExcelDocument.getCellStringValue(rowMap.get("site_uid"));
            String hospitalUid = ExcelDocument.getCellStringValue(rowMap.get("hospital_uid"));
            String institutionUid = ExcelDocument.getCellStringValue(rowMap.get("institution_uid"));

            Integer hospitalId = ExcelDocument.getCellIntegerValue(rowMap.get("hospital_id"));

            if (null == id) {
                break;
            }

            OrgInfo data = new OrgInfo();

            data.setId(id + ORG_INFO_ID_MINIMUM);
            data.setUid(reverse(uid));
            data.setName(name);
            data.setNameEn(nameEn);
            data.setNamePyCode(namePyCode);
            data.setShortName(shortName);
            data.setShortNamePyCode(shortNamePyCode);
            data.setOrgCode(orgCode);

            data.setOrgLevel(orgLevel);
            data.setOrgType(orgType);

            if (null != parentId) {
                data.setParentID(parentId + ORG_INFO_ID_MINIMUM);
                data.setParentUID(reverse(parentUid));
            }

            data.setSiteUID(reverse(siteUid));
            data.setHospitalUID(reverse(hospitalUid));
            data.setInstitutionUID(reverse(institutionUid));
            data.setTenantUID(currentUser.getTenantUID());

            data.setSiteId(currentUser.getSiteId());
            if (null != hospitalId) {
                data.setHospitalId(hospitalId + ORG_INFO_ID_MINIMUM);
            }



            resultList.add(data);
        }

        return resultList;
    }

    public List<OrgInfo> saveAll(List<OrgInfo> data){
        List<OrgInfo> resultList = new ArrayList<>();

        for (OrgInfo element: data) {
            resultList.add(save(element));
        }

        return resultList;
    }

    @Transactional
    public OrgInfo save(OrgInfo element) {
        return orgInfoRepository.save(element);
    }

    private String reverse(String source) {
        String result = new StringBuilder(source).reverse().toString();
        return result;
    }


}

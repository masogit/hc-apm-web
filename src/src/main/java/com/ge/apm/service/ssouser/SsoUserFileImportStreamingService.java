package com.ge.apm.service.ssouser;

import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.dao.UserOrgInfoRepository;
import com.ge.apm.dao.UserRoleRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.domain.UserOrgInfo;
import com.ge.apm.domain.UserRole;
import com.ge.apm.service.utils.ExcelDocument;
import com.ge.apm.service.utils.ExcelDocumentStreamingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author 212579464
 */
@Service
@Scope("prototype")
public class SsoUserFileImportStreamingService {

    public static final int EXCEL_SSO_USER_HEADER_ROW = 1;
    public static final int EXCEL_SSO_USER_DATA_ROW = 2;
    public static final String EXCEL_SSO_USER_TEMPLATE_VERSION = "v1.0";

    public enum ImportStatus {
        New, Exist, Created, Failure, CheckFail
    }

    @Autowired
    UserAccountRepository repository;
    @Autowired
    UserOrgInfoRepository userOrgInfoRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    private static final Logger logger = LoggerFactory.getLogger(SsoUserFileImportStreamingService.class);

    public List<Map<String, Object>> getUserMapFromTemplate(ExcelDocumentStreamingReader doc, OrgInfo orgInfo, Integer roleId) throws Exception {

        LocalDateTime startTime = LocalDateTime.now();

        List<Map<String, Object>> result = new ArrayList<>();

        int dataRow = EXCEL_SSO_USER_DATA_ROW;

        Iterator<Map<String, ExcelDocument.ValueWrapper>> iterator = doc.iterator();
        while (iterator.hasNext()) {
            Map<String, ExcelDocument.ValueWrapper> rowMap = iterator.next();
            try {
                Map<String, Object> map = new LinkedHashMap<>();

                UserAccount userAccount;

                UserAccount byLoginName = null;
                String sso = ExcelDocument.getCellStringValue(rowMap.get("SSO"));
                if (!StringUtils.isEmpty(sso)) {
                    byLoginName = repository.getByLoginName("ge" + sso);
                }
                ImportStatus status = ImportStatus.New;
                if (byLoginName != null) {
                    userAccount = byLoginName;
                    status = ImportStatus.Exist;
                } else {
                    userAccount = new UserAccount();
                    if (!StringUtils.isEmpty(sso)) {
                        userAccount.setGeSso(sso);
                        userAccount.setLoginName("ge" + sso);
                    }
                    String name = ExcelDocument.getCellStringValue(rowMap.get("NAME"));
                    if (!StringUtils.isEmpty(name)) {
                        userAccount.setName(name);
                    }
                    if (StringUtils.isEmpty(sso) || StringUtils.isEmpty(name)) {
                        status = ImportStatus.CheckFail;
                    }
                }
                if (status.equals(ImportStatus.New)) {
                    userAccount.setPlainPassword(getRandomPwd());
                    userAccount.entryptPassword();
                    userAccount.setPlainPassword(null);
                    userAccount.setOrgInfoId(orgInfo.getId());
                    userAccount.setOrgInfoUID(orgInfo.getInstitutionUID());
                    userAccount.setHospitalId(orgInfo.getHospitalId());
                    userAccount.setSiteId(orgInfo.getSiteId());
                    userAccount.setTenantUID(orgInfo.getTenantUID());
                    userAccount.setInstitutionUID(orgInfo.getInstitutionUID());
                    userAccount.setHospitalUID(orgInfo.getHospitalUID());
                    userAccount.setSiteUID(orgInfo.getSiteUID());
                    userAccount.setIsActive(true);
                    userAccount.setOrgLevel(orgInfo.getOrgLevel());
                    repository.save(userAccount);
                    UserOrgInfo userOrgInfo = new UserOrgInfo();
                    userOrgInfo.setOrgType(orgInfo.getOrgType());
                    userOrgInfo.setUserUid(userAccount.getUid());
                    userOrgInfo.setOrgUid(userAccount.getOrgInfoUID());
                    userOrgInfoRepository.save(userOrgInfo);
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userAccount.getId());
                    userRole.setRoleId(roleId);
                    userRole.setRoleType(0);
                    userRoleRepository.save(userRole);
                }
                userAccount.setLineNum(dataRow);
                map.put("user", userAccount);
                map.put("status", status);
                map.put("rowData", rowMap);

                result.add(map);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new Exception("第" + dataRow + "行数据错误:" + e.getMessage());
            }

            ++dataRow;
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        logger.info("Duration for getUserMapFromTemplate: {} seconds", duration.getSeconds());

        return result;
    }


    public boolean checkDocVersion(ExcelDocumentStreamingReader doc) {
        return EXCEL_SSO_USER_TEMPLATE_VERSION.equalsIgnoreCase(doc.getVersion());
    }

    private String getRandomPwd() {
        return UUID.randomUUID().toString().substring(20);
    }
}

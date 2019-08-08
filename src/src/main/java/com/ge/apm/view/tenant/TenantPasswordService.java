package com.ge.apm.view.tenant;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.service.utils.PasswordHashUtil;
import com.ge.apm.service.utils.PasswordUtil;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TenantPasswordService {

    @Autowired
    TenantInfoRepository dao;
    @Autowired
    UserAccountRepository userAccountDao;

    @Transactional
    public void updateUserAccountPasswordWithTenantPassword(String tenantUid) throws DecoderException, NoSuchAlgorithmException {

        TenantInfo tenantInfo = this.dao.findByuid(tenantUid);

        List<UserAccount> userAccountList = userAccountDao.findByTenantUID(tenantUid);

        for (UserAccount userAccount: userAccountList) {
            String hashPassword1 = PasswordHashUtil.hashPassword(PasswordUtil.DEFAULT_PWD, userAccount.getPwdSalt());
            String hashPassword2 = PasswordHashUtil.hashPassword(PasswordUtil.DEFAULT_PWD2, userAccount.getPwdSalt());

            if (userAccount.getPassword().equals(hashPassword1) || userAccount.getPassword().equals(hashPassword2)) {
                userAccount.setPassword(tenantInfo.getInitialPassword());
                userAccount.setPwdSalt(tenantInfo.getInitialPasswordSalt());
                userAccountDao.save(userAccount);
            }
        }

    }

    @Transactional
    public List<UserAccount> listUserAccountsWithDefaultPassword(String tenantUid) throws DecoderException, NoSuchAlgorithmException {
        List<UserAccount> resultList = new ArrayList<>();

        TenantInfo tenantInfo = this.dao.findByuid(tenantUid);

        List<UserAccount> userAccountList = userAccountDao.findByTenantUID(tenantUid);

        for (UserAccount userAccount: userAccountList) {
            String hashPassword1 = PasswordHashUtil.hashPassword(PasswordUtil.DEFAULT_PWD, userAccount.getPwdSalt());
            String hashPassword2 = PasswordHashUtil.hashPassword(PasswordUtil.DEFAULT_PWD2, userAccount.getPwdSalt());

            if (userAccount.getPassword().equals(hashPassword1) || userAccount.getPassword().equals(hashPassword2)) {
                resultList.add((userAccount));
            }
        }

        return resultList;
    }

}
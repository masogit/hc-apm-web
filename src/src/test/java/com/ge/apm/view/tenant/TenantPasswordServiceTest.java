package com.ge.apm.view.tenant;

import com.ge.apm.dao.TenantInfoRepository;
import com.ge.apm.dao.UserAccountRepository;
import com.ge.apm.domain.OrgInfo;
import com.ge.apm.domain.TenantInfo;
import com.ge.apm.domain.UserAccount;
import com.ge.apm.mock.FacesContextTestExecutionListener;
import com.ge.apm.service.utils.PasswordHashUtil;
import com.ge.apm.service.utils.PasswordUtil;
import org.apache.commons.codec.DecoderException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/config/applicationContext.xml"})
@TestExecutionListeners(listeners = {FacesContextTestExecutionListener.class, TransactionalTestExecutionListener.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public class TenantPasswordServiceTest {

    @Autowired
    TenantPasswordService tenantPasswordService;

    @Autowired
    UserAccountRepository userAccountDao;

    @Autowired
    TenantInfoRepository tenantInfoDao;

    TenantInfo tenantInfoA;
    TenantInfo tenantInfoB;

    @Before
    @Transactional
    public void setup() {
        clean();

        tenantInfoA = new TenantInfo();
        tenantInfoA.initializeUid();
        tenantInfoA.setName("AutomationTest-Tenant A");
        tenantInfoA.setPlainPassword("tenant-a-test-password");
        tenantInfoDao.save(tenantInfoA);

        tenantInfoB = new TenantInfo();
        tenantInfoB.initializeUid();
        tenantInfoB.setName("AutomationTest-Tenant B");
        tenantInfoB.setPlainPassword("tenant-b-test-password");
        tenantInfoDao.save(tenantInfoB);

    }

//    @After
    @Transactional
    public void tearDown() {
        clean();

    }

    private void clean() {
        int deleted;

        if (tenantInfoA != null && tenantInfoA.getUid() != null) {
            deleted = userAccountDao.deleteUserAccountsByTenantUID(tenantInfoA.getUid());
            System.out.println("tenant A deleted " + deleted);

            TenantInfo tenantInfoA1 = tenantInfoDao.findByuid(tenantInfoA.getUid());
            tenantInfoDao.delete(tenantInfoA1);
        }

        if (tenantInfoB != null && tenantInfoB.getUid() != null) {
            deleted = userAccountDao.deleteUserAccountsByTenantUID(tenantInfoB.getUid());
            System.out.println("Tenant B deleted " + deleted);

            TenantInfo tenantInfoB1 = tenantInfoDao.findByuid(tenantInfoB.getUid());
            tenantInfoDao.delete(tenantInfoB1);
        }




    }

    @Test
    @Transactional
    public void updateUserAccountPasswordWithTenantPassword() throws DecoderException, NoSuchAlgorithmException {
        UserAccount userA1 = buildUserAccount("AutomationTest-User-A1", PasswordUtil.DEFAULT_PWD, tenantInfoA.getUid());
        userAccountDao.save(userA1);

        UserAccount userA2 = buildUserAccount("AutomationTest-User-A2", PasswordUtil.DEFAULT_PWD, tenantInfoA.getUid());
        userAccountDao.save(userA2);

        UserAccount userA3 = buildUserAccount("AutomationTest-User-A3", "user-test-password", tenantInfoA.getUid());
        userAccountDao.save(userA3);

        int count = userAccountDao.countUserAccountsByTenantUID(tenantInfoA.getUid());
        assertEquals(3, count);

        List<UserAccount> userAccountList = tenantPasswordService.listUserAccountsWithDefaultPassword(tenantInfoA.getUid());
        assertEquals(2, userAccountList.size());

        tenantPasswordService.updateUserAccountPasswordWithTenantPassword(tenantInfoA.getUid());

        userAccountList = tenantPasswordService.listUserAccountsWithDefaultPassword(tenantInfoA.getUid());
        assertEquals(0, userAccountList.size());

        count = userAccountDao.countUserAccountsByTenantUID(tenantInfoA.getUid());
        assertEquals(3, count);

        UserAccount userA3Copy = userAccountDao.getByLoginName("AutomationTest-User-A3");

        assertEquals(userA3Copy.getPassword(), PasswordHashUtil.hashPassword("user-test-password", userA3Copy.getPwdSalt()));

    }


    @Test
    @Transactional
    public void listUserAccountsWithDefaultPassword() throws DecoderException, NoSuchAlgorithmException {

        UserAccount userA1 = buildUserAccount("AutomationTest-User-A1", PasswordUtil.DEFAULT_PWD, tenantInfoA.getUid());
        userAccountDao.save(userA1);

        UserAccount userA2 = buildUserAccount("AutomationTest-User-A2", PasswordUtil.DEFAULT_PWD, tenantInfoA.getUid());
        userAccountDao.save(userA2);

        UserAccount userA3 = buildUserAccount("AutomationTest-User-A3", "user-test-password", tenantInfoA.getUid());
        userAccountDao.save(userA3);

        List<UserAccount> userAccountList = tenantPasswordService.listUserAccountsWithDefaultPassword(tenantInfoA.getUid());
        assertEquals(2, userAccountList.size());
    }

    private UserAccount buildUserAccount(String name, String password, String tenantUid){
        UserAccount userAccount = new UserAccount();
        userAccount.initializeUid();
        userAccount.setName(name);
        userAccount.setPlainPassword(password);
        try {
            userAccount.entryptPassword();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        userAccount.setLoginName(name);
        userAccount.setTenantUID(tenantUid);
        userAccount.setOrgInfoId(-1);

        return userAccount;
    }
}
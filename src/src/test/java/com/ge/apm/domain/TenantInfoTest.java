package com.ge.apm.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class TenantInfoTest {

    @Test
    public void encryptPassword() {

        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setPlainPassword("JY2019@e");

        System.out.println(tenantInfo.getInitialPassword());
        System.out.println(tenantInfo.getInitialPasswordSalt());

        assertNotNull(tenantInfo.getInitialPassword());
        assertNotNull(tenantInfo.getInitialPasswordSalt());
    }

    @Test
    public void clearPassword() {

        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setPlainPassword("JY2019@e");

        assertNotNull(tenantInfo.getInitialPassword());
        assertNotNull(tenantInfo.getInitialPasswordSalt());

        tenantInfo.setPlainPassword(null);

        assertNull(tenantInfo.getInitialPassword());
        assertNull(tenantInfo.getInitialPasswordSalt());
    }
}
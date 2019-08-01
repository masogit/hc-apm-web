package com.ge.apm.domain;

import org.junit.Test;

import static org.junit.Assert.*;

public class TenantInfoTest {

    @Test
    public void encryptPassword() {

        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setPlainPassword("JY2019@e");
        tenantInfo.encryptPassword();
        System.out.println(tenantInfo.getInitialPassword());
        System.out.println(tenantInfo.getInitialPasswordSalt());

        assertNotNull(tenantInfo.getInitialPassword());
        assertNotNull(tenantInfo.getInitialPasswordSalt());
    }
}
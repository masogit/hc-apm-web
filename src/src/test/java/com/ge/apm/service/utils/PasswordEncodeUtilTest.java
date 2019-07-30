package com.ge.apm.service.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordEncodeUtilTest {

    @Test
    public void decrypt() {

        String password = "test";
        String encrypted = PasswordEncodeUtil.encrypt(password);
        String decrypted = PasswordEncodeUtil.decrypt(encrypted);

        assertEquals(password, decrypted);
    }

    @Test
    public void encrypt() {

        String password = "JY2019@e";
        String encrypted = PasswordEncodeUtil.encrypt(password);
        System.out.println(encrypted.length());


        assertTrue(encrypted.length() < 64);
    }
}
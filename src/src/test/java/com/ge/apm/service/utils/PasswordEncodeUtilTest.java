package com.ge.apm.service.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class PasswordEncodeUtilTest {

    @Test
    public void decrypt() {

        String mima = "test";
        String encrypted = PasswordEncodeUtil.encrypt(mima);
        String decrypted = PasswordEncodeUtil.decrypt(encrypted);

        assertEquals(mima, decrypted);
    }

    @Test
    public void encrypt() {

        String mima = "JY2019@e";
        String encrypted = PasswordEncodeUtil.encrypt(mima);
        System.out.println(encrypted.length());


        assertTrue(encrypted.length() < 64);
    }
}
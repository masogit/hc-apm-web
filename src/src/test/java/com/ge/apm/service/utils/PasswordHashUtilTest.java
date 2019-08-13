package com.ge.apm.service.utils;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class PasswordHashUtilTest {

    @Test
    public void hashPassword() throws DecoderException, NoSuchAlgorithmException {
        String password = PasswordHashUtil.hashPassword(PasswordUtil.DEFAULT_PWD, "b5c0f7054b32fb45");

        assertEquals("8693b2e8eab8570efbe86f68dab595f4c250b5b9c8c7f0a73a20089fa492f61d", password);
    }

    @Test
    public void hashPassword2() throws DecoderException, NoSuchAlgorithmException {
        System.out.println(Digests.decodeHex("b5c0f7054b32fb45").length);
        String password = PasswordHashUtil.hashPassword(PasswordUtil.DEFAULT_PWD2, "b5c0f7054b32fb45");

        assertEquals("8693b2e8eab8570efbe86f68dab595f4c250b5b9c8c7f0a73a20089fa492f61d", password);
    }
}
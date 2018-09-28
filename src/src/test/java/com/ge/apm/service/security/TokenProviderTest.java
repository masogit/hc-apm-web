package com.ge.apm.service.security;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class TokenProviderTest {

    @Test
    public void encode() throws UnsupportedEncodingException {

        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.encode("admin");

        assertNotNull(token);
    }

    @Test
    public void decode() throws UnsupportedEncodingException {

        TokenProvider tokenProvider = new TokenProvider();
        String token = tokenProvider.encode("test");

        String loginName = tokenProvider.verify(token);

        assertEquals("test", loginName);
    }
}
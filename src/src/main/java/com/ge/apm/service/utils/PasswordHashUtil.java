package com.ge.apm.service.utils;

import com.ge.apm.domain.UserAccount;
import org.apache.commons.codec.DecoderException;

import java.security.NoSuchAlgorithmException;

public class PasswordHashUtil {
    public static String hashPassword(String plainPassword, String salt) throws DecoderException, NoSuchAlgorithmException {

        byte[] hashPassword = new byte[0];
        hashPassword = Digests.sha1(plainPassword.getBytes(), Digests.decodeHex(salt), UserAccount.HASH_INTERATIONS);

        return Digests.encodeHex(hashPassword);

    }

}

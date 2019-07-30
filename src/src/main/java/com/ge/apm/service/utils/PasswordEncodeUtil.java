package com.ge.apm.service.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.Charset;
import java.security.spec.KeySpec;

public class PasswordEncodeUtil {

    private static final String UNICODE_FORMAT = "UTF8";
    private static final String ENCODE_KEY = "APM";
    private static final String SALT = "APM@@WEB";
    private static final String ENCRYPTION_SCHEME = "PBEWithMD5AndDES";

    // Salt
    private static final byte[] SALT_BYTES = {
            (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
            (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };
    static PBEParameterSpec pbeParamSpec;
    static SecretKey key;

    static {

        try {
            KeySpec ks = new PBEKeySpec(ENCODE_KEY.toCharArray(), SALT.getBytes(Charset.forName("utf-8")), 1000, 128 * 8);
            pbeParamSpec = new PBEParameterSpec(SALT_BYTES, 1000);

            SecretKeyFactory skf = SecretKeyFactory.getInstance(ENCRYPTION_SCHEME);

            key = skf.generateSecret(ks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_SCHEME);
            cipher.init(Cipher.ENCRYPT_MODE, key, pbeParamSpec);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            encryptedString = new String(Base64.encodeBase64(encryptedText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public static String decrypt(String encryptedString) {
        String decryptedText=null;
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_SCHEME);
            cipher.init(Cipher.DECRYPT_MODE, key, pbeParamSpec);
            byte[] encryptedText = Base64.decodeBase64(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText= new String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

}

package com.ge.apm.service.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.Arrays;


/**
 *  aes 加密的工具类
 *  1.存储 加密的秘钥key
 *  2.实现 aes 加密
 *  3.实现aes解密的功能
 * @author cxq
 */
@Slf4j
public class AesUtil {
    /**
     * 定义 aes 加密的key
     * 密钥  必须是16位, 自定义,
     * 如果不是16位, 则会出现InvalidKeyException: Illegal key size
     * 解决方案有两种：
     * 需要安装Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files(可以在Oracle下载).
     *  .设置设置key的长度为16个字母和数字的字符窜（128 Bit/8=16字符）就不报错了。
     */
    private static final String KEY = "KEYBOARDISTPKCS5";

    /**
     *  偏移量
     */
    private static final int OFFSET = 16;
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String ALGORITHM = "AES";

    /**
     * 加密
     * @param content content
     * @return String
     */
    public static String encrypt(String content) {
        return encrypt(content, KEY);
    }

    /**
     * 解密
     *
     * @param content content
     * @return String
     */
    public static String decrypt(String content) {
        return decrypt(content, KEY);
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param key     加密密钥
     * @return String
     */
    public static String encrypt(String content, String key) {
        try {
            SecretKeySpec sKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, OFFSET);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            //定义加密编码
            String charset = "utf-8";
            byte[] byteContent = content.getBytes(charset);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, sKey, iv);
            byte[] result = cipher.doFinal(byteContent);
            // 加密
            return new Base64().encodeToString(result);
        } catch (Exception e) {
            log.debug("加密失败:{}",e.getMessage());
        }
        return null;
    }

    /**
     * AES（256）解密
     *
     * @param content 待解密内容
     * @param key     解密密钥
     * @return 解密之后
     */
    public static String decrypt(String content, String key) {
        try {

            SecretKeySpec sKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, OFFSET);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, sKey, iv);
            byte[] result = cipher.doFinal(new Base64().decode(content));
            // 解密
            return new String(result);
        } catch (Exception e) {
            log.debug("解密失败:{}",e.getMessage());
        }
        return null;
    }

    public static String decryptForCryptoJSaesdefault(String encryptedText, String secret){
        try {
            byte[] cipherData = new Base64().decode(encryptedText);
            byte[] saltData = Arrays.copyOfRange(cipherData, 8, 16);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[][] keyAndIV = GenerateKeyAndIV(32, 16, 1, saltData, secret.getBytes(StandardCharsets.UTF_8), md5);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[0], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[1]);
            byte[] encrypted = Arrays.copyOfRange(cipherData, 16, cipherData.length);
            Cipher aesCBC = Cipher.getInstance(TRANSFORMATION);
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(encrypted);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("aesDecrypt error.param={}", encryptedText, e);
            return null;
        }
    }

    public static byte[][] GenerateKeyAndIV(int keyLength, int ivLength, int iterations, byte[] salt, byte[] password, MessageDigest md) {
        int digestLength = md.getDigestLength();
        int requiredLength = (keyLength + ivLength + digestLength - 1) / digestLength * digestLength;
        byte[] generatedData = new byte[requiredLength];
        int generatedLength = 0;
        try {
            md.reset();
            while (generatedLength < keyLength + ivLength) {
                if (generatedLength > 0)
                    md.update(generatedData, generatedLength - digestLength, digestLength);
                md.update(password);
                if (salt != null)
                    md.update(salt, 0, 8);
                md.digest(generatedData, generatedLength, digestLength);
                for (int i = 1; i < iterations; i++) {
                    md.update(generatedData, generatedLength, digestLength);
                    md.digest(generatedData, generatedLength, digestLength);
                }
                generatedLength += digestLength;
            }
            byte[][] result = new byte[2][];
            result[0] = Arrays.copyOfRange(generatedData, 0, keyLength);
            if (ivLength > 0)
                result[1] = Arrays.copyOfRange(generatedData, keyLength, keyLength + ivLength);
            return result;
        } catch (DigestException e) {
            throw new RuntimeException(e);
        } finally {
            Arrays.fill(generatedData, (byte)0);
        }
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;

    }

    public static void main(String[] args) {
        String lsfjlsjflasjdflsjflsf123XSDF3453 = AesUtil.decryptForCryptoJSaesdefault("=VTJGc2RHVmtYMStsOCtGN3NGYWJoNFV5b0tnNmFmZlZqaG5JUGNVbmV0NHRsV3RhcWlPNTRPdXhOdi8veW9LZQ==", "lsfjlsjflasjdflsjflsf123XSDF3453");
        System.out.println(lsfjlsjflasjdflsjflsf123XSDF3453);
    }


}




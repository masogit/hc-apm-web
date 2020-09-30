package webapp.framework.util;

import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class StringUtil {

    private static final Logger logger = Logger.getLogger(StringUtil.class);

    public static String generateRandomString(Integer length) {
        StringBuilder strb = new StringBuilder();

        //65-122
        Random rn = new Random(new Date().getTime());

        for (int i = 0; i < length; i++) {
            int intVal = rn.nextInt(Integer.MAX_VALUE);
            char letter;
            if (intVal % 2 == 0) {
                letter = (char) ((rn.nextInt(Integer.MAX_VALUE) % 26) + 65);
            } else {
                letter = (char) ((rn.nextInt(Integer.MAX_VALUE) % 26) + 97);
            }
            strb.append(letter);
        }
        return strb.toString();
    }

    public static String desEncrypt(String input) {
        try {
            DESEncryptTool desEncryptTool = new DESEncryptTool();
            return desEncryptTool.encrypt(input);
        } catch (Exception e) {
            logger.debug("encrypt failed.", e);
        }
        return input;
    }

    public static String desDecrypt(String input) {
        try {
            DESEncryptTool desEncryptTool = new DESEncryptTool();
            return desEncryptTool.decrypt(input);
        } catch (Exception e) {
            logger.debug("encrypt failed.", e);
        }
        return input;
    }

    /**
     * 手机号码前三后四脱敏
     * @param mobile
     * @return
     */
    public static String mobileEncrypt(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

}

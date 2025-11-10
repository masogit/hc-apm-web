package webapp.framework.util;

import java.util.Date;
import java.util.Random;
import java.util.StringJoiner;

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
     *
     * @param mobile
     * @return
     */
    public static String mobileEncrypt(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 拼接字符串,忽略空字符串
     * @param delimiter 拼接符号
     * @param strings   需要拼接的字符串
     * @return
     */
    public static String joinNotEmpty(String delimiter, CharSequence... strings) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (CharSequence s : strings) {
            boolean empty = org.springframework.util.StringUtils.isEmpty(s);
            if (empty) {
                continue;
            }
            joiner.add(s);
        }
        return joiner.toString();
    }
    /**
     * 下划线转驼峰
     * @param snakeCase 下划线字段
     * @return
     */
    public static String snakeToCamel(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = false;
        for (char ch : snakeCase.toCharArray()) {
            if (ch == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    sb.append(Character.toUpperCase(ch));
                    nextUpper = false;
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }
    /**
     * 驼峰转下划线
     * @param camelCase 驼峰字段
     * @return
     */
    public static String camelToSnakeOptimized(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char ch = camelCase.charAt(i);

            if (Character.isUpperCase(ch)) {
                // 情况1：不是首字母，且后面不是小写字母（说明是缩写结尾，如 XMLH → xml_h）
                // 情况2：前面是小写（如 barCode → bar_code）
                boolean prevIsLower = i > 0 && Character.isLowerCase(camelCase.charAt(i - 1));
                boolean nextIsLower = i + 1 < camelCase.length() && Character.isLowerCase(camelCase.charAt(i + 1));

                if (prevIsLower || (i > 0 && !prevIsLower && nextIsLower)) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(ch));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}

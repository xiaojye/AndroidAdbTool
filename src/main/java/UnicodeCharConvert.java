import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeCharConvert {
    private static final Pattern PATTERN_UNICODE = Pattern.compile("\\\\u[a-f0-9A-F]{1,4}");

    /**
     * unicode串转字符串
     *
     * @param unicode unicode串
     * @return 字符串
     */
    private static String unicodeToChar(String unicode) {
        if (unicode == null || unicode.isEmpty()) {
            return unicode;
        }
        StringBuffer str = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int index = 1; index < hex.length; index++) {
            int data = Integer.parseInt(hex[index], 16);
            str.append((char) data);
        }
        return str.toString();
    }

    /**
     * 字符串转unicode串
     *
     * @param str 字符串
     * @return unicode串
     */
    public static String charToUnicode(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuffer unicode = new StringBuffer();
        for (int index = 0; index < str.length(); index++) {
            char c = str.charAt(index);

            // 转换为unicode
            String tmp = Integer.toHexString(c);
            if (tmp.length() >= 4) {
                unicode.append("\\u" + tmp);
            } else if (tmp.length() == 3) {
                unicode.append("\\u0" + tmp);
            } else if (tmp.length() == 2) {
                unicode.append("\\u00" + tmp);
            } else if (tmp.length() == 1) {
                unicode.append("\\u000" + tmp);
            } else if (tmp.length() == 3) {
                unicode.append("\\u0000");
            }

        }
        return unicode.toString();
    }


    /**
     * 混合串转普通字符串
     * 混合串指的是包含unicode和普通字符的字符串
     *
     * @param mixStr 混合串
     * @return 普通字符串
     */
    public static String mixStrToString(String mixStr) {
        if (mixStr == null || mixStr.isEmpty()) {
            return mixStr;
        }
        int start = 0;
        StringBuffer result = new StringBuffer();
        Matcher matcher = PATTERN_UNICODE.matcher(mixStr);
        while (matcher.find()) {
            String oldChar = matcher.group();
            result.append(mixStr.substring(start, matcher.start()));
            result.append(unicodeToChar(oldChar));
            start = matcher.start() + oldChar.length();
        }
        result.append(mixStr.substring(start));
        return result.toString();
    }

    /**
     * 混合串转unicode串
     * 混合串指的是包含unicode和普通字符的字符串
     *
     * @param mixStr 混合串
     * @return unicode串
     */
    public static String mixStrToUnicode(String mixStr) {
        if (mixStr == null || mixStr.isEmpty()) {
            return mixStr;
        }
        int start = 0;
        StringBuffer result = new StringBuffer();
        Matcher matcher = PATTERN_UNICODE.matcher(mixStr);
        while (matcher.find()) {
            String oldChar = matcher.group();
            result.append(charToUnicode(mixStr.substring(start, matcher.start())));
            result.append(oldChar);
            start = matcher.start() + oldChar.length();
        }
        result.append(charToUnicode(mixStr.substring(start)));
        return result.toString();
    }


    /**
     * 字符串转换unicode，不能转换符号
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            if (c < 0x20 || c > 0x7E) {
                // 转换为unicode
                String tmp = Integer.toHexString(c);
                if (tmp.length() >= 4) {
                    unicode.append("\\u" + Integer.toHexString(c));
                } else if (tmp.length() == 3) {
                    unicode.append("\\u0" + Integer.toHexString(c));
                } else if (tmp.length() == 2) {
                    unicode.append("\\u00" + Integer.toHexString(c));
                } else if (tmp.length() == 1) {
                    unicode.append("\\u000" + Integer.toHexString(c));
                } else if (tmp.length() == 3) {
                    unicode.append("\\u0000");
                }
            } else {
                unicode.append(c);
            }
        }
        return unicode.toString();
    }
}
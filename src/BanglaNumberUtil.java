public class BanglaNumberUtil {

    private BanglaNumberUtil() {}

    public static String toAsciiDigits(String banglaDigits) {
        if (banglaDigits == null || banglaDigits.isEmpty()) return banglaDigits;
        StringBuilder sb = new StringBuilder(banglaDigits.length());
        for (int i = 0; i < banglaDigits.length(); i++) {
            char c = banglaDigits.charAt(i);
            if (c >= '\u09E6' && c <= '\u09EF') {
                sb.append((char) ('0' + (c - '\u09E6')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isBanglaNumber(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '\u09E6' || c > '\u09EF') return false;
        }
        return true;
    }
}
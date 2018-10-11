package com.updater.utils;

import java.text.DecimalFormat;
import java.util.List;

public class StringUtil {

    public static final DecimalFormat SIMPLE = new DecimalFormat("#,###.#");
    public static final DecimalFormat EXTENDED = new DecimalFormat("#,###");

    public static String formatDecimal(final double d, final DecimalFormat formatting) {
        if (d < 1000) {
            return Integer.toString(((int) d));
        } else if (formatting.equals(SIMPLE)) {
            final String[] suffix = {"k", "m", "b", "t"};
            final int power = (int) StrictMath.log10(d);
            return formatting.format(d / (Math.pow(10, (power / 3) * 3))).concat(suffix[power / 3 - 1]);
        } else {
            return formatting.format(d);
        }
    }

    public static boolean listContains(List<Object> l, Object str) {
        for (Object o : l) {
            if (str.toString().contains(o.toString())) {
                return true;
            }
        }
        return false;
    }

    public static String replaceNonAlphanumeric(final String string) {
        final StringBuilder builder = new StringBuilder();
        for (final char c : string.toCharArray()) {
            if (!containsNonAlphanumeric(Character.toString(c))) {
                builder.append(Character.toString(c));
            }
        }
        return builder.toString();
    }

    public static boolean containsNonAlphanumeric(final String string) {
        return string.matches(".*\\W+.*");
    }

    public static String stripHTML(final String string) {
        return string.replaceAll("\\<[^>]*>", "").replaceAll("\\&.*?\\;", "");
    }
}
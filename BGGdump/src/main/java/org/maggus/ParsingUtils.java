package org.maggus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParsingUtils {

    public static Double safeParseDouble(String val) {
        try {
            return Double.parseDouble(val);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Integer safeParseInteger(String val) {
        try {
            return Integer.parseInt(val);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Long safeParseLong(String val) {
        try {
            return Long.parseLong(val);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String joinLongArray(String delim, long[] arr) {
        return Arrays.stream(arr).mapToObj(l -> Long.toString(l)).collect(Collectors.joining(","));
    }

    public static boolean containsLongArray(Long val, long[] arr) {
        return Arrays.stream(arr).anyMatch(id -> val != null && val == id);
    }
}

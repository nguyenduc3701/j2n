package com.example.j2n.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonUtils {

    /**
     * Safely trims a string. If the string is null, returns null.
     *
     * @param str the string to trim
     * @return the trimmed string, or null if the input was null
     */
    public String safeTrim(String str) {
        return str == null ? null : str.trim();
    }
}

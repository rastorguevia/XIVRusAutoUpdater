package ru.rastorguev.util;

import java.util.regex.Pattern;

public class PatternUtil {

    private static final Pattern versionPattern = Pattern.compile("\\D*([.\\d]+\\w?).*");

    public static String getVersionFrom(String release) {
        return versionPattern.matcher(release).replaceFirst("$1").trim();
    }

}

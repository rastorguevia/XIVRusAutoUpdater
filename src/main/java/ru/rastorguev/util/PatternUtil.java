package ru.rastorguev.util;

import java.util.regex.Pattern;

public class PatternUtil {

    private static final Pattern versionPattern = Pattern.compile("\\D*([.\\d]+\\w?).*");
    private static final Pattern extentionPattern = Pattern.compile("[.][^.]+$");


    public static String getVersionFrom(String release) {
        return versionPattern.matcher(release).replaceFirst("$1").trim();
    }

    public static String getFileNameWithoutExtension(String filename) {
        return extentionPattern.matcher(filename).replaceFirst("").trim();
    }

}

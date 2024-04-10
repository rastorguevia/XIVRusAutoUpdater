package ru.rastorguev.dto.constant;

import ru.rastorguev.exception.UtilitarianClassException;

public class Constants {

    private Constants() {
        throw new UtilitarianClassException("Constants");
    }

    public static final String XIV_RU_RELEASE_LATEST = "https://api.github.com/repos/xivrus/xiv_ru_weblate/releases/latest";
    public static final String XIV_RU_LATEST_TRANSLATION_FILE = "https://github.com/xivrus/xiv_ru_weblate/releases/latest/download/release.pmp";
    public static final String XIV_RUS_SITE = "https://xivrus.ru/download";

    public static final String XIV_RU_META_FILE = "meta.json";
    public static final String PROGRAM_DIR = "user.dir";
    public static final String GITHUB_TAG = "tag_name";
    public static final String META_JSON_VERSION = "Version";
    public static final String RELEASE_ZIP = "/release.zip";
    public static final String LOG_PATH = "/log";

    public static final String XIV_RU_FOLDER_NAME=  "XIV Rus";
    public static final String XIV_RUS_TRANSLATION_AUTO_UPDATER = "XIVRusTranslationAutoUpdater";
    public static final String FOLDER_NOT_DELETED = "Предыдущая версия перевода НЕ удалена";

}

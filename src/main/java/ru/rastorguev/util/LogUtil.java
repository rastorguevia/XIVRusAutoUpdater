package ru.rastorguev.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Arrays;

import static ru.rastorguev.dto.constant.Constants.XIV_RUS_SITE;

@Slf4j
public class LogUtil {

    public static void logWhatIsNew(JSONObject jsonLatestGithubRelease) {
        log.info("_________________________________ \n");
        log.info("Изменения:");

        Arrays.stream(jsonLatestGithubRelease.getString("body")
                        .replace("**", "")
                        .split("\n"))
                .filter(str -> str.startsWith("*"))
                .forEach(log::info);

        log.info("Подробнее о проекте перевода: " + XIV_RUS_SITE);
        log.info("Об изменениях на Github странице перевода: https://github.com/xivrus/xiv_ru_weblate/releases");
        log.info("_________________________________ \n");
    }

}

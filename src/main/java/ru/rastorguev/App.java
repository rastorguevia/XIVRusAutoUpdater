package ru.rastorguev;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.apache.commons.io.FileUtils.*;
import static ru.rastorguev.dto.constant.Constants.*;
import static ru.rastorguev.util.DownloadUtil.downloadTranslationRelease;
import static ru.rastorguev.util.FileUtil.*;
import static ru.rastorguev.util.JsonUtil.getJsonLatestGithubRelease;
import static ru.rastorguev.util.JsonUtil.readAll;
import static ru.rastorguev.util.LogUtil.logWhatIsNew;
import static ru.rastorguev.util.PatternUtil.getFileNameWithoutExtension;
import static ru.rastorguev.util.PatternUtil.getVersionFrom;
import static ru.rastorguev.util.SystemNotificationUtil.*;
import static ru.rastorguev.util.TimeUtil.getTimeConsumption;


@Slf4j
public class App {

    public static void main(String[] args) throws InterruptedException {
        long timer = System.currentTimeMillis();
        long startTimer = System.currentTimeMillis();

        try {
            final var programDir = new File(System.getProperty(PROGRAM_DIR));

            if (args.length == 0) updateTranslationFromRemote(programDir, timer);
            else updateTranslationFromFile(args[0], programDir, timer);

            deleteOldLogs(programDir);

        } catch (Exception e) {
            log.error("main", e);
            notificationError();

        } finally {
            log.info("Завершение работы: {} мс", getTimeConsumption(startTimer));
            // ждем возможного нажатия на уведомление для перехода на сайт при обновлении, либо для открытия лога при ошибке
            Thread.sleep(10000);
            System.exit(0);
        }
    }

    private static void updateTranslationFromFile(String arg, File programDir, long timer) throws IOException {
        log.info("Запущено обновление из локального файла");

        final var translationFolder = getTranslationFolder(programDir);
        final var jsonTranslationMeta = new JSONObject(readAll(new FileReader(getTranslationMeta(translationFolder).getPath())));
        final var localVersion = getVersionFrom(jsonTranslationMeta.getString(META_JSON_VERSION));
        log.info("Текущая версия: {}", localVersion);

        final var openedFile = new File(arg);
        if (!openedFile.exists()) {
            log.error("Файл не найден");
            return;
        } log.info("Выбранный файл {}", openedFile.getAbsoluteFile());

        log.debug("Проверка тега версии и выбранного файла: {} мс", getTimeConsumption(timer));
        timer = System.currentTimeMillis();

        deleteDirectory(translationFolder);
        log.debug("Удаление текущего перевода: {} мс", getTimeConsumption(timer));
        timer = System.currentTimeMillis();

        unzip(openedFile.getAbsolutePath(), translationFolder.getAbsolutePath());
        log.info("Новая версия перевода разархивирована в папку с модами: {} мс", getTimeConsumption(timer));
        timer = System.currentTimeMillis();

        final var jsonTranslationMetaNew = new JSONObject(readAll(new FileReader(getTranslationMeta(translationFolder).getPath())));
        final var localVersionNew = getVersionFrom(jsonTranslationMetaNew.getString(META_JSON_VERSION));
        log.info("Новая версия из {}: {}", openedFile.getName(), localVersionNew);

        var historicFolderStr = programDir + TRANSLATION_HISTORY_PATH;
        var newFileName = getFileNameWithoutExtension(openedFile.getName()) + "_" + localVersionNew + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss")) + ".pmp";
        copyFile(openedFile, new File(historicFolderStr + "/" + newFileName));
        log.info("Файл перевода скопирован в папку {} c новым названием {}: {} мс", historicFolderStr, newFileName, getTimeConsumption(timer));

        notificationUpdateFromFile("Обновление " + localVersion + " ⮞ " + localVersionNew);

        deleteOldTraslationFiles(historicFolderStr);
    }

    private static void updateTranslationFromRemote(File programDir, long timer) throws Exception {
        log.info("Запущена проверка наличия обновлений");

        final var translationFolder = getTranslationFolder(programDir);
        final var jsonTranslationMeta = new JSONObject(readAll(new FileReader(getTranslationMeta(translationFolder).getPath())));
        final var localVersion = getVersionFrom(jsonTranslationMeta.getString(META_JSON_VERSION));
        log.info("Текущая версия: {}", localVersion);

        final var jsonLatestGithubRelease = getJsonLatestGithubRelease(XIV_RU_RELEASE_LATEST);
        var versionTagGithub = getVersionFrom(jsonLatestGithubRelease.getString(GITHUB_TAG));
        log.info("Последняя версия с Github: {}", versionTagGithub);

        log.debug("Проверка тегов версий: {} мс", getTimeConsumption(timer));
        timer = System.currentTimeMillis();

        if (!localVersion.equals(versionTagGithub) || config.isDebugUpdate()) {
            final var releasePmpFile = new File(programDir + RELEASE_PMP);

            runAsync(() -> renameAndDeleteOldTranslation(translationFolder));

            downloadTranslationRelease(XIV_RU_LATEST_TRANSLATION_FILE,  releasePmpFile.toPath());
            log.info("Архив новой версии перевода сохранен: {} мс", getTimeConsumption(timer));
            timer = System.currentTimeMillis();

            unzip(releasePmpFile.getAbsolutePath(), programDir.getParentFile() + "/" + XIV_RU_FOLDER_NAME);
            log.info("Новая версия перевода разархивирована в папку с модами: {} мс", getTimeConsumption(timer));
            timer = System.currentTimeMillis();

            delete(releasePmpFile);
            log.info("Архив перевода удален: {} мс", getTimeConsumption(timer));
            log.info("Новая версия перевода: {}", versionTagGithub);

            logWhatIsNew(jsonLatestGithubRelease);
            notificationUpdate("Обновление " + localVersion + " ⮞ " + versionTagGithub);

        } else log.info("Нет новых обновлений");
    }

}

package ru.rastorguev;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.json.JSONObject;
import ru.rastorguev.util.SystemNotificationUtil;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.runAsync;
import static org.apache.commons.io.FileUtils.delete;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static ru.rastorguev.dto.constant.Constants.*;
import static ru.rastorguev.util.PatternUtil.getVersionFrom;


@Slf4j
public class App {

    public static void main(String[] args) throws InterruptedException {

        long timer = System.nanoTime();
        long startTimer = System.nanoTime();

        try {
            final var jsonLatestGithubRelease = getJsonLatestGithubRelease(XIV_RU_RELEASE_LATEST);
            var versionTagGithub = getVersionFrom(jsonLatestGithubRelease.getString(GITHUB_TAG));
            log.info("Последняя версия с Github: {}", versionTagGithub);

            final var programDir = new File(System.getProperty(PROGRAM_DIR));
            final var translationFolder = getTranslationFolder(programDir);

            final var jsonTranslationMeta = new JSONObject(readAll(new FileReader(getTranslationMeta(translationFolder).getPath())));
            final var localVersion = getVersionFrom(jsonTranslationMeta.getString(META_JSON_VERSION));
            log.info("Локальная версия: {}", localVersion);

            log.info("Проверка тегов версий: {} ms", getTimeConsumption(timer));
            timer = System.nanoTime();

            if (!localVersion.equals(versionTagGithub)) {
                final var releaseZipFile = new File(programDir + RELEASE_ZIP);

                runAsync(() -> renameAndDeleteOldTranslation(translationFolder));

                downloadTranslationRelease(XIV_RU_LATEST_TRANSLATION_FILE,  releaseZipFile.toPath());
                log.info("Архив новой версии перевода сохранен: {} ms", getTimeConsumption(timer));
                timer = System.nanoTime();

                unzip(releaseZipFile.getAbsolutePath(), programDir.getParentFile() + "/" + XIV_RU_FOLDER_NAME);
                log.info("Новая версия перевода разархивирована в папку с модами: {} ms", getTimeConsumption(timer));
                timer = System.nanoTime();

                delete(releaseZipFile);
                log.info("Архив перевода удален: {} ms", getTimeConsumption(timer));
                log.info("Новая версия перевода: {}", versionTagGithub);

                logWhatIsNew(jsonLatestGithubRelease);
                SystemNotificationUtil.notificationUpdate("Обновление " + localVersion + " ⮞ " + versionTagGithub);

            } else log.info("Нет новых обновлений");

            deleteOldLogs(programDir);

        } catch (Exception e) {
            log.error("main", e);
            SystemNotificationUtil.notificationError();

        } finally {
            log.info("Завершение работы: {} ms", getTimeConsumption(startTimer));
            // ждем возможного нажатия на уведомление для перехода на сайт при обновлении, либо для открытия лога при ошибке
            Thread.sleep(10000);
            System.exit(0);
        }

    }

    private static void deleteOldLogs(File programDir) {
        var logDir = new File(programDir + LOG_PATH);

        if (logDir.exists()) {
            var logListFiles = logDir.listFiles();

            if (logListFiles != null && logListFiles.length > 5) {
                log.info("Удаление старых логов");

                Stream.of(Objects.requireNonNull(logDir.listFiles()))
                        .sorted(Comparator.comparingLong(File::lastModified).reversed())
                        .skip(5)
                        .forEach(File::delete);
            }

        }

    }

    private static Long getTimeConsumption(long timer) {
        return (System.nanoTime() - timer)/1000000;
    }

    private static void renameAndDeleteOldTranslation(File translationFolder) {
        long timer = System.nanoTime();
        var oldTranslationFolder = new File(translationFolder.getParent() + "/" + XIV_RU_FOLDER_NAME + "_old");

        if (!translationFolder.renameTo(oldTranslationFolder)) return;

        try {
            deleteDirectory(oldTranslationFolder);
        } catch (IOException e) {
            log.error(FOLDER_NOT_DELETED);
            SystemNotificationUtil.notificationError(FOLDER_NOT_DELETED);
            return;
        }
        log.info("Предыдущая версия перевода удалена: {} ms", getTimeConsumption(timer));
    }

    private static File getTranslationMeta(File translationFolder) {
        return Arrays.stream(Objects.requireNonNull(translationFolder.listFiles()))
                .filter(file -> XIV_RU_META_FILE.equals(file.getName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Json файл (" + XIV_RU_META_FILE + ".json) не найден. " +
                        "Проверьте расположение средства для обновления, либо убедитесь в наличии папки с переводом."));
    }

    private static File getTranslationFolder(File programDir) {
        return Arrays.stream(Objects.requireNonNull(programDir.getParentFile().listFiles()))
                .filter(File::isDirectory)
                .filter(file -> XIV_RU_FOLDER_NAME.equals(file.getName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Папка с переводом (" + XIV_RU_FOLDER_NAME + ") не найдена. " +
                        "Проверьте расположение средства для обновления, либо убедитесь в наличии папки с переводом."));
    }

    public static JSONObject getJsonLatestGithubRelease(String urlFrom) throws IOException, URISyntaxException {
        try (final var is = URL.of(new URI(urlFrom), null).openStream()) {
            return new JSONObject(readAll(new InputStreamReader(is, StandardCharsets.UTF_8)));
        }
    }

    private static String readAll(Reader rd) throws IOException {
        final var sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        rd.close();

        return sb.toString();
    }

    public static void unzip(String source, String destination) throws IOException {
        try (final var zip = new ZipFile(source)) {
            zip.extractAll(destination);
        }
    }

    public static void downloadTranslationRelease(String urlFrom, Path destination) throws IOException, URISyntaxException {
        try (final var is = URL.of(new URI(urlFrom), null).openStream()) {
            Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

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

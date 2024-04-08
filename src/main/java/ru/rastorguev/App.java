package ru.rastorguev;

import net.lingala.zip4j.ZipFile;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.delete;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static ru.rastorguev.dto.constant.Constants.*;
import static ru.rastorguev.util.LoggerUtil.log;


public class App {



    public static void main(String[] args) {

        try {
            final var jsonLatestGithubRelease = getJsonLatestGithubRelease(XIV_RU_RELEASE_LATEST);
            var versionTagGithub = (String) jsonLatestGithubRelease.get(GITHUB_TAG);
            versionTagGithub = versionTagGithub.startsWith("v") ? versionTagGithub.substring(1) : versionTagGithub;
            log.info(versionTagGithub);

            final var programDir = new File(System.getProperty(PROGRAM_DIR));

            final var translationFolder = getTranslationFolder(programDir);
            log.fine(translationFolder.getName());

            final var jsonTranslationMeta = new JSONObject(readAll(new FileReader(getTranslationMeta(translationFolder).getPath())));
            final var localVersion = jsonTranslationMeta.getString(META_JSON_VERSION);
            log.fine(localVersion);

            if (!localVersion.contains(versionTagGithub)) {
                final var releaseZipFile = new File(programDir + RELEASE_ZIP);

                saveNewFile(XIV_RU_LATEST_TRANSLATION_FILE,  releaseZipFile.toPath());
                log.info("Архив новой версии перевода сохранен");

                deleteDirectory(translationFolder);
                log.info("Предыдущая версия перевода удалена");

                unzip(releaseZipFile.getAbsolutePath(), programDir.getParentFile() + "/" + XIV_RU_FOLDER_NAME);
                log.info("Новая версия перевода разархивирована в папку с модами");

                delete(releaseZipFile);
                log.info("Архив перевода удален");
                log.info("Новая версия перевода " + versionTagGithub);

            } else log.info("Нет новых обновлений");

        } catch (Exception e) {
            log.severe(e.getMessage());
        }

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

    public static JSONObject getJsonLatestGithubRelease(String url) throws IOException, URISyntaxException {
        try (final var is = URL.of(new URI(url), null).openStream()) {
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

    public static void unzip(String source, String destination) {
        try (final var zip = new ZipFile(source)) {
            zip.extractAll(destination);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public static void saveNewFile(String url, Path destination) throws IOException, URISyntaxException {
        try (final var is = URL.of(new URI(url), null).openStream()) {
            Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    //TODO доделать
    public static void whatIsNew(JSONObject jsonLatestGithubRelease) {
        var whatsNew = (String) jsonLatestGithubRelease.get("body");

        whatsNew = whatsNew.replace("**", "");
        var splitedWhatsNew = Arrays.stream(whatsNew.split("\n")).toList();

        log.info("\n" + whatsNew);
        log.info("__________________________");

        var sortedWhatsew = splitedWhatsNew.stream().filter(str -> str.startsWith("*")).toList();

        sortedWhatsew.forEach(log::info);
    }

    //TODO сделать прием файлов через вар арги
    //TODO добавить виндовые уведомления

}

package ru.rastorguev;

import net.lingala.zip4j.ZipFile;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

import static org.apache.commons.io.FileUtils.delete;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static ru.rastorguev.Constants.*;
import static ru.rastorguev.LoggerHandler.log;


public class App {



    public static void main(String[] args) {

        try {
            var jsonLatestGithubRelease = getJsonLatestGithubRelease(XIV_RU_RELEASE_LATEST);
            var versionTagGithub = (String) jsonLatestGithubRelease.get("tag_name");
            versionTagGithub = versionTagGithub.startsWith("v") ? versionTagGithub.substring(1) : versionTagGithub;

            log.fine(versionTagGithub);

            var userDir = new File(System.getProperty("user.dir"));

            var translationFolder = Arrays.stream(Objects.requireNonNull(userDir.getParentFile().listFiles()))
                    .filter(File::isDirectory)
                    .filter(file -> XIV_RU_FOLDER_NAME.equals(file.getName()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Папка с переводом (" + XIV_RU_FOLDER_NAME + ") не найдена. " +
                            "Проверьте расположение средства для обновления, либо убедитесь в наличии папки с переводом."));

            log.fine(translationFolder.getName());

            var jsonFromTranslationFolder = Arrays.stream(Objects.requireNonNull(translationFolder.listFiles()))
                    .filter(file -> XIV_RU_META_FILE.equals(file.getName()))
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Json файл (" + XIV_RU_META_FILE + ".json) не найден. " +
                            "Проверьте расположение средства для обновления, либо убедитесь в наличии папки с переводом."));

            log.fine(jsonFromTranslationFolder.getName());

            var jsonLocal = new JSONObject(readAll(new FileReader(jsonFromTranslationFolder.getPath())));
            var localVersion = jsonLocal.getString("Version");

            log.fine(localVersion);

            if (!localVersion.contains(versionTagGithub)) {
                var releaseZipFile = new File(userDir + "/release.zip");

                saveNewFile(XIV_RU_LATEST_TRANSLATION_FILE,  releaseZipFile.toPath());
                log.info("Архив новой версии перевода сохранен");
                deleteDirectory(translationFolder);
                log.info("Предыдущая версия перевода удалена");
                unzip(releaseZipFile.getAbsolutePath(), userDir.getParentFile() + "/" + XIV_RU_FOLDER_NAME);
                log.info("Новая версия перевода разархивирована в папку с модами");
                //Удаляем zip файл
                delete(releaseZipFile);
                log.info("Архив перевода удален");
                log.info("Новая версия перевода " + versionTagGithub);
            } else log.info("Нет новых обновлений");
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public static JSONObject getJsonLatestGithubRelease(String url) throws IOException {
        try (var is = new URL(url).openStream()) {
            return new JSONObject(readAll(new InputStreamReader(is, StandardCharsets.UTF_8)));
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        rd.close();

        return sb.toString();
    }

    public static void unzip(String source, String destination) {
        try (var zip = new ZipFile(source)) {
            zip.extractAll(destination);
        } catch (Exception e) {
            log.severe(e.getMessage());
        }
    }

    public static void saveNewFile(String url, Path destination) throws IOException {
        try (var in = new URL(url).openStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}

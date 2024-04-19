package ru.rastorguev.util;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import ru.rastorguev.dto.ConfigDto;
import ru.rastorguev.dto.HistoricTranslationFileDto;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static ru.rastorguev.dto.constant.Constants.*;
import static ru.rastorguev.dto.constant.Constants.XIV_RU_FOLDER_NAME;
import static ru.rastorguev.util.PatternUtil.getFileNameWithoutExtension;
import static ru.rastorguev.util.TimeUtil.getTimeConsumption;
import static ru.rastorguev.util.TimeUtil.getTranslationFileExecuteDateTime;

@Slf4j
public class FileUtil {

    public static final ConfigDto config = YamlUtil.getConfig();

    public static void deleteOldLogs(File programDir) {
        var logDir = new File(programDir + LOG_PATH);

        if (logDir.exists()) {
            var logFilesArray = logDir.listFiles();

            if (logFilesArray != null && logFilesArray.length > config.getLeaveLogsForDays()) {
                log.info("Удаление старых логов");

                Stream.of(Objects.requireNonNull(logDir.listFiles()))
                        .sorted(Comparator.comparingLong(File::lastModified).reversed())
                        .skip(config.getLeaveLogsForDays())
                        .forEach(File::delete);
            }
        }
    }

    public static void deleteOldTraslationFiles(String pathToTranslationFolder) {
        var translationDir = new File(pathToTranslationFolder);

        if (translationDir.exists()) {
            var translationFilesArray = translationDir.listFiles();

            if (translationFilesArray != null && translationFilesArray.length > config.getLeaveTranslationPmpFiles()) {
                log.info("Удаление старых логов");

                Stream.of(Objects.requireNonNull(translationDir.listFiles()))
                        .map(file -> new HistoricTranslationFileDto(file, getTranslationFileExecuteDateTime(
                                getFileNameWithoutExtension(file.getName())))
                        )
                        .sorted(Comparator.comparing(HistoricTranslationFileDto::getLocalDateTime).reversed())
                        .skip(config.getLeaveTranslationPmpFiles())
                        .forEach(hFile -> hFile.getFile().delete());
            }
        }
    }

    public static void renameAndDeleteOldTranslation(File translationFolder) {
        long timer = System.currentTimeMillis();
        var oldTranslationFolder = new File(translationFolder.getParent() + "/" + XIV_RU_FOLDER_NAME + "_old");

        if (!translationFolder.renameTo(oldTranslationFolder)) return;

        try {
            deleteDirectory(oldTranslationFolder);
        } catch (IOException e) {
            log.error(FOLDER_NOT_DELETED);
            SystemNotificationUtil.notificationError(FOLDER_NOT_DELETED);
            return;
        }
        log.info("Предыдущая версия перевода удалена: {} мс", getTimeConsumption(timer));
    }

    public static File getTranslationMeta(File translationFolder) {
        return Arrays.stream(Objects.requireNonNull(translationFolder.listFiles()))
                .filter(file -> XIV_RU_META_FILE.equals(file.getName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Json файл (" + XIV_RU_META_FILE + ".json) не найден. " +
                        "Проверьте расположение средства для обновления, либо убедитесь в наличии папки с переводом."));
    }

    public static File getTranslationFolder(File programDir) {
        return Arrays.stream(Objects.requireNonNull(programDir.getParentFile().listFiles()))
                .filter(File::isDirectory)
                .filter(file -> XIV_RU_FOLDER_NAME.equals(file.getName()))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Папка с переводом (" + XIV_RU_FOLDER_NAME + ") не найдена. " +
                        "Проверьте расположение средства для обновления, либо убедитесь в наличии папки с переводом."));
    }

    public static void unzip(String source, String destination) throws IOException {
        try (final var zip = new ZipFile(source)) {
            zip.extractAll(destination);
        }
    }

}

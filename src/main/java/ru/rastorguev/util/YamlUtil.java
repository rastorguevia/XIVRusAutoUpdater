package ru.rastorguev.util;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;
import ru.rastorguev.dto.ConfigDto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static ru.rastorguev.dto.constant.Constants.PROGRAM_DIR;

@Slf4j
public class YamlUtil {

    public static ConfigDto getConfig() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(System.getProperty(PROGRAM_DIR) + "/config.yaml");
        } catch (FileNotFoundException e) {
            log.error("Файл config.yaml не найден. Используется дефолтный.");
            inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("config.yaml");
        }

        return new Yaml().loadAs(inputStream, ConfigDto.class);
    }

}

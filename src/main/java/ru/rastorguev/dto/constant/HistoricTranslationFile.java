package ru.rastorguev.dto.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HistoricTranslationFile {

    private File file;
    private LocalDateTime localDateTime;

}

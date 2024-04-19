package ru.rastorguev.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class HistoricTranslationFileDto {

    private File file;
    private LocalDateTime localDateTime;

}

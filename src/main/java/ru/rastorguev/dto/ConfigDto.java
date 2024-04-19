package ru.rastorguev.dto;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class ConfigDto {

    private int leaveLogsForDays;
    private int leaveTranslationPmpFiles;
    private boolean debugUpdate;
    private boolean allWinNotificationOff;
    private boolean errWinNotificationOff;

}

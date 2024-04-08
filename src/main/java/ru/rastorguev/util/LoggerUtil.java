package ru.rastorguev.util;

import ru.rastorguev.exception.LogfileException;
import ru.rastorguev.exception.UtilitarianClassException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.*;

import static ru.rastorguev.dto.constant.Constants.PROGRAM_DIR;

public class LoggerUtil {

    private LoggerUtil() {
        throw new UtilitarianClassException("LoggerUtil");
    }

    public static final Logger log = Logger.getLogger(LoggerUtil.class.getName());

    static  {
        log.setUseParentHandlers(false);

        var ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(getFormatter());
        log.addHandler(ch);

        try {
            var fh = new FileHandler(new File(System.getProperty(PROGRAM_DIR)).getAbsolutePath() + new SimpleDateFormat("d_M_y_HH_mm_ss").format(Calendar.getInstance().getTime()) + ".log");
            fh.setFormatter(getFormatter());
            log.addHandler(fh);
        } catch (IOException e) {
            throw new LogfileException("Невозможно создать лог файл", e);
        }
    }

    private static Formatter getFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord logRecord) {

                var logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                var cal = new GregorianCalendar();
                cal.setTimeInMillis(logRecord.getMillis());

                return "[" + logRecord.getLevel() + "] " +
                        "[" + logTime.format(cal.getTime()) + "] " +
                        "[" + logRecord.getSourceClassName().substring(
                        logRecord.getSourceClassName().lastIndexOf(".") + 1) +
                        "." + logRecord.getSourceMethodName() + "] " +
                        ": " + logRecord.getMessage() + "\n";
            }
        };
    }

}

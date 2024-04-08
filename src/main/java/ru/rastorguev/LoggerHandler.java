package ru.rastorguev;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.*;

public class LoggerHandler {

    public static final Logger log = Logger.getLogger(LoggerHandler.class.getName());

    static  {
        log.setUseParentHandlers(false);


        FileHandler fh = null;
        try {
            fh = new FileHandler(new SimpleDateFormat("d-M_HH_mm_ss").format(Calendar.getInstance().getTime()) + ".log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fh.setFormatter(getFormatter());
        log.addHandler(fh);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
        ch.setFormatter(getFormatter());
        log.addHandler(ch);
    }

    private static Formatter getFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {

                var logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                var cal = new GregorianCalendar();
                cal.setTimeInMillis(record.getMillis());

                return "[" + record.getLevel() + "] " +
                        "[" + logTime.format(cal.getTime()) + "] " +
                        "[" + record.getSourceClassName().substring(
                        record.getSourceClassName().lastIndexOf(".") + 1) +
                        "." + record.getSourceMethodName() + "] " +
                        ": " + record.getMessage() + "\n";
            }
        };
    }

}

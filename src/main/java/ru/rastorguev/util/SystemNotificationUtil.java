package ru.rastorguev.util;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ru.rastorguev.dto.constant.Constants.*;

@Slf4j
public class SystemNotificationUtil {

    private static SystemTray tray;
    private static Image image;
    private static TrayIcon trayIcon;

    static {
        if (SystemTray.isSupported()) {
            tray = SystemTray.getSystemTray();
            image = Toolkit.getDefaultToolkit().createImage("favicon.ico");
            trayIcon = new TrayIcon(image, "Tray");

            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("System tray demo");

            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                log.error("SystemNotificationUtil", e);
            }

        } else log.info("SystemNotificationUtil: System tray not supported");
    }

    public static void notification(String message) {
        notification(XIV_RUS_TRANSLATION_AUTO_UPDATER, message, TrayIcon.MessageType.NONE);
    }

    public static void notification(String message, String title) {
        notification(title, message, TrayIcon.MessageType.INFO);
    }

    public static void notification(String title, String message, TrayIcon.MessageType messageType) {
        trayIcon.displayMessage(title, message, messageType);
    }

    public static void notificationUpdate(String title) {
        trayIcon.addActionListener(_ -> {
            try {
                Desktop.getDesktop().browse(new URI(XIV_RUS_SITE));
            } catch (IOException | URISyntaxException e) {
                log.error("SystemNotificationUtil.error", e);
            }
        });
        trayIcon.displayMessage(title, "\n* Перевод был обновлен \n* Для получения подробной информации нажмите на данное уведомление", TrayIcon.MessageType.NONE);
    }

    public static void notificationError() {
        notificationError("Что-то пошло не так, нажмите на уведомление для открытия лога и получения полной информации");
    }

    public static void notificationError(String message) {
        trayIcon.addActionListener(_ -> {
            try {
                Desktop.getDesktop().open(new File(System.getProperty(PROGRAM_DIR) + "/log/app.log"));
            } catch (IOException e) {
                log.error("SystemNotificationUtil.error", e);
            }
        });

        trayIcon.displayMessage(XIV_RUS_TRANSLATION_AUTO_UPDATER, message, TrayIcon.MessageType.ERROR);
    }

}

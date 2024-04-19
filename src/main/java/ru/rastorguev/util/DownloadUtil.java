package ru.rastorguev.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DownloadUtil {

    public static void downloadTranslationRelease(String urlFrom, Path destination) throws IOException, URISyntaxException {
        try (final var is = URL.of(new URI(urlFrom), null).openStream()) {
            Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}

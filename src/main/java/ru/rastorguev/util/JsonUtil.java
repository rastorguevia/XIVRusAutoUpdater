package ru.rastorguev.util;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    public static JSONObject getJsonLatestGithubRelease(String urlFrom) throws IOException, URISyntaxException {
        try (final var is = URL.of(new URI(urlFrom), null).openStream()) {
            return new JSONObject(readAll(new InputStreamReader(is, StandardCharsets.UTF_8)));
        }
    }

    public static String readAll(Reader rd) throws IOException {
        final var sb = new StringBuilder();
        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        rd.close();

        return sb.toString();
    }

}

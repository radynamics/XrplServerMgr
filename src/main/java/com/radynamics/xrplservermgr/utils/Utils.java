package com.radynamics.xrplservermgr.utils;

import com.radynamics.xrplservermgr.xrpl.XrplApiException;

import java.awt.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Utils {
    public static String toHexString(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Long bytesToKb(Long value) {
        return value / 1024;
    }

    public static String getContent(URL url) throws IOException, XrplApiException {
        var conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(2000);
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new XrplApiException(String.format("Failed to get content from %s due HttpResponseCode %s", url, responseCode));
        }

        var sb = new StringBuilder();
        var scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            sb.append(scanner.nextLine());
        }
        scanner.close();
        return sb.toString();
    }
}

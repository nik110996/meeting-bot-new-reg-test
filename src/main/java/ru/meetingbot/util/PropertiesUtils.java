package ru.meetingbot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Получает объект Properties из .properties файлов
 */
public class PropertiesUtils {

    public static Properties getProperties(String nameWithoutExt) {
        Properties properties;

        File file = Path.of("./", nameWithoutExt + ".properties").toFile();
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}

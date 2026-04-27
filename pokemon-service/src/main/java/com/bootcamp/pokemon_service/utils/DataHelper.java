package com.bootcamp.pokemon_service.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataHelper {
    private static final DateTimeFormatter DEFAULT_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String now() {
        return LocalDateTime.now().format(DEFAULT_FORMATER);
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DEFAULT_FORMATER);
    }
}

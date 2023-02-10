package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeAdapterTest {
    @Test
    void check_serialization() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        Gson gson = gsonBuilder.create();
        final LocalDateTime localDateTime = LocalDateTime.now();
        String json = gson.toJson(localDateTime);
        LocalDateTime fromJson = gson.fromJson(json, LocalDateTime.class);
        assertEquals(localDateTime, fromJson);
    }
}
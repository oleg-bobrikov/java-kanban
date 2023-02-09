package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import ru.yandex.bobrikov.kanban.manager.Managers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeAdapterTest {
    @Test
    void check_serialization() {
        Gson gson = Managers.getGson();
        final LocalDateTime localDateTime = LocalDateTime.now();
        String json = gson.toJson(localDateTime);
        LocalDateTime fromJson = gson.fromJson(json, LocalDateTime.class);
        assertEquals(localDateTime, fromJson);
    }
}
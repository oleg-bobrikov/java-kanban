package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DurationAdapterTest {

    @Test
    void check_serialization() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        Gson gson = gsonBuilder.create();
        final Duration duration = Duration.ofDays(3)
                .plusHours(5)
                .plusMinutes(17)
                .plusSeconds(17)
                .plusMillis(537)
                .plusNanos(999999999);
        String json = gson.toJson(duration);
        Duration fromJson = gson.fromJson(json, Duration.class);
        assertEquals(duration, fromJson);
    }

}
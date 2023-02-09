package ru.yandex.bobrikov.kanban.adapter;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import ru.yandex.bobrikov.kanban.manager.Managers;

import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DurationAdapterTest {

    @Test
    void check_serialization() throws IOException {
        Gson gson = Managers.getGson();
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
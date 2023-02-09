package ru.yandex.bobrikov.kanban.manager.server;

import ru.yandex.bobrikov.kanban.manager.exception.KVTaskClientException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private final String API_TOKEN;
    private final InetSocketAddress address;
    private final HttpClient client;

    public KVTaskClient(InetSocketAddress address) throws KVTaskClientException {
        this.address = address;
        this.client = HttpClient.newHttpClient();
        URI uri = URI.create(String.format("http://%s:%d/register",
                address.getHostString(),
                address.getPort()));
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientException(e.getMessage());
        }
        if (response.statusCode() != 200) {
            throw new KVTaskClientException(String.format("Неверный код ответа: %s", response.statusCode()));
        }
        this.API_TOKEN = response.body();
    }

    public void put(String key, String json) {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(String.format("http://%s:%d/save/%s?API_TOKEN=%s",
                address.getHostString(),
                address.getPort(),
                key,
                API_TOKEN));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientException(e.getMessage());
        }
        if (response.statusCode() != 200) {
            throw new KVTaskClientException(String.format("Неверный код ответа: %s", response.statusCode()));
        }

    }

    public String load(String key) {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create(String.format("http://%s:%d/load/%s?API_TOKEN=%s",
                address.getHostString(),
                address.getPort(),
                key,
                API_TOKEN));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            throw new KVTaskClientException(e.getMessage());
        }
        if (response.statusCode() != 200) {
            return null;
        }
        return response.body();
    }

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

}

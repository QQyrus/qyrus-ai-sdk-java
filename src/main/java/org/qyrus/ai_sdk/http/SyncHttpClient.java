package org.qyrus.ai_sdk.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class SyncHttpClient {
    private HttpClient httpClient;

    public SyncHttpClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public HttpResponse<String> post(String url, String requestBody, Map<String, String> headers) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.build();
                

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

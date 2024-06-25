package org.qyrus.ai_sdk.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.Exceptions.AuthorizationException;
import org.qyrus.ai_sdk.Exceptions.BadRequestException;

public class AsyncHttpClient {
    private HttpClient httpClient;

    public AsyncHttpClient() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public CompletableFuture<HttpResponse<String>> post(String url, String requestBody, Map<String, String> headers) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        headers.forEach(requestBuilder::header);
        HttpRequest request = requestBuilder.build();

        // Send the request asynchronously
        // After the response is received, it's processed by handleResponse asynchronously
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        return handleResponse(response); // Call to our status code handler
                    } catch (Exception ex) {
                        CompletableFuture<HttpResponse<String>> failedFuture = new CompletableFuture<>();
                        failedFuture.completeExceptionally(ex); // Propagate exception in failed future
                        return failedFuture.join(); // This will throw the exception
                    }
                });
    }

    private static HttpResponse<String> handleResponse(HttpResponse<String> response) throws BadRequestException, AuthorizationException, Exception {
        int statusCode = response.statusCode();
        switch (statusCode) {
            case 400:
                throw new BadRequestException("Bad Request: " + response.body());
            case 401:
                throw new AuthorizationException("Unauthorized: " + response.body());
            case 422:
                throw new BadRequestException("Unprocessable Entity: " + response.body());
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 505:
                throw new Exception("Server Error: " + response.body());
            default:
                return response;
        }
    }
}

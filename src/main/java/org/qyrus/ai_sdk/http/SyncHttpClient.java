package org.qyrus.ai_sdk.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.Collections;
import java.net.URI;


import org.qyrus.ai_sdk.Exceptions.AuthorizationException;
import org.qyrus.ai_sdk.Exceptions.BadRequestException;


public class SyncHttpClient {

    
    private HttpClient httpClient;

    public SyncHttpClient() {
        this.httpClient = HttpClient.newHttpClient();
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

    public HttpResponse<String> post(String url, String requestBody, Map<String, String> headers) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody));

        headers.forEach(requestBuilder::header);

        HttpRequest request = requestBuilder.build();

        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
        
    }

    public HttpResponse<String> get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
        // Build the URL with query parameters
        if (params != null && !params.isEmpty()) {
            String queryParams = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            url = url + "?" + queryParams;
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET(); // Set the request method to GET

        // Add headers
        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        HttpRequest request = requestBuilder.build();

        // Send the request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response); // Handle and return the response
    }

}



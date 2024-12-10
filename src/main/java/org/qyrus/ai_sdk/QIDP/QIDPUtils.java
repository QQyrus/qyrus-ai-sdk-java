package org.qyrus.ai_sdk.QIDP;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class QIDPUtils {

    private static final String AUTHORIZATION_TOKEN = "Bearer 90540897-748a-3ef2-b3a3-c6f8f42022da";
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(600))
            .build();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean verifyToken(String aiToken) throws IOException, InterruptedException {
        String url = "https://stg-gateway.quinnox.info:8243/authentication/v1/api/validateAPIToken?apiToken=" + aiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", AUTHORIZATION_TOKEN)
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonNode data = objectMapper.readTree(response.body());
            System.out.println("\\nDATA for verify token : " + data.toPrettyString());
            return "Authentication Token Validated Successfully.".equals(data.get("message").asText());
        } else {
            throw new RuntimeException("401"); // Using RuntimeException to avoid having to declare the exception
        }
    }

    public static JsonNode getDefaultGateway(String aiToken) throws IOException, InterruptedException {
        String url = "https://stg-gateway.quinnox.info:8243/authentication/v1/api/authenticateAPIToken?apiToken=" + aiToken;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .header("Authorization", AUTHORIZATION_TOKEN)
                .build();

        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            JsonNode data = objectMapper.readTree(response.body());
            System.out.println("\\nDATA for gateway details : " + data.toPrettyString());
            if (data.has("status") && data.get("status").asBoolean()) {
                ((ObjectNode) data).remove("uuid");
                return data;
            } else {
                throw new RuntimeException("401"); // Using RuntimeException to avoid having to declare the exception
            }
        } else {
            throw new RuntimeException("401"); // Using RuntimeException to avoid having to declare the exception
        }
    }
}

package org.qyrus.ai_sdk.APIBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.AsyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncAPIBuilder {
    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;
    
    public AsyncAPIBuilder(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        try {
            // Verifying the token
            boolean tokenValid = QIDPUtils.verifyToken(apiKey);
            if (!tokenValid) {
                throw new RuntimeException("401: Token is not valid.");
            }
            
            // Getting the gateway details
            JsonNode gatewayDetails = QIDPUtils.getDefaultGateway(apiKey);
            this.baseUrl = gatewayDetails.get("gatewayUrl").asText();
            this.gatewayToken = gatewayDetails.get("gatewayToken").asText(); // Assuming gatewayToken is the field name
            System.out.println("GATEWAY URL USED IS " + this.baseUrl);
            // // Adjust the base URL if needed
            // if (this.baseUrl != null) {
            //     this.baseUrl = this.baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
            // }

        } catch (IOException | InterruptedException e) {
            // Log the exception here
            e.printStackTrace();
            throw new RuntimeException("An error occurred while setting up the NovaJira instance: " + e.getMessage());
        }
    }

    public CompletableFuture<APIBuilderResponse> create(String user_description) {
        String url = this.baseUrl + "/" + Configurations.getAPIBuilderContextPath("build");

        Map<String, String> data = new HashMap<>();
        data.put("user_description", user_description);
        data.put("email", "");


        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        AsyncHttpClient asyncClient = new AsyncHttpClient();
        CompletableFuture<HttpResponse<String>> responseFuture =
            asyncClient.post(url, jsonRequestBody, headers);

        // Return the Swagger JSON directly as a string
        return responseFuture.thenApplyAsync(response -> {
            try {
                return new APIBuilderResponse(response.body()); // This will parse JSON string into CreateScenariosResponse type
            } catch (Exception e) {
                // If an error happens in parsing, print stack trace and throw a RuntimeException; in real scenario, you'd handle this differently
                e.printStackTrace();
                throw new RuntimeException("Error processing the async response", e);
            }
        });
    }

    private String toJson(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Response class that contains the swagger_json String.
    public static class APIBuilderResponse {
        private String swaggerJson;
       
        public APIBuilderResponse(String swaggerJson) {
            this.swaggerJson = swaggerJson;
        }

        @JsonProperty("swagger_json")
        public String getSwaggerJson() {
            return swaggerJson;
        }

    }
}

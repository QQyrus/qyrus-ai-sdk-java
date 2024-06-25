package org.qyrus.ai_sdk.APIAssertions;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.AsyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncHeaderAssertion {
    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;
    
    public AsyncHeaderAssertion(String apiKey, String baseUrl) {
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

    public CompletableFuture<HeaderAssertionResponse> create(String response_headers) {
        String url = this.baseUrl + "/" + Configurations.getAPIAssertionContextPath("headers");
        
        Map<String, String> data = new HashMap<>();
        data.put("headers", response_headers);

        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", apiKey);

        AsyncHttpClient asyncClient = new AsyncHttpClient();
        CompletableFuture<HttpResponse<String>> responseFuture = asyncClient.post(url, jsonRequestBody, headers);

        return responseFuture.thenApplyAsync(response -> {
            try {
                return new HeaderAssertionResponse(parseHeaderAssertionItemList(response.body()));
            } catch (Exception e) {
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

    private List<HeaderAssertionItem> parseHeaderAssertionItemList(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Change return type to List<HeaderAssertionItem>
            return objectMapper.readValue(json, new TypeReference<List<HeaderAssertionItem>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class HeaderAssertionResponse {
        private List<HeaderAssertionItem> headerAssertions;

        // Constructor that accepts a list of HeaderAssertionItem
        public HeaderAssertionResponse(List<HeaderAssertionItem> headerAssertions) {
            this.headerAssertions = headerAssertions;
        }

        // Getter
        public List<HeaderAssertionItem> getHeaderAssertions() {
            return headerAssertions;
        }
    }

    public static class HeaderAssertionItem {
        private String assertHeaderKey;
        private String assertHeaderValue;
        private String assertionDescription;

        @JsonProperty("assertHeaderKey")
        public String getAssertHeaderKey() {
            return assertHeaderKey;
        }

        @JsonProperty("assertHeaderValue")
        public String getAssertHeaderValue() {
            return assertHeaderValue;
        }

        @JsonProperty("assertionDescription")
        public String getAssertionDescription() {
            return assertionDescription;
        }

        public void setAssertHeaderKey(String assertHeaderKey) {
            this.assertHeaderKey = assertHeaderKey;
        }

        public void setAssertHeaderValue(String assertHeaderValue) {
            this.assertHeaderValue = assertHeaderValue;
        }

        public void setAssertionDescription(String assertionDescription) {
            this.assertionDescription = assertionDescription;
        }
    }
}

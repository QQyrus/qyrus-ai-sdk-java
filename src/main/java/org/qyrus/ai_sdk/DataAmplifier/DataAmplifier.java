package org.qyrus.ai_sdk.DataAmplifier;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataAmplifier {

    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;
    
    public DataAmplifier(String apiKey, String baseUrl) {
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

    public DataAmplifierResponse create(List<Map<String, Object>> example_data, int data_count) {
        String url = baseUrl + "/" + Configurations.getDataAmplifierContextPath();
        Map<String, Object> data = new HashMap<>();
        
        data.put("data", example_data);
        data.put("data_count", data_count);
        
        String jsonRequestBody = toJson(data);
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth()); 
        headers.put("Custom", apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        try {
            HttpResponse<String> response = syncClient.post(url, jsonRequestBody, headers);
            return createDataAmplifierResponseFromJson(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception, or return a custom failed response
    }

    private String toJson(Map<String, Object> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private DataAmplifierResponse createDataAmplifierResponseFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, DataAmplifierResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static class DataAmplifierResponse {
        private Map<String, List<String>> data;
        private boolean status;
        private String message;

        @JsonProperty("data")
        public Map<String, List<String>> getData() {
            return data;
        }

        @JsonProperty("data")
        public void setData(Map<String, List<String>> data) {
            this.data = data;
        }

        @JsonProperty("status")
        public boolean isStatus() {
            return status;
        }

        @JsonProperty("status")
        public void setStatus(boolean status) {
            this.status = status;
        }

        @JsonProperty("message")
        public String getMessage() {
            return message;
        }

        @JsonProperty("message")
        public void setMessage(String message) {
            this.message = message;
        }
    }
}

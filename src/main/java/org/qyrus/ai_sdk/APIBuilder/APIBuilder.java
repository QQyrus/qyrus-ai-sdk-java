package org.qyrus.ai_sdk.APIBuilder;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIBuilder {
    private final String apiKey;
    private String baseUrl;

    public APIBuilder(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
        } else {
            this.baseUrl = Configurations.getDefaultGateway();
        }
    }

    public APIBuilderResponse create(String user_description) {
        String url = this.baseUrl + "/" + Configurations.getAPIBuilderContextPath("build");

        Map<String, String> data = new HashMap<>();
        data.put("user_description", user_description);
        data.put("email", "");

        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        
        try {
            HttpResponse<String> response = syncClient.post(url, jsonRequestBody, headers);
            // Wrap the Swagger JSON in an object with a key
            return new APIBuilderResponse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception, or return a failed response
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

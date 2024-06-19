package org.qyrus.ai_sdk.APIAssertions;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONSchemaAssertion {

    private final String apiKey;
    private String baseUrl;

    public JSONSchemaAssertion(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
        } else {
            this.baseUrl = Configurations.getDefaultGateway();
        }
    }

    public JsonSchemaResponse create(Object example_response) {
        String url = baseUrl + "/" + Configurations.getAPIAssertionContextPath("jsonschema");
        Map<String, Object> data = new HashMap<>();
        data.put("response", example_response);
        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        try {
            HttpResponse<String> response = syncClient.post(url, jsonRequestBody, headers);
            return parseJsonSchemaResponse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception, or return a failed response
    }

    private String toJson(Object data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonSchemaResponse parseJsonSchemaResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, JsonSchemaResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class Property {
        @JsonProperty("type")
        private String type;

        public Property() {
        }

        // Getters and setters if needed
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class JsonSchemaResponse {
        @JsonProperty("$schema")
        private String schema;

        @JsonProperty("type")
        private String type;

        @JsonProperty("properties")
        private Map<String, Property> properties;

        @JsonProperty("required")
        private List<String> required;

        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        public JsonSchemaResponse() {
        }

        
        public String getSchema() {
            return schema;
        }
        
        public String getType() {
            return type;
        }
        
        public Map<String, Property> getProperties() {
            return properties;
        }
        
        public List<String> getRequired() {
            return required;
        }
        
        public String getTitle() {
            return title;
        }
        
        public String getDescription() {
            return description;
        }

        
        public void setSchema(String schema) {
            this.schema = schema;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public void setProperties(Map<String, Property> properties) {
            this.properties = properties;
        }
        
        public void setRequired(List<String> required) {
            this.required = required;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        
    }

}

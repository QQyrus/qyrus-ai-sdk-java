package org.qyrus.ai_sdk.VisionNova;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VisionNovaAccessibilityTests{

    private final String apiKey;
    private String baseUrl;

    public VisionNovaAccessibilityTests(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
        } else {
            this.baseUrl = Configurations.getDefaultGateway(); // Assumes this method exists in your Configurations class.
        }
    }

    public CreateScenariosResponse create(String image_url) {
        String url = this.baseUrl + "/" + Configurations.getVisionNovaContextPath("json-accessibility-comment");
        Map<String, String> data = new HashMap<>();
        data.put("image_url", image_url);

        // Convert your Map to a JSON String, use your preferred library (like Gson or Jackson)
        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth()); // Assumed to be a method in your configurations
        headers.put("Custom", this.apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        try {
            HttpResponse<String> response = syncClient.post(url, jsonRequestBody, headers);

            // Use your preferred library to convert response.body() to the actual response type
            return createScenariosResponseFromJson(response.body());
        } catch (Exception e) {
            // Proper exception handling goes here
            e.printStackTrace();
        }
        
        return null; // or throw an exception, or return a failed response
    }


    private String toJson(Map<String, String> map) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private CreateScenariosResponse createScenariosResponseFromJson(String json) {
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, CreateScenariosResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the error as appropriate
            return null;
        }
    }


    public static class AccessibilityScenario {
        @JsonProperty("accessibility_type")
        private String accessibility_type;

        @JsonProperty("accessibility_comment")
        private String accessibility_comment;

        // Default constructor
        public AccessibilityScenario() {
        }

        public AccessibilityScenario(String accessibility_type, String accessibility_comment) {
            this.accessibility_type = accessibility_type;
            this.accessibility_comment = accessibility_comment;
        }

        public String getAccessibilityType(){
            return this.accessibility_type;
        }

        public String getAccessibilityComment(){
            return this.accessibility_comment;
        }
    }

    public static class CreateScenariosResponse {
        private boolean ok;
        private String message;

        @JsonProperty("visual_accessibility")
        private List<AccessibilityScenario> visual_accessibility;

        @JsonProperty("vision_nova_request_id")
        private String vision_nova_request_id;

        // Default constructor
        public CreateScenariosResponse() {
        }

        public CreateScenariosResponse(boolean ok, String message, List<AccessibilityScenario> visual_accessibility, String vision_nova_request_id) {
            this.ok = ok;
            this.message = message;
            this.visual_accessibility = (visual_accessibility != null) ? visual_accessibility : new ArrayList<>();
            this.vision_nova_request_id = vision_nova_request_id;
        }

        public boolean isOk() {
            return this.ok;
        }
        
        public String getMessage() {
            return this.message;
        }

        public List<AccessibilityScenario> getAccessibilityScenarios() {
            return (visual_accessibility != null) ? visual_accessibility : new ArrayList<>();
        }

        public String getVisionNovaRequestId(){
            return this.vision_nova_request_id;
        }

        public void setScenarios(List<AccessibilityScenario> visual_accessibility) {
            this.visual_accessibility = visual_accessibility;
        }
    }

}
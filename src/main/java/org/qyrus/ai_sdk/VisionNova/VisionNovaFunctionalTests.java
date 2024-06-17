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

public class VisionNovaFunctionalTests{

    private final String apiKey;
    private String baseUrl;

    public VisionNovaFunctionalTests(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
        } else {
            this.baseUrl = Configurations.getDefaultGateway(); // Assumes this method exists in your Configurations class.
        }
    }

    public CreateScenariosResponse create(String image_url) {
        String url = this.baseUrl + "/" + Configurations.getVisionNovaContextPath("json-create-tests");
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


    public static class Scenario {
        @JsonProperty("scenario_name")
        private String scenario_name;

        @JsonProperty("objective")
        private String objective;

        @JsonProperty("description")
        private String description;

        @JsonProperty("steps")
        private List<String> steps;

        // Default constructor
        public Scenario() {
        }

        public Scenario(String scenario_name, String objective, String description, List<String> steps) {
            this.scenario_name = scenario_name;
            this.objective = objective;
            this.description = description;
            this.steps = steps;
        }

        public String getScenarioName(){
            return this.scenario_name;
        }

        public String getScenarioObjective(){
            return this.objective;
        }

        public String getScenarioDescription(){
            return this.description;
        }

        public List<String> getSteps(){
            return this.steps;
        }
    }

    public static class CreateScenariosResponse {
        private boolean ok;
        private String message;

        @JsonProperty("scenarios")
        private List<Scenario> scenarios;

        @JsonProperty("vision_nova_request_id")
        private String vision_nova_request_id;

        // Default constructor
        public CreateScenariosResponse() {
        }

        public CreateScenariosResponse(boolean ok, String message, List<Scenario> scenarios, String vision_nova_request_id) {
            this.ok = ok;
            this.message = message;
            this.scenarios = (scenarios != null) ? scenarios : new ArrayList<>();
            this.vision_nova_request_id = vision_nova_request_id;
        }

        public boolean isOk() {
            return this.ok;
        }
        
        public String getMessage() {
            return this.message;
        }

        public List<Scenario> getScenarios() {
            return (scenarios != null) ? scenarios : new ArrayList<>();
        }

        public String getVisionNovaRequestId(){
            return this.vision_nova_request_id;
        }

        public void setScenarios(List<Scenario> scenarios) {
            this.scenarios = scenarios;
        }
    }

}
package org.qyrus.ai_sdk.VisionNova;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.AsyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncVisionNovaFunctionalTests{

    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;
    
    public AsyncVisionNovaFunctionalTests(String apiKey, String baseUrl) {
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

    public CompletableFuture<CreateScenariosResponse> create(String image_url) {
        String url = this.baseUrl + "/" + Configurations.getVisionNovaContextPath("json-create-tests");
        Map<String, String> data = new HashMap<>();
        data.put("image_url", image_url);

        // Convert your Map to a JSON String, use your preferred library (like Gson or Jackson)
        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth()); // Assumed to be a method in your configurations
        headers.put("Custom", this.apiKey);

        AsyncHttpClient asyncClient = new AsyncHttpClient();
        CompletableFuture<HttpResponse<String>> responseFuture = 
            asyncClient.post(url, jsonRequestBody, headers);

        // This applies a function to the HttpResponse once it's available and returns the parsed CreateScenariosResponse
        return responseFuture.thenApplyAsync(response -> {
            try {
                return createScenariosResponseFromJson(response.body()); // This will parse JSON string into CreateScenariosResponse type
            } catch (Exception e) {
                // If an error happens in parsing, print stack trace and throw a RuntimeException; in real scenario, you'd handle this differently
                e.printStackTrace();
                throw new RuntimeException("Error processing the async response", e);
            }
        });
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
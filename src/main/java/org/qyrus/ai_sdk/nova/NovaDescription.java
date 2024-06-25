package org.qyrus.ai_sdk.nova;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NovaDescription {

    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;

    public NovaDescription(String apiKey, String baseUrl) {
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

    public CreateScenariosResponse create(String user_description) {
        
        String url = this.baseUrl + "/" + Configurations.getNovaContextPath("user");
        Map<String, String> data = new HashMap<>();
        data.put("user_description", user_description);
        

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
        @JsonProperty("test_script_name")
        private String test_script_name;

        @JsonProperty("test_script_objective")
        private String test_script_objective;

        @JsonProperty("reason_to_test")
        private String reason_to_test;

        @JsonProperty("criticality_description")
        private String criticality_description;

        @JsonProperty("criticality_score")
        private int criticality_score;

        // Default constructor
        public Scenario() {
        }

        public Scenario(String test_script_name, String test_script_objective, String reason_to_test, String criticality_description, int criticality_score) {
            this.test_script_name = test_script_name;
            this.test_script_objective = test_script_objective;
            this.reason_to_test = reason_to_test;
            this.criticality_description = criticality_description;
            this.criticality_score = criticality_score;
        }

        public String getTestScriptName(){
            return this.test_script_name;
        }

        public String getTestScriptObjective(){
            return this.test_script_objective;
        }

        public String getReasonToTest(){
            return this.reason_to_test;
        }

        public String getCriticalityDescription(){
            return this.criticality_description;
        }

        public int getCriticalityScore(){
            return this.criticality_score;
        }
    }

    public static class CreateScenariosResponse {
        private boolean ok;
        private String message;

        @JsonProperty("scenarios")
        private List<Scenario> scenarios;

        @JsonProperty("nova_request_id")
        private String nova_request_id;

        // Default constructor
        public CreateScenariosResponse() {
        }

        public CreateScenariosResponse(boolean ok, String message, List<Scenario> scenarios, String nova_request_id) {
            this.ok = ok;
            this.message = message;
            this.scenarios = (scenarios != null) ? scenarios : new ArrayList<>();
            this.nova_request_id = nova_request_id;
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

        public String getNovaRequestId(){
            return this.nova_request_id;
        }

        public void setScenarios(List<Scenario> scenarios) {
            this.scenarios = scenarios;
        }
    }
}

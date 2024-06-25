package org.qyrus.ai_sdk.APIAssertions;

// Necessary imports
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONBodyAssertion {

    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;
    
    public JSONBodyAssertion(String apiKey, String baseUrl) {
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

    public JsonBodyAssertionResponse create(Object example_response) {
        String url = baseUrl + "/" + Configurations.getAPIAssertionContextPath("jsonbody");
        
        Map<String, Object> data = new HashMap<>();
        data.put("response", example_response);

        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        try {
            HttpResponse<String> response = syncClient.post(url, jsonRequestBody, headers);
            System.out.println("RESPonse from the api " + response.body());
            return parseJsonBodyAssertionResponse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null; // Or handle accordingly
    }

    private String toJson(Object data) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonBodyAssertionResponse parseJsonBodyAssertionResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Assertion> assertionList = objectMapper.readValue(json, new TypeReference<List<Assertion>>() {});
            return new JsonBodyAssertionResponse(assertionList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class JsonBodyAssertionResponse {
        private List<Assertion> assertions;

        public JsonBodyAssertionResponse(List<Assertion> assertions) {
            this.assertions = assertions;
        }

        // Remove the @JsonProperty annotation since it's no longer needed
        public List<Assertion> getAssertions() {
            return assertions;
        }

        public void setAssertions(List<Assertion> assertions) {
            this.assertions = assertions;
        }
    }

    public static class Assertion {
        private String value;
        private String type;
        private String assertionDescription;

        public Assertion() {
        }

        @JsonProperty("value")
        public String getValue() {
            return value;
        }

        @JsonProperty("value")
        public void setValue(String value) {
            this.value = value;
        }

        @JsonProperty("type")
        public String getType() {
            return type;
        }

        @JsonProperty("type")
        public void setType(String type) {
            this.type = type;
        }

        @JsonProperty("assertionDescription")
        public String getAssertionDescription() {
            return assertionDescription;
        }

        @JsonProperty("assertionDescription")
        public void setAssertionDescription(String assertionDescription) {
            this.assertionDescription = assertionDescription;
        }
    }
}

package org.qyrus.ai_sdk.LLMEvaluator;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LLMEval {
    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;
    
    public LLMEval(String apiKey, String baseUrl) {
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
            throw new RuntimeException("An error occurred while setting up the LLM evaluator instance: " + e.getMessage());
        }
    }

    public LLMEvalResponse evaluate(String context, String expected_output, List<String> executed_output, String guardrails) {
        String url = this.baseUrl + "/" + Configurations.getLLMEvaluatorContextPath("judge");

        Map<String, Object> data = new HashMap<>();
        data.put("context", context);
        data.put("expected_output", expected_output);
        data.put("executed_output", executed_output);
        data.put("guardrails", guardrails);

        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        
        try {
            HttpResponse<String> response = syncClient.post(url, jsonRequestBody, headers);
            // Wrap the Swagger JSON in an object with a key
            return new LLMEvalResponse(response.body());
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
    public static class LLMEvalResponse {
        private String responseJson;
       
        public LLMEvalResponse(String responseJson) {
            this.responseJson = responseJson;
        }

        @JsonProperty("report")
        public String getReport() {
            return responseJson;
        }

    }
}

package org.qyrus.ai_sdk.APIAssertions;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.http.AsyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AsyncJSONPathAssertion {

    private final String apiKey;
    private String baseUrl;

    public AsyncJSONPathAssertion(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
        } else {
            this.baseUrl = Configurations.getDefaultGateway();
        }
    }

    public CompletableFuture<JsonPathAssertionResponse> create(Object example_response) {
        String url = this.baseUrl + "/" + Configurations.getAPIAssertionContextPath("jsonpath");
        Map<String, Object> data = new HashMap<>();
        data.put("response", example_response);

        String jsonRequestBody = toJson(data);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        AsyncHttpClient asyncClient = new AsyncHttpClient();
        CompletableFuture<HttpResponse<String>> responseFuture =
            asyncClient.post(url, jsonRequestBody, headers);

        return responseFuture.thenApplyAsync(response -> {
            try {
                return parseJsonPathAssertionResponse(response.body());
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

    private JsonPathAssertionResponse parseJsonPathAssertionResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Assertion> assertionList = objectMapper.readValue(json, new TypeReference<List<Assertion>>() {});
            return new JsonPathAssertionResponse(assertionList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class JsonPathAssertionResponse {
        // Assuming the response is a list of assertions
        private List<Assertion> assertions;

        public JsonPathAssertionResponse(List<Assertion> assertions) {
            this.assertions = assertions;
        }
        
        public List<Assertion> getAssertions() {
            return assertions;
        }

        public void setAssertions(List<Assertion> assertions) {
            this.assertions = assertions;
        }
    }

    public static class Assertion {
        private String jsonPath;
        private String jsonPathValue;
        private String type;
        private String assertionDescription;

        @JsonProperty("jsonPath")
        public String getJsonPath() {
            return jsonPath;
        }

        @JsonProperty("jsonPathValue")
        public String getJsonPathValue() {
            return jsonPathValue;
        }

        @JsonProperty("type")
        public String getType() {
            return type;
        }

        @JsonProperty("assertionDescription")
        public String getAssertionDescription() {
            return assertionDescription;
        }

        // Setters omitted for brevity
    }
}
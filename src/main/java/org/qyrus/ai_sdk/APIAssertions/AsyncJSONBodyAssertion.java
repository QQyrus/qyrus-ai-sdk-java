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

public class AsyncJSONBodyAssertion {

    private final String apiKey;
    private String baseUrl;

    public AsyncJSONBodyAssertion(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        if (baseUrl != null && !baseUrl.isEmpty()) {
            this.baseUrl = baseUrl.replace(".qyrus.com", "-gateway.qyrus.com");
        } else {
            this.baseUrl = Configurations.getDefaultGateway();
        }
    }

    public CompletableFuture<JsonBodyAssertionResponse> create(Object example_response) {
        String url = this.baseUrl + "/" + Configurations.getAPIAssertionContextPath("jsonbody");

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
                System.out.println("RESPonse from the api " + response.body());
                return parseJsonBodyAssertionResponse(response.body());
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

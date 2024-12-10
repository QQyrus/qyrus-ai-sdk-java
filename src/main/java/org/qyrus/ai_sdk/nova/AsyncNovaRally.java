package org.qyrus.ai_sdk.nova;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.AsyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Collections;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AsyncNovaRally{

    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;

    public AsyncNovaRally(String apiKey, String baseUrl) {
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
        } catch (IOException | InterruptedException e) {
            // Log the exception here
            e.printStackTrace();
            throw new RuntimeException("An error occurred while setting up the NovaRally instance: " + e.getMessage());
        }
    }

    public CompletableFuture<AsyncNovaDescription.CreateScenariosResponse> create(String rallyURL, String rallyAPIToken, String workspaceName, String ticketId) {
        // Step 1: Fetch workspaces asynchronously
        return getRallyWorkspacesAsync(rallyURL, rallyAPIToken)
            .thenApply(workspaces -> {
                
                // Step 2: Match the workspace by _refObjectName
                Map<String, Object> matchedWorkspace = workspaces.stream()
                    .filter(workspace -> workspaceName.equals(workspace.get("_refObjectName")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Workspace with name " + workspaceName + " not found."));
                
                return matchedWorkspace;
            })
            .thenCompose(matchedWorkspace -> {
                // Step 3: Extract the _ref parameter from the matched workspace
                String workspaceRef = (String) matchedWorkspace.get("_ref");
                if (workspaceRef == null) {
                    return CompletableFuture.failedFuture(
                        new RuntimeException("The _ref parameter is missing for the workspace: " + workspaceName)
                    );
                }

                // Step 4: Fetch ticket details asynchronously
                Map<String, String> ticketParams = Map.of("WORKSPACE_REF", workspaceRef, "TICKET_ID", ticketId, "RALLY_URL", rallyURL, "RALLY_API_KEY", rallyAPIToken);
                return getTicketDetails(ticketParams);
            })
            .thenCompose(ticketDetailsJson -> {
                // Step 5: Call the NovaDescription's create method
                // Assuming this is a synchronous call; if it's not, then additional refactoring may be needed.
                AsyncNovaDescription novaDescription = new AsyncNovaDescription(this.apiKey, null);
                
                // This step would need to be updated if NovaDescription create method can be made async.
                return novaDescription.create(ticketDetailsJson);
            });
    }


private CompletableFuture<List<Map<String, Object>>> getRallyWorkspacesAsync(String rallyURL, String rallyAPIToken) {

    String url = this.baseUrl + "/" + Configurations.getNovaContextPath("get_rally_workspaces");

    // Create headers map
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Authorization", Configurations.getAuth());
    headers.put("Custom", this.apiKey);

    Map<String, String> params = new HashMap<>();
    params.put("RALLY_URL", rallyURL);
    params.put("RALLY_API_KEY", rallyAPIToken);

    // Use AsyncHttpClient for the GET request
    AsyncHttpClient asyncClient = new AsyncHttpClient();
    return asyncClient.get(url, params, headers)
        .thenApplyAsync(response -> {
            try {
                // Handle the response using the previously defined method
                System.out.println("RESPONSE FROM GET WORKSPACES " + response.body());
                return parseWorkspacesFromJson(response.body());
            } catch (Exception e) {
                // Log or handle the exception as needed
                e.printStackTrace();
                throw new RuntimeException("Error parsing workspaces JSON.", e);
            }
        })
        .exceptionally(e -> {
            // Log or handle the exception as needed
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list on failure
        });
    }

    private List<Map<String, Object>> parseWorkspacesFromJson(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Assuming the responseBody structure is like: {"workspaces": [...]}
        Map<String, List<Map<String, Object>>> responseMap = objectMapper.readValue(
                responseBody, new TypeReference<Map<String, List<Map<String, Object>>>>() {});

        return responseMap.getOrDefault("workspaces", Collections.emptyList());
    }


    private CompletableFuture<String> getTicketDetails(Map<String, String> params) {
        String url = this.baseUrl + "/" + Configurations.getNovaContextPath("get_rally_ticket_details");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        AsyncHttpClient asyncClient = new AsyncHttpClient();
        
        return asyncClient.get(url, params, headers)
                .thenApplyAsync(response -> {
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Failed to fetch ticket details: " + response.body());
                    }
                    try {
                        // Parse response body
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode responseJson = objectMapper.readTree(response.body());

                        // Extract and return the fields if present
                        if (responseJson.has("ticket_title") && responseJson.has("description")) {
                            Map<String, String> result = new HashMap<>();
                            result.put("ticket_title", responseJson.get("ticket_title").asText());
                            result.put("description", responseJson.get("description").asText());

                            return objectMapper.writeValueAsString(result);
                        }
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Error parsing ticket details: " + response.body(), e);
                    }
                })
                .exceptionally(e -> {
                    // Log and handle exceptions
                    e.printStackTrace();
                    return null; // Return null or a default value, depending on your error handling policy
                });
    }
}


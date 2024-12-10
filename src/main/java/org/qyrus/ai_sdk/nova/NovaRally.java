package org.qyrus.ai_sdk.nova;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.Collections;
import java.net.URI;


import org.qyrus.ai_sdk.QIDP.QIDPUtils;
import org.qyrus.ai_sdk.http.SyncHttpClient;
import org.qyrus.ai_sdk.util.Configurations;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;


public class NovaRally{

    private final String apiKey;
    private String baseUrl;
    private String gatewayToken;

    public NovaRally(String apiKey, String baseUrl) {
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
            throw new RuntimeException("An error occurred while setting up the NovaJira instance: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> getRallyWorkspaces(String rallyURL, String rallyAPIToken) {

        //GET URL
        String url = this.baseUrl + "/" + Configurations.getNovaContextPath("get_rally_workspaces");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        Map<String, String> params = new HashMap<>();
        params.put("RALLY_URL", rallyURL);
        params.put("RALLY_API_KEY", rallyAPIToken);

        SyncHttpClient syncClient = new SyncHttpClient();
        try {
            System.out.println("GETTING WORKSPACES WITH THESE DETAILS " + "URL " + url + "PARAMS " + params + "HEADERS " + headers);
            // Make GET call with headers and params
            HttpResponse<String> response = syncClient.get(url, params, headers);

            System.out.println("STATUS CODE FOR GETTING TICKET DETAILS " + response.statusCode());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch workspaces: " + response.body());
            }

            // Parse response JSON into a list of workspace maps
            return parseWorkspacesFromJson(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list on failure
        }
    }

    private List<Map<String, Object>> parseWorkspacesFromJson(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Assuming the responseBody structure is like: {"workspaces": [...]}
        Map<String, List<Map<String, Object>>> responseMap = objectMapper.readValue(
                responseBody, new TypeReference<Map<String, List<Map<String, Object>>>>() {});

        return responseMap.getOrDefault("workspaces", Collections.emptyList());
    }

    private String getTicketDetails(Map<String, String> params) throws Exception {
        String url = this.baseUrl + "/" + Configurations.getNovaContextPath("get_rally_ticket_details");

        // Prepare headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", Configurations.getAuth());
        headers.put("Custom", this.apiKey);

        SyncHttpClient syncClient = new SyncHttpClient();
        
        // Make GET call with headers and params
        HttpResponse<String> response = syncClient.get(url, params, headers);

        System.out.println("STATUS CODE FOR GETTING TICKET DETAILS " + response.statusCode());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch ticket details: " + response.body());
        }
        // Parse response body
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(response.body());

        // Extract and return the "description" key if present
        if (responseJson.has("description")) {
            
            Map<String, String> result = new HashMap<>();
            result.put("ticket_title", responseJson.get("ticket_title").asText());
            result.put("description", responseJson.get("description").asText());

            return objectMapper.writeValueAsString(result);
        }
        
        return null; 

        
    }

    // Helper method to convert Map to JSON string
    // private String toJson(Map<String, String> map) {
    //     // Use a JSON library like Gson or Jackson to serialize the map
    //     // Placeholder implementation for illustration:
    //     return map.entrySet().stream()
    //             .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
    //             .collect(Collectors.joining(",", "{", "}"));
    // }


    public NovaDescription.CreateScenariosResponse create(String rallyURL, String rallyAPIToken, String workspaceName, String ticketId) {

        // Step 1: Fetch workspaces
        List<Map<String, Object>> workspaces = getRallyWorkspaces(rallyURL, rallyAPIToken);
        System.out.println("Workspaces " + workspaces);
        // Step 2: Match the workspace by _refObjectName
        Map<String, Object> matchedWorkspace = null;
        for (Map<String, Object> workspace : workspaces) {
            if (workspaceName.equals(workspace.get("_refObjectName"))) {
                matchedWorkspace = workspace;
                break;
            }
        }

        if (matchedWorkspace == null) {
            throw new RuntimeException("Workspace with name " + workspaceName + " not found.");
        }

        // Step 3: Extract the _ref parameter from the matched workspace
        String workspaceRef = (String) matchedWorkspace.get("_ref");
        if (workspaceRef == null) {
            throw new RuntimeException("The _ref parameter is missing for the workspace: " + workspaceName);
        }

        try {
            // Step 4: Fetch ticket details
            Map<String, String> ticketParams = Map.of("WORKSPACE_REF", workspaceRef, "TICKET_ID", ticketId, "RALLY_URL", rallyURL, "RALLY_API_KEY", rallyAPIToken);
            String ticketDetailsJson = getTicketDetails(ticketParams);

            // Step 5: Call the AsyncNovaDescription's create method
            NovaDescription novaDescription = new NovaDescription(this.apiKey, null);
            return novaDescription.create(ticketDetailsJson);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create scenarios: " + e.getMessage(), e);
        }
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

    private NovaDescription.CreateScenariosResponse createScenariosResponseFromJson(String json) {
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, NovaDescription.CreateScenariosResponse.class);
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

        @JsonProperty("jira_id")
        private String jira_id;

        @JsonProperty("jira_title")
        private String jira_title;

        @JsonProperty("jira_description")
        private String jira_description;

        @JsonProperty("jira_endpoint")
        private String jira_endpoint;

        @JsonProperty("nova_request_id")
        private String nova_request_id;

        // Default constructor
        public CreateScenariosResponse() {
        }

        public CreateScenariosResponse(boolean ok, String message, List<Scenario> scenarios, String jira_id, String jira_description, String jira_endpoint, String jira_title, String nova_request_id) {
            this.ok = ok;
            this.message = message;
            this.scenarios = (scenarios != null) ? scenarios : new ArrayList<>();
            this.jira_id = jira_id;
            this.jira_title = jira_title;
            this.jira_description = jira_description;
            this.jira_endpoint = jira_endpoint;
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

        public String getJiraId(){
            return this.jira_id;
        }

        public String getJiraTitle(){
            return this.jira_title;
        }

        public String getJiraDescription(){
            return this.jira_description;
        }

        public String getJiraEndpoint(){
            return this.jira_endpoint;
        }

        public String getNovaRequestId(){
            return this.nova_request_id;
        }


        public void setScenarios(List<Scenario> scenarios) {
            this.scenarios = scenarios;
        }
    }
}

package org.qyrus.ai_sdk.nova;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;



public class CreateScenariosResponse {
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
package org.qyrus.ai_sdk.nova;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Scenario {
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
package org.qyrus.ai_sdk.util;

public class Configurations {

    public static String getAuth() {
        return "Bearer 90540897-748a-3ef2-b3a3-c6f8f42022da";
    }

    public static String getDefaultGateway() {
        return "https://stg-gateway.quinnox.info:8243";
    }

    // public static String getDefaultGateway() {
    //     return "https://0c893a0c-6d38-4afb-b2c8-4c18ca3af327.mock.pstmn.io";
    // }

    public static String getNovaContextPath(String from) {
        return "nova-sdk/v1/api/nova_" + from;
    }

    public static String getAPIBuilderContextPath(String from) {
        return "api-builder-sdk/v1/api/" + from;
    }

    public static String getDataAmplifierContextPath() {
        return "synthetic-data-generator-sdk/v1/api/datagen";
    }

    public static String getAPIAssertionContextPath(String from) {
        return "api-assertion-gpt-sdk/v1/api/assertion/" + from;
    }

    public static String getVisionNovaContextPath(String from) {
        return "vision-nova-sdk/v1/api/" + from;
    }

    public static String getLLMEvaluatorContextPath(String from){
        return "llm-evaluator-sdk/v1/" + from;
    }
        
   
}

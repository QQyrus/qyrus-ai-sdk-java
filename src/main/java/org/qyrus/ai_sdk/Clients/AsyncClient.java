package org.qyrus.ai_sdk.Clients;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.APIAssertions.AsyncHeaderAssertion;
import org.qyrus.ai_sdk.APIAssertions.AsyncJSONBodyAssertion;
import org.qyrus.ai_sdk.APIAssertions.AsyncJSONPathAssertion;
import org.qyrus.ai_sdk.APIAssertions.AsyncJSONSchemaAssertion;
import org.qyrus.ai_sdk.APIBuilder.AsyncAPIBuilder;
import org.qyrus.ai_sdk.DataAmplifier.AsyncDataAmplifier;
import org.qyrus.ai_sdk.VisionNova.AsyncVisionNovaAccessibilityTests;
import org.qyrus.ai_sdk.VisionNova.AsyncVisionNovaFunctionalTests;
import org.qyrus.ai_sdk.nova.AsyncNovaDescription;
import org.qyrus.ai_sdk.nova.AsyncNovaJira;

public class AsyncClient {

    public class NovaWrapper {
        public class FromJira{
            public CompletableFuture<AsyncNovaJira.CreateScenariosResponse> create(String jiraEndpoint, String jiraApiToken, String jiraUsername, String jiraId) {
                return novaJira.create(jiraEndpoint, jiraApiToken, jiraUsername, jiraId);
            }
        }

        public class FromDescription{
            public CompletableFuture<AsyncNovaDescription.CreateScenariosResponse> create(String use_case_description) {
                return novaDescription.create(use_case_description);
            }
        }

        public final FromJira from_jira;
        public final FromDescription from_description;

        private AsyncNovaJira novaJira;
        private AsyncNovaDescription novaDescription;

        public NovaWrapper(String apiToken, String baseUrl){
            this.novaJira = new AsyncNovaJira(apiToken, baseUrl);
            this.novaDescription = new AsyncNovaDescription(apiToken, baseUrl);
            this.from_jira = new FromJira();
            this.from_description = new FromDescription();
        }
    }

    public class VisionNovaWrapper{
        public class AccessibilityTests{
            public CompletableFuture<AsyncVisionNovaAccessibilityTests.CreateScenariosResponse> create(String image_url) {
            return vision_nova_acc.create(image_url);
        }
        }

        public class FunctionalTests{
            public CompletableFuture<AsyncVisionNovaFunctionalTests.CreateScenariosResponse> create(String image_url) {
            return vision_nova_fun.create(image_url);
        }
        }

        public final AccessibilityTests accessibility_tests;
        public final FunctionalTests functional_tests;

        private AsyncVisionNovaAccessibilityTests vision_nova_acc;
        private AsyncVisionNovaFunctionalTests vision_nova_fun;


        public VisionNovaWrapper(String apiToken, String baseUrl){
            this.vision_nova_acc = new AsyncVisionNovaAccessibilityTests(apiToken, baseUrl);
            this.vision_nova_fun = new AsyncVisionNovaFunctionalTests(apiToken, baseUrl);
            this.accessibility_tests = new AccessibilityTests();
            this.functional_tests = new FunctionalTests();
        }
    }
    
    public class APIBuilderWrapper{
        public CompletableFuture<AsyncAPIBuilder.APIBuilderResponse> build(String user_description) {
            return apibuilder.create(user_description);
        }
        private AsyncAPIBuilder apibuilder;

        public APIBuilderWrapper(String apiToken, String baseUrl){
            this.apibuilder = new AsyncAPIBuilder(apiToken, baseUrl);
        }
    }

    public class DataAmplifierWrapper{

        public CompletableFuture<AsyncDataAmplifier.DataAmplifierResponse> amplify(List<Map<String, Object>> example_data, int data_count) {
            return dataamplifier.create(example_data, data_count);
        }

        private AsyncDataAmplifier dataamplifier;

        public DataAmplifierWrapper(String apiToken, String baseUrl){
            this.dataamplifier = new AsyncDataAmplifier(apiToken, baseUrl);
        }
    }


    public class APIAssertionsWrapper{

        public class HeadersWrapper{
            public CompletableFuture<AsyncHeaderAssertion.HeaderAssertionResponse> create(String response_headers){
                return headerassertion.create(response_headers);
            }
        }

        public class JSONBodyWrapper{
            public CompletableFuture<AsyncJSONBodyAssertion.JsonBodyAssertionResponse> create(Object response){
                return jsonbodyassertion.create(response);
            }
        }

        public class JSONSchemaWrapper{
            public CompletableFuture<AsyncJSONSchemaAssertion.JsonSchemaResponse> create(Object response){
                return jsonschemaassertion.create(response);
            }
        }

        public class JSONPathWrapper{
            public CompletableFuture<AsyncJSONPathAssertion.JsonPathAssertionResponse> create(Object response){
                return jsonpathassertion.create(response);
            }
        }

        public final HeadersWrapper headers;
        public final JSONBodyWrapper jsonbody;
        public final JSONSchemaWrapper jsonschema;
        public final JSONPathWrapper jsonpath;

        private AsyncHeaderAssertion headerassertion;
        private AsyncJSONBodyAssertion jsonbodyassertion;
        private AsyncJSONPathAssertion jsonpathassertion;
        private AsyncJSONSchemaAssertion jsonschemaassertion;

        public APIAssertionsWrapper(String apiToken, String baseUrl){
            this.headers = new HeadersWrapper();
            this.jsonbody = new JSONBodyWrapper();
            this.jsonschema = new JSONSchemaWrapper();
            this.jsonpath = new JSONPathWrapper();

            this.headerassertion = new AsyncHeaderAssertion(apiToken, baseUrl);
            this.jsonbodyassertion = new AsyncJSONBodyAssertion(apiToken, baseUrl);
            this.jsonpathassertion  = new AsyncJSONPathAssertion(apiToken, baseUrl);
            this.jsonschemaassertion = new AsyncJSONSchemaAssertion(apiToken, baseUrl);
            
        }
    }

    public NovaWrapper nova;
    public VisionNovaWrapper vision_nova;
    public APIBuilderWrapper api_builder;
    public APIAssertionsWrapper api_assertions;
    public DataAmplifierWrapper data_amplifier;

    public AsyncClient(String apiToken, String baseUrl) {
        this.nova = new NovaWrapper(apiToken, baseUrl);
        this.vision_nova = new VisionNovaWrapper(apiToken, baseUrl);
        this.api_builder = new APIBuilderWrapper(apiToken, baseUrl);
        this.api_assertions = new APIAssertionsWrapper(apiToken, baseUrl);
        this.data_amplifier = new DataAmplifierWrapper(apiToken, baseUrl);
    }
}

package org.qyrus.ai_sdk.Clients;

import java.util.List;
import java.util.Map;

import org.qyrus.ai_sdk.APIAssertions.HeaderAssertion;
import org.qyrus.ai_sdk.APIAssertions.JSONBodyAssertion;
import org.qyrus.ai_sdk.APIAssertions.JSONPathAssertion;
import org.qyrus.ai_sdk.APIAssertions.JSONSchemaAssertion;
import org.qyrus.ai_sdk.APIBuilder.APIBuilder;
import org.qyrus.ai_sdk.DataAmplifier.DataAmplifier;
import org.qyrus.ai_sdk.VisionNova.VisionNovaAccessibilityTests;
import org.qyrus.ai_sdk.VisionNova.VisionNovaFunctionalTests;
import org.qyrus.ai_sdk.nova.NovaDescription;
import org.qyrus.ai_sdk.nova.NovaJira;

public class SyncClient {

    public class NovaWrapper {
        public class FromJira{
            public NovaJira.CreateScenariosResponse create(String jiraEndpoint, String jiraApiToken, String jiraUsername, String jiraId) {
            return novaJira.create(jiraEndpoint, jiraApiToken, jiraUsername, jiraId);
        }
        }

        public class FromDescription{
            public NovaDescription.CreateScenariosResponse create(String use_case_description) {
            return novaDescription.create(use_case_description);
        }
        }

        public final FromJira from_jira;
        public final FromDescription from_description;

        private NovaJira novaJira;
        private NovaDescription novaDescription;


        public NovaWrapper(String apiToken, String baseUrl){
            this.novaJira = new NovaJira(apiToken, baseUrl);
            this.novaDescription = new NovaDescription(apiToken, baseUrl);
            this.from_jira = new FromJira();
            this.from_description = new FromDescription();
        }
    }
    
    public class VisionNovaWrapper{
        public class AccessibilityTests{
            public VisionNovaAccessibilityTests.CreateScenariosResponse create(String image_url) {
            return vision_nova_acc.create(image_url);
        }
        }
        
        public class FunctionalTests{
            public VisionNovaFunctionalTests.CreateScenariosResponse create(String image_url) {
            return vision_nova_fun.create(image_url);
        }
        }

        public final AccessibilityTests accessibility_tests;
        public final FunctionalTests functional_tests;

        private VisionNovaAccessibilityTests vision_nova_acc;
        private VisionNovaFunctionalTests vision_nova_fun;


        public VisionNovaWrapper(String apiToken, String baseUrl){
            this.vision_nova_acc = new VisionNovaAccessibilityTests(apiToken, baseUrl);
            this.vision_nova_fun = new VisionNovaFunctionalTests(apiToken, baseUrl);
            this.accessibility_tests = new AccessibilityTests();
            this.functional_tests = new FunctionalTests();
        }
    }

    public class APIBuilderWrapper{
        public APIBuilder.APIBuilderResponse build(String user_description) {
            return apibuilder.create(user_description);
        }
        private APIBuilder apibuilder;

        public APIBuilderWrapper(String apiToken, String baseUrl){
            this.apibuilder = new APIBuilder(apiToken, baseUrl);
        }
    }

    public class DataAmplifierWrapper{

        public DataAmplifier.DataAmplifierResponse amplify(List<Map<String, Object>> example_data, int data_count) {
            return dataamplifier.create(example_data, data_count);
        }

        private DataAmplifier dataamplifier;

        public DataAmplifierWrapper(String apiToken, String baseUrl){
            this.dataamplifier = new DataAmplifier(apiToken, baseUrl);
        }
    }

    public class APIAssertionsWrapper{

        public class HeadersWrapper{
            public HeaderAssertion.HeaderAssertionResponse create(String response_headers){
                return headerassertion.create(response_headers);
            }
        }

        public class JSONBodyWrapper{
            public JSONBodyAssertion.JsonBodyAssertionResponse create(Object response){
                return jsonbodyassertion.create(response);
            }
        }

        public class JSONSchemaWrapper{
            public JSONSchemaAssertion.JsonSchemaResponse create(Object response){
                return jsonschemaassertion.create(response);
            }
        }

        public class JSONPathWrapper{
            public JSONPathAssertion.JsonPathAssertionResponse create(Object response){
                return jsonpathassertion.create(response);
            }
        }

        public final HeadersWrapper headers;
        public final JSONBodyWrapper jsonbody;
        public final JSONSchemaWrapper jsonschema;
        public final JSONPathWrapper jsonpath;

        private HeaderAssertion headerassertion;
        private JSONBodyAssertion jsonbodyassertion;
        private JSONPathAssertion jsonpathassertion;
        private JSONSchemaAssertion jsonschemaassertion;

        public APIAssertionsWrapper(String apiToken, String baseUrl){
            this.headers = new HeadersWrapper();
            this.jsonbody = new JSONBodyWrapper();
            this.jsonschema = new JSONSchemaWrapper();
            this.jsonpath = new JSONPathWrapper();

            this.headerassertion = new HeaderAssertion(apiToken, baseUrl);
            this.jsonbodyassertion = new JSONBodyAssertion(apiToken, baseUrl);
            this.jsonpathassertion  = new JSONPathAssertion(apiToken, baseUrl);
            this.jsonschemaassertion = new JSONSchemaAssertion(apiToken, baseUrl);
            
        }
    }

    public NovaWrapper nova;
    public VisionNovaWrapper vision_nova;
    public APIBuilderWrapper api_builder;
    public APIAssertionsWrapper api_assertions;
    public DataAmplifierWrapper data_amplifier;

    public SyncClient(String apiToken, String baseUrl) {
        this.nova = new NovaWrapper(apiToken, baseUrl);
        this.vision_nova = new VisionNovaWrapper(apiToken, baseUrl);
        this.api_builder = new APIBuilderWrapper(apiToken, baseUrl);
        this.api_assertions = new APIAssertionsWrapper(apiToken, baseUrl);
        this.data_amplifier = new DataAmplifierWrapper(apiToken, baseUrl);
    }
}

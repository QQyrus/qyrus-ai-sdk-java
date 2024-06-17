package org.qyrus.ai_sdk.Clients;

import java.util.concurrent.CompletableFuture;

import org.qyrus.ai_sdk.VisionNova.AsyncVisionNovaAccessibilityTests;
import org.qyrus.ai_sdk.VisionNova.AsyncVisionNovaFunctionalTests;
import org.qyrus.ai_sdk.nova.AsyncNovaDescription;
import org.qyrus.ai_sdk.nova.AsyncNovaJira;

public class AsyncClient {

    public class Nova {
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

        public Nova(String apiToken, String baseUrl){
            this.novaJira = new AsyncNovaJira(apiToken, baseUrl);
            this.novaDescription = new AsyncNovaDescription(apiToken, baseUrl);
            this.from_jira = new FromJira();
            this.from_description = new FromDescription();
        }
    }

    public class VisionNova{
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


        public VisionNova(String apiToken, String baseUrl){
            this.vision_nova_acc = new AsyncVisionNovaAccessibilityTests(apiToken, baseUrl);
            this.vision_nova_fun = new AsyncVisionNovaFunctionalTests(apiToken, baseUrl);
            this.accessibility_tests = new AccessibilityTests();
            this.functional_tests = new FunctionalTests();
        }
    }
    

    public Nova nova;
    public VisionNova vision_nova;

    public AsyncClient(String apiToken, String baseUrl) {
        this.nova = new Nova(apiToken, baseUrl);
        this.vision_nova = new VisionNova(apiToken, baseUrl);
    }
}

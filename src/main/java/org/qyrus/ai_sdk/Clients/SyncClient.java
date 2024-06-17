package org.qyrus.ai_sdk.Clients;

import org.qyrus.ai_sdk.VisionNova.VisionNovaAccessibilityTests;
import org.qyrus.ai_sdk.VisionNova.VisionNovaFunctionalTests;
import org.qyrus.ai_sdk.nova.NovaDescription;
import org.qyrus.ai_sdk.nova.NovaJira;

public class SyncClient {

    public class Nova {
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


        public Nova(String apiToken, String baseUrl){
            this.novaJira = new NovaJira(apiToken, baseUrl);
            this.novaDescription = new NovaDescription(apiToken, baseUrl);
            this.from_jira = new FromJira();
            this.from_description = new FromDescription();
        }
    }
    
    public class VisionNova{
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


        public VisionNova(String apiToken, String baseUrl){
            this.vision_nova_acc = new VisionNovaAccessibilityTests(apiToken, baseUrl);
            this.vision_nova_fun = new VisionNovaFunctionalTests(apiToken, baseUrl);
            this.accessibility_tests = new AccessibilityTests();
            this.functional_tests = new FunctionalTests();
        }
    }
    public Nova nova;
    public VisionNova vision_nova;

    public SyncClient(String apiToken, String baseUrl) {
        this.nova = new Nova(apiToken, baseUrl);
        this.vision_nova = new VisionNova(apiToken, baseUrl);
    }
}

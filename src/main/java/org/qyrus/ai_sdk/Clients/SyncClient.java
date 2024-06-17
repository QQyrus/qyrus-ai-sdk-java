package org.qyrus.ai_sdk.Clients;

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
    
    public Nova nova;

    public SyncClient(String apiToken, String baseUrl) {
        this.nova = new Nova(apiToken, baseUrl);
    }
}

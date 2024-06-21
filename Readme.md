# Installation 🔧

> git clone https://github.com/QQyrus/qyrus-ai-sdk-java.git

In the repo folder where pom.xml is present, run this command to create a jar in local mvn cache

> mvc clean install

<br></br>
# Usage 🚀



## Obtain AI SDK API Token

Get your API Token from qyrus support team or create your api token by signing up to Qyrus

---------------------------------------------------------------------------

## Install SDK

Add this dependency in your repository where you want to install this sdk in pom.xml

```
<dependencies>
    <dependency>
        <groupId>org.qyrus.ai_sdk</groupId>
        <artifactId>qyrus-sdk-java</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

--------------------------------------------------------------------------
## Run the code

Below you will find the samples to use the SDK
Once you include the code, run the code using

>mvn compile exec:java

---------------------------------------------------------------------------
## CLIENTS

There are 2 clients available with the SDK

<b>SyncClient</b> and <b>AsyncClient</bs>
 

```
import org.qyrus.ai_sdk.Clients.AsyncClient;
AsyncClient client = new AsyncClient(<API_TOKEN>, null);

import org.qyrus.ai_sdk.Clients.SyncClient;
SyncClient client = new SyncClient(<API_TOKEN>, null);

```

## NOVA

<b> NOVA </b> api allows you to create test cases for your use case
using either <b> Use Case Description </b> or <b> JIRA details </b>


Lets look at example of using Nova using <b>SyncClient</b>

#### Using Description

```
private static void createTestScenariosWithDescription(){

    SyncClient client = new SyncClient(<API_TOKEN>, null);
    String use_case_description = <USE_CASE_DESCRIPTION>;

    try {
        NovaDescription.CreateScenariosResponse response = client.nova.from_description.create(use_case_description);
        System.out.println("Creation Status: " + response.isOk());
        System.out.println("Creation Status: " + response.getMessage());

        //Show all the scenarios generated
        if (response != null && response.isOk() == true && response.getScenarios() != null) {
            System.out.println("Creation Status: " + response.getNovaRequestId());

            List<NovaDescription.Scenario> scenarios = response.getScenarios();

            // Loop over the list and print out scenarios
            for (NovaDescription.Scenario scenario : scenarios) {
                System.out.println("Test Script Name: " + scenario.getTestScriptName());
                System.out.println("Test Script Objective: " + scenario.getTestScriptObjective());
                System.out.println("Reason to Test: " + scenario.getReasonToTest());
                System.out.println("Criticality Description: " + scenario.getCriticalityDescription());
                System.out.println("Criticality Score: " + scenario.getCriticalityScore());
                System.out.println("--------------------------------------");
            }
        }
        
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}

```

#### Using JIRA

```
private static void createTestScenariosWithJira(){

    SyncClient client = new SyncClient(<API_TOKEN>, null);

    String jiraEndpoint = <JIRA_ENDPOINT>;
    String jiraApiToken = <JIRA_API_TOKEN>;
    String jiraUsername = <JIRA_USERNAME>;
    String jiraId = <JIRA_ID>;

    

    try {
        NovaJira.CreateScenariosResponse response = client.nova.from_jira.create(jiraEndpoint, jiraApiToken, jiraUsername, jiraId);
        System.out.println("Creation Status: " + response.isOk());
        System.out.println("Creation Status: " + response.getMessage());
        

        //Show all the scenarios generated
        if (response != null && response.isOk() == true && response.getScenarios() != null) {
            System.out.println("Creation Status: " + response.getNovaRequestId());
            System.out.println("Creation Status: " + response.getJiraTitle());
            System.out.println("Creation Status: " + response.getJiraDescription());

            List<NovaJira.Scenario> scenarios = response.getScenarios();

            // Loop over the list and print out scenarios
            for (NovaJira.Scenario scenario : scenarios) {
                System.out.println("Test Script Name: " + scenario.getTestScriptName());
                System.out.println("Test Script Objective: " + scenario.getTestScriptObjective());
                System.out.println("Reason to Test: " + scenario.getReasonToTest());
                System.out.println("Criticality Description: " + scenario.getCriticalityDescription());
                System.out.println("Criticality Score: " + scenario.getCriticalityScore());
                System.out.println("--------------------------------------");
            }
        }
        
        
    } catch (Exception e) {
        e.printStackTrace();
    }
    
}
```

Lets look at How to use Async client nova apis


With User Description

```
private static void asyncCreateTestScenariosWithDescription(){

    AsyncClient client = new AsyncClient(<API_TOKEN>, null);
    String use_case_description = <USE_CASE_DESCRIPTION>


    int numberOfRequests = 2;

    long startTime = System.currentTimeMillis();

    List<CompletableFuture<AsyncNovaDescription.CreateScenariosResponse>> futures = new ArrayList<>();

    for (int i = 0; i < numberOfRequests; i++) {
    
        CompletableFuture<AsyncNovaDescription.CreateScenariosResponse> responseFuture = client.nova.from_description.create(use_case_description);

        responseFuture.thenAccept(response -> {
            if (response.isOk()) {
                System.out.println("Creation Status: " + response.getNovaRequestId());

                List<AsyncNovaDescription.Scenario> scenarios = response.getScenarios();

                // Loop over the list and print out scenarios
                for (AsyncNovaDescription.Scenario scenario : scenarios) {
                    System.out.println("Test Script Name: " + scenario.getTestScriptName());
                    System.out.println("Test Script Objective: " + scenario.getTestScriptObjective());
                    System.out.println("Reason to Test: " + scenario.getReasonToTest());
                    System.out.println("Criticality Description: " + scenario.getCriticalityDescription());
                    System.out.println("Criticality Score: " + scenario.getCriticalityScore());
                    System.out.println("--------------------------------------");
                }
            }
            else {
                System.out.println("Creation failed with message: " + response.getMessage());
            }
    }).exceptionally(ex -> {
        System.out.println("An error occurred: " + ex.getMessage());
        ex.printStackTrace();
        return null;
    });

    // Keep your program running until all futures are resolved
    // responseFuture.join(); // Block and wait for the future to complete
    futures.add(responseFuture);
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    long endTime = System.currentTimeMillis();
    System.out.println("Asynchronous total time for Nova with Description" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
    
}

```

With Jira Details

```
private static void asyncCreateTestScenariosWithJira(){
    AsyncClient client = new AsyncClient(<API_TOKEN>, null);

    String jiraEndpoint = <JIRA_ENDPOINT>;
    String jiraApiToken = <JIRA_API_TOKEN>;
    String jiraUsername = <JIRA_USERNAME>;
    String jiraId = <JIRA_ID>;

    int numberOfRequests = 2;
    
    long startTime = System.currentTimeMillis();

    List<CompletableFuture<AsyncNovaJira.CreateScenariosResponse>> futures = new ArrayList<>();

    for (int i = 0; i < numberOfRequests; i++) {

        CompletableFuture<AsyncNovaJira.CreateScenariosResponse> responseFuture =
            client.nova.from_jira.create(jiraEndpoint, jiraApiToken, jiraUsername, jiraId);

        responseFuture.thenAccept(response -> {
            if (response.isOk()) {
                System.out.println("Creation Status: " + response.isOk());
                System.out.println("Message: " + response.getMessage());

                for (AsyncNovaJira.Scenario scenario : response.getScenarios()) {
                    System.out.println("Test Script Name: " + scenario.getTestScriptName());
                    System.out.println("Test Script Objective: " + scenario.getTestScriptObjective());
                    System.out.println("Reason to Test: " + scenario.getReasonToTest());
                    System.out.println("Criticality Description: " + scenario.getCriticalityDescription());
                    System.out.println("Criticality Score: " + scenario.getCriticalityScore());
                    System.out.println("--------------------------------------");
                }
            } else {
                System.out.println("Creation failed with message: " + response.getMessage());
            }
        }).exceptionally(ex -> {
            System.out.println("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });

    // Keep your program running until all futures are resolved
    // responseFuture.join(); // Block and wait for the future to complete
    futures.add(responseFuture);
    }

    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    long endTime = System.currentTimeMillis();
    System.out.println("Asynchronous total time for Nova with Jira" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
    
}
```


## VISION NOVA

<b>VISION NOVA</b> allows you to create test scenarios for the app screen and also allows you to create accessibility tests. Provide your

Lets look at using it with SyncClient

```
private static void createTestVisionNovaScenarios(){

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        String image_url = "https://s3-us-west-2.amazonaws.com/ctc-qa-ai/ai/demo/public/images/home-loan-screenshot.png";

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;

        for (int i = 0; i < numberOfRequests; i++) {
            try {
                VisionNovaFunctionalTests.CreateScenariosResponse response = client.vision_nova.functional_tests.create(image_url);
                System.out.println("Creation Status: " + response.isOk());
                System.out.println("Creation Status: " + response.getMessage());
        
                //Show all the scenarios generated
                if (response != null && response.isOk() == true && response.getScenarios() != null) {
                    System.out.println("Creation Status: " + response.getVisionNovaRequestId());

                    List<VisionNovaFunctionalTests.Scenario> scenarios = response.getScenarios();

                    // Loop over the list and print out scenarios
                    for (VisionNovaFunctionalTests.Scenario scenario : scenarios) {
                        System.out.println("Scenario Name: " + scenario.getScenarioName());
                        System.out.println("Scenario Objective: " + scenario.getScenarioObjective());
                        System.out.println("Scenario Description: " + scenario.getScenarioDescription());
                        System.out.println("Scenario Steps: " + scenario.getSteps());
                        
                        System.out.println("--------------------------------------");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous total time for Vision Nova functional tests" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
    }
```

With Async Client 

```
private static void asyncCreateTestVisionNovaAccessibilityTests(){

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        String image_url = "https://s3-us-west-2.amazonaws.com/ctc-qa-ai/ai/demo/public/images/home-loan-screenshot.png";
    
        int numberOfRequests = 2;
        
        long startTime = System.currentTimeMillis();

        List<CompletableFuture<AsyncVisionNovaAccessibilityTests.CreateScenariosResponse>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<AsyncVisionNovaAccessibilityTests.CreateScenariosResponse> responseFuture = client.vision_nova.accessibility_tests.create(image_url);
                
        
            responseFuture.thenAccept(response -> {
            if (response.isOk()) {
                System.out.println("Creation Status: " + response.getVisionNovaRequestId());

                List<AsyncVisionNovaAccessibilityTests.AccessibilityScenario> scenarios = response.getAccessibilityScenarios();

                // Loop over the list and print out scenarios
                for (AsyncVisionNovaAccessibilityTests.AccessibilityScenario scenario : scenarios) {
                    System.out.println("Accessibility type: " + scenario.getAccessibilityType());
                    System.out.println("Accessibility Comment: " + scenario.getAccessibilityComment());
                    
                    
                    System.out.println("--------------------------------------");
                }

            }
            else {
                System.out.println("Creation failed with message: " + response.getMessage());
            }
            }).exceptionally(ex -> {
                System.out.println("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

        // Keep your program running until all futures are resolved
        // responseFuture.join(); // Block and wait for the future to complete
        futures.add(responseFuture);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for Vision Nova Accessibility Tests" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
        
    } 
```

## API BUILDER

<b> API BUILDER </b> allows you to create the apis to your use case. It returns you the swagger documentation. Input your use case description.

With Sync

```
private static void testAPIBuilder() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        String userInputDescription = "hello world get api"; // Your input to the function.

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;

        for (int i = 0; i < numberOfRequests; i++) {
            try {
                // Assuming there is an api_builder field in SyncClient and a create method that matches the described input
                APIBuilder.APIBuilderResponse response = client.api_builder.build(userInputDescription);
                // Assuming the APIBuilderResponse class has a getSwaggerJson method to retrieve the swagger json
                String swaggerJson = response.getSwaggerJson();
                System.out.println("Generated Swagger JSON: " + swaggerJson);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous Total time for API Builder request: " + (endTime - startTime) + " ms");
    }

```

With Async

```
private static void AsyncTestAPIBuilder() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        String userInputDescription = "hello world get api"; // Your input to the function.

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        List<CompletableFuture<AsyncAPIBuilder.APIBuilderResponse>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfRequests; i++) {
            
            // Assuming there is an api_builder field in SyncClient and a create method that matches the described input
            CompletableFuture<AsyncAPIBuilder.APIBuilderResponse> responseFuture = client.api_builder.build(userInputDescription);
            // Assuming the APIBuilderResponse class has a getSwaggerJson method to retrieve the swagger json

            responseFuture.thenAccept(response -> {
                String swaggerJson = response.getSwaggerJson();
                System.out.println("Generated Swagger JSON: " + swaggerJson);
                
             }).exceptionally(ex -> {
            System.out.println("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });

        // // Keep your program running until all futures are resolved
        // responseFuture.join(); // Block and wait for the future to complete
        futures.add(responseFuture);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for API Builder" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
    }

```

## Data Amplifier

<b> Data Amplifier </b> Lets you create similar test data to one you already have. Give example data as the request and number of rows to generate

With sync
```
private static void testDataAmplification() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        // Assuming SyncClient is similar to the provided NovaJira code and has a 'data_amplifier' field and an 'amplify' method
        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);

        
        List<Map<String, Object>> example_data = List.of(
            Map.of(
                "column_name", "name",
                "column_description", "name",
                "column_restriction", "no restrictions",
                "column_values", List.of("Sameer Seikh", "Sunil Dutt")
            ),
            Map.of(
                "column_name", "email",
                "column_description", "email",
                "column_restriction", "no restrictions",
                "column_values", List.of("sameer.seikh@gmail.com","sunil.dutt88@hotmail.com")
            )
            
        );

        int data_count = 10;

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;

        for (int i = 0; i < numberOfRequests; i++) {

            try {
                // Accessing the data_amplifier's amplify method the same way as it's described in the input
                DataAmplifier.DataAmplifierResponse response = client.data_amplifier.amplify(example_data, data_count);
                System.out.println("Amplification Status: " + response.isStatus());
                System.out.println("Amplification Message: " + response.getMessage());

                // Assuming DataAmplifierResponse contains a 'getData' method to access generated data
                Map<String, List<String>> amplifiedData = response.getData();
                
                // Iterate over the map to display generated data
                for (Map.Entry<String, List<String>> entry : amplifiedData.entrySet()) {
                    System.out.println("Column: " + entry.getKey());
                    System.out.println("Values: " + entry.getValue());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Total time for Data Amplification request: " + (endTime - startTime) + " ms");
    }

```

With Async

```

private static void asyncTestDataAmplification() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        // Assuming SyncClient is similar to the provided NovaJira code and has a 'data_amplifier' field and an 'amplify' method
        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        int numberOfRequests = 2;
        List<Map<String, Object>> example_data = List.of(
            Map.of(
                "column_name", "name",
                "column_description", "name",
                "column_restriction", "no restrictions",
                "column_values", List.of("Sameer Seikh", "Sunil Dutt")
            ),
            Map.of(
                "column_name", "email",
                "column_description", "email",
                "column_restriction", "no restrictions",
                "column_values", List.of("sameer.seikh@gmail.com","sunil.dutt88@hotmail.com")
            )
            
        );

        int data_count = 10;

        long startTime = System.currentTimeMillis();

        List<CompletableFuture<AsyncDataAmplifier.DataAmplifierResponse>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {

        
            // Accessing the data_amplifier's amplify method the same way as it's described in the input
            CompletableFuture<AsyncDataAmplifier.DataAmplifierResponse> responseFuture = client.data_amplifier.amplify(example_data, data_count);

            responseFuture.thenAccept(response -> {
            if (response.isStatus()) {
                System.out.println("Amplification Status: " + response.isStatus());
                System.out.println("Amplification Message: " + response.getMessage());

                // Assuming DataAmplifierResponse contains a 'getData' method to access generated data
                Map<String, List<String>> amplifiedData = response.getData();
                
                // Iterate over the map to display generated data
                for (Map.Entry<String, List<String>> entry : amplifiedData.entrySet()) {
                    System.out.println("Column: " + entry.getKey());
                    System.out.println("Values: " + entry.getValue());
                }
            
        }
            else {
                System.out.println("Creation failed with message: " + response.getMessage());
            }
        }).exceptionally(ex -> {
            System.out.println("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });

        // // Keep your program running until all futures are resolved
        // responseFuture.join(); // Block and wait for the future to complete
        futures.add(responseFuture);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for Data Amplification" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
    }

    
```


## API Assertions

<b> API Assertions </b> allows you to create API JSON body, Header, JsoSchema and JsonPath tests

#### Header Assertions

Provide your API Response header to get the tests for the response

With SyncClient

```
private static void testHeaderAssertion() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        String responseHeaders = "Access-Control-Allow-Credentials: true Access-Control-Allow-Origin: * Content-Length: 166 Content-Type: application/json Date: Wed, 12 Jun 2024 10:29:06 GMT Server: awselb/2.0";

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;

        for (int i = 0; i < numberOfRequests; i++) {
            try {
                // Assuming `client.header_assertion` is available and `assert` method matches the described input
                HeaderAssertion.HeaderAssertionResponse response = client.api_assertions.headers.create(responseHeaders);

                // Process and print the assertions
                for (HeaderAssertion.HeaderAssertionItem item : response.getHeaderAssertions()) {
                    System.out.println(item.getAssertHeaderKey());
                    System.out.println(item.getAssertHeaderValue());
                    System.out.println(item.getAssertionDescription());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous Total time for Header Assertion request: " + (endTime - startTime) + " ms");
    }

```

With Async client

```
private static void asyncTestHeaderAssertion() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        String responseHeaders = "Access-Control-Allow-Credentials: true Access-Control-Allow-Origin: * Content-Length: 166 Content-Type: application/json Date: Wed, 12 Jun 2024 10:29:06 GMT Server: awselb/2.0";

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        List<CompletableFuture<AsyncHeaderAssertion.HeaderAssertionResponse>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfRequests; i++) {

            CompletableFuture<AsyncHeaderAssertion.HeaderAssertionResponse> responseFuture = 
                client.api_assertions.headers.create(responseHeaders);

            responseFuture.thenAccept(response -> {
                // Process and print the assertions
                for (AsyncHeaderAssertion.HeaderAssertionItem item : response.getHeaderAssertions()) {
                    System.out.println(item.getAssertHeaderKey());
                    System.out.println(item.getAssertHeaderValue());
                    System.out.println(item.getAssertionDescription());
                }
            }).exceptionally(ex -> {
                System.out.println("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

        futures.add(responseFuture);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for Header Assertion" + numberOfRequests + " requests: " + (endTime - startTime) + " ms");
    }

```

## JSON Body Assertions

<b> JSON body assertion </b> Allows you to create assertions for the json response body. Input your response.

with sync

```
private static void testJSONBodyAssertion() {

        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        Map<String, Object> loanApplication = Map.of(
        "loanId", 101,
        "userId", 1,
        "loanAmount", 200000,
        "loanTerm", 30,
        "propertyValue", 250000,
        "income", 100000,
        "creditScore", 750,
        "status", "pending"
    );

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        
        for (int i = 0; i < numberOfRequests; i++) {
            try {
                // Assuming that the client has an appropriate method for JSON body assertions
                JSONBodyAssertion.JsonBodyAssertionResponse response = client.api_assertions.jsonbody.create(loanApplication);
                
                // Process and print JSON body assertions
                for (JSONBodyAssertion.Assertion assertion : response.getAssertions()) {
                    System.out.println(assertion.getValue());
                    System.out.println(assertion.getType());
                    System.out.println(assertion.getAssertionDescription());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous Total time for JSON Body Assertion request: " + (endTime - startTime) + " ms");
    }

    
```

With AsyncClient

```
private static void asyncTestJSONBodyAssertion() {
        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        Map<String, Object> loanApplication = Map.of(
                "loanId", 101,
                "userId", 1,
                "loanAmount", 200000,
                "loanTerm", 30,
                "propertyValue", 250000,
                "income", 100000,
                "creditScore", 750,
                "status", "pending"
            );

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        List<CompletableFuture<AsyncJSONBodyAssertion.JsonBodyAssertionResponse>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<AsyncJSONBodyAssertion.JsonBodyAssertionResponse> responseFuture =
                client.api_assertions.jsonbody.create(loanApplication);

                responseFuture.thenAccept(response -> {
                // Process and print JSON body assertions
                for (AsyncJSONBodyAssertion.Assertion assertion : response.getAssertions()) {
                    System.out.println(assertion.getValue());
                    System.out.println(assertion.getType());
                    System.out.println(assertion.getAssertionDescription());
                }
            }).exceptionally(ex -> {
                System.out.println("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

            futures.add(responseFuture);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for JSON Body Assertion: " + (endTime - startTime) + " ms");
    }

```

## JSON Path Assertion

<json path assertion> allows you to create json path tests for your api response. Input your api response.

with SyncClient

```
private static void testJSONPathAssertion() {
        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        Map<String, Object> loanApplication = Map.of(
        "loanId", 101,
        "userId", 1,
        "loanAmount", 200000,
        "loanTerm", 30,
        "propertyValue", 250000,
        "income", 100000,
        "creditScore", 750,
        "status", "pending"
    );

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        
        for (int i = 0; i < numberOfRequests; i++) {
            try {
                // Assuming that the client has an appropriate method for JSON body assertions
                JSONPathAssertion.JsonPathAssertionResponse response = client.api_assertions.jsonpath.create(loanApplication);
                
                // Process and print JSON body assertions
                for (JSONPathAssertion.Assertion assertion : response.getAssertions()) {
                    System.out.println(assertion.getJsonPath());
                    System.out.println(assertion.getJsonPathValue());
                    System.out.println(assertion.getType());
                    System.out.println(assertion.getAssertionDescription());
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous Total time for JSON Path Assertion request: " + (endTime - startTime) + " ms");
    }

    
```

With AsyncClient

```
private static void asyncTestJSONPathAssertion() {
        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        Map<String, Object> loanApplication = Map.of(
                "loanId", 101,
                "userId", 1,
                "loanAmount", 200000,
                "loanTerm", 30,
                "propertyValue", 250000,
                "income", 100000,
                "creditScore", 750,
                "status", "pending"
            );

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        List<CompletableFuture<AsyncJSONPathAssertion.JsonPathAssertionResponse>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<AsyncJSONPathAssertion.JsonPathAssertionResponse> responseFuture =
                client.api_assertions.jsonpath.create(loanApplication);

                responseFuture.thenAccept(response -> {
                // Process and print JSON body assertions
                for (AsyncJSONPathAssertion.Assertion assertion : response.getAssertions()) {
                    System.out.println(assertion.getJsonPath());
                    System.out.println(assertion.getJsonPathValue());
                    System.out.println(assertion.getType());
                    System.out.println(assertion.getAssertionDescription());
                }
            }).exceptionally(ex -> {
                System.out.println("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

            futures.add(responseFuture);
        }
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for JSON Path Assertion: " + (endTime - startTime) + " ms");
    }

    
```

## JSON Schema Assertions

<json schema assertion> allows you to create the schema tests for the api response body. Input your api response.

With SyncClient

```
private static void testJSONSchemaAssertions() {
        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");

        SyncClient client = new SyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        Map<String, Object> loanApplication = Map.of(
                "loanId", 101,
                "userId", 1,
                "loanAmount", 200000,
                "loanTerm", 30,
                "propertyValue", 250000,
                "income", 100000,
                "creditScore", 750,
                "status", "pending"
            );

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;

        for (int i = 0; i < numberOfRequests; i++) {
            try {
                // Assuming that the client has a method for JSON schema assertions
                JSONSchemaAssertion.JsonSchemaResponse response = client.api_assertions.jsonschema.create(loanApplication);

                // Process and print JSON schema properties and required fields
                System.out.println("Schema: " + response.getSchema());
                response.getProperties().forEach((key, property) ->
                    System.out.println("Field: " + key + ", Type: " + property.getType())
                ); 
                System.out.println("Required fields: " + String.join(", ", response.getRequired()));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Synchronous Total time for JSON Schema Assertion request: " + (endTime - startTime) + " ms");
    }

```

with AsyncClient

```
private static void asyncTestJSONSchemaAssertions() {
        Dotenv dotenv = Dotenv.load();
        String QYRUS_AI_SDK_API_TOKEN = dotenv.get("QYRUS_AI_SDK_API_TOKEN");
        
        AsyncClient client = new AsyncClient(QYRUS_AI_SDK_API_TOKEN, null);
        Map<String, Object> loanApplication = Map.of(
                "loanId", 101,
                "userId", 1,
                "loanAmount", 200000,
                "loanTerm", 30,
                "propertyValue", 250000,
                "income", 100000,
                "creditScore", 750,
                "status", "pending"
            );

        long startTime = System.currentTimeMillis();
        int numberOfRequests = 2;
        List<CompletableFuture<AsyncJSONSchemaAssertion.JsonSchemaResponse>> futures = new ArrayList<>();

        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<AsyncJSONSchemaAssertion.JsonSchemaResponse> responseFuture =
                client.api_assertions.jsonschema.create(loanApplication);

            responseFuture.thenAccept(response -> {
                // Process and print JSON schema properties and required fields
                System.out.println("Schema: " + response.getSchema());
                response.getProperties().forEach((key, property) ->
                    System.out.println("Field: " + key + ", Type: " + property.getType())
                );
                System.out.println("Required fields: " + String.join(", ", response.getRequired()));
            }).exceptionally(ex -> {
                System.out.println("An error occurred: " + ex.getMessage());
                ex.printStackTrace();
                return null;
            });

            futures.add(responseFuture);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long endTime = System.currentTimeMillis();
        System.out.println("Asynchronous total time for JSON Schema Assertion: " + (endTime - startTime) + " ms");
    }
```
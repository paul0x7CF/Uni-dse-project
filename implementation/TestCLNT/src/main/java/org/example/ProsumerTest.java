package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProsumerTest {
    private static final Logger logger = LogManager.getLogger(ProsumerTest.class);

    /*@Value("${receiver.address}")
    private String receiverAddress;

    @Value("${sender.address}")
    private String senderAddress;

    @Value("${port}")
    private int port;*/

    private String api;
    private String consumersEndpoint;
    private String producersEndpoint;

    public ProsumerTest(String address, int port) {
        logger.info("NetworkManager created");
        this.api = address + ":" + port + "/api";
        this.consumersEndpoint = "/consumers";
        this.producersEndpoint = "/producers";
    }

    public void createConsumer(String type) {
        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> createResponse = restTemplate.exchange(
                api + consumersEndpoint + "?type=" + type,
                HttpMethod.POST,
                null,
                String.class
        );

        // Check the create response status code
        if (createResponse.getStatusCode().is2xxSuccessful()) {
            // Successful create response
            System.out.println("Consumer created: " + createResponse.getBody());

        } else {
            // Error create response
            System.out.println("Error creating consumer: " + createResponse.getStatusCode());
        }
    }

    public void getConsumers() {
        // Set the base URL of your REST API

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make a GET request to the /producers endpoint
        ResponseEntity<String> response = restTemplate.exchange(
                api + consumersEndpoint,
                HttpMethod.GET,
                null,
                String.class
        );

        // Check the response status code
        if (response.getStatusCode().is2xxSuccessful()) {
            // Successful response
            System.out.println("Response Body: " + response.getBody());
        } else {
            // Error response
            System.out.println("Error: " + response.getStatusCode());
        }
    }

    public void updateConsumer(String type, String consumption) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + consumersEndpoint + "?type=" + type + "&consumption=" + consumption,
                HttpMethod.PUT,
                null,
                String.class
        );

        // Check the response status code
        if (response.getStatusCode().is2xxSuccessful()) {
            // Successful response
            System.out.println("Consumer updated: " + response.getBody());
        } else {
            // Error response
            System.out.println("Error updating consumer: " + response.getStatusCode());
        }
    }

    public void deleteConsumer(String type) {
        // Set the base URL of your REST API

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make a DELETE request to delete the consumer
        ResponseEntity<String> response = restTemplate.exchange(
                api + consumersEndpoint + "?type=" + type,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Check the response status code
        if (response.getStatusCode().is2xxSuccessful()) {
            // Successful response
            System.out.println("Consumer deleted: " + response.getBody());
        } else {
            // Error response
            System.out.println("Error: " + response.getStatusCode());
        }
    }



    public void createProducer(String type) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> createResponse = restTemplate.exchange(
                api + producersEndpoint + "?type=" + type,
                HttpMethod.POST,
                null,
                String.class
        );

        // Check the create response status code
        if (createResponse.getStatusCode().is2xxSuccessful()) {
            // Successful create response
            System.out.println("Producer created: " + createResponse.getBody());
        } else {
            // Error create response
            System.out.println("Error creating producer: " + createResponse.getStatusCode());
        }
    }

    public void getProducers() {
         // Set the base URL of your REST API

        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Make a GET request to the /producers endpoint
        ResponseEntity<String> response = restTemplate.exchange(
                api + producersEndpoint,
                HttpMethod.GET,
                null,
                String.class
        );

        // Check the response status code
        if (response.getStatusCode().is2xxSuccessful()) {
            // Successful response
            System.out.println("Response Body: " + response.getBody());
        } else {
            // Error response
            System.out.println("Error: " + response.getStatusCode());
        }
    }

    public void updateProducer(String type, String efficiency) {
        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + producersEndpoint + "?type=" + type + "&efficiency=" + efficiency,
                HttpMethod.PUT,
                null,
                String.class
        );

        // Check the response status code
        if (response.getStatusCode().is2xxSuccessful()) {
            // Successful response
            System.out.println("Producer updated: " + response.getBody());
        } else {
            // Error response
            System.out.println("Error updating producer: " + response.getStatusCode());
        }
    }

    public void deleteProducer(String type) {
        // Create a RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + producersEndpoint + "?type=" + type,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Check the response status code
        if (response.getStatusCode().is2xxSuccessful()) {
            // Successful response
            System.out.println("Producer deleted: " + response.getBody());
        } else {
            // Error response
            System.out.println("Error deleting producer: " + response.getStatusCode());
        }
    }
/*
    private void createConsumer(String category, String clientUUID, Object taskData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonData = mapper.writeValueAsString(taskData);
            String xmlData = "json;!" + jsonData;
            String message = new MessageBuilder()
                    .category(category)
                    .idSender(clientUUID)
                    .messageType("PUBLISH")
                    .senderAddress(senderAddress)
                    .receiverAddress(receiverAddress)
                    .xmlData(xmlData)
                    .xmlClass("")
                    .build();
            logger.info("JSON Stringify: " + message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("http://" + api + receiverAddress, requestEntity, String.class);
        } catch (JsonProcessingException e) {
            logger.error("Error converting task data to JSON: " + e.getMessage());
        }
    }

    public void sendHashTask(String category, String clientUUID, HashTaskData task) {
        sendTask(category, clientUUID, task);
    }

    public void sendPlainTask(String category, String clientUUID, PlainTaskData task) {
        sendTask(category, clientUUID, task);
    }

    public void publish(String clientUUID, String category) {
        String message = new MessageBuilder()
                .category(category)
                .idSender(clientUUID)
                .messageType("PUBLISH")
                .senderAddress(senderAddress)
                .receiverAddress(receiverAddress)
                .xmlData("json;!")
                .xmlClass("")
                .build();
        //logger.info("JSON Stringify: " + message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://" + api + publishEndpoint, requestEntity, String.class);
    }

    public void subscribe(String clientUUID, String category) {
        String message = new MessageBuilder()
                .category(category)
                .idSender(clientUUID)
                .messageType("SUBSCRIBE")
                .senderAddress(senderAddress)
                .receiverAddress(receiverAddress)
                .xmlData("")
                .xmlClass("")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://" + api + subscribeEndpoint, requestEntity, String.class);
        //logger.info("Sub: " + message);
    }

    public void unsubscribe(String clientUUID, String category) {
        String message = new MessageBuilder()
                .category(category)
                .idSender(clientUUID)
                .messageType("UNSUBSCRIBE")
                .senderAddress(senderAddress)
                .receiverAddress(receiverAddress)
                .xmlData("")
                .xmlClass("")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://" + api + unsubscribeEndpoint, requestEntity, String.class);
    }

    public String poll(String clientUUID) {
        String result = "";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "http://" + api + pollingEndpoint + "/" + senderAddress + "/" + clientUUID,
                HttpMethod.GET,
                null,
                String.class
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            result = response.getBody();
        }
        return result;
    }

    public void register(String clientUUID, RegisterData registerData) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonData = mapper.writeValueAsString(registerData);
            String xmlData = "json;!" + jsonData;
            String message = new MessageBuilder()
                    .category("Register;")
                    .idSender(clientUUID)
                    .messageType("PUBLISH")
                    .senderAddress(clientUUID)
                    .receiverAddress(receiverAddress)
                    .xmlData(xmlData)
                    .xmlClass("")
                    .build();
            //logger.info("JSON Stringify: " + message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("http://" + api + publishEndpoint, requestEntity, String.class);
        } catch (JsonProcessingException e) {
            logger.error("Error converting register data to JSON: " + e.getMessage());
        }
    }

    public void subscribeOnStart(String clientUUID) {
        subscribe(clientUUID, "PriceTable");
        subscribe(clientUUID, "CalcOutputOnlyForMe");
        subscribe(clientUUID, "CrackingStatusOnlyForMe");
    }

    public void unsubscribeOnStop(String clientUUID) {
        unsubscribe(clientUUID, "PriceTable");
        unsubscribe(clientUUID, "CalcOutputOnlyForMe");
        unsubscribe(clientUUID, "CrackingStatusOnlyForMe");
    }*/
}

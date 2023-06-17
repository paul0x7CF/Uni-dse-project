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
    private final String api;
    private final String consumersEndpoint;
    private final String producersEndpoint;

    public ProsumerTest(String address, int port) {
        logger.info("NetworkManager created");
        this.api = address + ":" + port + "/api";
        this.consumersEndpoint = "/consumers";
        this.producersEndpoint = "/producers";
    }

    public void createConsumer(String type) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> createResponse = restTemplate.exchange(
                api + consumersEndpoint + "?type=" + type,
                HttpMethod.POST,
                null,
                String.class
        );

        if (createResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("Consumer created: " + createResponse.getBody());

        } else {
            System.out.println("Error creating consumer: " + createResponse.getStatusCode());
        }
    }

    public void getConsumers() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + consumersEndpoint,
                HttpMethod.GET,
                null,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Response Body: " + response.getBody());
        } else {
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

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Consumer updated: " + response.getBody());
        } else {
            System.out.println("Error updating consumer: " + response.getStatusCode());
        }
    }

    public void deleteConsumer(String type) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + consumersEndpoint + "?type=" + type,
                HttpMethod.DELETE,
                null,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Consumer deleted: " + response.getBody());
        } else {
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

        if (createResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("Producer created: " + createResponse.getBody());
        } else {
            System.out.println("Error creating producer: " + createResponse.getStatusCode());
        }
    }

    public void getProducers() {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + producersEndpoint,
                HttpMethod.GET,
                null,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Response Body: " + response.getBody());
        } else {
            System.out.println("Error: " + response.getStatusCode());
        }
    }

    public void updateProducer(String type, String efficiency) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + producersEndpoint + "?type=" + type + "&efficiency=" + efficiency,
                HttpMethod.PUT,
                null,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Producer updated: " + response.getBody());
        } else {
            System.out.println("Error updating producer: " + response.getStatusCode());
        }
    }

    public void deleteProducer(String type) {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                api + producersEndpoint + "?type=" + type,
                HttpMethod.DELETE,
                null,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Producer deleted: " + response.getBody());
        } else {
            System.out.println("Error deleting producer: " + response.getStatusCode());
        }
    }
}

package MSP.Communication;

import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import MSP.Logic.Prosumer.Singleton;
import MSP.Data.EProducerType;
import MSP.Data.Producer;
import MSP.Logic.Prosumer.RESTData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.beans.JavaBean;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  Rest manager which takes care of the Communication over the REST-Endpoints
 */

// Example: http://localhost:8081/dserestapi/test
@SpringBootApplication
@RestController
@RequestMapping(value = "/api")
public class RestHandler {
    private static final Logger logger = LogManager.getLogger(RestHandler.class);
    private LinkedHashSet<Consumer> consumerList;
    private String test;
    private final ConsumptionBuilding consumptionBuilding = Singleton.getInstance().getConsumptionBuilding();


    private RESTData restData;


    protected RestHandler(RESTData restData) {
        this.restData = restData;
    }

    protected RestHandler() {

    }


/*
    @GetMapping("/producers")
    public ResponseEntity<Set<Producer>> getProducers() {
        Set<Producer> producers = restData.getProducers();
        return ResponseEntity.ok(producers);
    }

    @GetMapping("/consumers")
    public ResponseEntity<Set<Consumer>> getConsumers() {
        Set<Consumer> consumers = restData.getConsumers();
        return ResponseEntity.ok(consumers);
    }

    @PostMapping("/producers")
    public ResponseEntity<Void> createProducer(@RequestBody EProducerType producerType) {
        boolean success = restData.createProducer(producerType);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/consumers")
    public ResponseEntity<Void> createConsumer(@RequestBody EConsumerType consumerType) {
        boolean success = restData.createConsumer(consumerType);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
*/

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.POST,consumes={"application/json"})
    private ResponseEntity<String> createConsumer(@RequestBody String type) {
        /*Consumer newConsumer = new Consumer(type);
        consumerList.add(newConsumer);*/



        EConsumerType type1 = EConsumerType.valueOf(type);

        consumptionBuilding.createConsumer(type1);

        logger.info("Created new Consumer from type {}", type1);
        return new ResponseEntity<>("Created new Consumer from type " + type1, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.DELETE, consumes = {"application/json"})
    private ResponseEntity<String> deleteConsumer(@RequestBody EConsumerType type) {
        AtomicInteger countDeleted = new AtomicInteger();
        this.consumerList.removeIf(consumer -> {
            countDeleted.getAndIncrement();
            return consumer.getConsumerType().equals(type);
        });
        if (countDeleted.get() > 0) {
            logger.info("Deleted {} Consumer from type {}", countDeleted.get(), type);
            return new ResponseEntity<>("Deleted " + countDeleted.get() + " Consumer from type " + type, HttpStatus.OK);
        } else {
            logger.info("No Producer Consumer from type {}", type);
            return new ResponseEntity<>("No Producer Consumer from type " + type, HttpStatus.NOT_FOUND);
        }
    }

/*    @PutMapping("/producers")
    public ResponseEntity<Void> updateProducer(@RequestBody EProducerType producerDTO) {
        boolean success = restData.updateProducer(producerDTO.getProducerType(), producerDTO.getEfficiency());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/consumers")
    public ResponseEntity<Void> updateConsumer(@RequestBody EConsumerType consumerDTO) {
        boolean success = restData.updateConsumer(consumerDTO.getConsumerType(), consumerDTO.getEfficiency());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }*/

 /*   @DeleteMapping("/producers/{producerType}")
    public ResponseEntity<Void> deleteProducer(@PathVariable EProducerType producerType) {
        boolean success = restData.deleteProducer(producerType);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/consumers/{consumerType}")
    public ResponseEntity<Void> deleteConsumer(@PathVariable EConsumerType consumerType) {
        boolean success = restData.deleteConsumer(consumerType);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/
}
package MSP.Communication;

import MSP.Data.*;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import MSP.Logic.Prosumer.Singleton;
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

@SpringBootApplication
@RestController
@RequestMapping(value = "/api")
public class RestHandler {
    private static final Logger logger = LogManager.getLogger(RestHandler.class);
    private LinkedHashSet<Consumer> consumerList;
    private String test;
    private final ConsumptionBuilding consumptionBuilding = Singleton.getInstance().getConsumptionBuilding();


    private RESTData restData;


    /*protected RestHandler(RESTData restData) {
        this.restData = restData;
    }*/

    protected RestHandler() {

    }

    @CrossOrigin
    @RequestMapping(value = "/producers", method = RequestMethod.POST)
    private ResponseEntity<String> createProducer(@RequestParam String type) {
        if (!consumptionBuilding.getProsumerType().equals(EProsumerType.NETTO_ZERO_BUILDING))
            return new ResponseEntity<>("Prosumer is no Net-Zero-Building!", HttpStatus.NOT_FOUND);

        EProducerType type1 = EProducerType.valueOf(type);

        consumptionBuilding.createProducer(type1);

        logger.info("Created new Consumer from type {}", type1);
        return new ResponseEntity<>("Created new Consumer from type " + type1, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(value = "/producers", method = RequestMethod.GET,produces={"application/json"})
    private ResponseEntity<String> getProducers() {
        if (consumptionBuilding.getProsumerType().equals(EProsumerType.CONSUMPTION_BUILDING))
            return new ResponseEntity<>("Prosumer is no Net-Zero-Building!", HttpStatus.NOT_FOUND);

        return new ResponseEntity<>("ProducerList: " + consumptionBuilding.getProducers().toString(), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/producers", method = RequestMethod.PUT)
    private ResponseEntity<String> updateProsumer(@RequestParam String type, @RequestParam String efficiency) {
        EProducerType type1 = EProducerType.valueOf(type);
        int updatedConsumer = consumptionBuilding.updateProducer(type1, Integer.parseInt(efficiency));

        if (updatedConsumer == 0) {
            logger.warn("Update Failed!");
            return new ResponseEntity<>("Update Failed! " + type1, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Updated " + updatedConsumer + " Producer(s) from type " + type1, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/producers", method = RequestMethod.DELETE)
    private ResponseEntity<String> deleteProducer(@RequestParam String type) {
        EProducerType type1 = EProducerType.valueOf(type);
        boolean deletedConsumer = consumptionBuilding.deleteProducer(type1);

        if (!deletedConsumer) {
            logger.warn("Delete Failed!");
            return new ResponseEntity<>("Delete Failed! " + type1, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>( "Producer(s) from type " + type1 + " deleted", HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.POST)
    private ResponseEntity<String> createConsumer(@RequestParam String type) {
        EConsumerType type1 = EConsumerType.valueOf(type);

        consumptionBuilding.createConsumer(type1);

        logger.info("Created new Consumer from type {}", type1);
        return new ResponseEntity<>("Created new Consumer from type " + type1, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.GET,produces={"application/json"})
    private ResponseEntity<String> getConsumers() {
        return new ResponseEntity<>("ConsumerList: " + consumptionBuilding.getConsumers().toString(), HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.PUT)
    private ResponseEntity<String> updateConsumer(@RequestParam String type, @RequestParam String consumption) {
        EConsumerType type1 = EConsumerType.valueOf(type);
        int updatedConsumer = consumptionBuilding.updateConsumer(type1, Integer.parseInt(consumption));

        if (updatedConsumer == 0) {
            logger.warn("Update Failed!");
            return new ResponseEntity<>("Update Failed! " + type1, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Updated " + updatedConsumer + " Consumer(s) from type " + type1, HttpStatus.OK);

        //Zivan CODE
        /*EConsumerType type1 = EConsumerType.valueOf(type);

        HashSet<Consumer> consumers = consumptionBuilding.getConsumerss();

        int count = 0;

        for (Consumer consumer : consumers) {
            if (consumer.getConsumerType().equals(type1)) {
                consumer.setAverageConsumption(Double.parseDouble(consumption));
                logger.info("Updated Consumer from type {}", type1);
                count++;
            }
        }

        if (count == 0) {
            logger.warn("Update Failed!");
            return new ResponseEntity<>("Update Failed!" + type1, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>("Updated " + count + " Consumer(s) from type " + type1, HttpStatus.OK);*/
    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.DELETE)
    private ResponseEntity<String> deleteConsumer(@RequestParam String type) {
        EConsumerType type1 = EConsumerType.valueOf(type);
        boolean deletedConsumer = consumptionBuilding.deleteConsumer(type1);

        if (!deletedConsumer) {
            logger.warn("Delete Failed!");
            return new ResponseEntity<>("Delete Failed! " + type1, HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>( "Consumer(s) from type " + type1 + " deleted", HttpStatus.OK);
    }
}
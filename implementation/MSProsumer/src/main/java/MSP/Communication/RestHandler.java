package MSP.Communication;

import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

   /* protected RestHandler(LinkedHashSet<Consumer> consumerList) {
        this.consumerList = consumerList;
    }*/

    protected RestHandler() {

    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.POST,consumes={"application/json"})
    private ResponseEntity<String> createConsumer(@RequestBody EConsumerType type) {
        Consumer newConsumer = new Consumer(type);
        consumerList.add(newConsumer);

        logger.info("Created new Consumer from type {}", type);
        return new ResponseEntity<>("Created new Consumer from type " + type, HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(value = "/consumers", method = RequestMethod.DELETE,consumes={"application/json"})
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

/*
    *//**
     * Endpoint order to subscribe, only takes sub Messages
     * @param message which is recived by the endpoint
     * @return String with decription of outcome of operation
     *//*

    @CrossOrigin
    @RequestMapping(value = "/subscribe", method = RequestMethod.POST,consumes={"application/json"})
    public ResponseEntity<String> subscribe(@RequestBody() Message message) {
        try{
            // for validating message
            new MessageBuilder(message).build();
        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>("Invalid Message provided", HttpStatus.NOT_ACCEPTABLE);
        }
        if(!message.getMessageType().equals(EMessageType.SUBSCRIBE)){
            return new ResponseEntity<>("only subscribe messages at this endpoint",HttpStatus.NOT_ACCEPTABLE);
        }

        pollStorage.putIfAbsent(message.getSenderAdress(), new HashSet<>());
        try {
            outputRest.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("Successfully Subscribe your polling id is" + message.getSenderAdress(), HttpStatus.ACCEPTED);
    }
    *//**
     * Endpoint order to Unsubscribe, only takes Unsubscribe Messages
     * @param message which is recived by the endpoint
     * @return String with decription of outcome of operation
     *//*

    @CrossOrigin
    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST,consumes={"application/json"})
    public ResponseEntity<String> unsubscribe(@RequestBody Message message) {
        try{
            // for validating message
            new MessageBuilder(message).build();
        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>("Invalid Message provided", HttpStatus.NOT_ACCEPTABLE);
        }
        if(!message.getMessageType().equals(EMessageType.UNSUBSCRIBE)){
            return new ResponseEntity<>("only subscribe messages at this endpoint",HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            outputRest.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("Unsubscribe recived", HttpStatus.ACCEPTED);
    }

    *//**
     * Endpoint order to Publish, only takes Publish Messages
     * @param message which is recived by the endpoint
     * @return String with decription of outcome of operation
     *//*
    @CrossOrigin
    @RequestMapping(value = "/publish", method = RequestMethod.POST,consumes={"application/json"})
    public ResponseEntity<String> post(@RequestBody Message message) {
        try{
            // for validating message
            new MessageBuilder(message).build();
        } catch (InvalidArgumentException e) {
            return new ResponseEntity<>("Invalid Message provided", HttpStatus.NOT_ACCEPTABLE);
        }
        if(!message.getMessageType().equals(EMessageType.PUBLISH)){
            return new ResponseEntity<>("only subscribe messages at this endpoint",HttpStatus.NOT_ACCEPTABLE);
        }
        //pollStorage.putIfAbsent(message.getSenderAdress(), new HashSet<>());
        try {
            outputRest.put(message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>("Message Posted Successfully", HttpStatus.ACCEPTED);
    }

    *//**
     * This method is used by the client to get the messages from the queue
     * @param pollingID the polling id of the subscriber
     * @return the messages
     *//*
    @CrossOrigin
    @RequestMapping(value = "/polling/{pollingID}/{uuid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Message>> subscribe(@PathVariable String pollingID,@PathVariable String uuid ) {
        if(pollStorage.containsKey(pollingID)){
            try {
                Message pingMessage = new MessageBuilder("PING").messageType(EMessageType.PING).senderID(UUID.fromString(uuid)).technology(ETransferTechnology.REST).build();
                outputRest.put(pingMessage);
            }
            catch(Exception e){
                e.printStackTrace();
                return new ResponseEntity<>(null, HttpStatus.SEE_OTHER);
            }
            Set<Message> messages = pollStorage.get(pollingID);
            return new ResponseEntity<>(pollStorage.replace(pollingID, new HashSet<>()),HttpStatus.ACCEPTED);
        }
        else{
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }

    }

    *//**
     * Test Message to test if it is working, returns the same thing every time
     * @return
     * @throws InvalidArgumentException
     * @throws MarshallException
     *//*
    @CrossOrigin
    @RequestMapping(value = "/testmessage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Message testMessage() throws InvalidArgumentException, MarshallException {
        Message test = new MessageBuilder("Test").messageType(EMessageType.PUBLISH).technology(ETransferTechnology.UDP).target("XOXO").xmlData(new TrashClass("Hello")).build();
        return test;
    }*/

}

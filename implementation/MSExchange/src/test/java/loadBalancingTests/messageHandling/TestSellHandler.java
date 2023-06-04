package loadBalancingTests.messageHandling;

import broker.Broker;
import broker.BrokerRunner;
import loadManager.exchangeManagement.LoadManager;
import loadManager.networkManagment.LoadManagerMessageHandler;
import msExchange.messageHandling.MessageBuilder;
import org.junit.Test;
import protocol.Message;
import protocol.MessageFactory;
import sendable.EServiceType;
import sendable.Sell;

import java.util.UUID;

public class TestSellHandler {
/*
    @Test
    public void handleSell_AllExchangesAtCapacity_ThrowsException(){
        //create a mock Sell object for testing
        Sell sell = new Sell(10, 10, UUID.randomUUID(), UUID.randomUUID());

        //Create a mock Message object containing the Sell object
        MessageFactory messageFactory = new MessageFactory();
        messageFactory.setPayload(sell);
        Message message = messageFactory.build();

        //Create a mock Broker object with a current service type
        BrokerRunner brokerRunner = new BrokerRunner(EServiceType.Exchange, 1010);

        //Create a mock LoadManager object that throws AllExchangesAtCapacityException
        LoadManager loadManager = new LoadManager();
        LoadManagerMessageHandler loadManagerMessageHandler = new LoadManagerMessageHandler(brokerRunner.getOutgoingQueue());


    }*/
}

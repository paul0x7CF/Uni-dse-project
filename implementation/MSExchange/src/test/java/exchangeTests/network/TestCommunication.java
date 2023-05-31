package exchangeTests.network;

import broker.BrokerRunner;
import msExchange.networkCommunication.CommunicationExchange;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import sendable.EServiceType;
import sendable.MSData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestCommunication {

    @Test
    public void givenCommunication_getCurrentService_expectedCorrectMsData() {
        BlockingQueue incomingMessages = new LinkedBlockingQueue();
        BlockingQueue outgoingMessages = new LinkedBlockingQueue();


        CommunicationExchange communication = new CommunicationExchange(incomingMessages, outgoingMessages);
        MSData msData = communication.getMyMSData();
        BrokerRunner brokerRunner = communication.getBroker();

        //This test will fail, ones the port is dynamic
        //Assertions.assertEquals(1609, msData.getPort());
        Assertions.assertEquals(EServiceType.Exchange, msData.getType());

        Assertions.assertEquals(msData.getPort(), brokerRunner.getCurrentService().getPort());
        Assertions.assertEquals(msData.getAddress(), brokerRunner.getCurrentService().getAddress());
        Assertions.assertEquals(msData.getType(), brokerRunner.getCurrentService().getType());
    }


}

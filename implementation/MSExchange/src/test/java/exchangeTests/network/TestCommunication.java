package exchangeTests.network;

import CF.broker.BrokerRunner;
import msExchange.networkCommunication.CommunicationExchange;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import CF.sendable.EServiceType;
import CF.sendable.MSData;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestCommunication {

    @Test
    public void givenCommunication_getCurrentService_expectedCorrectMsData() {
        BlockingQueue incomingMessages = new LinkedBlockingQueue();
        BlockingQueue outgoingMessages = new LinkedBlockingQueue();


        CommunicationExchange communication = new CommunicationExchange(incomingMessages, 1);
        MSData msData = communication.getBroker().getCurrentService();
        BrokerRunner brokerRunner = communication.getBroker();

        //This test will fail, ones the port is dynamic
        //Assertions.assertEquals(1609, msData.getPort());
        Assertions.assertEquals(EServiceType.ExchangeWorker, msData.getType());

        Assertions.assertEquals(msData.getPort(), brokerRunner.getCurrentService().getPort());
        Assertions.assertEquals(msData.getAddress(), brokerRunner.getCurrentService().getAddress());
        Assertions.assertEquals(msData.getType(), brokerRunner.getCurrentService().getType());
    }


}

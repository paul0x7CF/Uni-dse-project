package loadBalancingTests.prosumerManager;

import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.protocol.MessageFactory;
import CF.sendable.Bid;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import loadManager.Controller;
import mainPackage.ESubCategory;
import mainPackage.IMessageBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class TestIncomingBid {
  /*  @Mock
    private BlockingQueue<Message> incomingQueue;

    @Test
    public void testController() throws Exception {
        // Initialize the mocks
        MockitoAnnotations.openMocks(this);

        // Create an instance of Controller
        Controller controller = new Controller();

        // Use reflection to access the private incomingQueue field
        Field incomingQueueField = Controller.class.getDeclaredField("incomingQueue");
        incomingQueueField.setAccessible(true);

        // Set the mocked incomingQueue
        incomingQueueField.set(controller, incomingQueue);

        // Use reflection to access the private messageHandler field
        Field messageHandlerField = Controller.class.getDeclaredField("messageHandler");
        messageHandlerField.setAccessible(true);

        // Perform your desired actions to add something to the incomingQueue
        Bid bid = new Bid(100, 2, UUID.randomUUID(), UUID.randomUUID());
        incomingQueue.put(buildMessage(bid));

        Thread startThread = new Thread(() -> {
            System.out.println("Controller starts");
            controller.run();
        });

        // Create a separate thread to set the flag and exit the loop after a period of time
        Thread stopThread = new Thread(() -> {
            try {
                Thread.sleep(50000); // Wait for 5 seconds
                messageHandlerField.set(controller, null); // Set messageHandler to null to exit the loop
                System.out.println("Stop thread starts");
            } catch (InterruptedException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        // Start the stop thread
        startThread.start();
        stopThread.start();

        // Use Mockito.verify to assert if everything went fine
        Mockito.verify(incomingQueue, Mockito.timeout(6000)).add(Mockito.any(Message.class));

    }

    private Message buildMessage(Bid bid) {
        MSData recieverMS = new MSData(UUID.randomUUID(), EServiceType.Exchange, "78.104.161.104", 9000);
        MSData senderMS = new MSData(UUID.randomUUID(), EServiceType.Prosumer, "localhost", 9000);
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(recieverMS, senderMS);
        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.Bid)).setPayload(bid);
        return messageFactory.build();
    }*/
}

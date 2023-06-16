package BrokerTests;

import CF.broker.*;
import CF.broker.discovery.MessageScheduler;
import CF.communication.NetworkHandler;
import CF.exceptions.MessageProcessingException;
import CF.messageHandling.MessageHandler;
import CF.protocol.Message;
import CF.sendable.AckInfo;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BrokerTest extends Broker {

    @Mock
    private NetworkHandler networkHandler;

    @Mock
    private ServiceRegistry serviceRegistry;

    @Mock
    private MessageHandler messageHandler;

    @Mock
    private AckHandler ackHandler;


    public BrokerTest() {
        super(EServiceType.Exchange, 1234);
        MockitoAnnotations.openMocks(this);
        this.networkHandler = mock(NetworkHandler.class);
        this.serviceRegistry = mock(ServiceRegistry.class);
        this.messageHandler = mock(MessageHandler.class);
        this.ackHandler = mock(AckHandler.class);
    }

    @Test
    public void stop_ShouldUnregisterServicesAndStopNetworkHandler() throws InterruptedException {
        MSData service1 = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        MSData service2 = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 5678);
        when(serviceRegistry.getAvailableServices()).thenReturn(List.of(service1, service2));

        assertDoesNotThrow(this::stop);

        verify(serviceRegistry).unregisterService(service1);
        verify(serviceRegistry).unregisterService(service2);
        verify(networkHandler).stop();
    }

    @Test
    public void sendMessage_ShouldSendMessageAndTrackAck() {
        MSData msData = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        Message message = InfoMessageBuilder.createErrorMessage(msData, msData, "error in test", "test");

        sendMessage(message);

        verify(networkHandler).sendMessage(any());
        verify(ackHandler).trackMessage(message);
    }

    @Test
    public void receiveMessage_WhenMessageIsNotAlreadyReceived_ShouldHandleMessage() throws MessageProcessingException {
        MessageReceiver receiver = new MessageReceiver();
        MSData msData = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        Message message = InfoMessageBuilder.createErrorMessage(msData, msData, "error in test", "test");

        when(networkHandler.receiveMessage()).thenReturn(new byte[0]);
        when(Marshaller.unmarshal(any(byte[].class))).thenReturn(message);
        when(receiver.isMessageAlreadyReceived(message)).thenReturn(false);

        assertDoesNotThrow(this::receiveMessage);

        verify(networkHandler).sendMessage(any());
        verify(messageHandler).handleMessage(message);
        verify(receiver).receiveMessage(message);
    }

    @Test
    public void receiveMessage_WhenMessageIsAlreadyReceived_ShouldNotHandleMessage() throws MessageProcessingException {
        MessageReceiver receiver = new MessageReceiver();
        MSData msData = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        Message message = InfoMessageBuilder.createErrorMessage(msData, msData, "error in test", "test");

        when(networkHandler.receiveMessage()).thenReturn(new byte[0]);
//        when(Marshaller.unmarshal(any(byte[].class))).thenReturn(message);
        when(receiver.isMessageAlreadyReceived(message)).thenReturn(true);

        assertDoesNotThrow(this::receiveMessage);

        verify(networkHandler).sendMessage(any());
        verify(messageHandler, never()).handleMessage(message);
        verify(receiver).receiveMessage(message);
    }

    @Test
    public void ackReceived_ShouldCallAckHandler() {
        MSData msData = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        Message message = InfoMessageBuilder.createErrorMessage(msData, msData, "error in test", "test");
        AckInfo ackInfo = new AckInfo(message.getMessageID(), message.getCategory(), message.getSenderPort(), message.getReceiverPort());

        ackReceived(ackInfo);

        verify(ackHandler).ackReceived(ackInfo);
    }

    @Test
    public void registerService_ShouldCallServiceRegistry() {
        MSData msData = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        when(serviceRegistry.registerService(msData)).thenReturn(true);

        boolean result = registerService(msData);

        assertTrue(result);
        verify(serviceRegistry).registerService(msData);
    }

    @Test
    public void unregisterService_ShouldCallServiceRegistry() {
        MSData msData = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);

        unregisterService(msData);

        verify(serviceRegistry).unregisterService(msData);
    }

    @Test
    public void getCurrentService_ShouldCallServiceRegistry() {
        MSData expectedService = new MSData(UUID.randomUUID(), EServiceType.Exchange, "testAddress", 1234);
        when(serviceRegistry.getCurrentService()).thenReturn(expectedService);

        MSData currentService = getCurrentService();

//        assertEquals(expectedService, currentService);
        verify(serviceRegistry).getCurrentService();
    }
}

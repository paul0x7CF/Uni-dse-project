package broker;

import broker.discovery.DiscoveryService;
import broker.discovery.IScheduleBroker;
import communication.LocalMessage;
import communication.NetworkHandler;
import exceptions.MessageProcessingException;
import mainPackage.ConfigReader;
import messageHandling.InfoMessageHandler;
import messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.AckInfo;
import sendable.EServiceType;
import sendable.MSData;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Broker implements IServiceBroker, IScheduleBroker {
    private static final Logger log = LogManager.getLogger(Broker.class);
    private final AckHandler ackHandler;
    // not IMessageHandler because addMessageHandler is not part of the interface.
    private final MessageHandler messageHandler;
    private final NetworkHandler networkHandler;
    private final ServiceRegistry serviceRegistry;

    protected Broker(EServiceType serviceType, int listeningPort) {
        // listeningPort is the currentMSData port so others can send messages to this broker.
        networkHandler = new NetworkHandler(serviceType, listeningPort);

        // give currentMS to registry
        serviceRegistry = new ServiceRegistry(networkHandler.getMSData());

        // AckHandler uses IBroker to send messages.
        ackHandler = new AckHandler(this);

        // MessageHandlers are based on the category so that other components can have their own implementations
        messageHandler = new MessageHandler();
        messageHandler.addMessageHandler(ECategory.Info, new InfoMessageHandler(this));
    }

    protected void startBroker() throws UnknownHostException {
        networkHandler.startSockets();

        // register Microservice
        // TODO: should we add a Info;GetAllServices message which returns a list of MSData?
        // TODO: Fixed IP addresses?
        //  Its always 10.102.102.x (Prosumer(17), Exchange(13), Forecast(9)) but whats the port?
        DiscoveryService discoveryService = new DiscoveryService(this);
        discoveryService.scheduleDiscovery();

        // start receiving messages in new thread
        try {
            receiveMessage();
        } catch (MessageProcessingException e) {
            log.error("Error while receiving message: ", e);
        }
    }

    protected void stop() {
        for (MSData service : getServices()) {
            sendMessage(InfoMessageBuilder.createUnregisterMessage(serviceRegistry.getCurrentService(), service));
        }
        networkHandler.stop();
    }

    @Override
    public void sendMessage(Message message) {
        log.trace("{} sending message: {}", getCurrentService().getType(), message.getCategory());
        networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(message),
                message.getReceiverAddress(),
                message.getReceiverPort()));

        // Ack and Register messages should not be acknowledged as this would cause an infinite loop and register
        // has an answer in the form of "Ping" anyway.
        if (!Objects.equals(message.getSubCategory(), "Ack")
                && !Objects.equals(message.getSubCategory(), "Register")) {
            ackHandler.trackMessage(message);
        }
    }

    /**
     * while true loop to receive messages and handle them
     *
     * @throws IOException                if an I/O error occurs
     * @throws ClassNotFoundException     if the class of a serialized object could not be found
     * @throws MessageProcessingException if the message could not be processed
     */
    private void receiveMessage() throws MessageProcessingException {
        // TODO: maybe catch exceptions and try again
        while (true) {
            Message message = Marshaller.unmarshal(networkHandler.receiveMessage());
            // Ack and Register messages should not be acknowledged as this would cause an infinite loop and register
            // has an answer in the form of "Ping" anyway.
            if (!Objects.equals(message.getSubCategory(), "Ack")
                    && !Objects.equals(message.getSubCategory(), "Register")) {
                sendMessage(InfoMessageBuilder.createAckMessage(message));
            }
            log.trace("{} received message: {}", getCurrentService().getType(), message.getCategory());
            messageHandler.handleMessage(message);
        }
    }

    @Override
    public void scheduleMessage(LocalMessage localMessage, int delay) {
        networkHandler.scheduleMessage(localMessage, delay);
    }

    @Override
    public void ackReceived(AckInfo ack) {
        ackHandler.ackReceived(ack);
    }

    @Override
    public void registerService(MSData msData) {
        serviceRegistry.registerService(msData);
    }

    public void unregisterService(MSData msData) {
        serviceRegistry.unregisterService(msData);
    }

    @Override
    public MSData getCurrentService() {
        return serviceRegistry.getCurrentService();
    }

    protected MessageHandler getMessageHandler() {
        return messageHandler;
    }

    protected MSData findService(UUID serviceId) {
        return serviceRegistry.findService(serviceId);
    }

    protected List<MSData> getServices() {
        return serviceRegistry.getAvailableServices();
    }

    protected List<MSData> getServicesByType(EServiceType serviceType) {
        return serviceRegistry.getServicesByType(serviceType);
    }
}


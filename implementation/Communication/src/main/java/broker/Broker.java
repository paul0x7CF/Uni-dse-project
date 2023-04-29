package broker;

import communication.LocalMessage;
import communication.NetworkHandler;
import exceptions.MessageProcessingException;
import messageHandling.InfoMessageHandler;
import messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageBuilder;
import sendable.AckInfo;
import sendable.EServiceType;
import sendable.MSData;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Broker implements IServiceBroker {
    private static final Logger logger = LogManager.getLogger(Broker.class);

    private final AckHandler ackHandler;
    // not IMessageHandler because addMessageHandler is not part of the interface.
    private final MessageHandler messageHandler;
    private final NetworkHandler networkHandler;
    private MSData broadcastService;
    private ServiceRegistry serviceRegistry;

    public Broker(EServiceType serviceType, int listeningPort) {
        // listeningPort is the currentMSData port so others can send messages to this broker.
        networkHandler = new NetworkHandler(serviceType, listeningPort);

        // give currentMS to registry
        try {
            serviceRegistry = new ServiceRegistry(networkHandler.getMSData());
        } catch (UnknownHostException e) {
            logger.error("Could not get current service", e);
        }

        // Create broadcastService for sending to all services
        String broadcastAddress = networkHandler.getBroadcastAddress();
        MSData currentService = serviceRegistry.getCurrentService();
        this.broadcastService = new MSData(currentService.getId(), currentService.getType(), broadcastAddress, currentService.getPort());

        // AckHandler uses IBroker to send messages.
        ackHandler = new AckHandler(this);

        // MessageHandlers are based on the category so that other components can have their own implementations
        messageHandler = new MessageHandler();
        messageHandler.addMessageHandler(ECategory.Info, new InfoMessageHandler(this));
    }

    public void start() throws UnknownHostException {
        networkHandler.start();

        // register Microservice
        // TODO: Fixed IP addresses? Its always 10.102.102.x (Prosumer(17), Exchange(13), Forecast(9)) but whats the port?
        sendMessage(InfoMessageCreator.createRegisterMessage(networkHandler.getMSData(), broadcastService));

        // start receiving messages in new thread
        try {
            receiveMessage();
        } catch (InterruptedException | IOException | ClassNotFoundException | MessageProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        // unregister Microservice
        sendMessage(InfoMessageCreator.createUnregisterMessage(serviceRegistry.getCurrentService(), broadcastService));
        networkHandler.stop();
    }

    @Override
    public void sendMessage(Message message) {
        try {
            logger.info("{} broker sending message: {}", getCurrentService().getType(), message.getCategory());
            networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(message),
                    message.getReceiverAddress(),
                    message.getReceiverPort()));

            // here because if sending fails, the message should not be tracked
            if (!Objects.equals(message.getSubCategory(), "Ack")) {
                // don't track ack messages
                ackHandler.trackMessage(message);
            }
        } catch (IOException e) {
            logger.error("Broker: Error while sending message: {}", e.toString());
        }
    }

    /**
     * while true loop to receive messages and handle them
     *
     * @throws InterruptedException       if the thread is interrupted
     * @throws IOException                if an I/O error occurs
     * @throws ClassNotFoundException     if the class of a serialized object could not be found
     * @throws MessageProcessingException if the message could not be processed
     */
    private void receiveMessage() throws InterruptedException, IOException, ClassNotFoundException, MessageProcessingException {
        // TODO: maybe catch exceptions and try again
        // TODO: does this lock the thread?
        while (true) {
            Message message = Marshaller.unmarshal(networkHandler.receiveMessage());
            if (!Objects.equals(message.getSubCategory(), "Ack")) {
                logger.info("{} broker received message: {}", getCurrentService().getType(), message.getCategory());
                sendMessage(InfoMessageCreator.createAckMessage(message));
            }
            messageHandler.handleMessage(message);
        }
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

    public MSData findService(UUID serviceId) {
        return serviceRegistry.findService(serviceId);
    }

    public List<MSData> getServices() {
        return serviceRegistry.getAvailableServices();
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return serviceRegistry.getServicesByType(serviceType);
    }
}


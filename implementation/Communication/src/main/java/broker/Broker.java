package broker;

import communication.LocalMessage;
import communication.NetworkHandler;
import exceptions.MessageProcessingException;
import mainPackage.MainForTesting;
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
    private static final Logger logger = LogManager.getLogger(MainForTesting.class);

    private final AckHandler ackHandler;
    // not IMessageHandler because addMessageHandler is not part of the interface.
    private final MessageHandler messageHandler;
    private final NetworkHandler networkHandler;
    private final MSData broadcastService;
    private ServiceRegistry serviceRegistry;

    public Broker(EServiceType serviceType, int listeningPort) {
        ackHandler = new AckHandler(this);

        // MessageHandlers are based on the category so that other components can have their own MessageHandler
        // implementations
        messageHandler = new MessageHandler();
        messageHandler.addMessageHandler(ECategory.Info, new InfoMessageHandler(this));

        networkHandler = new NetworkHandler(serviceType, listeningPort);

        // Create broadcastService for sending to all services
        String broadcastAddress = networkHandler.getBroadcastAddress();
        broadcastService = new MSData(serviceRegistry.getCurrentService().getId(),
                serviceType, broadcastAddress, listeningPort);
    }

    public void start() throws UnknownHostException {
        networkHandler.start();
        // give currentMS to registry
        // TODO: getMSData() should store the listeningPort so messages can be sent to. Again the +1 is a workaround.
        serviceRegistry = new ServiceRegistry(networkHandler.getMSData());

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
            networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(message),
                    message.getReceiverAddress(),
                    message.getReceiverPort()));

        } catch (IOException e) {
            logger.error("Error while sending message: " + e.getMessage());
        }
        // don't track ack messages TODO: maybe use != instead of equals
        if (!Objects.equals(message.getSubCategory(), "Ack")) {
            ackHandler.trackMessage(message);
        }
    }

    /**
     * while true loop to receive messages and handle them
     *
     * @throws InterruptedException         if the thread is interrupted
     * @throws IOException                  if an I/O error occurs
     * @throws ClassNotFoundException       if the class of a serialized object could not be found
     * @throws MessageProcessingException   if the message could not be processed
     */
    private void receiveMessage() throws InterruptedException, IOException, ClassNotFoundException, MessageProcessingException {
        // TODO: maybe catch exceptions and try again
        while (true) {
            Message message = Marshaller.unmarshal(networkHandler.receiveMessage());
            messageHandler.handleMessage(message);
            if (!Objects.equals(message.getSubCategory(), "Ack")) {
                //TODO: payload needs to be AckInfo, right now it's the same as before. Instantiate MSData and use InfoMessageCreator.
                Message ackMessage = MessageBuilder.reverse(message);
                ackMessage.setCategory(ECategory.Info, "Ack");
                sendMessage(ackMessage);
            }
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


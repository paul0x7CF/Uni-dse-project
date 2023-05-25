package broker;

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

import static broker.InfoMessageBuilder.createRegisterMessage;

public class Broker implements IServiceBroker {
    private static final Logger log = LogManager.getLogger(Broker.class);
    private final AckHandler ackHandler;
    // not IMessageHandler because addMessageHandler is not part of the interface.
    private final MessageHandler messageHandler;
    private final NetworkHandler networkHandler;
    private final ServiceRegistry serviceRegistry;
    private final ConfigReader configReader;

    protected Broker(EServiceType serviceType, int listeningPort) {
        // read config
        configReader = new ConfigReader();
        int ackTimeout = Integer.parseInt(configReader.getProperty("ackTimeout"));

        // listeningPort is the currentMSData port so others can send messages to this broker.
        networkHandler = new NetworkHandler(serviceType, listeningPort);

        // give currentMS to registry
        serviceRegistry = new ServiceRegistry(networkHandler.getMSData());

        // AckHandler uses IBroker to send messages.
        ackHandler = new AckHandler(this, ackTimeout);

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
        scheduleRegisterMessage();

        // start receiving messages in new thread
        try {
            receiveMessage();
        } catch (InterruptedException | IOException | ClassNotFoundException | MessageProcessingException e) {
            throw new RuntimeException(e);
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
        try {
            log.trace("{} sending message: {}", getCurrentService().getType(), message.getCategory());
            networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(message),
                    message.getReceiverAddress(),
                    message.getReceiverPort()));

            // in try block because if sending fails, the message should not be tracked
            if (!Objects.equals(message.getSubCategory(), "Ack")) {
                ackHandler.trackMessage(message);
            }
        } catch (IOException e) {
            log.error("Broker: Error marshalling while sending message: {}", e.toString());
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
    private void receiveMessage() throws InterruptedException, IOException, ClassNotFoundException,
            MessageProcessingException {
        // TODO: maybe catch exceptions and try again
        while (true) {
            Message message = Marshaller.unmarshal(networkHandler.receiveMessage());
            // Ack and Register messages should not be acknowledged as this would cause an infinite loop and register
            // has an answer in the form of "Ping" anyway.
            if (!Objects.equals(message.getSubCategory(), "Ack")
                    && !Objects.equals(message.getSubCategory(), "Register")) {
                log.trace("{} received message: {}", getCurrentService().getType(), message.getCategory());
                sendMessage(InfoMessageBuilder.createAckMessage(message));
            }
            messageHandler.handleMessage(message);
        }
    }

    public void scheduleRegisterMessage() {
        // TODO: whats with the other addresses in config?
        String broadcastAddress = configReader.getProperty("broadcastAddress");
        int delay = Integer.parseInt(configReader.getProperty("registerMessageFrequency"));
        int prosumerPort = Integer.parseInt(configReader.getProperty("prosumerPort"));
        int storagePort = Integer.parseInt(configReader.getProperty("storagePort"));
        int exchangePort = Integer.parseInt(configReader.getProperty("exchangePort"));
        int forecastPort = Integer.parseInt(configReader.getProperty("forecastPort"));

        List<Integer> receiverPorts = List.of(prosumerPort, storagePort, exchangePort, forecastPort);
        MSData sender = serviceRegistry.getCurrentService();

        // TODO: remove this when done testing locally
        broadcastAddress = "127.0.0.1";

        for (int receiverPort : receiverPorts) {
            log.trace("Registering service {} on port {}", sender.getPort(), receiverPort);
            // TODO: for x times, make receiverPort += 10 so that all services are registered
            if (broadcastAddress != null) {
                Message message = createRegisterMessage(sender, broadcastAddress, receiverPort);
                try {
                    LocalMessage localMessage = new LocalMessage(Marshaller.marshal(message), broadcastAddress, receiverPort);
                    log.trace("Scheduling message to {} on port {}", localMessage.getReceiverAddress(), localMessage.getReceiverPort());
                    networkHandler.scheduleMessage(localMessage, delay);
                } catch (IOException e) {
                    log.error("Error while marshalling message: {}", e.toString());
                    throw new RuntimeException(e);
                }
            } else {
                log.error("Error while getting broadcast address, value is null");
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


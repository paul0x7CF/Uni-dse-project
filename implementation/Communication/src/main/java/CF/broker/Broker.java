package CF.broker;

import CF.broker.discovery.DiscoveryService;
import CF.broker.discovery.IScheduleBroker;
import CF.broker.discovery.MessageScheduler;
import CF.broker.discovery.SyncService;
import CF.communication.LocalMessage;
import CF.communication.NetworkHandler;
import CF.exceptions.MessageProcessingException;
import CF.messageHandling.InfoMessageHandler;
import CF.messageHandling.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.AckInfo;
import CF.sendable.EServiceType;
import CF.sendable.MSData;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Broker class which handles the communication between the different components. It is the central point of the
 * communication and is responsible for sending and receiving messages.
 * <p>
 * It also handles the registration of services and the discovery of other services.
 * <p>
 * It is also responsible for sending and receiving acknowledgements.
 * <p>
 * In addition to that it is also responsible for the scheduling of register and sync messages.
 * <p>
 * Before sending or receiving messages the user has to add a message handler for every category a message can have.
 *
 * @see MessageHandler
 */
public class Broker implements IServiceBroker, IScheduleBroker {
    private static final Logger log = LogManager.getLogger(Broker.class);
    private final AckHandler ackHandler;
    // not IMessageHandler because addMessageHandler is not part of the interface.
    private final MessageHandler messageHandler;
    private final MessageScheduler messageScheduler;
    private final NetworkHandler networkHandler;
    private final ServiceRegistry serviceRegistry;


    /**
     * Constructor for the Broker. It initializes the networkHandler, the serviceRegistry, the ackHandler and the
     * messageHandler.
     *
     * @param serviceType   The type of the service.
     * @param listeningPort The port the broker should listen on. The sending port is always the listening port + 1.
     */
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

        messageScheduler = new MessageScheduler();
    }

    /**
     * Starts the broker. This will schedule register and sync messages and start the sockets to receive messages.
     *
     * @throws UnknownHostException
     */
    protected void startBroker() throws UnknownHostException {
        // register Microservices
        messageScheduler.addObserver(new DiscoveryService(this));
        messageScheduler.addObserver(new SyncService(this));
        messageScheduler.startScheduling();

        // start sockets to receive messages
        networkHandler.startSockets();

        // start receiving messages, this is blocking
        try {
            receiveMessage();
        } catch (MessageProcessingException e) {
            log.error("Error while receiving message: ", e);
        }
    }

    /**
     * Stops the broker and sends unregister messages for all services. This is called in the {@link BrokerRunner} when
     * the process is terminated.
     */
    protected void stop() {
        for (MSData service : getServices()) {
            sendMessage(InfoMessageBuilder.createUnregisterMessage(serviceRegistry.getCurrentService(), service));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("Error while stopping broker: ", e);
        }
        networkHandler.stop();
    }

    /**
     * This method sends a message to the given address and port. These are defined in the message itself.
     * <p>
     * It also adds the message to the ackHandler so that it can be tracked and acknowledged.
     *
     * @param message   The {@link Message} to be sent.
     */
    @Override
    public void sendMessage(Message message) {
        log.trace("{} sending message: {}", getCurrentService().getType(), message.getCategory());
        networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(message),
                message.getReceiverAddress(),
                message.getReceiverPort()));

        printMessage(message,true);
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
     * @throws MessageProcessingException if the message could not be processed
     */
    protected void receiveMessage() throws MessageProcessingException {
        ReceivedMessages receiver = new ReceivedMessages();
        // TODO: maybe catch exceptions and try again
        while (true) {
            byte[] receivedMessage = networkHandler.receiveMessage();
            if (receivedMessage == null) {
                return;
            }
            Message message = Marshaller.unmarshal(receivedMessage);
            log.trace("{} received {}", getCurrentService().getType(), message.getSubCategory());

            // Ack and Register messages should not be acknowledged as this would cause an infinite loop and register
            // has an answer in the form of "Ping" anyway.
            if (!Objects.equals(message.getSubCategory(), "Ack")
                    && !Objects.equals(message.getSubCategory(), "Register")) {
                sendMessage(InfoMessageBuilder.createAckMessage(message));
            }

            if (!receiver.isMessageAlreadyReceived(message)) {
                messageHandler.handleMessage(message);
            }

            receiver.saveMessage(message);
        }
    }

    /**
     * Debug
     *
     * @param message   The message to be printed.
     * @param isSender  True if the message was sent, false if it was received.
     */
    private void printMessage(Message message, boolean isSender) {
        int port = getCurrentService().getPort();
        if (port == 11001) {
            String sr = isSender ? "S" : "R ";
            String ackBind = isSender ? "sent for" : "received for";
            String messageBind = isSender ? "message sent to" : "message received from";
            if (isSender && !message.getSubCategory().equals("Ack")) {
                log.trace("######### {} {} sent to {} | {}", port, message.getSubCategory(), message.getReceiverPort(),
                        message.getMessageID().toString().substring(0, 4));
            } else {
                log.trace("######### {} received from {} | {}", port, message.getSenderPort(), message.getMessageID());
            }
            int from = isSender ? message.getSenderPort() : message.getReceiverPort();
            if (message.getSubCategory().equals("Ack")) {
                AckInfo ackInfo = (AckInfo) message.getSendable(AckInfo.class);
                log.trace("######### "+sr+port+" {} {} {} "+sr+" {}", message.getSubCategory(), ackBind,
                        ackInfo.getMessageID().toString().substring(0, 4), from);
            } else {
                log.trace("######### "+sr+port+" {} {} {} | {}", message.getSubCategory(), messageBind, from, message.getMessageID().toString().substring(0, 4));
            }
        } else if (port == 9000) {
            log.trace("9000");
        }
    }

    /**
     * This method is called by the {@link InfoMessageHandler} when an ack is received. It will then be removed from the
     * tracking list.
     *
     * @param ack   The {@link AckInfo} that was received.
     */
    @Override
    public void ackReceived(AckInfo ack) {
        ackHandler.ackReceived(ack);
    }

    @Override
    public boolean registerService(MSData msData) {
        return serviceRegistry.registerService(msData);
    }

    public void unregisterService(MSData msData) {
        log.debug("Unregistering service: {}", msData.getPort());
        serviceRegistry.unregisterService(msData);
    }

    @Override
    public MSData getCurrentService() {
        return serviceRegistry.getCurrentService();
    }

    protected MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public MSData findService(UUID serviceId) {
        return serviceRegistry.findService(serviceId);
    }

    public boolean serviceExists(int port) {
        return serviceRegistry.serviceExists(port);
    }

    public List<MSData> getServices() {
        return serviceRegistry.getAvailableServices();
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return serviceRegistry.getServicesByType(serviceType);
    }
}
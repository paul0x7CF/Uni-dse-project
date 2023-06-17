package CF.broker;

import CF.messageHandling.IMessageHandler;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

/**
 * BrokerRunner class that is the Runnable for the Broker. It is used to start the broker in a new thread, otherwise the
 * program would block.
 *
 * @see Broker
 */
public class BrokerRunner implements Runnable {
    private static final Logger log = LogManager.getLogger(BrokerRunner.class);
    private final Broker broker;

    /**
     * Constructor for the BrokerRunner. It initializes the broker.
     *
     * @param serviceType   The type of the service. This is used when registering the service with other services.
     * @param listeningPort The port the broker should listen on. The sending port is always the listening port + 1.
     */
    public BrokerRunner(EServiceType serviceType, int listeningPort) {
        this.broker = new Broker(serviceType, listeningPort);
    }

    /**
     * Starts the broker in a new thread. It also adds a shutdown hook so the broker unregisters when the program is
     * stopped. This is necessary because if the hook was not added, the broker would not unregister and the other
     * services would not know that this service is not available anymore.
     */
    @Override
    public void run() {
        try {
            log.info("Starting {} instance on port {}", broker.getCurrentService().getType(), broker.getCurrentService().getPort());

            // Shutdown Hook so the broker unregisters when the program is stopped.
            Runtime.getRuntime().addShutdownHook(new Thread(broker::stop));

            // Start the broker.
            broker.startBroker();
        } catch (UnknownHostException e) {
            log.error("Error while starting broker: ", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a message to the receiver specified in the message.
     *
     * @param message   The message to send.
     */
    public void sendMessage(Message message) {
        broker.sendMessage(message);
    }

    public UUID getId() {
        return broker.getCurrentService().getId();
    }

    public MSData getCurrentService() {
        return broker.getCurrentService();
    }

    public void addMessageHandler(ECategory category, IMessageHandler handler) {
        broker.getMessageHandler().addMessageHandler(category, handler);
    }

    public void stop() {
        log.info("Stopping {} {}", broker.getCurrentService().getType(), broker.getCurrentService().getPort());
        broker.stop();
    }

    public List<MSData> getServices() {
        return broker.getServices();
    }

    public MSData findService(UUID serviceId) {
        return broker.findService(serviceId);
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return broker.getServicesByType(serviceType);
    }
}

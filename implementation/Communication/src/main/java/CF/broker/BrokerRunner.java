package CF.broker;

import CF.messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.ECategory;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.MSData;

import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

public class BrokerRunner implements Runnable {
    private static final Logger log = LogManager.getLogger(BrokerRunner.class);
    private final Broker broker;

    public BrokerRunner(EServiceType serviceType, int listeningPort) {
        this.broker = new Broker(serviceType, listeningPort);
    }

    @Override
    public void run() {
        try {
            log.info("Starting {} instance on port {}", broker.getCurrentService().getType(), broker.getCurrentService().getPort());

            // Shutdown Hook so the broker unregisters when the program is stopped.
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    broker.stop();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));

            broker.startBroker();
        } catch (UnknownHostException e) {
            log.error("Error while starting broker: ", e);
            throw new RuntimeException(e);
        }
    }

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
        try {
            broker.stop();
        } catch (InterruptedException e) {
            log.error("Error while stopping broker: ", e);
        }
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

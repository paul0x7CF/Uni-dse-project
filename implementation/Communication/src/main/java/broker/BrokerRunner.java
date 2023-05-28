package broker;

import messageHandling.IMessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

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
            broker.startBroker();
        } catch (UnknownHostException e) {
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

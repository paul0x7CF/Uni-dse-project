package broker.discovery;

import broker.InfoMessageBuilder;
import broker.Marshaller;
import mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;
import sendable.MSDataArray;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SyncService implements IMessageSchedulerObserver {
    private static final Logger log = LogManager.getLogger(SyncService.class);
    private final IScheduleBroker broker;
    ConfigReader configReader = new ConfigReader();
    MSData currentService;

    public SyncService(IScheduleBroker broker) {
        this.broker = broker;

        currentService = broker.getCurrentService();
    }

    @Override
    public void scheduleMessages(ScheduledExecutorService scheduler) {
        int messageFrequency = Integer.parseInt(configReader.getProperty("syncMessageFrequency"));
        scheduler.scheduleAtFixedRate(this::sendSyncMessages, 1, messageFrequency, TimeUnit.SECONDS);
    }

    private void sendSyncMessages() {
        List<MSData> services = broker.getServices();
        MSDataArray servicesArray = new MSDataArray(currentService, services.toArray(new MSData[0]));
        for (MSData service : services) {
            if (!service.equals(broker.getCurrentService())
                    && service.getType() == EServiceType.Consumption && currentService.getPort() == 9000) {
                log.info("Sending sync message with size {}", services.size()); // TODO: remove
                log.trace("Sending sync message to {}", service.getPort());
                Message message = InfoMessageBuilder.createSyncMessage(currentService, service, servicesArray);
                byte[] bytes = Marshaller.marshal(message);
                String s = new String(bytes);
                // log.warn("Marshalling: {}", s);
                // log.warn("Unmarshalling: {}", Marshaller.unmarshal(bytes).getPayload());

                broker.sendMessage(message);
            }
        }
    }
}
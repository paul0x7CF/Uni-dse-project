package broker.discovery;

import broker.InfoMessageBuilder;
import mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
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
    int messageFrequency;

    public SyncService(IScheduleBroker broker) {
        this.broker = broker;

        currentService = broker.getCurrentService();
        messageFrequency = Integer.parseInt(configReader.getProperty("syncMessageFrequency"));
    }

    @Override
    public void scheduleMessages(ScheduledExecutorService scheduler) {
        scheduler.scheduleAtFixedRate(this::sendSyncMessages, 1, messageFrequency, TimeUnit.SECONDS);
    }

    private void sendSyncMessages() {
        List<MSData> services = broker.getServices();
        for (MSData service : services) {
            if (service.getId() != broker.getCurrentService().getId()) {
                log.trace("Sending empty sync message to {}", service.getPort());
                Message message = InfoMessageBuilder.createSyncMessage(currentService, service, new MSDataArray(currentService, new MSData[0]));
                broker.sendMessage(message);
            }
        }
    }
}
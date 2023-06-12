package CF.broker.discovery;

import CF.broker.InfoMessageBuilder;
import CF.mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import CF.sendable.MSDataList;

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
        // TODO: add messageFrequency to this delay (currently only 1-3 seconds)
        int delay = (int) (Math.random() * 2) + 1; // delay between 1 and 3 seconds + messageFrequency
        scheduler.scheduleAtFixedRate(this::sendSyncMessages, delay, messageFrequency, TimeUnit.SECONDS);
    }

    private void sendSyncMessages() {
        List<MSData> services = broker.getServices();
        MSDataList servicesArray = new MSDataList(currentService, services);
        for (MSData service : services) {
            if (!service.equals(broker.getCurrentService())
                    && service.getType() == EServiceType.Prosumer && currentService.getPort() == 9000) {
                log.trace("Sending sync message to {}", service.getPort());
//                log.warn("Sending sync message with size() {}", servicesArray.getMsDataList().size());
                Message message = InfoMessageBuilder.createSyncMessage(currentService, service, servicesArray);
                // TODO: This does not get unmarshalled correctly. It is the List<MSData> that is the problem.
                // TODO: There is a memory leak somewhere. About 1GB every 10 minutes.
                // broker.sendMessage(message);

/*
                byte[] bytes = Marshaller.marshal(message);
                String s = new String(bytes);
                log.warn("Marshalling: {}", s);
                log.warn("Unmarshalling: {}", Marshaller.unmarshal(bytes).getPayload());
*/

            }
        }
    }
}
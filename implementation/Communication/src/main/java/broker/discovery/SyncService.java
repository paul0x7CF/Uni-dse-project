package broker.discovery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SyncService extends DiscoveryService {
    private static final Logger log = LogManager.getLogger(SyncService.class);
    private final IScheduleBroker broker;

    public SyncService(IScheduleBroker broker) {
        super(broker);
        this.broker = broker;
    }
}

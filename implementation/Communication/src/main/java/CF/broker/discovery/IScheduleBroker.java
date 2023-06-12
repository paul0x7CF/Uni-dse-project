package CF.broker.discovery;

import CF.broker.IBroker;
import CF.sendable.MSData;

import java.util.List;
import java.util.UUID;

public interface IScheduleBroker extends IBroker {
    MSData getCurrentService();
    List<MSData> getServices();
    MSData findService(UUID serviceId);
    boolean serviceExists(int port);
}

package CF.broker.discovery;

import CF.broker.IBroker;
import CF.sendable.MSData;

import java.util.List;
import java.util.UUID;

/**
 * Interface for the ScheduleBroker. This interface is used to make provide the necessary methods for the
 * {@link IMessageSchedulerObserver} implementations to schedule messages.
 *
 */
public interface IScheduleBroker extends IBroker {
    MSData getCurrentService();
    List<MSData> getServices();
    MSData findService(UUID serviceId);
    boolean serviceExists(int port);
}

package CF.broker.discovery;

import java.util.concurrent.ScheduledExecutorService;

/**
 * This interface is used to schedule messages.
 * The MessageScheduler class will call the scheduleMessages method on all observers.
 */
public interface IMessageSchedulerObserver {
    void scheduleMessages(ScheduledExecutorService scheduler);
}

package broker.discovery;

import java.util.concurrent.ScheduledExecutorService;

public interface IMessageSchedulerObserver {
    void scheduleMessages(ScheduledExecutorService scheduler);
}

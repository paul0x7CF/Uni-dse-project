package broker.discovery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MessageScheduler {
    private static final Logger log = LogManager.getLogger(MessageScheduler.class);
    private final ScheduledExecutorService scheduler;
    private final List<IMessageSchedulerObserver> observers;

    public MessageScheduler() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.observers = new ArrayList<>();
    }

    public void addObserver(IMessageSchedulerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(IMessageSchedulerObserver observer) {
        observers.remove(observer);
    }

    public void startScheduling() {
        log.info("Starting message scheduling");
        for (IMessageSchedulerObserver observer : observers) {
            observer.scheduleMessages(scheduler);
        }
    }
}

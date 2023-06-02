package broker.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessageScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<MessageSchedulerObserver> observers = new ArrayList<>();

    public void addObserver(MessageSchedulerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(MessageSchedulerObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (MessageSchedulerObserver observer : observers) {
            observer.handleSchedule();
        }
    }

    public void startScheduler(int initialDelay, int period) {
        scheduler.scheduleAtFixedRate(this::notifyObservers, initialDelay, period, TimeUnit.SECONDS);
    }
}
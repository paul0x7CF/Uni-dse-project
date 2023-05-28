package broker.discovery;

import communication.LocalMessage;
import sendable.MSData;

public interface IScheduleBroker {
    void scheduleMessage(LocalMessage localMessage, int delay);
    MSData getCurrentService();
}

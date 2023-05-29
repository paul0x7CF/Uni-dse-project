package broker.discovery;

import communication.LocalMessage;
import sendable.MSData;

public interface IScheduleBroker {
    void scheduleRegisterMessage(LocalMessage localMessage, int delay);
    MSData getCurrentService();
}

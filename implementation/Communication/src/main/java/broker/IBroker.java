package broker;

import protocol.Message;
import sendable.MSData;

public interface IBroker {
    void sendMessage(Message message);
    MSData getCurrentService();
}

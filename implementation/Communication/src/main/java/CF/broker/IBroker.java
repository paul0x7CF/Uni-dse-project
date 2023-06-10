package CF.broker;

import CF.protocol.Message;

public interface IBroker {
    void sendMessage(Message message);
}

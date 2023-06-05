package broker;

import protocol.Message;

public interface IBroker {
    void sendMessage(Message message);
}

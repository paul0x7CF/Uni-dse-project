package broker;

import protocol.Message;

public interface IBroker {
    void resendMessage(Message message);
}

package broker;

import protocol.Message;

public interface IAckCallback {
    void resendMessage(Message message);
}

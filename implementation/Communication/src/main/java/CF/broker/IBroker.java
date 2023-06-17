package CF.broker;

import CF.protocol.Message;

/**
 * Interface for the broker. It is used to send messages.
 */
public interface IBroker {
    void sendMessage(Message message);
}

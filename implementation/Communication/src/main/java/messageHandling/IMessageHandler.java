package messageHandling;

import exceptions.MessageProcessingException;
import protocol.Message;

public interface IMessageHandler {
    void handleMessage(Message message) throws MessageProcessingException;
}

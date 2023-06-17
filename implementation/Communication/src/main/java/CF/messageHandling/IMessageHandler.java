package CF.messageHandling;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.protocol.Message;

/**
 * Interface for handling messages. Used by each Microservice to handle incoming messages. There needs to be a handler
 * for every category a message can have.
 */
public interface IMessageHandler {
    void handleMessage(Message message) throws MessageProcessingException, RemoteException;
}
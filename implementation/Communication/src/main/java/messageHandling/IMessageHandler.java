package messageHandling;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import protocol.Message;

/**
 * Interface for handling messages. Used by each Microservice to handle incoming messages.
 */
public interface IMessageHandler {
    void handleMessage(Message message) throws MessageProcessingException, RemoteException;
}
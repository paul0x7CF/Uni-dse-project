package MSStorage.communication;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

public class StorageMessageHandler implements IMessageHandler {
    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }

    private void handleTransactionMessage(Message message) throws MessageProcessingException, RemoteException {

    }
}

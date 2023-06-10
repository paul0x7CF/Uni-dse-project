package MSS.communication;

import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import CF.protocol.Message;

public class StorageMessageHandler implements IMessageHandler {
    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }

    private void handleTransactionMessage(Message message) throws MessageProcessingException, RemoteException {

    }
}

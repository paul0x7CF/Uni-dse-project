package MSProsumer.Communication;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

public class ProsumerMessageHandler implements IMessageHandler {
    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }

    private void handleForecast(Message message) {

    }

    private void handleAuction(Message message) {

    }

    private void handleExchange(Message message) {

    }
}

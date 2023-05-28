package Logic;

import broker.IBroker;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

public class AuctionMessageHandler implements IMessageHandler {

    private IBroker broker;;
    private Prosumer prosumer;
    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }
}

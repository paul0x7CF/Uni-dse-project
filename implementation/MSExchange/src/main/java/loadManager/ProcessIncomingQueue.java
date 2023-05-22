package loadManager;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;
import sendable.Bid;

public class ProcessIncomingQueue implements IMessageHandler {

    public ProcessIncomingQueue() {
        //TODO: implement
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        //TODO: implement
    }

    public void process(Message message) {
        //TODO: implement
    }

    private Bid buildBid(Message message) {
        //TODO: implement
        return null;
    }


}

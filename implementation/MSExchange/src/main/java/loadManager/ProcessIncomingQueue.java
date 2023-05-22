package loadManager;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.networkManagment.ExtendedMessageBuilder;
import messageHandling.IMessageHandler;
import protocol.Message;
import sendable.Bid;
import sendable.Sell;
import sendable.Transaction;

public class ProcessIncomingQueue implements IMessageHandler {

    public ProcessIncomingQueue() {
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {

    }

    public void process(Message message) {

    }

    private Bid buildBid(Message message) {
        //TODO: implement
        return null;
    }


}

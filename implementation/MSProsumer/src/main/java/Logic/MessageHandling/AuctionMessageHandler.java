package Logic.MessageHandling;

import Logic.Prosumer.Prosumer;
import broker.IBroker;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import messageHandling.IMessageHandler;
import protocol.Message;

public class AuctionMessageHandler implements IMessageHandler {

    private IBroker broker;
    private Prosumer prosumer;
    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
       /* Bid, 
        Sell, 
        Transaction, 
        TimeSlot*/
        switch (message.getSubCategory()) {
            case "BidHigher" -> handleBidHigher(message);
            case "SellLower" -> handleSellLower(message);
            
        }
    }

    private void handleSellLower(Message message) {
    }

    private void handleBidHigher(Message message) {
    }


}

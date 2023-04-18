package loadManager.prosumerActionManagement;

import loadManager.auctionManagement.AuctionManager;

import java.util.UUID;

public class ProsumerManager {
    AuctionManager auctionManager;
    public ProsumerManager(AuctionManager auctionManager){
        this.auctionManager = auctionManager;
    }

    public void handleNewAction(UUID prosumerID, EProsumerTyp prosumerTyp, double price, double kwh) {
    }
}

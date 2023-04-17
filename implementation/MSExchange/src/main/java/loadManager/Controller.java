package loadManager;

import loadManager.auctionManagement.AuctionManager;
import loadManager.auctionManagement.AuctionProsumerTracker;
import loadManager.networkManagment.NetworkManager;
import loadManager.prosumerActionManagement.EProsumerTyp;
import loadManager.prosumerActionManagement.ProsumerManager;
import loadManager.timeSlotManagement.TimeSlotManager;

import java.util.UUID;

public class Controller {
    private AuctionManager auctionManager = new AuctionManager();
    private ProsumerManager prosumerManager;
    private NetworkManager networkManager;
    private TimeSlotManager timeSlotManager = new TimeSlotManager();
    private AuctionProsumerTracker auctionProsumerTracker = new AuctionProsumerTracker();

    public Controller(){
        prosumerManager= new ProsumerManager(auctionManager);
        this.networkManager = new NetworkManager(this);
    }

    public synchronized void handleProsumerAction(UUID prosumerID, EProsumerTyp prosumerTyp, double price, double kwh) {
        prosumerManager.handleNewAction(prosumerID, prosumerTyp, price, kwh);
    }

}

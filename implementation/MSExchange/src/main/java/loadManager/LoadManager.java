package loadManager;

import loadManager.networkCommunication.NetworkCommunication;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LoadManager{
    private List<ExchangeServiceInformation> exchangeServicesInformation;
    private Map<UUID, Double> openAuctions;
    private NetworkCommunication networkCommunication;
    private AuctionProsumerTracker auctionProsumerTracker= new AuctionProsumerTracker();
    public LoadManager(int port) {
        this.networkCommunication= new NetworkCommunication(this, port);
    }

    public void start() {
    }

    public void handleNewBid(UUID prosumerID, double price, double kwh) {
    }

    public void handleNewSell(UUID prosumerID, double price, double kwh) {
    }

    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
    }

    public void exchangeAtCapacity(UUID exchangeID) {
    }

    public void setOpenAuctions(Map<UUID, Double> openAuctions) {
    }

    public void closeAuction(UUID auctionId){}
    private void findProsumersNotSatisfied(){}
    private void sendProsumerToStorage(UUID prosumer, double kwh){}

}

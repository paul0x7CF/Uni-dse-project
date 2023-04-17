package loadManager;

import loadManager.AuctionProsumerTracker;

import java.util.List;
import java.util.UUID;

public class AuctionFindingAlgorithm extends Thread {
    private List<UUID> auctionParticipation;
    private double bidPrice;
    private double neededKwH;
    private UUID prosumerId;

    public AuctionFindingAlgorithm(double bidPrice, double neededKwH, UUID prosumerId) {
        bidPrice = bidPrice;
        neededKwH = neededKwH;
        prosumerId = prosumerId;
    }

    public void allocateAuctions() {//while -> not all neededKwh covered -> search auction}
    }

    private void addProsumerToAuction(UUID auction, UUID prosumer) {
    }


}
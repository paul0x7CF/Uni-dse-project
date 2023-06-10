package loadManager.prosumerActionManagement.bidManagement;

import CF.sendable.Bid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BidForTimeSlot {
    private Bid incomingBid;
    private List<Bid> bidsInAuctions;

    public BidForTimeSlot(Bid incomingBid) {
        this.incomingBid = incomingBid;
        this.bidsInAuctions = new ArrayList<>();
    }

    public Bid getIncomingBid() {
        return incomingBid;
    }

    public List<Bid> getBidsInAuctions() {
        return bidsInAuctions;
    }

    public double getCoveredVolume() {
        return bidsInAuctions.stream().mapToDouble(Bid::getVolume).sum();
    }

    public void addBid(Bid bid) {
        bidsInAuctions.add(bid);
    }

    public void updateBids(List<UUID> auctions) {
        Iterator<Bid> iterator = bidsInAuctions.iterator();
        while (iterator.hasNext()) {
            Bid bid = iterator.next();
            if (!auctions.contains(bid.getAuctionID())) {
                iterator.remove();
            }
        }
    }
}

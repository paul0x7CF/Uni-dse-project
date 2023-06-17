package loadManager.prosumerActionManagement.bidManagement;

import CF.sendable.Bid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BidForTimeSlot {
    private static final Logger logger = LogManager.getLogger(BidForTimeSlot.class);
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

    public void updateBids(List<UUID> auctionsBidderIsCurrentWinner) {
        List<Bid> bidsToRemove = new ArrayList<>();
        for (Bid bid : bidsInAuctions) {
            if (!auctionsBidderIsCurrentWinner.contains(bid.getAuctionID().get())) {
                bidsToRemove.add(bid);
                logger.debug("LOAD_MANAGER: Bidder has been outbid for auction {}", bid.getAuctionID().get());
            }
        }
        bidsInAuctions.removeAll(bidsToRemove);
    }
}

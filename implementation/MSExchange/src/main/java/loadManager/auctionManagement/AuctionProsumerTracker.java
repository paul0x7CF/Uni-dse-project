package loadManager.auctionManagement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionProsumerTracker{
    private Map<UUID, List<UUID>> biddersPerAuction;
    public List<UUID> getBiddersLostInEveryAuction(){return null;}
    public void addBidderToAuction(UUID auctionId, UUID bidderId){}
}
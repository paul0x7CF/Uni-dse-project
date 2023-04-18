package loadManager.auctionManagement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionProsumerTracker{
    private Map<UUID, UUID> biddersPerAuction;
    public List<UUID> biddersLostInEveryAuction(){return null;}
    public void addBidderToAuction(UUID auctionId, UUID bidderId){}
    public void addAuctionToList(UUID auctionId){}
}
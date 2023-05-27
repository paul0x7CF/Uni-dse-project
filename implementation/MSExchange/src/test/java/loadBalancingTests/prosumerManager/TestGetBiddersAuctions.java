package loadBalancingTests.prosumerManager;

import loadManager.SellInformation;
import loadManager.auctionManagement.Auction;
import loadManager.auctionManagement.AuctionManager;
import org.junit.jupiter.api.Test;
import sendable.Bid;
import sendable.Sell;

import java.util.UUID;

public class TestGetBiddersAuctions {
    private double volume = 100;
    private double price = 100;
    private UUID timeSlotID = UUID.randomUUID();
    private UUID bidderID1 = UUID.randomUUID();
    private UUID bidderID2 = UUID.randomUUID();

    @Test
    public void givenAuctionsFromBidder_getBiddersAuctions_expectedCorrectList() {
//TODO: wrong class -> change
        // Arrange
        AuctionManager auctionManager = new AuctionManager();
        for (int i = 0; i < 10; i++) {
            Sell sell = new Sell(volume, price, timeSlotID, UUID.randomUUID());
            SellInformation sellInformation = new SellInformation(sell, UUID.randomUUID());

            Auction auction = new Auction(UUID.randomUUID(), sellInformation);
            auctionManager.addAuction(auction);

            if (i % 2 == 0) {
                Bid bid1 = new Bid(volume, price, timeSlotID, bidderID1);
                auctionManager.setBidder(auction.getAuctionId(), bid1);
            } else {
                Bid bid2 = new Bid(volume, price, timeSlotID, bidderID2);
                auctionManager.setBidder(auction.getAuctionId(), bid2);
            }
        }

        //Act

        //Assert
    }
}

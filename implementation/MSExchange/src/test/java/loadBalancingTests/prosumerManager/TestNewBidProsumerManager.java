package loadBalancingTests.prosumerManager;

import org.junit.Test;
import sendable.Bid;

import java.util.UUID;

public class TestNewBidProsumerManager {
    private double volume = 100;
    private double price = 100;
    private UUID timeSlotID = UUID.randomUUID();

    @Test
    public void givenAuctions_handleNewBid_expectedAuctionManagerWithBidAndProsumerTracker() {
        //arrange
        Bid bid = new Bid(volume, price, timeSlotID);


        //act
        //assert
    }
}

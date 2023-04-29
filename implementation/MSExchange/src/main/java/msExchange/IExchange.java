package msExchange;

import sendable.Bid;
import sendable.Sell;

public interface IExchange {
    void processBidQueue();

    void receivedBid(Bid bid);

    void receivedSell(Sell sell);
}

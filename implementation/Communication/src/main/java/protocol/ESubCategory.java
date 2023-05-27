package protocol;

import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;
import sendable.Transaction;

public enum ESubCategory {
    Ping,
    Register,
    Unregister,
    Ack,
    Error,
    Bid,
    Sell,
    Transaction,
    TimeSlot
}

package loadManager.networkManagment;

import java.util.UUID;

public enum EBuildCategory {
    BidToProsumer,
    BidToExchange,
    SellToProsumer,
    SellToExchange,
    Transaction,
    TimeSlot,
    BidToStorage,
    SellToStorage;

    private UUID uuid;

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}

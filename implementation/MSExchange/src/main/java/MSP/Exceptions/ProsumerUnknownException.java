package MSP.Exceptions;

import java.util.Optional;
import java.util.UUID;

public class ProsumerUnknownException extends Exception {
    private Optional<UUID> bidderID;

    public ProsumerUnknownException(String message, Optional<UUID> bidderID) {
        super(message);
        this.bidderID = bidderID;
    }

    public Optional<UUID> getBidderID() {
        return bidderID;
    }
}

package sendable;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class holds the information about other microservices in the network.
 */
public class MSData implements ISendable, Serializable {
    private final UUID id;
    private final EServiceType type;
    private final String address;
    private final int port;
    // private LocalDateTime lastSeen = LocalDateTime.now(); TODO: gson can't handle LocalDateTime
    // private LocalDateTime lastUpdated = LocalDateTime.now(); TODO: gson can't handle LocalDateTime

    public MSData(UUID id, EServiceType type, String address, int port) {
        this.id = id;
        this.type = type;
        this.address = address;
        this.port = port;
    }

    public UUID getId() {
        return id;
    }

    public EServiceType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof MSData otherMSData) {
            return this.id.equals(otherMSData.id);
        }
        return false;
    }

    /*
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
     */
}

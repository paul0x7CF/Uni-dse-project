package sendable;

import sendable.EMSType;
import sendable.ISendable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class holds the information about other microservices in the network.
 */
public class MSData implements ISendable {
    private UUID id;
    EMSType type;
    private String address;
    private int port;
    private LocalDateTime lastSeen;
    private LocalDateTime lastUpdated;

    public MSData(UUID id, EMSType type, String address, int port, LocalDateTime lastSeen, LocalDateTime lastUpdated) {
        this.id = id;
        this.type = type;
        this.address = address;
        this.port = port;
        this.lastSeen = lastSeen;
        this.lastUpdated = lastUpdated;
    }

    public UUID getId() {
        return id;
    }

    public EMSType getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

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
}

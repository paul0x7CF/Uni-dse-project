package sendable;

import java.time.LocalDateTime;
import java.util.UUID;
import com.google.gson.Gson;

/**
 * This class holds the information about other microservices in the network.
 */
public class MSData implements ISendable {
    private UUID id;
    EServiceType type;
    private String address;
    private int port;
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

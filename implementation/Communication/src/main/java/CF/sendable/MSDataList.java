package CF.sendable;

import java.io.Serializable;
import java.util.List;

/**
 * This class holds the information about other microservices in the network. It is used to send a list of microservices
 * to another microservice using Sync messages.
 */
public class MSDataList implements ISendable, Serializable {
    private final MSData sender;
    private final List<MSData> msDataList;

    public MSDataList(MSData sender, List<MSData> msDataList) {
        this.sender = sender;
        this.msDataList = msDataList;
    }

    public MSData getSender() {
        return sender;
    }

    public List<MSData> getMsDataList() {
        return msDataList;
    }

    public boolean isEmpty() {
        return msDataList.size() == 0;
    }
}

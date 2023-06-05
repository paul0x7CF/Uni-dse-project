package sendable;

import java.io.Serializable;
import java.util.List;

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

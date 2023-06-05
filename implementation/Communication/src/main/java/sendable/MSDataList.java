package sendable;

import java.util.List;

public class MSDataList implements ISendable {
    private final MSData sender;
    private final List<MSData> msDataList;

    public MSDataList(MSData sender, List<MSData> msDataArray) {
        this.sender = sender;
        this.msDataList = msDataArray;
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

package sendable;

public class MSDataArray implements ISendable {
    private final MSData sender;
    private final MSData[] msDataArray;

    public MSDataArray(MSData sender, MSData[] msDataArray) {
        this.sender = sender;
        this.msDataArray = msDataArray;
    }

    public MSData getSender() {
        return sender;
    }

    public MSData[] getMsDataArray() {
        return msDataArray;
    }

    public boolean isEmpty() {
        return msDataArray.length == 0;
    }
}

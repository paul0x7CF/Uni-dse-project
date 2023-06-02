package sendable;

public class MSDataArray implements ISendable {
    private MSData sender;
    private MSData[] msDataArray;

    public MSDataArray(MSData sender, MSData[] msDataList) {
        this.sender = sender;
        this.msDataArray = msDataList;
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

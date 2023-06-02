package sendable;

public class MSDataArray implements ISendable {
    private MSData[] msDataArray;

    public MSDataArray(MSData[] msDataList) {
        this.msDataArray = msDataList;
    }

    public MSData[] getMsDataArray() {
        return msDataArray;
    }

    public void setMsDataArray(MSData[] msDataList) {
        this.msDataArray = msDataList;
    }
}

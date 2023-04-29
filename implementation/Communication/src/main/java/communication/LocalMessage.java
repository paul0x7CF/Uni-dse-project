package communication;

public class LocalMessage {
    private byte[] message;
    private String receiverAddress;
    private int receiverPort;

    public LocalMessage(byte[] message, String receiverAddress, int receiverPort) {
        this.message = message;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
    }

    public byte[] getMessage() {
        return message;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

}

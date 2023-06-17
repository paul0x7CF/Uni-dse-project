package CF.communication;

/**
 * This class is used to store the message and the address + port of the receiver. It is needed because the
 * NetworkManager gets a byte[] and cannot extract the address and port from it.
 */
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

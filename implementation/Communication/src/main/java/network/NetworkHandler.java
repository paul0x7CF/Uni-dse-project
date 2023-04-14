package network;

import message.Message;
import sendable.EMSType;
import sendable.MSData;

import java.util.Map;
import java.util.UUID;

public class NetworkHandler {
    private Map<EMSType, MSData> availableMS;
    private MSData currentMS;
    private ReceiveSocket input;
    private SendSocket output;

    public NetworkHandler() {
        input = new ReceiveSocket();
        output = new SendSocket();
    }

    public void send(Message message) {
        // output.send(message);
    }

    public void receive() {
        // input.receive();
    }
}

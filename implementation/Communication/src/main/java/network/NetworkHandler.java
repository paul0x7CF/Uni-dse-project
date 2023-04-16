package network;

import message.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.Map;

public class NetworkHandler {
    private Map<EServiceType, MSData> availableMS;
    private MSData currentMS;
    private InputSocket input;
    private OutputSocket output;

    public NetworkHandler() {
        input = new InputSocket();
        output = new OutputSocket();
    }

    public void send(Message message) {
        // output.send(message);
    }

    public void receive() {
        // input.receive();
    }
}

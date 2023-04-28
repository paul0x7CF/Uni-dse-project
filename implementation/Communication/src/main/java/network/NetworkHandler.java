package network;

import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class NetworkHandler {
    private Map<EServiceType, MSData> availableMS;
    private MSData currentMS;
    private InputSocket input;
    private OutputSocket output;

    public NetworkHandler() {
        // input = new InputSocket();
        // output = new OutputSocket();
    }

    public void send(Message message) {
        // output.send(message);
    }

    public byte[] receive() {
        return null;
    }
}

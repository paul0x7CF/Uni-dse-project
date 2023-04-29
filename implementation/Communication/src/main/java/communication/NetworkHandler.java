package communication;

import sendable.EServiceType;
import sendable.MSData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class handles the network communication for a microservice and provides methods to send and receive messages.
 * It uses two threads to send and receive messages.
 * The input and output queues are blocking queues so that the threads are blocked when there is no message to send or
 * receive.
 */
public class NetworkHandler {
    private final BlockingQueue<byte[]> inputQueue;
    private final BlockingQueue<LocalMessage> outputQueue;
    private final InputSocket inputSocket;
    private final OutputSocket outputSocket;
    private final EServiceType serviceType;
    private final int listeningPort;
    private final ExecutorService executor;

    public NetworkHandler(EServiceType serviceType, int listeningPort) {
        this.serviceType = serviceType;
        this.listeningPort = listeningPort;

        inputQueue = new LinkedBlockingQueue<>();
        outputQueue = new LinkedBlockingQueue<>();

        inputSocket = new InputSocket(inputQueue, listeningPort);
        outputSocket = new OutputSocket(outputQueue, listeningPort + 1); // TODO: how to not do +1

        executor = Executors.newFixedThreadPool(2);
    }

    public void start() {
        executor.execute(outputSocket);
        executor.execute(inputSocket);
    }

    public void stop() {
        executor.shutdown();
    }

    public void sendMessage(LocalMessage localMessage) {
        outputQueue.add(localMessage);
    }

    public byte[] receiveMessage() throws InterruptedException {
        return inputQueue.take();
    }

    public MSData getMSData() throws UnknownHostException {
        return new MSData(UUID.randomUUID(), serviceType, InetAddress.getLocalHost().getHostAddress(), listeningPort);
    }

    public String getBroadcastAddress() {
        // TODO: this is not the correct way to do this
        return "localhost";
    }
}

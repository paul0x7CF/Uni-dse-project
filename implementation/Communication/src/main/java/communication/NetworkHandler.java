package communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.*;

import static broker.InfoMessageBuilder.createRegisterMessage;

/**
 * This class handles the network communication for a microservice and provides methods to send and receive messages.
 * It uses two threads to send and receive messages.
 * The input and output queues are blocking queues so that the threads are blocked when there is no message to send or
 * receive.
 */
public class NetworkHandler {
    private static final Logger log = LogManager.getLogger(NetworkHandler.class);
    private final BlockingQueue<byte[]> inputQueue;
    private final BlockingQueue<LocalMessage> outputQueue;
    private final BlockingQueue<LocalMessage> broadcastQueue;
    private final InputSocket inputSocket;
    private final OutputSocket outputSocket;
    private final BroadcastSocket broadcastSocket;
    private final EServiceType serviceType;
    private final int listeningPort;
    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;

    public NetworkHandler(EServiceType serviceType, int listeningPort) {
        this.serviceType = serviceType;
        this.listeningPort = listeningPort;

        inputQueue = new LinkedBlockingQueue<>();
        outputQueue = new LinkedBlockingQueue<>();
        broadcastQueue = new LinkedBlockingQueue<>();

        inputSocket = new InputSocket(inputQueue, listeningPort);
        outputSocket = new OutputSocket(outputQueue, listeningPort + 1); // TODO: This +1 will stay I think
        broadcastSocket = new BroadcastSocket(broadcastQueue, listeningPort + 2); // TODO: This +2 will stay I think

        executor = Executors.newFixedThreadPool(2);
        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startSockets() {
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

    public void scheduleMessage(LocalMessage localMessage, int delay) {
        scheduler.schedule(() -> broadcastQueue.add(localMessage), delay, TimeUnit.SECONDS);
    }

    public MSData getMSData() {
        try {
            return new MSData(UUID.randomUUID(), serviceType, InetAddress.getLocalHost().getHostAddress(), listeningPort);
        } catch (UnknownHostException e) {
            log.error("Could not get current service, using \"localhost\" instead", e);
            return new MSData(UUID.randomUUID(), serviceType, "localhost", listeningPort);
        }
    }
}

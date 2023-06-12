package CF.communication;

import CF.sendable.MSData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.sendable.EServiceType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.*;

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

        executor = Executors.newFixedThreadPool(3);
        scheduler = Executors.newScheduledThreadPool(20);
    }

    public void createSockets() {

    }

    public void startSockets() {
        executor.execute(inputSocket);
        executor.execute(outputSocket);
        scheduler.execute(broadcastSocket);
    }

    public void stop() {
        executor.shutdown();
        scheduler.shutdown();
    }

    public void sendMessage(LocalMessage localMessage) {
        outputQueue.add(localMessage);
    }

    public byte[] receiveMessage() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            log.error("Could not receive message", e);
            return null;
        }
    }

    public ScheduledFuture<?> scheduleMessage(LocalMessage localMessage, UUID senderID, int delay, boolean repeating) {
        log.debug("Scheduled repeating: {} message to {}", repeating, localMessage.getReceiverPort());
        ScheduledFuture<?> scheduledTask;
        if (repeating) {
            // 1 is just the initial delay, it is not 0 because not everything may be initialized.
            scheduledTask = scheduler.scheduleAtFixedRate(() -> broadcastQueue.add(localMessage), 1, delay, TimeUnit.SECONDS);
        } else {
            scheduledTask = scheduler.schedule(() -> broadcastQueue.add(localMessage), delay, TimeUnit.SECONDS);
        }
        return scheduledTask;
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

package CF.communication;

import CF.mainPackage.ConfigReader;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * This class handles the network communication for a microservice and provides methods to send and receive messages. It
 * uses two threads to send and receive messages. The input and output queues are blocking queues so that the threads
 * are blocked when there is no message to send or receive.
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
        ConfigReader configReader = new ConfigReader();
        String ipAddress = null;

        switch (serviceType) {
            case ExchangeWorker -> ipAddress = configReader.getProperty("exchangeAddress");
            case Prosumer -> ipAddress = configReader.getProperty("prosumerAddress");
            case Exchange -> ipAddress = configReader.getProperty("exchangeAddress");
            case Storage -> ipAddress = configReader.getProperty("prosumerAddress");
            case Forecast -> ipAddress = configReader.getProperty("forecastAddress");
            default -> {
            }
            // Handle unknown serviceType, if needed
        }

        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress() && address.getHostAddress().indexOf(':') == -1) {
                            log.warn("OpenVPN IP-Adresse: " + address.getHostAddress());
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return new MSData(UUID.randomUUID(), serviceType, ipAddress, listeningPort);
    }
}

package CF.communication;

import CF.mainPackage.ConfigReader;
import CF.sendable.EServiceType;
import CF.sendable.MSData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class handles the network communication for a microservice and provides methods to send and receive messages.
 * It uses two threads to send and receive messages.
 * The input and output queues are blocking queues so that the threads are blocked when there is no message to send or
 * receive, respectively.
 */
public class NetworkHandler {
    private static final Logger log = LogManager.getLogger(NetworkHandler.class);
    private final BlockingQueue<byte[]> inputQueue;
    private final BlockingQueue<LocalMessage> outputQueue;
    private final InputSocket inputSocket;
    private final OutputSocket outputSocket;
    private final EServiceType serviceType;
    private final int listeningPort;
    private final ExecutorService executor;


    /**
     * Constructor for the NetworkHandler. It initializes the input and output queues and the input and output sockets.
     * It also initializes the ExecutorService.
     *
     * @param serviceType   The type of the service.
     * @param listeningPort The port the broker should listen on. The sending port is always the listening port + 1.
     */
    public NetworkHandler(EServiceType serviceType, int listeningPort) {
        this.serviceType = serviceType;
        this.listeningPort = listeningPort;

        inputQueue = new LinkedBlockingQueue<>();
        outputQueue = new LinkedBlockingQueue<>();

        inputSocket = new InputSocket(inputQueue, listeningPort);
        outputSocket = new OutputSocket(outputQueue, listeningPort + 1);

        executor = Executors.newFixedThreadPool(3);
    }

    /**
     * This method starts the input and output sockets. It uses the executor to start the sockets in separate threads.
     */
    public void startSockets() {
        executor.execute(inputSocket);
        executor.execute(outputSocket);
    }

    public void stop() {
        executor.shutdown();
    }

    public void sendMessage(LocalMessage localMessage) {
        outputQueue.add(localMessage);
    }

    /**
     * This message blocks until a message is received.
     *
     * @return the received message as byte array or null if an error occurred.
     */
    public byte[] receiveMessage() {
        try {
            return inputQueue.take();
        } catch (InterruptedException e) {
            log.error("Could not receive message", e);
            return null;
        }
    }

    /**
     * This method creates the currentMSData data of the microservice.
     * It reads the IP address from the config file depending on the serviceType.
     *
     * @return the MSData data of the microservice.
     */
    public MSData getMSData() {
            ConfigReader configReader = new ConfigReader();
            String ipAddress = configReader.getProperty(serviceType+ ".Address");
            return new MSData(UUID.randomUUID(), serviceType, ipAddress, listeningPort);
    }
}

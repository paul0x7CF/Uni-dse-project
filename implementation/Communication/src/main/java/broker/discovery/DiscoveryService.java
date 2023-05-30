package broker.discovery;

import mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static broker.InfoMessageBuilder.createRegisterMessage;

public class DiscoveryService {
    private static final Logger log = LogManager.getLogger(DiscoveryService.class);
    private final ConfigReader configReader;
    private final IScheduleBroker broker;
    private final Map<Integer, Message> messagesToSchedule = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    String broadcastAddress;
    int messageFrequency;
    MSData currentService;


    public DiscoveryService(IScheduleBroker broker) {
        this.broker = broker;

        this.configReader = new ConfigReader();
        this.currentService = broker.getCurrentService();
        this.broadcastAddress = configReader.getProperty("broadcastAddress");
        this.messageFrequency = Integer.parseInt(configReader.getProperty("registerMessageFrequency"));
    }

    public void scheduleDiscovery() {
        int portJumpSize = Integer.parseInt(configReader.getProperty("portJumpSize"));
        int prosumerPort = Integer.parseInt(configReader.getProperty("prosumerPort"));
        int prosumerAmount = Integer.parseInt(configReader.getProperty("prosumerAmount"));
        int storagePort = Integer.parseInt(configReader.getProperty("storagePort"));
        int storageAmount = Integer.parseInt(configReader.getProperty("storageAmount"));
        int exchangePort = Integer.parseInt(configReader.getProperty("exchangePort"));
        int exchangeAmount = Integer.parseInt(configReader.getProperty("exchangeAmount"));
        int solarPort = Integer.parseInt(configReader.getProperty("solarPort"));
        int solarAmount = Integer.parseInt(configReader.getProperty("solarAmount"));
        int consumptionPort = Integer.parseInt(configReader.getProperty("consumptionPort"));
        int consumptionAmount = Integer.parseInt(configReader.getProperty("consumptionAmount"));


        // register prosumer
        for (int i = 0; i < prosumerAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Prosumer || currentService.getPort() != prosumerPort + i)
                scheduleRegisterMessage(prosumerPort + i);
        }

        // register storage
        for (int i = 0; i < storageAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Storage || currentService.getPort() != storagePort + i) {
                scheduleRegisterMessage(storagePort + i);
            }
        }

        // register exchange
        for (int i = 0; i < exchangeAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Exchange || currentService.getPort() != exchangePort + i) {
                scheduleRegisterMessage(exchangePort + i);
            }
        }

        // register solar
        for (int i = 0; i < solarAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Solar || currentService.getPort() != solarPort + i) {
                scheduleRegisterMessage(solarPort + i);
            }
        }

        // register consumption
        for (int i = 0; i < consumptionAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Consumption || currentService.getPort() != consumptionPort + i) {
                scheduleRegisterMessage(consumptionPort + i);
            }
        }

        // Start a single thread that checks and sends register messages periodically
        // this::checkAndSendMessages is a lambda expression for the method checkAndSendMessages
        scheduler.scheduleAtFixedRate(this::checkAndSendMessages, 1, messageFrequency, TimeUnit.SECONDS);
    }

    private void scheduleRegisterMessage(int port) {
        if (broadcastAddress != null) {
            // TODO: address per type from config?
            Message message = createRegisterMessage(currentService, broadcastAddress, port);
            log.trace("Scheduling message to {} on port {}", broadcastAddress, port);

            // Dont send schedule immediately, store in the map instead
            if (currentService.getPort() != port) {
                messagesToSchedule.put(message.getReceiverPort(), message);
            } else {
                // if() can be considered useless as there already is a check on that in the for loops above
                log.debug("Is this if() useless?");
            }
        }
    }

    private void checkAndSendMessages() {
        // Go through all the messages in the map
        for (int port : messagesToSchedule.keySet()) {
            // If the service is already registered, no need to send the message
            if (broker.serviceExists(port)) {
                log.trace("{} already registered at {} not sending register message anymore", port, currentService.getPort());
                messagesToSchedule.remove(port);
            } else {
                Message message = messagesToSchedule.get(port);
                log.trace("{} sending to {}", currentService.getId(), message.getSenderID());
                broker.sendMessage(message);
            }
        }
    }
}
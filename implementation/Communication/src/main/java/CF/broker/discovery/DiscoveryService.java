package CF.broker.discovery;

import CF.broker.InfoMessageBuilder;
import CF.mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;
import CF.sendable.EServiceType;
import CF.sendable.MSData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DiscoveryService implements IMessageSchedulerObserver {
    private static final Logger log = LogManager.getLogger(DiscoveryService.class);
    ConfigReader configReader = new ConfigReader();
    String broadcastAddress;
    MSData currentService;
    private final IScheduleBroker broker;
    private final Map<Integer, Message> messagesToSchedule;

    public DiscoveryService(IScheduleBroker broker) {
        this.broker = broker;
        this.messagesToSchedule = new ConcurrentHashMap<>();
        this.broadcastAddress = configReader.getProperty("broadcastAddress");
        this.currentService = broker.getCurrentService();

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

        // ExchangeWorker does not need to register itself to the other services because
        // they will only talk through the LoadManager.
        if (currentService.getType() != EServiceType.ExchangeWorker) {
            // register prosumer
            for (int i = 0; i < prosumerAmount * portJumpSize; i += portJumpSize) {
                // TODO: maybe &&, not ||
                if (currentService.getType() != EServiceType.Prosumer || currentService.getPort() != prosumerPort + i)
                    addMessageToSchedule(prosumerPort + i);
            }

            // register storage
            for (int i = 0; i < storageAmount * portJumpSize; i += portJumpSize) {
                if (currentService.getType() != EServiceType.Storage || currentService.getPort() != storagePort + i) {
                    addMessageToSchedule(storagePort + i);
                }
            }

            // register solar
            for (int i = 0; i < solarAmount * portJumpSize; i += portJumpSize) {
                if (currentService.getType() != EServiceType.Solar || currentService.getPort() != solarPort + i) {
                    addMessageToSchedule(solarPort + i);
                }
            }

            // register consumption
            for (int i = 0; i < consumptionAmount * portJumpSize; i += portJumpSize) {
                if (currentService.getType() != EServiceType.Consumption || currentService.getPort() != consumptionPort + i) {
                    addMessageToSchedule(consumptionPort + i);
                }
            }
        }

        // register exchange
        for (int i = 0; i < exchangeAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Exchange || currentService.getPort() != exchangePort + i) {
                addMessageToSchedule(exchangePort + i);
            }
        }
    }

    public void scheduleMessages(ScheduledExecutorService scheduler) {
        int messageFrequency = Integer.parseInt(configReader.getProperty("registerMessageFrequency"));
        long delay = (long) (Math.random() * 2) + 1;
        scheduler.scheduleAtFixedRate(this::checkAndSendMessages, delay, messageFrequency, TimeUnit.SECONDS);
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

    private void addMessageToSchedule(int port) {
        messagesToSchedule.put(port, InfoMessageBuilder.createRegisterMessage(currentService, broadcastAddress, port));
    }
}
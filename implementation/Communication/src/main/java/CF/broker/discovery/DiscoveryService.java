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
        String prosumerAddress = configReader.getProperty("prosumerAddress");
        int prosumerPort = Integer.parseInt(configReader.getProperty("prosumerPort"));
        int prosumerAmount = Integer.parseInt(configReader.getProperty("prosumerAmount"));
        String storageAddress = configReader.getProperty("storageAddress");
        int storagePort = Integer.parseInt(configReader.getProperty("storagePort"));
        int storageAmount = Integer.parseInt(configReader.getProperty("storageAmount"));
        String exchangeAddress = configReader.getProperty("exchangeAddress");
        int exchangePort = Integer.parseInt(configReader.getProperty("exchangePort"));
        int exchangeAmount = Integer.parseInt(configReader.getProperty("exchangeAmount"));
        String forecastAddress = configReader.getProperty("forecastAddress");
        int forecastPort = Integer.parseInt(configReader.getProperty("forecastPort"));
        int forecastAmount = Integer.parseInt(configReader.getProperty("forecastAmount"));

        // ExchangeWorker does not need to register itself to the other services because
        // they will only talk through the LoadManager.
        if (currentService.getType() != EServiceType.ExchangeWorker) {
            // register prosumer
            for (int i = 0; i < prosumerAmount * portJumpSize; i += portJumpSize) {
                if (currentService.getType() != EServiceType.Prosumer || currentService.getPort() != prosumerPort + i) {
                    addMessageToSchedule(prosumerAddress, prosumerPort + i);
                }
            }

            // register storage
            for (int i = 0; i < storageAmount * portJumpSize; i += portJumpSize) {
                if (currentService.getType() != EServiceType.Storage || currentService.getPort() != storagePort + i) {
                    addMessageToSchedule(storageAddress, storagePort + i);
                }
            }

            // register forecast
            for (int i = 0; i < forecastAmount * portJumpSize; i += portJumpSize) {
                if (currentService.getType() != EServiceType.Forecast || currentService.getPort() != forecastPort + i) {
                    addMessageToSchedule(exchangeAddress, forecastPort + i);
                }
            }
        }

        // register exchange
        // ExchangeWorker only registers to the Exchange
        for (int i = 0; i < exchangeAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Exchange || currentService.getPort() != exchangePort + i) {
                addMessageToSchedule(forecastAddress, exchangePort + i);
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
                log.trace("{} sending to {}", currentService.getId(), message.getReceiverID());
                broker.sendMessage(message);
            }
        }
    }

    private void addMessageToSchedule(String address, int port) {
        messagesToSchedule.put(port, InfoMessageBuilder.createRegisterMessage(currentService, address, port));
    }
}
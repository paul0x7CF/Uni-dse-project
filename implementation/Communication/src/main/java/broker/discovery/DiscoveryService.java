package broker.discovery;

import broker.Marshaller;
import communication.LocalMessage;
import mainPackage.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import static broker.InfoMessageBuilder.createRegisterMessage;

public class DiscoveryService {
    private static final Logger log = LogManager.getLogger(DiscoveryService.class);
    private final ConfigReader configReader;
    String broadcastAddress;
    int messageFrequency;
    private IScheduleBroker broker;

    public DiscoveryService(IScheduleBroker broker) {
        this.broker = broker;

        configReader = new ConfigReader();
        broadcastAddress = configReader.getProperty("broadcastAddress");
        messageFrequency = Integer.parseInt(configReader.getProperty("registerMessageFrequency"));
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

        MSData currentService = broker.getCurrentService();

        // create prosumer
        for (int i = 0; i < prosumerAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Prosumer || currentService.getPort() != prosumerPort + i)
                sendRegisterMessage(prosumerPort + i);
        }

        // create storage
        for (int i = 0; i < storageAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Storage || currentService.getPort() != storagePort + i) {
                sendRegisterMessage(storagePort + i);
            }
        }

        // create exchange
        for (int i = 0; i < exchangeAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Exchange || currentService.getPort() != exchangePort + i) {
                sendRegisterMessage(exchangePort + i);
            }
        }

        // create solar forecast
        for (int i = 0; i < solarAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Solar || currentService.getPort() != solarPort + i) {
                sendRegisterMessage(solarPort + i);
            }
        }

        // create consumption forecast
        for (int i = 0; i < consumptionAmount * portJumpSize; i += portJumpSize) {
            if (currentService.getType() != EServiceType.Consumption || currentService.getPort() != consumptionPort + i) {
                sendRegisterMessage(consumptionPort + i);
            }
        }
    }

    private void sendRegisterMessage(int port) {
        if (broadcastAddress != null) {
            // TODO: address per type from config?
            Message message = createRegisterMessage(broker.getCurrentService(), broadcastAddress, port);
            LocalMessage localMessage = new LocalMessage(Marshaller.marshal(message), broadcastAddress, port);
            log.trace("Scheduling message to {} on port {}", localMessage.getReceiverAddress(), localMessage.getReceiverPort());
            // TODO: check if already registered and only send if not?
            //  could use thread
            broker.scheduleMessage(localMessage, messageFrequency);
        }
    }
}

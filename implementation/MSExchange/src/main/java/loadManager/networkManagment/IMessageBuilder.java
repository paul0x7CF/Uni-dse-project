package loadManager.networkManagment;

import protocol.Message;
import protocol.MessageFactory;
import sendable.Bid;
import sendable.EServiceType;
import sendable.MSData;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public interface IMessageBuilder {

    private static MSData buildMSData() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("../config.properties");
            properties.load(configFile);
            configFile.close();

            int PORT = Integer.parseInt(properties.getProperty("loadmanager.port"));
            String ADDRESS = properties.getProperty("loadmanager.address");
            EServiceType SERVICE_TYPE = EServiceType.valueOf(properties.getProperty("loadmanager.serviceType"));
            UUID id = UUID.fromString(properties.getProperty("loadmanager.id"));
            return new MSData(id, SERVICE_TYPE, ADDRESS, PORT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message buildMessagePriceIsToLow(Bid bid, double averagePrice) {
        MessageFactory messageFactory = new MessageFactory();
        return null;
        //TODO: implement
    }

    public Message buildTimeSlotMessage();


}

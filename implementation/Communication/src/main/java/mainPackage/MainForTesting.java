package mainPackage;

import broker.Broker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageBuilder;

public class MainForTesting {
    private static final Logger logger = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        logger.info("Starting MainForTesting");

        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setCategory(ECategory.Info, "Register");
        messageBuilder.setReceiverPort(8080);

        Message message = messageBuilder.build();



        // broker.send(message);
    }
}

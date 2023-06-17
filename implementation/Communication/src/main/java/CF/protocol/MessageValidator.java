package CF.protocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class will validate a message.
 */
public class MessageValidator {
    private static final Logger log = LogManager.getLogger(MessageValidator.class);

    public static boolean validateMessage(Message message) {
        String category = message.getCategory();
        ECategory mainCategory = ECategory.valueOf(category.split(";")[0]);
        String subCategory = category.split(";")[1];

        if (message.getCategory() == null) {
            log.error("Category is null");
            return false;
        }
        if (mainCategory.toString().split(";").length > 1) {
            log.error("mainCategory has more than one semicolon");
            return false;
        }
        if (subCategory.split(";").length > 1) {
            log.error("subCategory has more than one semicolon");
            return false;
        }
        if (message.getSenderID() == null) {
            log.error("SenderID is null");
            return false;
        }
        if (message.getSenderAddress() == null) {
            log.error("SenderAddress is null");
            return false;
        }
        if (message.getSenderPort() == 0) {
            log.error("SenderPort is 0");
            return false;
        }
        if (message.getPayload() == null || message.getPayload().equals("")) {
            log.error("Payload is {}", message.getPayload());
            return false;
        }

        return true;
    }

    public static boolean validateMessageFactory(MessageFactory messageFactory) {
        return false;
    }
}

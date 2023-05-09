package protocol;

import sendable.ISendable;

public class MessageValidator {
    public static boolean validateMessage(Message message) {
        ECategory category = message.getMainCategory();
        String subCategory = message.getSubCategory();

        boolean validity = false;

        return validity;
    }

    public static boolean validateMessageFactory(MessageFactory messageFactory) {


        boolean validity = false;

        return validity;
    }
}

package messageHandling;

import exceptions.MessageProcessingException;
import protocol.Message;

public class InfoMessageHandler implements IMessageHandler {

    @Override
    public void handleMessage(Message message) throws MessageProcessingException {
        String subcategory = message.getSubCategory();
        if (subcategory.contains(";")) {
            throw new MessageProcessingException("Subcategory has another subcategory: " + subcategory);
        }
        // possible: Ping, Register, Unregister, Ack, Error
        switch(subcategory) {
            case "Ping":
                // Ping is response to Register
                break;
            case "Register":
                // Register is first message with MSData as Payload
                break;
            case "Unregister":
                // Unregister is last message with MSData as payload
                break;
            case "Ack":
                // Ack has AckInfo as payload
                break;
            case "Error":
                // Error has Error as Payload
                break;
            default:
                throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
        }
    }
}

package broker;

import protocol.ECategory;
import protocol.Message;
import protocol.MessageBuilder;
import sendable.AckInfo;
import sendable.ErrorInfo;
import sendable.MSData;

import java.util.UUID;

public class InfoMessageCreator {
    private MSData sender;

    public InfoMessageCreator(MSData sender) {
        this.sender = sender;
    }

    public Message createPingMessage(MSData receiver) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setCategory(ECategory.Info, "Ping").
            setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort());
        return messageBuilder.build();
    }

    public Message createRegisterMessage(MSData sender) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setCategory(ECategory.Info, "Register").
            setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).

            // TODO: Send to Broadcast address
            //setReceiverAddress().
            //setReceiverPort().
            setPayload(sender);
        return messageBuilder.build();
    }

    public Message createUnregisterMessage(MSData sender, MSData receiver) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setCategory(ECategory.Info, "Unregister").
            setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort()).
            setPayload(sender);
        return messageBuilder.build();
    }

    public Message createAckMessage(MSData sender, MSData receiver, UUID messageID) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setCategory(ECategory.Info, "Ack").
            setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(receiver.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort()).
            setPayload(new AckInfo(messageID));
        return messageBuilder.build();
    }

    public Message createErrorMessage(MSData sender, MSData receiver, String errorName, String errorMessage) {
        MessageBuilder messageBuilder = new MessageBuilder();
        messageBuilder.setCategory(ECategory.Info, "Error").
            setSenderID(sender.getId()).
            setSenderAddress(sender.getAddress()).
            setSenderPort(sender.getPort()).
            setReceiverID(receiver.getId()).
            setReceiverAddress(receiver.getAddress()).
            setReceiverPort(receiver.getPort()).
            setPayload(new ErrorInfo(errorName, errorMessage));
        return messageBuilder.build();
    }
}

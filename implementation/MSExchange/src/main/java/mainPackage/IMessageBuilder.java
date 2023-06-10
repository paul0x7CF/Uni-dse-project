package mainPackage;

import CF.protocol.MessageFactory;
import CF.sendable.MSData;

public interface IMessageBuilder {
    static MessageFactory senderAndReceiverTemplate(MSData reciever, MSData sender) {
        MessageFactory messageFactory = new MessageFactory();
        return messageFactory.setSenderID(sender.getId())
                .setSenderAddress(sender.getAddress())
                .setSenderPort(sender.getPort())
                .setReceiverID(reciever.getId())
                .setReceiverAddress(reciever.getAddress())
                .setReceiverPort(reciever.getPort());
    }
}

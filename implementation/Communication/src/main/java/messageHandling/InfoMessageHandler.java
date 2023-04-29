package messageHandling;

import broker.IServiceBroker;
import broker.InfoMessageCreator;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import protocol.Message;
import sendable.AckInfo;
import sendable.ErrorInfo;
import sendable.ISendable;
import sendable.MSData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InfoMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(InfoMessageHandler.class);
    IServiceBroker broker;
    MSData currentService;

    public InfoMessageHandler(IServiceBroker broker) {
        this.broker = broker;
        this.currentService = broker.getCurrentService();
    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        String subcategory = message.getSubCategory();
        if (subcategory.contains(";")) {
            throw new MessageProcessingException("Subcategory has another subcategory: " + subcategory);
        }
        // possible: Ping, Register, Unregister, Ack, Error
        switch(subcategory) {
            case "Ping":
                // Ping is response to Register response
                ISendable ping = message.getSendable(MSData.class);
                if (ping == null) {
                    throw new MessageProcessingException("Payload is null");
                }
                if (ping instanceof MSData) {
                    broker.registerService((MSData) ping);
                } else {
                    throw new MessageProcessingException("Payload is not of type MSData");
                }
                break;
            case "Register":
                // Register is first message with MSData as Payload
                ISendable register = message.getSendable(MSData.class);
                if (register == null) {
                    throw new MessageProcessingException("Payload is null");
                }
                if (register instanceof MSData from) {
                    // TODO: maybe "ping" is not needed because if the service is already present in the registry, no response is sent besides an Ack.
                    broker.registerService(from);
                    // TODO: is currentService correct for other services?
                    broker.sendMessage(InfoMessageCreator.createPingMessage(currentService, from));
                    System.out.println("Register received");
                } else {
                    throw new MessageProcessingException("Payload is not of type MSData");
                }
                break;
            case "Unregister":
                ISendable unregister = message.getSendable(MSData.class);
                if (unregister == null) {
                    throw new MessageProcessingException("Payload is null");
                }
                if (unregister instanceof MSData) {
                    broker.unregisterService((MSData) unregister);
                } else {
                    throw new MessageProcessingException("Payload is not of type MSData");
                }
                break;
            case "Ack":
                ISendable ack = message.getSendable(AckInfo.class);
                if (ack == null) {
                    throw new MessageProcessingException("Payload is null");
                }
                if (ack instanceof AckInfo) {
                    broker.ackReceived((AckInfo) ack);
                } else {
                    throw new MessageProcessingException("Payload is not of type AckInfo");
                }
                break;
            case "Error":
                // Error has Error as Payload
                ISendable error = message.getSendable(ErrorInfo.class);
                if (error == null) {
                    throw new MessageProcessingException("Payload is null");
                }
                if (error instanceof ErrorInfo from) {
                    logger.error("Received RemoteError");
                    //TODO: How to handle this in each service?
                    throw new RemoteException(from.getName());
                } else {
                    throw new MessageProcessingException("Payload is not of type ErrorInfo");
                }
            default:
                throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
        }
    }
}

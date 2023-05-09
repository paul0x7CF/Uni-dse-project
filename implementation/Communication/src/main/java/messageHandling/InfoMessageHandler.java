package messageHandling;

import broker.IServiceBroker;
import broker.InfoMessageBuilder;
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
    private final IServiceBroker broker;
    private final MSData currentService;

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
        switch(subcategory) {
            case "Ping":
                handlePing(message);
                break;
            case "Register":
                handleRegister(message);
                break;
            case "Unregister":
                handleUnregister(message);
                break;
            case "Ack":
                handleAck(message);
                break;
            case "Error":
                handleError(message);
            default:
                throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
        }

        logger.trace("{} Message processed" + message.getCategory());
    }

    private void handlePing(Message message) throws MessageProcessingException {
        // Ping is response to Register request
        ISendable ping = message.getSendable(MSData.class);
        if (ping == null) {
            throw new MessageProcessingException("Payload is null");
        }
        if (ping instanceof MSData) {
            broker.registerService((MSData) ping);
        } else {
            throw new MessageProcessingException("Payload is not of type MSData");
        }
    }

    private void handleRegister(Message message) throws MessageProcessingException {
        // Register is first message with MSData as Payload when broker is started
        ISendable register = message.getSendable(MSData.class);
        if (register == null) {
            throw new MessageProcessingException("Payload is null");
        }
        if (register instanceof MSData from) {
            broker.registerService(from);
            // TODO: is currentService correct for other services?
            broker.sendMessage(InfoMessageBuilder.createPingMessage(currentService, from));
        } else {
            throw new MessageProcessingException("Payload is not of type MSData");
        }
    }

    private void handleUnregister(Message message) throws MessageProcessingException {
        // Unregister is sent when broker is stopped
        ISendable unregister = message.getSendable(MSData.class);
        if (unregister == null) {
            throw new MessageProcessingException("Payload is null");
        }
        if (unregister instanceof MSData) {
            broker.unregisterService((MSData) unregister);
        } else {
            throw new MessageProcessingException("Payload is not of type MSData");
        }
    }

    private void handleAck(Message message) throws MessageProcessingException {
        ISendable ack = message.getSendable(AckInfo.class);
        if (ack == null) {
            throw new MessageProcessingException("Payload is null");
        }
        if (ack instanceof AckInfo) {
            broker.ackReceived((AckInfo) ack);
        } else {
            throw new MessageProcessingException("Payload is not of type AckInfo");
        }
    }

    private void handleError(Message message) throws MessageProcessingException, RemoteException {
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
    }
}

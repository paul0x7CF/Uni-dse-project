package messageHandling;

import broker.IServiceBroker;
import broker.InfoMessageBuilder;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import protocol.Message;
import sendable.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class InfoMessageHandler implements IMessageHandler {
    private static final Logger log = LogManager.getLogger(InfoMessageHandler.class);
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
        switch (subcategory) {
            case "Ping" -> handlePing(message);
            case "Register" -> handleRegister(message);
            case "Unregister" -> handleUnregister(message);
            case "Ack" -> handleAck(message);
            case "Error" -> handleError(message);
            case "Sync" -> handleSync(message);
            default -> throw new MessageProcessingException("Unknown message subCategory: " + message.getSubCategory());
        }

        log.trace("{} Message processed", message.getCategory());
    }

    private void handlePing(Message message) throws MessageProcessingException {
        // Ping is response to Register request
        ISendable otherMS = message.getSendable(MSData.class);
        if (otherMS == null) {
            log.error("Received Ping with null payload");
            throw new MessageProcessingException("Payload is null");
        }
        if (otherMS instanceof MSData) {
            broker.registerService((MSData) otherMS);
        } else {
            log.error("Received Ping with wrong payload");
            throw new MessageProcessingException("Payload is not of type MSData");
        }
    }

    private void handleRegister(Message message) throws MessageProcessingException {
        // Register is first message with MSData as Payload when broker is started
        ISendable register = message.getSendable(MSData.class);
        if (register == null) {
            log.error("Received Register with null payload");
            throw new MessageProcessingException("Payload is null");
        }
        if (register instanceof MSData otherMS) {
            broker.registerService(otherMS);
            broker.sendMessage(InfoMessageBuilder.createPingMessage(currentService, otherMS));
        } else {
            log.error("Received Register with wrong payload");
            throw new MessageProcessingException("Payload is not of type MSData");
        }
    }

    private void handleUnregister(Message message) throws MessageProcessingException {
        // Unregister is sent when broker is stopped
        ISendable otherMS = message.getSendable(MSData.class);
        if (otherMS == null) {
            log.error("Received Unregister with null payload");
            throw new MessageProcessingException("Payload is null");
        }
        if (otherMS instanceof MSData) {
            broker.unregisterService((MSData) otherMS);
        } else {
            log.error("Received Unregister with wrong payload");
            throw new MessageProcessingException("Payload is not of type MSData");
        }
    }

    private void handleAck(Message message) throws MessageProcessingException {
        ISendable ack = message.getSendable(AckInfo.class);
        if (ack == null) {
            log.error("Received Ack with null payload");
            throw new MessageProcessingException("Payload is null");
        }
        if (ack instanceof AckInfo) {
            broker.ackReceived((AckInfo) ack);
        } else {
            log.error("Received Ack with wrong payload");
            throw new MessageProcessingException("Payload is not of type AckInfo");
        }
    }

    private void handleError(Message message) throws MessageProcessingException, RemoteException {
        // Error has Error as Payload
        ISendable error = message.getSendable(ErrorInfo.class);
        if (error == null) {
            log.error("Received Error with null payload");
            throw new MessageProcessingException("Payload is null");
        }
        if (error instanceof ErrorInfo errorInfo) {
            // TODO: How to handle this in each service?
            if (!Objects.equals(errorInfo.getName(), "test")) {
                log.error("Received remote error: {}", errorInfo.getMessage());
                throw new RemoteException(errorInfo.getName());
            } else {
                log.error("Received test error: {}", errorInfo.getMessage());
            }
        } else {
            log.error("Received Error with wrong payload");
            throw new MessageProcessingException("Payload is not of type ErrorInfo");
        }
    }

    private void handleSync(Message message) throws MessageProcessingException {
        // Sync has MSDataList as payload
        ISendable sync = message.getSendable(MSDataList.class);
        if (sync == null) {
            log.error("Received Sync with null payload");
            return;
        }
        if (sync instanceof MSDataList msToSync) {
            if (msToSync.isEmpty()) {
                MSDataList allServices = new MSDataList(currentService, broker.getServices());
                log.trace("{} sending Sync with {} services", currentService.getPort(), allServices.getMsDataList().size());
                Message response = InfoMessageBuilder.createSyncMessage(currentService, msToSync.getSender(), allServices);
                broker.sendMessage(response);
            } else {
                log.trace("{} received Sync with {} services", currentService.getPort(), msToSync.getMsDataList().size());
                for (MSData msData : msToSync.getMsDataList()) {
                    broker.registerService(msData);
                }
            }
        } else {
            log.error("Received Sync with wrong payload");
            throw new MessageProcessingException("Payload is not of type MSDataList");
        }
    }
}

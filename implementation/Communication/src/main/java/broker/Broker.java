package broker;

import communication.LocalMessage;
import communication.NetworkHandler;
import exceptions.MessageProcessingException;
import messageHandling.IMessageHandler;
import messageHandling.MessageHandler;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

public class Broker implements IBroker {
    private AckHandler ackHandler;
    private IMessageHandler messageHandler;
    private ServiceRegistry serviceRegistry;
    private NetworkHandler networkHandler;
    private InfoMessageCreator messageCreator;

    public Broker(EServiceType serviceType, int listeningPort) {
        ackHandler = new AckHandler(this);
        messageHandler = new MessageHandler();
        networkHandler = new NetworkHandler(serviceType, listeningPort);
    }

    public void start() throws UnknownHostException {
        networkHandler.start();
        serviceRegistry = new ServiceRegistry(networkHandler.getMSData());
        messageCreator = new InfoMessageCreator(networkHandler.getMSData());

        Message registerMessage = messageCreator.createRegisterMessage(networkHandler.getMSData());
        try {
            networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(registerMessage),
                    registerMessage.getReceiverAddress(),
                    registerMessage.getReceiverPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        networkHandler.stop();
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        networkHandler.sendMessage(new LocalMessage(Marshaller.marshal(message),
                message.getReceiverAddress(),
                message.getReceiverPort()));
    }

    private void receiveMessage() throws InterruptedException, IOException, ClassNotFoundException, MessageProcessingException {
        while (true) {
            Message message = Marshaller.unmarshal(networkHandler.receiveMessage());
            messageHandler.handleMessage(message);
        }
    }

    public void registerService(MSData msData) {
        serviceRegistry.registerService(msData);
    }

    public void unregisterService(MSData msData) {
        serviceRegistry.unregisterService(msData);
    }

    public MSData findService(UUID serviceId) {
        return serviceRegistry.findService(serviceId);
    }

    public List<MSData> getAvailableService() {
        return serviceRegistry.getAvailableServices();
    }

    public List<MSData> getServicesByType(EServiceType serviceType) {
        return serviceRegistry.getServicesByType(serviceType);
    }
}


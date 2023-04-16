package loadManager;

import loadManager.networkCommunication.MessageHandler;

import java.util.List;

public class LoadManager {
    private List<ExchangeServiceInformation> exchangeServicesInformation;
    private MessageHandler messageHandler;

    public LoadManager() {
        //create Exchange instance
        messageHandler= new MessageHandler();
    }
}

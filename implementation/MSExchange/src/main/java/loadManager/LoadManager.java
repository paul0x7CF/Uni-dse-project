package loadManager;

import loadManager.networkCommunication.MessageHandler;

import java.util.List;

public class LoadManager {
    private List<ExchangeServiceInformation> exchangeServicesInformation;

    private MessageHandler messageHandler = new MessageHandler();

    public LoadManager() {
        //create Exchange instance
    }


}

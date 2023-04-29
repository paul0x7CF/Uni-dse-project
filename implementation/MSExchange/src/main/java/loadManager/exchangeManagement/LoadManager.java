package loadManager.exchangeManagement;

import java.util.List;
import java.util.UUID;

public class LoadManager {

    private List<ExchangeServiceInformation> exchangeServicesInformation;


    public void addExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        // Adds a new MicroserviceInformation object to the list.
    }

    public void removeExchangeServiceInformation(ExchangeServiceInformation exchangeServiceInformation) {
        // Removes the MicroserviceInformation object with the given ID from the list.
    }


    public void setExchangeAtCapacity(UUID exchangeID) {
    }

    public ExchangeServiceInformation getFreeExchange() {
        return null;
    }

    public void duplicateExchange() {
    }
}

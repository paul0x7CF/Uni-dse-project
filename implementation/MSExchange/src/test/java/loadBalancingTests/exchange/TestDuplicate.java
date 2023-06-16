package loadBalancingTests.exchange;

import CF.exceptions.InvalidMessageException;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import loadManager.exchangeManagement.LoadManager;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;

public class TestDuplicate {
    @Test
    public void testDuplicate() {
        LoadManager loadManager = new LoadManager();
        UUID exchangeID = UUID.randomUUID();
        try {
            loadManager.addExchangeServiceInformation(new ExchangeServiceInformation(exchangeID));
        } catch (InvalidMessageException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertDoesNotThrow(() -> loadManager.setExchangeCapacity(exchangeID));

    }
}

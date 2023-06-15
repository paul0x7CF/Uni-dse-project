package loadBalancingTests.exchange;

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
        loadManager.addExchangeServiceInformation(new ExchangeServiceInformation(exchangeID));

        Assertions.assertDoesNotThrow(() -> loadManager.setExchangeAtCapacity(exchangeID));

    }
}

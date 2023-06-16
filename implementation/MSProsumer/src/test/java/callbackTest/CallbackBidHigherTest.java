package callbackTest;

import MSP.Communication.Communication;
import MSP.Communication.callback.CallbackBidHigher;
import MSP.Data.EProsumerType;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import org.junit.Test;

import java.lang.reflect.Field;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class CallbackBidHigherTest {

    @Test
    public void testActBidHigherQuestion() throws NoSuchFieldException, IllegalAccessException {
        ConsumptionBuilding prosumerTest = new ConsumptionBuilding(EProsumerType.CONSUMPTION_BUILDING, 999, 6000);

        Field communicationField = ConsumptionBuilding.class.getDeclaredField("communicator");
        communicationField.setAccessible(true); // Zugriff auf private Felder erlauben

        Communication communication = (Communication) communicationField.get(prosumerTest);

        Field callbackField = Communication.class.getDeclaredField("callbackOnBidHigher");
        callbackField.setAccessible(true); // Zugriff auf privates Feld erlauben

        CallbackBidHigher callback = prosumerTest.actBidHigherQuestion();

        // Überprüfen, ob das zurückgegebene CallbackBidHigher-Objekt nicht null ist
        assertNotNull(callback);
        assertNotNull(prosumerTest);
        assertNotNull(callbackField);
    }

    /*@Test
    public void testSetCallbackBidHigher() {
        ConsumptionBuilding building = new ConsumptionBuilding();
        CallbackBidHigher callbackTest = mock(CallbackBidHigher.class);

        building.actBidHigherQuestion(callbackTest);

        // Überprüfen, ob das CallbackBidHigher-Objekt korrekt gesetzt wurde
        // Check if the callback is set on the Communication
        assertEquals(callbackTest, building.getCallbackBidHigher());
    }*/
}

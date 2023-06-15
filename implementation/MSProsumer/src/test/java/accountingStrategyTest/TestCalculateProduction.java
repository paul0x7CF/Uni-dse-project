package accountingStrategyTest;

import CF.sendable.SolarRequest;
import CF.sendable.TimeSlot;
import MSP.Communication.Communication;
import MSP.Communication.polling.PollForecast;
import MSP.Communication.polling.PollProductionForecast;
import MSP.Data.EProducerType;
import MSP.Data.IProsumerDevice;
import MSP.Data.Producer;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Logic.AccountingStrategy.CalcProduction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.*;

public class TestCalculateProduction {

    @Mock
    Communication comMock;


    @Before
    public void setUP() {
        comMock = mock(Communication.class);
    }

    @Test
    public void calculateAcounting_WithProducers_ReturnsPollForecast() throws DeviceNotSupportedException {
        // Arrange
        List<IProsumerDevice> devices = new ArrayList<>();
        Producer producer1 = new Producer(EProducerType.MONOCRYSTALLINE_TYPE_x1);
        Producer producer2 = new Producer(EProducerType.MONOCRYSTALLINE_TYPE_x2);
        devices.add(producer1);
        devices.add(producer2);

        UUID fixedTimeSlotID = UUID.fromString("77b82e01-b5b0-4ab9-b7cf-51277214f4a8");
        TimeSlot timeSlot = new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(1), fixedTimeSlotID);
        CalcProduction calcProduction = new CalcProduction(comMock);

        // Mock
        PollProductionForecast expectedForecast = new PollProductionForecast();
        when(comMock.sendProductionRequestMessage(any(SolarRequest.class))).thenReturn(expectedForecast);

        // Act
        PollForecast result = calcProduction.calculateAccounting(devices, timeSlot);

        // Assert
        verify(comMock, times(1)).sendProductionRequestMessage(any());
    }
}

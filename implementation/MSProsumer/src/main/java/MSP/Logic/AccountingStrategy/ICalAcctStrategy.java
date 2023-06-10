package MSP.Logic.AccountingStrategy;

import MSP.Communication.PollForecast;
import MSP.Data.IProsumerDevice;
import MSP.Exceptions.DeviceNotSupportedException;

import java.util.List;
import java.util.UUID;

public interface ICalAcctStrategy {

    public PollForecast calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException;

}

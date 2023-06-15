package MSP.Logic.AccountingStrategy;

import CF.sendable.TimeSlot;
import MSP.Communication.polling.PollForecast;
import MSP.Data.IProsumerDevice;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.ServiceNotFoundException;

import java.util.List;

public interface ICalAcctStrategy {

    public PollForecast calculateAccounting(List<IProsumerDevice> devices, TimeSlot timeSlotId) throws DeviceNotSupportedException, ServiceNotFoundException;

}

package Logic.AccountingStrategy;

import Communication.PollForecast;
import Data.IProsumerDevice;
import Exceptions.DeviceNotSupportedException;

import java.util.List;
import java.util.UUID;

public interface ICalAcctStrategy {

    public PollForecast calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException;

}

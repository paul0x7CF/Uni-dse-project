package Logic.AccountingStrategy;

import Data.IProsumerDevice;
import Exceptions.DeviceNotSupportedException;

import java.util.List;
import java.util.UUID;

public interface ICalAcctStrategy {

    public double calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException;

}

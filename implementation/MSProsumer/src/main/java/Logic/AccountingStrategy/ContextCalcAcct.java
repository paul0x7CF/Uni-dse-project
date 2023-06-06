package Logic.AccountingStrategy;

import Data.IProsumerDevice;
import Exceptions.DeviceNotSupportedException;

import java.util.List;
import java.util.UUID;

public class ContextCalcAcct {
    private ICalAcctStrategy strategy;

    public void setCalcAcctAStrategy(ICalAcctStrategy concreteStrategy) {
        this.strategy = concreteStrategy;
    }

    public double calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException {
        return this.strategy.calculateAccounting(devices, timeSlotId);

    }
}

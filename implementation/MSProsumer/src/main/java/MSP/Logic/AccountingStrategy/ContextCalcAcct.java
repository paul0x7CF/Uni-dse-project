package MSP.Logic.AccountingStrategy;

import CF.sendable.TimeSlot;
import MSP.Communication.polling.PollForecast;
import MSP.Data.IProsumerDevice;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.UndefinedStrategyException;

import java.util.List;
import java.util.Optional;

public class ContextCalcAcct {
    private Optional<ICalAcctStrategy> strategy = Optional.empty();

    public void setCalcAcctAStrategy(ICalAcctStrategy concreteStrategy) {
        this.strategy = Optional.of(concreteStrategy);
    }

    public PollForecast calculateAccounting(List<IProsumerDevice> devices, TimeSlot timeSlotId) throws DeviceNotSupportedException, UndefinedStrategyException {
        if(this.strategy.isEmpty())
            throw new UndefinedStrategyException();
        return this.strategy.get().calculateAccounting(devices, timeSlotId);

    }
}

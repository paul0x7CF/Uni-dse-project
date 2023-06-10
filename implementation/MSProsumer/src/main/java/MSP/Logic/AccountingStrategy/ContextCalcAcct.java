package MSP.Logic.AccountingStrategy;

import MSP.Communication.PollForecast;
import MSP.Data.IProsumerDevice;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.UndefinedStrategyException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ContextCalcAcct {
    private Optional<ICalAcctStrategy> strategy = Optional.empty();

    public void setCalcAcctAStrategy(ICalAcctStrategy concreteStrategy) {
        this.strategy = Optional.of(concreteStrategy);
    }

    public PollForecast calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException, UndefinedStrategyException {
        if(this.strategy.isEmpty())
            throw new UndefinedStrategyException();
        return this.strategy.get().calculateAccounting(devices, timeSlotId);

    }
}

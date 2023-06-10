package Logic.AccountingStrategy;

import Communication.PollForecast;
import Data.IProsumerDevice;
import Exceptions.DeviceNotSupportedException;
import Exceptions.UndefinedStrategyException;

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
        this.strategy.get().calculateAccounting(devices, timeSlotId);
        // TODO: @Paul define return type
        return null;

    }
}

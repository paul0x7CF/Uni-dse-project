package Logic.AccountingStrategy;

import Data.IProsumerDevice;

import java.util.List;

public class ContextCalcAcct {
    private ICalAcctStrategy strategy;

    public void setCalcAcctAStrategy(ICalAcctStrategy concreteStrategy) {
        this.strategy = concreteStrategy;
    }

    public double calculateAccounting(List<IProsumerDevice> devices) {
        return this.strategy.calculateAccounting(devices);
    }
}

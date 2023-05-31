package Logic.AccountingStrategy;

import Data.IProsumerDevice;

import java.util.List;

public interface ICalAcctStrategy {

    public double calculateAccounting(List<IProsumerDevice> devices);

}

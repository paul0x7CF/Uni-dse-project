package Logic.AccountingStrategy;

import Communication.Communication;
import Data.IProsumerDevice;
import sendable.EServiceType;

import java.util.List;

public class CalcConsumption implements ICalAcctStrategy {

    Communication comConsumption = new Communication(EServiceType.Consumption);

    @Override
    public double calculateAccounting(List<IProsumerDevice> devices) {

        return 0;

    }
}

package Logic.AccountingStrategy;

import Communication.Communication;
import Data.IProsumerDevice;
import sendable.EServiceType;

import java.util.List;

public class CalcConsumption implements ICalAcctStrategy {

    private Communication communication;

    public CalcConsumption(Communication communication) {
        this.communication = communication;
    }

    @Override
    public double calculateAccounting(List<IProsumerDevice> devices) {

        return 0;

    }
}

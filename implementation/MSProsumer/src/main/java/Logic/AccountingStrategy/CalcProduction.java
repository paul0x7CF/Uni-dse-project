package Logic.AccountingStrategy;

import Communication.Communication;
import Data.IProsumerDevice;

import java.util.List;

public class CalcProduction implements ICalAcctStrategy{

    private Communication communication;
    public CalcProduction(Communication communication) {
        this.communication = communication;
    }
    @Override
    public double calculateAccounting(List<IProsumerDevice> devices) {

        return 0;

    }
}

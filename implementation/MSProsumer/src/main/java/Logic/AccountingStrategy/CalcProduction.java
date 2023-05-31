package Logic.AccountingStrategy;

import Communication.Communication;
import Data.IProsumerDevice;
import sendable.EServiceType;

import java.util.List;

public class CalcProduction implements ICalAcctStrategy{

    private Communication comProduction = new Communication(EServiceType.Solar);
    @Override
    public double calculateAccounting(List<IProsumerDevice> devices) {

        return 0;

    }
}

package Logic.AccountingStrategy;

import Communication.Communication;
import Data.Consumer;
import Data.IProsumerDevice;
import Data.Producer;

import java.util.List;

public class CalcProduction implements ICalAcctStrategy{

    private Communication communication;
    public CalcProduction(Communication communication) {
        this.communication = communication;
    }
    @Override
    public double calculateAccounting(List<IProsumerDevice> devices) {

        devices.forEach(device ->{
            if(device.getDevice() instanceof Producer){
                Producer producer = (Producer) device.getDevice();

            }
        });


        return 0;

    }
}

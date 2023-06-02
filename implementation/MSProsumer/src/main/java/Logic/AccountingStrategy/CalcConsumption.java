package Logic.AccountingStrategy;

import Communication.Communication;
import Data.Consumer;
import Data.IProsumerDevice;
import sendable.EServiceType;

import java.util.HashMap;
import java.util.List;

public class CalcConsumption implements ICalAcctStrategy {

    private Communication communication;

    public CalcConsumption(Communication communication) {
        this.communication = communication;
    }

    @Override
    public double calculateAccounting(List<IProsumerDevice> devices) {
        HashMap<String, Double> consumptionMap = new HashMap<>();
        devices.forEach(device ->{
            if(device.getDevice() instanceof Consumer){
                Consumer consumer = (Consumer) device.getDevice();
                consumptionMap.put(consumer.getConsumerType().toString(), consumer.getAverageConsumption());
            }
        });



        return 0;

    }
}

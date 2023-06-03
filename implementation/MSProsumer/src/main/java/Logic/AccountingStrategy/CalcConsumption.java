package Logic.AccountingStrategy;

import Communication.Communication;
import Data.Consumer;
import Data.IProsumerDevice;
import sendable.ConsumptionRequest;
import sendable.EServiceType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CalcConsumption implements ICalAcctStrategy {

    private Communication communication;

    public CalcConsumption(Communication communication) {
        this.communication = communication;
    }

    @Override
    public double calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) {

        HashMap<String, Double> consumptionMap = getConsumptionMap(devices);
        ConsumptionRequest request = new ConsumptionRequest(consumptionMap, timeSlotId);


        return 0;

    }

    private HashMap<String, Double> getConsumptionMap(List<IProsumerDevice> devices) {
        HashMap<String, Double> consumptionMap = new HashMap<>();
        devices.forEach(device ->{
            if(device.getDevice() instanceof Consumer consumer){
                consumptionMap.put(consumer.getConsumerType().toString(), consumer.getAverageConsumption());
            }
        });
        return consumptionMap;
    }
}

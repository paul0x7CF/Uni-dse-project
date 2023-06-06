package Logic.AccountingStrategy;

import Communication.Communication;
import Data.Consumer;
import Data.IProsumerDevice;
import Exceptions.DeviceNotSupportedException;
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
    public double calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException {

        HashMap<String, Double> consumptionMap = getConsumptionMap(devices);
        ConsumptionRequest request = new ConsumptionRequest(consumptionMap, timeSlotId);
        communication.sendConsumptionRequestMessage(request);


        return 0;

    }

    private HashMap<String, Double> getConsumptionMap(List<IProsumerDevice> devices) throws DeviceNotSupportedException{
        HashMap<String, Double> consumptionMap = new HashMap<>();
        for (IProsumerDevice device : devices) {
            if (device.getDevice() instanceof Consumer consumer) {
                consumptionMap.put(consumer.getConsumerType().toString(), consumer.getAverageConsumption());
            } else {
                throw new DeviceNotSupportedException("A Consumer Device was expected but a " + device.getDevice().getClass().getName() + " was found");
            }
        }
        return consumptionMap;
    }
}

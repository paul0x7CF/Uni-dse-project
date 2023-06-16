package MSP.Logic.AccountingStrategy;

import CF.sendable.TimeSlot;
import MSP.Communication.Communication;
import MSP.Communication.polling.PollForecast;
import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Data.IProsumerDevice;
import MSP.Exceptions.DeviceNotSupportedException;
import CF.sendable.ConsumptionRequest;
import MSP.Exceptions.ServiceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalcConsumption implements ICalAcctStrategy {

    private static final Logger logger = LogManager.getLogger(CalcConsumption.class);

    private final Communication communication;

    public CalcConsumption(Communication communication) {
        this.communication = communication;
    }

    @Override
    public PollForecast calculateAccounting(List<IProsumerDevice> devices, TimeSlot timeSlot) throws DeviceNotSupportedException, ServiceNotFoundException {

        HashMap<String, Double> consumptionMap = getConsumptionMap(devices, timeSlot);
        ConsumptionRequest request = new ConsumptionRequest(consumptionMap, timeSlot.getTimeSlotID());
        return communication.sendConsumptionRequestMessage(request);


    }

    private HashMap<String, Double> getConsumptionMap(List<IProsumerDevice> devices, TimeSlot timeSlot) throws DeviceNotSupportedException{
        HashMap<String, Double> consumptionMap = new HashMap<>();
        int deviceCount = 0;
        int deviceAdded = 0;
        ArrayList<EConsumerType> addedConsumers = new ArrayList<>();
        for (IProsumerDevice device : devices) {
            deviceCount++;
            if (device.getDevice() instanceof Consumer consumer) {
                if(consumer.isAllowedToConsume(timeSlot.getStartTime().toLocalTime())) {
                    consumptionMap.put(consumer.getConsumerType().toString(), consumer.getAverageConsumption());
                    deviceAdded++;
                    addedConsumers.add(consumer.getConsumerType());
                    logger.trace("Consumer " + consumer.getConsumerType() + " was put into the consumptionMap");
                }
            } else {
                throw new DeviceNotSupportedException("A Consumer Device was expected but a " + device.getDevice().getClass().getName() + " was found");
            }
        }
        logger.debug("{} of {} devices were added to the consumptionMap from type {}", deviceAdded, deviceCount, addedConsumers.toString());
        return consumptionMap;
    }
}

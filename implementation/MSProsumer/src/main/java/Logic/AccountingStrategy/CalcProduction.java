package Logic.AccountingStrategy;

import Communication.Communication;
import Communication.PollForecast;
import Data.IProsumerDevice;
import Data.Producer;
import Exceptions.DeviceNotSupportedException;

import java.util.List;
import java.util.UUID;

public class CalcProduction implements ICalAcctStrategy{

    private final Communication communication;
    public CalcProduction(Communication communication) {
        this.communication = communication;
    }
    @Override
    public PollForecast calculateAccounting(List<IProsumerDevice> devices, UUID timeSlotId) throws DeviceNotSupportedException {

        for (IProsumerDevice device : devices) {
            if (device.getDevice() instanceof Producer producer) {

                // TODO: @Paul implement Logic for productionStrategy

            } else {
                throw new DeviceNotSupportedException("A Producer Device was expected but a " + device.getDevice().getClass().getName() + " was found");

            }
        }


        // TODO: @Paul define return type
        return null;

    }
}

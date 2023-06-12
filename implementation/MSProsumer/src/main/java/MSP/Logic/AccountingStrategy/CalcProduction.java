package MSP.Logic.AccountingStrategy;

import MSP.Communication.Communication;
import MSP.Communication.PollForecast;
import MSP.Data.IProsumerDevice;
import MSP.Data.Producer;
import MSP.Exceptions.DeviceNotSupportedException;

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

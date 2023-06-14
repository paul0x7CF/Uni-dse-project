package MSP.Logic.AccountingStrategy;

import CF.sendable.SolarRequest;
import CF.sendable.TimeSlot;
import MSP.Communication.Communication;
import MSP.Communication.polling.PollForecast;
import MSP.Data.IProsumerDevice;
import MSP.Data.Producer;
import MSP.Exceptions.DeviceNotSupportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CalcProduction implements ICalAcctStrategy{

    private static final Logger logger = LogManager.getLogger(CalcProduction.class);

    private final Communication communication;
    public CalcProduction(Communication communication) {
        this.communication = communication;
    }
    @Override
    public PollForecast calculateAccounting(List<IProsumerDevice> devices, TimeSlot timeSlotId) throws DeviceNotSupportedException {
        List<Producer> producers = new LinkedList<>();
        for (IProsumerDevice device : devices) {
            if (device.getDevice() instanceof Producer producer) {
                producers.add(producer);
            } else {
                throw new DeviceNotSupportedException("A Producer Device was expected but a " + device.getDevice().getClass().getName() + " was found");
            }
        }
        SolarRequest solarRequest = getSolarRequest(producers, timeSlotId.getTimeSlotID());
        return communication.sendProductionRequestMessage(solarRequest);

    }


    private SolarRequest getSolarRequest(List<Producer> devices, UUID timeSlotID) {
        double[] area = new double[devices.size()];
        int[] efficiency = new int[devices.size()];
        int[] compassAngle = new int[devices.size()];
        int[] standingAngle = new int[devices.size()];
        for(int i = 0; i < devices.size(); i++) {
            area[i] = devices.get(i).getArea();
            efficiency[i] = devices.get(i).getEfficiency();
            compassAngle[i] = devices.get(i).getCompassAngle();
            standingAngle[i] = devices.get(i).getStandingAngle();
        }

        SolarRequest resultSolarRequest= new SolarRequest(timeSlotID, devices.size(), area, efficiency, compassAngle, standingAngle);
        logger.debug("SolarRequest created with {} devices", devices.size());
        return resultSolarRequest;
    }
}

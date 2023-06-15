package MSF.mainPackage;

import CF.sendable.TimeSlot;
import MSF.data.EForecastType;
import MSF.exceptions.UnknownForecastTypeException;
import MSF.historicData.HistoricDataReader;

import java.time.LocalDateTime;
import java.util.List;

public class TEST {
    public static void main(String[] args) throws UnknownForecastTypeException {
       /* double frequency = 1; // Adjust this value for the desired frequency
        double amplitude = 100; // Adjust this value for the desired amplitude
        double phase = 0.0;

        double energyConsumption = amplitude * Math.sin((2 * Math.PI * frequency * 3600 + phase) * (Math.PI / 180));

        double consumption = (double) 100 / ((double) 3600 / 60);
        System.out.println("energyConsumption: " + energyConsumption / 60);

        System.out.println("Consumption: " + consumption);*/



        List<String> historicData = HistoricDataReader.getHistoricData(new TimeSlot(LocalDateTime.now(), LocalDateTime.now().plusHours(1)), EForecastType.INCA_L);

        double smoothedData = 0;
        double smoothingFactor = 0.8;
        boolean firstValue = true;

        for (String data : historicData) {
            System.out.println("data: " + data);
            double value = Double.parseDouble(data);

            if (firstValue) {
                firstValue = false;
                smoothedData = value;
                continue;
            }

            smoothedData = smoothingFactor * value + (1 - smoothingFactor) * smoothedData;
        }

        System.out.println("Smoothed data: " + smoothedData);
    }
}

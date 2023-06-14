package MSF.calculation;

import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.EForecastType;
import MSF.data.ProsumerConsumptionRequest;
import CF.sendable.SolarRequest;
import MSF.data.ProsumerSolarRequest;
import MSF.exceptions.UnknownForecastTypeException;
import MSF.historicData.HistoricDataReader;

import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private BlockingQueue<ProsumerSolarRequest> incomingSolarRequest;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private EForecastType forecastType;
    private TimeSlot currentTimeSlot;

    public ProductionForecast(BlockingQueue<ProsumerSolarRequest> incomingSolarRequest, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot, EForecastType forecastType) {
        this.incomingSolarRequest = incomingSolarRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
        this.forecastType = forecastType;
    }
    @Override
    public void run() {
        while (true) {
            try {
                ProsumerSolarRequest prosumerSolarRequest = incomingSolarRequest.take();
                predictProduction(prosumerSolarRequest);
            } catch (InterruptedException | UnknownForecastTypeException e) {
                e.printStackTrace();
            }
        }
    }

    private void predictProduction(ProsumerSolarRequest prosumerSolarRequest) throws UnknownForecastTypeException {

        //TODO: calculate production (also check currentTimeSlotID)

        double production = 0;

        for (int i = 0; i < prosumerSolarRequest.getAmountOfPanels(); i++) {
            switch (forecastType) {
                case APOLIS -> {
                    double irradiation = Double.parseDouble(HistoricDataReader.getHistoricData(currentTimeSlot, forecastType).split(";")[1]) * 1000 / 24;

                    production += irradiation * prosumerSolarRequest.getArea()[i] * prosumerSolarRequest.getEfficiency()[i] * Math.cos(prosumerSolarRequest.getCompassAngle()[i]) * Math.cos(prosumerSolarRequest.getStandingAngle()[i]);
                }
            }
        }


    }
}

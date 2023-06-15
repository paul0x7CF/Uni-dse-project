package MSF.calculation;

import CF.sendable.SolarResponse;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.EForecastType;
import MSF.data.ProsumerSolarRequest;
import MSF.exceptions.UnknownForecastTypeException;
import MSF.historicData.HistoricDataReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private BlockingQueue<ProsumerSolarRequest> incomingSolarRequest;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private EForecastType forecastType;
    private TimeSlot currentTimeSlot;
    private double smoothingFactor;
    private List<Double> lastForecasts;

    public ProductionForecast(BlockingQueue<ProsumerSolarRequest> incomingSolarRequest, ForecastCommunicationHandler forecastCommunicationHandler, TimeSlot currentTimeSlot, EForecastType forecastType) throws UnknownForecastTypeException {
        this.incomingSolarRequest = incomingSolarRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlot = currentTimeSlot;
        this.forecastType = forecastType;
        this.lastForecasts = new ArrayList<>();
        this.smoothingFactor = 0.8;
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

    private double getHistoricMeasurements() throws UnknownForecastTypeException {
        List<String> historicData = HistoricDataReader.getHistoricData(currentTimeSlot, forecastType);

        double smoothedData = 0;
        boolean firstValue = true;

        for (String data : historicData) {
            double value = Double.parseDouble(data);

            if (firstValue) {
                firstValue = false;
                smoothedData = value;
                continue;
            }

            smoothedData = smoothingFactor * value + (1 - smoothingFactor) * smoothedData;
        }

        return smoothedData;
    }

    private void predictProduction(ProsumerSolarRequest prosumerSolarRequest) throws UnknownForecastTypeException {

        //TODO: CHECK TimeSlotID

        double production = 0;
        double irradiation = getHistoricMeasurements();

        for (int i = 0; i < prosumerSolarRequest.getAmountOfPanels(); i++) {
            double standingAngleRad = prosumerSolarRequest.getStandingAngle()[i] * (Math.PI / 180);
            double compassAngleRad = prosumerSolarRequest.getCompassAngle()[i] * (Math.PI / 180);
            double efficiency = (double) prosumerSolarRequest.getEfficiency()[i] / 100;

            production += irradiation * prosumerSolarRequest.getArea()[i] * efficiency * Math.cos(compassAngleRad) * Math.cos(standingAngleRad);
        }

        for (Double lastForecast : lastForecasts) {
            production = smoothingFactor * production + (1 - smoothingFactor) * lastForecast;
        }

        lastForecasts.add(production);

        SolarResponse solarResponse = new SolarResponse(prosumerSolarRequest.getCurrentTimeSlotID(), production);
        this.forecastCommunicationHandler.sendProductionResponseMessage(solarResponse, prosumerSolarRequest.getSenderAddress(), prosumerSolarRequest.getSenderPort(), prosumerSolarRequest.getSenderID());
    }
}

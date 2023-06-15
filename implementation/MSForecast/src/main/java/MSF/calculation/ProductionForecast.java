package MSF.calculation;

import CF.sendable.SolarResponse;
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
            double irradiation = 0;
            double standingAngleRad = prosumerSolarRequest.getStandingAngle()[i] * (Math.PI / 180);
            double compassAngleRad = prosumerSolarRequest.getCompassAngle()[i] * (Math.PI / 180);
            double efficiency = (double) prosumerSolarRequest.getEfficiency()[i] / 100;

            switch (forecastType) {
                case APOLIS ->
                    irradiation = Double.parseDouble(HistoricDataReader.getHistoricData(currentTimeSlot, forecastType).split(";")[1]) * 1000 / 24;
                case GROUNDSTATION, INCA_L ->
                    irradiation = Double.parseDouble(HistoricDataReader.getHistoricData(currentTimeSlot, forecastType).split(";")[1]);
                default ->
                    throw new UnknownForecastTypeException();
            }

            production += irradiation * prosumerSolarRequest.getArea()[i] * efficiency * Math.cos(compassAngleRad) * Math.cos(standingAngleRad);
        }

        SolarResponse solarResponse = new SolarResponse(prosumerSolarRequest.getCurrentTimeSlotID(), production);
        this.forecastCommunicationHandler.sendProductionResponseMessage(solarResponse, prosumerSolarRequest.getSenderAddress(), prosumerSolarRequest.getSenderPort(), prosumerSolarRequest.getSenderID());
    }
}

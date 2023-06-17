package MSF.calculation;

import CF.sendable.SolarResponse;
import CF.sendable.TimeSlot;
import MSF.communication.ForecastCommunicationHandler;
import MSF.data.EForecastType;
import MSF.data.ProsumerSolarRequest;
import MSF.exceptions.UnknownForecastTypeException;
import MSF.historicData.HistoricDataReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class ProductionForecast implements Runnable {
    private static final Logger logger = LogManager.getLogger(ProductionForecast.class);
    private BlockingQueue<ProsumerSolarRequest> incomingSolarRequest;
    private ForecastCommunicationHandler forecastCommunicationHandler;
    private EForecastType forecastType;
    private Map<UUID, TimeSlot> currentTimeSlots;
    private double smoothingFactor;
    private List<Double> lastForecasts;

    public ProductionForecast(BlockingQueue<ProsumerSolarRequest> incomingSolarRequest, ForecastCommunicationHandler forecastCommunicationHandler, Map<UUID, TimeSlot> currentTimeSlot, EForecastType forecastType) throws UnknownForecastTypeException {
        this.incomingSolarRequest = incomingSolarRequest;
        this.forecastCommunicationHandler = forecastCommunicationHandler;
        this.currentTimeSlots = currentTimeSlot;
        this.forecastType = forecastType;
        this.lastForecasts = new ArrayList<>();
        this.smoothingFactor = 0.5;
    }
    @Override
    public void run() {
        logger.info("ProductionForecast started");

        while (true) {
            try {
                ProsumerSolarRequest prosumerSolarRequest = incomingSolarRequest.take();
                predictProduction(prosumerSolarRequest);
            } catch (InterruptedException | UnknownForecastTypeException e) {
                e.printStackTrace();
            }
        }
    }

    private double getHistoricMeasurements(TimeSlot timeSlot) throws UnknownForecastTypeException {
        List<String> historicData = HistoricDataReader.getHistoricData(timeSlot, forecastType);

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
        double production = 0;
        double irradiation = 0;
        UUID timeSlotID = UUID.randomUUID();

        boolean timeSlotReceived = false;

        for (var entry : currentTimeSlots.entrySet()) {
            timeSlotID = entry.getKey();
            irradiation = getHistoricMeasurements(entry.getValue());

            if (entry.getKey().equals(prosumerSolarRequest.getCurrentTimeSlotID())) {
                timeSlotReceived = true;
                break;
            }
        }


        if (timeSlotReceived)
            irradiation = getHistoricMeasurements(currentTimeSlots.get(prosumerSolarRequest.getCurrentTimeSlotID()));

        logger.trace("Historic Irradiation: " + irradiation);
        logger.trace("Forecasting production for prosumer " + prosumerSolarRequest.getSenderID() + " for timeslot " + prosumerSolarRequest.getCurrentTimeSlotID());

        Duration duration = Duration.between(currentTimeSlots.get(timeSlotID).getStartTime(), currentTimeSlots.get(timeSlotID).getEndTime());

        for (int i = 0; i < prosumerSolarRequest.getAmountOfPanels(); i++) {
            double standingAngleRad = prosumerSolarRequest.getStandingAngle()[i] * (Math.PI / 180);
            double compassAngleRad = prosumerSolarRequest.getCompassAngle()[i] * (Math.PI / 180);
            double efficiency = (double) prosumerSolarRequest.getEfficiency()[i] / 100;

            double panelProduction = irradiation * prosumerSolarRequest.getArea()[i] * efficiency * Math.cos(compassAngleRad) * Math.cos(standingAngleRad);

            if (panelProduction < 0) {
                panelProduction = panelProduction * -1;
            }

            production += panelProduction;
        }

        // was removed, because it caused the forecast to be too low
        /*for (int i = 0; i < lastForecasts.size() - 1; i++) {
            production = smoothingFactor * lastForecasts.get(i) + (1 - smoothingFactor) * lastForecasts.get(i + 1);
        }*/

        // to account for the faulty data in the historic data
        production = production * 1.5;

        production = production / ((double) 3600 / duration.getSeconds());
        lastForecasts.add(production);

        SolarResponse solarResponse = new SolarResponse(prosumerSolarRequest.getCurrentTimeSlotID(), production);
        this.forecastCommunicationHandler.sendProductionResponseMessage(solarResponse, prosumerSolarRequest.getSenderAddress(), prosumerSolarRequest.getSenderPort(), prosumerSolarRequest.getSenderID());
    }

    public void setCurrentTimeSlot(TimeSlot currentTimeSlot) {
        this.currentTimeSlots.put(currentTimeSlot.getTimeSlotID(), currentTimeSlot);
    }
}

package MSP.Communication.MessageHandling;

import CF.sendable.ConsumptionResponse;
import CF.sendable.SolarResponse;
import MSP.Communication.polling.PollConsumptionForecast;
import MSP.Communication.polling.PollProductionForecast;
import MSP.Configuration.ConfigFileReader;
import MSP.Data.EConsumerType;
import MSP.Exceptions.MessageNotSupportedException;
import CF.exceptions.MessageProcessingException;
import CF.exceptions.RemoteException;
import CF.messageHandling.IMessageHandler;
import MSP.Exceptions.UnknownForecastResponseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;

import java.util.HashMap;
import java.util.UUID;

public class ForecastMessageHandler implements IMessageHandler {
    private static final Logger logger = LogManager.getLogger(ForecastMessageHandler.class);

    private HashMap<UUID, PollConsumptionForecast> pollForecastConsumptionMap;
    private HashMap<UUID, PollProductionForecast> pollForecastProductionMap;

    public ForecastMessageHandler(HashMap<UUID, PollConsumptionForecast> pollForecastConsumptionMap, HashMap<UUID, PollProductionForecast> pollForecastProductionMap) {
        this.pollForecastConsumptionMap = pollForecastConsumptionMap;
        this.pollForecastProductionMap = pollForecastProductionMap;

    }

    @Override
    public void handleMessage(Message message) throws MessageProcessingException, RemoteException {
        try {
            switch (message.getSubCategory()) {
                case "Production" -> handleProduction(message);
                case "Consumption" -> handleConsumption(message);
                default -> throw new MessageNotSupportedException();
            }
        } catch (MessageNotSupportedException e) {
            logger.warn(e.getMessage());
        }
    }

    private void handleConsumption(Message message) {
        logger.debug("Consumption message received");
        ConsumptionResponse consumptionResponse = (ConsumptionResponse) message.getSendable(ConsumptionResponse.class);
        try {
            if (!this.pollForecastConsumptionMap.containsKey(consumptionResponse.getRequestTimeSlotId())) {
                throw new UnknownForecastResponseException();
            }

            PollConsumptionForecast pollForecastForTimeSlotID = this.pollForecastConsumptionMap.get(consumptionResponse.getRequestTimeSlotId());

            // Check if the PollForecast is already available because request was broadcasted to all Forecasts
            if (!pollForecastForTimeSlotID.isAvailable()) {
                // Convert the HashMap<String, Double> to HashMap<EConsumerType, Double> for the PollingObject
                HashMap<String, Double> consumptionForecastMap = consumptionResponse.getConsumptionMap();
                HashMap<EConsumerType, Double> pollingResult = new HashMap<>();
                for (var entry : consumptionForecastMap.entrySet()) {
                    pollingResult.put(EConsumerType.valueOf(entry.getKey()), entry.getValue());
                }

                pollForecastForTimeSlotID.setPollResult(pollingResult);
                pollForecastForTimeSlotID.setAvailable(true);
                logger.debug("Consumption Forecast Response was set on Poll Object");
            } else {
                logger.trace("Received a consumption forecast response for a time slot that is already available");
            }
        } catch (UnknownForecastResponseException e) {
            logger.trace(e.getMessage() + "Message was ignored");
        }
    }


    private void handleProduction(Message message) {
        logger.debug("Production message received");
        SolarResponse solarResponse = (SolarResponse) message.getSendable(SolarResponse.class);
        try {
            if (!this.pollForecastProductionMap.containsKey(solarResponse.getResponseTimeSlotId())) {
                throw new UnknownForecastResponseException();
            }

            final int MAX_RESPONSES_FORECAST = Integer.parseInt(ConfigFileReader.getCommunicationProperty("forecastAmount"));
            PollProductionForecast pollForecastForTimeSlotID = this.pollForecastProductionMap.get(solarResponse.getResponseTimeSlotId());
            if (pollForecastForTimeSlotID.getResponseSize() < MAX_RESPONSES_FORECAST) {
                pollForecastForTimeSlotID.setPollResult(solarResponse.getSolarProduction());
                logger.debug("Production Forecast Response was added to Poll Object and the size is now {}", pollForecastForTimeSlotID.getResponseSize());
            } else if (pollForecastForTimeSlotID.getResponseSize() == MAX_RESPONSES_FORECAST) {
                pollForecastForTimeSlotID.setAvailable(true);
                logger.debug("Production Forecast Poll Object was set on available because all expected responses were received");
            } else {
                logger.trace("More Production Forecast Responses were received than expected for the TimeSlot, Message was ignored");
            }
        } catch (UnknownForecastResponseException e) {
            logger.trace(e.getMessage() + "Message was ignored");
        }
    }

}

package MSP.Logic;

import CF.sendable.TimeSlot;
import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class Scheduler {

    private static final Logger logger = LogManager.getLogger(Scheduler.class);

    private static double[] lastPrices = new double[10];

    // TODO: add last prices

    public Scheduler() {
    }

    public double calculate(LinkedHashSet<Consumer> consumerList) {
        double summedNeededConsumption = 0;
        for (Consumer currConsumer : consumerList) {
            if(currConsumer.isHeavyConsumer()){
                if (isPriceDescending()) {
                    summedNeededConsumption += currConsumer.getResultOfForecast();
                    currConsumer.decrementStillNeededEnergy(currConsumer.getResultOfForecast());
                }
                else {
                    logger.info("Price is not descending, so no heavy consumer");
                }
            }
            else{
                summedNeededConsumption += currConsumer.getResultOfForecast();
                currConsumer.decrementStillNeededEnergy(currConsumer.getResultOfForecast());
            }
        }
        logger.trace("Summed needed consumption: " + summedNeededConsumption);
        return summedNeededConsumption;
    }



    private boolean isPriceDescending() {
        double threshold = 0.1;

        for (int i = 1; i < lastPrices.length; i++) {
            double currentPrice = lastPrices[i];
            double previousPrice = lastPrices[i - 1];
            double priceDifference = currentPrice - previousPrice;

            if (priceDifference > threshold) {
                continue;
            }

            if (priceDifference < 0) {
                // Non-descending trend
                return false;
            }
        }
        // Descending trend
        return true;
    }
}

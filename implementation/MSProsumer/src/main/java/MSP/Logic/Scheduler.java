package MSP.Logic;

import CF.sendable.TimeSlot;
import MSP.Data.Consumer;
import MSP.Data.EConsumerType;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Scheduler {

    private static final Logger logger = LogManager.getLogger(Scheduler.class);

    private LinkedList<Double> lastPrices = new LinkedList<>();
    private static final int maxSize = 5;


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

    public void insertValue(double value) {
        logger.debug("inserting value: {}",value);
        if (lastPrices.size() >= maxSize) {
            lastPrices.removeFirst();
        }
        lastPrices.addLast(value);
    }



    private boolean isPriceDescending() {
        double threshold = 1.5;
        if(lastPrices.size() == 0){
            return true;
        }

        Iterator<Double> iterator = lastPrices.iterator();
        double previousPrice = iterator.next();

        while (iterator.hasNext()) {
            double currentPrice = iterator.next();
            double priceDifference = currentPrice - previousPrice;

            if (priceDifference > threshold) {
                continue;
            }

            if (priceDifference < 0) {
                // Non-descending trend
                return false;
            }

            previousPrice = currentPrice;
        }
        // Descending trend
        return true;
    }
}

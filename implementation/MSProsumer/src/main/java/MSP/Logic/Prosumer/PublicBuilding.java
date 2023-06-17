package MSP.Logic.Prosumer;

import MSP.Configuration.ConfigFileReader;
import MSP.Data.EConsumerType;
import MSP.Data.EProsumerType;

public class PublicBuilding extends ConsumptionBuilding {
    public PublicBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);
    }

    @Override
    protected void initializeConsumer() {
        final int INITIALIZED_CONSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("consumer.amount.OfPublicBuilding"));
        for (int i = 1; i <= INITIALIZED_CONSUMER_AMOUNT; i++) {
            createConsumer(EConsumerType.valueOf(ConfigFileReader.getProperty("consumer.type"+i+".OfPublicBuilding")));
        }
    }
}

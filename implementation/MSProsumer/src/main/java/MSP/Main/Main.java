package MSP.Main;

import MSP.Configuration.ConfigFileReader;
import MSP.Data.EProsumerType;
import MSP.Logic.Prosumer.ConsumptionBuilding;
import MSP.Logic.Prosumer.NettoZeroBuilding;
import MSP.Logic.Prosumer.PublicBuilding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {

        //read in the config file
        //final int PROSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("prosumer.amount"));
        final double CASH_BALANCE = Double.parseDouble(ConfigFileReader.getProperty("wallet.cashBalance"));
        final int PROSUMER_START_PORT = Integer.parseInt(ConfigFileReader.getCommunicationProperty("prosumerPort"));
        final int PROSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getCommunicationProperty("prosumerAmount"));
        final int PORT_JUMP = Integer.parseInt(ConfigFileReader.getCommunicationProperty("portJumpSize"));

        int amountOfConsumptionBuilding = 0;
        int amountOfNettoZeroBuilding = 0;
        int amountOfPublicBuilding = 0;

        for (int i = 0; i < PROSUMER_AMOUNT; i++) {
            final int PORT = PROSUMER_START_PORT + (i * PORT_JUMP);
            Random random = new Random();
            int randomValue = random.nextInt(EProsumerType.values().length);
            EProsumerType prosumerType = EProsumerType.valueOf(ConfigFileReader.getProperty("prosumer.type" + (i+1)));
            switch (prosumerType) {
                case NETTO_ZERO_BUILDING ->{
                    amountOfNettoZeroBuilding++;
                    new Thread(new NettoZeroBuilding(prosumerType, CASH_BALANCE, PORT),"Prosumer-"+i).start();
                }
                case CONSUMPTION_BUILDING -> {
                    amountOfConsumptionBuilding++;
                    new Thread(new ConsumptionBuilding(prosumerType, CASH_BALANCE, PORT),"Prosumer-"+i).start();
                }
                case PUBLIC_BUILDING -> {
                    amountOfPublicBuilding++;
                    new Thread(new PublicBuilding(prosumerType, CASH_BALANCE, PORT),"Prosumer-"+i).start();
                }
            }
        }
        logger.info("{} NettoZeroBuilding were created", amountOfNettoZeroBuilding);
        logger.info("{} ConsumptionBuilding were created", amountOfConsumptionBuilding);
        logger.info("{} PublicBuilding were created", amountOfPublicBuilding);
        System.out.println("Main Thread ended");

    }
}

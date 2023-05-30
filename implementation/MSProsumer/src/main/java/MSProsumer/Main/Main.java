package MSProsumer.Main;

import Configuration.ConfigFileReader;
import Data.EProsumerType;
import Logic.Prosumer.ConsumptionBuilding;
import Logic.Prosumer.NettoZeroBuilding;
import Logic.Prosumer.Prosumer;
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

        for (int i = 0; i < PROSUMER_AMOUNT; i++) {
            final int PORT = PROSUMER_START_PORT + (i * PORT_JUMP);
            Random random = new Random();
            int randomValue = random.nextInt(EProsumerType.values().length);
            EProsumerType prosumerType = EProsumerType.values()[randomValue];
            switch (prosumerType) {
                case NETTO_ZERO_BUILDING ->{
                    new Thread(new NettoZeroBuilding(prosumerType, CASH_BALANCE, PORT),"Prosumer-"+i).start();
                }
                case CONSUMPTION_BUILDING, PUBLIC_BUILDING -> {
                    new Thread(new ConsumptionBuilding(prosumerType, CASH_BALANCE, PORT),"Prosumer-"+i).start();
                }
            }
        }

    }
}

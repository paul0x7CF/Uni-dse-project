package MSProsumer.Main;

import Data.EProsumerType;
import Logic.Prosumer;
import mainPackage.MainForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class Main {

    private static final Logger logger = LogManager.getLogger(MainForTesting.class);
    public static void main(String[] args) {

        //read in the config file
        final int PROSUMER_AMOUNT;
        final double CASH_BALANCE;

        try {
            Properties properties = new Properties();
            final String PATH = "implementation/MSProsumer/src/main/resources/config.properties";
            FileInputStream configFile = new FileInputStream(PATH);
            properties.load(configFile);

            PROSUMER_AMOUNT = Integer.parseInt(properties.getProperty("prosumer.amount"));
            CASH_BALANCE = Double.parseDouble(properties.getProperty("wallet.cashBalance"));

        } catch (FileNotFoundException e) {
            logger.error("Config file not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error while reading config file");
            throw new RuntimeException(e);
        }


        for (int i = 0; i < PROSUMER_AMOUNT; i++) {
            Random random = new Random();
            int randomValue = random.nextInt(EProsumerType.values().length);
            EProsumerType prosumerType = EProsumerType.values()[randomValue];
            new Thread(new Prosumer(prosumerType, CASH_BALANCE)).start();
        }

    }
}

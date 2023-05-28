package MSProsumer.Main;

import Configuration.ConfigFileReader;
import Data.EProsumerType;
import Logic.Prosumer;
import mainPackage.ConfigReader;
import mainPackage.MainForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {

        //read in the config file
        final int PROSUMER_AMOUNT = Integer.parseInt(ConfigFileReader.getProperty("prosumer.amount"));
        final double CASH_BALANCE = Double.parseDouble(ConfigFileReader.getProperty("wallet.cashBalance"));


        for (int i = 0; i < PROSUMER_AMOUNT; i++) {
            final int port = 1050 + i * 50;
            Random random = new Random();
            int randomValue = random.nextInt(EProsumerType.values().length);
            EProsumerType prosumerType = EProsumerType.values()[randomValue];
            new Thread(new Prosumer(prosumerType, CASH_BALANCE, port)).start();
        }

    }
}

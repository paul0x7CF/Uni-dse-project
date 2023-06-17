package MSS.main;

import MSS.dataBase.DbTransaction;

import MSS.configuration.ConfigFileReader;
import MSS.dataBase.DbTransaction;
import MSS.dataBase.TransactionDAO;
import MSS.storage.MSStorageManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);


    private static final Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) {


       final int STORAGE_START_PORT = Integer.parseInt(ConfigFileReader.getCommunicationProperty("storagePort"));
        final double WALLET_START_MONEY = Double.parseDouble(ConfigFileReader.getProperty("walletStartMoney"));

        new Thread(new MSStorageManager(STORAGE_START_PORT, WALLET_START_MONEY),"Storage").start();
        System.out.println("----------------MAIN Thread Ended----------------");

    }
}

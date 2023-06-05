package mainPackage;

import loadManager.Controller;
import msExchange.MSExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static MSExchange msExchange;
    private static Controller controller;

    public static void main(String[] args) {
        boolean duplicated = false;
        int instanceNumber = 1;
        if (args.length == 0) {
            logger.info("No arguments passed, running as non-duplicated");
        } else {
            if ("-s".equals(args[0])) {
                duplicated = true;
            }
            if (args[1] == null) {
                logger.error("No instance number passed");
                throw new IllegalStateException("No instance number passed");
            } else {
                instanceNumber = Integer.parseInt(args[1]);
            }
        }


        msExchange = new MSExchange(duplicated, instanceNumber);
        Thread exchangeThread = new Thread(msExchange, "ExchangeThread");
        exchangeThread.start();

        if (!duplicated) {
            controller = new Controller();
            Thread controllerThread = new Thread(controller, "LoadManagerThread");
            controllerThread.start();
        }
    }

    public static MSExchange getMsExchange() {
        return msExchange;
    }

    public static Optional<Controller> getController() {
        Optional<Controller> controller = Optional.ofNullable(Main.controller);
        return controller;
    }
}

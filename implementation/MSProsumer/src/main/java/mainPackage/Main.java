package mainPackage;

import communication.NetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.EServiceType;

public class Main {

    private static final Logger logger = LogManager.getLogger(MainForTesting.class);
    public static void main(String[] args) {
        NetworkHandler network = new NetworkHandler(EServiceType.Prosumer, 8080);

        logger.info("Client Started");
        logger.debug("hello debug");
        logger.error("hello error");
        logger.fatal("hello fatal");
        logger.trace("hello trace");
        logger.warn("hello warn");
    }
}

package mainPackage;

import network.NetworkHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        NetworkHandler network = new NetworkHandler();

        logger.info("Client Started");
        logger.debug("hello debug");
        logger.error("hello error");
        logger.fatal("hello fatal");
        logger.trace("hello trace");
        logger.warn("hello warn");
    }
}

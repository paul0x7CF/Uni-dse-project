package MSProsumer.Main;

import mainPackage.MainForTesting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger log = LogManager.getLogger(MainForTesting.class);
    public static void main(String[] args) {

        log.info("Client Started");
        log.debug("hello debug");
        log.error("hello error");
        log.fatal("hello fatal");
        log.trace("hello trace");
        log.warn("hello warn");
    }
}

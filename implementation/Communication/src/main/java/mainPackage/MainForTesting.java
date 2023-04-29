package mainPackage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sendable.EServiceType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainForTesting {
    private static final Logger logger = LogManager.getLogger(MainForTesting.class);

    public static void main(String[] args) {
        Runnable prosumer = new BrokerTestRunner(EServiceType.Prosumer, 5000);

        Runnable exchange = new BrokerTestRunner(EServiceType.Exchange, 6000);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(prosumer);
        executor.execute(exchange);
    }
}

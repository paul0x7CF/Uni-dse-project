package mainPackage;

import broker.Broker;
import broker.IBroker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;
import sendable.EServiceType;
import sendable.MSData;

import java.net.UnknownHostException;

public class BrokerTestRunner implements Runnable {
    private static final Logger logger = LogManager.getLogger(BrokerTestRunner.class);
    private final Broker broker;

    protected BrokerTestRunner(EServiceType serviceType, int listeningPort) {
        this.broker = new Broker(serviceType, listeningPort);
    }

    protected Broker getBroker() {
        return broker;
    }

    protected void sendMessage(Message message) {
        broker.sendMessage(message);
    }

    @Override
    public void run() {
        try {
            logger.info("Starting broker {}", broker.getCurrentService().getPort());
            broker.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public MSData getCurrentService() {
        return broker.getCurrentService();
    }
}

package mainPackage;

import broker.Broker;
import sendable.EServiceType;

import java.net.UnknownHostException;

public class BrokerTestRunner implements Runnable {
    private final Broker broker;

    protected BrokerTestRunner(EServiceType serviceType, int listeningPort) {
        this.broker = new Broker(serviceType, listeningPort);
    }

    @Override
    public void run() {
        try {
            broker.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}

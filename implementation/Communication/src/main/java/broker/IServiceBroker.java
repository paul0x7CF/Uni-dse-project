package broker;

import sendable.AckInfo;
import sendable.MSData;

import java.util.List;

public interface IServiceBroker extends IBroker {
    MSData getCurrentService();
    List<MSData> getServices();
    void ackReceived(AckInfo ack);
    boolean registerService(MSData msData);
    void unregisterService(MSData msData);
}
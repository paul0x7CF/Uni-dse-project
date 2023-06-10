package CF.broker;

import CF.sendable.AckInfo;
import CF.sendable.MSData;

import java.util.List;

public interface IServiceBroker extends IBroker {
    MSData getCurrentService();
    List<MSData> getServices();
    void ackReceived(AckInfo ack);
    boolean registerService(MSData msData);
    void unregisterService(MSData msData);
}
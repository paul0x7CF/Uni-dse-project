package broker;

import sendable.AckInfo;
import sendable.MSData;

public interface IServiceBroker extends IBroker {
    MSData getCurrentService();
    void ackReceived(AckInfo ack);
    void registerService(MSData msData);
    void unregisterService(MSData msData);
}
package broker.discovery;

import broker.IBroker;
import protocol.Message;
import sendable.MSData;

import java.util.UUID;

public interface IScheduleBroker extends IBroker {
    MSData getCurrentService();
    MSData findService(UUID serviceId);
    boolean serviceExists(int port);
}

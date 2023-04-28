package broker;

import protocol.Message;

import java.io.IOException;

public interface IBroker {
    void sendMessage(Message message) throws IOException;
}

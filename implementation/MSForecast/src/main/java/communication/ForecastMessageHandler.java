package communication;

import exceptions.MessageProcessingException;
import messageHandling.IMessageHandler;
import protocol.ECategory;
import protocol.Message;

import java.util.HashMap;
import java.util.Map;

public class ForecastMessageHandler implements IMessageHandler {
    private final Map<ECategory, IMessageHandler> handlers = new HashMap<>();

    @Override
    public void handleMessage(Message message) throws MessageProcessingException {

    }
}

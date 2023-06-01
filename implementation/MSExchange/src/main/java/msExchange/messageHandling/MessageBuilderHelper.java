package msExchange.messageHandling;

import loadManager.networkManagment.IMessageBuilder;
import protocol.Message;
import sendable.TimeSlot;

import java.util.List;

public class MessageBuilderHelper implements IMessageBuilder {

    @Override
    public List<Message> buildTimeSlotMessages(List<TimeSlot> timeSlots) {
        return null;
    }
}

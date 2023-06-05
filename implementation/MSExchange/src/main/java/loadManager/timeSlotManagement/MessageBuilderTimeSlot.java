package loadManager.timeSlotManagement;

import mainPackage.ESubCategory;
import mainPackage.IMessageBuilder;
import mainPackage.PropertyFileReader;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.MSData;
import sendable.TimeSlot;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilderTimeSlot {
    private final int DURATION;
    private TimeSlot lastSentTimeSlots;

    public MessageBuilderTimeSlot() {
        PropertyFileReader propertyFileReader = new PropertyFileReader();
        DURATION = Integer.parseInt(propertyFileReader.getDuration());
    }

    private Message buildSlotMessage(MessageFactory messageFactory, TimeSlot timeSlot) {
        messageFactory.setCategory(ECategory.Exchange, String.valueOf(ESubCategory.TimeSlot)).setPayload(timeSlot);
        return messageFactory.build();
    }


    public List<Message> buildTimeSlotMessages(TimeSlot newTimeSlot, List<MSData> currentServices, MSData myService) {
        List<Message> messages = new ArrayList<>();

        for(MSData msData: currentServices){
            messages.add(buildSlotMessage(IMessageBuilder.senderAndReceiverTemplate(msData, myService), newTimeSlot));
        }

        return messages;
    }
}

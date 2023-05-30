package loadManager.timeSlotManagement;

import loadManager.networkManagment.IMessageBuilder;
import mainPackage.ESubCategory;
import protocol.ECategory;
import protocol.Message;
import protocol.MessageFactory;
import sendable.MSData;
import sendable.TimeSlot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MessageBuilderTimeSlot implements IMessageBuilder {
    private final int DURATION;
    private final int NUM_NEW_TIME_SLOTS;
    private List<TimeSlot> lastSentTimeSlots = new ArrayList<>();

    public MessageBuilderTimeSlot() {
        Properties properties = new Properties();
        try {
            FileInputStream configFile = new FileInputStream("../config.properties");
            properties.load(configFile);
            configFile.close();

            DURATION = Integer.parseInt(properties.getProperty("timeslot.duration"));
            NUM_NEW_TIME_SLOTS = Integer.parseInt(properties.getProperty("timeslot.numNewTimeSlots"));
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> buildTimeSlotMessages(List<TimeSlot> timeSlots) {
        List<Message> messages = new ArrayList<>();

        if (lastSentTimeSlots.isEmpty()) {
            lastSentTimeSlots = timeSlots.subList(0, NUM_NEW_TIME_SLOTS);
        } else {
            List<TimeSlot> remainingTimeSlots = new ArrayList<>(timeSlots);

            for (TimeSlot oldSlot : lastSentTimeSlots) {
                remainingTimeSlots.removeIf(receivedSlot -> oldSlot.getTimeSlotID().equals(receivedSlot.getTimeSlotID()));
            }

            lastSentTimeSlots.clear();

            for (int i = 0; i < NUM_NEW_TIME_SLOTS && i < remainingTimeSlots.size(); i++) {
                TimeSlot newSlot = remainingTimeSlots.get(i);
                lastSentTimeSlots.add(newSlot);
                messages.add(buildSlotMessage(newSlot));
            }
        }

        return messages;
    }

    private Message buildSlotMessage(TimeSlot timeSlot) {
        //TODO: where do I get the receiver MSDATA? How to handle broadcast here?
        //just for now -> change later to correct MSData

        MSData receiverMS = new MSData(null, null, null, 0);
        MessageFactory messageFactory = IMessageBuilder.senderAndReceiverTemplate(receiverMS);

        messageFactory.setCategory(ECategory.Auction, String.valueOf(ESubCategory.TimeSlot)).setPayload(timeSlot);
        return messageFactory.build();
    }


}

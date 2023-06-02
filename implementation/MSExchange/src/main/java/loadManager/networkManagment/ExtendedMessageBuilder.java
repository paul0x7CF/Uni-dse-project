package loadManager.networkManagment;

import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import loadManager.exchangeManagement.ExchangeServiceInformation;
import messageHandling.IMessageHandler;
import protocol.Message;
import protocol.MessageFactory;
import sendable.Bid;
import sendable.Sell;
import sendable.TimeSlot;

import java.util.List;

public class ExtendedMessageBuilder implements IMessageBuilder{

    @Override
    public List<Message> buildTimeSlotMessages(List<TimeSlot> timeSlots) {
        return null;
    }

}

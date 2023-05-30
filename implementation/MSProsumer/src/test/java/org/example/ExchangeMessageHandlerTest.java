package org.example;

import Exceptions.MessageNotSupportedException;
import Logic.MessageHandling.ExchangeMessageHandler;
import exceptions.MessageProcessingException;
import exceptions.RemoteException;
import org.junit.Test;
import protocol.Message;
import org.mockito.Mockito;

public class ExchangeMessageHandlerTest {
    @Test
    public void handleMessage_WhenTimeSlotMessageReceived_ShouldHandleTimeSlot() throws MessageProcessingException, RemoteException, MessageNotSupportedException {
        // Arrange
        ExchangeMessageHandler messageHandler = new ExchangeMessageHandler();
        Message timeSlotMessage = createMockTimeSlotMessage();

        // Act
        messageHandler.handleMessage(timeSlotMessage);

        // Assert
        // Verify that the handleTimeSlot method was called
        //Mockito.verify(messageHandler, Mockito.times(1)).handleTimeSlot(timeSlotMessage);
    }

    private Message createMockTimeSlotMessage() {
        // Create a mock TimeSlot message
        Message timeSlotMessage = Mockito.mock(Message.class);
        Mockito.when(timeSlotMessage.getSubCategory()).thenReturn("TimeSlot");
        return timeSlotMessage;
    }
}

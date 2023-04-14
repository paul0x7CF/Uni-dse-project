package msExchange.networkCommunication;

import message.Message;
import network.NetworkHandler;
import network.ReceiveSocket;
import network.SendSocket;
import sendable.Timeslot;

import java.util.Collections;
import java.util.List;

public class MessageHandler {
    private int port;
    private ReceiveSocket receiveSocket;
    private SendSocket sendSocket;
    private NetworkHandler network;

    public MessageHandler(int port) {
    }
    //incoming messages
    public void handleMessage(Message message){}

    //outgoing messages
    private Message register(){return null;}//von Konstruktor aufgerufen

    private void updateTimeSlots(List<Timeslot> timeslots){}



}

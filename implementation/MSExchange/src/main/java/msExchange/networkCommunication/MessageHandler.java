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

    public void handleMessage(Message message){}

    private void register(){}//von Konstruktor aufgerufen

    public Message updateTimeSlots(){
        return null;
    }



}

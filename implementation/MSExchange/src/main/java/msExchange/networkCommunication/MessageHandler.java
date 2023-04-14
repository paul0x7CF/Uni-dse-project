package msExchange.networkCommunication;

import message.Message;
import network.InputSocket;
import network.NetworkHandler;
import network.OutputSocket;

public class MessageHandler {
    private int port;
    private InputSocket inputSocket;
    private OutputSocket outputSocket;
    private NetworkHandler network;

    public MessageHandler(int port) {
    }

    public void handleMessage(Message message){}

    private void register(){}//von Konstruktor aufgerufen

    public Message updateTimeSlots(){
        return null;
    }



}

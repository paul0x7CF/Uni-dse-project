package mainPackage;

import loadManager.LoadManager;
import network.NetworkHandler;

public class Main {
    public static void main(String[] args) {
        NetworkHandler network = new NetworkHandler();
        LoadManager loadManager = new LoadManager();
    }
}

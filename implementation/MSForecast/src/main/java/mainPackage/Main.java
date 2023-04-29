package mainPackage;

import communication.NetworkHandler;
import sendable.EServiceType;

public class Main {
    public static void main(String[] args) {
        NetworkHandler network = new NetworkHandler(EServiceType.Solar, 8080);
    }


}

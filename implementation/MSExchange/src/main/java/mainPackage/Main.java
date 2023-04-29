package mainPackage;

import loadManager.Controller;
import msExchange.MSExchange;

public class Main {

    public static <String> void main(String[] args) {
        int port = 1;
        String host = (String) "hostname";
        boolean duplicated = false;
        if ("-s".equals(args[0])) {
            duplicated = true;
        }
        MSExchange msExchange = new MSExchange(duplicated);

        if (!duplicated) {
            Controller controller = new Controller();
            Thread controllerThread = new Thread(controller);
            controllerThread.start();
        }
    }
}

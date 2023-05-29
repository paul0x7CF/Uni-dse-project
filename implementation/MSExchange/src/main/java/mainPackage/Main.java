package mainPackage;

import loadManager.Controller;
import msExchange.MSExchange;

import java.util.Optional;

public class Main {
    private static MSExchange msExchange;
    private static Controller controller;

    public static <String> void main(String[] args) {
        boolean duplicated = false;
        if ("-s".equals(args[0])) {
            duplicated = true;
        }

        msExchange = new MSExchange(duplicated);
        Thread exchangeThread = new Thread(msExchange);
        exchangeThread.start();

        if (!duplicated) {
            controller = new Controller();
            Thread controllerThread = new Thread(controller);
            controllerThread.start();
        }
    }

    public static MSExchange getMsExchange() {
        return msExchange;
    }

    public static Optional<Controller> getController() {
        Optional<Controller> controller = Optional.ofNullable(Main.controller);
        return controller;
    }
}

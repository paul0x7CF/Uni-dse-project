package mainPackage;

import loadManager.LoadManager;
import msExchange.MSExchange;

public class Main {

    public static <String> void main(String[] args) {
        int port=1;

        boolean duplicated=false;
        if ("-s".equals(args[0])) {
            duplicated = true;
        }
        MSExchange msExchange = new MSExchange(duplicated);

        if(!duplicated){
            LoadManager loadManager = new LoadManager(port);
        }
    }
}

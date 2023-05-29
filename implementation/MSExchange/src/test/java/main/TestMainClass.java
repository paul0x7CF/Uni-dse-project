package main;

import mainPackage.Main;
import msExchange.MSExchange;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestMainClass {

    @Test
    public void givenSFlag_main_expectedNoController() {
        String[] args = {"-s"};

        // Call the main method and check if it initializes MSExchange with duplicated as true
        Main.main(args);

        // Access the msExchange object from Main and assert its properties
        Main mainInstance = new Main();
        MSExchange msExchange = mainInstance.getMsExchange();
        Assertions.assertTrue(msExchange.isDuplicated());

        Assertions.assertTrue(mainInstance.getController().isEmpty());
    }

    @Test
    public void givenPFlag_main_expectedController() {
        String[] args = {"-p"};

        // Call the main method and check if it initializes MSExchange with duplicated as true
        Main.main(args);

        // Access the msExchange object from Main and assert its properties
        Main mainInstance = new Main();
        MSExchange msExchange = mainInstance.getMsExchange();
        Assertions.assertFalse(msExchange.isDuplicated());

        Assertions.assertFalse(mainInstance.getController().isEmpty());
    }
}

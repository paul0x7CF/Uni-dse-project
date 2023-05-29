package Logic.Prosumer;

import Data.Consumer;
import Data.EProsumerType;
import Logic.Prosumer.Prosumer;
import Logic.Scheduler;

import java.util.List;

public class ConsumptionBuilding extends Prosumer {

    public ConsumptionBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);
    }
}

package Logic.Prosumer;

import Data.EProsumerType;
import Data.Producer;
import Logic.Scheduler;

import java.util.List;

public class NettoZeroBuilding extends Prosumer {

    private List<Producer> producer;
    private Scheduler scheduler;
    public NettoZeroBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);
    }

}



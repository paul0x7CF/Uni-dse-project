package MSP.Logic.Prosumer;

import MSP.Data.EProsumerType;
import MSP.Data.Producer;
import MSP.Logic.Scheduler;

import java.util.List;

public class NettoZeroBuilding extends Prosumer {

    private List<Producer> producer;
    private Scheduler scheduler;
    public NettoZeroBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);
    }

}



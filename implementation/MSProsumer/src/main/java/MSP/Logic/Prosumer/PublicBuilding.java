package MSP.Logic.Prosumer;

import MSP.Data.EProsumerType;

public class PublicBuilding extends ConsumptionBuilding {
    public PublicBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);
    }
}

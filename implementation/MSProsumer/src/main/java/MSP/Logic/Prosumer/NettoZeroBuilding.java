package MSP.Logic.Prosumer;

import CF.sendable.TimeSlot;
import MSP.Communication.polling.PollProductionForecast;
import MSP.Data.EProsumerType;
import MSP.Data.Producer;
import MSP.Exceptions.DeviceNotSupportedException;
import MSP.Exceptions.UndefinedStrategyException;
import MSP.Logic.AccountingStrategy.CalcProduction;
import MSP.Logic.AccountingStrategy.ContextCalcAcct;
import MSP.Logic.Scheduler;

import java.util.ArrayList;
import java.util.List;

public class NettoZeroBuilding extends ConsumptionBuilding {

    private List<Producer> producerList;
    private PollProductionForecast pollOnProduction;
    private Scheduler scheduler;

    public NettoZeroBuilding(EProsumerType prosumerType, double cashBalance, int port) {
        super(prosumerType, cashBalance, port);
    }

    private void createProducer() {

    }

    @Override
    public void executeAccountingStrategy(TimeSlot newTimeSlot) {
        super.executeAccountingStrategy(newTimeSlot);
        try {

            ContextCalcAcct contextCalcAcct = new ContextCalcAcct();
            contextCalcAcct.setCalcAcctAStrategy(new CalcProduction(super.communicator));

            this.pollOnProduction = (PollProductionForecast) contextCalcAcct.calculateAccounting(new ArrayList<>(producerList), newTimeSlot);

        } catch (DeviceNotSupportedException | UndefinedStrategyException e) {
            throw new RuntimeException(e);
        }
    }

}




package CF.sendable;

/**
 * This enum is used to identify the type of service. ExchangeWorker is only used for the communication between
 * the Exchange and the ExchangeWorker.
 */
public enum EServiceType {
    Prosumer,
    Storage,
    Exchange,
    ExchangeWorker,
    Forecast
}
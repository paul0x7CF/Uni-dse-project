package MSP.Exceptions;

public class UndefinedStrategyException extends Exception{
    public UndefinedStrategyException() {
        super("No strategy found for executing strategy");
    }
}

package MSP.Exceptions;

public class ConfigFileReaderRuntimeException extends RuntimeException{
    public ConfigFileReaderRuntimeException() {
        super("Read value was empty");
    }
}

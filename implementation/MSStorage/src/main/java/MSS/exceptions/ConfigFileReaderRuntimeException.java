package MSS.exceptions;

public class ConfigFileReaderRuntimeException extends RuntimeException{
    public ConfigFileReaderRuntimeException() {
        super("Read value was empty");
    }
}

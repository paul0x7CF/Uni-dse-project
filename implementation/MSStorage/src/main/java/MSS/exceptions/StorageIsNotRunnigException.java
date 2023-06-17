package MSS.exceptions;

public class StorageIsNotRunnigException extends Exception{
    public StorageIsNotRunnigException() {
        super("Storage is not running");
    }
}

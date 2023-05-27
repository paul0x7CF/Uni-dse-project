package Exceptions;

public class WalletEmptyException extends Exception{
    public WalletEmptyException(String message) {
        super(message);
    }
}

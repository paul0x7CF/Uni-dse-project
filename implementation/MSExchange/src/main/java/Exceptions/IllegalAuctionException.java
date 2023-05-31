package Exceptions;

public class IllegalAuctionException extends IllegalArgumentException {
    public IllegalAuctionException(String message) {
        super(message);
    }
}

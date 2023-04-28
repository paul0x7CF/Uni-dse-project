package exceptions;

/**
 * This exception is thrown when there is a problem with the NetworkHandler. If there is a SocketException for example,
 * this will be forwarded as a NetworkException.
 */
public class NetworkException extends Exception {
    public NetworkException(String message) {
        super(message);
    }
}

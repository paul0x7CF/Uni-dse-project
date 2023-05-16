package communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class InputSocket implements Runnable {
    private static final Logger log = LogManager.getLogger(InputSocket.class);
    private final BlockingQueue<byte[]> input;
    private final int port;

    public InputSocket(BlockingQueue<byte[]> input, int port) {
        this.input = input;
        this.port = port;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        while (true) {
            byte[] buffer = new byte[1000];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            try {
                socket = new DatagramSocket(port);
                socket.receive(request);
                input.put(request.getData());
                log.trace("Received message from {}:{}", request.getAddress(), request.getPort());
            } catch (InterruptedException | IOException e) {
                log.error("Error while sending message: {}", e.getMessage());
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}

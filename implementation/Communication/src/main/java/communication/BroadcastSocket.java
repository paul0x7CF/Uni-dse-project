package communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;

public class BroadcastSocket implements Runnable {
    private static final Logger log = LogManager.getLogger(BroadcastSocket.class);
    private final BlockingQueue<LocalMessage> output;
    private final int port;

    public BroadcastSocket(BlockingQueue<LocalMessage> output, int port) {
        this.output = output;
        this.port = port;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            // Enable broadcast
            socket.setBroadcast(true);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LocalMessage localMessage = output.take();
                byte[] data = localMessage.getMessage();
                InetAddress address = InetAddress.getByName(""); // TODO: Broadcast address?
                DatagramPacket packet = new DatagramPacket(data, data.length, address, localMessage.getReceiverPort());
                socket.send(packet);
                log.trace("Broadcasting message to {}:{}", address, localMessage.getReceiverPort());
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }
}
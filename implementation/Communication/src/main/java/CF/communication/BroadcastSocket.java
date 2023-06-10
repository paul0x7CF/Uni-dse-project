package CF.communication;

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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                socket = new DatagramSocket(port);
                // Enable broadcast
                socket.setBroadcast(true);

                LocalMessage localMessage = output.take();
                byte[] data = localMessage.getMessage();
                InetAddress address = InetAddress.getByName(localMessage.getReceiverAddress());
                DatagramPacket packet = new DatagramPacket(data, data.length, address, localMessage.getReceiverPort());
                socket.send(packet);
                log.trace("Broadcasting message to {}:{}", address, localMessage.getReceiverPort());
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
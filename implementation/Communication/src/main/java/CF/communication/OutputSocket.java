package CF.communication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

public class OutputSocket implements Runnable {
    private static final Logger log = LogManager.getLogger(OutputSocket.class);
    private final BlockingQueue<LocalMessage> output;
    private final int port;

    public OutputSocket(BlockingQueue<LocalMessage> output, int port) {
        this.output = output;
        this.port = port;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                socket = new DatagramSocket(port);
                LocalMessage localMessage = output.take();
                byte[] data = localMessage.getMessage();
                InetAddress address = InetAddress.getByName(localMessage.getReceiverAddress());
                DatagramPacket packet = new DatagramPacket(data, data.length, address, localMessage.getReceiverPort());
                socket.send(packet);
                log.trace("Sending message to {}:{}", localMessage.getReceiverAddress(), localMessage.getReceiverPort());
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

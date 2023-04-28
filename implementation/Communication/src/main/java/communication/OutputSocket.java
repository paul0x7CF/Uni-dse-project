package communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class OutputSocket implements Runnable {
    private final BlockingQueue<LocalMessage> output;
    private final int port;

    public OutputSocket(BlockingQueue<LocalMessage> output, int port) {
        this.output = output;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (!Thread.currentThread().isInterrupted()) {
            try {
                LocalMessage localMessage = output.take();
                byte[] data = localMessage.getMessage();
                InetAddress address = InetAddress.getByName(localMessage.getReceiverAddress());
                DatagramPacket packet = new DatagramPacket(data, data.length, address, localMessage.getReceiverPort());
                socket.send(packet);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}

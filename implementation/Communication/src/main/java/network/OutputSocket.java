package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

public class OutputSocket implements Runnable {
    private final int port;
    private final BlockingQueue<byte[]> output;

    public OutputSocket(int port, BlockingQueue<byte[]> output) {
        this.port = port;
        this.output = output;
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

        while (true) {
            // Read packet from queue and create a packet.
            byte[] data = new byte[0];
            try {
                data = output.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // TODO: if data has
            DatagramPacket packet = new DatagramPacket(data, data.length);

            // Send the packet
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

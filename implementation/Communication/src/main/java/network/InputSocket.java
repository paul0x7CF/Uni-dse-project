package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class InputSocket implements Runnable {
    private final int port;
    private final BlockingQueue<byte[]> input;

    public InputSocket(int port, BlockingQueue<byte[]> input) {
        this.port = port;
        this.input = input;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            byte[] buffer = new byte[1000];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(response);
                input.put(response.getData());
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

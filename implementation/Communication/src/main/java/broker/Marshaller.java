package broker;

import protocol.Message;

import java.io.*;

public class Marshaller {
    public static byte[] marshal(Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        return baos.toByteArray();
    }

    public static Message unmarshal(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Message) ois.readObject();
    }
}
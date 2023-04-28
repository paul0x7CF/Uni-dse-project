package broker;

import protocol.Message;
import protocol.MessageBuilder;

import java.io.*;

public class Marshaller {
    public byte[] marshal(Message message) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        return baos.toByteArray();
    }

    public Message unmarshal(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Message) ois.readObject();
    }
}
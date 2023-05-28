package broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocol.Message;

import java.io.*;

public class Marshaller {
    private static final Logger log = LogManager.getLogger(Marshaller.class);
    public static byte[] marshal(Message message) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
        } catch (IOException e) {
            log.error("Could not marshal message", e);
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

    public static Message unmarshal(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bais);
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Could not unmarshal message", e);
            throw new RuntimeException(e);
        }
    }
}
package CF.broker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import CF.protocol.Message;

import java.io.*;

public class Marshaller {
    private static final Logger log = LogManager.getLogger(Marshaller.class);
    public static synchronized byte[] marshal(Message message) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        ObjectOutputStream objectOS = null;
        try {
            objectOS = new ObjectOutputStream(byteArrayOS);
            objectOS.writeObject(message);
        } catch (IOException e) {
            log.error("Could not marshal message", e);
            throw new RuntimeException(e);
        }
        return byteArrayOS.toByteArray();
    }

    public static synchronized Message unmarshal(byte[] bytes) {
        ByteArrayInputStream byteArrayIS = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream objectIS = new ObjectInputStream(byteArrayIS);
            return (Message) objectIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("Could not unmarshal message", e);
            throw new RuntimeException(e);
        }
    }
}
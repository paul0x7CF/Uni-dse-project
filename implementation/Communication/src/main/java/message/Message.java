package message;


public class Message {
    private String category;
    private String senderID;


    public Message(String category, String senderID) {
        this.category = category;
        this.senderID = senderID;
    }

    protected Message() {
    }


    @Override
    public String toString() {
        return "Message{" +
                "category='" + category + '\'' +
                ", senderID=" + senderID +
                '}';
    }
}

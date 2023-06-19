import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Meeting {
    private int id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<Participant> participantList = new ArrayList<>();


    public Meeting(int id, String title, String description, String location, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Participant> getParticipantList() {
        return participantList;
    }

    // setters
    public void addParticipant(Participant participant) {
        participantList.add(participant);
    }

}
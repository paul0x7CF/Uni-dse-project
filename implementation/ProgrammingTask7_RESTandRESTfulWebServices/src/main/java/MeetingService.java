import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MeetingService {
    private Map<Integer, Meeting> meetings = new HashMap<>();

    public MeetingService(){
        Meeting meeting1 = new Meeting(1, "Meeting 1", "Description 1", "Location 1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Meeting meeting2 = new Meeting(2, "Meeting 2", "Description 2", "Location 2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3));
        Meeting meeting3 = new Meeting(3, "Meeting 3", "Description 3", "Location 3", LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4));
        Meeting meeting4 = new Meeting(4, "Meeting 4", "Description 4", "Location 4", LocalDateTime.now().plusHours(4), LocalDateTime.now().plusHours(5));
        Meeting meeting5 = new Meeting(5, "Meeting 5", "Description 5", "Location 5", LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6));
        Meeting meeting6 = new Meeting(6, "Meeting 6", "Description 6", "Location 6", LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(7));
        Meeting meeting7 = new Meeting(7, "Meeting 7", "Description 7", "Location 7", LocalDateTime.now().plusHours(7), LocalDateTime.now().plusHours(8));
        Meeting meeting8 = new Meeting(8, "Meeting 8", "Description 8", "Location 8", LocalDateTime.now().plusHours(8), LocalDateTime.now().plusHours(9));
        Meeting meeting9 = new Meeting(9, "Meeting 9", "Description 9", "Location 9", LocalDateTime.now().plusHours(9), LocalDateTime.now().plusHours(10));

        meetings.put(meeting1.getId(), meeting1);
        meetings.put(meeting2.getId(), meeting2);
        meetings.put(meeting3.getId(), meeting3);
        meetings.put(meeting4.getId(), meeting4);
        meetings.put(meeting5.getId(), meeting5);
        meetings.put(meeting6.getId(), meeting6);
        meetings.put(meeting7.getId(), meeting7);
        meetings.put(meeting8.getId(), meeting8);
        meetings.put(meeting9.getId(), meeting9);
    }

    public void addMeeting(Meeting meeting) {
        meetings.put(meeting.getId(), meeting);
    }

    public void removeMeeting(int id) {
        meetings.remove(id);
    }

    public Map<Integer, Meeting> getMeetings() {
        return meetings;
    }

    public Meeting getMeeting(int id) {
        return meetings.get(id);
    }

    public void addParticipant(int id, String firstName, String lastName, String email) {
        Meeting meeting = meetings.get(id);
        Participant participant = new Participant(meeting.getParticipantList().size(), firstName, lastName, email);
        meeting.addParticipant(participant);
    }

}
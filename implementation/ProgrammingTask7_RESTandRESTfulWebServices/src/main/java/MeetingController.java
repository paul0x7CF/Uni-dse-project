import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MeetingController {
    private final MeetingService meetingService = new MeetingService();

    public MeetingController() {
    }

    @RequestMapping(value = "/meetings", method = RequestMethod.GET, produces = {"application/json"})
    public ResponseEntity<List<Meeting>> getMeetings() {
        if (meetingService.getMeetings().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            Map<Integer, Meeting> meetings = meetingService.getMeetings();
            return ResponseEntity.ok((List<Meeting>) meetings.values());
        }

    }

    @RequestMapping(value= "/meetings", method= RequestMethod.POST)
    public ResponseEntity<Void> addMeeting(Meeting meeting) {
        meetingService.addMeeting(meeting);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value= "/meetings/{id}", method= RequestMethod.DELETE)
    public ResponseEntity<Void> removeMeeting(int id) {
        meetingService.removeMeeting(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value= "/meetings/{id}/participants", method= RequestMethod.POST)
    public ResponseEntity<Void> addParticipant(int id, String firstName, String lastName, String email) {
        meetingService.addParticipant(id, firstName, lastName, email);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value= "/meetings/{id}", method= RequestMethod.GET)
    public ResponseEntity<Meeting> getMeeting(int id) {
        Meeting meeting = meetingService.getMeeting(id);
        return ResponseEntity.ok(meeting);
    }

    @RequestMapping(value= "/meetings/{id}/participants", method= RequestMethod.GET)
    public ResponseEntity<List<Participant>> getParticipants(int id) {
        Meeting meeting = meetingService.getMeeting(id);
        return ResponseEntity.ok(meeting.getParticipantList());
    }

    @RequestMapping(value= "/meetings/{id}/participants/{participantId}", method= RequestMethod.DELETE)
    public ResponseEntity<Void> removeParticipant(int id, int participantId) {
        Meeting meeting = meetingService.getMeeting(id);
        meeting.removeParticipant(participantId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value= "/meetings/{id}/participants/{participantId}", method= RequestMethod.GET)
    public ResponseEntity<Participant> getParticipant(int id, int participantId) {
        Meeting meeting = meetingService.getMeeting(id);
        Participant participant = meeting.getParticipant(participantId);
        return ResponseEntity.ok(participant);
    }

    @RequestMapping(value= "/meetings/{id}/participants/{participantId}", method= RequestMethod.PUT)
    public ResponseEntity<Void> updateParticipant(int id, int participantId, String firstName, String lastName, String email) {
        Meeting meeting = meetingService.getMeeting(id);
        meeting.updateParticipant(participantId, firstName, lastName, email);
        return ResponseEntity.ok().build();
    }



}

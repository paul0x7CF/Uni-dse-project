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


}

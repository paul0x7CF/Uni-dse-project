import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class MeetingTest {
    private final String address;
    private final String api = "/api";
    private final String meetings = "/meetings";

    public MeetingTest(String address) {
        this.address = address;
    }

    public void getMeetings() {
        String url = address + api + meetings;
        System.out.println("GET " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }

    public void addMeeting(String start, String end) {
        String url = api + meetings;
        System.out.println("POST " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
    }

    public void removeMeeting(int id) {
        String url = api + meetings + "/" + id;
        System.out.println("DELETE " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }

    public void addParticipant(int id, String firstName) {
        String url = api + meetings + "/" + id + "/participants";
        System.out.println("POST " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
    }

    public void getMeeting(int id) {
        String url = api + meetings + "/" + id;
        System.out.println("GET " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }

    public void getParticipants(int id) {
        String url = api + meetings + "/" + id + "/participants";
        System.out.println("GET " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
    }

    public void removeParticipant(int id, int participantId) {
        String url = api + meetings + "/" + id + "/participants/" + participantId;
        System.out.println("DELETE " + url);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}

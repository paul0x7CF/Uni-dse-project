public class TestClient {
    public static void main(String[] args) {
        final String ip = "localhost:8080/api";
        String address = "http://" + ip;

        MeetingTest meetingTest = new MeetingTest(address);

        meetingTest.addMeeting("2021-06-01T10:00:00", "2021-06-01T11:00:00");
        meetingTest.getMeetings();
        meetingTest.removeMeeting(1);
        meetingTest.getMeetings();
        meetingTest.addParticipant(2, "John");
        meetingTest.getMeetings();
        meetingTest.removeParticipant(2, 1);
    }
}

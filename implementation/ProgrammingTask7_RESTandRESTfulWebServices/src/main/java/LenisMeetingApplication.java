import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"java"})
public class LenisMeetingApplication {

    public static void main(String[] args) {
        MeetingController meetingController = new MeetingController();
        SpringApplication.run(LenisMeetingApplication.class, args);
    }
}

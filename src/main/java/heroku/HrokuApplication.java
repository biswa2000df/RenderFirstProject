package heroku;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HrokuApplication {

    public static void main(String[] args) {
        SpringApplication.run(HrokuApplication.class, args);
    }
}

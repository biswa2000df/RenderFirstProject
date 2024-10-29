package heroku.Controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@CrossOrigin(origins = "*")
public class DemoController {

    @GetMapping("/api/Welcome")
    @Operation(summary = "Welcome Data")
    public String hello() {
        return "Welcome to Biswajit Framework";
    }



    // Second API: Greet with a name
    @GetMapping("/api/greet")
    @Operation(summary = "Check your Name")
    public String greet(@RequestParam String name) {
        return "Hello, My Dear " + name + "!";
    }



    private static final String API_URL = "https://renderfirstproject.onrender.com/api/sendMail";
    private static final String Every_5MIN_API_URL = "https://renderfirstproject.onrender.com/api/Welcome";
    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(cron = "0 30 3 * * *") // Every day at 9 AM
    public void callApiAt9AM() {
        callApi();
    }

    @Scheduled(cron = "0 30 4 * * *") // Every day at 10 AM
    public void callApiAt10AM() {
        callApi();
    }

    @Scheduled(cron = "0 30 11 * * *") // Every day at 5 PM
    public void callApiAt5PM() {
        callApi();
    }

    @Scheduled(cron = "0 30 12 * * *") // Every day at 6 PM
    public void callApiAt6PM() {
        callApi();
    }


    @Scheduled(cron = "0 */5 * * * *") //
    public void callApiAtEvery5MIN() {
        continuousCallApi();
    }

    public void continuousCallApi() {
        try {
            String response = restTemplate.getForObject(Every_5MIN_API_URL, String.class);
            System.out.println("API Response: " + response);
        } catch (Exception e) {
            System.err.println("Error calling API: " + e.getMessage());
        }
    }

    public void callApi() {
        try {
            String response = restTemplate.getForObject(API_URL, String.class);
            System.out.println("API Response: " + response);
        } catch (Exception e) {
            System.err.println("Error calling API: " + e.getMessage());
        }
    }

    String mailBody = "Dear Team,\n" +
            "\n" +
            "This is a friendly reminder to ensure that you punch in when you arrive at the office and punch out before you leave for the day.\n" +
            "\n" +
            "Thank you for your cooperation!\n" +
            "\n" +
            "Best regards,\n" +
            "Biswajit sahoo\n" +
            "QA Engineer\n" +
            "Mahindra & Mahindra Financial Services Limited";

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/api/sendMail")
    @Operation(summary = "Mail Send")
    public String sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("sahoo.biswajit@mahindra.com", "namratashete38@gmail.com");
        message.setSubject("Reminder: Punch In and Out for Attendance Compliance");
        message.setText(mailBody);

        mailSender.send(message);
        return "Mail Send Successfully";
    }




}

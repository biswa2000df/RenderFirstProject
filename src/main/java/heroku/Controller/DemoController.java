package heroku.Controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


@RestController
@CrossOrigin(origins = "*")
public class DemoController {

    @GetMapping("/api/hello")
    @Operation(summary = "Welcome Data")
    public String hello() {
        return "Welcome to Biswajit Framework";
    }



    // Second API: Greet with a name
    @GetMapping("/api/greet")
    @Operation(summary = "Check your Name")
    public String greet(@RequestParam String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/run-jar")
    @Operation(summary = "Run Automation Script")
    public String runJarFile() {
        StringBuilder output = new StringBuilder();

        try {
            // Command to execute the JAR file
            String jarFilePath = System.getProperty("user.dir") + File.separator + "BiswajitJARSeleniumDockerIsworkingorNot.jar";
            System.out.println("Jar file path: " + jarFilePath);

            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarFilePath);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output from the JAR file
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            output.append("JAR exited with code: ").append(exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            return "Error running the JAR file: " + e.getMessage();
        }

        return output.toString();
    }

}

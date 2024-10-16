package heroku.Controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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

}

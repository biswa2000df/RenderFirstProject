package heroku.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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


    @Scheduled(cron = "0 */10 * * * *") //
    public void callApiAtEvery5MIN() {
        continuousCallApi();
    }

    public void continuousCallApi() {
        try {
            String response = restTemplate.getForObject(Every_5MIN_API_URL, String.class);
//            System.out.println("API Response: " + response);
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
        message.setTo("sahoo.biswajit@mahindra.com", "namratashete38@gmail.com", "deotare.sandhya@mahfin.com");
        message.setSubject("Reminder: Punch In and Out for Attendance Compliance");
        message.setText(mailBody);

        mailSender.send(message);
        return "Mail Send Successfully";
    }



    private static final String FILE_NAME = System.getProperty("user.dir") + File.separator + "data.xlsx";

    public static void writeDataToExcel(List<String> data) throws IOException {
        Workbook workbook;
        File file = new File(FILE_NAME);

        if (file.exists()) {
            workbook = new XSSFWorkbook(new FileInputStream(file));
        } else {
            workbook = new XSSFWorkbook();
            workbook.createSheet("Data");
        }

        Sheet sheet = workbook.getSheet("Data");
        int rowCount = sheet.getLastRowNum();

        Row row = sheet.createRow(++rowCount);
        for (int i = 0; i < data.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(data.get(i));
        }

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    public static InputStream getExcelFile() throws IOException {
        return new FileInputStream(FILE_NAME);
    }

    @PostMapping("/save")
    @Operation(summary = "Enter Data Daily Basic")
    public ResponseEntity<String> saveData(
            @RequestParam String Si_No,
            @RequestParam String DATE,
            @RequestParam String Work_Done,
            @RequestParam String In_Time,
            @RequestParam String Out_Time) {
        try {
            writeDataToExcel(Arrays.asList(Si_No, DATE, Work_Done, In_Time, Out_Time));
            return ResponseEntity.status(HttpStatus.CREATED).body("Data saved successfully in Excel sheet.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving data: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename:.+}") // Accept any filename including those with dots
    @Operation(summary = "Download ExcelSheet File")
    public ResponseEntity<byte[]> downloadExcel(@PathVariable String filename) {
        File file = new File(System.getProperty("user.dir") + File.separator + filename); // Specify the directory where files are stored
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if the file does not exist
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            byte[] bytes = inputStream.readAllBytes(); // Read the file into a byte array
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", file.getName()); // Set the content disposition for download
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Set the content type
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK); // Return the response entity
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle the error
        }
    }


    String uploadedFileName = "";
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator ; // Set the upload directory

    @Operation(summary = "Upload a file", description = "Uploads a file to the server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping(value = "/api/upload", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadFile(@RequestParam("file")
            MultipartFile file)
    {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }

            // Save the file locally
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.createDirectories(path.getParent()); // Create directories if not exists
            Files.write(path, file.getBytes());

            uploadedFileName = file.getOriginalFilename();
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }

    @Operation(summary = "Delete a file", description = "Deletes a specified file from the server")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(hidden = true)))
    })
    @DeleteMapping("/api/delete/{filename:.+}")
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filename);
            Files.deleteIfExists(path);
            return ResponseEntity.ok("File deleted successfully: " + filename);
        } catch (NoSuchFileException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found: " + filename);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not delete file: " + e.getMessage());
        }
    }


    @Operation(summary = "List all files", description = "Retrieves a list of all files in the upload directory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Files retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/api/listOfFiles")
    public ResponseEntity<List<String>> listFiles() {
        List<String> fileNames = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(UPLOAD_DIR))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileNames.add(path.getFileName().toString());
                }
            }
            return ResponseEntity.ok(fileNames);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    @GetMapping("/api/MotiExcelSheet/WorkingHourCount")
    @Operation(summary = "MotiExcelSheet WorkingHourCount")
    public String MotiExcelsheet() {

        String excelFilePath = System.getProperty("user.dir") + File.separator + uploadedFileName;
        try (FileInputStream fis = new FileInputStream(excelFilePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            // Access the desired sheet
            XSSFSheet sheet = workbook.getSheet("Sheet1");

            List<String> updatedTime = new ArrayList<String>();

            int i=0;//for column count


            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                if (rowIndex < 4) {
                    continue;
                }

                for (Cell cell : row) {
                    i++;
                    // Print cell values based on the cell type
                    String cellValue =  getCellValues(cell);
//                    System.out.println(cellValue);
                    if (cell.getColumnIndex() != 0) {
                        updatedTime.add(checkAndGetUpdatedTime(cellValue));
                    }
                }
                System.out.println("********** Move to the next line after each row ***********"); // Move to the next line after each row


                int lastRowNum = sheet.getLastRowNum();
//                System.out.println("lastrow number = " + lastRowNum);
                Row newRow = sheet.createRow(lastRowNum + 1);

                //create cell
                for(int k = 1; k<i; k++) {
                    newRow.createCell(k).setCellValue(updatedTime.get(k-1));
                }


                // Initialize total monthly working minutes
                double totalMonthlyWorkingMinutes = 0.0;
                for (String numberStr : updatedTime) {
                    try {
                        double number = Double.parseDouble(numberStr);
                        totalMonthlyWorkingMinutes += number;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing number: " + numberStr);
                    }
                }
                newRow.createCell(i).setCellValue(totalMonthlyWorkingMinutes);


                try (FileOutputStream fileOut = new FileOutputStream(excelFilePath)) {
                    workbook.write(fileOut);
                }
//                System.out.println(updatedTime);
//                System.out.println("Excel file updated successfully!");
                break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Total Hours updated successfully!";

    }


    private static String  getCellValues(Cell cell){
        String cellValue = "";
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getStringCellValue();

                break;
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());

                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());

                break;
            default:
                System.out.print("Unknown\t");
        }

        return cellValue;
    }


    private static String checkAndGetUpdatedTime(String cellValue){

        ArrayList<String> al = new ArrayList<String>();

        String beforespace = "";

        for(int i = 0; i< cellValue.length(); i++){
            if(cellValue.charAt(i) == '\n' )
            {
                al.add(beforespace);
                beforespace = "";
            }else{
                beforespace = beforespace + cellValue.charAt(i);
            }
        }

        if (!beforespace.isEmpty()) {
            al.add(beforespace);
        }

        String presentAndAbsent = al.get(al.size()-1);
        String totalTime = al.get(al.size()-2);

        // Define formatter to parse and format the time in "H:mm" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        // Parse the string to LocalTime
        LocalTime time = LocalTime.parse(totalTime, formatter);

        if(presentAndAbsent.equalsIgnoreCase("P")) {
            // Check if the time is less than 12:00 (noon)
            if (time.isBefore(LocalTime.NOON)) {
                // Subtract 30 minutes
                time = time.minusMinutes(30);
            }
        }
        // Convert back to string for display
        String updatedTimeString = time.format(formatter);

//        System.out.println("Updated Time: " + updatedTimeString);
        return updatedTimeString.replaceAll(":",".");
    }






}

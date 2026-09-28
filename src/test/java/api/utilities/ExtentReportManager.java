package api.utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportManager {

    private static ExtentReports extent;

    public static ExtentReports getReportInstance() {

        if (extent == null) {

            String reportPath =
                    System.getProperty("user.dir")
                            + "/test-output/ExtentReport.html";

            ExtentSparkReporter sparkReporter =
                    new ExtentSparkReporter(reportPath);

            sparkReporter.config()
                    .setDocumentTitle("FHIR API Automation Report");

            sparkReporter.config()
                    .setReportName("Patient API Test Report");

            extent = new ExtentReports();

            extent.attachReporter(sparkReporter);

            extent.setSystemInfo(
                    "Project",
                    "FHIR API Automation"
            );

            extent.setSystemInfo(
                    "Tester",
                    "Sameer"
            );

            extent.setSystemInfo(
                    "Environment",
                    "QA"
            );
        }

        return extent;
    }
}
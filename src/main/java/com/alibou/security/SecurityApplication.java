package com.alibou.security;

import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Duration;

import static com.alibou.security.user.Role.ADMIN;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(AuthenticationService service) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .username("Admin")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            System.out.println("Admin token: " + service.register(admin).getAccessToken());

            // Exécution du script Selenium
            runSeleniumTests();
        };
    }

    public void runSeleniumTests() {
        System.out.println("AngularSolar test");

        System.setProperty("webdriver.gecko.driver", "C:\\tools\\geckodriver\\geckodriver.exe");

        FirefoxOptions options = new FirefoxOptions();
        options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");

        WebDriver driver = new FirefoxDriver(options);

        try {
            String appUrl = "http://localhost:4200/";

            driver.get(appUrl);
            driver.manage().window().maximize();

            JavascriptExecutor js = (JavascriptExecutor) driver;

            int pageHeight = ((Long) js.executeScript("return document.body.scrollHeight")).intValue();
            int scrollAmount = 100;
            int currentPosition = 0;

            while (currentPosition < pageHeight) {
                js.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);
                currentPosition += scrollAmount;
                Thread.sleep(100);
            }

            Thread.sleep(500);

            currentPosition = pageHeight;
            while (currentPosition > 0) {
                js.executeScript("window.scrollBy(0, arguments[0]);", -scrollAmount);
                currentPosition -= scrollAmount;
                Thread.sleep(100);
            }

            String expectedTitle = "AngularSolar";

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            String actualTitle = driver.getTitle();

            if (expectedTitle.equals(actualTitle)) {
                System.out.println("Verification Successful - The correct title is displayed on the web page.");
            } else {
                System.out.println("Verification Failed - An incorrect title is displayed on the web page.");
            }

            WebElement addressInput = driver.findElement(By.className("adress"));
            String partialAddress = "16 Rue des Peupliers 62980";
            addressInput.clear();
            addressInput.sendKeys(partialAddress);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("pac-item")));

            WebElement firstSuggestion = driver.findElement(By.className("pac-item"));
            firstSuggestion.click();

            WebElement submitButton = driver.findElement(By.className("submit-button"));
            submitButton.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("googleMap")));

            driver.navigate().refresh();

            WebElement map = driver.findElement(By.className("googleMap"));
            Actions actions = new Actions(driver);
            actions.moveToElement(map).perform();
            actions.moveByOffset(0, 0).click().perform();

            Thread.sleep(500);

            WebElement returnToHome = driver.findElement(By.className("return-btn"));
            returnToHome.click();

            Thread.sleep(500);

            System.out.println("test ok");
        } catch (Exception e) {
            System.err.println("Erreur lors du script: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Script test exécuté avec succès.");
        }
    }
}

// This sample code supports Appium Java client >=9
// https://github.com/appium/java-client
import io.appium.java_client.remote.options.BaseOptions;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.AppiumBy;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginTest {

    private AndroidDriver driver;
    private WebDriverWait wait;
    private Connection conn;
    private CountDownLatch lock = new CountDownLatch(1);

    @BeforeEach
    public void setUp() throws MalformedURLException, SQLException {
        var options = new BaseOptions()
                .amend("platformName", "Android")
                .amend("appium:automationName", "UiAutomator2")
                .amend("appium:deviceName", "Pixel_4a_API_31:5554")
                .amend("appium:app", "C:\\Users\\HP\\Documents\\flutter\\nomnoman\\build\\app\\outputs\\flutter-apk\\app-debug.apk")
                .amend("appium:ensureWebviewsHavePages", true)
                .amend("appium:nativeWebScreenshot", true)
                .amend("appium:newCommandTimeout", 3600)
                .amend("appium:connectHardwareKeyboard", true);

        driver = new AndroidDriver(new URL("http://127.0.0.1:4723/wd/hub"), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(1));

        String url = "jdbc:postgresql://localhost/nomnoman";
        Properties props = new Properties();
        props.setProperty("user", "HP");
        props.setProperty("password", "");
        conn = DriverManager.getConnection(url, props);
    }


    @Test
    public void fieldRequired() throws InterruptedException {
        var loginButton = driver.findElement(AppiumBy.accessibilityId("Login"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();

        lock.await(1500, TimeUnit.MILLISECONDS);
        var emailValidation = driver.findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"email required\"]"));
        wait.until(ExpectedConditions.visibilityOfAllElements(emailValidation));

        var passwordValidation = driver.findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"password required\"]"));
        wait.until(ExpectedConditions.visibilityOfAllElements(passwordValidation));

        assert emailValidation.isDisplayed() && passwordValidation.isDisplayed();
    }

    @Test
    public void invalidEmailFormat() throws InterruptedException {
        var emailField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)"));
        wait.until(ExpectedConditions.elementToBeClickable(emailField));
        emailField.click();
        lock.await(1000, TimeUnit.MILLISECONDS);
        emailField.sendKeys("asdgmail.com");

        var passwordField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)"));
        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passwordField.click();
        lock.await(1000, TimeUnit.MILLISECONDS);
        passwordField.sendKeys("asdasdasd");

        var loginButton = driver.findElement(AppiumBy.accessibilityId("Login"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();

        lock.await(1200, TimeUnit.MILLISECONDS);
        var emailValidation = driver.findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"email format invalid\"]"));
        wait.until(ExpectedConditions.visibilityOfAllElements(emailValidation));

        assert emailValidation.isDisplayed();
    }

    @Test
    public void invalidPasswordFormat() throws InterruptedException {
        var emailField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)"));
        wait.until(ExpectedConditions.elementToBeClickable(emailField));
        emailField.click();
        lock.await(1000, TimeUnit.MILLISECONDS);
        emailField.sendKeys("asd@gmail.com");

        var passwordField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)"));
        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passwordField.click();
        lock.await(1000, TimeUnit.MILLISECONDS);
        passwordField.sendKeys("asdasda");

        var loginButton = driver.findElement(AppiumBy.accessibilityId("Login"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();

        lock.await(1200, TimeUnit.MILLISECONDS);
        var passwordValidation = driver.findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"must greater than 8\"]"));
        wait.until(ExpectedConditions.visibilityOfAllElements(passwordValidation));

        assert passwordValidation.isDisplayed();
    }

    @Test
    public void wrongEmailOrPassword() throws InterruptedException {
        var emailField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)"));
        wait.until(ExpectedConditions.elementToBeClickable(emailField));
        emailField.click();
        lock.await(1000, TimeUnit.MILLISECONDS);
        emailField.sendKeys("asd@gmail.co.id");

        var passwordField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)"));
        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        passwordField.click();
        lock.await(1000, TimeUnit.MILLISECONDS);
        passwordField.sendKeys("asdasdasd");

        var loginButton = driver.findElement(AppiumBy.accessibilityId("Login"));
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();

        lock.await(1200, TimeUnit.MILLISECONDS);
        var wrongToast = driver.findElement(AppiumBy.xpath("//android.view.View[@content-desc=\"wrong email or password\"]"));
        wait.until(ExpectedConditions.visibilityOfAllElements(wrongToast));

        assert wrongToast.isDisplayed();
    }

    @Test
    public void loginSuccess() throws InterruptedException, SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT id, field_type, field_value, identifier, identifier_type, is_expected FROM public.e2e WHERE id IN ('550e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440009');");
        while (rs.next()) {
            String fieldType = rs.getString("field_type");
            String fieldValue = rs.getString("field_value");
            String identifier = rs.getString("identifier");
            String identifierType = rs.getString("identifier_type");
            boolean isExpected = rs.getBoolean("is_expected");
            By findBy = null;

            if (identifierType.equals("androidUIAutomator")) {
                findBy = AppiumBy.androidUIAutomator(identifier);
            }

            if (identifierType.equals("accessibilityId")) {
                findBy = AppiumBy.accessibilityId(identifier);
            }

            if (isExpected) {
                lock.await(1200, TimeUnit.MILLISECONDS);
            }

            var element = driver.findElement(findBy);
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();

            if (fieldType.equals("TEXT_FIELD")) {
                lock.await(1000, TimeUnit.MILLISECONDS);
                element.sendKeys(fieldValue);
            }

            if (isExpected) {
                assert element.isDisplayed();
            }
        }

        rs.close();
        st.close();

//        var emailField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(0)"));
//        wait.until(ExpectedConditions.elementToBeClickable(emailField));
//        emailField.click();
//        lock.await(1000, TimeUnit.MILLISECONDS);
//        emailField.sendKeys("asd@gmail.com");
//
//        var passwordField = driver.findElement(AppiumBy.androidUIAutomator("new UiSelector().className(\"android.widget.EditText\").instance(1)"));
//        wait.until(ExpectedConditions.elementToBeClickable(passwordField));
//        passwordField.click();
//        lock.await(1000, TimeUnit.MILLISECONDS);
//        passwordField.sendKeys("asdasdasd");
//
//        var loginButton = driver.findElement(AppiumBy.accessibilityId("Login"));
//        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
//        loginButton.click();
//
//        lock.await(1200, TimeUnit.MILLISECONDS);
//        var welcomeElm = driver.findElement(AppiumBy.accessibilityId("Welcome"));
//        wait.until(ExpectedConditions.visibilityOfAllElements(welcomeElm));
//
//        assert welcomeElm.isDisplayed();
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }
}


package SmileOne;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class preservedLogin_Firefox {
    private static Logger logger = Logger.getLogger(SmileOneBot.class);

    private static WebDriver driver ;
    private static Map<String, Object> vars = new HashMap<String, Object>();
    private static JavascriptExecutor js;

    public static void main(String[] args) {

//        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
//        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");
        System.setProperty("webdriver.gecko.driver","webdrivers\\geckodriver027.exe");


//        ChromeDriverService chSvc = new ChromeDriverService.Builder()
//                .usingDriverExecutable(new File("webdrivers\\chromedriver.exe")).usingAnyFreePort().build();
//                .usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe")).usingAnyFreePort().build();


//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 7");
//        options.addArguments("--incognito");


        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("botSmileOne");


        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setProfile(myprofile);

        driver = new FirefoxDriver(firefoxOptions);
//        driver = new ChromeDriver(chSvc, options);
//        driver = new ChromeDriver(options);

        js = (JavascriptExecutor) driver;

        try {
            /*START LOGIN*/
            logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

            driver.get("https://www.smile.one/customer/account/login");
            driver.manage().window().setSize(new Dimension(1435, 749));
            vars.put("window_handles", driver.getWindowHandles());

            waitForProcess(1500);
            driver.findElement(By.cssSelector(".google > .login_method_p2")).click();

            vars.put("win1110", waitForWindow(2000));
            vars.put("root", driver.getWindowHandle());
            driver.switchTo().window(vars.get("win1110").toString());


            logger.log(Level.INFO, "/*-------------PURCHASE PROCESS FINISHED---------------*/");
            /*-------------END PURCHASE-----------------*/


            /*Purchasing*/

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            /*driver.quit();*/
        }
    }

    private static String waitForWindow(int timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Set<String> whNow = driver.getWindowHandles();
        Set<String> whThen = (Set<String>) vars.get("window_handles");
        if (whNow.size() > whThen.size()) {
            whNow.removeAll(whThen);
        }
        return whNow.iterator().next();
    }

    private static void waitForProcess(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

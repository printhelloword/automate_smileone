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

        System.setProperty("webdriver.gecko.driver","webdrivers\\geckodriver027.exe");

        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("botSmileOne");

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setProfile(myprofile);

        driver = new FirefoxDriver(firefoxOptions);
        js = (JavascriptExecutor) driver;

        try {
            /*START LOGIN*/
            logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

            driver.get("https://www.smile.one/customer/account/login");

            driver.manage().window().setSize(new Dimension(1547, 950));
//            driver.manage().window().setPosition(new Point(0,0));

            vars.put("window_handles", driver.getWindowHandles());

            waitForWindow(1500);
            driver.findElement(By.cssSelector(".google")).click();

            vars.put("win9253", waitForWindow(2000));
            vars.put("root", driver.getWindowHandle());
            driver.switchTo().window(vars.get("root").toString());

            logger.log(Level.INFO, "/*-------------PURCHASE PROCESS FINISHED---------------*/");
            /*END LOGIN*/

            while(true){
                try{
                    driver.findElement(By.cssSelector(".listol1 > .list-li:nth-child(3) .imgicon"));
                    logger.log(Level.INFO, "FOUND MLBB MENU");
                    driver.navigate().to("https://www.smile.one/merchant/mobilelegends?source=other");
                    break;
                }catch (Exception e){
                    logger.log(Level.INFO, "MLBB MENU NOT FOUND.. STILL LOOKING FOR");
                    e.printStackTrace();
                }
                Thread.sleep(2000);
            }


            /*MOVE TO MLBB WINDOW*/
            vars.put("win3322", waitForWindow(2000));
            driver.switchTo().window(vars.get("win3322").toString());
            /*END MOVE TO MLBB WINDOW*/

            /*Checking for MLBB Page Fully Loaded*/
            while (true) {
                try {
                    driver.findElement(By.cssSelector(".PcliF-em2")).click();
                    SmileOneBot.logger.info( "FOUND SELECTED DENOM.. MLBB PAGE HAS BEEN LOADED IS CONFIRMED");
                    break;
                } catch (Exception e) {
                    SmileOneBot.logger.info("DIAMOND DENOM NOT FOUND.. STILL LOOKING FOR");
                }
                Thread.sleep(1000);
            }

            /*Adding elements to Array For Checking Later*/
            ArrayList<WebElement> webElements = new ArrayList<>();
            webElements.add(driver.findElement(By.cssSelector(".PcliF-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliS-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliT-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliFo-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliFif-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliSix-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliSev-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliEig-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliNin-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliEle-em2")));
            webElements.add(driver.findElement(By.cssSelector(".PcliTwl-em2")));

            /*Start Checking for each denom and print value in log*/
            for(int i = 0; i < webElements.size(); i++)
            {
                logger.log(Level.INFO, "PRINTING objects VALUE");
                System.out.println(webElements.get(i).getAttribute("class"));
                webElements.get(i).click();

                Thread.sleep(1500);
            }

            logger.log(Level.INFO, "/*-------------PRODUCT CHECKING START---------------*/");

                driver.findElement(By.id("puseid")).click();
                driver.findElement(By.id("puseid")).sendKeys("126606687");
                driver.findElement(By.id("pserverid")).sendKeys("2632");
                driver.findElement(By.cssSelector(".PcDiamant-ul > .fl:nth-child(1)")).click();
                driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
//                driver.findElement(By.id("Nav-btnpc")).click();


            logger.log(Level.INFO, "/*-------------PURCHASE PROCESS FINISHED---------------*/");

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

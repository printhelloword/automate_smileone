package SmileOne;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class login_purchase_VK {
    private static Logger logger = Logger.getLogger(SmileOneBot.class);

    private static WebDriver driver ;
    private static Map<String, Object> vars = new HashMap<String, Object>();
    private static JavascriptExecutor js;

    public static void main(String[] args) {

//        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");

        ChromeDriverService chSvc = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("webdrivers\\chromedriver.exe")).usingAnyFreePort().build();
//                .usingDriverExecutable(new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe")).usingAnyFreePort().build();

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 5");
        options.addArguments("--incognito");

//        driver = new FirefoxDriver();
        driver = new ChromeDriver(chSvc, options);

        js = (JavascriptExecutor) driver;

        try {
            /*START LOGIN*/
            logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

            driver.get("https://www.smile.one/customer/account/login");

            driver.manage().window().setSize(new Dimension(1547, 950));

            /*HANDLE NEW WINDOWS AFTER DRIVER.GET*/
            vars.put("window_handles", driver.getWindowHandles());
            /*END HANDLE NEW WINDOWS AFTER DRIVER.GET*/

            driver.findElement(By.cssSelector(".vk > .login_method_p2")).click();

            /*SWITCH & HANDLE POPUP LOGIN WINDOWS*/
            vars.put("win2423", waitForWindow(2000));
            vars.put("root", driver.getWindowHandle());
            driver.switchTo().window(vars.get("win2423").toString());
            /*END SWITCH & HANDLE POPUP LOGIN WINDOWS*/

            /*START LOGIN PROCESS*/
            driver.findElement(By.name("email")).click();
            driver.findElement(By.name("email")).sendKeys("botsmileone@gmail.com");
            driver.findElement(By.name("pass")).sendKeys("vk1101");
            driver.findElement(By.name("pass")).sendKeys(Keys.ENTER);
            /*END LOGIN PROCESS*/

            /*SWITCH TO NEW REDIRECTED PAGE*/
//            driver.close();
            driver.switchTo().window(vars.get("root").toString());
//            driver.close();

            logger.log(Level.INFO, "/*-------------LOGIN PROCESS FINISHED---------------*/");
            /*END LOGIN*/

            /*NAVIGATE TO MLBB PAGE AFTER LOGGED IN*/
            driver.navigate().to("https://www.smile.one/merchant/mobilelegends?source=other");

            /*PROCESS DIVIDER*/

            /*START PURCHASE*/
            logger.log(Level.INFO, "/*-------------START PURCHASE PROCESS---------------*/");

            /*Manual Checking Loaded Home Page*/
//            while(true){
//                try{
//                    driver.findElement(By.cssSelector(".listol1 > .list-li:nth-child(3) .imgicon")).click();
//                    logger.log(Level.INFO, "FOUND MLBB MENU");
//                    break;
//                }catch (Exception e){
//                    logger.log(Level.INFO, "MLBB MENU NOT FOUND.. STILL LOOKING FOR");
//                    /*e.printStackTrace();*/
//                }
//                Thread.sleep(2000);
//            }
            /*Manual Checking Loaded Home Page*/

            /*MOVE TO MLBB WINDOW*/
            vars.put("win3322", waitForWindow(2000));
            driver.switchTo().window(vars.get("win3322").toString());
            /*END MOVE TO MLBB WINDOW*/

            /*MANUAL CHECKING FOR LOADED MLBB PAGE*/
            while(true){
                try{
                    driver.findElement(By.cssSelector(".PcliF-em2"));
                    logger.log(Level.INFO, "FOUND DIAMOND DENOM.. MLBB PAGE IS LOADED CONFIRMED");
                    break;
                }catch (Exception e){
                    logger.log(Level.INFO, "DIAMOND DENOM NOT FOUND.. STILL LOOKING FOR");
                }
                Thread.sleep(1000);
            }
            /*MANUAL CHECKING FOR LOADED MLBB PAGE*/


            /*ADDING DENOMSTO ARRAYLIST*/
//            ArrayList<WebElement> webElements = new ArrayList<>();
//            webElements.add(driver.findElement(By.cssSelector(".PcliF-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliS-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliT-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliFo-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliFif-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliSix-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliSev-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliEig-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliNin-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliEle-em2")));
//            webElements.add(driver.findElement(By.cssSelector(".PcliTwl-em2")));

            /*LOCATE EACH DENOM AND PRINT ITS VALUE*/
//            for(int i = 0; i < webElements.size(); i++)
//            {
//                logger.log(Level.INFO, "PRINTING objects VALUE");
//                System.out.println(webElements.get(i).getAttribute("class"));
//                webElements.get(i).click();
//
//                Thread.sleep(1500);
//            }

            /*CHECKING FOR PRODUCTS*/
            logger.log(Level.INFO, "/*-------------PRODUCT CHECKING START---------------*/");

            driver.findElement(By.id("puseid")).click();
            driver.findElement(By.id("puseid")).sendKeys("126606687");
            driver.findElement(By.id("pserverid")).sendKeys("2632");
            driver.findElement(By.cssSelector(".PcDiamant-ul > .fl:nth-child(1)")).click();
            driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
            driver.findElement(By.id("Nav-btnpc")).click();

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

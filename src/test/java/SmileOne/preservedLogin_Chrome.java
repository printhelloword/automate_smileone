package SmileOne;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class preservedLogin_Chrome {
    private static Logger logger = Logger.getLogger(SmileOneBot.class);

    private static WebDriver driver ;
    private static Map<String, Object> vars = new HashMap<String, Object>();

    public static void main(String[] args) {
//        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver85x86.exe");
        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");

        String chromeProfilePath = "C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 7";

        ChromeOptions chromeProfile = new ChromeOptions();
        chromeProfile.addArguments("user-data-dir=" + chromeProfilePath);
        chromeProfile.addArguments("profile-directory=Profile 6");
//        chromeProfile.setBinary("C:\\Program Files (x86)\\Google\\Chrome\\Application");

//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 7");

        driver = new ChromeDriver(chromeProfile);

        try {
            /*-------------START LOGIN-----------------*/
            logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

//            driver.get("https://www.smile.one/customer/account/login");
            driver.get("https://www.google.com");

//            driver.manage().window().setSize(new Dimension(1397, 820));
//            vars.put("window_handles", driver.getWindowHandles());
//
//            driver.findElement(By.cssSelector(".google")).click();
//
//            vars.put("win5733", waitForWindow(2000));
//            vars.put("root", driver.getWindowHandle());
//            driver.switchTo().window(vars.get("win5733").toString());

            logger.log(Level.INFO, "/*-------------LOGIN PROCESS FINISHED---------------*/");
            /*-------------END LOGIN-----------------*/


            /*---------------------------------PROCESS DIVIDER-------------------------------------*/


            /*-------------START PURCHASE-----------------*/
            logger.log(Level.INFO, "/*-------------START PURCHASE PROCESS---------------*/");

//            waitForProcess(3000);
//            vars.put("window_handles", driver.getWindowHandles());

            while(true){
                try{
                    driver.findElement(By.cssSelector(".listol1 > .list-li:nth-child(3) .imgicon")).click();
                    logger.log(Level.INFO, "FOUND MLBB MENU");
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


            /*START CHECKING FOR PRODUCTS*/
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


            for(int i = 0; i < webElements.size(); i++)
            {
                logger.log(Level.INFO, "PRINTING objects VALUE");
                System.out.println(webElements.get(i).getAttribute("class"));
                webElements.get(i).click();

                Thread.sleep(1500);
            }
            /*END CHECKING FOR PRODUCTS*/

//            logger.log(Level.INFO, "/*-------------PURCHASE PROCESS FINISHED---------------*/");
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

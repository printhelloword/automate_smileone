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
//        System.setProperty("webdriver.chrome.driver","D:\\SpringBoot\\`VoucherGame\\webdrivers\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 3");
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
//        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

        try {
            /*-------------START LOGIN-----------------*/
            logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

            driver.get("https://www.smile.one/customer/account/login");

            driver.manage().window().setSize(new Dimension(1547, 950));
            driver.manage().window().setPosition(new Point(0,0));

            vars.put("window_handles", driver.getWindowHandles());
            driver.findElement(By.cssSelector(".google")).click();

            vars.put("win9253", waitForWindow(2000));
            vars.put("root", driver.getWindowHandle());
            driver.switchTo().window(vars.get("win9253").toString());
            driver.findElement(By.id("identifierId")).click();
            driver.findElement(By.id("identifierId")).sendKeys("botsmileone");
            driver.findElement(By.id("identifierId")).sendKeys(Keys.ENTER);

            waitForProcess(2000);

            driver.findElement(By.name("password")).sendKeys("vassa8888");
            driver.findElement(By.name("password")).sendKeys(Keys.ENTER);


            driver.switchTo().window(vars.get("root").toString());
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



            /*CHECKING FOR PRODUCTS*/
//            logger.log(Level.INFO, "/*-------------PRODUCT CHECKING START---------------*/");
//
//
//                driver.findElement(By.id("puseid")).click();
//                driver.findElement(By.id("puseid")).sendKeys("126606687");
//                driver.findElement(By.id("pserverid")).sendKeys("2632");
//                driver.findElement(By.cssSelector(".PcDiamant-ul > .fl:nth-child(1)")).click();
//                driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
//                driver.findElement(By.id("Nav-btnpc")).click();
//
//
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

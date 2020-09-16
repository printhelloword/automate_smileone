package SmileOne.controller;

import org.apache.log4j.Level;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class TransactionController {


    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TransactionController.class);

    private static WebDriver driver ;
    private static Map<String, Object> vars = new HashMap<String, Object>();

    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.password}")
    private String botPassword;

    @Value("${denom.1}")
    private String denom1;
    @Value("${denom.2}")
    private String denom2;
    @Value("${denom.3}")
    private String denom3;
    @Value("${denom.4}")
    private String denom4;
    @Value("${denom.5}")
    private String denom5;
    @Value("${denom.6}")
    private String denom6;
    @Value("${denom.7}")
    private String denom7;
    @Value("${denom.8}")
    private String denom8;
    @Value("${denom.9}")
    private String denom9;
    @Value("${denom.10}")
    private String denom10;
    @Value("${denom.11}")
    private String denom11;



    /*@GetMapping("/trx/{idPlayer}/{denom}/{trxId}")*/  // --> Technical Spec
    @GetMapping("/trx/{playerId}/{zoneId}/{denom}")  // -> Developer
    // "/trx/<idplayer>/<denom>/<trx_id>"
    public String startPurchase(
            @PathVariable String playerId,
            @PathVariable String zoneId,
            @PathVariable String denom
    ) throws IOException {

        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        String response = null;

        driver = new ChromeDriver(options);
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
            driver.findElement(By.id("identifierId")).sendKeys(botUsername);
            driver.findElement(By.id("identifierId")).sendKeys(Keys.ENTER);

            waitForProcess(2000);

            driver.findElement(By.name("password")).sendKeys(botPassword);
            driver.findElement(By.name("password")).sendKeys(Keys.ENTER);


            driver.switchTo().window(vars.get("root").toString());
            logger.log(Level.INFO, "/*-------------LOGIN PROCESS FINISHED---------------*/");
            /*-------------END LOGIN-----------------*/



            /*---------------------------------PROCESS DIVIDER-------------------------------------*/


            /*-------------START PURCHASE-----------------*/
            logger.log(Level.INFO, "/*-------------START PURCHASE PROCESS---------------*/");

            while(true){
                try{
                    driver.findElement(By.cssSelector(".listol1 > .list-li:nth-child(3) .imgicon")).click();
                    logger.log(Level.INFO, "FOUND MLBB MENU.. HOMEPAGE HAS BEEN LOADED IS CONFIRMED");
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
                    response = driver.findElement(By.cssSelector(denom)).getAttribute("class");
                    driver.findElement(By.cssSelector(denom)).click();
                    logger.log(Level.INFO, "FOUND SELECTED DENOM.. MLBB PAGE HAS BEEN LOADED IS CONFIRMED");
                    break;
                }catch (Exception e){
                    logger.log(Level.INFO, "DIAMOND DENOM NOT FOUND.. STILL LOOKING FOR");
                }
                Thread.sleep(1000);
            }


            driver.findElement(By.id("puseid")).click();
            driver.findElement(By.id("puseid")).sendKeys(playerId);
            driver.findElement(By.id("pserverid")).sendKeys(zoneId);
            driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
            driver.findElement(By.id("Nav-btnpc")).click();


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            /*driver.quit();*/
        }
        return response;
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

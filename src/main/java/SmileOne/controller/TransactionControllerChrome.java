package SmileOne.controller;

import SmileOne.SmileOneBot;
import SmileOne.helper.DBInboxes;
import SmileOne.helper.DBOutboxes;
import SmileOne.model.Inboxes;
import SmileOne.model.Outboxes;
import SmileOne.pojo.ResponsePojo;
import SmileOne.pojo.VoucherPojo;
import org.apache.log4j.Level;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class TransactionControllerChrome {

    private static WebDriver driver ;
    private static Map<String, Object> vars = new HashMap<String, Object>();

    private static final String status00 = "Success";
    private static final String status01 = "Error";
    private static final String auto_close = "1"; //1 = auto close browser = on

    @Value("${bot.username}")
    private String botUsername;
    @Value("${bot.password}")
    private String botPassword;

    @Value("${wait.time}")
    private String waitTime;

    @Value("${auto.close}")
    private String autoClose;

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

    @GetMapping("/trx/{playerId}/{denom}/{trxId}")
    public String startPurchase(
            @PathVariable String playerId,
            @PathVariable String denom,
            @PathVariable String trxId,
            HttpServletRequest request
    ) throws IOException {

        String accountId, zoneId;
        Integer currentInboxID = null;

        String responseMsg=null;
        String responsePlayerID=null;
        String responseDenom=null;
        String responseTrxid=null;
        String responseStatus=status01; //initialize with Error status

        String response = null;
        JSONObject jsonObject = null;

        /*initialize Webdrivers for each browser*/
        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");
        System.setProperty("webdriver.gecko.driver","webdrivers\\geckodriver.exe");
        System.setProperty("webdriver.ie.driver","webdrivers\\IEDriverServer.exe");

        /*option start chrome incognito mode*/
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");

        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setCapability("browser.privatebrowsing.autostart", true);


        /*DB Helpers*/
        DBInboxes dbInboxes = new DBInboxes();
        DBOutboxes dbOutboxes = new DBOutboxes();

        /*initialize response message with null values*/
        VoucherPojo voucherPojo = new VoucherPojo(responsePlayerID, responseDenom);
        ResponsePojo responsePojo = new ResponsePojo(null, responseMsg, responseTrxid, responseStatus);

        /*Input validation*/
        if (!isTrxIdValid(trxId)) { //trxId Validation
            responseMsg = "Trx ID tidak Valid";
//            responseMsg = "Invalid Trx ID";
            responsePojo.setMessage(responseMsg);
        }
        else {
            if (dbInboxes.isTrxIdExists(trxId)) { //check for duplicate trxId
                responseMsg = "Trx id Sudah terdapat di Dababase";
//                responseMsg = "Same Trx ID found in the database";
                responsePojo.setMessage(responseMsg);
            }
            else{
                if(getDenomELementLocator(denom)==null) {
                    responseMsg = "Denom tidak valid";
//                    responseMsg = "Invalid Denom";
                    responsePojo.setMessage(responseMsg);
                }
                else{
                    if(!isNumeric(playerId) || playerId.length()>13){
                        responseMsg="Player ID tidak valid";
//                        responseMsg="Invalid Player ID";
                        responsePojo.setMessage(responseMsg);
                    }else{

                        accountId = playerId.substring(0, playerId.length()-4);
                        zoneId = playerId.substring(playerId.length()-4);

                        try {

                            /*Saving Inbox*/
                            try {
                                SmileOneBot.logger.info("Incoming Request with URI: "+request.getRequestURI());

                                /*Saving to Inbox*/
                                String message = trxId + "#" + denom + "#" + playerId;
                                Date date = new Date();
                                Inboxes inbox = new Inboxes(message, "localhost:8080/trx", 0, date, trxId);
                                SmileOneBot.logger.info("Saving Request to Inbox");
                                currentInboxID = dbInboxes.saveInbox(inbox);
                            }
                            catch (Exception e){
                                SmileOneBot.logger.info("---------Fail Save Inbox to DB----------");
                                e.printStackTrace();
                                SmileOneBot.logger.info("---------Fail Save Inbox to DB----------");
                            }
                            /*END CHECKING&SAVING INBOX*/

                            driver = new ChromeDriver(options);
//                            driver = new FirefoxDriver(firefoxOptions);
//                            driver = new InternetExplorerDriver();

                            Thread.sleep(Integer.parseInt(waitTime));

                            /*START LOGIN*/
                            SmileOneBot.logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

                            driver.get("https://www.smile.one/customer/account/login");
//                            driver.get("https://www.smile.one/");

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
                            driver.findElement(By.name("email")).sendKeys(botUsername);
                            driver.findElement(By.name("pass")).sendKeys(botPassword);
                            driver.findElement(By.name("pass")).sendKeys(Keys.ENTER);
                            /*END LOGIN PROCESS*/

                            /*Handle Switch to HomePage after Loggedin*/
                            //driver.close();
                            driver.switchTo().window(vars.get("root").toString());
                            //driver.close();

                            SmileOneBot.logger.log(Level.INFO, "/*-------------LOGIN PROCESS FINISHED---------------*/");
                            /*End Handle Switch to HomePage after Loggedin*/

                            /*Navigate to MLBB Page from Homepage after logged in*/

                            SmileOneBot.logger.log(Level.INFO, "Logged in. Navigating to MLBB Page");
                            driver.navigate().to("https://www.smile.one/merchant/mobilelegends?source=other");
                            /*START PURCHASE*/
                            SmileOneBot.logger.log(Level.INFO, "/*-------------START TOP-UP PROCESS---------------*/");

                            /*Checking for Page Fully Loaded*/
                            /*while (true) {
                                try {
                                    driver.findElement(By.cssSelector(".listol1 > .list-li:nth-child(3) .imgicon")).click();
                                    logger.info("FOUND MLBB MENU.. HOMEPAGE HAS BEEN LOADED IS CONFIRMED");
                                    break;
                                } catch (Exception e) {
                                    logger.info("MLBB MENU NOT FOUND.. HOMEPAGE NOT FULLY LOADED YET");
                                }
                                Thread.sleep(2000);
                            }*/

                            /*Switch to MLBB Page*/
                            vars.put("win3322", waitForWindow(2000));
                            driver.switchTo().window(vars.get("win3322").toString());
                            /*End Switch to MLBB Page*/

                            /*Validating for Page Fully Loaded*/

                            /*Checking for MLBB Page Fully Loaded*/
//                            while (true) {
                            int maxRetry=5;
                            int retryInterval=2000; //ms

                            while(true){
//                            for(int i=0; i<maxRetry; i++){
                                try {
                                    driver.findElement(By.cssSelector(getDenomELementLocator(denom))).click();
                                    SmileOneBot.logger.info( "Selecting Denom");
                                    break;
                                } catch (Exception e) {
                                    SmileOneBot.logger.info("Waiting for MLBB page in readyState");
                                }
                                Thread.sleep(retryInterval);
                            }

                            driver.findElement(By.id("puseid")).click();
                            SmileOneBot.logger.info( "Inserting Player ID");
                            driver.findElement(By.id("puseid")).sendKeys(accountId);
                            SmileOneBot.logger.info( "Inserting Zone ID");
                            driver.findElement(By.id("pserverid")).sendKeys(zoneId);
                            SmileOneBot.logger.info( "Selecting Payment Type");
                            driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
                            driver.findElement(By.id("Nav-btnpc")).click();
                            SmileOneBot.logger.info( "Finished Inputs.. Now Processing Top-up...");

                            if (driver.getCurrentUrl().equalsIgnoreCase("https://www.smile.one/merchant/mobilelegends?source=other")) {
                                WebDriverWait wait = new WebDriverWait(driver, 5);
                                /*Next try catch try to validates whether alert is present(player id does not exist) / payment is success or failed */
                                try {
                                    wait.until(ExpectedConditions.alertIsPresent());
                                    String alrt = driver.switchTo().alert().getText();
                                    SmileOneBot.logger.info("Found Alert with message : " + alrt);
                                    SmileOneBot.logger.info("Player " + accountId + "(" + zoneId + ") Does Not Exist");
                                    responseMsg = "Akun Player tidak di temukan";
//                                    responseMsg = "Player Does not Exist";
                                } catch (Exception e) {
                                    SmileOneBot.logger.info("Player: "+accountId+" ("+zoneId+") di temukan. Melanjutkan top-up...");
//                                    SmileOneBot.logger.info("Player "+accountId+" ("+zoneId+") exists. Countinue top-up...");
                                    /*this statement validates if balance is insufficient*/
                                    if (driver.getCurrentUrl().equalsIgnoreCase("https://www.smile.one/customer/recharge")) {
                                        SmileOneBot.logger.info("Insufficient Balance");
                                        responseMsg = "Saldo tidak mencukupi";
//                                        responseMsg = "Insuficient Balance";
                                    }
                                    /*this statement validates if purchase is successful*/
                                    else if (driver.getCurrentUrl().equalsIgnoreCase("https://www.smile.one/message/success")) {
                                        SmileOneBot.logger.info("");
                                        responseMsg = "Top-up Berhasil";
//                                        responseMsg = "Top-up Successful";
                                        responseStatus = status00;
                                    }
                                }
                            }

                            /* Create Json Response (success purchase) */

                            Thread.sleep(2000);

                        } catch (Exception e) {
                            e.printStackTrace();
                            SmileOneBot.logger.info("Error Found during purchase. Closing Browser Now");
                            if(autoClose.equalsIgnoreCase(auto_close))
                                driver.quit();
                        } finally {
                            SmileOneBot.logger.info("Closing Browser Now");
                            if(autoClose.equalsIgnoreCase(auto_close))
                                driver.quit();
                        }

                        /*Start Generating Response JSON*/
                        voucherPojo.setPlayerId(playerId);
                        voucherPojo.setDenom(denom);

                        responsePojo.setVoucher(voucherPojo);
                        responsePojo.setMessage(responseMsg);
                        responsePojo.setTrxId(trxId);
                        responsePojo.setStatus(responseStatus);

                    } //End if player ID is valid (last validation)

                } //End if denom is valid

            } //End if trxID is not in DB (yet)

        } //End if trxID is valid

        jsonObject =  new JSONObject(responsePojo);
        response = jsonObject.toString();

        SmileOneBot.logger.info("Process finished with response:\n"+jsonObject.toString(4));

        /*Saving to Outbox*/
        if (currentInboxID!=null) {
            SmileOneBot.logger.info("Saving to Outbox with inbox_id : "+currentInboxID);
            try {
                /*SAVING TO OUTBOX*/
                SmileOneBot.logger.info("Saving Request to Outbox");
                Date date = new Date();
                Outboxes outboxes = new Outboxes(response, null, date, currentInboxID);
                dbOutboxes.saveOutbox(outboxes);
            } catch (Exception e) {
                SmileOneBot.logger.info("---------Fail Save Outbox to DB----------");
                e.printStackTrace();
                SmileOneBot.logger.info("---------Fail Save Outbox to DB----------");
            }
        }
        /*End Saving to Outbox*/

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

//    private static void waitForProcess(int time){
//        try {
//            Thread.sleep(time);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private boolean isTrxIdValid(String input) {
        boolean result=false;
        if (input!=null){
            try {
                Long.parseLong(input);
                result=true;
            } catch (Exception e) {
                SmileOneBot.logger.info("Invalid TrxId");
            }
        }
        return result;
    }

    private boolean isNumeric(String input) {
        boolean result=false;
        if (input!=null){
            try {
                Long.parseLong(input);
                result=true;
            } catch (Exception e) {
                SmileOneBot.logger.info("Player ID + Zone ID Input not valid");
            }
        }
        return result;
    }

    private String getDenomELementLocator(String denomInput) {
        String denomResult;

        switch(denomInput) {
            case "86":
                denomResult=denom1;
                break;
            case "172":
                denomResult=denom2;
                break;
            case "257":
                denomResult=denom3;
                break;
            case "706":
                denomResult=denom4;
                break;
            case "2195":
                denomResult=denom5;
                break;
            case "3688":
                denomResult=denom6;
                break;
            case "5532":
                denomResult=denom7;
                break;
            case "9288":
                denomResult=denom8;
                break;
            case "starlight":
                denomResult=denom9;
                break;
            case "twilight":
                denomResult=denom10;
                break;
            case "starlightplus":
                denomResult=denom11;
                break;
            default:
                denomResult=null;
                SmileOneBot.logger.info("INVALID DENOM IN CURRENT REQUEST : "+denomInput);
        }

        return denomResult;
    }


}

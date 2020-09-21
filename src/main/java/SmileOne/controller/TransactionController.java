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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
public class TransactionController {

    private static WebDriver driver ;
    private static Map<String, Object> vars = new HashMap<String, Object>();

    private static final String status00 = "Success";
    private static final String status01 = "Error";

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
    @GetMapping("/trx/{playerId}/{denom}/{trxId}")  // -> Developer
    // "/trx/<idplayer>/<denom>/<trx_id>"
    public String startPurchase(
            @PathVariable String playerId,
            @PathVariable String denom,
            @PathVariable String trxId
    ) throws IOException {

        String accountId, zoneId;
        Integer currentInboxID = null;

        System.setProperty("webdriver.chrome.driver","webdrivers\\chromedriver.exe");
        System.setProperty("webdriver.firefox.driver","webdrivers\\geckodriver.exe");
        System.setProperty("webdriver.ie.driver","webdrivers\\IEDriverServer.exe");

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 5");
        options.addArguments("--incognito");

        String response = null;

        JSONObject jsonObject = null;

        DBInboxes dbInboxes = new DBInboxes();
        DBOutboxes dbOutboxes = new DBOutboxes();

        /*GENERATING RESPONSE MESSAGE*/
        VoucherPojo voucherPojo = new VoucherPojo(null,null);
        voucherPojo.setPlayerId(null);
        voucherPojo.setDenom(null);

        ResponsePojo responsePojo = new ResponsePojo(null, null, null, status01);

        /*Input validation*/
        if (!isTrxIdValid(trxId)) { //trxId Validation
            response = "INVALID TRX ID";
            responsePojo.setMessage(response);
        }
        else if (dbInboxes.isTrxIdExists(trxId)) { //check for duplicate trxId
            response = "SAME TRX ID FOUND IN THE DATABASE";
            responsePojo.setTrxId(trxId);
            responsePojo.setMessage(response);;
        }
        else if(!isNumeric(playerId) || playerId.length()!=13) { //playerId Validation
            response = "INVALID PLAYER ID";
            responsePojo.setTrxId(trxId);
            responsePojo.setMessage(response);
        }
        else if(getDenomELementLocator(denom)==null) {
            response = "INVALID DENOM";
            responsePojo.setMessage(response);
        }
        else if (isNumeric(playerId) && playerId.length()==13) {
            accountId = playerId.substring(0, 9);
            zoneId = playerId.substring(9);

            try {

                /*CHECKING&SAVING INBOX*/
                try {
                    /*SAVING TO INBOX*/
                    SmileOneBot.logger.info("Saving Request to Inbox");
//                    dbInboxes = new DBInboxes();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String message = trxId + "#" + denom + "#" + playerId;
                    Inboxes inbox = new Inboxes(message, "localhost:8080/trx", 0, date, trxId);
                    currentInboxID = dbInboxes.saveInbox(inbox);
                }
                catch (Exception e){
                    SmileOneBot.logger.info("---------Fail Save to DB----------");
                    e.printStackTrace();
                }
                /*END CHECKING&SAVING INBOX*/

                driver = new ChromeDriver(options);
//                driver = new FirefoxDriver();
//                driver = new InternetExplorerDriver();

                /*START LOGIN*/
                SmileOneBot.logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

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

                /*SWITCH TO NEW REDIRECTED PAGE AFTER LOGGED IN*/
                //driver.close();
                driver.switchTo().window(vars.get("root").toString());
                //driver.close();

                SmileOneBot.logger.log(Level.INFO, "/*-------------LOGIN PROCESS FINISHED---------------*/");
                /*END LOGIN*/

                /*NAVIGATE TO MLBB PAGE AFTER LOGGED IN*/
                driver.navigate().to("https://www.smile.one/merchant/mobilelegends?source=other");

                /*START PURCHASE*/
                SmileOneBot.logger.log(Level.INFO, "/*-------------START PURCHASE PROCESS---------------*/");

                /*Checking for Page Fully Loaded*/
//                while (true) {
//                    try {
//                        driver.findElement(By.cssSelector(".listol1 > .list-li:nth-child(3) .imgicon")).click();
//                        logger.info("FOUND MLBB MENU.. HOMEPAGE HAS BEEN LOADED IS CONFIRMED");
//                        break;
//                    } catch (Exception e) {
//                        logger.info("MLBB MENU NOT FOUND.. HOMEPAGE NOT FULLY LOADED YET");
//                    }
//                    Thread.sleep(2000);
//                }

                /*MOVE TO MLBB WINDOW*/
                vars.put("win3322", waitForWindow(2000));
                driver.switchTo().window(vars.get("win3322").toString());
                /*END MOVE TO MLBB WINDOW*/

                /*Validating for Page Fully Loaded*/

                /*Checking for MLBB Page Fully Loaded*/
                while (true) {
                    try {
                        response = driver.findElement(By.cssSelector(getDenomELementLocator(denom))).getAttribute("class");
                        driver.findElement(By.cssSelector(getDenomELementLocator(denom))).click();
                        SmileOneBot.logger.info( "FOUND SELECTED DENOM.. MLBB PAGE HAS BEEN LOADED IS CONFIRMED");
                        break;
                    } catch (Exception e) {
                        SmileOneBot.logger.info("DIAMOND DENOM NOT FOUND.. STILL LOOKING FOR");
                    }
                    Thread.sleep(1000);
                }

                driver.findElement(By.id("puseid")).click();
                driver.findElement(By.id("puseid")).sendKeys(accountId);
                driver.findElement(By.id("pserverid")).sendKeys(zoneId);
                driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
                driver.findElement(By.id("Nav-btnpc")).click();

                /*VALIDATION ON SUCCESS PURCHASE (COMMING SOON*/

                /* Create Json Response (success purchase) */

                /*Start Generating Response JSON*/
                voucherPojo.setPlayerId(Long.parseLong(playerId));
                voucherPojo.setDenom(denom);

                responsePojo.setVoucher(voucherPojo);
                responsePojo.setMessage("Voucher Purchased");
                responsePojo.setTrxId(trxId);
                responsePojo.setStatus(status00);



                /*CHECKING&SAVING OUTBOX*/
                if (currentInboxID!=null) {
                    SmileOneBot.logger.info("Saving to Outbox with inbox_id : "+currentInboxID);
                    try {
                        /*SAVING TO OUTBOX*/
                        SmileOneBot.logger.info("Saving Request to Outbox");
                        Date date = new Date();
                        Outboxes outboxes = new Outboxes(response, null, date, currentInboxID);
                        dbOutboxes.saveOutbox(outboxes);
                    } catch (Exception e) {
                        SmileOneBot.logger.info("---------Fail Save to DB----------");
                        e.printStackTrace();
                    }
                }
                /*END CHECKING&SAVING OUTBOX*/

            } catch (Exception e) {
                e.printStackTrace();
//                driver.quit();
            } finally {
                driver.quit();
            }
        }
            jsonObject =  new JSONObject(responsePojo);
            response = jsonObject.toString();

        SmileOneBot.logger.info("PURCHASE FINISHED WITH DETAILS :\n"+jsonObject.toString(4));

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

    private boolean isTrxIdValid(String input) {
        boolean result=false;
        if (input!=null){
            try {
                Long.parseLong(input);
                result=true;
            } catch (Exception e) {
                SmileOneBot.logger.info("TRX ID is invalid");
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
            case "tarlightplus":
                denomResult=denom11;
                break;
            default:
                denomResult=null;
                SmileOneBot.logger.info("INVALID DENOM IN CURRENT REQUEST : "+denomInput);
        }

        return denomResult;
    }


}

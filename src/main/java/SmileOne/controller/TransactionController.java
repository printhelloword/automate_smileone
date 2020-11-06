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
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
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
import java.util.concurrent.TimeUnit;

@RestController
public class TransactionController {

    private static WebDriver driver;
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

    @Value("${wait.purchase}")
    private String waitToPurchase;

    @Value("${auto.close}")
    private String autoClose;

    @Value("${login.method}")
    private String loginMethod;

    @Value("${browser.name}")
    private String browserName;

    @Value("${session.firefox}")
    private String firefoxMode;

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

    public final String FIREFOX_SESSION_PRIVATE = "private";
    public final String FIREFOX_SESSION_PROFILE = "profile";

    @GetMapping("/trx/{playerId}/{denom}/{trxId}")
    public String startPurchase(
            @PathVariable String playerId,
            @PathVariable String denom,
            @PathVariable String trxId,
            HttpServletRequest request
    ) throws IOException {

        String accountId = "", zoneId = "";
        Integer currentInboxID = null;

        String responseMsg = null;
        String responsePlayerID = null;
        String responseDenom = null;
        String responseTrxid = null;
        String responseStatus = status01; //initialize with Error status

        String response = null;
        JSONObject jsonObject = null;

        /*initialize Webdrivers for each browser*/
//        System.setProperty("webdriver.chrome.driver", "webdrivers\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", "webdrivers\\chromedriver86.0.424.exe");
        System.setProperty("webdriver.gecko.driver", "webdrivers\\geckodriver.exe");
        System.setProperty("webdriver.ie.driver", "webdrivers\\IEDriverServer.exe");

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
        } else {
            if (dbInboxes.isTrxIdExists(trxId)) { //check for duplicate trxId
                responseMsg = "Trx id Sudah terdapat di Dababase";
//                responseMsg = "Same Trx ID found in the database";
                responsePojo.setMessage(responseMsg);
            } else {
                if (getDenomELementLocator(denom) == null) {
                    responseMsg = "Denom tidak valid";
//                    responseMsg = "Invalid Denom";
                    responsePojo.setMessage(responseMsg);
                } else {
                    if (!isNumeric(playerId) || playerId.length() > 15) {
                        responseMsg = "Player ID tidak valid";
//                        responseMsg="Invalid Player ID";
                        responsePojo.setMessage(responseMsg);
                    } else {

                        if (playerId.length() < 14) {
                            accountId = playerId.substring(0, playerId.length() - 4);
                            zoneId = playerId.substring(playerId.length() - 4);
                        } else {
                            accountId = playerId.substring(0, playerId.length() - 5);
                            zoneId = playerId.substring(playerId.length() - 5);
                        }

                        try {

                            /*Saving Inbox*/
                            try {
                                SmileOneBot.logger.info("Incoming Request with URI: " + request.getRequestURI());

                                /*Saving to Inbox*/
                                String message = trxId + "#" + denom + "#" + playerId;
                                Date date = new Date();
                                Inboxes inbox = new Inboxes(message, "/trx", 0, date, trxId);
                                SmileOneBot.logger.info("Saving Request to Inbox");
                                currentInboxID = dbInboxes.saveInbox(inbox);
                            } catch (Exception e) {
                                SmileOneBot.logger.info("---------Fail Save Inbox to DB----------");
                                e.printStackTrace();
                                SmileOneBot.logger.info("---------Fail Save Inbox to DB----------");
                            }
                            /*END CHECKING&SAVING INBOX*/

                            SmileOneBot.logger.info("Starting Browser : " + browserName);
                            initBrowser(browserName);

                            Thread.sleep(Integer.parseInt(waitTime));

                            /*START LOGIN*/
                            SmileOneBot.logger.log(Level.INFO, "/*-------------START LOGIN PROCESS-----------------*/");

                            driver.get("https://www.smile.one/customer/account/login");

//                            driver.manage().window().setSize(new Dimension(1547, 950));
                            driver.manage().window().setSize(new Dimension(1126, 725));

                            /*HANDLE NEW WINDOWS AFTER DRIVER.GET*/
                            vars.put("window_handles", driver.getWindowHandles());
                            /*END HANDLE NEW WINDOWS AFTER DRIVER.GET*/

                            processLogin(loginMethod, botUsername, botPassword);
                            driver.switchTo().window(vars.get("root").toString());

                            SmileOneBot.logger.log(Level.INFO, "/*-------------LOGIN PROCESS FINISHED---------------*/");

                            /*Navigate to MLBB Page from Homepage after logged in*/

                            SmileOneBot.logger.log(Level.INFO, "Logged in. Navigating to MLBB Page");
                            driver.navigate().to("https://www.smile.one/merchant/mobilelegends?source=other");
                            /*START PURCHASE*/
                            SmileOneBot.logger.log(Level.INFO, "/*-------------START TOP-UP PROCESS---------------*/");

                            /*Switch to MLBB Page*/
//                            vars.put("win5381", waitForWindow(2000));
                            vars.put("win5381", waitForWindow(2000));
                            driver.switchTo().window(vars.get("win5381").toString());
                            /*End Switch to MLBB Page*/

                            /*Validating for Page Fully Loaded*/

                            /*Checking for MLBB Page Fully Loaded*/

                            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

                            driver.findElement(By.cssSelector(getDenomELementLocator(denom))).click();
                            SmileOneBot.logger.info("Selecting Denom");

                            driver.findElement(By.id("puseid")).click();
                            SmileOneBot.logger.info("Inserting Player ID");
                            driver.findElement(By.id("puseid")).sendKeys(accountId);
                            SmileOneBot.logger.info("Inserting Zone ID");
                            driver.findElement(By.id("pserverid")).sendKeys(zoneId);
                            SmileOneBot.logger.info("Selecting Payment Type");
                            driver.findElement(By.cssSelector(".section-nav:nth-child(1) .smilecoin > .cartao-name")).click();
                            driver.findElement(By.id("Nav-btnpc")).click();
                            SmileOneBot.logger.info("Finished Inputs.. Now Checking if Exist...");

//                            if (driver.getCurrentUrl().equalsIgnoreCase("https://www.smile.one/merchant/mobilelegends?source=other")) {
                            WebDriverWait wait = new WebDriverWait(driver, 3);
                            /*Next try catch try to validates whether alert is present(player id does not exist) / payment is success or failed */
                            try {
                                wait.until(ExpectedConditions.alertIsPresent());
//                                    driver.switchTo().alert();
                                String alrt = driver.switchTo().alert().getText();
                                driver.switchTo().alert().accept();
                                SmileOneBot.logger.info("Found Alert with message : " + alrt);
                                SmileOneBot.logger.info("Player " + accountId + "(" + zoneId + ") Does Not Exist");
                                responseMsg = "Akun Player tidak di temukan";
                            } catch (Exception e) {
//                                    SmileOneBot.logger.info("Player: "+accountId+" ("+zoneId+") di temukan. Melanjutkan top-up...");
                                SmileOneBot.logger.info("Player " + accountId + " (" + zoneId + ") exists. Now Processing top-up...");
                                /*this statement validates if balance is insufficient*/
//                                    Thread.sleep(Integer.parseInt(waitToPurchase));
//                                    Thread.sleep(5000);
//                                    try {
//                                        if (driver.findElements(By.cssSelector(".myreddem-account")).size() != 0) {
//                                    if (driver.getCurrentUrl().equalsIgnoreCase("https://www.smile.one/customer/recharge")) {
//                                    if (driver.findElements(By.cssSelector(".myreddem-account")).size() != 0) {
                                if (!driver.findElements(By.cssSelector(".myreddem-account")).isEmpty()) {
                                    SmileOneBot.logger.info("Insufficient Balance");
                                    responseMsg = "Saldo tidak mencukupi";
//                                    } else if (driver.getCurrentUrl().equalsIgnoreCase("https://www.smile.one/message/success")) {
                                } else if (!driver.findElements(By.cssSelector(".btnsuccess > span")).isEmpty()) {
                                    SmileOneBot.logger.info("");
                                    responseMsg = "Top-up Berhasil";
                                    responseStatus = status00;
                                    voucherPojo.setPlayerId(playerId);
                                    voucherPojo.setDenom(denom);
                                }
//                                        }
//                                    }catch(Exception ex){
//                                        ex.printStackTrace();
//                                    }
                                /*this statement validates if purchase is successful*/

                                e.printStackTrace();
                            }
//                            }

                            /* Create Json Response (success purchase) */

                            Thread.sleep(2000);

                        } catch (Exception e) {
                            e.printStackTrace();
                            SmileOneBot.logger.info("Error Found during purchase.");
                            if (autoClose.equalsIgnoreCase(auto_close)) {
                                SmileOneBot.logger.info("Closing Browser Now");
                                driver.quit();
                            }
                        } finally {
                            SmileOneBot.logger.info("Closing Browser Now");
                            if (autoClose.equalsIgnoreCase(auto_close))
                                driver.quit();
                        }

                        /*Start Generating Response JSON*/
                        responsePojo.setVoucher(voucherPojo);
                        responsePojo.setMessage(responseMsg);
                        responsePojo.setTrxId(trxId);
                        responsePojo.setStatus(responseStatus);

                    } //End if player ID is valid (last validation)

                } //End if denom is valid

            } //End if trxID is not in DB (yet)

        } //End if trxID is valid

        jsonObject = new JSONObject(responsePojo);
        response = jsonObject.toString();

        SmileOneBot.logger.info("Process finished with response:\n" + jsonObject.toString(4));

        /*Saving to Outbox*/
        if (currentInboxID != null) {
            SmileOneBot.logger.info("Saving to Outbox with inbox_id : " + currentInboxID);
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
        boolean result = false;
        if (input != null) {
            try {
                Long.parseLong(input);
                result = true;
            } catch (Exception e) {
                SmileOneBot.logger.info("Invalid TrxId");
            }
        }
        return result;
    }

    private boolean isNumeric(String input) {
        boolean result = false;
        if (input != null) {
            try {
                Long.parseLong(input);
                result = true;
            } catch (Exception e) {
                SmileOneBot.logger.info("Player ID + Zone ID Input not valid");
            }
        }
        return result;
    }

    private void initBrowser(String driverName) {
        /*Chrome Parameters*/
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--incognito");

        /*Firefox Parameters*/
        ProfilesIni profile = new ProfilesIni();
        FirefoxProfile myprofile = profile.getProfile("botSmileOne");
        FirefoxOptions firefoxOptions = new FirefoxOptions();

        if (driverName.equalsIgnoreCase("chrome"))
            driver = new ChromeDriver(options);
        else if (driverName.equalsIgnoreCase("firefox")) {
            if (firefoxMode.equalsIgnoreCase(FIREFOX_SESSION_PRIVATE))
                firefoxOptions.setCapability("browser.privatebrowsing.autostart", true);
            else if (firefoxMode.equalsIgnoreCase(FIREFOX_SESSION_PROFILE))
                firefoxOptions.setProfile(myprofile);

            driver = new FirefoxDriver(firefoxOptions);
        }
    }

    private void processLogin(String loginMethod, String userName, String userPwd) {
        SmileOneBot.logger.log(Level.INFO, "Selecting Login Method: " + loginMethod);

        if (browserName.equalsIgnoreCase("chrome") && loginMethod.equalsIgnoreCase("vk")) {
            driver.findElement(By.cssSelector(".vk > .login_method_p2")).click(); // vk login

            /*SWITCH & HANDLE POPUP LOGIN WINDOWS*/
            vars.put("win2423", waitForWindow(2000));
            vars.put("root", driver.getWindowHandle());
            driver.switchTo().window(vars.get("win2423").toString());
            /*END SWITCH & HANDLE POPUP LOGIN WINDOWS*/

            driver.findElement(By.name("email")).click();
            driver.findElement(By.name("email")).sendKeys(userName);
            driver.findElement(By.name("pass")).sendKeys(userPwd);
            driver.findElement(By.name("pass")).sendKeys(Keys.ENTER);
        } else if (browserName.equalsIgnoreCase("firefox")) {
            if (firefoxMode.equalsIgnoreCase(FIREFOX_SESSION_PRIVATE)) {
                SmileOneBot.logger.info("Start Firefox in Private Mode");
                driver.findElement(By.cssSelector(".google")).click(); //google login

                /*SWITCH & HANDLE POPUP LOGIN WINDOWS*/
                vars.put("win2423", waitForWindow(2000));
                vars.put("root", driver.getWindowHandle());
                driver.switchTo().window(vars.get("win2423").toString());
                /*END SWITCH & HANDLE POPUP LOGIN WINDOWS*/

                driver.findElement(By.id("identifierId")).click();
                driver.findElement(By.id("identifierId")).sendKeys(userName);
                driver.findElement(By.id("identifierId")).sendKeys(Keys.ENTER);
                driver.findElement(By.name("password")).sendKeys(userPwd);
                driver.findElement(By.name("password")).sendKeys(Keys.ENTER);
            } else if (firefoxMode.equalsIgnoreCase(FIREFOX_SESSION_PROFILE)) {
                SmileOneBot.logger.info("Start Firefox in Profiled Mode");
                waitForWindow(2000);

                if (loginMethod.equalsIgnoreCase("google"))
                    driver.findElement(By.cssSelector(".google")).click();
                else if (loginMethod.equalsIgnoreCase("vk"))
                    driver.findElement(By.cssSelector(".vk > .login_method_p2")).click(); // vk login

                /*SWITCH & HANDLE POPUP LOGIN WINDOWS*/
                vars.put("win2423", waitForWindow(5000));
                vars.put("root", driver.getWindowHandle());
                driver.switchTo().window(vars.get("win2423").toString());
                /*END SWITCH & HANDLE POPUP LOGIN WINDOWS*/

            }
        }
    }

    private String getDenomELementLocator(String denomInput) {
        String denomResult;

        switch (denomInput) {
            case "86":
                denomResult = denom1;
                break;
            case "172":
                denomResult = denom2;
                break;
            case "257":
                denomResult = denom3;
                break;
            case "706":
                denomResult = denom4;
                break;
            case "2195":
                denomResult = denom5;
                break;
            case "3688":
                denomResult = denom6;
                break;
            case "5532":
                denomResult = denom7;
                break;
            case "9288":
                denomResult = denom8;
                break;
            case "starlight":
                denomResult = denom9;
                break;
            case "twilight":
                denomResult = denom10;
                break;
            case "starlightplus":
                denomResult = denom11;
                break;
            default:
                denomResult = null;
                SmileOneBot.logger.info("INVALID DENOM IN CURRENT REQUEST : " + denomInput);
        }

        return denomResult;
    }


}

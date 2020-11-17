package bot.smileone.entity;

import bot.smileone.SmileOneApplication;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SmileOneBot {

    private static String botUsername;

    @Value("${bot.username}")
    public void setBotUsername(String value) {
        this.botUsername = value;
    }

    private static String botPassword;

    @Value("${bot.password}")
    public void set(String value) {
        this.botPassword = value;
    }

    private static String botBrowser;

    @Value("${bot.browser}")
    public void setBotBrowser(String value) {
        this.botBrowser = value;
    }

    private static String botSession;

    @Value("${bot.session}")
    public void setBotSession(String value) {
        this.botSession = value;
    }

    private static String chromeProfiler;

    @Value("${bot.profile.chrome}")
    public void setChromeProfiler(String value) {
        this.chromeProfiler = value;
    }

    private static String firefoxProfiler;

    @Value("${bot.profile.firefox}")
    public void setFirefoxProfiler(String value) {
        this.firefoxProfiler = value;
    }

    //Fields
    private static final String URL_BASE_LOGIN = "https://www.smile.one/customer/account/login";
    private static final String URL_BASE_PURCHASE_DIAMONDS_MLBB = "https://www.smile.one/merchant/mobilelegends?source=other";

    private static final String VOUCHER_TYPE_DIAMOND = "diamond";
    private static final String VOUCHER_TYPE_MEMBER = "member";

    private static final String BOT_BROWSER_CHROME = "chrome";
    private static final String BOT_BROWSER_FIREFOX = "firefox";

    private static final String BOT_SESSION_MODE_PRIVATE = "private";
    private static final String BOT_SESSION_MODE_PROFILED = "profiled";

    private static final String BOT_FIREFOX_PRIVATE_PROPERTY = "browser.privatebrowsing.autostart";
    private static final String BOT_CHROME_PRIVATE_PROPERTY = "--incognito";

    //ELEMENT LOCATORS
//    private static final String ELEMENT_FORM_DENOM = ;
    private static final String ELEMENT_FORM_USER_ID = "puseid";
    private static final String ELEMENT_FORM_ZONE_ID = "pserverid";
    private static final String ELEMENT_FORM_PAYMENT_METHOD = ".section-nav:nth-child(1) .smilecoin > .cartao-name";

    private static final String ELEMENT_BUTTON_BUY_NOW = "Nav-btnpc";

    private static final String ELEMENT_STATUS_INSUFFICIENT_BALANCE = ".myreddem-account";
    private static final String ELEMENT_STATUS_SUCCESS = ".btnsuccess > span";

    private static final String MESSAGE_ERROR_PLAYER_DOES_NOT_EXIST = "Akun Player Tidak Ditemukan";
    private static final String MESSAGE_ERROR_INSUFFICIENT_BALANCE = "Saldo Tidak Mencukupi";
    private static final String MESSAGE_SUCCESS_TRANSACTION = "Top-up Berhasil";

    private static final String STATUS_TRUE = "true";
    private static final String STATUS_FALSE = "false";

    private Voucher voucher;
    private String url;
    String message = "";
    boolean status = false;

    private Map<Boolean, String> transactionStatus = new HashMap<>();

    private static WebDriver driver;
    private static Map<String, Object> vars;
//    private static JavascriptExecutor js;

    @Autowired
    public SmileOneBot() {

    }

    public Map<Boolean, String> processTopTupAndGetMessage() {
        initSystemDriverProperties();

        setUpBot();

        try {
            processTopUp();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SmileOneApplication.logger.info("SmileOne Bot Finised");
//            driver.quit();
        }

        transactionStatus.put(status, message);

        return transactionStatus;
    }

    private void setUpBot() {
        if (botBrowser.equalsIgnoreCase(BOT_BROWSER_CHROME)) {
            ChromeOptions options = getChromeOption();
            driver = new ChromeDriver(options);
        } else if (botBrowser.equalsIgnoreCase(BOT_BROWSER_FIREFOX)) {
            FirefoxOptions firefoxOptions = getFirefoxOption();
            driver = new FirefoxDriver(firefoxOptions);
        }
        vars = new HashMap<>();
    }

    private FirefoxOptions getFirefoxOption() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        ProfilesIni profile = new ProfilesIni();
        if (botSession.equalsIgnoreCase(BOT_SESSION_MODE_PRIVATE)) {
            firefoxOptions.setCapability(BOT_FIREFOX_PRIVATE_PROPERTY, true);
            return firefoxOptions;
        } else if (botSession.equalsIgnoreCase(BOT_SESSION_MODE_PROFILED)) {
            FirefoxProfile myprofile = profile.getProfile(firefoxProfiler);
            firefoxOptions.setProfile(myprofile);
            return firefoxOptions;
        } else return firefoxOptions;
    }

    private ChromeOptions getChromeOption() {
        ChromeOptions options = new ChromeOptions();
        if (botSession.equalsIgnoreCase(BOT_SESSION_MODE_PRIVATE)) {
            options.addArguments(BOT_CHROME_PRIVATE_PROPERTY);
            return options;
        } else if (botSession.equalsIgnoreCase(BOT_SESSION_MODE_PROFILED)) {
            options.addArguments("user-data-dir=" +chromeProfiler);
            return options;
        } else
            return options;
    }

    public SmileOneBot(Voucher voucher) {
        this.voucher = voucher;
    }

    public static SmileOneBot withVoucher(Voucher voucher) {
        return new SmileOneBot(voucher);
    }

    private static void initSystemDriverProperties() {
        System.setProperty("webdriver.chrome.driver", "webdrivers\\chromedriver86.0.424.exe");
        System.setProperty("webdriver.gecko.driver", "webdrivers\\geckodriver28.exe");
        System.setProperty("webdriver.ie.driver", "webdrivers\\IEDriverServer.exe");
    }

    private void setUrl() throws Exception {
        url = getGeneratedUrl();
    }

    private String getGeneratedUrl() {
        if (botSession.equalsIgnoreCase(BOT_SESSION_MODE_PRIVATE))
            return URL_BASE_LOGIN;
        else if (botSession.equalsIgnoreCase(BOT_SESSION_MODE_PROFILED))
            return URL_BASE_PURCHASE_DIAMONDS_MLBB;
        else return null;
    }

    private void updateTransactionStatusAndMessage(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    private void processTopUp() throws Exception {

        setUrl();
        startBrowserAndNavigateToPage();
        inputVoucherDetails();
        sleep(1);
        processPayment();
        sleep(1);

        checkAndUpdatePaymentResult();
    }

    private void checkAndUpdatePaymentResult() throws Exception {
        String alert = getAlertText();
        String generatedMessage = checkPostPaymentAndGetStatus();
        if (alert != null) {
            SmileOneApplication.logger.info(">>>>ALERT EXIST");
            updateTransactionStatusAndMessage(false, MESSAGE_ERROR_PLAYER_DOES_NOT_EXIST);
            printAndCloseAlert(alert);
        } else {
            SmileOneApplication.logger.info(">>>>ALERT DOES NOT EXIST");
            if (generatedMessage != null) {
                if (generatedMessage.equalsIgnoreCase(STATUS_FALSE))
                    updateTransactionStatusAndMessage(false, MESSAGE_ERROR_INSUFFICIENT_BALANCE);
                else
                    updateTransactionStatusAndMessage(true, MESSAGE_SUCCESS_TRANSACTION);
            }
        }
    }

    private String checkPostPaymentAndGetStatus() {
        String message = null;
        try {
            if (!driver.findElements(By.cssSelector(ELEMENT_STATUS_INSUFFICIENT_BALANCE)).isEmpty()) {
                SmileOneApplication.logger.info("Insufficient Balance");
                message = STATUS_FALSE;
            } else if (!driver.findElements(By.cssSelector(ELEMENT_STATUS_SUCCESS)).isEmpty()) {
                SmileOneApplication.logger.info("Payment Successful");
                message = STATUS_TRUE;
            }
        }catch (Exception e){
            SmileOneApplication.logger.info("NOT REDIRECTED");

        }
        return message;
    }

    private void printAndCloseAlert(String alert) {
        SmileOneApplication.logger.info("Found Alert w/ Message : " + alert);
//        driver.switchTo().alert().accept();
    }

    private String getAlertText() {
        String alert = null;
        WebDriverWait wait = new WebDriverWait(driver, 3);
        /*Next try catch try to validates whether alert is present(player id does not exist) / payment is success or failed */
        try{
            wait.until(ExpectedConditions.alertIsPresent());
            alert = driver.switchTo().alert().getText();
        }catch (Exception e){
            SmileOneApplication.logger.info("FAIL GETTING ALERT");
        }

        return alert;
    }

    private void processPayment() throws Exception {
        printPerformedAction("Click", ELEMENT_BUTTON_BUY_NOW);
        driver.findElement(By.id(ELEMENT_BUTTON_BUY_NOW)).click();

    }

    private void startBrowserAndNavigateToPage() throws Exception {
        printPerformedAction("Navigating", url);
        driver.get(url);
    }

    private void inputVoucherDetails() throws Exception {
        SmileOneApplication.logger.info("Input Player ID And Zone ID");
        sleep(2);

        SmileOneApplication.logger.info("Selecting Denom w/ Element : "+voucher.getDenomLocator());

        driver.findElement(By.id(ELEMENT_FORM_USER_ID)).click();

        driver.findElement(By.id(ELEMENT_FORM_USER_ID)).sendKeys(voucher.getPlayer().getPlayerId());
        printPerformedAction("Input", voucher.getPlayer().getPlayerId());

        driver.findElement(By.id(ELEMENT_FORM_ZONE_ID)).sendKeys(voucher.getPlayer().getZoneId());
        printPerformedAction("Input", voucher.getPlayer().getZoneId());

        driver.findElement(By.cssSelector(voucher.getDenomLocator())).click();

        driver.findElement(By.cssSelector(ELEMENT_FORM_PAYMENT_METHOD)).click();
        printPerformedAction("Click", ELEMENT_FORM_PAYMENT_METHOD);
    }

    private void printPerformedAction(String action, String element){
        SmileOneApplication.logger.info("Action :" +action+ " | Target : "+element);
    }

    private void sleep(Integer time) {
        try {
            Thread.sleep(time * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

package SmileOne;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class visitSmileOone {

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver","D:\\SpringBoot\\`VoucherGame\\webdrivers\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 3");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.smile.one/merchant/mobilelegends?source=other");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
//            driver.quit();
        }


    }
}

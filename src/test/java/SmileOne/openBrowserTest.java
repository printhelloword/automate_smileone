package SmileOne;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class openBrowserTest {

    public static void main(String[] args) {
        
//        System.setProperty("webdriver.chrome.driver","D:\\SpringBoot\\`VoucherGame\\webdrivers\\chromedriver.exe");
        System.setProperty("webdriver.chrome.driver","C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 3");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().window().setSize(new Dimension(1936, 1066));
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}

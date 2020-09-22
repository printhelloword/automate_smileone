package SmileOne;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class openBrowserTest {

    public static void main(String[] args) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-data-dir=C:\\Users\\acer\\AppData\\Local\\Google\\Chrome\\User Data\\Profile 7");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().window().setSize(new Dimension(1936, 1066));
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}

package me.spencez.topic.recognition.util;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * http://npm.taobao.org/mirrors/chromedriver/83.0.4103.14/
 *
 * @author spence
 */
public class ChromeDriverUtils {

    static {

        System.setProperty("webdriver.chrome.driver", getDriverPath());
    }

    protected static String getDriverPath() {
        String osName = System.getProperty("os.name");
        String path = "src/main/resources/chromedriver/chromedriver";
        if (osName.contains("Mac")) {
            return path + "_mac64/chromedriver";
        } else if (osName.contains("Win")) {
            return path + "_win32/chromedriver.exe";
        } else {
            return path + "_linux64/chromedriver";
        }
    }

    public static ChromeDriver getDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--ignore-certificate-errors")
//                .addArguments("--headless")
                .addArguments("--disable-plugins")
                .addArguments("--disable-images")
                .addArguments("--disable-gpu")
                .addArguments("--start-maximized");

        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.managed_default_content_settings.images", 2);

        options.setExperimentalOption("prefs", prefs);
        options.setCapability("acceptInsecureCerts", true);
        options.setCapability("acceptSslCerts", true);

        ChromeDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        return driver;
    }

    public static void main(String[] args) {


        ChromeDriver driver = ChromeDriverUtils.getDriver();

        driver.get("https://www.baidu.com");

        driver.close();
        driver.quit();

        System.exit(0);
    }

}

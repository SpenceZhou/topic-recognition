package me.spencez.topic.recognition.spider.international;

import me.spencez.topic.recognition.repository.Mongo;
import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public class Nytimes {

    //https://www.nytimes.com/section/us

    String from = "TheNewYorkTimes";

    String seed = "https://www.nytimes.com/section/us";

    Mongo mongo = new Mongo();

    public void process() {

        ChromeDriver driver = ChromeDriverUtils.getDriver();

        try {

        } catch (Exception e) {

        } finally {
            driver.close();
            driver.quit();
        }

    }

}

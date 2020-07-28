package me.spencez.topic.recognition.spider.international;

import me.spencez.topic.recognition.repository.Mongo;
import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.openqa.selenium.chrome.ChromeDriver;

public class Twitter {

//    https://twitter.com/explore/tabs/news
//    https://twitter.com/explore/tabs/covid-19

    String from = "CNN";

    String seed = "https://edition.cnn.com/specials/last-50-stories";

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

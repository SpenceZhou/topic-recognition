package me.spencez.topic.recognition.spider.international;

import me.spencez.topic.recognition.entity.News;
import me.spencez.topic.recognition.repository.Mongo;
import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cnn {

    //https://edition.cnn.com/specials/last-50-stories
    String from = "CNN";
    String seed = "https://edition.cnn.com/specials/last-50-stories";
    Mongo mongo = new Mongo();
    Pattern pattern = Pattern.compile("[0-9]{4}.{1}[0-9]{2}.{1}[0-9]{2}");

    public void process() {

        ChromeDriver driver = ChromeDriverUtils.getDriver();
        List<News> list = new ArrayList<>();
        try {
            driver.get(seed);

            List<WebElement> elementList = driver.findElementById("automated-topic-zone").findElements(By.tagName("a"));
            for (WebElement webElement : elementList) {
                String title = webElement.getText();
                String url = webElement.getAttribute("href");
                Matcher matcher = pattern.matcher(url);
                String timeStr = null;
                if (matcher.find()) {
                    timeStr = matcher.group();
                }
                Date time = null;
                if (StringUtils.isNotBlank(timeStr)) {
                    time = DateUtils.parseDate(timeStr, "yyyy/MM/dd");
                }
                if (time == null) {
                    continue;
                }
                News news = new News(title, url, time, 1, this.from);
                list.add(news);
            }
            mongo.save(list);

        } catch (Exception e) {

        } finally {
            driver.close();
            driver.quit();
        }
    }

    public static void main(String[] args) {

        new Cnn().process();
        System.out.println("end!");

        System.exit(0);
    }
}

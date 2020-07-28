package me.spencez.topic.recognition.spider.internal;


import me.spencez.topic.recognition.entity.News;
import me.spencez.topic.recognition.repository.Mongo;
import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.*;

public class _163 {


    //https://news.163.com/rank/

    String from = "网易新闻";

    String seed = "https://news.163.com/rank/";

    Mongo mongo = new Mongo();

    public void process() {

        ChromeDriver driver = ChromeDriverUtils.getDriver();
        driver.get(seed);
        List<WebElement> list = driver.findElementsByClassName("tabBox");

        Map<String, News> map = new LinkedHashMap<>();
        for (WebElement webElement : list) {
            List<WebElement> elements = webElement.findElements(By.tagName("tr"));
            for (WebElement element : elements) {
                String text = element.getText();
                if (StringUtils.isBlank(text)) {
                    continue;
                }
                if (StringUtils.containsAny(text, "点击数", "跟贴数")) {
                    continue;
                }
                WebElement a = element.findElement(By.tagName("a"));
                if (a == null) {
                    continue;
                }
                String[] hotArr = text.split(" ");
                Integer hot = Integer.parseInt(hotArr[hotArr.length - 1]);
                String title = a.getText();
                String url = a.getAttribute("href");
                String[] arr = url.split("/");

                String timeStr = "20" + arr[3] + arr[4];

                try {
                    Date time = DateUtils.parseDate(timeStr, "yyyyMMdd");

                    News news = new News(title, url, time, hot, from);

                    if (map.containsKey(url)) {
                        Integer hotSum = news.getHot() + map.get(url).getHot();
                        news.setHot(hotSum);
                        map.remove(url);
                    }
                    map.put(url, news);

                } catch (Exception e) {
                    System.out.println(url);
                    e.printStackTrace();
                }
            }
        }

        List<News> newsList = new ArrayList<>(map.values());
        mongo.save(newsList);

        driver.close();
        driver.quit();

    }


    public static void main(String[] args) {
        new _163().process();

        System.out.println("end!");

        System.exit(0);
    }

}

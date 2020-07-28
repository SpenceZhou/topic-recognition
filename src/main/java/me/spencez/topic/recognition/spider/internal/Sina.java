package me.spencez.topic.recognition.spider.internal;

import me.spencez.topic.recognition.entity.News;
import me.spencez.topic.recognition.repository.Mongo;
import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sina {


    String from = "新浪新闻";

    String seed = "https://news.sina.com.cn/roll/#pageid=153&lid=2509&k=&num=50&page=1";

    Mongo mongo = new Mongo();

    public void process() {

        ChromeDriver driver = ChromeDriverUtils.getDriver();

        List<News> list = new ArrayList<>();
        try {
            driver.get(seed);

            List<WebElement> elementList = driver.findElementById("d_list").findElements(By.tagName("li"));

            for (WebElement webElement : elementList) {

                WebElement a = webElement.findElement(By.tagName("a"));
                WebElement span = webElement.findElement(By.className("c_time"));

                String title = a.getText();
                String url = a.getAttribute("href");

                String timeStr = span.getAttribute("s");

                Date time = new Date(Long.parseLong(timeStr)*1000);

                News news = new News(title,url,time,1,this.from);

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

        new Sina().process();
        System.out.println("end!");


        System.exit(0);
    }


}

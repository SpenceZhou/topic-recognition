package me.spencez.topic.recognition.spider.internal;

import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class QQ {

    //https://news.qq.com/


    String from = "腾讯新闻";

    String seed = "https://news.qq.com/";

    public void process(){
        ChromeDriver driver = ChromeDriverUtils.getDriver();

        try {

            driver.get(seed);

//            List<WebElement> list = driver.findElementsByClassName("tabBox");





        }catch (Exception e){

        }finally {
//            driver.close();
//            driver.quit();
        }



    }

    public static void main(String[] args) {

        new QQ().process();
        System.out.println("end!");
    }
}

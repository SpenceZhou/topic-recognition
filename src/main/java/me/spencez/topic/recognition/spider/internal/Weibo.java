package me.spencez.topic.recognition.spider.internal;


import me.spencez.topic.recognition.util.ChromeDriverUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Date;
import java.util.List;

public class Weibo {

//    https://www.weibo.com/?category=0
//    https://www.weibo.com/?category=1760
//    https://www.weibo.com/?category=99991


    String seed = "https://www.weibo.com/?category=99991";

    public void process() {
        ChromeDriver driver = ChromeDriverUtils.getDriver();
        try {
            driver.get(seed);
            sleep(2 * 1000);
            int total = 1;
            for (int i = 0; i < total; i++) {
                driver.executeScript("var q=document.documentElement.scrollTop=1000000");
                sleep(1000);
            }
            List<WebElement> webElementList = driver.findElementsByClassName("list_des");

            for (WebElement webElement : webElementList) {


                String url = webElement.getAttribute("href");
                String title = webElement.findElement(By.tagName("h3")).getText();

                //6月26日 17:46
                String timeStr = webElement.findElement(By.className("subinfo S_txt2")).getText();
                Date time = DateUtils.parseDate("2020年" + timeStr.trim(), "yyyy年MM月dd日 HH:mm");

                String numArrStr = webElement.findElement(By.className("subinfo_box clearfix subinfo_box_btm")).getText();


            }


        } catch (Exception e) {

            e.printStackTrace();
        } finally {
//            driver.close();
//            driver.quit();
        }
    }


    private void sleep(long timeMs) {

        try {
            Thread.sleep(timeMs);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new Weibo().process();

        System.out.println("end！");


        System.exit(0);
    }


}

package me.spencez.topic.recognition.entity;

import java.util.Date;

public class News {

    /**
     * 标题，微博
     */
    String title;

    /**
     * 来源URL
     *
     * 唯一索引
     *
     */
    String url;

    /**
     * 发布时间
     */
    Date time;

    /**
     * 点击数量
     * 评论数量
     * 转发数量
     * <p>
     * 求和
     */
    Integer hot;


    /**
     * 信息来源
     */
    String from;

    Date createTime = new Date();

    public News(String title, String url, Date time, Integer hot, String from) {
        this.title = title;
        this.url = url;
        this.time = time;
        this.hot = hot;
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", time=" + time +
                ", hot=" + hot +
                ", from='" + from + '\'' +
                '}';
    }
}

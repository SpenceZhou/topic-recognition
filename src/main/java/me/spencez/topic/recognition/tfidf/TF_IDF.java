package me.spencez.topic.recognition.tfidf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.WordBasedSegment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TF_IDF {

    /**
     * 百度搜索出的最大数量为1亿
     */
    private final long TOTAL = 100000000;

    private CloseableHttpClient httpclient = HttpClients.createDefault();

    public static void main(String[] args) {
        String text = "TF-IDF是一种用于信息检索与数据挖掘的常用加权技术。TF意思是词频(Term Frequency)，IDF意思是逆文本频率指数(Inverse Document Frequency)。";
//        String text = "When security vulnerabilities or misconfigurations are actively exploited by attackers, organizations need to react quickly in order to protect potentially vulnerable assets. 。";
        List<String> keywords = new TF_IDF().keywords(text, 10);
        for (String keyword : keywords) {
            System.out.println(keyword);
        }
    }


    public List<String> keywords(String text, int topN) {
        // 1. 分词
        Map<String, Integer> segMap = getCWS(text);
        Map<String, Double> tf_idfMap = new LinkedHashMap<>();
        // 2. 计算每个词的 tf-idf
        for (String key : segMap.keySet()) {

            double tf_idf = getTF_IDF(key, segMap.get(key));
            tf_idfMap.put(key, tf_idf);
        }

        // 3. 取 TopN
        List<String> keywords = getTopN(tf_idfMap, topN);
        return keywords;
    }


    /**
     * 对输入的文本进行中文分词，并进行词频统计
     *
     * @param text
     * @return
     */
    public Map<String, Integer> getCWS(String text) {
        Map<String, Integer> map = Maps.newHashMap();
        List<Term> words = HanLP.newSegment().seg(text);
        for (Term word : words) {
            //标点符号
            if (word.nature.startsWith("w")) {
                continue;
            }
            String key = word.word.toLowerCase().trim();

            if (map.keySet().contains(key)) {
                int value = map.get(key);
                map.remove(key);
                map.put(key, value + 1);
            } else {
                map.put(key, 1);
            }
        }
        return map;
    }


    Pattern pattern = Pattern.compile("百度为您找到相关结果约(\\d+(,\\d{3})*)个");

    /**
     * 从网络中获取包含关键词的文档数量
     *8
     * @param keyword
     * @return
     */
    private long getFrequencyByNetwork(String keyword) {
        long df = 0;
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //String url = "https://www.baidu.com/s?wd="+keyword;
        String url = "https://www.baidu.com/s?wd=" + keyword + "&rsv_spt=1";
        String page = getResponseString(url);
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()) {
            String result = matcher.group(1);
            result = result.replace(",", "");
            df = Long.parseLong(result);

        }
        return df;
    }


    private String getResponseString(String url) {

        HttpGet get = new HttpGet(url);
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.130 Safari/537.36");
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
            return String.valueOf(response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public double getTF_IDF(String keyword, int tf) {
        long documentNum = getFrequencyByNetwork(keyword);
        System.out.println("【"+keyword+"】df为："+documentNum);
        double idf = Math.log(TOTAL / (documentNum + 1.0));
        double tfIdf = tf * idf;
        System.out.println("【"+keyword + "】的tf-idf值为：" + tfIdf);
        return tfIdf;
    }


    //获取TopN
    public List<String> getTopN(Map<String, Double> tfIdfMap, int n) {
        if (tfIdfMap == null) {
            return null;
        }
        if (tfIdfMap.keySet().size() <= n) {
            return new ArrayList<>(tfIdfMap.keySet());
        }

        List<String> list = Lists.newArrayList();

        Map<String, Double> sortMap = Maps.newLinkedHashMap();
        sortMap = sortByValue(tfIdfMap);
        int count = 0;
        for (String key : sortMap.keySet()) {
            list.add(key);
            count++;
            if (count >= n) {
                break;
            }
        }
        return list;
    }

    //排序
    private Map<String, Double> sortByValue(Map<String, Double> map) {
        List<Map.Entry<String, Double>> sortList = Lists.newLinkedList(map.entrySet());
        Collections.sort(sortList, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return -Double.compare(o1.getValue(), o2.getValue());
            }
        });
        Map<String, Double> result = Maps.newLinkedHashMap();
        for (Map.Entry<String, Double> entity : sortList) {
            result.put(entity.getKey(), entity.getValue());
        }
        return result;
    }

}

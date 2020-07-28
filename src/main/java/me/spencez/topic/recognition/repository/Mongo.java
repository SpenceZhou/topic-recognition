package me.spencez.topic.recognition.repository;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import me.spencez.topic.recognition.entity.News;
import me.spencez.topic.recognition.util.DocumentUtils;
import org.bson.Document;

import java.util.List;
import java.util.function.Consumer;

public class Mongo {

    MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://root:spence_126@127.0.0.1:27018"));

    private String dbName = "topic-recognition";

    private String colName = "news";

    public void save(List<News> list) {
        MongoCollection<Document> col = mongoClient.getDatabase(dbName).getCollection(colName);
        for (News news : list) {
            try {
                col.insertOne(DocumentUtils.toDocument(news));
            } catch (Exception e) {

            }
        }
    }

    public void process(Consumer<News> consumer) {
        MongoCollection<Document> col = mongoClient.getDatabase(dbName).getCollection(colName);
        FindIterable<Document> findIterable = col.find().batchSize(1000);
        for (Document document : findIterable) {

            News news = (News) DocumentUtils.fromDocument(document, News.class);
            consumer.accept(news);
        }
    }


}

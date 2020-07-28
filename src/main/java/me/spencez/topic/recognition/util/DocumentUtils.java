package me.spencez.topic.recognition.util;


import org.bson.Document;

import java.lang.reflect.Field;

public class DocumentUtils {

    public static Object fromDocument(Document doc, @SuppressWarnings("rawtypes") Class clz) {
        try {
            Object object = clz.newInstance();
            Field[] fields = clz.getDeclaredFields();

            for (Field field : fields) {
                String name = field.getName();

                Object value = doc.get(name);

                if(value!=null) {
                    if(field.getType().isInstance(value)||field.getType().getSimpleName().equalsIgnoreCase(value.getClass().getSimpleName())) {
                        field.setAccessible(true);
                        try {
                            field.set(object, value);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            return object;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Document toDocument(Object obj) {
        Document document = new Document();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            String key = field.getName();
            try {
                Object value = field.get(obj);
                document.append(key, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return document;
    }

}

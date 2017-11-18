package com.xk.kson;

import com.xk.kson.bean.News;
import com.xk.kson.bean.User;
import com.xk.ksonlib.KSon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuekai on 2017/11/12.
 */

public class Main {
    public static void main(String[] args) {

        try {
            News bean1 = createBean();
//            bean1.setMap(createMap());
//            String json = "[{\"id\":2,\"name\":\"aa\",\"pwd\":\"aa\",\"isBoy\":true},{\"id\":1,\"name\":\"bb\",\"pwd\":null,\"isBoy\":false}]";
            String json = "{\"id\":1,\"title\":\"title\",\"content\":\"content\",\"author\":{\"id\":1,\"name\":\"Fancyy\",\"pwd\":\"123456\",\"isBoy\":false},\"readerForList\":[{\"id\":2,\"name\":\"aa\",\"pwd\":null,\"isBoy\":false},{\"id\":1,\"name\":\"bb\",\"pwd\":null,\"isBoy\":false}],\"readerForArray\":[{\"id\":2,\"name\":\"aa\",\"pwd\":null,\"isBoy\":false},{\"id\":1,\"name\":\"bb\",\"pwd\":null,\"isBoy\":false}]}\n";
            News news = KSon.toModel(json, News.class);
            System.out.println("===result:\n"+news);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            JSONObject jsonObject = new JSONObject();
//            News bean1 = createBean();
//            bean1.setMap(createMap());
//            String s = KSon.toJson(bean1);
//            System.out.println(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    public static Map<User, Boolean> createMap() {

        HashMap<User, Boolean> stringBooleanHashMap = new HashMap<>();
        stringBooleanHashMap.put(createAuthor(), null);
        stringBooleanHashMap.put(null, true);
        stringBooleanHashMap.put(createAuthor(), true);
        stringBooleanHashMap.put(createAuthor(), false);
        return stringBooleanHashMap;
    }

    public static News createBean() {
        News news = new News();
        news.setId(1);
        news.setTitle("title");
        news.setContent("content");
        news.setAuthor(createAuthor());
        news.setReaderForList(createUsersForList());
        news.setReaderForArray(createReadersForArray());
        return news;
    }


    private static List<User> createUsersForList() {
        List<User> readers = new ArrayList<>();
        User readerA = new User();
        readerA.setId(2);
        readerA.setName("aa");
        readers.add(readerA);

        User readerB = new User();
        readerB.setId(1);
        readerB.setName("bb");
        readers.add(readerB);
        readers.add(null);
        return readers;
    }

    private static User[] createReadersForArray() {
        User[] readers = new User[2];
        User readerA = new User();
        readerA.setId(2);
        readerA.setName("aa");
        readers[0] = readerA;

        User readerB = new User();
        readerB.setId(1);
        readerB.setName("bb");
        readers[1] = readerB;
        return readers;
    }

    private static User createAuthor() {
        User author = new User();
        author.setId(1);
        author.setName("Fancyy");
        author.setPwd("123456");
        return author;
    }
}

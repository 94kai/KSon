package com.xk.kson.bean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/2/19 0019.
 */
public class News {
    public News()  {
    }

    private int id;
    private String title;
    private String content;
    private User author;
    private List<User> readerForList;
    private User[] readerForArray;


    public void setId(int id) {
        this.id = id;

    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setAuthor(User author) {
        this.author = author;
    }
    public void setReaderForList(List<User> readerForList) {
        this.readerForList = readerForList;
    }

    public void setReaderForArray(User[] readerForArray) {
        this.readerForArray = readerForArray;
    }


    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author=" + author +
                ", readerForList=" + readerForList +
                ", readerForArray=" + Arrays.toString(readerForArray) +
                '}';
    }
}
package com.example.vamsi.blogpost;


import java.util.Date;

public class BlogPost extends BlogPostId {

    //BlogPost is a class that is used as datastructure for the implementation of the posting posts.
    //It contains the methods that are required for getting and setting Image_url,Image_thumb,desc,user_id,timeStamp;

    //String and Dtae classes used for getting and setting
    public String image_url, image_thumb, desc, user_id;
    public Date timestamp;


    //Empty Constructor
    public BlogPost() {

    }

    //Constructor for setting the values of image_url,image_thumb,user_id,timestamp,desc to the local valuesby
    //using this keyword
    public BlogPost(String image_url, String image_thumb, String desc, String user_id, Date timestamp) {
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.desc = desc;
        this.user_id = user_id;
        this.timestamp = timestamp;

    }

    //These are Getters and Setters of various functions

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

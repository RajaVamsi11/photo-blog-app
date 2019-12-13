package com.example.vamsi.blogpost.Model;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class BlogPostId {

    //This BlogPostId is used in implementing the BlogPost timestamp

    @Exclude
    public  String BlogPostId;

    public <T extends BlogPostId> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }

}

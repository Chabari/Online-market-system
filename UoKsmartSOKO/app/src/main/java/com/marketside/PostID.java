package com.marketside;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class PostID {
    @Exclude
    public String PostID;

    public <T extends PostID> T withID(@NonNull final String id){
        this.PostID = id;
        return (T) this;
    }
}

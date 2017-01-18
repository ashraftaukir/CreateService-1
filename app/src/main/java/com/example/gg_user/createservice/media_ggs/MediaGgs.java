package com.example.gg_user.createservice.media_ggs;

import android.net.Uri;

import java.net.URI;

/**
 * Created by gg-user on 1/15/17.
 */

public class MediaGgs {
    private Uri uri;
    private int type;
    public MediaGgs(Uri uri,int type) {
        this.uri = uri;
        this.type = type;
    }

    public MediaGgs(int type) {
        this.type = type;

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof MediaGgs){
            MediaGgs m = (MediaGgs) obj;
            return this.getUri()!=null && this.getUri().equals(m.getUri());

        }
        return false;
    }


}

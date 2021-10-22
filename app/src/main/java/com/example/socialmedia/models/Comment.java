package com.example.socialmedia.models;

public class Comment {

    private String id;
    private String comment;
    private String idUser;
    private String idPost;
    private long timeStamp;

    public Comment() {
    }

    public Comment(String id, String comment, String idUser, String idPost, long timeStamp) {
        this.id = id;
        this.comment = comment;
        this.idUser = idUser;
        this.idPost = idPost;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}

package com.example.wordwave.model;

public class Ranking {
    private String userId;
    private String topicId;
    private int score;


    public Ranking(){}
    public Ranking(String userId, String topicId,int score) {
        this.topicId = topicId;
        this.userId = userId;
        this.score = score;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

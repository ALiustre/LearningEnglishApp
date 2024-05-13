package com.example.wordwave.model;

public class SelectedTopic {

    private String userId;
    private String topicId;

    // Required no-argument constructor for Firestore
    public SelectedTopic() {
    }

    public SelectedTopic(String userId, String topicId) {
        this.userId = userId;
        this.topicId = topicId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTopicId() {
        return topicId;
    }

}

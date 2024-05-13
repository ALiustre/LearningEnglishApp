package com.example.wordwave.model;

import java.util.List;

public class Topic {
    String topicId;
    String name;

    Long termNum;

    String userId;

    boolean isPublic;

    List<String> subUserIds;


    public Topic(){
    }

    public Topic(String topicId,String userId,String name) {
        this.topicId = topicId;
        this.name = name;
        this.userId = userId;
        this.termNum = (long)0;
        this.isPublic = false;
    }

    public Topic(String topicId,String userId,String name, Long termNum) {
        this.topicId = topicId;
        this.name = name;
        this.termNum = termNum;
        this.userId = userId;
        this.isPublic=false;
    }
    public Topic(String topicId,String userId,String name, Long termNum,List<String> subUserIds) {
        this.topicId = topicId;
        this.name = name;
        this.termNum = termNum;
        this.userId = userId;
        this.isPublic=false;
        this.subUserIds= subUserIds;
    }

    public List<String> getSubUsersId() {
        return subUserIds;
    }

    public void setSubUsersId(List<String> subUsersId) {
        this.subUserIds = subUsersId;
    }

    public void setTermNum(Long termNum) {
        this.termNum = termNum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTermNum() {
        return termNum;
    }


    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}

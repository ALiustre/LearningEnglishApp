package com.example.wordwave.model;

public class Word {

    String wordId;
    String term;
    String definition;

    String topicId;
    String status;
    boolean isFavorite;


    public Word(){
        this.term ="";
        this.definition="";
    }
    public Word(String term, String definition) {
        this.term = term;
        this.definition = definition;
    }
    public Word(String wordId,String term, String definition) {
        this.wordId = wordId;
        this.term = term;
        this.definition = definition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean marked) {
        isFavorite = marked;
    }
}

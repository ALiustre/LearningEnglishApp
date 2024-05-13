package com.example.wordwave.model;

public class Flashcard {
    private String word;
    private String definition;
    private boolean flipOnSpeakerClick;
    private boolean isWordSideUp;

    public Flashcard(){};

    public Flashcard(String word, String definition) {
        this.word = word;
        this.definition = definition;
        this.isWordSideUp = true;
    }
    public boolean isWordSideUp() {
        return isWordSideUp;
    }

    public void setWordSideUp(boolean wordSideUp) {
        isWordSideUp = wordSideUp;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

}

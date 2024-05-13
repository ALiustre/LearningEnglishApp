package com.example.wordwave.model;

public class Fill {
    private String question,answer, correct;

    public Fill(String question, String answer, String correct) {
        this.question = question;
        this.answer = answer;
        this.correct = correct;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }
}

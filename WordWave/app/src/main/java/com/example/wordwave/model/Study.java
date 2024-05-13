package com.example.wordwave.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Study {
    private String question, a, b, c, d, correctA;

    private String key;

    private int setNum;

    public Study(){
    }
    public Study(String question, String a, String b, String c, String d, String correctA, String key, int setNum) {
        this.question = question;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.correctA = correctA;
        this.key = key;
        this.setNum = setNum;
    }
    public List<String> getOptions() {
        // Return a list containing options a, b, c, and d
        return Arrays.asList(a, b, c, d);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getCorrectA() {
        return correctA;
    }

    public void setCorrectA(String correctA) {
        this.correctA = correctA;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getSetNum() {
        return setNum;
    }

    public void setSetNum(int setNum) {
        this.setNum = setNum;
    }
}

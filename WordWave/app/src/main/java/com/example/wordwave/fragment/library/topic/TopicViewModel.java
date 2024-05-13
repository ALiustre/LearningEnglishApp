package com.example.wordwave.fragment.library.topic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wordwave.model.Topic;

import java.util.List;

public class TopicViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Topic>> topicsLiveData = new MutableLiveData<>();

    public void setTopics(List<Topic> topics) {
        topicsLiveData.setValue(topics);
    }

    public LiveData<List<Topic>> getTopics() {
        return topicsLiveData;
    }

}
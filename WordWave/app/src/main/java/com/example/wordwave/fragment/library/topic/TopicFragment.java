package com.example.wordwave.fragment.library.topic;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.adapter.topic.TopicAdapter;
import com.example.wordwave.model.Topic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TopicFragment extends Fragment {

    private TopicViewModel mViewModel;
    private TopicAdapter topicAdapter;
    FirebaseFirestore db;
    List<String> titleList;
    List<Topic> topicList;
    int termNum;
    private RecyclerView recyclerView;
    public static TopicFragment newInstance() {
        return new TopicFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        mViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        // Observe changes in the LiveData
        mViewModel.getTopics().observe(this, new Observer<List<Topic>>() {
            @Override
            public void onChanged(List<Topic> topics) {
                // Update your UI or adapter when the data changes
                topicAdapter.setTopics(topics);
                topicAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_topic,container,false);


        db = FirebaseFirestore.getInstance();
        topicList = new ArrayList<>();  // Initialize topicList here
        titleList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.topic_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(TopicFragment.newInstance().getContext()));

        // Use ViewModel to retain data across configuration changes
        topicAdapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(topicAdapter);

        loadTopicsFromFirebase();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        // TODO: Use the ViewModel
    }


    private void loadTopicsFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("topic").whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        titleList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentName = document.getString("name");
                            String topicId = document.getId();
                            String currentUserId = document.getString("userId");

                            if (document.contains("termNum") && document.getLong("termNum") != null) {
                                // Retrieve the value of 'termNum' from the document
                                termNum = document.getLong("termNum").intValue();
                            }
                            // Retrieve data from the document
                            // Now you can use documentId and data as needed
                            titleList.add(documentName);
                            topicList.add(new Topic(topicId,currentUserId, documentName, Long.parseLong(String.valueOf(termNum))));
                        }
                        topicAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("topic").whereArrayContains("subUserIds", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        titleList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentName = document.getString("name");
                            String topicId = document.getId();
                            String currentUserId = document.getString("userId");

                            if (document.contains("termNum") && document.getLong("termNum") != null) {
                                // Retrieve the value of 'termNum' from the document
                                termNum = document.getLong("termNum").intValue();
                            }
                            // Retrieve data from the document
                            // Now you can use documentId and data as needed
                            titleList.add(documentName);
                            topicList.add(new Topic(topicId,currentUserId, documentName, Long.parseLong(String.valueOf(termNum))));
                        }
                        topicAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });

        mViewModel.setTopics(topicList);
    }

//    private void titleToTopic(){
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        for(int i = 0; i< titleList.size();i++){
//            topicList.add(new Topic(userId,titleList.get(i)));
//        }
//    }
}
package com.example.wordwave.fragment.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;
import com.example.wordwave.adapter.folder.FolderAdapter;
import com.example.wordwave.adapter.topic.TopicAdapter;
import com.example.wordwave.adapter.word.WordAdapter;
import com.example.wordwave.databinding.FragmentHomeBinding;
import com.example.wordwave.model.Folder;
import com.example.wordwave.model.Topic;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TopicAdapter publicTopicAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    List<String> titleList;
    List<Topic> topicList;
    int termNum;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().setTitle("WordWave");

        db = FirebaseFirestore.getInstance();
        topicList = new ArrayList<>();  // Initialize topicList here
        titleList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.public_topics_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        publicTopicAdapter = new TopicAdapter(topicList);
        recyclerView.setAdapter(publicTopicAdapter);

        loadTopicsFromFirebase();


        return view;
    }

    private void loadTopicsFromFirebase() {
        db = FirebaseFirestore.getInstance();
        db.collection("topic").whereEqualTo("isPublic", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        titleList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentName = document.getString("name");
                            String topicId = document.getId();
                            String userId = document.getString("userId").trim();
                            if (document.contains("termNum") && document.getLong("termNum") != null) {
                                // Retrieve the value of 'termNum' from the document
                                termNum = document.getLong("termNum").intValue();
                            }
                            // Retrieve data from the document
                            // Now you can use documentId and data as needed
                            titleList.add(documentName);
                            topicList.add(new Topic(topicId,userId, documentName, Long.parseLong(String.valueOf(termNum))));
                        }
                        publicTopicAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
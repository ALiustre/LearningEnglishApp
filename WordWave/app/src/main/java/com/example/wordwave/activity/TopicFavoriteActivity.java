package com.example.wordwave.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.wordwave.R;
import com.example.wordwave.adapter.wordcontent.WordContentAdapter;
import com.example.wordwave.model.Word;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TopicFavoriteActivity extends AppCompatActivity {

    private WordContentAdapter adapter;
    RecyclerView faTopicRcv;
    String topicId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_favorite);
        faTopicRcv = findViewById(R.id.topic_favorite_content_rcv);

        topicId = getIntent().getStringExtra("topicId");


        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        // Get a reference to the Firestore collection containing favorite words
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference favoriteWordsRef = db.collection("topic")
                .document(topicId) // Replace with your actual topic ID
                .collection("word");

        // Query to get only the words marked as favorite
        Query query = favoriteWordsRef.whereEqualTo("favorite", true);

        // Configure the adapter options
        FirestoreRecyclerOptions<Word> options = new FirestoreRecyclerOptions.Builder<Word>()
                .setQuery(query, Word.class)
                .build();

        // Initialize the adapter
        adapter = new WordContentAdapter(options);

        // Get a reference to the RecyclerView and set the adapter
        faTopicRcv.setLayoutManager(new LinearLayoutManager(this));
        faTopicRcv.setAdapter(adapter);
        adapter.startListening();
    }
}
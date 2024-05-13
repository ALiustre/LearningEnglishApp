package com.example.wordwave.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.wordwave.R;
import com.example.wordwave.adapter.folder.FolderAdapter;
import com.example.wordwave.adapter.ranking.RankingAdapter;
import com.example.wordwave.model.Folder;
import com.example.wordwave.model.Ranking;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class RankingActivity extends AppCompatActivity {

    String topicId;

    RankingAdapter rankingAdapter;
    RecyclerView rankRcv;

    ImageView close;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        topicId = getIntent().getStringExtra("topicId");
        rankRcv = findViewById(R.id.ranking_recyclerView);
        close = findViewById(R.id.close_btn);

        rankRcv.setLayoutManager(new LinearLayoutManager(this));

        getData();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void getData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Chỉ lấy 5 hạng đầu trong Query
        rankingAdapter = new RankingAdapter(
                new FirestoreRecyclerOptions.Builder<Ranking>()
                        .setQuery(db.collection("ranking")
                                .whereEqualTo("topicId", topicId)  // Kiểm tra topicId
                                .orderBy("score", Query.Direction.DESCENDING)
                                .limit(5), Ranking.class)
                        .build()
        );

        // Set the adapter to the RecyclerView
        rankRcv.setAdapter(rankingAdapter);
        rankingAdapter.startListening();
    }
}
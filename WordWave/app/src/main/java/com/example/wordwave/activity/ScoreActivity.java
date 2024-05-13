package com.example.wordwave.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wordwave.R;
import com.example.wordwave.model.Ranking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ScoreActivity extends AppCompatActivity {

    TextView knowTv, stillTv, leftTv ,finalScoreTv;
    ImageView close;
    String score, left, quizznum, finalScore, userId, topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        knowTv = findViewById(R.id.score_know_tv);
        stillTv = findViewById(R.id.score_still_tv);
        leftTv = findViewById(R.id.score_term_left);
        close = findViewById(R.id.close_btn);
        finalScoreTv = findViewById(R.id.finalScore);

        topicId = getIntent().getStringExtra("topicId");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        score = getIntent().getStringExtra("score");
        left = getIntent().getStringExtra("left");
        quizznum = getIntent().getStringExtra("numquizz");

        knowTv.setText(score);
        stillTv.setText(String.valueOf(Integer.parseInt(quizznum)-Integer.parseInt(score)-Integer.parseInt(left)));
        leftTv.setText(left);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        finalScore = String.valueOf(100/Integer.parseInt(quizznum)*Integer.parseInt(score));
        finalScoreTv.setText(finalScore);

        saveToFirestore(userId,topicId, Integer.parseInt(finalScore));

    }
    private void saveToFirestore(String userId, String topicId, int score) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new Ranking object
        Ranking ranking = new Ranking(userId, topicId, score);

        // Save the ranking to Firestore
        db.collection("ranking")
                .add(ranking)
                .addOnSuccessListener(documentReference -> {
                    // Successfully added to Firestore
                    // You can perform additional actions if needed
                })
                .addOnFailureListener(e -> {
                    // Handle the failure to add to Firestore
                });
    }
}
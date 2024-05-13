package com.example.wordwave.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.adapter.flashcardAdapter.FlashcardAdapter;
import com.example.wordwave.adapter.word.WordAdapter;
import com.example.wordwave.model.Flashcard;
import com.example.wordwave.model.Topic;
import com.example.wordwave.model.Word;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlashcardsActivity extends AppCompatActivity implements FlashcardAdapter.FinishButtonClickListener{

    private ViewPager2 viewPager;
    private FlashcardAdapter flashcardAdapter;
    private WordAdapter wordAdapter;
    private List<Flashcard> flashcardList;
    private String topicId, userId;
    private TextToSpeech textToSpeech;
    private List<Flashcard> flashcards;

    boolean loadLanguage =false;

    boolean loadFavoriteStudyDataOnly = false;

    ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);
        topicId = getIntent().getStringExtra("topicId");
        userId = getIntent().getStringExtra("userId");

        viewPager = findViewById(R.id.viewPager);


        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        loadFavoriteStudyDataOnly = preferences.getBoolean("loadFavoriteStudyDataOnly", false);
        if(loadFavoriteStudyDataOnly){
            flashcardList = connectFavorites();
            saveStateInPreferences(false);
        }else {
            flashcardList = connect();
        }








        flashcardAdapter = new FlashcardAdapter(flashcardList, new FlashcardAdapter.SpeakerClickListener() {
            @Override
            public void onSpeakerClick(String text) {
                speakOut(text);
            }
        }, this);
        viewPager.setAdapter(flashcardAdapter);



    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_study_settings, null);

        // Find your buttons in the bottom sheet layout
        LinearLayout studyWithFavoriteListBtn = view.findViewById(R.id.studyWithFavoriteListBtn);


        studyWithFavoriteListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Study with favorite list" option
                // You may want to start a new activity or perform other actions
                flashcards.clear();
                saveStateInPreferences(!loadFavoriteStudyDataOnly);

                // Dismiss the bottom sheet dialog
                bottomSheetDialog.dismiss();

                // Recreate the activity
                recreate();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
    private void saveStateInPreferences(boolean loadFavoriteStudyDataOnly) {
        // Save the state in SharedPreferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean("loadFavoriteStudyDataOnly", loadFavoriteStudyDataOnly).apply();
    }

    private void speakOut(String text) {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.US);
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                            result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle language data missing or not supported
                    } else {
                        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                } else {
                    // Handle initialization failure
                }
            }
        });
    }
    private List<Flashcard> connect(){
        flashcards = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("topic").document(topicId).collection("word")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Word word = document.toObject(Word.class);
                            flashcards.add(new Flashcard(word.getTerm(),word.getDefinition()));
                        }
                        flashcardAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FlashcardsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        return flashcards;
    }
    private List<Flashcard> connectFavorites(){
        flashcards = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("topic").document(topicId).collection("word").whereEqualTo("favorite",true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Word word = document.toObject(Word.class);
                            flashcards.add(new Flashcard(word.getTerm(),word.getDefinition()));
                        }
                        flashcardAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FlashcardsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
        return flashcards;
    }
    @Override
    public void onFinishButtonClick(int correctCount, int incorrectCount, int totalFlashcards) {
        // Handle the finish button click
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("score", String.valueOf(correctCount));
        intent.putExtra("left", String.valueOf(totalFlashcards - correctCount - incorrectCount));
        intent.putExtra("numquizz", String.valueOf(totalFlashcards));
        intent.putExtra("topicId",topicId);
        startActivity(intent);
        finish(); // Finish the current activity
    }


}
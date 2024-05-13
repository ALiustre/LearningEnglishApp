package com.example.wordwave.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.databinding.ActivityFillBinding;
import com.example.wordwave.databinding.ActivityStudyBinding;
import com.example.wordwave.model.Fill;
import com.example.wordwave.model.Study;
import com.example.wordwave.model.Word;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FillActivity extends AppCompatActivity {
    ActivityFillBinding binding;

    TextToSpeech textToSpeech;
    ArrayList<Fill> fillList;
    int left = 0;
    int count = 0;
    int position = 0;
    int score = 0;
    CountDownTimer timer;

    FirebaseFirestore db;
    String userId;


    boolean loadLanguage =false;

    boolean loadFavoriteStudyDataOnly = false;

    String topicId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFillBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        fillList = new ArrayList<>();

        topicId = getIntent().getStringExtra("topicId");
        userId = getIntent().getStringExtra("userId");

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        loadLanguage = preferences.getBoolean("loadLanguageSelect", false);
        loadFavoriteStudyDataOnly = preferences.getBoolean("loadFavoriteStudyDataOnly", false);


        if(loadFavoriteStudyDataOnly){
            loadFavoriteFillData();
            saveStateInPreferences(false);
        } else if (loadLanguage) {
            loadFillData2();
            saveStateInPreferences(false);
        }else {
            loadFillData();
        }
        binding.fillSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        binding.fillNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.fillCheckBtn.setEnabled(true);
                binding.fillCheckBtn.setAlpha(1.0f);
                binding.fillNextBtn.setEnabled(false);
                binding.fillNextBtn.setAlpha(0.3f);
                binding.fillAnswerEdt.setEnabled(true);
                binding.fillAnswerEdt.setText("");
                position++;

                if(position == fillList.size()){
                    Intent intent = new Intent(FillActivity.this,ScoreActivity.class);
                    intent.putExtra("score", String.valueOf(score));
                    intent.putExtra("left", String.valueOf(left));
                    intent.putExtra("numquizz", String.valueOf(fillList.size()));
                    intent.putExtra("topicId",topicId);
                    startActivity(intent);
                    finish();
                }

                displayQuestionAndOptions();
            }
        });
    }
    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_study_settings, null);

        // Find your buttons in the bottom sheet layout
        LinearLayout studyWithOtherLanguageBtn = view.findViewById(R.id.studyWithOtherLanguageBtn);
        LinearLayout studyWithFavoriteListBtn = view.findViewById(R.id.studyWithFavoriteListBtn);

        studyWithOtherLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Study with other language" option
                // You may want to start a new activity or perform other actions
                fillList.clear();
                saveStateInPreferences2(!loadLanguage);
                bottomSheetDialog.dismiss();
                recreate();

            }
        });

        studyWithFavoriteListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Study with favorite list" option
                // You may want to start a new activity or perform other actions
                fillList.clear();
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
    private void saveStateInPreferences2(boolean loadLanguageSelect) {
        // Save the state in SharedPreferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        preferences.edit().putBoolean("loadLanguageSelect", loadLanguageSelect).apply();
    }

    private void loadFillData() {
        db.collection("topic").document(topicId).collection("word")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Word> wordList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Word word = document.toObject(Word.class);
                        wordList.add(word);
                    }

                    // Randomly shuffle the list of words
                    Collections.shuffle(wordList);

                    // Select a limited number of words (adjust as needed)

                    int wordSize= wordList.size();

                    switch (wordSize){
                        case 1:

                    }
                    // Create Study objects from selected words
                    for (Word word : wordList) {
                        

                        // Create a Study object
                        Fill study = new Fill(word.getTerm(), binding.fillAnswerEdt.getText().toString(),word.getDefinition());

                        fillList.add(study);
                    }

                    // Display the first question and options
                    displayQuestionAndOptions();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FillActivity.this, "Error loading study data", Toast.LENGTH_SHORT).show();
                });
    }
    private void loadFillData2() {
        db.collection("topic").document(topicId).collection("word")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Word> wordList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Word word = document.toObject(Word.class);
                        wordList.add(word);
                    }

                    // Randomly shuffle the list of words
                    Collections.shuffle(wordList);

                    // Select a limited number of words (adjust as needed)

                    int wordSize= wordList.size();

                    switch (wordSize){
                        case 1:

                    }
                    // Create Study objects from selected words
                    for (Word word : wordList) {


                        // Create a Study object
                        Fill study = new Fill(word.getDefinition(), binding.fillAnswerEdt.getText().toString(),word.getTerm());

                        fillList.add(study);
                    }

                    // Display the first question and options
                    displayQuestionAndOptions2();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FillActivity.this, "Error loading study data", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadFavoriteFillData() {
        db.collection("topic").document(topicId).collection("word").whereEqualTo("favorite",true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Word> wordList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Word word = document.toObject(Word.class);
                        wordList.add(word);
                    }

                    // Randomly shuffle the list of words
                    Collections.shuffle(wordList);

                    // Select a limited number of words (adjust as needed)

                    int wordSize= wordList.size();

                    switch (wordSize){
                        case 1:

                    }
                    // Create Study objects from selected words
                    for (Word word : wordList) {


                        // Create a Study object
                        Fill study = new Fill(word.getTerm(), binding.fillAnswerEdt.getText().toString(),word.getDefinition());

                        fillList.add(study);
                    }

                    // Display the first question and options
                    displayQuestionAndOptions();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FillActivity.this, "Error loading study data", Toast.LENGTH_SHORT).show();
                });
    }

    private void displayQuestionAndOptions() {
        if(position <fillList.size()){
            setupTimer();
            startTimer();
            Fill currentFill = fillList.get(position);
            String question = currentFill.getQuestion();
            String correctA = currentFill.getCorrect().toLowerCase();

            int num = position + 1;

            binding.studyTermSpeaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech = new TextToSpeech(FillActivity.this,new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                int result = textToSpeech.setLanguage(Locale.US);
                                if (result == TextToSpeech.LANG_MISSING_DATA ||
                                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    // Handle language data missing or not supported
                                }
                            } else {
                                // Handle initialization failure
                            }


                            speakOut(question);
                        }
                    });

                }
            });

            binding.fillNumIndicator.setText(num+"/"+fillList.size());
            binding.fillQuestionTV.setText(question);

            binding.fillCheckBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    if(binding.fillAnswerEdt.getText().toString().toLowerCase().equals(correctA.toLowerCase())){
                        score++;
                        binding.fillAnswerEdt.setBackgroundResource(R.drawable.right_answer);
                    }
                    else {
                        binding.fillAnswerEdt.setBackgroundResource(R.drawable.wrong_answer);
                        showIncorrectAnswerDialog(correctA);
                    }
                    binding.fillAnswerEdt.setEnabled(false);
                    binding.fillCheckBtn.setEnabled(false);
                    binding.fillCheckBtn.setAlpha(0.3f);
                    binding.fillNextBtn.setEnabled(true);
                    binding.fillNextBtn.setAlpha(1.0f);
                }
            });


        }
    }
    private void displayQuestionAndOptions2() {
        if(position <fillList.size()){
            setupTimer();
            startTimer();
            Fill currentFill = fillList.get(position);
            String question = currentFill.getQuestion();
            String correctA = currentFill.getCorrect().toLowerCase();

            int num = position + 1;

            binding.studyTermSpeaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech = new TextToSpeech(FillActivity.this,new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if (status == TextToSpeech.SUCCESS) {
                                int result = textToSpeech.setLanguage(new Locale("vi","VN"));
                                if (result == TextToSpeech.LANG_MISSING_DATA ||
                                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                    // Handle language data missing or not supported
                                }
                            } else {
                                // Handle initialization failure
                            }
                            speakOut(question);
                        }
                    });

                }
            });

            binding.fillNumIndicator.setText(num+"/"+fillList.size());
            binding.fillQuestionTV.setText(question);

            binding.fillCheckBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTimer();
                    if(binding.fillAnswerEdt.getText().toString().toLowerCase().equals(correctA.toLowerCase())){
                        score++;
                        binding.fillAnswerEdt.setBackgroundResource(R.drawable.right_answer);
                    }
                    else {
                        binding.fillAnswerEdt.setBackgroundResource(R.drawable.wrong_answer);
                        showIncorrectAnswerDialog(correctA);
                    }
                    binding.fillAnswerEdt.setEnabled(false);
                    binding.fillCheckBtn.setEnabled(false);
                    binding.fillCheckBtn.setAlpha(0.3f);
                    binding.fillNextBtn.setEnabled(true);
                    binding.fillNextBtn.setAlpha(1.0f);
                }
            });


        }
    }

    private void speakOut(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    private void setupTimer() {
        // Set up a countdown timer for 30 seconds with a tick interval of 1000 milliseconds (1 second)
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the UI with the remaining time
                binding.timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                // Handle the case when the timer finishes (e.g., user didn't answer in time)
                handleTimeout();
            }
        };
    }
    private void handleTimeout() {
        // Handle the case when the user didn't answer in time
        // You may want to show a message, reveal the correct answer, or take other actions
        left ++;
        Fill fill = fillList.get(position);
        showIncorrectAnswerDialog(fill.getCorrect());
        // Proceed to the next question or handle as needed
        binding.fillAnswerEdt.setEnabled(false);
        binding.fillCheckBtn.setEnabled(false);
        binding.fillCheckBtn.setAlpha(0.3f);
        binding.fillNextBtn.setEnabled(true);
        binding.fillNextBtn.setAlpha(1.0f);
    }

    private void startTimer() {
        // Start the countdown timer
        timer.start();
    }

    private void stopTimer() {
        // Stop the countdown timer
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the timer when the activity is destroyed to prevent memory leaks
        stopTimer();
    }
    private void showIncorrectAnswerDialog(String correctAnswer) {
        // Create a custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_fill_timeout, null);
        builder.setView(dialogView);

        // Set custom dialog title and message
        TextView dialogTitle = dialogView.findViewById(R.id.fill_incorrect_tv);
        TextView dialogMessage = dialogView.findViewById(R.id.dialog_correct_answer_tv);
        Button dialogButton = dialogView.findViewById(R.id.dialog_fill_ok_button);

        dialogTitle.setText("Incorrect Answer");
        dialogMessage.setText("The correct answer is:\n" + correctAnswer);

        // Set a listener for the OK button
        AlertDialog dialog = builder.create();
        dialog.show();

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the OK button click if needed
                dialog.dismiss();
            }
        });
    }
}
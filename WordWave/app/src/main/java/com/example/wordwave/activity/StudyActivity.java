package com.example.wordwave.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.databinding.ActivityStudyBinding;
import com.example.wordwave.model.Study;
import com.example.wordwave.model.Word;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudyActivity extends AppCompatActivity {


    ActivityStudyBinding binding;

    TextToSpeech textToSpeech;
    TextToSpeech textToSpeech2;
    ArrayList<Study> studyList;

    int count = 0;
    int position = 0;
    int score = 0;
    int left = 0;
    CountDownTimer timer;

    FirebaseFirestore db;

    boolean loadLanguage =false;

    boolean loadFavoriteStudyDataOnly = false;


    String topicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        studyList = new ArrayList<>();

        topicId = getIntent().getStringExtra("topicId");


        // Retrieve the state from SharedPreferences
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        loadLanguage = preferences.getBoolean("loadLanguageSelect", false);
        loadFavoriteStudyDataOnly = preferences.getBoolean("loadFavoriteStudyDataOnly", false);

        if (loadFavoriteStudyDataOnly) {
            // Clear the existing studyList
            studyList.clear();

            // Load only favorite study data
            loadFavoriteStudyData();

            // Reset the state in SharedPreferences
            saveStateInPreferences(false);
        } else if (loadLanguage) {
            loadStudyData2();
            saveStateInPreferences2(false);
        } else {
            // Load the regular study data
            loadStudyData();
        }
        binding.studySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });


        binding.studyNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                binding.studyNextBtn.setEnabled(false);
                binding.studyNextBtn.setAlpha(0.3f);

                enableAllOptions();
                position++;

                if(position == studyList.size()){

                }
                AnimatorSet animatorSet = new AnimatorSet();

                ObjectAnimator fadeOutQ = ObjectAnimator.ofFloat(binding.questionTV, "alpha", 0f, 100f);
                ObjectAnimator fadeInQ = ObjectAnimator.ofFloat(binding.questionTV, "alpha", 0f, 100f);
                ObjectAnimator fadeOutO1 = ObjectAnimator.ofFloat(binding.option1, "alpha", 1f, 0f);
                ObjectAnimator fadeInO1 = ObjectAnimator.ofFloat(binding.option1, "alpha", 0f, 1f);
                ObjectAnimator fadeOutO2 = ObjectAnimator.ofFloat(binding.option2, "alpha", 1f, 0f);
                ObjectAnimator fadeInO2 = ObjectAnimator.ofFloat(binding.option2, "alpha", 0f, 1f);
                ObjectAnimator fadeOutO3 = ObjectAnimator.ofFloat(binding.option3, "alpha", 1f, 0f);
                ObjectAnimator fadeInO3 = ObjectAnimator.ofFloat(binding.option3, "alpha", 0f, 1f);
                ObjectAnimator fadeOutO4 = ObjectAnimator.ofFloat(binding.option4, "alpha", 1f, 0f);
                ObjectAnimator fadeInO4 = ObjectAnimator.ofFloat(binding.option4, "alpha", 0f, 1f);

                animatorSet.play(fadeOutQ).with(fadeInQ);
                long optionFadeDuration = 200; // Điều chỉnh độ dài hiệu ứng cho mỗi option
                animatorSet.play(fadeOutO1).with(fadeInO1);
                animatorSet.play(fadeOutO2).with(fadeInO2);
                animatorSet.play(fadeOutO3).with(fadeInO3);
                animatorSet.play(fadeOutO4).with(fadeInO4);

                animatorSet.setDuration(500);

                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        // Xử lý khi bắt đầu animation
                        displayQuestionAndOptions();
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        // Xử lý khi kết thúc animation

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        // Xử lý khi animation bị hủy
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                        // Xử lý khi animation lặp lại (nếu cần)
                    }
                });

                animatorSet.start();


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
                studyList.clear();
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
                studyList.clear();
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

    private void loadStudyData() {
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


                    // Create Study objects from selected words
                    for (Word word : wordList) {
                        List<String> allDefinitions = new ArrayList<>();
                        allDefinitions.add(word.getDefinition()); // Correct definition
                        allDefinitions.addAll(getIncorrectOptions(wordList, word));

                        // Randomly shuffle the list of definitions
                        Collections.shuffle(allDefinitions);

                        // Create a Study object
                        Study study = new Study(word.getTerm(), allDefinitions.get(0),
                                allDefinitions.get(1), allDefinitions.get(2), allDefinitions.get(3),
                                word.getDefinition(), "", 0);

                        studyList.add(study);
                    }

                    // Display the first question and options
                    displayQuestionAndOptions();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyActivity.this, "Error loading study data", Toast.LENGTH_SHORT).show();
                });
    }
    private void loadStudyData2() {
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

                    // Create Study objects from selected words
                    for (Word word : wordList) {
                        List<String> allTerms = new ArrayList<>();
                        allTerms.add(word.getTerm()); // Correct term
                        allTerms.addAll(getIncorrectOptions2(wordList, word));

                        // Randomly shuffle the list of terms
                        Collections.shuffle(allTerms);

                        // Create a Study object with the question as the definition and options as terms
                        Study study = new Study(word.getDefinition(), allTerms.get(0),
                                allTerms.get(1), allTerms.get(2), allTerms.get(3),
                                word.getTerm(), "", 0);

                        studyList.add(study);
                    }

                    // Display the first question and options
                    displayQuestionAndOptions2();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyActivity.this, "Error loading study data", Toast.LENGTH_SHORT).show();
                });
    }
    private void loadFavoriteStudyData() {
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

//                    switch (wordSize){
//                        case 1:
//
//                    }
                    // Create Study objects from selected words
                    for (Word word : wordList) {
                        List<String> allDefinitions = new ArrayList<>();
                        allDefinitions.add(word.getDefinition()); // Correct definition
                        allDefinitions.addAll(getIncorrectOptions(wordList, word));

                        // Randomly shuffle the list of definitions
                        Collections.shuffle(allDefinitions);

                        // Create a Study object
                        Study study = new Study(word.getTerm(), allDefinitions.get(0),
                                allDefinitions.get(1), allDefinitions.get(2), allDefinitions.get(3),
                                word.getDefinition(), "", 0);

                        studyList.add(study);
                    }

                    // Display the first question and options
                    displayQuestionAndOptions();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudyActivity.this, "Error loading study data", Toast.LENGTH_SHORT).show();
                });
    }

    private List<String> getIncorrectOptions(List<Word> wordList, Word correctWord) {
        // Get all definitions from wordList except the correct definition
        List<String> incorrectOptions = new ArrayList<>();
        for (Word word : wordList) {
            if (!word.equals(correctWord)) {
                incorrectOptions.add(word.getDefinition());
            }
        }
        while (incorrectOptions.size() < 3) {
            incorrectOptions.addAll(incorrectOptions);
        }


        // Randomly shuffle the list of incorrect options
        Collections.shuffle(incorrectOptions);

        // Select up to 3 incorrect options
        return incorrectOptions.subList(0, Math.min(3, incorrectOptions.size()));
    }
    private List<String> getIncorrectOptions2(List<Word> wordList, Word correctWord) {
        // Get all definitions from wordList except the correct definition
        List<String> incorrectOptions = new ArrayList<>();
        for (Word word : wordList) {
            if (!word.equals(correctWord)) {
                incorrectOptions.add(word.getTerm());
            }
        }
        while (incorrectOptions.size() < 3) {
            incorrectOptions.addAll(incorrectOptions);
        }

        // Randomly shuffle the list of incorrect options
        Collections.shuffle(incorrectOptions);

        // Select up to 3 incorrect options
        return incorrectOptions.subList(0, Math.min(3, incorrectOptions.size()));
    }

    private void displayQuestionAndOptions() {
        resetOptionBackgrounds();

        if (position < studyList.size()) {
            setupTimer();
            startTimer();
            Study currentStudy = studyList.get(position);
            String question = currentStudy.getQuestion();
            List<String> options = currentStudy.getOptions();

            int subPosition = position+1;
            // Update your layout to display the current question
            binding.studyTermSpeaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech = new TextToSpeech(StudyActivity.this,new TextToSpeech.OnInitListener() {
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
            binding.questionTV.setText(question);
            binding.numIndicator.setText(subPosition +"/"+studyList.size());

            // Check the number of options available
            int numOptions = options.size();

            // Display the available options
            switch (numOptions) {
                case 2:
                    binding.option1.setText(options.get(0));
                    binding.option2.setText(options.get(1));
                    binding.option3.setVisibility(View.GONE);
                    binding.option4.setVisibility(View.GONE);
                    break;
                case 3:
                    binding.option1.setText(options.get(0));
                    binding.option2.setText(options.get(1));
                    binding.option3.setText(options.get(2));
                    binding.option4.setVisibility(View.GONE);
                    break;
                case 4:
                    binding.option1.setText(options.get(0));
                    binding.option2.setText(options.get(1));
                    binding.option3.setText(options.get(2));
                    binding.option4.setText(options.get(3));
                    break;
                default:
                    break;
            }

            // Add logic for handling user selection and scoring
            // ...
            binding.option1.setOnClickListener(v -> checkAnswer(binding.option1, currentStudy));
            binding.option2.setOnClickListener(v -> checkAnswer(binding.option2, currentStudy));

            if (numOptions >= 3) {
                binding.option3.setOnClickListener(v -> checkAnswer(binding.option3, currentStudy));
            }

            if (numOptions == 4) {
                binding.option4.setOnClickListener(v -> checkAnswer(binding.option4, currentStudy));
            }

        } else {
            Intent intent = new Intent(StudyActivity.this,ScoreActivity.class);
            intent.putExtra("score", String.valueOf(score));
            intent.putExtra("left", String.valueOf(left));
            intent.putExtra("numquizz", String.valueOf(studyList.size()));
            intent.putExtra("topicId",topicId);
            startActivity(intent);
            finish();
        }
    }
    private void displayQuestionAndOptions2() {
        resetOptionBackgrounds();

        if (position < studyList.size()) {
            setupTimer();
            startTimer();
            Study currentStudy = studyList.get(position);
            String question = currentStudy.getQuestion();
            List<String> options = currentStudy.getOptions();

            int subPosition = position+1;
            // Update your layout to display the current question
            binding.studyTermSpeaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textToSpeech = new TextToSpeech(StudyActivity.this,new TextToSpeech.OnInitListener() {
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
            binding.questionTV.setText(question);
            binding.numIndicator.setText(subPosition +"/"+studyList.size());

            // Check the number of options available
            int numOptions = options.size();

            // Display the available options
            switch (numOptions) {
                case 2:
                    binding.option1.setText(options.get(0));
                    binding.option2.setText(options.get(1));
                    binding.option3.setVisibility(View.GONE);
                    binding.option4.setVisibility(View.GONE);
                    break;
                case 3:
                    binding.option1.setText(options.get(0));
                    binding.option2.setText(options.get(1));
                    binding.option3.setText(options.get(2));
                    binding.option4.setVisibility(View.GONE);
                    break;
                case 4:
                    binding.option1.setText(options.get(0));
                    binding.option2.setText(options.get(1));
                    binding.option3.setText(options.get(2));
                    binding.option4.setText(options.get(3));
                    break;
                default:
                    break;
            }

            // Add logic for handling user selection and scoring
            // ...
            binding.option1.setOnClickListener(v -> checkAnswer(binding.option1, currentStudy));
            binding.option2.setOnClickListener(v -> checkAnswer(binding.option2, currentStudy));

            if (numOptions >= 3) {
                binding.option3.setOnClickListener(v -> checkAnswer(binding.option3, currentStudy));
            }

            if (numOptions == 4) {
                binding.option4.setOnClickListener(v -> checkAnswer(binding.option4, currentStudy));
            }

        } else {
            Intent intent = new Intent(StudyActivity.this,ScoreActivity.class);
            intent.putExtra("score", String.valueOf(score));
            intent.putExtra("left", String.valueOf(left));
            intent.putExtra("numquizz", String.valueOf(studyList.size()));
            intent.putExtra("topicId",topicId);
            startActivity(intent);
            finish();
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
    private void checkAnswer(AppCompatButton selectedOption, Study currentStudy) {
        // Kiểm tra xem đáp án có đúng không
        boolean isCorrect = selectedOption.getText().equals(currentStudy.getCorrectA());

        // Hiển thị màu sắc tương ứng
        if (isCorrect) {
            selectedOption.setBackgroundResource(R.drawable.right_answer);
            score++;
        } else {
            selectedOption.setBackgroundResource(R.drawable.wrong_answer);
            // Hiển thị đáp án đúng
            showCorrectAnswer();
        }
        // Vô hiệu hóa tất cả các nút phương án sau khi đã chọn
        disableAllOptions();

        // Kích hoạt nút "Next"
        binding.studyNextBtn.setEnabled(true);
        binding.studyNextBtn.setAlpha(1.0f);
    }

    private void showCorrectAnswer() {
        Study currentStudy = studyList.get(position);
        // Hiển thị đáp án đúng với màu xanh lá cây
        if (binding.option1.getText().equals(currentStudy.getCorrectA())) {
            binding.option1.setBackgroundResource(R.drawable.right_answer);
        } else if (binding.option2.getText().equals(currentStudy.getCorrectA())) {
            binding.option2.setBackgroundResource(R.drawable.right_answer);
        } else if (binding.option3.getText().equals(currentStudy.getCorrectA())) {
            binding.option3.setBackgroundResource(R.drawable.right_answer);
        } else if (binding.option4.getText().equals(currentStudy.getCorrectA())) {
            binding.option4.setBackgroundResource(R.drawable.right_answer);
        }
    }

    private void handleTimeout() {
        // Handle the case when the user didn't answer in time
        // You may want to show a message, reveal the correct answer, or take other actions

        left = left+1;
        showCorrectAnswer();
        disableAllOptions();
        // Proceed to the next question or handle as needed
        binding.studyNextBtn.setEnabled(true);
        binding.studyNextBtn.setAlpha(1.0f);
    }

    // ... (existing methods)

    private void disableAllOptions() {
        // Vô hiệu hóa tất cả các nút phương án
        binding.option1.setEnabled(false);
        binding.option2.setEnabled(false);
        binding.option3.setEnabled(false);
        binding.option4.setEnabled(false);
    }
    private void enableAllOptions() {
        // Kích hoạt tất cả các nút phương án
        binding.option1.setEnabled(true);
        binding.option2.setEnabled(true);
        binding.option3.setEnabled(true);
        binding.option4.setEnabled(true);
    }
    private void resetOptionBackgrounds() {
        // Reset the background of all options to the default state
        binding.option1.setBackgroundResource(R.drawable.btn_option_back);
        binding.option2.setBackgroundResource(R.drawable.btn_option_back);
        binding.option3.setBackgroundResource(R.drawable.btn_option_back);
        binding.option4.setBackgroundResource(R.drawable.btn_option_back);
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

}
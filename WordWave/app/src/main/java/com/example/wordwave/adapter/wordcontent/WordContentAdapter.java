package com.example.wordwave.adapter.wordcontent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;
import com.example.wordwave.adapter.word.WordViewHolder;
import com.example.wordwave.model.Topic;
import com.example.wordwave.model.Word;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WordContentAdapter extends FirestoreRecyclerAdapter<Word, WordContentAdapter.WordContentHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private TextToSpeech textToSpeech;
    public WordContentAdapter(@NonNull FirestoreRecyclerOptions<Word> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull WordContentHolder holder, int position, @NonNull Word model) {
        holder.term_tv.setText(model.getTerm());
        holder.definition_tv.setText(model.getDefinition());
        holder.speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech = new TextToSpeech(holder.speaker.getContext(),new TextToSpeech.OnInitListener() {
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

                        String termToSpeak = model.getTerm();
                        speakOut(termToSpeak);
                    }
                });
            }
        });
        // Set the initial state of the star icon based on the model's star status
        updateStarIcon(holder.star, model.isFavorite());

        // Check if the word is marked as a favorite in Firestore
        checkFirestoreFavoriteStatus(model, new FirestoreFavoriteStatusCallback() {
            @Override
            public void onFavoriteStatus(boolean isFavorite) {
                // Update the model's marked status based on Firestore favorite status
                model.setFavorite(isFavorite);

                // Update the star icon accordingly
                updateStarIcon(holder.star, isFavorite);
            }
        });

        // Set click listener for the star icon
        holder.star.setOnClickListener(v -> {


            // Toggle the star status in the model
            boolean newStarStatus = !model.isFavorite();
            model.setFavorite(newStarStatus);

            // Update the star icon accordingly
            updateStarIcon(holder.star, newStarStatus);

            updateFirestoreFavoriteStatus(model, newStarStatus);

            // TODO: Update the star status in Firestore if needed
            // For example: you might want to update the Firestore document for the word
            // with the new star status. Implement this based on your Firestore structure.
        });
    }
    private void updateFirestoreFavoriteStatus(Word word, boolean isFavorite) {
        // Update the "favorite" field in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference wordRef = db.collection("topic")
                .document(word.getTopicId())
                .collection("word")
                .document(word.getWordId());

        wordRef.update("favorite", isFavorite)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {
                    // Failed to update Firestore, handle the error
                });
    }
    private void checkFirestoreFavoriteStatus(Word word, FirestoreFavoriteStatusCallback callback) {
        if (word != null && word.getTopicId() != null && word.getWordId() != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference wordRef = db.collection("topic")
                    .document(word.getTopicId())
                    .collection("word")
                    .document(word.getWordId());

            wordRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Check the value of the "favorite" field in Firestore
                            boolean isFavorite = documentSnapshot.getBoolean("favorite");
                            callback.onFavoriteStatus(isFavorite);
                        } else {
                            // Document does not exist, handle accordingly
                            callback.onFavoriteStatus(false);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Failed to retrieve Firestore data, handle the error
                        callback.onFavoriteStatus(false);
                    });
        } else {
            // Handle the case where the word or its properties are null
            callback.onFavoriteStatus(false);
        }
    }
    private void saveFavoriteWords(List<Word> favoriteWords,Context context) {
        // Chuyển danh sách từ đã được đánh dấu thành chuỗi JSON
        Gson gson = new Gson();
        String favoriteWordsJson = gson.toJson(favoriteWords);

        // Lưu chuỗi JSON vào SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("favoriteWords", favoriteWordsJson);
        editor.apply();
    }
    private List<Word> getFavoriteWords(Context context) {
        // Lấy chuỗi JSON từ SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String favoriteWordsJson = sharedPreferences.getString("favoriteWords", "");

        // Chuyển chuỗi JSON thành danh sách từ đã được đánh dấu
        Gson gson = new Gson();
        Type type = new TypeToken<List<Word>>() {}.getType();
        List<Word> favoriteWords = gson.fromJson(favoriteWordsJson, type);

        // Kiểm tra nếu danh sách rỗng, trả về một danh sách trống
        return favoriteWords != null ? favoriteWords : new ArrayList<>();
    }
    private void updateStarIcon(ImageButton starButton, boolean isStarred) {
        int starIconResId = isStarred ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24;
        starButton.setImageResource(starIconResId);
    }
    private void speakOut(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @NonNull
    @Override
    public WordContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_item,parent,false);
        return new WordContentHolder(view);
    }

    class WordContentHolder extends  RecyclerView.ViewHolder{
        TextView term_tv;
        TextView definition_tv;
        ImageButton speaker;
        ImageButton star;


        public WordContentHolder(View view){
            super(view);
            term_tv = view.findViewById(R.id.word_term);
            definition_tv = view.findViewById(R.id.word_definition);
            speaker = view.findViewById(R.id.word_speaker);
            star = view.findViewById(R.id.star_marker);
        }
    }
    public Word getWordAtPosition(int position) {
        return getItem(position);
    }

    interface FirestoreFavoriteStatusCallback {
        void onFavoriteStatus(boolean isFavorite);
    }

}

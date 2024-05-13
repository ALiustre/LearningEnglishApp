package com.example.wordwave.adapter.flashcardAdapter;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;
import com.example.wordwave.activity.ScoreActivity;
import com.example.wordwave.model.Flashcard;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.List;
import java.util.Locale;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder>{

    private List<Flashcard> flashcards;
    private TextToSpeech textToSpeech;
    int know = 0;
    int still = 0;
    private SpeakerClickListener speakerClickListener;




    private FinishButtonClickListener finishButtonClickListener;

    boolean isWordSideUp;
    public FlashcardAdapter(List<Flashcard> flashcards, SpeakerClickListener speakerClickListener, FinishButtonClickListener finishButtonClickListener) {
        this.flashcards = flashcards;
        this.speakerClickListener = speakerClickListener;
        this.finishButtonClickListener = finishButtonClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        Flashcard flashcard = flashcards.get(position);
        int num = position + 1;

        holder.statusDisplay.setText(num + "/" + getItemCount());
        holder.wordTextView.setText(flashcard.getWord());
        holder.definitionTextView.setText(flashcard.getDefinition());

        // Create TextToSpeech instances for English and Vietnamese
        TextToSpeech englishTextToSpeech = new TextToSpeech(holder.termSpeaker.getContext(), null);
        TextToSpeech vietnameseTextToSpeech = new TextToSpeech(holder.termSpeaker.getContext(), null);

        // Determine the language for text-to-speech
        Locale englishLocale = Locale.US;
        Locale vietnameseLocale = new Locale("vi", "VN");

        holder.termSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Speak the word in English
                englishTextToSpeech.setLanguage(englishLocale);
                speakOut(englishTextToSpeech, flashcard.getWord());

                // Speak the definition in Vietnamese
                vietnameseTextToSpeech.setLanguage(vietnameseLocale);
                speakOut(vietnameseTextToSpeech, flashcard.getDefinition());
            }
        });
        holder.knowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                know = know +1;
                holder.knowBtn.setEnabled(false);
                holder.stillLearnBtn.setEnabled(false);
            }
        });
        holder.stillLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                still = still + 1;
                holder.knowBtn.setEnabled(false);
                holder.stillLearnBtn.setEnabled(false);
            }
        });
        if(num == getItemCount()){
            holder.finishButton.setEnabled(true);
            holder.finishButton.setAlpha(1.0f);
            holder.finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishButtonClickListener.onFinishButtonClick(know, still, getItemCount());
                }
            });
        }
        holder.settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }




    private void speakOut(TextToSpeech textToSpeech, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {

        EasyFlipView easyFlipView;
        TextView statusDisplay;
        TextView wordTextView;
        TextView definitionTextView;
        ImageButton termSpeaker, definitionSpeaker;
        ImageView settings;

        AppCompatButton knowBtn, stillLearnBtn, finishButton;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            easyFlipView = itemView.findViewById(R.id.easyFlipView);
            wordTextView = itemView.findViewById(R.id.wordTextView);
            definitionTextView = itemView.findViewById(R.id.definitionTextView);
            termSpeaker = itemView.findViewById(R.id.term_speaker);
            statusDisplay = itemView.findViewById(R.id.flashcard_status_display);
            knowBtn = itemView.findViewById(R.id.flashcard_know_btn);
            stillLearnBtn = itemView.findViewById(R.id.flashcard_still_learn_btn);
            finishButton = itemView.findViewById(R.id.flashcard_finish_button);
            definitionSpeaker = itemView.findViewById(R.id.definition_speaker);
            settings = itemView.findViewById(R.id.flashcard_settings);







//            itemView.setOnClickListener(v -> easyFlipView.flipTheView());
        }
    }
    public interface SpeakerClickListener {
        void onSpeakerClick(String text);
    }
    public interface FinishButtonClickListener {
        void onFinishButtonClick(int correctCount, int incorrectCount, int totalFlashcards);
    }
    public interface SettingsButtonClickListener {
        void onSettingButtonClick();
    }
}

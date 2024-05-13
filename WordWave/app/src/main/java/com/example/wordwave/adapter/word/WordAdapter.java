package com.example.wordwave.adapter.word;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;
import com.example.wordwave.model.Topic;
import com.example.wordwave.model.Word;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordViewHolder> {

    Context context;

    List<Word> wordList;

    public WordAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_word_card,parent,false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        final Word word = wordList.get(position);
        if(word == null){
            return;
        }

        holder.termEdt.setText(wordList.get(position).getTerm());
        holder.definitionEdt.setText(wordList.get(position).getDefinition());
        holder.termEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                word.setTerm(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed in this case
            }
        });

        holder.definitionEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed in this case
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                word.setDefinition(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed in this case
            }
        });
    }

    @Override
    public int getItemCount() {
        if(wordList != null){
            return wordList.size();
        }
        return 0;
    }
    public Word getWord(int position){
        return wordList.get(position);
    }

    public List<Word> getDataList() {
        return wordList;
    }
}

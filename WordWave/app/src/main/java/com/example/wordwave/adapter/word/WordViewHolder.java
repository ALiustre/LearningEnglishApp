package com.example.wordwave.adapter.word;

import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;


public class WordViewHolder extends RecyclerView.ViewHolder {

    EditText titleName;
    public EditText termEdt;
    public EditText definitionEdt;

    Adapter adapter;
    public WordViewHolder(@NonNull View itemView) {
        super(itemView);

        termEdt = itemView.findViewById(R.id.term_edt);
        definitionEdt = itemView.findViewById(R.id.definition_edt);
    }

    public WordViewHolder linkAdapter(Adapter adapter){
        this.adapter = adapter;
        return this;
    }
}

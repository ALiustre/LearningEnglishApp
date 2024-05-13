package com.example.wordwave.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.adapter.word.WordAdapter;
import com.example.wordwave.adapter.word.WordViewHolder;
import com.example.wordwave.model.Word;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditTopicActivity extends AppCompatActivity {

    String topicId,topicName;

    EditText topicTitle;
    FirestoreRecyclerAdapter<Word, WordViewHolder> editTopicAdapter;
    WordAdapter wordAdapter;
    RecyclerView editTopicRcv;
    Button add,remove;

    List<Word> wordList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);

        add = findViewById(R.id.edit_topic_add_description);
        remove = findViewById(R.id.edit_topic_remove_description);

        editTopicRcv = findViewById(R.id.edit_topic_rcv);
        topicTitle = findViewById(R.id.edit_topic_title_edt);

        Intent intent = getIntent();
        topicName = intent.getStringExtra("topicName");
        topicId = intent.getStringExtra("topicId");

        topicTitle.setText(topicName);

        editTopicRcv.setLayoutManager(new LinearLayoutManager(EditTopicActivity.this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(topicName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));


        // Connect data
        connectData();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordList.add(new Word("",""));
                wordAdapter.notifyItemInserted(wordList.size()-1);
                editTopicRcv.scrollToPosition(wordList.size()-1);
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wordList.size() > 0) {
                    wordList.remove(wordList.size() - 1);
                    wordAdapter.notifyItemRemoved(wordList.size());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, TopicActivity.class);
            intent.putExtra("topicId", topicId);
            intent.putExtra("topicName", topicName);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_edit_done) {
            updateData();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Document reference for the specific topic
        DocumentReference topicDocument = db.collection("topic").document(topicId);

        // Update the title
        String updatedTitle = topicTitle.getText().toString();
        topicDocument.update("name", updatedTitle)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success updating title", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error updating title", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Update the termNum
        int updatedTermNum = wordList.size();
        topicDocument.update("termNum", updatedTermNum)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Success updating termNum", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error updating termNum", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Collection reference for the words in the specific topic
        CollectionReference wordsCollection = db.collection("topic").document(topicId).collection("word");

        // Clear the existing words in the collection
        wordsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }

                    // Add the updated list of words to the collection
                    for (int i = 0; i < wordList.size(); i++) {
                        Word word = wordList.get(i);

                        // If wordId is present, use it to update the existing document
                        if (word.getWordId() != null && !word.getWordId().isEmpty()) {
                            word.setStatus("term left");
                            word.setTopicId(topicId);
                            word.setFavorite(false);
                            wordsCollection.document(word.getWordId()).set(word, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Success update", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error update", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            // If wordId is not present, Firestore will automatically generate a new document ID
                            wordsCollection.add(word)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                // Save the newly generated ID back to the Word object
                                                String newWordId = task.getResult().getId();


                                                Map<String, Object> wordMap = new HashMap<>();
                                                wordMap.put("status", "term left");
                                                wordMap.put("topicId", topicId);
                                                wordMap.put("wordId", newWordId);


                                                // Update the wordId in the database
                                                wordsCollection.document(newWordId).update(wordMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Success update", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), "Error update", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error update", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    // Handle the error
                    Toast.makeText(getApplicationContext(), "Error clearing existing words", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void connectData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        wordList = new ArrayList<>();

        // Query to get the words for the specific topic
        CollectionReference wordsCollection = db.collection("topic").document(topicId).collection("word");
        wordsCollection.orderBy("term").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Create Word object and add it to the wordList
                                Word word = document.toObject(Word.class);
                                wordList.add(word);
                            }

                            // Initialize and set up WordAdapter with the retrieved wordList
                            wordAdapter = new WordAdapter(getApplicationContext(), wordList);
                            editTopicRcv.setAdapter(wordAdapter);
                        } else {

                        }
                    }
                });
    }
}
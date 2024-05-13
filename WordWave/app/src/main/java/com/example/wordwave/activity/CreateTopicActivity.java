package com.example.wordwave.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
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
import com.example.wordwave.model.Word;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTopicActivity extends AppCompatActivity {

    EditText titleEdt;
    RecyclerView recyclerView;


    List<Word> list;
    WordAdapter adapter;
    Button add;
    Button remove;

    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);

        titleEdt = findViewById(R.id.title_edt);
        recyclerView = findViewById(R.id.create_topic_rcv);
        add = findViewById(R.id.add_description);
        remove = findViewById(R.id.remove_description);

        list = new ArrayList<>();

        adapter = new WordAdapter(getApplicationContext(),list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
        recyclerView.setAdapter(adapter);
        list.add(new Word("",""));
        list.add(new Word("",""));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(new Word("",""));
                adapter.notifyItemInserted(list.size()-1);
                recyclerView.scrollToPosition(list.size()-1);
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() > 0) {
                    list.remove(list.size() - 1);
                    adapter.notifyItemRemoved(list.size());
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create topic");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id == android.R.id.home) {
            navigateBackToLibrary();
            return true;
        }
        else if(id == R.id.action_done){
            createTopic();
            navigateBackToLibrary();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void navigateBackToLibrary() {
        Intent intent = new Intent(this, MainActivity.class);
        // Add flags to clear the back stack and set the destination to Library fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("FRAGMENT_TO_LOAD", "Library"); // You may need to adjust this extra key based on your implementation
        startActivity(intent);
        finish(); // Optional, depending on whether you want to keep FolderActivity in the back stack
    }

//    private  void createTopic(){
//        String topicTitle = titleEdt.getText().toString();
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // Create a unique key for the new topic
//
//        // Set the value of the new topic
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//
//        Map<String, Object> mainTopicMap = new HashMap<>();
//        mainTopicMap.put("name", topicTitle);
//        mainTopicMap.put("userId", userId);
//        mainTopicMap.put("termNum", recyclerView.getAdapter().getItemCount());
//
//        // Assuming db is your Firestore database instance
//        CollectionReference collection = db.collection("topic");
//
//// Create a new document
//        DocumentReference documentReference = collection.document();
//
//        String topicId = "";
//
//        documentReference
//                .set(mainTopicMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        topicId = documentReference.getId();
//                        Toast.makeText(getApplicationContext(), "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "Error writing document", Toast.LENGTH_SHORT).show();
//                    }
//                });
//        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
//            Word word = adapter.getWord(i);
//
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("term", word.getTerm());
//            map.put("definition", word.getDefinition());
//
//            db.collection("topic").document(topicId)
//                    .collection("word")
//                    .add(map)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(getApplicationContext(), "Create Success", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getApplicationContext(), "Create Fail", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
    private void createTopic() {
        String topicTitle = titleEdt.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference topicCollection = db.collection("topic");

        // Create a new document for the main topic
        Map<String, Object> mainTopicMap = new HashMap<>();
        mainTopicMap.put("name", topicTitle);
        mainTopicMap.put("userId", userId);
        mainTopicMap.put("termNum", list.size());


        topicCollection.add(mainTopicMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String topicId = documentReference.getId();
                        Toast.makeText(getApplicationContext(), "Main Topic created successfully!", Toast.LENGTH_SHORT).show();


                        Map<String, Object> additionalFields = new HashMap<>();
                        additionalFields.put("topicId",topicId);
                        additionalFields.put("isPublic", false);

                        // Update the main topic document with additional fields
                        documentReference.update(additionalFields)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Additional fields added successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error adding additional fields", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        // Iterate over the words in RecyclerView and add them to Firestore
                        for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
                            Word word = adapter.getWord(i);

                            // Create a new document for each word
                            Map<String, Object> wordMap = new HashMap<>();

                            wordMap.put("term", word.getTerm());
                            wordMap.put("definition", word.getDefinition());
                            wordMap.put("topicId", topicId);
                            wordMap.put("favorite", false);
                            wordMap.put("status", "term left");

                            db.collection("topic")
                                    .document(topicId)
                                    .collection("word")
                                    .add(wordMap)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Map<String, Object> additionalFields1 = new HashMap<>();
                                            additionalFields1.put("wordId",documentReference.getId());
                                            documentReference.update(additionalFields1)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                            Toast.makeText(getApplicationContext(), "Word added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error adding word", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error creating main topic", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

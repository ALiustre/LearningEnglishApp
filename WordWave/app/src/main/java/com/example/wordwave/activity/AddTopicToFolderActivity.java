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
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.adapter.addtopic.AddTopicToFolderAdapter;
import com.example.wordwave.adapter.wordcontent.WordContentAdapter;
import com.example.wordwave.model.SelectedTopic;
import com.example.wordwave.model.Topic;
import com.example.wordwave.model.Word;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AddTopicToFolderActivity extends AppCompatActivity {


    private AddTopicToFolderAdapter addTopicAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    Set<String> existingTopicIds = new HashSet<>();
    Set<String> selectedTopicIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic_to_folder);


        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set up RecyclerView
        recyclerView = findViewById(R.id.add_topic_to_folder_rvc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Folder");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));

        connectData();
        checkExistingTopics();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_topic_to_folder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id == android.R.id.home) {
            String folderName = getIntent().getStringExtra("folderName");
            String folderId = getIntent().getStringExtra("folderId");
            navigateBack(folderName,folderId);
            return true;
        }
        if( id == R.id.action_add_topic_to_folder){

            addSelectedTopicsToFirestore();
            navigateBackToLibrary();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkExistingTopics() {
        String folderId = getIntent().getStringExtra("folderId");

        // Retrieve the existing topic IDs from the 'topicsfolder' collection of the folder
        db.collection("folders")
                .document(folderId)
                .collection("topicsfolder")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        existingTopicIds.add(document.getId());
                    }

                    // Set existing topic IDs in the adapter
                    addTopicAdapter.setExistingTopicIds(existingTopicIds);
                })
                .addOnFailureListener(e -> {
                    // Handle failure to retrieve existing topics
                    Toast.makeText(getApplicationContext(), "Error checking existing topics", Toast.LENGTH_SHORT).show();
                });
    }


    private void addSelectedTopicsToFirestore() {
        String folderId = getIntent().getStringExtra("folderId");
        selectedTopicIds = addTopicAdapter.getSelectedTopicIds();

        // Assuming you have a Firestore collection named "folders" to store the folders
        CollectionReference selectedTopicsRef = db.collection("folders");

        for (String topicId : selectedTopicIds) {
            // Check if the topic already exists in the collection
            selectedTopicsRef.document(folderId).collection("topicsfolder").document(topicId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // The topic already exists, handle accordingly (e.g., show a message)
                        } else {
                            // The topic does not exist, add it to Firestore
                            Map<String, Object> mainTopicMap = new HashMap<>();
                            mainTopicMap.put("topicId", topicId);
                            mainTopicMap.put("userId", auth.getCurrentUser().getUid());

                            selectedTopicsRef.document(folderId).collection("topicsfolder").document(topicId)
                                    .set(mainTopicMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Add topic successful", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Add topic failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle errors
                            Toast.makeText(getApplicationContext(), "Error checking topic existence", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // Optionally, you can perform additional actions or show a message
    }




    private void navigateBack(String folderName,String folderId) {
        Intent intent = new Intent(this, FolderActivity.class);
        intent.putExtra("folderName", folderName);
        intent.putExtra("folderId", folderId);
        startActivity(intent);
        finish(); // Optional, depending on whether you want to keep CreateTopicActivity in the back stack
    }



    private void connectData() {
        Query query = db.collection("topic")
                .whereEqualTo("userId", auth.getCurrentUser().getUid());

        // Set up FirestoreRecyclerOptions with the query and Topic model class
        FirestoreRecyclerOptions<Topic> options = new FirestoreRecyclerOptions.Builder<Topic>()
                .setQuery(query, Topic.class)
                .build();

        // Initialize the adapter
        addTopicAdapter = new AddTopicToFolderAdapter(options, AddTopicToFolderActivity.this);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(addTopicAdapter);
        addTopicAdapter.startListening();
    }
    private void navigateBackToLibrary() {
        Intent intent = new Intent(this, MainActivity.class);
        // Add flags to clear the back stack and set the destination to Library fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("FRAGMENT_TO_LOAD", "Library"); // You may need to adjust this extra key based on your implementation
        startActivity(intent);
        finish(); // Optional, depending on whether you want to keep FolderActivity in the back stack
    }
}
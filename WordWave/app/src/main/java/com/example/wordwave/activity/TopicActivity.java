package com.example.wordwave.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;
import com.example.wordwave.adapter.wordcontent.WordContentAdapter;
import com.example.wordwave.model.Topic;
import com.example.wordwave.model.Word;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicActivity extends AppCompatActivity {



    private static final int PICK_WORD_FILE_REQUEST_CODE = 456;
    String topicName, topicId, userId;
    TextView topicNameTv;
    WordContentAdapter adapter;
    RecyclerView topicRcv;
    CardView flashcardCV, studyCV, fillCV;

    private boolean isTopicPublic = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        topicRcv = findViewById(R.id.topic_content_rcv);
        topicNameTv = findViewById(R.id.topic_name);
        flashcardCV = findViewById(R.id.flashcard_cardView);
        studyCV = findViewById(R.id.study_cardView);
        fillCV = findViewById(R.id.fill_cardView);


        Intent intent = getIntent();
        topicName = intent.getStringExtra("topicName");
        topicId = intent.getStringExtra("topicId");
        userId = intent.getStringExtra("userId");

        topicNameTv.setText(topicName);
        // Set up RecyclerView
        topicRcv.setLayoutManager(new LinearLayoutManager(TopicActivity.this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(topicName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));


        // Connect data
        connectData();

        flashcardCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(TopicActivity.this,FlashcardsActivity.class);
                intent1.putExtra("topicId", topicId);
                intent1.putExtra("userId", userId);
                startActivity(intent1);
            }
        });
        studyCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(TopicActivity.this,StudyActivity.class);
                intent2.putExtra("topicId", topicId);
                intent2.putExtra("userId", userId);
                startActivity(intent2);
            }
        });
        fillCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(TopicActivity.this,FillActivity.class);
                intent3.putExtra("topicId", topicId);
                intent3.putExtra("userId", userId);
                startActivity(intent3);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topic_detail, menu);
        // Update menu items based on user ID
        MenuItem editTopicItem = menu.findItem(R.id.action_edit_topic);
        MenuItem deleteTopicItem = menu.findItem(R.id.action_delete_topic);
        MenuItem publishTopicItem = menu.findItem(R.id.action_publish_topic);

        // Check if the userId matches the current user's ID
        boolean isCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId);

        if (!isCurrentUser) {
            // Disable or hide menu items if the user is not the owner
            editTopicItem.setEnabled(false);
            deleteTopicItem.setEnabled(false);
            publishTopicItem.setEnabled(false);
        }

        updatePublishButtonLabel(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id == android.R.id.home) {
            navigateBackToLibrary();
            return true;
        }
        if( id == R.id.action_edit_topic){
            Intent intent = new Intent(TopicActivity.this, EditTopicActivity.class);
            intent.putExtra("topicId",topicId);
            intent.putExtra("topicName",topicName);
            startActivity(intent);
            return true;
        }
        if( id == R.id.action_import_topic){
            openWordFilePicker();
            return true;
        }
        if( id == R.id.action_delete_topic){
            deleteTopic();
            return true;
        }
        if(id == R.id.action_publish_topic){

            publishTopic();
            return true;
        }
        if(id == R.id.action_export_topic){
            export();
        }
        if(id == R.id.action_favorite_topic){
            Intent intent = new Intent(TopicActivity.this, TopicFavoriteActivity.class);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        }
        if(id == R.id.action_ranking_topic){
            Intent intent = new Intent(TopicActivity.this, RankingActivity.class);
            intent.putExtra("topicId", topicId);
            intent.putExtra("userId",userId);
            startActivity(intent);
        }



        return super.onOptionsItemSelected(item);
    }
    private void openWordFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");  // Change this according to the file type you want to select (e.g., text/csv)

        startActivityForResult(intent, PICK_WORD_FILE_REQUEST_CODE);
    }

    // ... (existing code)

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ... (existing code)

        if (requestCode == PICK_WORD_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri fileUri = data.getData();
                if (fileUri != null) {
                    importWordsFromCSV(fileUri);
                }
            }
        }
    }
    private void export(){
        StringBuilder data = new StringBuilder();
        data.append("term,definition");
        for(int i =0;i< topicRcv.getAdapter().getItemCount();i++){
            Word s = adapter.getWordAtPosition(i);
            data.append("\n"+ s.getTerm()+","+s.getDefinition());
        }
        try {
            FileOutputStream out = this.openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write(data.toString().getBytes());
            out.close();

            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(this.getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(getApplicationContext(),"com.example.wordwave.fileprovider",filelocation);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM,path);
            startActivity(Intent.createChooser(intent, "Send mail"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void publishTopic() {
        FirebaseFirestore.getInstance().collection("topic").document(topicId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    boolean isPublish = documentSnapshot.getBoolean("isPublic");

                    // Toggle the value of isPublish
                    isPublish = !isPublish;

                    // Update isPublish on Firestore
                    final boolean finalIsPublish = isPublish; // Create a final variable
                    FirebaseFirestore.getInstance().collection("topic").document(topicId)
                            .update("isPublic", finalIsPublish)
                            .addOnSuccessListener(aVoid -> {
                                isTopicPublic = finalIsPublish;
                                 // Update button label based on the current state
                                Toast.makeText(TopicActivity.this, "Topic updated successfully", Toast.LENGTH_SHORT).show();
                                reloadActivity();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TopicActivity.this, "Failed to update topic", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TopicActivity.this, "Failed to fetch topic details", Toast.LENGTH_SHORT).show();
                });
    }
    private void reloadActivity() {
        Intent intent = getIntent();
        finish(); // Close the current activity
        startActivity(intent); // Start a new instance of the activity
    }
    private void updatePublishButtonLabel(Menu menu) {
            FirebaseFirestore.getInstance().collection("topic").document(topicId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Boolean isPublic = documentSnapshot.getBoolean("isPublic");

                        if (isPublic != null) {
                            if (isPublic) {
                                menu.findItem(R.id.action_publish_topic).setTitle("Make Private");
                            } else {
                                menu.findItem(R.id.action_publish_topic).setTitle("Public topic");
                            }
                        } else {
                            // Handle the case when isPublic is null (optional)
                            // You might want to set a default label or do something else
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý khi không thể lấy giá trị từ Firestore
                        Toast.makeText(TopicActivity.this, "Failed to fetch topic details", Toast.LENGTH_SHORT).show();
                    });
    }

    private void importWordsFromCSV(Uri fileUri) {
        try {
            ArrayList<String> lines = loadFile(fileUri);
            int importedWordsCount = 0;
            // Start processing lines from index 1 to skip the first line
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");

                if (parts.length >= 2) {
                    String wordTerm = parts[0].trim();
                    String wordDefinition = parts[1].trim();

                    // Continue processing or add the word to the Firestore Database
                    addWordToDatabase(wordTerm, wordDefinition);
                    importedWordsCount++;
                }
            }
            updateTermNum(importedWordsCount);

            Toast.makeText(this, "Word import successful.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error importing words.", Toast.LENGTH_SHORT).show();
        }
    }
    private void deleteTopic() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Fetch all word documents in the "word" collection under the given topic
        db.collection("topic").document(topicId).collection("word").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Step 2: Delete each word document
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String wordId = document.getId();
                        deleteWord(wordId);
                    }

                    // Step 3: Delete the topic document
                    db.collection("topic").document(topicId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                deleteTopicInFolderCollection();
                                Toast.makeText(TopicActivity.this, "Topic deleted successfully", Toast.LENGTH_SHORT).show();
                                navigateBackToLibrary();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(TopicActivity.this, "Failed to delete topic", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TopicActivity.this, "Failed to fetch words", Toast.LENGTH_SHORT).show();
                });
    }
    private void deleteTopicInFolderCollection() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("folders")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot folderDocument : queryDocumentSnapshots.getDocuments()) {
                        String folderId = folderDocument.getId();

                        // Delete the corresponding entry in topicsfolder collection within each folder
                        db.collection("folders").document(folderId).collection("topicsfolder")
                                .whereEqualTo("topicId", topicId)
                                .get()
                                .addOnSuccessListener(query -> {
                                    for (DocumentSnapshot document : query.getDocuments()) {
                                        document.getReference().delete();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure to delete topic in folder collection
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch folders
                });
    }

    private void deleteWord(String wordId) {
        FirebaseFirestore.getInstance().collection("topic").document(topicId)
                .collection("word").document(wordId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Word deleted successfully
                })
                .addOnFailureListener(e -> {
                    // Failed to delete word
                });
    }
    private void updateTermNum(int importedWordsCount) {
        FirebaseFirestore.getInstance().collection("topic").document(topicId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Get the current termNum value
                        long currentTermNum = documentSnapshot.getLong("termNum");

                        // Calculate the new termNum value
                        long newTermNum = currentTermNum + importedWordsCount;

                        // Update the termNum in the Firestore database
                        FirebaseFirestore.getInstance().collection("topic").document(topicId)
                                .update("termNum", newTermNum)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(TopicActivity.this,
//                                                "Updated termNum successfully.",
//                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(TopicActivity.this,
//                                                "Failed to update termNum.",
//                                                Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TopicActivity.this,
                                "Failed to get current termNum.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addWordToDatabase(String term, String definition) {
        // Thực hiện thêm từ vào Firestore Database theo logic của bạn
        // ...

        // Ví dụ:
        Map<String, Object> map = new HashMap<>();
        map.put("term", term);
        map.put("definition", definition);
        map.put("topicId", topicId);
        map.put("favorite", false);
        map.put("status", "term left");

        FirebaseFirestore.getInstance().collection("topic").document(topicId).collection("word")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String wordId = documentReference.getId();

                        Map<String, Object> additionalFields = new HashMap<>();
                        additionalFields.put("wordId",wordId);

                        documentReference.update(additionalFields).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(TopicActivity.this,
//                                "Something went wrong.",
//                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    private ArrayList<String> loadFile(Uri fileUri) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        InputStream inputStream = getContentResolver().openInputStream(fileUri);

        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            reader.close();
            streamReader.close();
            inputStream.close();
        }

        return lines;
    }

    private void navigateBackToLibrary() {
        Intent intent = new Intent(this, MainActivity.class);
        // Add flags to clear the back stack and set the destination to Library fragment
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("FRAGMENT_TO_LOAD", "Library"); // You may need to adjust this extra key based on your implementation
        startActivity(intent);
        finish(); // Optional, depending on whether you want to keep FolderActivity in the back stack
    }

    private void connectData() {
        // Set up FirestoreRecyclerOptions with a query to fetch words ordered by "term"
        FirestoreRecyclerOptions<Word> options = new FirestoreRecyclerOptions.Builder<Word>()
                .setQuery(FirebaseFirestore.getInstance().collection("topic").document(topicId).collection("word").orderBy("term"), Word.class)
                .build();

        // Initialize and set up the WordContentAdapter
        adapter = new WordContentAdapter(options);
        topicRcv.setAdapter(adapter);
        adapter.startListening();
    }

}

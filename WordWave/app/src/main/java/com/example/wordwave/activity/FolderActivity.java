package com.example.wordwave.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wordwave.R;
import com.example.wordwave.adapter.topic.TopicAdapter;
import com.example.wordwave.model.SelectedTopic;
import com.example.wordwave.model.Topic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FolderActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TopicAdapter topicAdapter;

    int termNum;
    List<String> titleList;
    List<Topic> topicList;
    String folderName;
    String folderId;
    TextView folderNameTv;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        folderNameTv = findViewById(R.id.folder_name);

        topicList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        folderName = getIntent().getStringExtra("folderName");
        folderId = getIntent().getStringExtra("folderId");

        folderNameTv.setText(folderName);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Get the userId
            String userId = currentUser.getUid();

            // Fetch user details from Firestore
            getUserDetails(userId);
        }

        loadTopicsFromFirebase();
        recyclerView = findViewById(R.id.folder_content_rcv);  // Use the actual ID of your RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicAdapter = new TopicAdapter(topicList);  // Assuming your TopicAdapter constructor takes a List<Topic>
        recyclerView.setAdapter(topicAdapter);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Folder");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.white)));



    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_folder_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        if (id == android.R.id.home) {
            navigateBackToLibrary();
            return true;
        }
        if( id == R.id.action_add_topic_folder){
            Intent intent = new Intent(FolderActivity.this, AddTopicToFolderActivity.class);
            intent.putExtra("folderName", folderName);
            intent.putExtra("folderId", folderId);
            startActivity(intent);
            finish();

            return true;
        }
        if( id == R.id.action_edit_folder){
            showEditDialog();
            return true;
        }
        if( id == R.id.action_delete_folder){
            deleteFolder();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void getUserDetails(String userId) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // User document found, update UI
                            String username = document.getString("userName");
                            String userImageURL = document.getString("photoUrl");
                            updateUI(username, userImageURL);
                        } else {
                            // User document not found
                        }
                    } else {
                        // Error fetching user details
                    }
                });
    }

    private void updateUI(String username, String userImageURL) {
        // Update the TextView for username
        TextView usernameTextView = findViewById(R.id.folder_user_name);
        if (username != null) {
            usernameTextView.setText(username);
        }

        // Update the CircleImageView for userImage
        CircleImageView userImageView = findViewById(R.id.folder_image_view_user);
        if (userImageURL != null) {
            Glide.with(FolderActivity.this).load(userImageURL).into(userImageView);
        }
    }
    private void navigateBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional, depending on whether you want to keep CreateTopicActivity in the back stack
    }

    private void loadTopicsFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("topic").whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        titleList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentName = document.getString("name");
                            String topicId = document.getId();
                            if (document.contains("termNum") && document.getLong("termNum") != null) {
                                // Retrieve the value of 'termNum' from the document
                                termNum = document.getLong("termNum").intValue();
                            }
                            db.collection("folders")
                                    .document(folderId)
                                    .collection("topicsfolder")
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                                                if(i.toObject(SelectedTopic.class).getTopicId().equals(topicId)){
                                                    titleList.add(documentName);
                                                    topicList.add(new Topic(topicId,userId, documentName, Long.parseLong(String.valueOf(termNum))));
                                                }
                                            }
                                            if (topicAdapter != null) {
                                                // Update the adapter with the new data
                                                topicAdapter.setTopics(topicList);
                                            }
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                            // Retrieve data from the document
                            // Now you can use documentId and data as needed

                        }
                        topicAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void deleteFolder() {
        db.collection("folders").document(folderId)
                .collection("topicsfolder")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Duyệt qua danh sách chủ đề và xóa từng chủ đề

                    // Xóa toàn bộ topicsfolder liên quan đến thư mục
                    db.collection("folders").document(folderId)
                            .collection("topicsfolder")
                            .get()
                            .addOnSuccessListener(query -> {
                                for (QueryDocumentSnapshot document : query) {
                                    document.getReference().delete();
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Xử lý lỗi khi xóa topicsfolder
                                Toast.makeText(getApplicationContext(), "Failed to delete topicsfolder", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lấy danh sách chủ đề
                    Toast.makeText(getApplicationContext(), "Failed to fetch topics in the folder", Toast.LENGTH_SHORT).show();
                });
        // Delete the folder
        db.collection("folders").document(folderId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Folder deleted
                    navigateBackToLibrary();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to delete the folder
                    Toast.makeText(getApplicationContext(), "Failed to delete folder", Toast.LENGTH_SHORT).show();
                });
    }
    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_folder_name, null);
        builder.setView(view);

        EditText editTextFolderName = view.findViewById(R.id.edit_folder_name);
        Button buttonSave = view.findViewById(R.id.edit_folder_ok_button);
        Button buttonCancel = view.findViewById(R.id.edit_folder_cancel_button);

        // Pre-fill the edit text with the current folder name
        editTextFolderName.setText(folderName);

        AlertDialog dialog = builder.create();

        buttonSave.setOnClickListener(v -> {
            // Get the new folder name from the edit text
            String newFolderName = editTextFolderName.getText().toString();

            // Update the folder name and dismiss the dialog
            updateFolderName(newFolderName);
            dialog.dismiss();
        });
        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }
    private void updateFolderName(String newFolderName) {
        db.collection("folders").document(folderId)
                .update("folderName", newFolderName)
                .addOnSuccessListener(aVoid -> {
                    // Folder name updated
                    folderName = newFolderName;
                    folderNameTv.setText(folderName);
                    Toast.makeText(getApplicationContext(), "Folder name updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to update folder name
                    Toast.makeText(getApplicationContext(), "Failed to update folder name", Toast.LENGTH_SHORT).show();
                });
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
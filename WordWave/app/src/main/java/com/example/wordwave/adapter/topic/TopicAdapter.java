package com.example.wordwave.adapter.topic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordwave.R;
import com.example.wordwave.activity.EditTopicActivity;
import com.example.wordwave.activity.TopicActivity;
import com.example.wordwave.model.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicViewHolder> {


    List<Topic> topicList;
    public TopicAdapter(List<Topic> topicList) {

        this.topicList = topicList;
    }

    public void setTopics(List<Topic> topics) {
        this.topicList = topics;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item,parent,false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder,int position) {
        holder.name.setText(topicList.get(position).getName().toString());
        holder.termNumber.setText(topicList.get(position).getTermNum().toString());
        String userIdLogging = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userId = topicList.get(position).getUserId();
        String topicName = topicList.get(position).getName();
        String topicId = topicList.get(position).getTopicId();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userIdLogging.trim().equals( userId.trim())) {
                    Context context = holder.cardView.getContext();

                    // Get the topic name


                    // Create an Intent and pass the topic name
                    Intent intent = new Intent(context, TopicActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("topicName", topicName);
                    intent.putExtra("topicId", topicId);

                    // Start the TopicActivity
                    context.startActivity(intent);
                }
                else {
                    Context context = holder.cardView.getContext();
                    showConfirmationDialog(context, topicId, userId, topicName, userIdLogging);
                }
            }
        });

        // Fetch user details from Firestore
        fetchUserDetails(userId, holder);
    }
    private void isUserSubscribed(String topicId, String userIdLogging, OnCompleteListener<DocumentSnapshot> onComplete) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference topicRef = db.collection("topic").document(topicId);

        // Use listener to handle the asynchronous Firestore data retrieval
        topicRef.get().addOnCompleteListener(onComplete);
    }

    private void showConfirmationDialog(Context context, String topicId, String userId, String topicName, String userIdLogging) {
        // Check if the user is already subscribed to the topic
        isUserSubscribed(topicId, userIdLogging, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Topic topic = document.toObject(Topic.class);
                    List<String> subuserIds = topic.getSubUsersId();

                    boolean isSubscribed = subuserIds != null && subuserIds.contains(userIdLogging);

                    if (isSubscribed) {
                        // If the user is already subscribed, open TopicActivity directly
                        Intent intent = new Intent(context, TopicActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("topicName", topicName);
                        intent.putExtra("topicId", topicId);
                        context.startActivity(intent);
                    } else {
                        // Show the confirmation dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Do you want to study this topic?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Add subuserId to Firestore
                                addSubuserToTopic(topicId, userIdLogging);

                                // Open TopicActivity
                                Intent intent = new Intent(context, TopicActivity.class);
                                intent.putExtra("userId", userId);
                                intent.putExtra("topicName", topicName);
                                intent.putExtra("topicId", topicId);
                                context.startActivity(intent);
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
    }
    private void addSubuserToTopic(String topicId, String subuserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference topicRef = db.collection("topic").document(topicId);

        // Use arrayUnion to add the new subuser to the existing array
        topicRef.update("subUserIds", FieldValue.arrayUnion(subuserId))
                .addOnSuccessListener(aVoid -> {
                    // Successfully added the subuser to the topic
                    // You can handle it as needed
                })
                .addOnFailureListener(e -> {
                    // Handle error while updating the document
                });
    }
    private void fetchUserDetails(String userId, TopicViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // User document found, extract user details
                String userName = documentSnapshot.getString("userName");
                String userImage = documentSnapshot.getString("photoUrl");

                // Set user details in the ViewHolder
                holder.setUserDetails(userName, userImage);
            } else {
                // User document not found, set default or handle accordingly
                holder.setUserDetails("Default Name", "https://example.com/default_image.jpg");
            }
        }).addOnFailureListener(e -> {
            // Handle errors here
            holder.setUserDetails("Default Name", "https://example.com/default_image.jpg");
        });
    }

    @Override
    public int getItemCount() {
        if(topicList != null){
            return topicList.size();
        }
        return 0;
    }



}

package com.example.wordwave.adapter.addtopic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wordwave.R;
import com.example.wordwave.activity.AddTopicToFolderActivity;
import com.example.wordwave.adapter.folder.FolderAdapter;
import com.example.wordwave.model.Topic;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

public class AddTopicToFolderAdapter extends FirestoreRecyclerAdapter<Topic,AddTopicToFolderAdapter.AddTopicHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */

    private Set<String> selectedTopicIds = new HashSet<>();
    private Set<String> existingTopicIds;
    private Context context;
    public AddTopicToFolderAdapter(@NonNull FirestoreRecyclerOptions<Topic> options, Context context) {
        super(options);
        this.existingTopicIds = new HashSet<>();
        this.context = context;
    }
    public void setExistingTopicIds(Set<String> existingTopicIds) {
        this.existingTopicIds = existingTopicIds;
        notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull AddTopicHolder holder, int position, @NonNull Topic model) {
        holder.name.setText(model.getName());
        holder.termNum.setText(String.valueOf(model.getTermNum()));


        String topicId = getItem(position).getTopicId();
        selectedTopicIds =existingTopicIds;
        holder.checkBox.setChecked(selectedTopicIds.contains(topicId));

        holder.checkBox.setEnabled(true); // Enable the CheckBox
        holder.checkBox.setVisibility(View.VISIBLE);

        // Set click listener for CheckBox
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSelection(position);
            }
        });

        // Get the userId from the current Topic
        String userId = model.getUserId();

        // Fetch user details from Firestore and set them in the ViewHolder
        fetchUserDetails(userId, holder);


    }
    private void toggleSelection(int position) {
        String topicId = getItem(position).getTopicId();
        if (selectedTopicIds.contains(topicId)) {
            selectedTopicIds.remove(topicId);
            existingTopicIds.remove(topicId);
        } else {
            selectedTopicIds.add(topicId);
        }
        if (!existingTopicIds.contains(topicId)) {
            removeUncheckedTopicFromFirestore(topicId);
        }

        notifyDataSetChanged();
    }

    private void removeUncheckedTopicFromFirestore(String topicId) {
        // Assuming you have a Firestore collection named "folders" to store the folders
        String folderId = ((AddTopicToFolderActivity) context).getIntent().getStringExtra("folderId");
        CollectionReference selectedTopicsRef = FirebaseFirestore.getInstance().collection("folders");

        // Remove the topic from Firestore
        selectedTopicsRef.document(folderId).collection("topicsfolder").document(topicId)
                .delete()
                .addOnSuccessListener(unused -> {

                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });

    }

    public Set<String> getSelectedTopicIds() {
        return selectedTopicIds;
    }

    @NonNull
    @Override
    public AddTopicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.topic_item,parent,false);
        return new AddTopicToFolderAdapter.AddTopicHolder(view);
    }

    private void fetchUserDetails(String userId, AddTopicHolder holder) {
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

    class AddTopicHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView termNum;

        TextView userName;
        ImageView userImage;
        CardView cardView;
        CheckBox checkBox;

        public AddTopicHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.topic_name);
            termNum = itemView.findViewById(R.id.term_number_textview);
            cardView = itemView.findViewById(R.id.item_topic_card);
            userImage = itemView.findViewById(R.id.topic_image_view_user);
            userName = itemView.findViewById(R.id.topic_user_name);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void setUserDetails(String userName, String userImage1) {
            // Set user details in the ViewHolder
            this.userName.setText(userName);

            // Load user image using your preferred image loading library (e.g., Glide)
            // You may replace 'placeholder_image_url' with your default image URL
            Glide.with(itemView.getContext())
                    .load(userImage1)
                    .placeholder(R.drawable.default_avatar)  // Placeholder image
                    .error(R.drawable.default_avatar)        // Error image if loading fails
                    .into(userImage);
        }
    }
}

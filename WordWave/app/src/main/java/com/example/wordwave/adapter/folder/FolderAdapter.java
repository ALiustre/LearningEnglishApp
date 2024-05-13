package com.example.wordwave.adapter.folder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wordwave.R;
import com.example.wordwave.activity.AddTopicToFolderActivity;
import com.example.wordwave.activity.FolderActivity;
import com.example.wordwave.activity.TopicActivity;
import com.example.wordwave.adapter.wordcontent.WordContentAdapter;
import com.example.wordwave.model.Folder;
import com.example.wordwave.model.Topic;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FolderAdapter extends FirestoreRecyclerAdapter<Folder, FolderAdapter.FolderHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private FirestoreRecyclerOptions<Folder> options;
    public FolderAdapter(@NonNull FirestoreRecyclerOptions<Folder> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FolderHolder holder, int position, @NonNull Folder model) {
        holder.folderName.setText(model.getFolderName());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.cardView.getContext();

                String folderName = model.getFolderName();

                Intent intent = new Intent(context, FolderActivity.class);
                intent.putExtra("folderId", model.getFolderId());
                intent.putExtra("folderName", folderName);

                context.startActivity(intent);
            }
        });

        // Get the userId from the current Folder
        String userId = model.getUserId();

        // Fetch user details from Firestore and set them in the ViewHolder
        fetchUserDetails(userId, holder);
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_folder,parent,false);
        return new FolderAdapter.FolderHolder(view);
    }

    class FolderHolder extends RecyclerView.ViewHolder {
        TextView folderName;
        ImageView photoUrl;
        TextView userName;
        CardView cardView;
        public FolderHolder(View view){
            super(view);
            folderName = view.findViewById(R.id.folder_name);
            photoUrl= view.findViewById(R.id.folder_image_view_user);
            userName = view.findViewById(R.id.folder_user_name);
            cardView = view.findViewById(R.id.item_folder_card);

        }
        public void setUserDetails(String userName, String userImage1) {
                // Set user details in the ViewHolder
                this.userName.setText(userName);

                // Load user image using Glide
                Glide.with(itemView.getContext())
                        .load(userImage1)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(photoUrl);
        }
    }
    private void fetchUserDetails(String userId, FolderHolder holder) {
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
}

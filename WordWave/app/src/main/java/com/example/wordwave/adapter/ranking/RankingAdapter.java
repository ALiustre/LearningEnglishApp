package com.example.wordwave.adapter.ranking;

import static android.os.Build.VERSION_CODES.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wordwave.R;
import com.example.wordwave.adapter.folder.FolderAdapter;
import com.example.wordwave.model.Folder;
import com.example.wordwave.model.Ranking;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RankingAdapter extends FirestoreRecyclerAdapter<Ranking, RankingAdapter.RankingHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RankingAdapter(@NonNull FirestoreRecyclerOptions<Ranking> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RankingHolder holder, int position, @NonNull Ranking model) {


        String userIdLogging = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.score.setText(String.valueOf(model.getScore()));

        fetchUserDetails(model.getUserId(),holder);
    }

    @NonNull
    @Override
    public RankingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.wordwave.R.layout.item_ranking,parent,false);
        return new RankingAdapter.RankingHolder(view);
    }

    class RankingHolder extends RecyclerView.ViewHolder{
        TextView score, username;
        ImageView photoUrl;
        public RankingHolder(@NonNull View itemView) {
            super(itemView);
            score = itemView.findViewById(com.example.wordwave.R.id.score_ranking);
            username = itemView.findViewById(com.example.wordwave.R.id.username_ranking);
            photoUrl = itemView.findViewById(com.example.wordwave.R.id.user_avatar_ranking);

        }
        public void setUserDetails(String userName, String userImage1) {
            // Set user details in the ViewHolder
            this.username.setText(userName);

            // Load user image using Glide
            Glide.with(itemView.getContext())
                    .load(userImage1)
                    .placeholder(com.example.wordwave.R.drawable.default_avatar)
                    .error(com.example.wordwave.R.drawable.default_avatar)
                    .into(photoUrl);
        }
    }
    private void fetchUserDetails(String userId, RankingAdapter.RankingHolder holder) {
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

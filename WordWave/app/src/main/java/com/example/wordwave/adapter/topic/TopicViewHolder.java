package com.example.wordwave.adapter.topic;

import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wordwave.R;


public class TopicViewHolder extends RecyclerView.ViewHolder {

    TextView name;
    TextView termNumber;

    TextView userName;
    ImageView userImage;
    CardView cardView;

    Adapter adapter;
    public TopicViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.topic_name);
        termNumber = itemView.findViewById(R.id.term_number_textview);
        cardView = itemView.findViewById(R.id.item_topic_card);
        userImage = itemView.findViewById(R.id.topic_image_view_user);
        userName = itemView.findViewById(R.id.topic_user_name);
    }

    public TopicViewHolder linkAdapter(Adapter adapter){
        this.adapter = adapter;
        return this;
    }
    public void setUserDetails(String userName1, String userImage1) {
        userName.setText(userName1);
        // Load the user image using a library like Glide or Picasso
        // Example using Glide:
        Glide.with(itemView.getContext())
                .load(userImage1)
                .placeholder(R.drawable.default_avatar)  // Placeholder image
                .error(R.drawable.default_avatar)        // Error image if loading fails
                .into(userImage);
    }
}

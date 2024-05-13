package com.example.wordwave.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.wordwave.activity.CreateTopicActivity;
import com.example.wordwave.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AddDialogFragment extends BottomSheetDialogFragment {

    TextView addTopicButton;
    TextView addFolderButton;

    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_dialog,container ,false);

        addTopicButton = view.findViewById(R.id.open_add_topic_button);
        addFolderButton = view .findViewById(R.id.open_add_folder_button);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        addTopicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), CreateTopicActivity.class);
                requireContext().startActivity(intent);
            }
        });
        addFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateFolderDialog folderDialog = new CreateFolderDialog(requireContext(), userId);
                folderDialog.show();
            }
        });

        return view;
    }
}

package com.example.wordwave.fragment.library.folder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wordwave.R;
import com.example.wordwave.adapter.folder.FolderAdapter;
import com.example.wordwave.model.Folder;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FolderFragment extends Fragment {

    private FolderAdapter folderAdapter;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    public static FolderFragment newInstance() {
        return new FolderFragment();
    }
    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Override
    public void onStop() {
        super.onStop();
        folderAdapter.stopListening();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_folder, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.folder_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up FirestoreRecyclerOptions with the query


        return view;
    }
    private void getData(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = db.collection("folders").whereEqualTo("userId", userId);
        FirestoreRecyclerOptions<Folder> options = new FirestoreRecyclerOptions.Builder<Folder>()
                .setQuery(query, Folder.class)
                .build();

        // Create an instance of FolderAdapter
        folderAdapter = new FolderAdapter(options);

        // Set the adapter to the RecyclerView
        recyclerView.setAdapter(folderAdapter);
        folderAdapter.startListening();
    }

}
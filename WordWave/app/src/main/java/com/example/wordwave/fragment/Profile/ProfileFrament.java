package com.example.wordwave.fragment.Profile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wordwave.activity.MainActivity;
import com.example.wordwave.R;
import com.example.wordwave.databinding.FragmentProfileFramentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFrament extends Fragment {

    private View mView;
    Bitmap bitmap;
    private ProfileViewModel mViewModel;
    FragmentProfileFramentBinding binding;
    private ImageView imageView;
    private TextView nameView, emailView, update;
    private Uri mUri;
    private Uri ImageUri;
    private MainActivity mMainActivity;
    private FirebaseStorage storage;
    private FirebaseFirestore fireStore;
    private StorageReference mStorageRef;
    private String PhotoUrl;
    public static ProfileFrament newInstance() {
        return new ProfileFrament();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileFramentBinding.inflate(inflater, container, false);

        View rootView = binding.getRoot();


        imageView = rootView.findViewById(R.id.edt_image);
        nameView = rootView.findViewById(R.id.edt_name);
        emailView = rootView.findViewById(R.id.edt_email);
        update = rootView.findViewById(R.id.update_btn);

        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReference();



        mMainActivity = (MainActivity) getActivity();
        mMainActivity.setTitle("Profile");
        mView =rootView;
        setUserInformation();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckStoragePermission();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user == null){
                    return;
                }
                String fullname = nameView.getText().toString().trim();
                if (mUri != null) {
                    // Sử dụng Glide để tải hình ảnh từ URI
                    Glide.with(getActivity()).load(mUri).error(R.drawable.default_avatar).into(imageView);
                }
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(fullname)
                        .setPhotoUri(mUri)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    saveUserInformationToFirestore(user.getUid(), fullname, PhotoUrl, user.getEmail());
                                    Toast.makeText(getActivity(), "Update Success",Toast.LENGTH_SHORT).show();
                                    mMainActivity.showUserInformation();
                                }
                            }
                        });

            }
        });
        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }
    private void saveUserInformationToFirestore(String userId, String fullName, String photoUrl, String email) {
        // Tạo một bản ghi người dùng mới hoặc cập nhật thông tin người dùng hiện tại
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userName", fullName);
        userMap.put("photoUrl", photoUrl);
        userMap.put("userId", userId);
        userMap.put("email", email);

        // Lưu thông tin người dùng vào Firestore
        fireStore.collection("users")
                .document(userId)
                .set(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "User information saved to Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to save user information to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setUserInformation(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        nameView.setText(user.getDisplayName());
        emailView.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getPhotoUrl()).error(R.drawable.default_avatar).into(imageView);

    }
    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        launcher.launch(intent);
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if(data != null && data.getData()!= null){
                        mUri = data.getData();

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),mUri);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (mUri!=null){
                        imageView.setImageBitmap(bitmap);
                    }
                    uploadImage();
                }
            }
    );
    public void CheckStoragePermission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else {
                openGallery();
            }
        }
        else {
            openGallery();
        }
    }


    public void uploadImage(){
        if (mUri != null) {
            final StorageReference myRef = mStorageRef.child("photo/" + mUri.getLastPathSegment());
            myRef.putFile(mUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (uri != null) {
                                // Update PhotoUrl with the actual URL obtained from Firebase Storage
                                PhotoUrl = uri.toString();
                                updateFirestoreWithUserInfo();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure to get download URL
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure to upload image
                    Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void updateFirestoreWithUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String fullname = nameView.getText().toString().trim();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullname)
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            saveUserInformationToFirestore(user.getUid(), fullname, PhotoUrl, user.getEmail());
                            Toast.makeText(getActivity(), "Update Success", Toast.LENGTH_SHORT).show();
                            mMainActivity.showUserInformation();
                        }
                    }
                });
    }


}
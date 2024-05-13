package com.example.wordwave.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.wordwave.activity.MainActivity;
import com.example.wordwave.databinding.ActivityLoginBinding;
import com.example.wordwave.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;

//    @Overridee
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser =mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, coPass, fullName, phone;
                email = binding.edtEmail.getText().toString();
                password = binding.edtPass.getText().toString();
                coPass = binding.edtConfirmPass.getText().toString();


                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter Email",Toast.LENGTH_SHORT).show();
                }
                if (!isValidEmail(email)) {
                    Toast.makeText(Register.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return; // Dừng quá trình đăng ký
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter Password",Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(coPass)) {
                    Toast.makeText(Register.this, "Please confirm your password", Toast.LENGTH_SHORT).show();
                    return; // Dừng quá trình đăng ký
                }

                if(!password.equals(coPass)){
                    Toast.makeText(Register.this, "Confirm password is incorrect",Toast.LENGTH_SHORT).show();
                }
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String userId = mAuth.getCurrentUser().getUid();
                                    addUserToFirestore(userId, email);
                                    // Sign in success, update UI with the signed-in user's information
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
    private void addUserToFirestore(String userId, String email) {
        // Create a new user data map
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("userId", userId);

        // Add the user to the "users" collection with the user's UID as the document ID
        db.collection("users")
                .document(userId)
                .set(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "User added to Firestore", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Failed to add user to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
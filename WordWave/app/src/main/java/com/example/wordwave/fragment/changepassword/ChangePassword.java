package com.example.wordwave.fragment.changepassword;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordwave.R;
import com.example.wordwave.databinding.FragmentChangePasswordBinding;
import com.example.wordwave.fragment.Profile.ProfileFrament;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends Fragment {

    private View mView;
    private FragmentChangePasswordBinding binding;
    private ChangePasswordViewModel mViewModel;
    private EditText curPassEdt, newPassEdt,confPassEdt;
    private TextView autButton, changeButton;
    private String curPass;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    public static ChangePassword newInstance() {
        return new ChangePassword();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding =FragmentChangePasswordBinding.inflate(inflater, container, false);

        View rootView = binding.getRoot();
        curPassEdt = rootView.findViewById(R.id.current_pass_edt);
        newPassEdt =rootView.findViewById(R.id.new_pass_edt);
        confPassEdt = rootView.findViewById(R.id.confirm_pass_edt);
        autButton = rootView.findViewById(R.id.auth_btn);
        changeButton = rootView.findViewById(R.id.change_password_btn);

        mView = rootView;

        curPassEdt.setEnabled(true);
        newPassEdt.setEnabled(false);
        confPassEdt.setEnabled(false);

        getActivity().setTitle("Change Password");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user.equals("")){
            Toast.makeText(getActivity(),"Something went wrong! User's details not available",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), ProfileFrament.class);
            startActivity(intent);
        }else {
            reAuthenticateUser(user);
        }



        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChangePasswordViewModel.class);
        // TODO: Use the ViewModel
    }

    private void reAuthenticateUser(FirebaseUser user){
        autButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curPass = curPassEdt.getText().toString();

                if(TextUtils.isEmpty(curPass)){
                    Toast.makeText(getActivity(), "Password is needed", Toast.LENGTH_SHORT).show();
                    curPassEdt.setError("Please enter your current password to authenticate");
                    curPassEdt.requestFocus();
                }
                else {

                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), curPass);

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                curPassEdt.setEnabled(false);
                                newPassEdt.setEnabled(true);
                                confPassEdt.setEnabled(true);

                                autButton.setEnabled(false);
                                changeButton.setEnabled(true);

                                Toast.makeText(getActivity(), "Password has been verified",Toast.LENGTH_SHORT).show();

                                changeButton.setBackgroundTintList(ContextCompat.getColorStateList(
                                        getActivity(), R.color.teal_200));
                                changeButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(user);
                                    }
                                });

                            }
                            else {
                                try {
                                    throw task.getException();
                                }
                                catch (Exception e){
                                    Toast.makeText(getActivity(), e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser user) {
        String newPass = newPassEdt.getText().toString();
        String cfmPass = confPassEdt.getText().toString();

        if(TextUtils.isEmpty(newPass)){
            Toast.makeText(getActivity(), "New password is needed",Toast.LENGTH_SHORT).show();
            newPassEdt.setError("Please enter your new password");
            newPassEdt.requestFocus();
        } else if (TextUtils.isEmpty(cfmPass)) {
            Toast.makeText(getActivity(), "Please confirm your new password",Toast.LENGTH_SHORT).show();
            newPassEdt.setError("Please re-enter your new password");
            newPassEdt.requestFocus();
        }
        else if (!newPass.matches(cfmPass)) {
            Toast.makeText(getActivity(), "Password didn't match",Toast.LENGTH_SHORT).show();
            newPassEdt.setError("Please re-enter same password");
            newPassEdt.requestFocus();
        }
        else if (curPass.matches(newPass)) {
            Toast.makeText(getActivity(), "New password can not be same as old password",Toast.LENGTH_SHORT).show();
            newPassEdt.setError("Please enter a new password");
            newPassEdt.requestFocus();
        }
        else {
            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getActivity(),"Password has been changed", Toast.LENGTH_SHORT).show();
                        newPassEdt.setText("");
                        confPassEdt.setText("");

                    }else {
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

}
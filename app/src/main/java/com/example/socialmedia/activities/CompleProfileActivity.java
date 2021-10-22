package com.example.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.models.User;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.UserProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompleProfileActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    TextInputEditText txtUsername;
    TextInputEditText txtPhone;
    Button btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comple_profile);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        txtUsername = findViewById(R.id.txtCompletaUsurio);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        txtPhone = findViewById(R.id.txtCompletePhone);

        btnConfirmar.setOnClickListener(v -> {
            CompleteRegister();
        });

    }

    private void CompleteRegister()
    {
        String userName = txtUsername.getText().toString();
        String phone = txtPhone.getText().toString();
        if(!userName.isEmpty() && !phone.isEmpty())
        {
            String id = mAuthProvider.GetUid();

            User user = new User();
            user.setId(id);
            user.setUsername(userName);
            user.setPhone(phone);
            user.setTimeStamp(new Date().getTime());

            mUserProvider.Update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(CompleProfileActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(CompleProfileActivity.this, "Error al crear usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
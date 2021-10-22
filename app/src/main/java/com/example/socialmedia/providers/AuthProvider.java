package com.example.socialmedia.providers;

import android.content.Intent;
import android.widget.Toast;

import com.example.socialmedia.activities.HomeActivity;
import com.example.socialmedia.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider()
    {
        mAuth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> login(String email, String password)
    {
        return mAuth.signInWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> LoginGoogle(GoogleSignInAccount googleSignInAccount)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return mAuth.signInWithCredential(credential);
    }

    public String GetUid()
    {
        if(mAuth.getCurrentUser() != null)
        return mAuth.getCurrentUser().getUid();

        return null;
    }

    public FirebaseUser getUserSession()
    {
        if(mAuth.getCurrentUser() != null)
        {
            return mAuth.getCurrentUser();
        }
        else{
            return null;
        }
    }

    public String GetEmail()
    {
        if(mAuth.getCurrentUser() != null)
        {
            return mAuth.getCurrentUser().getEmail();
        }

        return null;
    }

    public Task<AuthResult> Register(String email, String password)
    {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public void logOut()
    {
        if(mAuth != null)
        {
            mAuth.signOut();
        }
    }

}

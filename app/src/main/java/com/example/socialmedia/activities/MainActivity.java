package com.example.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.models.User;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.UserProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    TextInputEditText txtEmail, txtPassword;
    Button btnLogin;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    SignInButton btnLoginGoogle;
    AlertDialog mDialog;

    private final int RC_SIGN_IN = 1;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtEmail = findViewById(R.id.txtLoginEmail);
        txtPassword = findViewById(R.id.txtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargando")
                .setCancelable(false).build();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mAuthProvider.getUserSession() != null)
        {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public void Registrar(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void Login()
    {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        mDialog.show();
        mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    mDialog.dismiss();
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Correo/Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Error al iniciar con google", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        mDialog.show();
        mAuthProvider.LoginGoogle(account).addOnCompleteListener(task ->{
           if(task.isSuccessful())
           {
               String id = mAuthProvider.GetUid();
               CheckUserExists(id);
           }
           else{
               mDialog.dismiss();
               Toast.makeText(this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
           }
        });

    }



    private void CheckUserExists(String id)
    {
        mUserProvider.GetUser(id).addOnSuccessListener(documentSnapshot->{

            if(documentSnapshot.exists())
            {
                mDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            else{
                String email = mAuthProvider.GetEmail();
                User user = new User();
                user.setEmail(email);
                user.setId(id);
                mUserProvider.CreateUser(user).addOnCompleteListener(task ->{
                    mDialog.dismiss();
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(MainActivity.this, CompleProfileActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Error al iniciar con google", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

}
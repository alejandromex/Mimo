package com.example.socialmedia.activities;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.models.User;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.UserProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText txtEmail, txtUserName, txtPassword, txtConfirmPassword, txtPhone;
    Button btnRegister;
    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btnRegistrarse);
        txtEmail = findViewById(R.id.txtRegistroEmail);
        txtPassword = findViewById(R.id.txtRegistroPassword);
        txtConfirmPassword = findViewById(R.id.txtRegistroPasswordConfirmar);
        txtUserName = findViewById(R.id.txtRegistroUsuario);
        txtPhone = findViewById(R.id.txtRegisterPhone);
        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        mDialog = new SpotsDialog.Builder().setContext(this).setMessage("Cargando").setCancelable(false).build();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });



    }


    public void BackToLogin(View view)
    {
        finish();
    }

    private void Register()
    {
        mDialog.show();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPass = txtConfirmPassword.getText().toString();
        String userName = txtUserName.getText().toString();
        String phone = txtPhone.getText().toString();
        
        if(!email.equals("") && !password.equals("") && !confirmPass.equals("") && !userName.equals("") && !phone.isEmpty())
        {
            if(VerifyEmail(email))
            {
                if(password.equals(confirmPass))
                {
                    if(password.length() >= 6)
                    {
                        CreateUser(email, password, userName, phone);
                    }
                    else{
                        mDialog.dismiss();
                        Toast.makeText(this, "La contraseña debe ser mayor a 5 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                mDialog.dismiss();
                Toast.makeText(this, "Correo no valido", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            mDialog.dismiss();
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean VerifyEmail(String email)
    {
        String expression = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void CreateUser(String email, String password, String userName, String phone)
    {


        mAuthProvider.Register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if(task.isSuccessful())
                {

                    String id = mAuthProvider.GetUid();
                    User user = new User();
                    user.setId(id);
                    user.setEmail(email);
                    user.setUsername(userName);
                    user.setPhone(phone);
                    user.setTimeStamp(new Date().getTime());
                    mUserProvider.CreateUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            mDialog.dismiss();
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                mDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    finish();
                }
                else{
                    mDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error al crear el usuario", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
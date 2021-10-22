package com.example.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.example.socialmedia.adapters.MyPostsAdapter;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.PostProvider;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    TextView txtTItlePhone, txtPhone, txtName, txtEmail, txtTotalPost, txtNoPostUser;
    CircleImageView imgUser;
    ImageView imgCover;
    String userId;
    UserProvider userProvider;
    PostProvider postProvider;
    MyPostsAdapter myPostsAdapter;
    AuthProvider authProvider;
    RecyclerView rvUserPost;
    Toolbar toolbar;
    FloatingActionButton mFabChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        imgCover = findViewById(R.id.imgUserCover);
        imgUser = findViewById(R.id.imgUserProfile);
        txtEmail = findViewById(R.id.txtUserEmail);
        txtPhone = findViewById(R.id.txtUserPhone);
        txtName = findViewById(R.id.txtUserName);
        txtTotalPost = findViewById(R.id.txtUserPostNumber);
        txtTItlePhone = findViewById(R.id.txtTitlePhoneUser);
        txtNoPostUser = findViewById(R.id.txtUserNoPublications);
        rvUserPost = findViewById(R.id.rvUserPosts);
        toolbar = findViewById(R.id.toolbar);
        mFabChat = findViewById(R.id.fabchat);



        authProvider = new AuthProvider();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUserPost.setLayoutManager(linearLayoutManager);

        userProvider = new UserProvider();
        postProvider = new PostProvider();
        userId = getIntent().getStringExtra("idUser");

        if(userId.equals(authProvider.GetUid()))
        {
            mFabChat.setVisibility(View.GONE);
        }

        userProvider.GetUser(userId).addOnSuccessListener(user ->{
            if(user.exists())
            {
                FillDataUser(user);
                GetTotalPost(user.getString("id"));
                checkIfExistsPost();
            }
        });

        mFabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity();
            }


        });
    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", authProvider.GetUid());
        intent.putExtra("idUser2", userId);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = postProvider.getPostsByUser(userId);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        myPostsAdapter = new MyPostsAdapter(options, this);
        rvUserPost.setAdapter(myPostsAdapter);
        myPostsAdapter.startListening();
    }

    private void checkIfExistsPost()
    {
        postProvider.getPostsByUser(userId).addSnapshotListener((sc, ex)->{
            int size = sc.size();
            if(size == 0)
            {
                txtNoPostUser.setText("No hay publicaciones");
                txtNoPostUser.setTextColor(Color.BLACK);
            }
            else{
                txtNoPostUser.setText("Publicaciones");
                txtNoPostUser.setTextColor(Color.RED);
            }
        });
    }

    private void GetTotalPost(String userId)
    {
        postProvider.getPostsByUser(userId).get().addOnSuccessListener(t ->{
            int numberPost = t.size();
            txtTotalPost.setText(String.valueOf(numberPost));
        });
    }

    private void FillDataUser(DocumentSnapshot user)
    {
        if(user.contains("image_cover"))
        {
            String image_cover = user.getString("image_cover");
            Picasso.with(this).load(image_cover).into(imgCover);
        }
        if(user.contains("image_profile"))
        {
            String image_profile = user.getString("image_profile");
            Picasso.with(this).load(image_profile).into(imgUser);
        }

        if(user.contains("username"))
        {
            txtName.setText(user.getString("username"));
        }
        if(user.contains("phone"))
        {
            txtPhone.setText(user.getString("phone"));
            txtTItlePhone.setText("Celular:");
        }
        if(user.contains("email"))
        {
            txtEmail.setText(user.getString("email"));
        }


    }
}
package com.example.socialmedia.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.EditProfileActivity;
import com.example.socialmedia.activities.PostActivity;
import com.example.socialmedia.adapters.MyPostsAdapter;
import com.example.socialmedia.adapters.PostsAdapter;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.PostProvider;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    LinearLayout mLinearLayout;
    ImageView imgProfileCover;
    TextView txtName, txtPhone, txtEmail, txtPhoneTitle, txtPostNumber, txtNoPosts;
    CircleImageView imgProfile;
    AuthProvider authProvider;
    UserProvider userProvider;
    PostProvider postProvider;
    View mView;
    RecyclerView rvMyPost;

    MyPostsAdapter postsAdapter;


    public ProfileFragment() {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {

        }
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = postProvider.getPostsByUser(authProvider.GetUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        postsAdapter = new MyPostsAdapter(options, getContext());
        rvMyPost.setAdapter(postsAdapter);
        postsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        postsAdapter.stopListening();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mLinearLayout = mView.findViewById(R.id.btnEditProfile);

        txtName = mView.findViewById(R.id.txtProfileName);
        txtPhone = mView.findViewById(R.id.txtProfilePhone);
        txtEmail = mView.findViewById(R.id.txtEmailProfile);
        txtPostNumber = mView.findViewById(R.id.txtPostNumber);
        txtPhoneTitle = mView.findViewById(R.id.txtTitlePhone);
        imgProfile = mView.findViewById(R.id.imgProfile);
        imgProfileCover = mView.findViewById(R.id.imgProfileCover);
        txtNoPosts = mView.findViewById(R.id.txtNoPublicacions);
        rvMyPost = mView.findViewById(R.id.rvMyPost);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvMyPost.setLayoutManager(linearLayoutManager);

        authProvider = new AuthProvider();
        userProvider = new UserProvider();
        postProvider = new PostProvider();

        GetInformationUser();
        checkIfExistsPost();
        getPostNumber();


        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });
        return mView;
    }

    private void checkIfExistsPost()
    {
        postProvider.getPostsByUser(authProvider.GetUid()).addSnapshotListener((sc, ex)->{
            int size = sc.size();
            if(size == 0)
            {
                txtNoPosts.setText("No hay publicaciones");
                txtNoPosts.setTextColor(Color.BLACK);
            }
            else{
                txtNoPosts.setText("Publicaciones");
                txtNoPosts.setTextColor(Color.RED);
            }
        });
    }

    private void GetInformationUser()
    {
        String uid = authProvider.GetUid();
        userProvider.GetUser(uid).addOnSuccessListener(document ->{
           if(document.exists())
           {
               if(document.contains("image_cover") && !document.getString("image_cover").isEmpty())
               {
                   Picasso.with(getContext()).load(document.getString("image_cover")).into(imgProfileCover);
               }
               if(document.contains("image_profile") && !document.getString("image_profile").isEmpty())
               {
                   Picasso.with(getContext()).load(document.getString("image_profile")).into(imgProfile);
               }
               if(document.contains("phone") && !document.getString("phone").isEmpty() )
               {
                    txtPhone.setText(document.getString("phone"));
                   txtPhoneTitle.setText("Telefono:");
               }
               if(document.contains("username") && !document.getString("username").isEmpty())
               {
                    txtName.setText(document.getString("username"));
               }
               if(document.contains("email") && !document.getString("email").isEmpty())
               {
                    txtEmail.setText(document.getString("email"));
               }
           }
        });
    }

    private void goToEditProfile()
    {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getPostNumber()
    {
        postProvider.getPostsByUser(authProvider.GetUid()).get().addOnSuccessListener(t ->{
           int numberPost = t.size();

           txtPostNumber.setText(String.valueOf(numberPost));
        });
    }
}
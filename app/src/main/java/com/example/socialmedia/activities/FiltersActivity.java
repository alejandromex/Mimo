package com.example.socialmedia.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.socialmedia.R;
import com.example.socialmedia.adapters.PostsAdapter;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FiltersActivity extends AppCompatActivity {

    String category;
    AuthProvider mAuthProvider;
    RecyclerView mRvFilter;
    PostProvider mPostProvider;
    PostsAdapter mPostAdapter;
    Toolbar mToolbar;
    TextView txtTotalFIlters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mAuthProvider = new AuthProvider();
        mAuthProvider = new AuthProvider();
        mPostProvider = new PostProvider();
        mRvFilter = findViewById(R.id.rvFilter);
        mToolbar = findViewById(R.id.toolbar);
        txtTotalFIlters = findViewById(R.id.txtTotalFilter);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category = getIntent().getStringExtra("category");
        mRvFilter.setLayoutManager(new GridLayoutManager( FiltersActivity.this,2));

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mPostProvider.getPostByCategoryAndTimestamp(category);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        mPostAdapter = new PostsAdapter(options, FiltersActivity.this, txtTotalFIlters);
        mRvFilter.setAdapter(mPostAdapter);
        mPostAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPostAdapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.home)
        {
            finish();
        }

        return true;
    }
}
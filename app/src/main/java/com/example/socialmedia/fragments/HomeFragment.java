package com.example.socialmedia.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.CreditoActivity;
import com.example.socialmedia.activities.MainActivity;
import com.example.socialmedia.activities.PostActivity;
import com.example.socialmedia.adapters.PostsAdapter;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;


public class HomeFragment extends Fragment {

    View mView;
    TextView btnRegistarCredito;
    FloatingActionButton btnLogOut;
    AuthProvider authProvider;

    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mView = inflater.inflate(R.layout.fragment_home, container, false);
        btnRegistarCredito = mView.findViewById(R.id.btnRegistarCredito);
        btnLogOut = mView.findViewById(R.id.btnLogOut);
        authProvider = new AuthProvider();
        btnLogOut.setOnClickListener(v ->{
            authProvider.logOut();
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        });

        btnRegistarCredito.setOnClickListener(v ->{
            goToCreditos();
        });

        setHasOptionsMenu(true);
        return mView;
    }

    private void goToCreditos()
    {
        Intent intent = new Intent(getContext(), CreditoActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }



}
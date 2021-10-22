package com.example.socialmedia.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmedia.R;
import com.example.socialmedia.adapters.ChatAdapter;
import com.example.socialmedia.adapters.PostsAdapter;
import com.example.socialmedia.models.Chat;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.ChatsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatsFragment extends Fragment {

    ChatAdapter chatAdapter;
    RecyclerView rv;
    View mView;
    AuthProvider authProvider;

    ChatsProvider chatProvider;

    public ChatsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_chats, container, false);
        rv = mView.findViewById(R.id.rvChats);
        chatProvider = new ChatsProvider();
        authProvider = new AuthProvider();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        return mView;

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = chatProvider.getAll(authProvider.GetUid());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
        chatAdapter = new ChatAdapter(options, getContext());
        rv.setAdapter(chatAdapter);
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatAdapter.stopListening();
    }
}
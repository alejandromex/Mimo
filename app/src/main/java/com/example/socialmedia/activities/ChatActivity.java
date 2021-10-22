package com.example.socialmedia.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.adapters.MessagesAdapter;
import com.example.socialmedia.adapters.MyPostsAdapter;
import com.example.socialmedia.models.Chat;
import com.example.socialmedia.models.Message;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.ChatsProvider;
import com.example.socialmedia.providers.MessageProvider;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String extraIdUser1;
    String extraIdUser2;
    EditText txtMessage;
    ImageView imgSendMessage;
    String idChat;

    ChatsProvider chatProvider;
    MessageProvider messageProvider;
    AuthProvider authProvider;
    UserProvider userProvider;
    View mActionBarView;
    RecyclerView mRvMessage;
    MessagesAdapter mAdapter;


    CircleImageView imgProfileChat;
    TextView txtViewRelativeTime;
    ImageView imgBack;
    TextView txtViewUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        extraIdUser1 = getIntent().getStringExtra("idUser1");
        extraIdUser2 = getIntent().getStringExtra("idUser2");
        idChat = getIntent().getStringExtra("idChat");

        chatProvider = new ChatsProvider();
        messageProvider = new MessageProvider();
        authProvider = new AuthProvider();
        userProvider = new UserProvider();

        txtMessage = findViewById(R.id.txtMessage);
        imgSendMessage = findViewById(R.id.imgSendMessage);

        mRvMessage = findViewById(R.id.rcMessage);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRvMessage.setLayoutManager(linearLayoutManager);


        showCustomToolbar(R.layout.custom_chat_toolbar);

        checkIfChatExists();


        imgSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = messageProvider.getMessageByChat(idChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
        mAdapter = new MessagesAdapter(options, this);
        mRvMessage.setAdapter(mAdapter);
        mAdapter.startListening();
    }


    private void sendMessage() {
        String textMessage = txtMessage.getText().toString();
        if(!textMessage.isEmpty())
        {
            Message message = new Message();
            message.setIdChat(idChat);
            if(authProvider.GetUid().equals(extraIdUser1))
            {
                message.setIdSender(extraIdUser1);
                message.setIdReceiver(extraIdUser2);
            }
            else{
                message.setIdReceiver(extraIdUser1);
                message.setIdSender(extraIdUser2);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setMessage(textMessage);
            
            messageProvider.create(message).addOnCompleteListener(t ->{
                if(t.isSuccessful())
                {
                    txtMessage.setText("");
                    Toast.makeText(this, "Mensaje Creado", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Error al crear mensaje", Toast.LENGTH_SHORT).show();
                }
            });
            
        }
    }

    private void checkIfChatExists()
    {
        chatProvider.getChatByUser1AndUser2(extraIdUser1, extraIdUser2).get().addOnSuccessListener(queryDocumentSnapshots -> {
           int size = queryDocumentSnapshots.size();
           if(size == 0)
           {
               idChat = queryDocumentSnapshots.getDocuments().get(0).getId();
               createChat();
           }
           else{
           }
        });
    }

    private void showCustomToolbar(int resource) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(mActionBarView);
        imgProfileChat = mActionBarView.findViewById(R.id.imgImageProfileChat);
        txtViewRelativeTime = mActionBarView.findViewById(R.id.txtViewRelativeTime);
        txtViewUserName = mActionBarView.findViewById(R.id.txtUserNameChat);
        imgBack = mActionBarView.findViewById(R.id.imgBackChat);

        imgBack.setOnClickListener(v ->{
            finish();
        });

        getUserInfo();
    }

    private void getUserInfo()
    {
        String idUserInfo = "";
        if(authProvider.GetUid().equals(extraIdUser1))
        {
            idUserInfo = extraIdUser2;
        }
        else{
            idUserInfo = extraIdUser1;
        }

        userProvider.GetUser(idUserInfo).addOnSuccessListener(ds ->{
           if(ds.exists())
           {
               if(ds.contains("username"))
               {
                   String userName = ds.getString("username");
                   txtViewUserName.setText(userName);
               }
               if(ds.contains("image_profile") && ds.getString("image_profile") != null)
               {
                   String imageProfile = ds.getString("image_profile");
                   Picasso.with(this).load(imageProfile).into(imgProfileChat);
               }
           }
        });
    }

    private void createChat()
    {
        Chat chat = new Chat();
        chat.setIdUser1(extraIdUser1);
        chat.setIdUser2(extraIdUser2);
        chat.setWriting(false);
        chat.setId(extraIdUser1 + extraIdUser2);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(extraIdUser1);
        ids.add(extraIdUser2);

        chat.setIds(ids);
        chat.setTimestamp(new Date().getTime());
        chatProvider.create(chat);
    }
}
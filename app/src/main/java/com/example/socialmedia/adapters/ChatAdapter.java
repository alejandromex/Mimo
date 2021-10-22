package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.ChatActivity;
import com.example.socialmedia.models.Chat;
import com.example.socialmedia.models.Comment;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

    Context contexto;
    UserProvider userProvider;
    AuthProvider authProvider;

    public ChatAdapter(FirestoreRecyclerOptions<Chat> options, Context contexto)
    {
        super(options);
        this.contexto = contexto;
        this.userProvider = new UserProvider();
        this.authProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String chatId = document.getId();

        if(authProvider.GetUid().equals(chat.getIdUser1()))
        {
            getUserInfo(chat.getIdUser2(), holder);
        }
        else{
            getUserInfo(chat.getIdUser1(), holder);
        }

        holder.viewHolder.setOnClickListener(v ->{
            goToChatACtivity(chatId, chat.getIdUser1(), chat.getIdUser2());
        });
    }

    private void goToChatACtivity(String chatId, String idUser1, String idUser2)
    {
        Intent intent = new Intent(contexto, ChatActivity.class);
        intent.putExtra("idChat",chatId);
        intent.putExtra("idUser1",idUser1);
        intent.putExtra("idUser2",idUser2);
        contexto.startActivity(intent);
    }

    private void getUserInfo(String idUser, ViewHolder holder)
    {
        userProvider.GetUser(idUser).addOnSuccessListener(user ->{
           if(user.exists())
           {
               if(user.contains("username"))
               {
                   String userName = user.getString("username");
                   holder.txtChatName.setText(userName);
               }if(user.contains("image_profile"))
               {
                   String imageProfile = user.getString("image_profile");
                   if(imageProfile != null && !imageProfile.isEmpty())
                   {
                       Picasso.with(this.contexto).load(imageProfile).into(holder.imgChatUser);
                   }
               }
           }
        });
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtChatName, txtChatLastMsn;
        CircleImageView imgChatUser;
        View viewHolder;

        public ViewHolder(View view)
        {
            super(view);
            txtChatName = view.findViewById(R.id.txtChatName);
            txtChatLastMsn = view.findViewById(R.id.txtChatLastMsn);
            imgChatUser = view.findViewById(R.id.imgChatUser);


            viewHolder = view;
        }


    }
}

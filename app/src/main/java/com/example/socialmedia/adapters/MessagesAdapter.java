package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.ChatActivity;
import com.example.socialmedia.models.Message;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.UserProvider;
import com.example.socialmedia.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.type.Date;
import com.google.type.DateTime;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {

    Context contexto;
    UserProvider userProvider;
    AuthProvider authProvider;

    public MessagesAdapter(FirestoreRecyclerOptions<Message> options, Context contexto)
    {
        super(options);
        this.contexto = contexto;
        this.userProvider = new UserProvider();
        this.authProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Message message) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String messageId = document.getId();
        holder.txtViewMessage.setText(message.getMessage());

        String relativeTime = RelativeTime.getTimeAgo(message.getTimestamp(), contexto);
        holder.txtViewDate.setText(relativeTime);
    }

    private void goToChatACtivity(String chatId, String idUser1, String idUser2)
    {
        Intent intent = new Intent(contexto, ChatActivity.class);
        intent.putExtra("idChat",chatId);
        intent.putExtra("idUser1",idUser1);
        intent.putExtra("idUser2",idUser2);
        contexto.startActivity(intent);
    }



    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtViewMessage, txtViewDate;
        View imgViewed;
        View viewHolder;

        public ViewHolder(View view)
        {
            super(view);
            txtViewMessage = view.findViewById(R.id.txtNewMessage);
            txtViewDate = view.findViewById(R.id.txtMessageDate);
            imgViewed = view.findViewById(R.id.imgSeenMessage);
            viewHolder = view;
        }


    }
}

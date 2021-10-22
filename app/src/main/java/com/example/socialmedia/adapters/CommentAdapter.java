package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.PostDetailActivity;
import com.example.socialmedia.models.Comment;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

    Context contexto;
    UserProvider userProvider;

    public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context contexto)
    {
        super(options);
        this.contexto = contexto;
        this.userProvider = new UserProvider();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Comment comment) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String commentId = document.getId();
        String idUser = document.getString("idUser");

        holder.txtComment.setText(comment.getComment());
        getUserInfo(idUser, holder);

    }

    private void getUserInfo(String idUser, ViewHolder holder)
    {
        userProvider.GetUser(idUser).addOnSuccessListener(user ->{
           if(user.exists())
           {
               if(user.contains("username"))
               {
                   String userName = user.getString("username");
                   holder.txtUserName.setText(userName);
               }if(user.contains("image_profile"))
               {
                   String imageProfile = user.getString("image_profile");
                   if(imageProfile != null && !imageProfile.isEmpty())
                   {
                       Picasso.with(this.contexto).load(imageProfile).into(holder.imgComment);
                   }
               }
           }
        });
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserName, txtComment;
        CircleImageView imgComment;
        View viewHolder;

        public ViewHolder(View view)
        {
            super(view);
            txtUserName = view.findViewById(R.id.txtUserNameComment);
            txtComment = view.findViewById(R.id.txtComment);
            imgComment = view.findViewById(R.id.imgComment);
            viewHolder = view;
        }


    }
}

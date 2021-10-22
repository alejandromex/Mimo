package com.example.socialmedia.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.PostDetailActivity;
import com.example.socialmedia.models.Like;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.LikesProvider;
import com.example.socialmedia.providers.PostProvider;
import com.example.socialmedia.providers.UserProvider;
import com.example.socialmedia.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostsAdapter extends FirestoreRecyclerAdapter<Post, MyPostsAdapter.ViewHolder> {

    Context contexto;
    UserProvider userProvider;
    LikesProvider likesProvider;
    AuthProvider authProvider;
    PostProvider postProvider;

    public MyPostsAdapter(FirestoreRecyclerOptions<Post> options, Context contexto)
    {
        super(options);
        this.contexto = contexto;
        userProvider = new UserProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Post post) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();
        String userId = post.getIdUser();

        String relativeTime = RelativeTime.getTimeAgo(post.getTimeStamp(), contexto);
        holder.txtRelativeTimeMyPost.setText(relativeTime);
        holder.txtTitleMyPost.setText(post.getTitle().toUpperCase());

        if(post.getImage1() != null && !post.getImage1().isEmpty())
        {
            Picasso.with(contexto).load(post.getImage1()).into(holder.imgMyPost);
        }

        if(post.getIdUser().equals(authProvider.GetUid()))
        {
            holder.imgCancelMyPost.setVisibility(View.VISIBLE);
        }
        else{
            holder.imgCancelMyPost.setVisibility(View.INVISIBLE);
        }

        holder.viewHolder.setOnClickListener(v ->{
            Intent intent = new Intent(contexto, PostDetailActivity.class);
            intent.putExtra("postId", postId);
            contexto.startActivity(intent);
        });

        holder.imgCancelMyPost.setOnClickListener(v ->{
            showConfirmDelete(postId);
        //    delete(postId);
        });

    }

    private void showConfirmDelete(String id)
    {
        new AlertDialog.Builder(contexto).setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("Eliminar publicacion")
                                        .setMessage("¿Seguro de eliminar la publicacion?")
                                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                delete(id);
                                            }
                                        }).setNegativeButton("No", null).show();
    }

    private void delete(String id)
    {
        postProvider.delete(id).addOnCompleteListener(t ->{
            if(t.isSuccessful())
            {
                Toast.makeText(contexto, "Publicacion eliminada", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(contexto, "Error al eliminar la pubilcacion", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_posts, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgMyPost;
        ImageView imgCancelMyPost;
        TextView txtTitleMyPost, txtRelativeTimeMyPost;
        View viewHolder;

        public ViewHolder(View view)
        {
            super(view);
            imgMyPost = view.findViewById(R.id.imgMyPost);
            txtTitleMyPost = view.findViewById(R.id.txtTitleMyPost);
            txtRelativeTimeMyPost = view.findViewById(R.id.txtRelativeTimeMyPost);
            imgCancelMyPost = view.findViewById(R.id.imgCancelMyPost);
            viewHolder = view;
        }


    }
}

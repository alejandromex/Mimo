package com.example.socialmedia.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmedia.R;
import com.example.socialmedia.activities.PostDetailActivity;
import com.example.socialmedia.models.Like;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.LikesProvider;
import com.example.socialmedia.providers.UserProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

    Context contexto;
    UserProvider userProvider;
    LikesProvider likesProvider;
    AuthProvider authProvider;
    TextView mTextViewNumberFilter;

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context contexto)
    {
        super(options);
        this.contexto = contexto;
        userProvider = new UserProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
    }

    public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context contexto, TextView textView)
    {
        super(options);
        this.contexto = contexto;
        userProvider = new UserProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
        mTextViewNumberFilter = textView;
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Post post) {
        holder.txtViewTitle.setText(post.getTitle().toUpperCase());
        holder.txtViewDescription.setText(post.getDescription());
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String postId = document.getId();
        String userId = post.getIdUser();

        if(mTextViewNumberFilter != null)
        {
            int number = getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(number));
        }

        userProvider.GetUser(userId).addOnSuccessListener(user ->{
            if(user.exists())
            {
                holder.txtViewUserName.setText(user.getString("username"));
                if(post.getImage1() != null && !post.getImage1().isEmpty())
                {
                    Picasso.with(contexto).load(post.getImage1()).into(holder.imgViewPost);
                }
                holder.imgLike.setOnClickListener(view ->{
                    Like like = new Like();
                    like.setIdPost(postId);
                    like.setIdUser(authProvider.GetUid());
                    like.setTimestamp(new Date().getTime());
                    like(like, holder);
                });

                getNumberLikesByPost(postId, holder);
                checkIfExistsLike(postId, authProvider.GetUid(), holder);


                holder.viewHolder.setOnClickListener(v ->{
                    Intent intent = new Intent(contexto, PostDetailActivity.class);
                    intent.putExtra("postId", postId);
                    contexto.startActivity(intent);
                });


            }
        });


    }

    private void getNumberLikesByPost(String idPost, ViewHolder holder)
    {
        likesProvider.getLikesByPost(idPost).addSnapshotListener((sc, ex) ->{
            if(sc == null) return;
            int numberLikes = sc.size();
            if(numberLikes > 0)
            {
                holder.txtLikes.setText(String.valueOf(numberLikes) + "Me gustas");
            }
        });
    }

    private void like(Like like, ViewHolder holder)
    {
        likesProvider.getLikeByPostAndUser(authProvider.GetUid(), like.getIdPost()).get().addOnSuccessListener(t->{
           int numberDocuments = t.size();
           if(numberDocuments > 0)
           {
               holder.imgLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
               likesProvider.delete(t.getDocuments().get(0).getId());
           }
           else{
               holder.imgLike.setImageResource(R.drawable.ic_baseline_favorite_24);
               likesProvider.create(like);
           }
        });

    }

    private void checkIfExistsLike(String idPost, String idUser, ViewHolder holder)
    {
        likesProvider.getLikeByPostAndUser(idUser, idPost).get().addOnSuccessListener(t->{
            int numberDocuments = t.size();
            if(numberDocuments > 0)
            {
                holder.imgLike.setImageResource(R.drawable.ic_baseline_favorite_24);
            }
            else{
                holder.imgLike.setImageResource(R.drawable.ic_baseline_favorite_border_24);
            }
        });

    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtViewTitle, txtViewDescription, txtViewUserName, txtLikes;
        ImageView imgViewPost, imgLike;
        View viewHolder;

        public ViewHolder(View view)
        {
            super(view);
            txtViewTitle = view.findViewById(R.id.txtNameCard);
            txtViewDescription = view.findViewById(R.id.txtDescripcionCard);
            imgViewPost = view.findViewById(R.id.imgPostCard);
            txtViewUserName = view.findViewById(R.id.txtPostUserName);
            imgLike = view.findViewById(R.id.imgLike);
            txtLikes = view.findViewById(R.id.txtLikes);
            viewHolder = view;
        }


    }
}

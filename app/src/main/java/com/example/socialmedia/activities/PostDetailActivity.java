package com.example.socialmedia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.adapters.CommentAdapter;
import com.example.socialmedia.adapters.PostsAdapter;
import com.example.socialmedia.adapters.SliderAdapter;
import com.example.socialmedia.models.Comment;
import com.example.socialmedia.models.FCMBody;
import com.example.socialmedia.models.FCMResponse;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.models.SliderItem;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.CommentsProvider;
import com.example.socialmedia.providers.LikesProvider;
import com.example.socialmedia.providers.NotificationProvider;
import com.example.socialmedia.providers.PostProvider;
import com.example.socialmedia.providers.TokenProvider;
import com.example.socialmedia.providers.UserProvider;
import com.example.socialmedia.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class PostDetailActivity extends AppCompatActivity {

    SliderView sliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> sliderItems = new ArrayList<>();
    PostProvider postProvider;

    TextView txtName, txtPhone, txtTitle, txtConsole, txtDescription, txtRelativeTime, txtTotalLikes;
    CircleImageView imgUser;
    FloatingActionButton btnComment;
    Button btnSeeProfile;
    ImageView imgGamePicture;
    String postId;
    String idUser;
    RecyclerView mRecyclerVIew;

    AuthProvider authProvider;
    UserProvider userProvider;
    CommentsProvider commentsProvider;
    LikesProvider likesProvider;
    NotificationProvider notificationProvider;
    TokenProvider tokenProvider;

    CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postProvider = new PostProvider();

        postId = getIntent().getStringExtra("postId");

        txtName = findViewById(R.id.txtProfilePostName);
        txtPhone = findViewById(R.id.txtProfilePostPhone);
        txtDescription = findViewById(R.id.txtProfileDescription);
        txtTitle = findViewById(R.id.txtProfileTitleNameGame);
        txtConsole = findViewById(R.id.txtProfileGameName);
        imgUser = findViewById(R.id.imgProfilePostPicture);
        btnComment = findViewById(R.id.btnCommentPost);
        btnSeeProfile = findViewById(R.id.btnSeeProfile);
        imgGamePicture = findViewById(R.id.imgProfileGamePicture);
        mRecyclerVIew = findViewById(R.id.rvComments);
        txtRelativeTime = findViewById(R.id.txtRelativeTime);
        txtTotalLikes = findViewById(R.id.txtTotalLikes);

        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();

        LinearLayoutManager manager = new LinearLayoutManager(PostDetailActivity.this);
        mRecyclerVIew.setLayoutManager(manager);

        authProvider = new AuthProvider();
        userProvider = new UserProvider();
        commentsProvider = new CommentsProvider();
        likesProvider = new LikesProvider();

        sliderView = findViewById(R.id.imageSliderPost);


        btnSeeProfile.setOnClickListener(v ->{
            GoToProfile();
        });

        btnComment.setOnClickListener(v ->{
           CommentPost();
        });

        getPost();
        getNumberLikes();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = commentsProvider.getCommentsByPost(postId);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();
        commentAdapter = new CommentAdapter(options, PostDetailActivity.this);
        mRecyclerVIew.setAdapter(commentAdapter);
        commentAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentAdapter.startListening();
    }

    public void CloseEdit(View view)
    {
        finish();
    }

    private void getNumberLikes()
    {
        likesProvider.getLikesByPost(postId).addSnapshotListener((sc, ex) ->{
            if(sc == null) return;
            int numberLikes = sc.size();
            if(numberLikes == 1)
            {
                txtTotalLikes.setText(numberLikes + " Me gusta");
            }
            else if(numberLikes == 0)
            {
                txtTotalLikes.setText("No tiene Me gustas");
            }
            else
                txtTotalLikes.setText(numberLikes + " Me gustas");
        });
    }

    private void GoToProfile()
    {
        Intent intent = new Intent(this, UserActivity.class);
        intent.putExtra("idUser", idUser);
        startActivity(intent);
    }

    private void CommentPost()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("Comentario");
        alert.setMessage("Ingresa tu comentario");

        EditText editText = new EditText(this);
        editText.setHint("Comentario");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(36,0,36,36);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        container.setLayoutParams(relativeParams);
        container.addView(editText);

        alert.setView(container);
        alert.setPositiveButton("Ok", (dialog, which) -> {
            String value = editText.getText().toString();
            if(!value.isEmpty())
            {
                createComment(value);
            }
            else{
                Toast.makeText(this, "Debe ingresar un comentario valido", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancelar", (dialog, which) ->{

        });

        alert.show();
    }
    private void createComment(String value)
    {
        Comment comment = new Comment();
        comment.setComment(value);
        comment.setIdPost(postId);
        comment.setIdUser(authProvider.GetUid());
        comment.setTimeStamp(new Date().getTime());

        commentsProvider.create(comment).addOnCompleteListener(t ->{
            if(t.isSuccessful())
            {
                sendNotification(value);
                Toast.makeText(this, "Comentario creado correctamente", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "No se pudo crear el comentario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotification(String value) {

        if(idUser == null)
        {
            return;
        }
        tokenProvider.getToken(idUser).addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists())
            {
                if(documentSnapshot.contains("token"))
                {
                    String token = documentSnapshot.getString("token");
                    Map<String, String> data = new HashMap<>();
                    data.put("title", "Nuevo Comentario");
                    data.put("body", value);
                    FCMBody body = new FCMBody(
                            token,
                            "high",
                            "4500s",
                            data
                    );
                    notificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if(response.body() != null)
                            {
                                if(response.body().getSuccess() == 1)
                                {
                                    Toast.makeText(PostDetailActivity.this, "Notificacion Enviada Correctamente", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(PostDetailActivity.this, "Error al enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(PostDetailActivity.this, "Error al enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {

                        }
                    });
                }
            }
            else{
                Toast.makeText(this, "El token de notificaciones de usuario no existe", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void InstanceSlider()
    {
        mSliderAdapter = new SliderAdapter(PostDetailActivity.this, sliderItems);
        sliderView.setSliderAdapter(mSliderAdapter);

        sliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
    }

    private void getPost()
    {
        postProvider.getPostById(postId).addOnSuccessListener(t ->{
            if(t.exists() && t.contains("image1") && t.contains("image2"))
            {
                String image1 = t.getString("image1");
                String image2 = t.getString("image2");
                sliderItems.add(new SliderItem(image1, new Date().getTime()));
                sliderItems.add(new SliderItem(image2, new Date().getTime()));
                InstanceSlider();
            }

            if(t.contains("title"))
            {
                String title = t.getString("title");
                this.txtTitle.setText(title);
            }

            if(t.contains("description"))
            {
                String description = t.getString("description");
                this.txtDescription.setText(description);
            }

            if(t.contains("category"))
            {
                String category = t.getString("category");
                this.txtConsole.setText(category);

                if(category.equals("PS4"))
                {
                    imgGamePicture.setImageResource(R.drawable.icon_ps4);
                }
                else if(category.equals("PC"))
                {
                    imgGamePicture.setImageResource(R.drawable.icon_pc);
                }
                else if(category.equals("Xbox"))
                {
                    imgGamePicture.setImageResource(R.drawable.icon_xbox);
                }
                else if(category.equals("Nintendo"))
                {
                    imgGamePicture.setImageResource(R.drawable.icon_nintendo);
                }
            }

            if(t.contains("idUser"))
            {
                idUser = t.getString("idUser");
                userProvider.GetUser(idUser).addOnSuccessListener(user ->{
                    if(user.exists())
                    {
                        if(user.contains("username"))
                        {
                            txtName.setText(user.getString("username"));
                        }
                        if(user.contains("phone"))
                        {
                            txtPhone.setText(user.getString("phone"));
                        }
                        if(user.contains("image_profile"))
                        {
                            String imageProfile = user.getString("image_profile");
                            Picasso.with(this).load(imageProfile).into(imgUser);
                        }
                    }
                });
            }

            if(t.contains("timeStamp"))
            {
                long timestamp = t.getLong("timeStamp");
                String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                txtRelativeTime.setText(relativeTime);
            }
        });
    }
}
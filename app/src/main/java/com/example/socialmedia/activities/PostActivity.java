package com.example.socialmedia.activities;

import androidx.annotation.Nullable;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmedia.R;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.ImageProvider;
import com.example.socialmedia.providers.PostProvider;
import com.example.socialmedia.utils.FileUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class PostActivity extends AppCompatActivity {

    final int GALLERY_CODE = 159;
    final int PHOTO_CODE = 951;

    ImageView imgPost1, imgPost2, imgPc, imgPs4, imgXbox, imgNintento;
    TextView txtTitleCategory;
    TextInputEditText txtNamePost, txtDescriptionPost;
    File mImageFile, mImageFile2;
    Button btnPostPublicar;

    ImageProvider mImageProvider;
    PostProvider postProvider;
    AuthProvider authProvider;

    AlertDialog.Builder mBuilderSelector;
    AlertDialog mDialog;

    CharSequence[] options;

    String category = "", mDescription = "", mTitle = "";
    int imageNumber = 0;

    String mAbsolutePhotoPath, mPhotoPath;
    String mAbsolutePhotoPath2, mPhotoPath2;
    File mPhotoFile, mPhotoFIle2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        imgPost1 = findViewById(R.id.imgPost1);
        imgPost2 = findViewById(R.id.imgPost2);

        btnPostPublicar = findViewById(R.id.btnPostPublicar);
        mImageProvider = new ImageProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        imgPc = findViewById(R.id.imgPc);
        imgPs4 = findViewById(R.id.imgPs4);
        imgXbox = findViewById(R.id.imgXbox);
        imgNintento = findViewById(R.id.imgNintendo);
        txtNamePost = findViewById(R.id.txtPostName);
        txtDescriptionPost = findViewById(R.id.txtPostDescription);
        txtTitleCategory = findViewById(R.id.txtTitleCategory);

        mDialog = new SpotsDialog.Builder()
                        .setContext(this)
                        .setMessage("Cargadno...")
                        .setCancelable(false).build();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");

        options = new CharSequence[]{
                "Galeria",
                "Tomar foto"
        };

        imgPost1.setOnClickListener(v ->{
            SelectOptionImage(1);
        });

        imgPost2.setOnClickListener(v ->{
            SelectOptionImage(2);
        });

        btnPostPublicar.setOnClickListener(v ->{
            clickPost();
        });

    }


    private void SelectOptionImage(int imageNumber)
    {
        mBuilderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0)
                {
                    OpenGallery(imageNumber);
                }
                else if(which == 1)
                {
                    takePhoto(imageNumber);
                }
            }
        });

        mBuilderSelector.show();
    }

    private  void takePhoto(int imageNumber)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            this.imageNumber = imageNumber;
            File photoFile = null;
            try{
                photoFile = createPhotoFile();
            }
            catch (Exception ex)
            {
                Log.d("ERROR",ex.getMessage());
                Toast.makeText(this, "Error en el archivo", Toast.LENGTH_SHORT).show();
            }

            if(photoFile != null)
            {
                Uri photoUri = FileProvider.getUriForFile(PostActivity.this, "com.example.socialmedia", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
               startActivityForResult(takePictureIntent, PHOTO_CODE);
            }
        }
    }

    private File createPhotoFile()
    {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = null;
        try {
                photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
            );
            if(this.imageNumber == 1)
            {
                mPhotoPath = "file:"+photoFile.getAbsolutePath();
                mAbsolutePhotoPath = photoFile.getAbsolutePath();
            }
            else if(this.imageNumber == 2)
            {
                mPhotoPath2 = "file:"+photoFile.getAbsolutePath();
                mAbsolutePhotoPath2 = photoFile.getAbsolutePath();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoFile;

    }

    public void back(View v)
    {
        finish();
    }


    private void clickPost()
    {
        mTitle = txtNamePost.getText().toString();
        mDescription = txtDescriptionPost.getText().toString();

        if(mTitle.isEmpty() || mDescription.isEmpty() || category.isEmpty())
        {
            Toast.makeText(PostActivity.this, "Titulo, descripcion y categoria son obligatorios", Toast.LENGTH_SHORT).show();
        }
        else{
            if(mImageFile != null && mImageFile2 != null)
            {
                SaveImage(mImageFile, mImageFile2);
            }
            else if(mPhotoFile != null && mPhotoFIle2 != null)
            {
                SaveImage(mPhotoFile, mPhotoFIle2);
            }
            else if(mImageFile != null && mPhotoFIle2 != null)
            {
                SaveImage(mImageFile, mPhotoFIle2);
            }
            else if(mPhotoFile != null && mImageFile2 != null)
            {
                SaveImage(mPhotoFile, mImageFile2);
            }
            else{
                Toast.makeText(PostActivity.this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void SaveImage(File imageFile, File imageFile2)
    {

       mImageProvider.save(this,imageFile).addOnCompleteListener(t ->{
           mDialog.show();
            if(t.isSuccessful())
            {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri ->{
                    String url = uri.toString();

                    mImageProvider.save(this, imageFile2).addOnCompleteListener(taskImg2 ->{
                       if(taskImg2.isSuccessful())
                       {
                           mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri2 ->{
                               String url2 = uri2.toString();
                               Post post = new Post();
                               post.setImage1(url);
                               post.setImage2(url2);
                               post.setTitle(mTitle.toLowerCase());
                               post.setDescription(mDescription);
                               post.setCategory(category);
                               post.setIdUser(authProvider.GetUid());
                               post.setTimeStamp(new Date().getTime());
                               postProvider.save(post).addOnCompleteListener(saveTask ->{
                                   if(saveTask.isSuccessful())
                                   {
                                       mDialog.dismiss();
                                       ClearForm();
                                       Toast.makeText(this, "Informacion almacenada correcamente", Toast.LENGTH_SHORT).show();
                                   }
                                   else{
                                       mDialog.dismiss();
                                       Toast.makeText(this, "Error al guardar la informacion", Toast.LENGTH_SHORT).show();
                                   }
                               });
                           });
                       }
                       else{
                           mDialog.dismiss();
                           Toast.makeText(this, "Error al crear el post", Toast.LENGTH_SHORT).show();
                       }
                    });
                });
            }
            else{
                mDialog.dismiss();
                Toast.makeText(this, "error al almacenar la imagen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ClearForm()
    {
        txtTitleCategory.setText("Categoria");
        txtNamePost.setText("");
        txtDescriptionPost.setText("");
        imgPost1.setImageResource(R.drawable.upload_image);
        imgPost2.setImageResource(R.drawable.upload_image);
    }

    public void ChoiseCategory(View v)
    {
        if(v.getTag().equals("PC")) category = "PC";
        else if(v.getTag().equals("PS4")) category = "PS4";
        else if(v.getTag().equals("Xbox")) category = "Xbox";
        else if(v.getTag().equals("Nintendo")) category = "Nintendo";
        txtTitleCategory.setText(category);
    }

    private void OpenGallery(int numberImage)
    {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        this.imageNumber = numberImage;
        startActivityForResult(galleryIntent, GALLERY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK)
        {
            try{
                if(this.imageNumber == 1)
                {
                    mPhotoFile = null;
                    mImageFile = FileUtil.from(this, data.getData());
                    imgPost1.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                }
                else if(this.imageNumber == 2)
                {
                    mPhotoFIle2 = null;
                    mImageFile2 = FileUtil.from(this, data.getData());
                    imgPost2.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
                }

            }
            catch(Exception ex)
            {
                Log.d("ERROR", "Se produjo un error: "+ ex.getMessage());
                Toast.makeText(this, "Se produjo un error", Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == PHOTO_CODE && resultCode == RESULT_OK)
        {
            if(this.imageNumber == 1)
            {
                mImageFile = null;
                mPhotoFile = new File(mAbsolutePhotoPath);
                Picasso.with(PostActivity.this).load(mPhotoPath).into(imgPost1);
            }
            else if(this.imageNumber == 2)
            {
                mImageFile2 = null;
                mPhotoFIle2 = new File(mAbsolutePhotoPath2);
                Picasso.with(PostActivity.this).load(mPhotoPath2).into(imgPost2);
            }
        }
    }
}
package com.example.socialmedia.activities;

import android.app.AlertDialog;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.socialmedia.R;
import com.example.socialmedia.models.Post;
import com.example.socialmedia.models.User;
import com.example.socialmedia.providers.AuthProvider;
import com.example.socialmedia.providers.ImageProvider;
import com.example.socialmedia.providers.UserProvider;
import com.example.socialmedia.utils.FileUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {

    final int GALLERY_CODE = 159;
    final int PHOTO_CODE = 951;

    ImageView imgProfilePicture, imgCoverProfilePicture;
    Button btnEditProfile;
    TextInputEditText txtName, txtPhone;

    AlertDialog.Builder mBuilderSelector;
    AlertDialog mDialog;
    CharSequence[] options;

    int imageNumber;
    String mAbsolutePhotoPath, mPhotoPath;
    String mAbsolutePhotoPath2, mPhotoPath2;
    File mPhotoFile, mPhotoFIle2;
    File mImageFile, mImageFile2;

    ImageProvider mImageProvider;
    UserProvider userProvider;
    AuthProvider authProvider;

    String imageProfileGlobal;
    String imageCoverGlobal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgProfilePicture = findViewById(R.id.imgEditPhotoProfile);
        imgCoverProfilePicture = findViewById(R.id.imgEditPhotoCoverProfile);
        txtName = findViewById(R.id.txtEditNameProfile);
        txtPhone = findViewById(R.id.txtEditPhoneProfile);
        btnEditProfile = findViewById(R.id.btnEditUpdateProfile);

        mImageProvider = new ImageProvider();
        userProvider = new UserProvider();
        authProvider = new AuthProvider();

        mBuilderSelector = new AlertDialog.Builder(this);
        mBuilderSelector.setTitle("Selecciona una opcion");

        options = new CharSequence[]{
                "Galeria",
                "Tomar foto"
        };

        mDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Cargadno...")
                .setCancelable(false).build();

        GetUser();

        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectOptionImage(1);
            }
        });

        imgCoverProfilePicture.setOnClickListener(v ->{
            SelectOptionImage(2);
        });

        btnEditProfile.setOnClickListener(v ->{
            EditProfile();
        });

    }

    private void GetUser()
    {
        userProvider.GetUser(authProvider.GetUid()).addOnSuccessListener(t ->{
           if(t.exists())
           {
               String userName = t.getString("username");
               String phone = t.getString("phone");
               imageProfileGlobal = t.getString("image_profile");
               imageCoverGlobal = t.getString("image_cover");

               txtName.setText(userName);
               txtPhone.setText(phone);

               Picasso.with(this).load(imageProfileGlobal).into(imgProfilePicture);
               Picasso.with(this).load(imageCoverGlobal).into(imgCoverProfilePicture);
           }
        });
    }


    private void EditProfile()
    {
        String name = txtName.getText().toString();
        String phone = txtPhone.getText().toString();

        if(!name.isEmpty() && !phone.isEmpty())
        {
            User user = new User();
            user.setUsername(name);
            user.setPhone(phone);
            user.setId(authProvider.GetUid());
            user.setImageCover(imageCoverGlobal);
            user.setImageProfile(imageProfileGlobal);


            if(mImageFile != null && mImageFile2 != null)
            {
                SaveImage(mImageFile, mImageFile2, user);
            }
            else if(mPhotoFile != null && mPhotoFIle2 != null)
            {
                SaveImage(mPhotoFile, mPhotoFIle2, user);
            }
            else if(mImageFile != null && mPhotoFIle2 != null)
            {
                SaveImage(mImageFile, mPhotoFIle2, user);
            }
            else if(mPhotoFile != null && mImageFile2 != null)
            {
                SaveImage(mPhotoFile, mImageFile2, user);
            }
            else if(mPhotoFile != null)
            {
                SaveImageWithOnePicture(mPhotoFile, user, true);
            }
            else if(mPhotoFIle2 != null)
            {
                SaveImageWithOnePicture(mPhotoFIle2, user, false);
            }
            else if(mImageFile != null)
            {
                SaveImageWithOnePicture(mImageFile, user, true);
            }
            else if(mImageFile2 != null)
            {
                SaveImageWithOnePicture(mImageFile2, user, false);
            }
            else{
                UpdateWithOutImage(user);
            }

        }else{
            Toast.makeText(this, "Los campos Nombre y Celular son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void UpdateWithOutImage(User user)
    {
        userProvider.Update(user).addOnCompleteListener(t ->{
           if(t.isSuccessful())
           {
               Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
               finish();
           }
        });
    }

    private void SaveImageWithOnePicture(File imageFile, User user, boolean IsProfilePicture)
    {
        mImageProvider.save(this, imageFile).addOnCompleteListener( t ->{

            mDialog.show();
            if(t.isSuccessful())
            {
                mImageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri ->{
                   String url = uri.toString();
                   if(IsProfilePicture) {
                       user.setImageProfile(url);
                   }
                   else{
                       user.setImageCover(url);
                   }
                    userProvider.Update(user).addOnCompleteListener(tUpdate ->{
                        if(tUpdate.isSuccessful())
                        {
                            Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                            finish();
                        }
                        else{
                            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();

                    });
                });
            }

        });
    }

    private void SaveImage(File imageFile, File imageFile2, User user)
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
                                user.setImageProfile(url);
                                user.setImageCover(url2);
                                userProvider.Update(user).addOnCompleteListener(t3 ->{
                                    if(t3.isSuccessful())
                                    {
                                        Toast.makeText(this, "La informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                                    }
                                    mDialog.dismiss();
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


    public void CloseEdit(View view)
    {
        finish();
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
                Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.example.socialmedia", photoFile);
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
                    imgProfilePicture.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
                }
                else if(this.imageNumber == 2)
                {
                    mPhotoFIle2 = null;
                    mImageFile2 = FileUtil.from(this, data.getData());
                    imgCoverProfilePicture.setImageBitmap(BitmapFactory.decodeFile(mImageFile2.getAbsolutePath()));
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
                Picasso.with(EditProfileActivity.this).load(mPhotoPath).into(imgProfilePicture);
            }
            else if(this.imageNumber == 2)
            {
                mImageFile2 = null;
                mPhotoFIle2 = new File(mAbsolutePhotoPath2);
                Picasso.with(EditProfileActivity.this).load(mPhotoPath2).into(imgCoverProfilePicture);
            }
        }
    }
}

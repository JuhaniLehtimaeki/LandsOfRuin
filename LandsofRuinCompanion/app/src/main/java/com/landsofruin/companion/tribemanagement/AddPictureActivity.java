package com.landsofruin.companion.tribemanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.android.Utils;
import com.cloudinary.utils.ObjectUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.landsofruin.gametracker.R;
import com.lyft.android.scissors.CropView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class AddPictureActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY = 2;
    private boolean useCamera;
    private CropView cardImage;
    private CropView profileImage;
    private View loadingOverlay;


    public static void startActivity(Context context, boolean useCamera) {
        Intent intent = new Intent(context, AddPictureActivity.class);
        intent.putExtra("useCamera", useCamera);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture);

        loadingOverlay = findViewById(R.id.uploading_overlay);

        cardImage = (CropView) findViewById(R.id.crop_view_card);
        profileImage = (CropView) findViewById(R.id.crop_view_profile);


        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingOverlay.setVisibility(View.VISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            cardImage.crop().compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            byte[] bitmapdata = bos.toByteArray();
                            ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                            Cloudinary cloudinary = new Cloudinary(Utils.cloudinaryUrlFromContext(AddPictureActivity.this));
                            Uploader uploader = cloudinary.uploader();
                            Map result = uploader.upload(bs, ObjectUtils.emptyMap());

                            String cardUrl = (String) result.get("url");
                            bos.close();
                            bs.close();

                            bos = new ByteArrayOutputStream();
                            profileImage.crop().compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                            bitmapdata = bos.toByteArray();
                            bs = new ByteArrayInputStream(bitmapdata);

                            result = uploader.upload(bs, ObjectUtils.emptyMap());
                            String profileUrl = (String) result.get("url");

                            CharacterPicture picture = new CharacterPicture();
                            picture.setCardPicture(cardUrl);
                            picture.setProfilePicture(profileUrl);

                            DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();


                            DatabaseReference newNode = firebaseRef.child("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("miniaturepics").push();
                            picture.setId(newNode.getKey());
                            newNode.setValue(picture);


                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadingOverlay.setVisibility(View.GONE);
                                }
                            });
                            Toast.makeText(AddPictureActivity.this, "Upload failed :-(\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }


                    }
                }).start();

            }
        });


        useCamera = getIntent().getBooleanExtra("useCamera", false);


        if (useCamera) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), REQUEST_IMAGE_GALLERY);
        }

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {

                Uri selectedImageUri = data.getData();
                cardImage.extensions().load(selectedImageUri);
                profileImage.extensions().load(selectedImageUri);

            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                cardImage.setImageBitmap(imageBitmap);
                profileImage.setImageBitmap(imageBitmap);

            }
        }
    }


}

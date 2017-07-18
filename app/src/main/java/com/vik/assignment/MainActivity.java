package com.vik.assignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FILE_PERMISSION = 201;
    private Bitmap mBitmap;
    private DatabaseHelper mDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);
        DataController.getInstance().setDataBaseInstance(mDatabaseHelper);
        initViews();
        checkPhotoPermission();
    }

    private void initViews() {
        if(mBitmap == null){
            findViewById(R.id.save_to_device).setEnabled(false);
        }

        findViewById(R.id.save_to_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText description = (EditText) findViewById(R.id.text);
                if (description.getText().length() > 0) {
                    ImageTagModel imageTagModel = new ImageTagModel();
                    imageTagModel.setImagePath(mCurrentPhotoPath);
                    imageTagModel.setTags(description.getText().toString());
                    mDatabaseHelper.saveImageToDb(imageTagModel);
                    Toast.makeText(MainActivity.this,"Image saved successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Please add some tags",Toast.LENGTH_SHORT).show();
                }
            }
        });


        findViewById(R.id.search_from_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FILE_PERMISSION && grantResults.length>1 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){
           writeToExternalStorage();
        }
    }


    private void writeToExternalStorage() {
        openCamera();
    }

    private void checkPhotoPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int photoPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (photoPermission != PackageManager.PERMISSION_GRANTED && cameraPermission!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, FILE_PERMISSION);
            } else {
                writeToExternalStorage();
            }
        } else {
            writeToExternalStorage();
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void openCamera() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhoto.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.vik.assignment.fileprovider", photoFile);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhoto, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Uri selectedImage = Uri.fromFile(new File(mCurrentPhotoPath));
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                if(mBitmap != null){
                    findViewById(R.id.save_to_device).setEnabled(true);
                }
                ImageView myImage = (ImageView) findViewById(R.id.image);
                myImage.setImageBitmap(mBitmap);
                createCameraSource();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        if (!textRecognizer.isOperational()) {
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        } else {
            Frame imageFrame = new Frame.Builder().setBitmap(mBitmap).build();
            SparseArray<TextBlock> textBlocksSparseArray = textRecognizer.detect(imageFrame);
            Toast.makeText(this, textBlocksSparseArray.size() + "", Toast.LENGTH_LONG).show();
            String imageText = null;
            for (int i = 0; i < textBlocksSparseArray.size(); i++) {
                TextBlock textBlock = textBlocksSparseArray.get(textBlocksSparseArray.keyAt(i));
                imageText = imageText + textBlock.getValue();                   // return string
            }
            ((TextView) findViewById(R.id.text_in_image)).setText(imageText);
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}


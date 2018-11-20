package ca.uqac.lecitoyen.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class ImageHandler {

    private static final String TAG = ImageHandler.class.getSimpleName();

    private static final ImageHandler mInstance = new ImageHandler();

    private static final int REQUEST_GALLERY_CODE = 2;
    private static final int REQUEST_AUDIO_CODE = 3;

    public static ImageHandler getInstance() {
        return mInstance;
    }

    private ImageHandler() {

    }

    public Intent openGallery(Activity activity) {
        Log.d(TAG, "openGallery called");
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(activity.getPackageManager()) != null) {
            openGalleryIntent.setType("image/*");
        }
        return openGalleryIntent;
    }

    public Intent openCamera(Activity activity) {

        try {

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_CODE);
            }
            /*final String dir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+ "/Folder/";
            File newdir = new File(dir);
            newdir.mkdirs();
            String file = dir+ DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString()+".jpg";
            File newfile = new File(file);

            try {
                newfile.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }*/


            //Uri outputFileUri = Uri.fromFile(newfile);
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            return openCameraIntent;

        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
        }
        /*if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_CODE);
        }
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openCameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            return openCameraIntent;
        } else {
            return new Intent();
        }*/
        return new Intent();
    }
}

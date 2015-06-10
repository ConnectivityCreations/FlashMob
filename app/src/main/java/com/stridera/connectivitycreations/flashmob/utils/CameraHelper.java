package com.stridera.connectivitycreations.flashmob.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class CameraHelper {
  public static Uri getPhotoFileUri(String appTag) {
    // Get safe storage directory for photos
    File mediaStorageDir = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appTag);

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
      Log.d(appTag, "failed to create directory");
    }

    // Return the file target for the photo based on filename
    return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + "photo.jpg"));
  }

  // Handle Image Rotation on Camera Intent
  public static String getRightAngleImage(String photoPath) {

    try {
      ExifInterface ei = new ExifInterface(photoPath);
      int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      int degree = 0;

      switch (orientation) {
        case ExifInterface.ORIENTATION_NORMAL:
          degree = 0;
          break;
        case ExifInterface.ORIENTATION_ROTATE_90:
          degree = 90;
          break;
        case ExifInterface.ORIENTATION_ROTATE_180:
          degree = 180;
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          degree = 270;
          break;
        case ExifInterface.ORIENTATION_UNDEFINED:
          degree = 0;
          break;
        default:
          degree = 90;
      }

      return rotateImage(degree,photoPath);

    } catch (Exception e) {
      e.printStackTrace();
    }

    return photoPath;
  }

  public static String rotateImage(int degree, String imagePath){

    if(degree<=0){
      return imagePath;
    }
    try{
      Bitmap b= BitmapFactory.decodeFile(imagePath);

      Matrix matrix = new Matrix();
      if(b.getWidth()>b.getHeight()){
        matrix.setRotate(degree);
        b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
            matrix, true);
      }

      FileOutputStream fOut = new FileOutputStream(imagePath);
      String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
      String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

      FileOutputStream out = new FileOutputStream(imagePath);
      if (imageType.equalsIgnoreCase("png")) {
        b.compress(Bitmap.CompressFormat.PNG, 100, out);
      }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
        b.compress(Bitmap.CompressFormat.JPEG, 100, out);
      }
      fOut.flush();
      fOut.close();

      b.recycle();
    }catch (Exception e){
      e.printStackTrace();
    }
    return imagePath;
  }

  public static Bitmap decodeFile(String path) {
    try {
      // Decode deal_image size
      BitmapFactory.Options o = new BitmapFactory.Options();
      o.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(path, o);
      // The new size we want to sxcale to
      final int REQUIRED_SIZE = 64;

      // Find the correct scale value. It should be the power of 2.
      int scale = 1;
      while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
        scale *= 2;
      // Decode with inSampleSize
      BitmapFactory.Options o2 = new BitmapFactory.Options();
      o2.inSampleSize = scale;
      return BitmapFactory.decodeFile(path, o2);
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }
}

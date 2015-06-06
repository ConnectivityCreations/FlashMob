package com.stridera.connectivitycreations.flashmob.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.stridera.connectivitycreations.flashmob.R;

import java.io.IOException;


public class EventCreateActivity extends AppCompatActivity {

  private static final int PICK_PHOTO_CODE = 1;
  private static final String TAG = EventCreateActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.event_create_activity);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_event_create, menu);
    return true;
  }

  public void onAttachPhoto(View btn) {
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(intent, PICK_PHOTO_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != RESULT_OK) {
      Log.d(TAG, "Ignoring activity result with code: " + resultCode);
      return;
    }

    if (requestCode == PICK_PHOTO_CODE) {
      if (data != null) {
        Uri photoUri = data.getData();
        try {
          Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
          // Load the selected image into a preview
          ImageView ivPreview = (ImageView) findViewById(R.id.photoImageView);
          ivPreview.setImageBitmap(selectedImage);
        } catch (IOException ex) {
          Log.e(TAG, "Error loading image", ex);
        }
      }
    } else {
      Log.w(TAG, "Unhandled result code: " + resultCode);
    }
  }
}

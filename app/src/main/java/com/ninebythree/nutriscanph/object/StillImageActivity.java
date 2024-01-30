/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ninebythree.nutriscanph.object;

import static java.lang.Math.max;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.ninebythree.nutriscanph.BitmapUtils;
import com.ninebythree.nutriscanph.GraphicOverlay;
import com.ninebythree.nutriscanph.MainActivity;
import com.ninebythree.nutriscanph.R;
import com.ninebythree.nutriscanph.VisionImageProcessor;
import com.ninebythree.nutriscanph.ml.ModelUnquant;
import com.ninebythree.nutriscanph.object.objectdetector.ObjectDetectorProcessor;
import com.ninebythree.nutriscanph.preference.PreferenceUtils;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/** Activity demonstrating different image detector features with a still image from camera. */
@KeepName
public final class StillImageActivity extends AppCompatActivity {

  private static final String TAG = "StillImageActivity";

  private static final String OBJECT_DETECTION = "Object Detection";
  private static final String OBJECT_DETECTION_CUSTOM = "Custom Object Detection";

  int imageSize = 224;

  private static final String SIZE_SCREEN = "w:screen"; // Match screen width
  private static final String SIZE_1024_768 = "w:1024"; // ~1024*768 in a normal ratio
  private static final String SIZE_640_480 = "w:640"; // ~640*480 in a normal ratio
  private static final String SIZE_ORIGINAL = "w:original"; // Original image size

  private static final String KEY_IMAGE_URI = "com.google.mlkit.vision.demo.KEY_IMAGE_URI";
  private static final String KEY_SELECTED_SIZE = "com.google.mlkit.vision.demo.KEY_SELECTED_SIZE";

  private static final int REQUEST_IMAGE_CAPTURE = 1001;
  private static final int REQUEST_CHOOSE_IMAGE = 1002;

  private ImageView preview;
  private GraphicOverlay graphicOverlay;
  private String selectedMode = OBJECT_DETECTION;
  private String selectedSize = SIZE_SCREEN;

  boolean isLandScape;

  private Uri imageUri;
  private int imageMaxWidth;
  private int imageMaxHeight;

  private Button btnSubmit;
  private TextView txtFood;
  private VisionImageProcessor imageProcessor;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_still_image);

    btnSubmit = findViewById(R.id.btnSubmit);
    txtFood = findViewById(R.id.txtFood);

    findViewById(R.id.select_image_button)
            .setOnClickListener(
                    view -> {
                      ImagePicker.with(StillImageActivity.this)
                              .crop() // Crop image(Optional), Check Customization for more option
                              .compress(1024) // Final image size will be less than 1 MB(Optional)
                              .maxResultSize(1080, 1080) // Final image resolution will be less than 1080 x 1080(Optional)
                              .start();

                      // startChooseImageIntentForResult();

                    });
    preview = findViewById(R.id.preview);
    graphicOverlay = findViewById(R.id.graphic_overlay);

    populateFeatureSelector();
    populateSizeSelector();

    isLandScape =
            (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);

    if (savedInstanceState != null) {
      imageUri = savedInstanceState.getParcelable(KEY_IMAGE_URI);
      selectedSize = savedInstanceState.getString(KEY_SELECTED_SIZE);
    }

    View rootView = findViewById(R.id.root);
    rootView
            .getViewTreeObserver()
            .addOnGlobalLayoutListener(
                    new OnGlobalLayoutListener() {
                      @Override
                      public void onGlobalLayout() {
                        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        imageMaxWidth = rootView.getWidth();
                        imageMaxHeight = rootView.getHeight() - findViewById(R.id.control).getHeight();
                        if (SIZE_SCREEN.equals(selectedSize)) {
                          tryReloadAndDetectInImage();
                        }
                      }
                    });

    ImageView settingsButton = findViewById(R.id.settings_button);
    settingsButton.setVisibility(View.GONE);
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createImageProcessor();
    tryReloadAndDetectInImage();
  }

  @Override
  public void onPause() {
    super.onPause();
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
  }

  private void populateFeatureSelector() {
    Spinner featureSpinner = findViewById(R.id.feature_selector);
    List<String> options = new ArrayList<>();
    options.add(OBJECT_DETECTION);
    options.add(OBJECT_DETECTION_CUSTOM);


    // Creating adapter for featureSpinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // attaching data adapter to spinner
    featureSpinner.setAdapter(dataAdapter);
    featureSpinner.setOnItemSelectedListener(
            new OnItemSelectedListener() {

              @Override
              public void onItemSelected(
                      AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                selectedMode = parentView.getItemAtPosition(pos).toString();
                createImageProcessor();
                tryReloadAndDetectInImage();
              }

              @Override
              public void onNothingSelected(AdapterView<?> arg0) {}
            });
  }

  private void populateSizeSelector() {
    Spinner sizeSpinner = findViewById(R.id.size_selector);
    List<String> options = new ArrayList<>();
    options.add(SIZE_SCREEN);
    options.add(SIZE_1024_768);
    options.add(SIZE_640_480);
    options.add(SIZE_ORIGINAL);

    // Creating adapter for featureSpinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // attaching data adapter to spinner
    sizeSpinner.setAdapter(dataAdapter);
    sizeSpinner.setOnItemSelectedListener(
            new OnItemSelectedListener() {

              @Override
              public void onItemSelected(
                      AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                selectedSize = parentView.getItemAtPosition(pos).toString();
                tryReloadAndDetectInImage();
              }

              @Override
              public void onNothingSelected(AdapterView<?> arg0) {}
            });
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(KEY_IMAGE_URI, imageUri);
    outState.putString(KEY_SELECTED_SIZE, selectedSize);
  }

  private void startCameraIntentForResult() {
    // Clean up last time's image
    imageUri = null;
    preview.setImageBitmap(null);

    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.TITLE, "New Picture");
      values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
      imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
      takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }
  }

  private void startChooseImageIntentForResult() {
    Intent intent = new Intent();
    intent.setType("image/*");
    intent.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CHOOSE_IMAGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      tryReloadAndDetectInImage();
    } else if (/*requestCode == REQUEST_CHOOSE_IMAGE && */resultCode == RESULT_OK) {
      // In this case, imageUri is returned by the chooser, save it.
      imageUri = data.getData();
      tryReloadAndDetectInImage();

      try {
        Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

        // Check if image is not null
        if (image != null) {
          int dimension = Math.min(image.getWidth(), image.getHeight());
          image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

          // Assuming 'imageSize' is defined and valid
          image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);

          classifyImage(image);


        }
      } catch (IOException e) {
        e.printStackTrace();
        Toast.makeText(getApplicationContext(), "Failed to load image " + e.getMessage(), Toast.LENGTH_SHORT).show();
        // Handle the exception
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  private void tryReloadAndDetectInImage() {
    Log.d(TAG, "Try reload and detect image");
    try {
      if (imageUri == null) {
        return;
      }

      if (SIZE_SCREEN.equals(selectedSize) && imageMaxWidth == 0) {
        // UI layout has not finished yet, will reload once it's ready.
        return;
      }

      Bitmap imageBitmap = BitmapUtils.getBitmapFromContentUri(getContentResolver(), imageUri);
      if (imageBitmap == null) {
        return;
      }

      // Clear the overlay first
      graphicOverlay.clear();

      Bitmap resizedBitmap;
      if (selectedSize.equals(SIZE_ORIGINAL)) {
        resizedBitmap = imageBitmap;
      } else {
        // Get the dimensions of the image view
        Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

        // Determine how much to scale down the image
        float scaleFactor =
                max(
                        (float) imageBitmap.getWidth() / (float) targetedSize.first,
                        (float) imageBitmap.getHeight() / (float) targetedSize.second);

        resizedBitmap =
                Bitmap.createScaledBitmap(
                        imageBitmap,
                        (int) (imageBitmap.getWidth() / scaleFactor),
                        (int) (imageBitmap.getHeight() / scaleFactor),
                        true);
      }

      preview.setImageBitmap(resizedBitmap);

      if (imageProcessor != null) {
        graphicOverlay.setImageSourceInfo(
                resizedBitmap.getWidth(), resizedBitmap.getHeight(), /* isFlipped= */ false);
        imageProcessor.processBitmap(resizedBitmap, graphicOverlay);
      } else {
        Log.e(TAG, "Null imageProcessor, please check adb logs for imageProcessor creation error");
      }
    } catch (IOException e) {
      Log.e(TAG, "Error retrieving saved image");
      imageUri = null;
    }
  }

  private Pair<Integer, Integer> getTargetedWidthHeight() {
    int targetWidth;
    int targetHeight;

    switch (selectedSize) {
      case SIZE_SCREEN:
        targetWidth = imageMaxWidth;
        targetHeight = imageMaxHeight;
        break;
      case SIZE_640_480:
        targetWidth = isLandScape ? 640 : 480;
        targetHeight = isLandScape ? 480 : 640;
        break;
      case SIZE_1024_768:
        targetWidth = isLandScape ? 1024 : 768;
        targetHeight = isLandScape ? 768 : 1024;
        break;
      default:
        throw new IllegalStateException("Unknown size");
    }

    return new Pair<>(targetWidth, targetHeight);
  }

  private void createImageProcessor() {
    if (imageProcessor != null) {
      imageProcessor.stop();
    }
    try {

      Log.i(TAG, "Using Custom Object Detector Processor");
      LocalModel localModel =
              new LocalModel.Builder()
                      .setAssetFilePath("custom_models/object_labeler.tflite")
                      .build();
      CustomObjectDetectorOptions customObjectDetectorOptions =
              PreferenceUtils.getCustomObjectDetectorOptionsForStillImage(this, localModel);
      imageProcessor = new ObjectDetectorProcessor(this, customObjectDetectorOptions);

    } catch (Exception e) {
      Log.e(TAG, "Can not create image processor: " + selectedMode, e);
      Toast.makeText(
                      getApplicationContext(),
                      "Can not create image processor: " + e.getMessage(),
                      Toast.LENGTH_LONG)
              .show();
    }
  }



  public void classifyImage(Bitmap image) {
    try {
      ModelUnquant model = ModelUnquant.newInstance(getApplicationContext());

      // Creates inputs for reference.
      TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
      ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4*imageSize*imageSize*3);
      byteBuffer.order(ByteOrder.nativeOrder());

      int [] intValues = new int[imageSize*imageSize];
      image.getPixels(intValues,0,image.getWidth(),0,0,image.getWidth(),image.getHeight());
      int pixel = 0;
      for(int i = 0; i < imageSize; i++){
        for(int j = 0; j < imageSize; j++){
          int val = intValues[pixel++]; // RGB
          byteBuffer.putFloat(((val >> 16) & 0xFF)*(1.f/255.f));
          byteBuffer.putFloat(((val >> 8) & 0xFF)*(1.f/255.f));
          byteBuffer.putFloat((val & 0xFF)*(1.f/255.f));
        }
      }

      inputFeature0.loadBuffer(byteBuffer);

      // Runs model inference and gets result.
      ModelUnquant.Outputs outputs = model.process(inputFeature0);
      TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

      float[] confidences = outputFeature0.getFloatArray();
      int maxPos = 0;
      float maxConfidence = 0;
      for(int i = 0; i < confidences.length; i++){
        if(confidences[i] > maxConfidence){
          maxConfidence = confidences[i];
          maxPos = i;
        }
      }

      String[] classes = {"Adobong Manok", "Ginisang sayote", "Menudo", "Sinigang", "Mushroom Soup", "Pandesal", "SunnySide Up Egg", "White Cooked Rice"};

      Log.d("RESULT", classes[maxPos]);
      String food = classes[maxPos];
      txtFood.setText(food);

      String s = "";
      for(int i = 0; i < classes.length; i++){
        s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
      }

      Log.d("RESULT", "Confidence" + s);



      btnSubmit.setOnClickListener(v -> {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("food", food);
        startActivity(intent);
        finish();

      });

      // Releases model resources if no longer used.
      model.close();



    } catch (IOException e) {
      // TODO Handle the exception
    }
  }

}

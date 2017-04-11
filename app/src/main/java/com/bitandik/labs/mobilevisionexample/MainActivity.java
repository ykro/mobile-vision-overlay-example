package com.bitandik.labs.mobilevisionexample;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

/**
 * Created by ykro.
 */

public class MainActivity extends AppCompatActivity {
  private static final int RC_HANDLE_GMS = 9001;
  private static final int RC_HANDLE_CAMERA_PERM = 2;

  private SourcePreview preview;
  private ImageOverlay imageOverlay;
  private CameraSource cameraSource;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    preview = (SourcePreview) findViewById(R.id.preview);
    imageOverlay = (ImageOverlay) findViewById(R.id.faceOverlay);

    int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    if (rc == PackageManager.PERMISSION_GRANTED) {
      createCameraSource();
    } else {
      requestCameraPermission();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    setupCameraParams();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (cameraSource != null) {
      cameraSource.stop();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }

  private void requestCameraPermission() {
    final String[] permissions = new String[]{Manifest.permission.CAMERA};

    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.CAMERA)) {
      ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
      return;
    }

    final Activity thisActivity = this;

    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ActivityCompat.requestPermissions(thisActivity, permissions,
            RC_HANDLE_CAMERA_PERM);
      }
    };

    Snackbar.make(imageOverlay, R.string.app_name,
        Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.app_name, listener)
        .show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    if (requestCode != RC_HANDLE_CAMERA_PERM) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }

    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      createCameraSource();
      return;
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        finish();
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Face Tracker sample")
        .setMessage(R.string.app_name)
        .setPositiveButton(R.string.app_name, listener)
        .show();
  }

  @NonNull
  private FaceDetector createFaceDetector(Context context) {
    FaceDetector detector = new FaceDetector.Builder(context)
        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
        .setTrackingEnabled(true)
        .setMode(FaceDetector.FAST_MODE)
        .setProminentFaceOnly(true)
        .setMinFaceSize(0.35f)
        .build();

    Tracker<Face> tracker = new CustomFaceTracker(imageOverlay);
    Detector.Processor<Face> processor = new LargestFaceFocusingProcessor.
        Builder(detector, tracker).
        build();
    detector.setProcessor(processor);

    if (!detector.isOperational()) {
      // Check for low storage.  If there is low storage, the native library will not be
      // downloaded, so detection will not become operational.
      IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
      boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

      if (hasLowStorage) {
          Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show();
      }
    }
    return detector;
  }

  private void createCameraSource() {
    Context context = getApplicationContext();
    FaceDetector detector = createFaceDetector(context);

    cameraSource = new CameraSource.Builder(context, detector)
        .setFacing(CameraSource.CAMERA_FACING_FRONT)
        .setRequestedPreviewSize(320, 240)
        .setRequestedFps(60.0f)
        .setAutoFocusEnabled(true)
        .build();

    preview.setCameraSource(cameraSource);
    preview.setOverlay(imageOverlay);

  }

  private void setupCameraParams(){
    int code = GoogleApiAvailability.
        getInstance().
        isGooglePlayServicesAvailable(getApplicationContext());
    if (code != ConnectionResult.SUCCESS) {
      Dialog dialog = GoogleApiAvailability.
          getInstance().
          getErrorDialog(this, code, RC_HANDLE_GMS);
      dialog.show();
    }

    if (cameraSource == null) {
      createCameraSource();
    }
  }
}

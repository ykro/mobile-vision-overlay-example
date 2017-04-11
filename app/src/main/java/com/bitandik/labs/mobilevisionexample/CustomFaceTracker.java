package com.bitandik.labs.mobilevisionexample;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by ykro.
 */

class CustomFaceTracker extends Tracker<Face> {
  private ImageOverlay imageOverlay;

  public CustomFaceTracker(ImageOverlay imageOverlay) {
    this.imageOverlay = imageOverlay;
  }

  @Override
  public void onNewItem(int id, Face face) {
  }

  @Override
  public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
    imageOverlay.update(face.getPosition(), face.getWidth(), face.getHeight());
  }

  @Override
  public void onMissing(FaceDetector.Detections<Face> detectionResults) {
    imageOverlay.clear();
  }

  @Override
  public void onDone() {
    imageOverlay.clear();
  }
}

package com.bitandik.labs.mobilevisionexample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

/**
 * Created by ykro.
 */

public class SourcePreview extends SurfaceView implements SurfaceHolder.Callback {
  private ImageOverlay overlay;
  private CameraSource cameraSource;

  public SourcePreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    getHolder().addCallback(this);
  }

  public void setCameraSource(CameraSource cameraSource) {
    this.cameraSource = cameraSource;
  }

  public void setOverlay(ImageOverlay overlay) {
    this.overlay = overlay;
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {
  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    try {
      start(surfaceHolder);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    if (cameraSource != null) {
      cameraSource.stop();
    }
  }

  public void start(SurfaceHolder surfaceHolder) throws IOException, SecurityException{

    if (cameraSource != null) {
      cameraSource.start(surfaceHolder);

      if (overlay != null) {

        Size size = cameraSource.getPreviewSize();
        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        overlay.setCameraInfo(min, max);
        overlay.clear();
      }

    }
  }

  public void start() throws IOException {
    start(getHolder());
  }
}

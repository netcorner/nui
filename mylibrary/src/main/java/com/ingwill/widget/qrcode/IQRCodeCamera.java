package com.ingwill.widget.qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.ingwill.widget.qrcode.camera.CameraManager;
import com.ingwill.widget.qrcode.view.ViewfinderView;

public interface IQRCodeCamera {
    CameraManager getCameraManager();
    Handler getCaptureActivityHandler();
    ViewfinderView getViewfinderView();
    void handleDecode(Result obj, Bitmap barcode);
    void drawViewfinder();
    void startActivity(Intent intent);
    void finishActivity(int result,Intent intent);
}

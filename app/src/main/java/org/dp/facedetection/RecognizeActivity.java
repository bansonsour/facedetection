package org.dp.facedetection;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.dp.facedetection.util.Constant;
import org.dp.facedetection.util.DLog;
import org.dp.facedetection.util.DrawHelper;
import org.dp.facedetection.util.camera.CameraHelper;
import org.dp.facedetection.util.camera.CameraListener;
import org.dp.facedetection.widget.FaceRectView;

/**
 * Created by caydencui on 2019/5/5.
 */
public class RecognizeActivity extends AppCompatActivity {
    private static final String TAG = "RecognizeActivity";
    private CameraHelper cameraHelper;
    private DrawHelper drawHelper;
    private Camera.Size previewSize;
    private Integer cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private View previewView;

    private FaceRectView faceRectView;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recognize);
        init();
        initCamera();


    }
    private void init(){
        if(!checkPermissions(Constant.NEEDED_PERMISSIONS)){
            ActivityCompat.requestPermissions(this,Constant.NEEDED_PERMISSIONS,ACTION_REQUEST_PERMISSIONS);
            return;
        }
        previewView = findViewById(R.id.texture_preview);
        //在布局结束后才做初始化操作
        faceRectView = findViewById(R.id.face_rect_view);
    }


    private void initCamera() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        CameraListener cameraListener=new CameraListener() {
            @Override
            public void onCameraOpened(Camera camera, int cameraId, int displayOrientation, boolean isMirror) {
                previewSize=camera.getParameters().getPreviewSize();
                drawHelper=new DrawHelper(previewSize.width,previewSize.height,previewView.getWidth(),previewView.getHeight(),displayOrientation
                        ,cameraId,isMirror);
            }

            @Override
            public void onPreview(byte[] data, Camera camera) {

            }

            @Override
            public void onCameraClosed() {
                DLog.d(TAG,"onCameraClosed");
            }

            @Override
            public void onCameraError(Exception e) {
                DLog.e(TAG,e.getMessage());
            }

            @Override
            public void onCameraConfigurationChanged(int cameraID, int displayOrientation) {
                if(drawHelper!=null){
                    drawHelper.setCameraDisplayOrientation(displayOrientation);
                }
            }
        };

        cameraHelper=new CameraHelper.Builder()
                .previewViewSize(new Point(previewView.getMeasuredWidth(),previewView.getMeasuredHeight()))
                .rotation(getWindowManager().getDefaultDisplay().getRotation())
                .specificCameraId(cameraID != null ? cameraID : Camera.CameraInfo.CAMERA_FACING_FRONT)
                .isMirror(false)
                .previewOn(previewView)
                .cameraListener(cameraListener)
                .build();
        cameraHelper.init();

    }
    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    protected void onDestroy() {

        if (cameraHelper != null) {
            cameraHelper.release();
            cameraHelper = null;
        }

        super.onDestroy();
    }
}

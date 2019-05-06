package org.dp.facedetection;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.dp.facedetection.util.Constant;
import org.dp.facedetection.util.DLog;
import org.dp.facedetection.util.DrawHelper;
import org.dp.facedetection.util.NV21ToBitmap;
import org.dp.facedetection.util.camera.CameraHelper;
import org.dp.facedetection.util.camera.CameraListener;
import org.dp.facedetection.widget.FaceRectView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;

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

    private Mat mat = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recognize);
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        }
        init();
        initCamera();
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    mat=new MatOfRect();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
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
            public void onPreview(byte[] nv21, Camera camera) {
                if (faceRectView != null) {
                    faceRectView.clearFaceInfo();
                }

                /*将nv21转bitmap*/
                NV21ToBitmap nv21ToBitmap=new NV21ToBitmap();
                Bitmap bmp=nv21ToBitmap.nv21ToBitmap(nv21,previewSize.width,previewSize.height);
                String str = "image size = "+bmp.getWidth()+"x"+bmp.getHeight()+"\n";

                Log.i("OpenCV", str);
                Bitmap bmp2=bmp.copy(bmp.getConfig(),true);
                Utils.bitmapToMat(bmp,mat);
                Log.i("OpenCV1", ""+(mat==null?true:false));
                Log.i("OpenCV1", str);
                Scalar FACE_RECT_COLOR =new Scalar(255.0, 0.0, 0.0);
                int FACE_RECT_THICKNESS = 3;
                long startTime = System.currentTimeMillis();
                Log.i("OpenCV1", ""+startTime);
                FaceDetectUtils faceDetect=new FaceDetectUtils();
                Log.i("OpenCV21", ""+mat.getNativeObjAddr());
                Face [] faces= faceDetect.facedetect(mat.getNativeObjAddr());
                str = str + "face num = "+faces.length+"\n";
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
